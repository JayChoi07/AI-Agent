# 인터랙션·모션(화면 이동·오버레이·피드백·모션 토큰) 조사 노트

조사일: 2026-10-14
담당 references 파일 번호: 28(인터랙션·모션)

목적: 앱 전체에 깔리는 화면 이동·오버레이·피드백·모션 규칙을 정하기 위해 공식 문서와 국내 사례(토스·당근 SEED)를 확인했다. 카카오·인스타그램은 공개된 Android 기준을 찾지 못했다(검색 도구가 미국 기준이라 한국어 사내 자료는 놓쳤을 수 있다 — "없다"가 아니라 "못 찾았다"). 아래 지역 번호(S1…)는 이 노트에서만 쓰고, 규칙은 `90-sources.md`·`92-sources-2.md`의 전역 번호를 인용한다.

## 출처

| # | 전역 | 조직 | 문서명 | URL | 문서 종류 |
|---|---|---|---|---|---|
| S1 | S287 | Google | Motion (MDC-Android theming) | https://github.com/material-components/material-components-android/blob/master/docs/theming/Motion.md | 오픈소스 문서 |
| S2 | S288 | Google | Nav3 Animate between destinations | https://developer.android.com/guide/navigation/navigation-3/animate-destinations | 공식 가이드 |
| S3 | S289 | Google | Nav3 레시피 Animations | https://developer.android.com/guide/navigation/navigation-3/recipes/animations | 공식 가이드 |
| S4 | S290 | Google | Predictive back design | https://developer.android.com/design/ui/mobile/guides/patterns/predictive-back | 공식 가이드 |
| S5 | S291 | Google | Set up predictive back (Compose) | https://developer.android.com/develop/ui/compose/system/predictive-back-setup | 공식 가이드 |
| S6 | S292 | Google | Core app quality guidelines | https://developer.android.com/docs/quality-guidelines/core-app-quality | 공식 가이드 |
| S7 | S293 | Google | Quick guide to animations in Compose | https://developer.android.com/develop/ui/compose/animation/quick-guide | 공식 가이드 |
| S8 | S294 | Google | Customize animations (Compose) | https://developer.android.com/develop/ui/compose/animation/customize | 공식 가이드 |
| S9 | S295 | Google | Dialog (Compose) | https://developer.android.com/develop/ui/compose/components/dialog | 공식 가이드 |
| S10 | S296 | Google | Snackbar (Compose) | https://developer.android.com/develop/ui/compose/components/snackbar | 공식 가이드 |
| S11 | S297 | Google | Bottom sheets (Compose) | https://developer.android.com/develop/ui/compose/components/bottom-sheets | 공식 가이드 |
| S12 | S298 | Google | MotionDurationScale | https://developer.android.com/reference/kotlin/androidx/compose/ui/MotionDurationScale | 공식 API 레퍼런스 |
| S13 | S299 | 당근 | SEED Design — Motion | https://seed-design.io/docs/foundation/motion | 디자인 시스템 문서(참고) |
| S14 | S300 | 당근 | SEED Design — Feedback (+ Scale) | https://seed-design.io/docs/foundation/feedback | 디자인 시스템 문서(참고) |
| S15 | S301 | 당근 | SEED Design — Snackbar | https://seed-design.io/docs/components/snackbar | 디자인 시스템 문서(참고) |
| S16 | S302 | 당근 | SEED Design — Bottom Sheet | https://seed-design.io/docs/components/bottom-sheet | 디자인 시스템 문서(참고) |
| S17 | S303 | 토스 | 인터랙션, 꼭 넣어야 해요? | https://toss.tech/article/interaction | 기술 블로그 |
| S18 | S304 | 토스 | 이런 것도 컴포넌트로 만들어도 될까? | https://toss.tech/article/tds-component-making | 기술 블로그 |
| S19 | S305 | 토스 | TDS Mobile useBottomSheet | https://tossmini-docs.toss.im/tds-mobile/hooks/OverlayExtension/use-bottom-sheet/ | 디자인 시스템 문서(참고) |
| S20 | S306 | Google | Lazy lists and lazy grids | https://developer.android.com/develop/ui/compose/lists | 공식 가이드 |
| S21 | S307 | Google | Progress indicators (Compose) | https://developer.android.com/develop/ui/compose/components/progress | 공식 가이드 |
| S22 | S308 | Google | Add haptic feedback to events (Views) | https://developer.android.com/develop/ui/views/haptics/haptic-feedback | 공식 가이드 |
| S23 | S309 | 당근 | SEED Design — Feedback · Scale | https://seed-design.io/foundations/feedback/scale | 디자인 시스템 문서(참고) |

SEED·토스 문서는 공식 기준이 아니라 사례다. 토스 TDS Mobile 문서는 웹 기반(앱인토스)이고 SEED는 px 단위의 플랫폼 공통 스펙이라, Android 네이티브 구현 방식은 확인하지 못했다.

## 확인한 내용 (원문 대조)

- **Material 패턴(S1)**: 전환 패턴이 넷이다. container transform(컨테이너가 있는 요소 사이), shared axis("spatial or navigational relationship", 온보딩은 x축·부모-자식은 z축), fade through("not have a strong relationship", 하단 내비 목적지), fade(화면 안에서 들어오고 나가는 다이얼로그·메뉴·스낵바). shared axis·fade through의 기본 시간은 300ms로 적혀 있다.
- **Material 곡선·시간(S1, 내려받아 grep)**: standard `cubic-bezier(0.2,0,0,1)`(화면 안에서 시작하고 끝남), standard decelerate `(0,0,0,1)`(들어옴), standard accelerate `(0.3,0,1,1)`(나감). 시간 Short1~4 = 50·100·150·200, Medium1~4 = 250·300·350·400, Long1~4 = 450·500·550·600, ExtraLong1~4 = 700·800·900·1000ms.
- **Material 스프링(S1)**: 6개 — fast spatial(damping 0.9, stiffness 1400, 스위치·버튼), default spatial(0.9, 700, 일부 화면을 덮는 바텀시트·드로어), slow spatial(0.9, 300, 전체 화면), 그리고 같은 속도의 effects 3개(damping 1, 3800·1600·800 — 색·투명도처럼 넘치면 안 되는 속성). 속도는 움직이는 크기·거리로 고른다.
- **Nav3 전환(S2·S3)**: `NavDisplay`의 `transitionSpec`·`popTransitionSpec`·`predictivePopTransitionSpec` 3종, 엔트리 metadata 키 `NavDisplay.TransitionKey`·`PopTransitionKey`·`PredictivePopTransitionKey`로 덮어쓰기. 정의가 없으면 "default transition"으로 대체하는데 그 모양은 문서에 적혀 있지 않다(확인 못 함). `sharedTransitionScope`는 한 엔트리가 두 Scene 사이를 오갈 때의 끊김을 막는 용도로 적혀 있다.
- **예측형 뒤로가기(S4·S5)**: 전체 화면 표면의 모션 수치 — 나가는 쪽 100%→90%, 들어오는 쪽 110%→100%, fade-through 경계 35%("neither screen is showing" 지점), 인터폴레이터 (.1,.1,0,1), 비커밋(놓음)이면 원래 크기로 복귀. 지원 API를 쓰면 Android 15 이상에서 기본 활성이고, 루트 액티비티에서 뒤로를 가로채면 back-to-home 애니메이션이 꺼진다. 같은 문서가 "이 공유 요소 전환은 FragmentManager·Navigation Component·Navigation Compose에서 동작하지 않는다"고 적는데 Nav3는 언급이 없다. 시스템 제스처 인셋 아래에 터치·드래그 대상을 두지 말라는 권고도 있다(채택하지 않음).
- **앱 품질 기준(S6)**: Back_Button_Nav("표준 뒤로 버튼을 지원하고 자체 on-screen 뒤로 프롬프트를 쓰지 않는다"), Back_Gesture_Nav, State_Preservation("뒤로가기 등으로 인한 우발적 데이터 손실을 막고 복귀 시 상태를 복원한다"), Touch_Target_Size(48dp 이상). Back_Button_Nav의 "custom on-screen back button prompts"가 뒤로 이벤트에 띄우는 확인 다이얼로그까지 막는지는 문구만으로 단정할 수 없다 — 규칙은 State_Preservation의 데이터 손실 방지를 근거로 삼는다(해석 불확실).
- **Compose 애니메이션(S7·S8·S12)**: 레이아웃 단계를 바꾸는 애니메이션은 영향받는 컴포저블을 모두 다시 배치하고 그리기 단계가 기본적으로 더 낫다. `Modifier.graphicsLayer {}` 같은 람다 Modifier를 권한다. `AnimatedContent`는 컴포저블 사이 전환, 표준 페이드만 필요하면 `Crossfade`. `spring(dampingRatio, stiffness)`(기본 `DampingRatioNoBouncy`·`StiffnessMedium`), 내장 이징 `FastOutSlowIn`·`LinearOutSlowIn`·`FastOutLinear`·`Linear`·`CubicBezierEasing`. `MotionDurationScale.scaleFactor`가 0이면 모션이 다음 프레임 콜백에서 끝난다(S12). 시스템 "애니메이션 줄이기"·개발자 옵션 배율이 이 값에 연결된다는 문장은 공식 레퍼런스에서 확인하지 못했다(비공식 논의에서만 보임).
- **오버레이(S9~S11)**: 다이얼로그 용도 = 동작 확인·입력 요청·선택지 목록, `onDismissRequest`는 바깥 탭 등으로 호출. 스낵바 용도 = 동작 확인과 되돌리기·네트워크 상태·저장 완료, "몇 초 뒤 사라진다". 바텀시트는 `ModalBottomSheet` + `SheetState`(`show`·`hide`), 숨긴 뒤 컴포지션에서 제거한다.
- **Material3 1.4.0 소스(Gradle 캐시의 sources.jar 직접 확인)**: `SnackbarDuration.Short`=4000ms, `Long`=10000ms. `SnackbarHostState`는 Mutex로 한 번에 하나만 보이고 나머지는 공정한 큐에 쌓인다. 스낵바에 "누르고 있으면 타이머 정지" 코드는 없다(`SnackbarHost.kt`·`Snackbar.kt`에서 pause·pointerInput 검색 0건). `ModalBottomSheetProperties`는 `shouldDismissOnBackPress`·`shouldDismissOnClickOutside`를 갖고 `rememberModalBottomSheetState(confirmValueChange)`가 있다. `MotionScheme`은 이 버전에서 `internal interface`다 — 공개 API가 아니다(릴리스 노트상 `MaterialTheme.motionScheme`은 1.5.0 알파 줄에서 보인다).
- **SEED(S13~S16, 원문 내려받아 대조)**: 시간 d1~d6 = 50~300ms, 미세 모션 0.2초 이하·거시 모션 0.2초 초과. 눌림은 표면 색 + 축소를 150ms(d3)로 같이 쓰고, 축소는 배율이 아니라 **거리**(세로 2px, 기준 길이 `max(높이, 폭÷4, 24)`, 배율 = (기준−2)÷기준, 중심 기준, 자리는 안 바뀜). OS 동작 줄이기가 켜지면 축소는 일어나지 않고, "어떤 요소가 빠지더라도 눌렸다는 사실은 남아야 한다". 스낵바: 4초(긴 내용은 더 길게), 하단 중앙, 한 번에 1개 + 큐, 터치를 유지하면 타이머 일시정지, "되돌리기" 같은 짧은 보조 액션, 지속 경고는 Page Banner·Callout. 바텀시트: 아래 드래그·바깥 탭으로 닫힘이 기본, 복잡한 양식·결제·화면이 바뀌는 시트는 상단 닫기 버튼과 핸들 제거, 최대 높이 90%(넘으면 전체 화면 권장), 스냅 포인트 기본 90·50·10%, 스크롤은 내용 영역만. 시트 모션은 등장 `d6`(300ms), 퇴장 `d4`(200ms)를 쓴다.
- **토스(S17~S19)**: 모션 이징을 bezier와 spring 두 가지로 쓰고, "하나의 타겟엔 하나의 모션만 붙일 수 있다"는 규칙을 둔다. 이징에 `bezier.expo`·`spring.quick`·`spring.basic` 같은 이름(토큰)을 붙여 개발·디자인이 같은 말로 소통한다. duration 수치와 spring 파라미터는 공개하지 않았다. 셀렉터를 바텀시트에서 메뉴로 바꾼 A/B 결과는 "전체 지표 큰 변화 없음, Android는 메뉴에서 아이템 클릭율이 10% 더 높음"이다(상대·절대 불명). TDS 바텀시트는 배경 클릭으로 닫는 것이 기본(`closeOnDimmerClick` 기본 true)이고 뒤로가기로 닫는 것을 권장한다(`UNSAFE_ignoreBackEvent`).

## 충돌·주의

- Material 문서의 fade 패턴 기본값은 등장 150ms·퇴장 75ms 선형이다. 규칙(R-28-12)은 SEED를 따라 등장 300ms·퇴장 200ms를 쓴다 — 사용자 결정이고 Material 기본값과 다르다.
- Material 문서 안에서도 `motionDurationLong1`이 시간 표에는 450ms로, 전환 기본값 표에는 300ms로 적혀 있다. 이 팩은 시간 표의 단계 이름(Short3·Short4·Medium2)과 값(150·200·300)만 쓴다.
- 공식 Nav3 레시피의 `ExitTransition.KeepUntilTransitionsFinished`는 companion 멤버가 `internal`이라 스코프 밖의 상수로는 컴파일되지 않는다(스크래치 컴파일 오류로 확인). 그러나 `AnimatedContentTransitionScope` 안에서는 공개 확장 속성이 쓰인다 — 스크래치에서 `AnimatedContentTransitionScope<*>.modalPush()` 확장 함수와 엔트리 metadata 람다 안의 `{ modalPush() }`가 컴파일됐다(리뷰 지적으로 처음의 "internal이라 못 쓴다"는 서술을 고쳤다). 하층 화면이 실제로 유지되는지는 화면에서 확인하지 않았다.
- 공식 문서끼리도 엇갈린다 — 예측형 뒤로가기 디자인 문서는 표면이 떨어져 나가는 공유 요소 전환이 Navigation Compose에서 동작하지 않는다고 적고("run the shared element transition as the user navigates back"으로 대체), 설정 문서는 Navigation Compose의 공유 요소와 예측형 뒤로가기 통합을 안내한다.
- Material 문서 안의 단계 값이 서로 다르다 — `motionDurationLong1`이 시간 표에서는 450ms, 전환 기본값 표에서는 300ms이고, Short1·Short2는 시간 표에서 50·100ms인데 fade 표에서는 75·150ms로 쓰이며, Medium2는 시간 표 300ms인데 container transform의 outgoing은 250ms로 적혀 있다. 이 팩은 시간 표의 값(Short3=150, Short4=200, Medium2=300)만 쓴다.
- 레시피 본문은 `sceneStrategy`(단수)라고 적지만 레시피 코드는 `sceneStrategies = listOf(...)`다. 팩 R-13-04의 표기가 코드와 같다.

## 확인하지 못한 것

- Nav3 기본 전환의 모양, Nav3 `predictivePopTransitionSpec`에서 공식 수치가 그대로 표현되는지.
- 시스템 "애니메이션 줄이기"가 Compose의 `MotionDurationScale`에 연결되는 방식의 공식 문서, 앱이 줄이기 상태를 직접 읽는 공식 방법.
- 카카오·인스타그램의 공개 인터랙션 기준.
- 실기기 시각 확인 전부(전환 거리·모달 전환의 아래 화면 유지·`pressScale` 체감·spring 3단의 느낌).

## 결정 (사용자, 2026-10-14)

91-decisions.md의 MOTION_* 행에 있다. 요약: 값은 팩에 넣고 비공식 값은 "사용자 결정"으로 표시, 푸시·팝은 shared axis X·최상위는 fade through·모달은 아래→위·스택 교체는 fade through, 예측형 뒤로가기는 공식 수치, 오버레이 4종 매핑, bezier 3종 + spring 3종, 누름 = 색 + 축소 150ms, 햅틱은 전역 스위치(기본 꺼짐), 제스처 정책은 정하지 않음.

## 리뷰 반영 (2026-10-14)

독립 서브에이전트와 astra(`gpt-6-astra`, effort high, read-only) 두 리뷰가 같은 곳을 짚은 것은 시트 닫힘 차단이 `hide()`까지 막는 문제(`SheetState.hide()`가 `confirmValueChange(Hidden)`을 거친다), 액션 있는 `showSnackbar`의 기본 duration이 `Indefinite`인 점이다. 반영한 것: `confirmValueChange`에 `allowHide` 플래그를 두는 시트 예시, 스낵바 duration 명시, 레이아웃 단계 예외(`Modifier.layout`·`animateContentSize`), `animateItem`·`Crossfade`의 fade에 토큰 명시, 공유 요소 규칙의 예외와 근거 정정, 오버레이 복원 범위의 구분, Konsist 테스트의 주석 제거·모듈 경로 판정 강화, SEED Scale 출처 등록, `modalPush`를 공식 `KeepUntilTransitionsFinished`를 쓰는 확장 함수로 교체. 반영하지 않은 것: 없음("가설"로 남긴 지적은 규칙에 미확인으로 적었다).

## 검증 (스크래치 복제본, 2026-10-14)

`InfocarEV_Android`를 복제해 `:app`에 템플릿 3개(`Motion`·`NavTransitions`·`Feedback`)를 넣고 `ConsoleApp`의 `NavDisplay`에 전환 3종을 꽂아 `compileDebugKotlin`·`ktlintCheck`·`detektDebug`를 통과시켰다(JDK 25.0.3, AGP 9.4.0, Compose BOM 2026.08.00). Konsist 0.17.3 테스트는 리뷰 반영 뒤 실제 모듈 배치(`core/designsystem/src/main/…`)에서 깨끗한 코드 통과, `tween<Float>(300)` 실패, `tween(easing = …, durationMillis = 300)` 실패, 주석·문자열 안에만 있는 `tween(300)` 통과, 문자열 리터럴(`"https://…"`) 뒤의 `tween(300)` 실패, `:app` 안의 `core/designsystem` **패키지 폴더**에 둔 리터럴 실패를 확인했다(2차 리뷰의 문자열 안 `//` 지적까지 반영). 규칙 예시의 API(`BackHandler`·`dropUnlessResumed`·`DialogProperties`·`ModalBottomSheetProperties`·`SnackbarHostState`·`Modifier.animateItem`·`Crossfade`·`LocalHapticFeedback` 등)는 같은 환경에서 컴파일했다. 실기기·시각 확인은 하지 않았다.
