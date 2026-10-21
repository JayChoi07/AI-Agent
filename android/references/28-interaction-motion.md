# 28 인터랙션·모션

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Material 3). 출처 번호(S..)는 90-sources.md·92-sources-2.md.

앱 전체에 깔리는 화면 이동·오버레이·피드백·모션 규칙이다. 화면 하나의 동작(어느 화면이 무엇을 띄우나)은 다루지 않는다. 근거 줄의 **공식**은 Android·Material 문서가 말하는 것이고, **사용자 결정**은 공식 문서가 정하지 않아 사용자가 정한 것이다(2026-10-14, 91-decisions의 MOTION_* 행). SEED·토스는 공식 기준이 아니라 참고 사례다.
값은 `:core:designsystem`의 `motion/` 패키지(`templates/designsystem/motion/`)가 소유하고 화면은 그 토큰만 쓴다(R-28-13). Material의 시간·곡선·스프링 표는 Views 테마 속성이라 Compose에는 값만 옮겼고, Compose용 `MotionScheme`은 Material3 1.4.0에서 `internal`이라 쓰지 않는다. 예시에 쓴 Compose·Nav3 API는 스크래치 프로젝트에서 컴파일해 존재와 시그니처를 확인했다(예시 코드 그대로는 아니다). 푸시·팝·뒤로 키 전환은 S22+에서 시스템 설정 앱의 전환과 녹화로 비교해 확인했다(2026-10-15). 모달·fade through·spring 3단·`pressScale`은 실기기에서 시각으로 확인하지 않았다.

### R-28-01 푸시·팝은 Activity 전환처럼 덮고 밀어내고, 하단 바는 탭 루트 화면과 함께 움직인다
- 규칙: `:app`의 `NavDisplay` 한 곳에서 `transitionSpec`·`popTransitionSpec`·`predictivePopTransitionSpec`을 `NavTransitions.push`·`pop`·`predictivePop`으로 정한다. 앞으로는 새 화면이 오른쪽에서 폭 전체로 들어와 이전 화면을 **불투명하게 덮고**(페이드 없음) 이전 화면은 왼쪽으로 폭의 1/4 밀린다. 뒤로는 위 화면이 오른쪽으로 빠지고 아래 화면이 왼쪽에서 돌아온다. 들어오는 쪽과 나가는 쪽은 같은 곡선·시간(300ms, `Motion.enter()`)을 써서 두 화면 사이에 틈이 벌어지지 않게 하고, 푸시는 새 화면이 팝은 나가는 화면이 위에 그려지도록 `targetContentZIndex`를 정한다. 덮는 모양이 되려면 엔트리가 불투명해야 하므로 `:app`이 엔트리 데코레이터로 앱 배경색을 화면마다 깐다. **직접 그린 푸시 움직임의 두 가지 규칙.** 푸시를 `NavTransitions.push` 대신 앱이 데코레이터·상태로 직접 그리는 변형(새 화면의 첫 프레임을 그린 뒤 움직이기 등)을 쓸 때 ① `transitionSpec`은 두 화면을 붙들기만 하되 **값이 실제로 바뀌는 애니메이션**(예: 새 화면 1px 슬라이드 + 나가는 화면 `ExitTransition.KeepUntilTransitionsFinished`)이어야 한다 — 알파 1→1 같은 빈 전환은 Compose가 애니메이션을 돌리지 않아 이전 화면이 첫 프레임에 사라진다. ② 그 움직임은 **엔트리 metadata로 전환을 따로 지정한 화면(모달·fade through, R-28-02)에는 주지 않는다** — 판정은 화면 목록이 아니라 metadata(`NavDisplay.TransitionKey`)로 한다. 어기면 모달의 아래→위 슬라이드와 겹쳐 대각선으로 들어오고 아래 화면까지 밀린다.
  **하단 탭 바는 탭 루트 엔트리 안에 그려 화면의 일부로 만든다.** 상세로 푸시하면 바가 따로 접히지 않고 화면과 함께 왼쪽으로 밀리며 새 화면이 그 위를 덮고, 뒤로 가면 함께 돌아온다. 바를 숨기는 전용 애니메이션과 `Scaffold`의 `bottomBar`(`AnimatedVisibility`)는 쓰지 않는다. 아래 여백은 엔트리마다 따로 정한다 — 데코레이터가 탭 루트에는 "내비게이션 인셋 + 바 높이", 그 밖의 화면에는 "내비게이션 인셋"을 비우고, 엔트리에 내려보내는 `contentPadding`의 아래는 0이다(바가 레이아웃에 관여하지 않으므로 나가는 화면이 전환 중 흔들리지 않는다).
  바가 각 탭 화면에 붙으므로 최상위 목적지(탭) 사이 이동은 `NavDisplay` 전환이 아니라 **탭 컨테이너가 화면을 살려 두고 바꾸는 것**으로 한다: 탭마다 `NavDisplay`·백스택을 따로 두고, 한 번 들어간 탭은 컴포지션에서 내리지 않고 숨긴다(배치하지 않고, 의미(semantics)를 비우고, 수명주기를 `CREATED` 로 내려 수집·뒤로가기 콜백을 멈춘다 — 측정은 유지해 목록 항목이 남게 한다). 전환은 즉시 교체다 — 페이드인은 매 프레임 두 화면을 알파 레이어로 합성해 측정상 득이 없었다. 탭 루트에서의 뒤로(시작 탭으로)는 앱 루트의 `BackHandler` 가 받는다. 화면은 자기 전환을 따로 만들지 않는다.
- 근거: 사용자 결정(2026-10-15). 푸시·팝은 처음에 shared axis X(폭 1/10 슬라이드 + 페이드)로 정했으나 실기기에서 덮는 전환이 아니라 크로스페이드로 보여 Activity 전환 모양으로 바꿨다 — S22+에서 시스템 설정 앱 전환을 `screenrecord`(애니메이터 배율 5배)로 녹화해 프레임으로 본 값이다(새 화면이 오른쪽에서 폭 전체로 불투명하게, 이전 화면 왼쪽 약 1/4, 약 300ms). 하단 바는 처음에 `Scaffold`의 `bottomBar`를 `AnimatedVisibility`로 접었는데 푸시 중 나가는 화면의 `innerPadding`이 바와 함께 줄어, 목록 끝까지 스크롤한 홈이 왼쪽으로 밀리면서 아래로 내려앉았고 바도 화면과 따로 움직였다. 사용자가 토스·카카오톡은 바를 따로 내리지 않고 새 화면이 위를 덮으며 원래 화면이 바째로 밀린다고 설명했고, 그대로 구현해 S22+에서 푸시·뒤로·탭 전환을 녹화로 확인했다(바가 페이지와 함께 움직이고 탭 전환 때 바가 사라지지 않음). 토스·카카오톡은 시험 기기에 없어 직접 보지 못했다(research/interaction-motion.md). 탭을 `NavDisplay` 한 곳의 엔트리로 두면 탭을 바꿀 때마다 화면을 처음부터 다시 구성·배치해 그 비용이 느린 프레임의 대부분이었고(S22+ 트레이스, 2026-10-15) 코드 최적화로 줄지 않아 탭 화면을 살려 두는 구조로 바꿨다 — 카카오톡 APK 도 메인 화면의 탭을 `ViewPager2` 로 살려 둔다(정적 분석). 결과(S22+ 릴리스): 탭을 누른 뒤 가장 긴 프레임 36~38ms → 27~31ms, 버벅임 비율은 변화 없음, 들어오는 탭 300ms 페이드는 p90 을 올려 뺐다. 이 구조는 모양을 우선한 사용자 결정이고 성능 비용이 있었다 — S22+ 릴리스에서 설정 푸시·팝 버벅임이 구 셸 약 5.3%에서 새 셸 약 9%로(홈 팝 프레임 41~46ms → 54~65ms) 늘었다(2026-10-15, 프로젝트 보고). 하단 바 컴포지션 약 2.8ms·엔트리 레이어 추가·`applyChanges` 증가를 의심하나 셸만 되돌린 변형으로 분리하지 못했고 구 셸은 다른 시각에 쟀다(미확인, R-41-18). 공식 — 탭(하단 내비게이션 목적지)은 "do not have a strong relationship"이라 fade 계열이라는 Material 패턴 선택 기준과 기본 시간 300ms [S287](https://github.com/material-components/material-components-android/blob/master/docs/theming/Motion.md). Nav3가 전환 3종과 엔트리 metadata 덮어쓰기를 제공하고 "the appropriate scene"의 전환을 쓴다고만 적는다 [S288](https://developer.android.com/guide/navigation/navigation-3/animate-destinations). Nav3 기본 전환의 모양은 문서에 없다. 직접 그린 푸시 움직임의 두 함정은 공식 문서가 다루지 않고 이 프로젝트 S22+에서 `animator_duration_scale`을 10배로 올린 중간 프레임 캡처로 재현했다(빈 전환 → 이전 화면이 첫 프레임에 사라짐, 2026-10-20 / 모달 위에 푸시 움직임이 겹침 → 대각선 진입과 아래 화면 밀림, 2026-10-20). 엔트리 metadata가 그 엔트리의 전환을 기본 전환 대신 쓴다는 계약은 위 [S288]이다.
- 예시:
  ```kotlin
  // Good — 탭 루트 엔트리 안에 바를 그린다(바가 화면과 함께 움직인다). 여백도 엔트리마다
  NavEntryDecorator<NavKey>(decorate = { entry ->
      val tab = entry.metadata[TOP_LEVEL_ENTRY] as? NavKey
      Box(Modifier.fillMaxSize().background(background)) {
          Box(Modifier.fillMaxSize().padding(bottom = navInset + if (tab != null) barBody else 0.dp)) { entry.Content() }
          if (tab != null) TopLevelNavigationBar(selected = tab, onSelect = onSelectTab, Modifier.align(BottomCenter))
      }
  })
  // 탭 전환: 탭마다 NavDisplay 를 두고 TabHost 가 살려 둔다 — 숨은 탭은 측정·의미·수명주기를 멈춘다
  TabHost(selected = state.topLevelRoute) { tab -> AppNavDisplay(entries = state.rememberTabEntries(tab, …), onBack = navigator::goBack) }
  // Bad — 바를 셸에 두고 숨길 때 접으면 나가는 화면의 여백이 흔들리고 바가 화면과 따로 움직인다
  Scaffold(bottomBar = { AnimatedVisibility(visible = atRoot) { NavigationBar { } } }) { innerPadding -> NavDisplay(...) }
  ```
- 체크: `NavDisplay` 전환 인자가 `:app` 한 곳에만 있는가. 화면·feature에 `slideIn*`·`fadeIn`이 있는가. `NavTransitions.push`·`pop`에 `fadeIn`·`fadeOut`이 없는가. 하단 바가 `Scaffold(bottomBar)`가 아니라 탭 루트 엔트리 안에 있는가. 엔트리에 내려보내는 `contentPadding`의 아래가 0이고 아래 여백을 데코레이터가 엔트리마다 비우는가. 탭 사이 이동이 화면을 다시 만들지 않는가(탭마다 `NavDisplay`, 숨은 탭 유지). 숨은 탭이 측정·의미·수집을 멈추는가. **실기기에서 푸시·뒤로(키·제스처)·탭 전환을 한 번씩 눌러 새 화면이 덮고 바가 화면과 함께 움직이는지, 목록 끝까지 스크롤한 화면에서 푸시해도 나가는 화면이 내려앉지 않는지 눈으로 본다 — 글로 정한 값은 모양을 보장하지 않는다.** 전환 위에 앱이 얹은 모션(데코레이터의 `graphicsLayer` 등)이 metadata로 전환을 지정한 엔트리(모달)를 건너뛰는가. **푸시·뒤로·모달 열기·모달 닫기·스택 교체를 각각 `animator_duration_scale` 10배로 올려 중간 프레임을 캡처해 본다 — 전역 전환을 바꾼 수정은 그 전환이 지나는 모든 변형을 이 방식으로 확인한다.**

### R-28-02 모달 전체 화면은 아래에서 위로, 스택 교체는 fade through다
- 규칙: 흐름을 벗어나는 독립 작업 전체 화면은 엔트리 metadata의 `NavDisplay.TransitionKey`를 `{ modalPush() }`(`NavTransitions.kt`의 `AnimatedContentTransitionScope` 확장 함수)로, `PopTransitionKey`·`PredictivePopTransitionKey`를 `NavTransitions.modalPop`으로 덮어써 아래에서 올라오고 닫을 때 아래로 내려가게 한다. 모달의 예측형 뒤로가기는 R-28-03의 `pop` 대신 `modalPop`을 쓴다. 백스택을 비우고 시작 화면으로 교체하는 이동은 `NavTransitions.fadeThrough`를 앞으로·뒤로 키에 모두 덮어쓴다. 어느 화면이 모달 성격인지는 그 화면의 설계가 정한다.
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
- 체크: 모달 엔트리가 전환 키 3개를 모두 덮어쓰는가. 일반 푸시 화면에 `modalPush`가 있는가. 앱이 전환 위에 얹은 모션이 이 엔트리들을 건너뛰는가(R-28-01).

### R-28-03 예측형 뒤로가기는 pop과 같은 모양으로 둔다
- 규칙: 앱이 예측형 뒤로가기를 켜면(지원 API를 쓰면 Android 15 이상에서 기본) **뒤로 키도** `popTransitionSpec`이 아니라 `predictivePopTransitionSpec`을 탄다. 그래서 `NavTransitions.predictivePop`은 `pop`과 같은 전환(R-28-01)으로 둔다. 공식 예측형 수치(나가는 화면 100→90% 축소, 들어오는 화면 110→100%, 35% 경계 fade through)는 쓰지 않는다 — 그 값을 쓰면 뒤로 키에서 위 화면이 오른쪽으로 빠지지 않고 아래 화면이 확대되며 나타나 R-28-01의 모양과 어긋난다. 제스처 중에는 슬라이드가 손가락 진행에 이어지고 놓으면 원래 자리로 돌아온다(제스처 중 미리보기는 실기기에서 확인하지 않았다).
- 근거: 사용자 결정(2026-10-15)과 실기기 녹화 — 뒤로 키를 눌렀을 때 `predictivePopTransitionSpec`의 전환(확대·페이드)이 재생되고 `popTransitionSpec`은 쓰이지 않는 것을 S22+에서 확인했다(research/interaction-motion.md). 공식 수치 표와 35% 경계는 전체 화면 표면의 값이다 [S290](https://developer.android.com/design/ui/mobile/guides/patterns/predictive-back). 앱이 직접 전환을 정할 때 그 값을 따라야 하는지는 문서에서 확인하지 못했다. 지원 API를 쓰면 Android 15 이상에서 기본 활성이고 루트에서 뒤로를 가로채면 back-to-home 애니메이션이 꺼진다 [S291](https://developer.android.com/develop/ui/compose/system/predictive-back-setup).
- 예시:
  ```kotlin
  // Good — 뒤로 키도 이 경로이므로 pop 과 같은 전환을 꽂는다
  val predictivePop: ContentTransform = pop
  predictivePopTransitionSpec = { NavTransitions.predictivePop }
  // Bad — 공식 축소 수치를 쓰면 뒤로 키가 R-28-01 의 슬라이드와 달라진다
  predictivePopTransitionSpec = { scaleIn(initialScale = 1.1f) togetherWith scaleOut(targetScale = 0.9f) }
  ```
- 체크: `predictivePop`이 `pop`과 같은 슬라이드인가. 실기기에서 뒤로 키를 눌렀을 때 위 화면이 오른쪽으로 빠지는가.

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
- 규칙: 클릭으로 이동을 일으키는 콜백은 `dropUnlessResumed { }`로 감싸 생명주기가 RESUMED가 아닌 동안의 호출을 버린다. 전환 중 연타로 화면이 두 번 쌓이는 것을 막으려는 것이고(엔트리 생명주기에 기댄 기대 효과, 미확인), 같은 키 중복 push를 막는 `Navigator`(R-13-03)와 함께 쓴다. 그리고 **이동 자체는 `:app`의 `Navigator`가 한 번에 하나씩 적용한다** — 푸시·팝처럼 전환이 있는 이동 뒤 전환 길이(`Motion.STANDARD_MS` × 시스템 애니메이터 배율)가 지나고 **실제 전환이 끝날 때까지** 들어온 이동(뒤로가기 포함)은 버리지 않고 줄을 세웠다가 순서대로 적용한다(상한 4). 줄을 푸는 신호는 고정 시간이 아니라 전환 종료 보고다 — 엔트리 데코레이터가 전환 진행(`transition.isRunning`)을 보고하고, 이동을 적용하기 직전에 기록을 비운 뒤(앞 이동에서 남은 보고자는 무시) 그 뒤에 보고된 것과 그 이동의 푸시 움직임이 **한 번** 모두 끝나면 푼다(래치). 고정 시간은 하한이고 시작·끝 대기 상한은 복구용이다. 탭 전환은 애니메이션이 없으므로 기다리지 않고 즉시 적용하며 줄을 비운다. 링크 교체도 줄을 비우고 자기만 남기며, 처리된 뒤(적용됐거나 탭 전환·새 링크로 버려졌거나)에 소비한다(줄에 넣고 바로 소비하면 회전으로 줄이 사라질 때 링크가 유실되고, 버려진 링크를 소비하지 않으면 회전 뒤 되살아난다). 줄을 푸는 쪽은 꺼내기 전에 지금의 전환이 끝났는지 다시 본다. 화면을 컴포지션에 둔 채 숨기는 컨테이너는 별도 창(다이얼로그·시트)을 따로 꺼야 한다 — 창은 화면 밖 배치·클립·의미 제거의 영향을 받지 않는다. `NavDisplay`는 전환 중 들어온 이동을 진행 중 `AnimatedContent` 위에 덮어씌워(z-index 재계산) 덮고 밀어내는 모양이 깨진다. **줄에만 서는 뒤로가기도 `NavDisplay`의 예측형 처리를 거치지 않게 한다** — 뒤로가기 키(3버튼)도 `onBackStarted`(진행도 0)를 먼저 보내고 `NavDisplay`는 그것을 예측형 뒤로가기로 보고 이전 화면으로 `seekTo`(미리 컴포지션)하므로, 바쁜 동안(줄이 있거나 전환이 도는 중) 뒤로가기를 받는 가드 핸들러를 **기본 우선순위로 두고 바쁨이 시작될 때마다 다시 등록**해 가장 최근 핸들러로 만든다(같은 우선순위는 나중 등록이 먼저). 가드를 오버레이 우선순위로 켜고 끄면 안 된다 — 플랫폼 콜백(오버레이용·기본용)이 키의 DOWN·UP 사이에 갈려 라이브러리가 완료를 버리고 이후 뒤로가기가 영구히 먹통이 된다.
- 근거: 공식 Nav3 레시피가 이동 클릭을 `dropUnlessResumed`로 감싼다 [S289](https://developer.android.com/guide/navigation/navigation-3/recipes/animations). 문서가 이유를 적지 않아 규칙으로 올리는 것은 사용자 결정이다. 큐: S22+ 녹화(2026-10-15)에서 푸시 80% 지점의 뒤로가기가 전환 위에 덮여 화면이 겹치거나 엉뚱한 탭이 보였고, 큐를 둔 뒤에는 푸시가 끝난 다음 팝이 재생됐다. 애니메이터 배율을 따르지 않으면 개발자 옵션 5배에서 큐가 전환 도중에 풀린다(같은 녹화). 공식 문서는 전환 중 이동의 겹침을 다루지 않는다(프로젝트 사례). 고정 시간만으로 풀면(S22+ 디버그, 2026-10-20) 타이머는 이동 적용 시점부터 재는데 전환은 새 화면의 첫 컴포지션(130~160ms) 뒤에 시작해 +470~510ms 에 끝나, 푸시 도중의 뒤로가기가 전환이 도는 중에 적용돼 뒤로 슬라이드가 사라지고 화면이 멈췄다 컷으로 바뀌었다(5/5). 타이머 뒤에 "지금 비었는가"를 기다리면 뒤로가기 입력이 같은 화면의 전환을 다시 켜 끝난 순간을 놓치고 상한 1.5초까지 교착했다(콜드 2/2) — 끝났는지를 래치한다. 줄에만 서는 뒤로가기(S22+ 디버그 250ms 3연타, 2026-10-21): 키 한 번마다 `onBackStarted`(진행도 0)→10~20ms→`onBackInvoked`(로그, S361·S362), 줄 선 키 2개가 팝 첫 프레임만큼 무거운 프레임(메인 `draw` 80~100ms)을 슬라이드 도중에 만들어 25ms 초과 프레임이 팝 3개에 5개였다(같은 시계로 정렬). 슬라이드 도중 멈춤 4회→0, 버벅임 34%→25%·p90 97→42ms. 오버레이 가드로 먼저 고쳤더니 400ms 간격 3연타 5회 중 2회 뒤로가기 키가 영구히 먹통이었다(가드 없는 HEAD 6회 중 0회, 기본 우선순위 가드 24회 중 0회). 릴리스에서도 줄 선 키의 추가 프레임(35ms)이 사라졌다(오버레이 가드 기준 측정).
- 예시:
  ```kotlin
  // Good
  Button(onClick = dropUnlessResumed { onOpenDetail(id) }) { Text(label) }
  // Bad — 이동 호출이 생명주기와 무관하게 실행된다
  Button(onClick = { onOpenDetail(id) }) { Text(label) }
  ```
- 체크: 이동 콜백을 부르는 `onClick`이 `dropUnlessResumed`로 감싸져 있는가. 이동이 `Navigator` 한 곳의 큐를 거치는가(전환 길이가 애니메이터 배율을 따르는가). 줄을 푸는 신호가 전환 종료 보고인가(고정 시간만이 아닌가), 이동 적용 직전에 기록을 비우는가, 끝 대기가 래치인가(콜드 스타트 모달 + 푸시 직후 뒤로가기로 지연이 없는지 본다). 실기기에서 푸시 직후 뒤로가기·푸시/뒤로 연타를 녹화해 전환이 겹치지 않는지 본다. 줄에만 서는 뒤로가기가 `NavDisplay`의 핸들러를 거치지 않는가(바쁜 동안 키 연타 시 메인 스레드 무거운 프레임이 팝 수만큼만 나오는가, 같은 시계로 키 시각과 겹치지 않는가). 가드가 오버레이가 아닌지, 그리고 간격을 바꿔(150~420ms) 연타를 반복해 뒤로가기가 먹통이 되지 않는지 본다.

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
- 규칙: 스낵바는 4초 표시(내용이 길면 더 길게), 하단 중앙, FAB 위(하단 바가 있으면 그 위)에 두고, 한 번에 1개만 보이며 새 것은 큐에 쌓여 순서대로 나온다(`SnackbarHostState`). `showSnackbar`는 액션 문구가 있으면 `duration` 기본값이 `Indefinite`가 되므로 4초가 되려면 `SnackbarDuration.Short`를 넘긴다. 텍스트 액션은 최대 1개이고 "되돌리기"처럼 구체적인 문구를 쓴다. 누르고 있으면 타이머가 멈추게 한다 — 타이머는 `SnackbarHost`에 있고 Material 3 기본에는 이 동작이 없어(소스 확인) `:core:designsystem`이 호스트를 직접 만든다. `showSnackbar`는 사용자 이벤트 경로에서는 `rememberCoroutineScope`, `UiState` 필드 경로(R-28-09)에서는 `LaunchedEffect`에서 부른다(R-17-08). 직접 만든 호스트는 Material 3 `SnackbarHost`처럼 의미(semantics)도 갖춘다 — `liveRegion = LiveRegionMode.Polite`와 `dismiss { }`를 **스낵바가 떠 있는 동안에만** 붙인다. 사라진 뒤에도 남겨 두면 보이지 않는 빈 노드가 TalkBack에 잡힌다.
- 근거: 공식 — 스낵바는 몇 초 뒤 사라진다 [S296](https://developer.android.com/develop/ui/compose/components/snackbar). Material3 1.4.0 소스에서 `SnackbarDuration.Short`=4000ms, `showSnackbar`의 기본 duration은 액션이 없으면 `Short`·있으면 `Indefinite`, `SnackbarHostState`는 Mutex로 한 번에 하나만 보이는 공정한 큐다. 참고 — SEED 스낵바의 4초·하단 중앙·1개와 큐·액션 1개·터치 유지 시 타이머 일시정지·FAB 위 [S301](https://seed-design.io/docs/components/snackbar). 값 채택(하단 바 위 포함)은 사용자 결정이다. 의미 — Material3 `SnackbarHost.kt`는 항목마다 `if (isVisible) { liveRegion = LiveRegionMode.Polite }`와 `dismiss { key.dismiss(); true }`를 붙이고 표시 시간을 `accessibilityManager.calculateRecommendedTimeoutMillis`로 늘린다(소스 확인 2026-10-15) [S350](https://raw.githubusercontent.com/androidx/androidx/androidx-main/compose/material3/material3/src/commonMain/kotlin/androidx/compose/material3/SnackbarHost.kt). "Alert-like components can be marked with the `liveRegion` semantics property"이고 대부분 `Polite`를 쓴다 [S348](https://developer.android.com/develop/ui/compose/accessibility/semantics). 사라진 뒤 남은 빈 노드가 TalkBack에 잡힌 것은 InfocarEV `AppSnackbarHost` 리뷰 사례(2026-10-15)다.
- 예시:
  ```kotlin
  // Good — 액션이 있어도 4초
  scope.launch { snackbarHostState.showSnackbar("삭제했습니다", actionLabel = "되돌리기", duration = SnackbarDuration.Short) }
  // Bad — 액션이 있는데 duration 을 생략해 무기한이 된다 / 액션을 둘 두거나 "확인" 같은 모호한 문구
  scope.launch { snackbarHostState.showSnackbar("삭제했습니다", actionLabel = "되돌리기") }
  ```
- 체크: 스낵바 액션이 1개이고 문구가 구체적인가. 액션이 있는 `showSnackbar`가 `duration`을 넘기는가. 지속 경고에 `Indefinite` 스낵바를 쓰는가. 직접 만든 호스트에 `liveRegion`·`dismiss`가 있고 스낵바가 없을 때는 빠지는가.

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
- 규칙: 로딩→콘텐츠→오류처럼 화면 상태가 바뀔 때는 `Crossfade`를 쓰고, 크기·위치가 달라지는 전환에만 `AnimatedContent`를 쓴다. 항목이 추가·삭제·재정렬되는 목록은 `LazyColumn` 항목에 `Modifier.animateItem()`을 쓰고 항목에 안정적인 `key`를 준다. 두 API의 기본 fade는 라이브러리 기본 spec이므로 `animationSpec = Motion.standard()`, `fadeInSpec = Motion.enter()`·`fadeOutSpec = Motion.exit()`로 토큰을 넘기고 `placementSpec`은 `Motion.basicSpring()`을 쓴다(R-28-14). 알파 레이어의 크기는 효과를 건 컴포저블의 **측정 경계**이므로(R-41-11), 화면보다 훨씬 큰 측정 경계를 가진 긴 비-lazy `Column`(한 달치 목록 본문 등)에는 `Crossfade`·알파를 걸지 않는다. 목록은 `LazyColumn` 항목으로 쪼개고 갈래 전환은 즉시 교체한다.
- 근거: 공식 — "Use `AnimatedContent` to animate between different composables, if you just want a standard fade between composables, use `Crossfade`" [S293](https://developer.android.com/develop/ui/compose/animation/quick-guide). `animateItem()`은 항목 변화 애니메이션이고 `key`를 줘야 제대로 동작한다고 적는다 [S306](https://developer.android.com/develop/ui/compose/lists). `animateItem`의 fade 기본값이 spring이라는 점은 리뷰가 foundation 1.9.4 소스에서 확인했다(이 프로젝트 버전과의 연결은 미확인). 이 둘을 앱 규칙으로 올린 것은 사용자 결정이다. 알파 레이어가 효과를 건 컴포저블의 측정 경계 크기라는 점은 공식 문서이고 [S325], `ModulateAlpha` 경로는 오프스크린 버퍼를 만들지 않아 모든 알파가 같은 비용은 아니다(R-41-11). 이 프로젝트 기록 탭에서는 항목 하나 안의 `Column`(약 5,300dp)에 `Crossfade`를 걸자 RenderThread가 `drawLayer [graphicsLayer] 990 x 14940`을 매 프레임 구워 프레임이 20~50ms가 된 것을 S22+ Perfetto 손가락 탭 트레이스로 확인했다(2026-10-19, 이 배치에 한정한 관찰). 항목으로 쪼갠 뒤 전환 6회의 프레임 430→226개, p99 61→40ms였다. 규칙의 채택은 사용자 요청(2026-10-20)에 따른 제안이다(`91-decisions.md`).
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
- 규칙: 누를 수 있는 요소는 눌린 동안 표면 색이 `MaterialTheme.colorScheme` 역할로 바뀌고, 구조가 허락하면 요소가 정해진 **거리**(2dp)만큼 줄어든다. 축소는 `Modifier.pressScale(interactionSource)`가 하고 기준 길이는 `max(높이, 폭÷4, 24dp)`, 배율은 `(기준−2)÷기준`이다. 색과 축소는 같은 150ms(`Motion.fast()`)를 써 함께 시작하고 끝난다. 터치 영역은 48dp 이상이다. **검정이 아닌 색을 그 색 그대로 나타내고 감추려고(페이드) 보간·`animateColorAsState`의 끝점에 `Color.Transparent`를 쓰지 않는다** — `lerp`는 Oklab에서 알파와 색 성분을 따로 보간하므로 알파 0 검정(`Transparent`)과 섞이는 중간 프레임이 그 색보다 어두워져 점멸한다(검정 자체를 페이드하는 경우는 같은 값이라 해당 없다). 그 색의 알파 0(`color.copy(alpha = 0f)`)이나 알파만 움직이는 보간을 쓴다. 그림자가 있는 면에 반투명 면을 쓰면 그림자가 비치므로 불투명 면을 쓴다(이 프로젝트 관찰, 제안).
- 근거: 공식 — 터치 영역은 48dp 이상이다 [S292](https://developer.android.com/docs/quality-guidelines/core-app-quality). 참고 — SEED는 눌림을 색 + 축소로 전달하고 둘 다 d3(150ms)를 쓰며 [S300](https://seed-design.io/docs/foundation/feedback), 축소를 배율이 아니라 거리로 정의한다(세로 2px, 기준 길이 `max(높이, 폭÷4, 24)`, 배율 `(기준−2)÷기준`) [S309](https://seed-design.io/foundations/feedback/scale). 채택은 사용자 결정이다. `lerp`의 KDoc은 "Interpolation is done in the [ColorSpaces.Oklab] color space"라 적고 구현은 알파를 별도 성분으로 보간한다 [S358](https://raw.githubusercontent.com/androidx/androidx/androidx-main/compose/ui/ui-graphics/src/commonMain/kotlin/androidx/compose/ui/graphics/Color.kt). 중간 프레임이 어둡게 보이는 것은 이 프로젝트 S22+에서 화면 녹화 프레임의 픽셀로 확인했다(2026-10-19).
- 예시:
  ```kotlin
  // Good — 거리 기준 축소
  Box(Modifier.pressScale(interactionSource).clickable(interactionSource, null) { onClick() })
  // Bad — 요소마다 고정 배율을 쓴다 (큰 요소일수록 많이 움직인다)
  Modifier.graphicsLayer { scaleX = 0.98f; scaleY = 0.98f }
  ```
- 체크: 눌릴 수 있는 요소에 색 변화가 있는가. 눌림 축소가 고정 배율인가. 터치 영역이 48dp 미만인가. `pressScale`과 `clickable`의 순서에 따른 히트 영역·리플 차이는 확인하지 못했다. 색 보간·`animateColorAsState`의 끝점에 `Color.Transparent`가 있는가(프로젝트가 Konsist로 막을 수 있다).

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

### R-28-22 화면을 채울 수 있는 바텀시트는 시트 modifier에 `statusBarsPadding()`을 걸어 최대 높이를 상태 바 아래로 제한한다
- 규칙: `ModalBottomSheet` 내용이 길어 시트가 화면 높이까지 커질 수 있으면(도움말·긴 목록·약관) 시트의 `modifier`에 `Modifier.statusBarsPadding()`을 건다. 기본값에서는 시트 안쪽에 상단 인셋 패딩이 들어가는데 그 크기가 시트의 현재 위치에서 정해져, 열림 애니메이션(300ms)이 끝난 뒤에도 윗면이 몇 초에 걸쳐 조금씩 더 올라가 상태 바 영역까지 덮는다. 짧은 시트는 영향이 없다. 시트는 별도 창이라 스크린샷 테스트가 내용만 찍으므로(R-28-08의 래퍼) 긴 시트는 기기에서 열고 2초 이상 지켜보거나 화면 녹화 프레임으로 윗면 위치를 잰다.
- 근거: Material3 1.4.0 소스에서 `ModalBottomSheetContent`는 `Surface`에 `consumeWindowInsets(WindowInsets(top = sheetState.offset …))`를 걸고 안쪽 `Column`에 `windowInsetsPadding(contentWindowInsets())`를 건다. `contentWindowInsets` 기본값 `BottomSheetDefaults.windowInsets`는 `WindowInsets.safeDrawing`의 위·아래이고, `Expanded` 앵커는 `max(0f, fullHeight - sheetSize.height)`로 시트 높이에서 정해진다 [S360](https://dl.google.com/dl/android/maven2/androidx/compose/material3/material3-android/1.4.0/material3-android-1.4.0-sources.jar). 공식 시트 문서는 이 상호작용을 적지 않는다 [S297](https://developer.android.com/develop/ui/compose/components/bottom-sheets). InfocarEV_Android S22+ 녹화(약 66fps, 2026-10-20): 도움말 시트가 열림 애니메이션 뒤 윗면 y=84에서 약 2.2초 동안 y=13까지 올라가는 동안 제목(y=191)과 마지막 줄(y=1986)은 1px 안에서 움직이지 않았다 — 내용은 그대로이고 면만 위로 늘어났다. `statusBarsPadding()` 뒤에는 윗면이 y=87에서 멈췄다. 되먹임(패딩 → 높이 → 위치 → 패딩)이 프레임마다 한 칸씩 도는 단계별 메커니즘은 소스와 측정에서 추론한 것이고 Compose 내부를 추적해 확인하지는 않았다(미확인). 확인한 것은 1.4.0뿐이다 — 이후 소스는 인셋 처리가 다르다.
- 예시:
  ```kotlin
  // Good — 시트의 최대 높이를 상태 바 아래로 제한한다
  ModalBottomSheet(onDismissRequest = onDismiss, modifier = Modifier.statusBarsPadding()) { LongHelp() }
  // Bad — 긴 내용이 기본값에서 열린 뒤 윗면이 상태 바 영역까지 서서히 올라간다
  ModalBottomSheet(onDismissRequest = onDismiss) { LongHelp() }
  ```
- 체크: 긴 내용을 담을 수 있는 `ModalBottomSheet`(래퍼 포함)에 `statusBarsPadding()`이 있는가. 긴 시트가 열린 뒤 윗면이 움직이는가(녹화 프레임으로 잰다).
