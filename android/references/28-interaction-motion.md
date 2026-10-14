# 28 인터랙션·모션

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Material 3). 출처 번호(S..)는 90-sources.md·92-sources-2.md.

앱 전체에 깔리는 화면 이동·오버레이·피드백·모션 규칙이다. 화면 하나의 동작(어느 화면이 무엇을 띄우나)은 다루지 않는다. 근거 줄의 **공식**은 Android·Material 문서가 말하는 것이고, **사용자 결정**은 공식 문서가 정하지 않아 사용자가 정한 것이다(2026-10-14, 91-decisions의 MOTION_* 행). SEED·토스는 공식 기준이 아니라 참고 사례다.
값은 `:core:designsystem`의 `motion/` 패키지(`templates/designsystem/motion/`)가 소유하고 화면은 그 토큰만 쓴다(R-28-13). Material의 시간·곡선·스프링 표는 Views 테마 속성이라 Compose에는 값만 옮겼고, Compose용 `MotionScheme`은 Material3 1.4.0에서 `internal`이라 쓰지 않는다. 예시에 쓴 Compose·Nav3 API는 스크래치 프로젝트에서 컴파일해 존재와 시그니처를 확인했다(예시 코드 그대로는 아니다). 슬라이드 거리·spring 3단·`pressScale`은 실기기에서 시각으로 확인하지 않았다.

### R-28-01 푸시·팝은 shared axis X, 최상위 목적지 간은 fade through로 전환한다
- 규칙: `:app`의 `NavDisplay` 한 곳에서 `transitionSpec`·`popTransitionSpec`·`predictivePopTransitionSpec`을 `NavTransitions.push`·`pop`·`predictivePop`으로 정한다. 앞으로는 오른쪽에서 들어오며 페이드하고 나가는 화면은 왼쪽으로 밀리며 페이드한다(뒤로는 반대). 하단 탭 같은 최상위 목적지 사이 이동은 그 엔트리 metadata의 `TransitionKey`·`PopTransitionKey`를 `NavTransitions.fadeThrough`로 덮어쓴다(어느 Scene의 키가 쓰이는지 문서에 없어 둘 다). 화면은 자기 전환을 따로 만들지 않는다.
- 근거: 공식 — Material은 관계로 패턴을 고른다: shared axis는 "spatial or navigational relationship"(온보딩 흐름은 x축), fade through는 "do not have a strong relationship"(하단 내비게이션 목적지)이고 문서가 두 패턴의 기본 시간을 300ms로 적는다 [S287](https://github.com/material-components/material-components-android/blob/master/docs/theming/Motion.md). Nav3가 전환 3종과 엔트리 metadata 덮어쓰기를 제공하고 "the appropriate scene"의 전환을 쓴다고만 적는다 [S288](https://developer.android.com/guide/navigation/navigation-3/animate-destinations). 사용자 결정 — 푸시·팝을 shared axis X로, 방향을 오른쪽 진입으로, 슬라이드 거리를 화면 폭의 1/10로 정한 것. Nav3 기본 전환의 모양은 문서에 없어 확인하지 못했다.
- 예시:
  ```kotlin
  // Good — :app 에서 한 번, 최상위 목적지만 metadata 로 덮어쓴다
  NavDisplay(transitionSpec = { NavTransitions.push }, popTransitionSpec = { NavTransitions.pop }, ...)
  entry<HomeKey>(metadata = metadata {
      put(NavDisplay.TransitionKey) { NavTransitions.fadeThrough }
      put(NavDisplay.PopTransitionKey) { NavTransitions.fadeThrough }
  }) { }
  // Bad — 화면이 자기 전환을 숫자로 만든다
  transitionSpec = { slideInHorizontally(tween(250)) { it } togetherWith fadeOut() }
  ```
- 체크: `NavDisplay` 전환 인자가 `:app` 한 곳에만 있는가. 화면·feature에 `slideIn*`·`fadeIn`이 있는가. 최상위 목적지 엔트리가 fade through인가.

### R-28-02 모달 전체 화면은 아래에서 위로, 스택 교체는 fade through다
- 규칙: 흐름을 벗어나는 독립 작업 전체 화면은 엔트리 metadata의 `NavDisplay.TransitionKey`를 `{ modalPush() }`(`NavTransitions.kt`의 `AnimatedContentTransitionScope` 확장 함수)로, `PopTransitionKey`·`PredictivePopTransitionKey`를 `NavTransitions.modalPop`으로 덮어써 아래에서 올라오고 닫을 때 아래로 내려가게 한다. 모달의 예측형 뒤로가기는 R-28-03의 공식 수치 대신 `modalPop`을 쓴다. 백스택을 비우고 시작 화면으로 교체하는 이동은 `NavTransitions.fadeThrough`를 앞으로·뒤로 키에 모두 덮어쓴다. 어느 화면이 모달 성격인지는 그 화면의 설계가 정한다.
- 근거: Nav3의 엔트리 metadata 전환 키 3종과 아래→위 슬라이드 예시 [S288](https://developer.android.com/guide/navigation/navigation-3/animate-destinations), [S289](https://developer.android.com/guide/navigation/navigation-3/recipes/animations). `ExitTransition.KeepUntilTransitionsFinished`는 companion 멤버가 internal이고 `AnimatedContentTransitionScope` 안의 확장 속성만 공개라서 `modalPush`를 그 스코프의 확장 함수로 만들었다(스크래치 컴파일로 확인, 아래 화면이 실제로 유지되는지는 화면에서 확인하지 않았다). 아래→위·스택 교체 페이드·모달에 `modalPop`을 쓰는 것은 사용자 결정이거나 템플릿의 선택이다. 스택 교체를 Nav3가 앞으로 이동으로 보는지 뒤로 이동으로 보는지는 문서에 없다.
- 예시:
  ```kotlin
  // Good — 모달 엔트리만 덮어쓴다
  entry<ComposeKey>(metadata = metadata {
      put(NavDisplay.TransitionKey) { modalPush() }
      put(NavDisplay.PopTransitionKey) { NavTransitions.modalPop }
      put(NavDisplay.PredictivePopTransitionKey) { NavTransitions.modalPop }
  }) { }
  // Bad — 일반 화면에 아래→위를 쓴다 (흐름을 벗어난다는 신호가 사라진다)
  ```
- 체크: 모달 엔트리가 전환 키 3개를 모두 덮어쓰는가. 일반 푸시 화면에 `modalPush`가 있는가.

### R-28-03 예측형 뒤로가기는 전체 화면 전환에 공식 수치를 쓴다
- 규칙: 전체 화면 전환의 예측형 뒤로가기는 공식 수치를 따른다 — 나가는 화면 100→90% 축소, 들어오는 화면 110→100%, 진행 35% 지점에서 나가는 화면은 완전히 사라지고 들어오는 화면이 나타나기 시작(fade through), 인터폴레이터 (0.1, 0.1, 0, 1), 놓아서 취소하면 원래 크기로 복귀한다. 값은 `NavTransitions.predictivePop`이 갖는다.
- 근거: 공식 — 전체 화면 표면의 모션 수치 표와 35% 경계 [S290](https://developer.android.com/design/ui/mobile/guides/patterns/predictive-back). 지원 API를 쓰면 Android 15 이상에서 기본 활성이고 루트에서 뒤로를 가로채면 back-to-home 애니메이션이 꺼진다 [S291](https://developer.android.com/develop/ui/compose/system/predictive-back-setup). Nav3의 `predictivePopTransitionSpec`이 이 수치를 제스처 진행에 맞춰 그대로 표현하는지는 확인하지 못했다(미확인).
- 예시:
  ```kotlin
  // Good — 수치는 NavTransitions 가 갖고 NavDisplay 는 꽂기만 한다
  predictivePopTransitionSpec = { NavTransitions.predictivePop }
  // Bad — 화면이 수치를 다시 쓴다
  predictivePopTransitionSpec = { scaleIn() togetherWith scaleOut(targetScale = 0.8f) }
  ```
- 체크: `predictivePopTransitionSpec`이 `NavTransitions.predictivePop`인가. 실기기에서 뒤로 스와이프 미리보기가 위 수치와 같은가(미확인).

### R-28-04 표준 뒤로 버튼·제스처를 지원하고 상태를 보존한다
- 규칙: 모든 화면은 시스템 뒤로 버튼과 뒤로 제스처로 이전 화면에 돌아간다. 백그라운드 복귀·화면 회전·프로세스 사망 뒤에도 사용자 입력을 복원한다(R-17-12, R-18-14). 열려 있던 오버레이는 Nav 엔트리 오버레이(R-28-09)만 프로세스 사망 뒤에도 복원되고 `UiState` 필드 오버레이는 구성 변경까지만 이어지므로, 프로세스 사망을 넘겨야 하면 엔트리로 둔다.
- 근거: 공식 앱 품질 기준 — Back_Button_Nav("supports standard back button navigation"), Back_Gesture_Nav("supports gesture navigation for going back"), State_Preservation("preserves user or app state ... prevents accidental data loss due to back navigation") [S292](https://developer.android.com/docs/quality-guidelines/core-app-quality). 오버레이 복원 범위의 구분은 R-28-09의 사용자 결정에서 나온다.
- 예시:
  ```kotlin
  // Good — 입력 중 상태가 회전·프로세스 사망을 넘긴다
  var draft by rememberSaveable { mutableStateOf("") }
  // Bad
  var draft by remember { mutableStateOf("") }
  ```
- 체크: 입력이 회전과 프로세스 사망 뒤 복원되는가. 프로세스 사망을 넘겨야 하는 오버레이가 `UiState` 필드로만 있는가. 뒤로 이벤트를 막는 `BackHandler`가 R-28-05 밖에 있는가.

### R-28-05 진행 중이거나 되돌릴 수 없는 작업 중 뒤로가기는 중단 확인 다이얼로그를 띄운다
- 규칙: 저장·전송·처리처럼 진행 중이거나 되돌릴 수 없는 작업이 있는 동안에만 `BackHandler(enabled = 작업 중)`로 뒤로를 가로채 "중단하고 나갈까요?" 다이얼로그(R-28-08의 파괴적 확인)를 띄운다. 확인하면 작업을 정리하고 나가고, 작업이 없으면 가로채지 않는다.
- 근거: 공식 State_Preservation이 뒤로가기 등으로 인한 우발적 데이터 손실을 막으라고 한다 [S292](https://developer.android.com/docs/quality-guidelines/core-app-quality). 확인 다이얼로그로 막는 방식은 사용자 결정이다. 같은 문서의 Back_Button_Nav는 "custom, onscreen back button prompts"를 쓰지 말라고 하는데, 시스템 뒤로 이벤트를 받아 띄우는 이 다이얼로그가 거기에 해당하는지는 문구만으로 단정할 수 없다(해석 불확실).
- 예시:
  ```kotlin
  // Good — 작업 중일 때만 가로챈다
  BackHandler(enabled = uiState.isSaving) { onRequestCancel() }
  // Bad — 항상 가로채 시스템 뒤로·예측형 애니메이션을 막는다
  BackHandler { onRequestCancel() }
  ```
- 체크: `BackHandler`의 `enabled`가 작업 중 상태에만 묶이는가. feature 모듈에서 `BackHandler`를 쓰면 `androidx.activity:activity-compose` 의존이 필요하다 — 템플릿은 `:app`에만 두었고 feature 컨벤션 플러그인에 있는지는 확인하지 못했다.

### R-28-06 이동 호출은 `dropUnlessResumed`로 감싼다
- 규칙: 클릭으로 이동을 일으키는 콜백은 `dropUnlessResumed { }`로 감싸 생명주기가 RESUMED가 아닌 동안의 호출을 버린다. 전환 중 연타로 화면이 두 번 쌓이는 것을 막으려는 것이고(엔트리 생명주기에 기댄 기대 효과, 미확인), 같은 키 중복 push를 막는 `Navigator`(R-13-03)와 함께 쓴다.
- 근거: 공식 Nav3 레시피가 이동 클릭을 `dropUnlessResumed`로 감싼다 [S289](https://developer.android.com/guide/navigation/navigation-3/recipes/animations). 문서가 이유를 적지 않아 규칙으로 올리는 것은 사용자 결정이다.
- 예시:
  ```kotlin
  // Good
  Button(onClick = dropUnlessResumed { onOpenDetail(id) }) { Text(label) }
  // Bad — 이동 호출이 생명주기와 무관하게 실행된다
  Button(onClick = { onOpenDetail(id) }) { Text(label) }
  ```
- 체크: 이동 콜백을 부르는 `onClick`이 `dropUnlessResumed`로 감싸져 있는가.

### R-28-07 화면 사이 공유 요소 전환은 쓰지 않는다
- 규칙: 화면 사이에 공유 요소 전환(`SharedTransitionLayout`)을 넣지 않는다. 한 엔트리가 두 Scene 사이를 오갈 때의 끊김을 막으려고 `NavDisplay`에 `sharedTransitionScope`를 넘기는 것(R-13-09의 list-detail 같은 Scene 전략)은 예외다. 화면 사이에 쓰려면 이 규칙을 먼저 고친다.
- 근거: 공식 문서가 엇갈린다 — 디자인 문서는 표면이 떨어져 나가는 예측형 뒤로가기의 공유 요소 전환이 FragmentManager·Navigation Component·Navigation Compose에서 동작하지 않는다고 적고 [S290](https://developer.android.com/design/ui/mobile/guides/patterns/predictive-back), 설정 문서는 Navigation Compose의 공유 요소와 예측형 뒤로가기 통합을 안내한다 [S291](https://developer.android.com/develop/ui/compose/system/predictive-back-setup). Nav3의 `sharedTransitionScope`는 Scene 사이 이동의 끊김 방지용이다 [S288](https://developer.android.com/guide/navigation/navigation-3/animate-destinations). 공식이 막아서가 아니라 첫 단계에서 쓰지 않기로 한 사용자 결정이다.
- 예시:
  ```kotlin
  // Good — 화면 사이는 R-28-01 의 전환만 쓴다
  NavDisplay(transitionSpec = { NavTransitions.push }, ...)
  // Bad — 목록→상세에 공유 요소를 넣는다
  SharedTransitionLayout { /* 목록 항목과 상세 화면이 sharedElement 로 연결된다 */ }
  ```
- 체크: 화면 사이 이동에 `SharedTransitionLayout`·`sharedElement`가 있는가.

### R-28-08 오버레이는 용도로 고른다 — 파괴적 확인은 다이얼로그, 선택·입력은 바텀시트, 가벼운 결과는 스낵바, 지속 상태는 화면 안 배너
- 규칙: 파괴적이거나 되돌릴 수 없는 동작의 확인은 `AlertDialog`, 선택지 고르기·짧은 입력은 `ModalBottomSheet`, 가벼운 결과 알림·되돌리기는 스낵바, 해소될 때까지 남아야 하는 상태(연결 끊김 등)는 화면 안 배너로 둔다 — 그런 상태를 오버레이로 띄우지 않는다. 다이얼로그·시트·스낵바는 `:core:designsystem` 컴포넌트로 감싸 쓴다(R-17-14).
- 근거: 공식 — 다이얼로그는 동작 확인·입력 요청·선택지 목록 [S295](https://developer.android.com/develop/ui/compose/components/dialog), 스낵바는 동작 확인과 되돌리기·네트워크 상태·저장 완료 [S296](https://developer.android.com/develop/ui/compose/components/snackbar), 시트는 `ModalBottomSheet` [S297](https://developer.android.com/develop/ui/compose/components/bottom-sheets). 이 규칙은 공식에서 두 군데 벗어난다 — 선택지 목록을 시트에 배정한 것(공식 다이얼로그 문서는 이것도 다이얼로그 용도로 적는다)과, 공식 스낵바 문서가 "Network Status"를 스낵바 용도로 적는데 지속되는 상태는 배너로 둔 것(참고 — SEED는 지속 경고를 Page Banner·Callout으로 둔다 [S301](https://seed-design.io/docs/components/snackbar)). 둘 다 사용자 결정이다. 토스는 셀렉터를 시트에서 메뉴로 바꾼 A/B에서 전체 지표는 변화 없음, Android 아이템 클릭율은 10% 높았다고 밝혔다(상대·절대 불명) [S304](https://toss.tech/article/tds-component-making).
- 예시:
  ```kotlin
  // Good — 삭제 확인은 다이얼로그, 삭제 결과는 스낵바(액션이 있으면 duration 을 명시한다, R-28-11)
  AppConfirmDialog(title = "삭제할까요?", onConfirm = onDelete, onDismiss = onCancel)
  snackbarHostState.showSnackbar("삭제했습니다", actionLabel = "되돌리기", duration = SnackbarDuration.Short)
  // Bad — 해소될 때까지 남아야 하는 상태를 스낵바로 띄운다
  snackbarHostState.showSnackbar(message = "스캐너 연결 끊김", duration = SnackbarDuration.Indefinite)
  ```
- 체크: 파괴적 확인이 스낵바·시트로 가는가. 지속 상태가 오버레이로 뜨는가. 오버레이를 Material 컴포넌트로 직접 만드는 feature가 있는가.

### R-28-09 오버레이의 표현은 뒤로가기·복원 기준으로 Nav 엔트리 또는 `UiState` 필드로 고른다
- 규칙: 뒤로가기로 닫혀야 하고 프로세스 사망 뒤에도 복원돼야 하는 독립 화면급 오버레이는 `entry<T>` + `DialogSceneStrategy`·`BottomSheetSceneStrategy` metadata로 띄운다(R-13-04). 그렇지 않은 확인 다이얼로그·스낵바는 화면의 `UiState` 필드로 두고 UI가 처리한 뒤 소비 콜백으로 되돌린다(R-12-03).
- 근거: 엔트리 방식의 구현 근거는 R-13-04, 일회성 결과의 상태 환원은 R-12-03이 소유한다 [S275](https://developer.android.com/guide/navigation/navigation-3/recipes/dialog), [S276](https://developer.android.com/guide/navigation/navigation-3/recipes/bottomsheet). 어느 쪽을 고를지의 기준은 사용자 결정이다.
- 예시:
  ```kotlin
  // Good — 독립 화면급은 엔트리, 단순 확인은 UiState 필드
  entry<ConfirmKey>(metadata = DialogSceneStrategy.dialog()) { }
  uiState.confirmDelete?.let { AppConfirmDialog(onConfirm = { onDelete(); onConsumed() }) }
  // Bad — 단순 확인마다 NavKey 를 만든다 / Channel 로 다이얼로그를 띄운다
  ```
- 체크: 단순 확인 다이얼로그가 `NavKey`로 만들어져 있는가. 시트·다이얼로그 엔트리가 있는데 `sceneStrategies`에 전략이 없는가.

### R-28-10 오버레이는 바깥 탭·뒤로가기·드래그로 닫히되, 파괴적·진행 중에는 닫히지 않게 한다
- 규칙: 다이얼로그·바텀시트는 바깥 탭·뒤로가기로 닫히고 시트는 아래 드래그로도 닫힌다. 오버레이가 열려 있으면 뒤로가기는 오버레이를 먼저 닫고 그다음에 화면을 pop한다. 파괴적 확인·진행 중 작업·결제처럼 실수로 닫히면 안 되는 오버레이는 닫힘을 끄고 명시적 닫기 버튼을 두며, 그 오버레이가 열려 있는 동안 뒤로가기는 아무것도 하지 않는다(화면을 pop하지 않는다). 시트는 `rememberModalBottomSheetState(confirmValueChange = { it != SheetValue.Hidden || allowHide })`로 드래그 닫힘을 끄는데 `hide()`도 이 콜백을 거치므로, 닫기 버튼은 `allowHide = true`로 바꾼 뒤 `hide()`를 부른다.
- 근거: 공식 — `AlertDialog`의 `onDismissRequest`는 바깥 탭 등으로 호출된다 [S295](https://developer.android.com/develop/ui/compose/components/dialog). Material3 1.4.0 소스에서 `SheetState.hide()`는 `confirmValueChange(Hidden)`이 참일 때만 숨긴다. 참고 — SEED 시트는 아래로 드래그하거나 바깥을 누르면 닫히는 것이 기본이고 중요한 작업은 핸들 제거와 닫기 버튼을 권한다 [S302](https://seed-design.io/docs/components/bottom-sheet). 토스 TDS 시트는 배경 클릭 닫힘이 기본이고 뒤로가기로 닫는 것을 권장하며, 뒤로·배경 클릭 무시(`UNSAFE_*`)는 취소 구현이 어려울 때만 쓰라고 경고한다 [S305](https://tossmini-docs.toss.im/tds-mobile/hooks/OverlayExtension/use-bottom-sheet/). 공식 앱 품질 테스트 T-Back_Nav는 다이얼로그에서도 뒤로가 이전 화면으로 가는지 본다 [S292](https://developer.android.com/docs/quality-guidelines/core-app-quality) — 뒤로 차단이 그 기대와 부딪히는지는 문구만으로 단정할 수 없다(해석 불확실). 정책은 사용자 결정이다.
- 예시:
  ```kotlin
  // Good — 진행 중 시트는 닫힘을 끄고 닫기 버튼만 연다
  var allowHide by remember { mutableStateOf(false) }
  val sheetState = rememberModalBottomSheetState(confirmValueChange = { it != SheetValue.Hidden || allowHide })
  ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState,
      properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false, shouldDismissOnClickOutside = false)) {
      TextButton(onClick = { allowHide = true; scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() } }) { Text("닫기") }
  }
  // Bad — 결제 중 시트가 바깥 탭 한 번에 사라진다
  ModalBottomSheet(onDismissRequest = onDismiss) { PaymentForm() }
  ```
- 체크: 진행 중·파괴적 오버레이에 닫힘 차단과 닫기 버튼이 있고 그 버튼이 실제로 닫는가. 오버레이가 열려 있을 때 뒤로가기가 화면을 먼저 pop하는가.

### R-28-11 스낵바는 4초·하단 중앙·한 번에 1개·액션 1개다
- 규칙: 스낵바는 4초 표시(내용이 길면 더 길게), 하단 중앙, FAB 위(하단 바가 있으면 그 위)에 두고, 한 번에 1개만 보이며 새 것은 큐에 쌓여 순서대로 나온다(`SnackbarHostState`). `showSnackbar`는 액션 문구가 있으면 `duration` 기본값이 `Indefinite`가 되므로 4초가 되려면 `SnackbarDuration.Short`를 넘긴다. 텍스트 액션은 최대 1개이고 "되돌리기"처럼 구체적인 문구를 쓴다. 누르고 있으면 타이머가 멈추게 한다 — 타이머는 `SnackbarHost`에 있고 Material 3 기본에는 이 동작이 없어(소스 확인) `:core:designsystem`이 호스트를 직접 만든다. `showSnackbar`는 사용자 이벤트 경로에서는 `rememberCoroutineScope`, `UiState` 필드 경로(R-28-09)에서는 `LaunchedEffect`에서 부른다(R-17-08).
- 근거: 공식 — 스낵바는 몇 초 뒤 사라진다 [S296](https://developer.android.com/develop/ui/compose/components/snackbar). Material3 1.4.0 소스에서 `SnackbarDuration.Short`=4000ms, `showSnackbar`의 기본 duration은 액션이 없으면 `Short`·있으면 `Indefinite`, `SnackbarHostState`는 Mutex로 한 번에 하나만 보이는 공정한 큐다. 참고 — SEED 스낵바의 4초·하단 중앙·1개와 큐·액션 1개·터치 유지 시 타이머 일시정지·FAB 위 [S301](https://seed-design.io/docs/components/snackbar). 값 채택(하단 바 위 포함)은 사용자 결정이다.
- 예시:
  ```kotlin
  // Good — 액션이 있어도 4초
  scope.launch { snackbarHostState.showSnackbar("삭제했습니다", actionLabel = "되돌리기", duration = SnackbarDuration.Short) }
  // Bad — 액션이 있는데 duration 을 생략해 무기한이 된다 / 액션을 둘 두거나 "확인" 같은 모호한 문구
  scope.launch { snackbarHostState.showSnackbar("삭제했습니다", actionLabel = "되돌리기") }
  ```
- 체크: 스낵바 액션이 1개이고 문구가 구체적인가. 액션이 있는 `showSnackbar`가 `duration`을 넘기는가. 지속 경고에 `Indefinite` 스낵바를 쓰는가.

### R-28-12 오버레이는 퇴장을 등장보다 짧게 한다
- 규칙: 직접 만드는 오버레이(`AnimatedVisibility` 등)는 등장 300ms 감속(`Motion.enter()`), 퇴장 200ms 가속(`Motion.exit()`)을 쓴다. 다이얼로그는 페이드, 시트는 아래에서 슬라이드한다. `AlertDialog`·`ModalBottomSheet`의 라이브러리 기본 모션은 억지로 바꾸지 않는다.
- 근거: 공식 — Material은 들어오는 움직임에 decelerate, 나가는 움직임에 accelerate 곡선을 쓰라고 적는다 [S287](https://github.com/material-components/material-components-android/blob/master/docs/theming/Motion.md). 참고 — SEED 시트는 등장 d6(300ms)·퇴장 d4(200ms)다 [S302](https://seed-design.io/docs/components/bottom-sheet). 사용자 결정 — 이 시간을 채택한 것(Material의 fade 기본값은 등장 150ms·퇴장 75ms 선형이라 다르다).
- 예시:
  ```kotlin
  // Good — 토큰 시간·곡선, 퇴장이 더 짧다
  AnimatedVisibility(visible, enter = fadeIn(Motion.enter()), exit = fadeOut(Motion.exit())) { }
  // Bad — 등장과 퇴장이 같거나 숫자를 직접 쓴다
  AnimatedVisibility(visible, enter = fadeIn(tween(500)), exit = fadeOut(tween(500))) { }
  ```
- 체크: 직접 만든 오버레이의 퇴장 시간이 등장보다 짧은가.

### R-28-13 모션 값은 `Motion` 토큰으로만 쓰고 bezier 3종·시간 3종이 그 값이다
- 규칙: 곡선은 `standardEasing (0.2,0,0,1)`·`enterEasing (0,0,0,1)`·`exitEasing (0.3,0,1,1)` 3종, 시간은 `FAST_MS 150`·`EXIT_MS 200`·`STANDARD_MS 300` 3종이고 `:core:designsystem`의 `Motion`만 정의한다. 화면·feature는 `tween`·`spring`에 숫자 리터럴을 쓰지 않고 `Motion.standard()`·`enter()`·`exit()`·`fast()`를 부른다. 시간 범위는 0.2초 이하(미세)와 초과(큰 모션)로 나눠 생각한다. `ArchitectureTest`의 R-28-13이 `:core:designsystem` 모듈(`/core/designsystem/src/`) 밖의 숫자 리터럴을 막는다.
- 근거: 공식 — Material 곡선 3개와 시간표(Short3=150, Short4=200, Medium2=300ms) [S287](https://github.com/material-components/material-components-android/blob/master/docs/theming/Motion.md), Compose의 `CubicBezierEasing`·`tween` [S294](https://developer.android.com/develop/ui/compose/animation/customize). 참고 — SEED의 0.2초 구분 [S299](https://seed-design.io/docs/foundation/motion), 토스가 이징에 이름을 붙여 개발·디자인이 같은 말로 소통한 방식 [S303](https://toss.tech/article/interaction). 값 선택은 사용자 결정이다.
- 예시:
  ```kotlin
  // Good
  val alpha by animateFloatAsState(target, animationSpec = Motion.fast(), label = "alpha")
  // Bad — 화면이 시간을 직접 정한다 (Konsist 가 막는다)
  val alpha by animateFloatAsState(target, animationSpec = tween(250), label = "alpha")
  ```
- 체크: `:core:designsystem` 밖에 `tween(숫자)`·`spring(숫자)`가 있는가. Konsist R-28-13 테스트가 통과하는가 — 문자열 리터럴과 주석은 지우고 첫 인자·이름 인자(`durationMillis`·`delayMillis`·`dampingRatio`·`stiffness`)의 숫자 리터럴만 잡는 정적 텍스트 검사라 상수 산술·별칭·`keyframes`·중첩 블록 주석은 못 잡는다.

### R-28-14 spring은 손에 이어지는 움직임에, 시간이 정해진 전환은 bezier에 쓴다
- 규칙: 드래그·제스처 복귀·시트 스냅처럼 사용자 입력에 이어지는 움직임은 spring 3종(`Motion.quickSpring()`·`basicSpring()`·`slowSpring()` = damping 0.9에 stiffness 1400·700·300)을 쓰고, 화면 전환·오버레이 등장·퇴장·페이드·색 변화처럼 시간이 정해진 전환은 bezier(R-28-13)를 쓴다. 속도는 움직이는 크기로 고른다 — 작은 요소는 quick, 일부를 덮는 시트는 basic, 전체 화면은 slow. 투명도·색에는 spring 토큰을 쓰지 않는다(damping 0.9는 값을 넘칠 수 있다).
- 근거: 공식 — Material spatial spring 3단(fast 0.9·1400, default 0.9·700, slow 0.9·300)과 속도를 크기로 고르는 기준. Material은 색·투명도용으로 넘치지 않는 effects spring(damping 1, 3800·1600·800)을 따로 두는데 이 팩은 채택하지 않았다 [S287](https://github.com/material-components/material-components-android/blob/master/docs/theming/Motion.md). Compose `spring(dampingRatio, stiffness)` [S294](https://developer.android.com/develop/ui/compose/animation/customize). 참고 — 토스의 bezier·spring 두 가지 이징 [S303](https://toss.tech/article/interaction). 손에 이어지면 spring·시간이 정해지면 bezier라는 구분과 spatial만 토큰으로 두는 것은 사용자 결정이며, 실기기 느낌은 확인하지 않았다.
- 예시:
  ```kotlin
  // Good — 드래그가 끝난 뒤 제자리로 돌아오는 움직임은 spring
  animateTo(0f, animationSpec = Motion.basicSpring())
  // Bad — 투명도에 spatial spring 을 써서 1.0 을 넘어 번쩍인다
  animateFloatAsState(alpha, animationSpec = Motion.quickSpring())
  ```
- 체크: 드래그·제스처 복귀에 spring이, 전환·페이드에 bezier가 쓰이는가. 투명도·색 애니메이션에 spring 토큰이 있는가.

### R-28-15 한 요소에는 하나의 모션만 붙인다
- 규칙: 하나의 요소(타겟)에는 같은 속성을 다루는 전환 정의를 하나만 붙인다. 이동과 페이드를 같이 쓰는 것은 하나의 `ContentTransform`·`EnterTransition`으로 묶는다. 같은 속성에 시작·끝 시점이 다른 독립 애니메이션을 여러 개 겹치지 않는다. 서로 다른 속성(눌림 색과 눌림 축소처럼 같은 입력에 묶인 것)은 따로 둘 수 있다.
- 근거: 참고 — 토스는 "하나의 타겟엔 하나의 모션만 붙일 수 있다"는 규칙으로 모션 스펙을 만들고 여러 타겟에는 타임라인을 쓴다 [S303](https://toss.tech/article/interaction). 이 원칙의 채택과 속성 단위 해석은 사용자 결정이다.
- 예시:
  ```kotlin
  // Good — 이동과 페이드가 하나의 전환
  slideInHorizontally(Motion.enter()) { it / 10 } + fadeIn(Motion.enter())
  // Bad — 한 요소의 투명도에 서로 다른 시점의 애니메이션 둘이 겹친다
  Modifier.graphicsLayer { alpha = a1.value * a2.value }
  ```
- 체크: 한 요소의 같은 속성에 독립인 애니메이션이 둘 이상 걸려 있는가.

### R-28-16 크기·위치·투명도 애니메이션은 그리기 단계에서 하고, 자주 바뀌는 값의 숫자는 즉시 교체한다
- 규칙: 크기·위치·투명도 애니메이션은 레이아웃 단계 대신 `Modifier.graphicsLayer { }`·`drawBehind { }` 같은 람다 Modifier에서 상태를 읽어 그리기 단계에서 한다. 다른 요소가 함께 재배치돼야 하는 실제 크기·위치 변화는 예외로 `Modifier.layout { }`이나 `animateContentSize()`를 쓴다. 수 초마다 갱신되는 수치(속도·잔량 같은 실시간 값)의 텍스트는 애니메이션 없이 즉시 교체하고, 게이지·막대 같은 시각 요소만 값 사이를 그리기 단계에서 보간한다. 값이 갱신될 때마다 레이아웃 단계 애니메이션을 시작하지 않는다.
- 근거: 공식 — "If your animation changes the layout phase, it requires all affected composables to relayout and redraw. If your animation occurs in the draw phase, it is by default more performant"이고 람다 버전 Modifier나 `graphicsLayer`를 쓰라고 한다. 같은 문서는 위치·크기 변화가 형제 요소에 영향을 줘야 하면 `Modifier.layout { }`을, 크기 변화에는 `animateContentSize()`를 안내한다 [S293](https://developer.android.com/develop/ui/compose/animation/quick-guide). 숫자 즉시 교체는 사용자 결정이다.
- 예시:
  ```kotlin
  // Good — 값은 즉시, 게이지만 보간하고 State 를 그리기 람다에서 읽는다
  val progress = animateFloatAsState(targetValue = value, animationSpec = Motion.fast(), label = "gauge")
  Box(Modifier.fillMaxWidth().height(8.dp).drawBehind { drawRect(Color.Gray, size = size.copy(width = size.width * progress.value)) })
  // Bad — 갱신마다 레이아웃을 다시 잰다
  Modifier.width(animateDpAsState(widthDp).value)
  ```
- 체크: 크기·위치 애니메이션이 `Modifier.size`·`offset(dp)` 같은 레이아웃 단계 Modifier에 값을 넘기는데 형제 재배치가 필요한 경우가 아닌가. 실시간 수치 텍스트에 애니메이션이 있는가.

### R-28-17 콘텐츠 상태 전환은 `Crossfade`, 목록 항목 변화는 `animateItem`을 쓰되 fade에는 토큰을 명시한다
- 규칙: 로딩→콘텐츠→오류처럼 화면 상태가 바뀔 때는 `Crossfade`를 쓰고, 크기·위치가 달라지는 전환에만 `AnimatedContent`를 쓴다. 항목이 추가·삭제·재정렬되는 목록은 `LazyColumn` 항목에 `Modifier.animateItem()`을 쓰고 항목에 안정적인 `key`를 준다. 두 API의 기본 fade는 라이브러리 기본 spec이므로 `animationSpec = Motion.standard()`, `fadeInSpec = Motion.enter()`·`fadeOutSpec = Motion.exit()`로 토큰을 넘기고 `placementSpec`은 `Motion.basicSpring()`을 쓴다(R-28-14).
- 근거: 공식 — "Use `AnimatedContent` to animate between different composables, if you just want a standard fade between composables, use `Crossfade`" [S293](https://developer.android.com/develop/ui/compose/animation/quick-guide). `animateItem()`은 항목 변화 애니메이션이고 `key`를 줘야 제대로 동작한다고 적는다 [S306](https://developer.android.com/develop/ui/compose/lists). `animateItem`의 fade 기본값이 spring이라는 점은 리뷰가 foundation 1.9.4 소스에서 확인했다(이 프로젝트 버전과의 연결은 미확인). 이 둘을 앱 규칙으로 올린 것은 사용자 결정이다.
- 예시:
  ```kotlin
  // Good — fade 는 토큰, 이동은 spring
  Crossfade(targetState = uiState.phase, animationSpec = Motion.standard(), label = "phase") { PhaseContent(it) }
  items(rows, key = { it.id }) { RowItem(it, Modifier.animateItem(
      fadeInSpec = Motion.enter(), placementSpec = Motion.basicSpring(), fadeOutSpec = Motion.exit())) }
  // Bad — key 없이 animateItem 을 쓴다
  items(rows) { RowItem(it, Modifier.animateItem()) }
  ```
- 체크: 상태 전환에 `AnimatedContent`가 필요 없이 쓰였는가. `animateItem()`을 쓰는 목록 항목에 `key`가 있는가. fade spec이 토큰인가.

### R-28-18 누름 피드백은 색 변화와 거리 기준 축소를 150ms로 같이 준다
- 규칙: 누를 수 있는 요소는 눌린 동안 표면 색이 `MaterialTheme.colorScheme` 역할로 바뀌고, 구조가 허락하면 요소가 정해진 **거리**(2dp)만큼 줄어든다. 축소는 `Modifier.pressScale(interactionSource)`가 하고 기준 길이는 `max(높이, 폭÷4, 24dp)`, 배율은 `(기준−2)÷기준`이다. 색과 축소는 같은 150ms(`Motion.fast()`)를 써 함께 시작하고 끝난다. 터치 영역은 48dp 이상이다.
- 근거: 공식 — 터치 영역은 48dp 이상이다 [S292](https://developer.android.com/docs/quality-guidelines/core-app-quality). 참고 — SEED는 눌림을 색 + 축소로 전달하고 둘 다 d3(150ms)를 쓰며 [S300](https://seed-design.io/docs/foundation/feedback), 축소를 배율이 아니라 거리로 정의한다(세로 2px, 기준 길이 `max(높이, 폭÷4, 24)`, 배율 `(기준−2)÷기준`) [S309](https://seed-design.io/foundations/feedback/scale). 채택은 사용자 결정이다.
- 예시:
  ```kotlin
  // Good — 거리 기준 축소
  Box(Modifier.pressScale(interactionSource).clickable(interactionSource, null) { onClick() })
  // Bad — 요소마다 고정 배율을 쓴다 (큰 요소일수록 많이 움직인다)
  Modifier.graphicsLayer { scaleX = 0.98f; scaleY = 0.98f }
  ```
- 체크: 눌릴 수 있는 요소에 색 변화가 있는가. 눌림 축소가 고정 배율인가. 터치 영역이 48dp 미만인가. `pressScale`과 `clickable`의 순서에 따른 히트 영역·리플 차이는 확인하지 못했다.

### R-28-19 로딩은 목적별로 나누고 300ms 안에 끝나는 로딩은 표시하지 않는다
- 규칙: 화면 콘텐츠를 불러오는 중에는 스켈레톤, 짧은 동작(버튼 처리 중)에는 스피너(`CircularProgressIndicator`), 진행률을 아는 작업에는 진행 막대(`LinearProgressIndicator(progress = { })`)를 쓴다. 스켈레톤은 `:core:designsystem` 컴포넌트로 직접 만든다. `Motion.LOADING_DELAY_MS`(300ms) 안에 끝나는 로딩은 표시하지 않아 깜빡임을 막는다.
- 근거: 공식 — 진행 표시는 진행률을 아는 determinate와 모르는 indeterminate로 나뉜다 [S307](https://developer.android.com/develop/ui/compose/components/progress). 목적별 선택과 300ms 지연 임계값은 사용자 결정이다(공식·SEED·토스 모두 값을 공개하지 않았다).
- 예시:
  ```kotlin
  // Good — 진행률을 아는 작업은 진행 막대
  LinearProgressIndicator(progress = { done / total.toFloat() })
  // Bad — 진행률을 아는데 스피너만 돌린다
  CircularProgressIndicator()
  ```
- 체크: 진행률을 아는 작업이 스피너로 표시되는가. 로딩 표시에 지연이 있는가.

### R-28-20 햅틱은 지금 쓰지 않고, 켜는 방식은 전역 스위치 하나다
- 규칙: 앱은 지금 햅틱을 일으키지 않는다. 나중에 켜기 위한 자리로 `:core:designsystem`에 `LocalHapticsEnabled`(기본 `false`) 하나를 두고, 디자인 시스템 컴포넌트가 이 값이 `true`일 때만 `LocalHapticFeedback`을 부르게 한다. 켜는 곳은 앱 루트 한 곳이다. 컴포넌트마다 햅틱 매개변수를 두지 않는다.
- 근거: 공식 문서에는 앱 전체 햅틱 정책이 없고 햅틱 API 안내만 있다 [S308](https://developer.android.com/develop/ui/views/haptics/haptic-feedback). SEED도 햅틱은 정의하지 않는다고 적는다 [S300](https://seed-design.io/docs/foundation/feedback). 전역 스위치 방식은 사용자 결정이다.
- 예시:
  ```kotlin
  // Good — 스위치가 꺼져 있으면 아무것도 하지 않는다
  val enabled = LocalHapticsEnabled.current
  if (enabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
  // Bad — 컴포넌트가 무조건 진동시킨다
  haptics.performHapticFeedback(HapticFeedbackType.LongPress)
  ```
- 체크: `performHapticFeedback`이 `LocalHapticsEnabled` 확인 없이 호출되는가.

### R-28-21 시스템 애니메이션 배율을 앱이 덮어쓰지 않고, 상태 변화는 모션 없이도 알아볼 수 있게 한다
- 규칙: Compose의 `MotionDurationScale.scaleFactor`가 0이면 모션이 다음 프레임에 끝난다. 앱은 이 배율을 덮어쓰지 않고, 모션이 사라져도 상태가 보이게 설계한다 — 눌림은 색이 남고, 전환은 결과 화면이 곧바로 나타난다. 이 경로에서는 `pressScale`의 축소도 꺼지는 것이 아니라 최종 축소값이 즉시 적용된다. 앱 안의 "모션 줄이기" 스위치는 이 규칙이 정하지 않는다.
- 근거: 공식 — `MotionDurationScale`은 "0f would cause motion to finish in the next frame callback"이다 [S298](https://developer.android.com/reference/kotlin/androidx/compose/ui/MotionDurationScale). 시스템 "애니메이션 줄이기"가 이 배율에 연결되는지는 공식 레퍼런스에서 확인하지 못했다(미확인). 참고 — SEED는 줄이기에서 축소를 끄고 "어떤 요소가 빠지더라도 눌렸다는 사실은 남아야 한다"고 적는다 [S300](https://seed-design.io/docs/foundation/feedback). 사용자가 고른 "이동·축소는 끄고 페이드·색은 유지"는 기본 Compose 애니메이션 경로에서는 배율 0에서 페이드도 즉시 끝나 앱이 배율을 덮어써야 하는데 그 방법을 확인하지 못해 보류했다(91-decisions REDUCE_MOTION, 사용자 재확인 필요).
- 예시:
  ```kotlin
  // Good — 모션이 없어도 눌림이 색으로 보인다
  val container = if (pressed) pressedColor else restColor
  // Bad — 의미 전달을 모션에만 맡긴다 (배율 0 에서 사라진다)
  ```
- 체크: 상태 변화를 모션으로만 알리는 곳이 있는가. 앱이 `MotionDurationScale`을 덮어쓰는가.
