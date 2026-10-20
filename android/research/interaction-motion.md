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

- Nav3 기본 전환의 모양(문서에 없음). `predictivePopTransitionSpec`에서 공식 수치가 제스처 진행에 그대로 이어지는지(제스처 중 미리보기는 못 봤다).
- 시스템 "애니메이션 줄이기"가 Compose의 `MotionDurationScale`에 연결되는 방식의 공식 문서, 앱이 줄이기 상태를 직접 읽는 공식 방법.
- 카카오·인스타그램의 공개 인터랙션 기준.
- 실기기 시각 확인 중 푸시·팝·뒤로 키는 2026-10-15에 했다(아래). 모달 전환의 아래 화면 유지·`pressScale` 체감·spring 3단의 느낌은 아직이다.

## 결정 (사용자, 2026-10-14)

91-decisions.md의 MOTION_* 행에 있다. 요약: 값은 팩에 넣고 비공식 값은 "사용자 결정"으로 표시, 푸시·팝은 shared axis X·최상위는 fade through·모달은 아래→위·스택 교체는 fade through, 예측형 뒤로가기는 공식 수치, 오버레이 4종 매핑, bezier 3종 + spring 3종, 누름 = 색 + 축소 150ms, 햅틱은 전역 스위치(기본 꺼짐), 제스처 정책은 정하지 않음.

## 리뷰 반영 (2026-10-14)

독립 서브에이전트와 astra(`gpt-6-astra`, effort high, read-only) 두 리뷰가 같은 곳을 짚은 것은 시트 닫힘 차단이 `hide()`까지 막는 문제(`SheetState.hide()`가 `confirmValueChange(Hidden)`을 거친다), 액션 있는 `showSnackbar`의 기본 duration이 `Indefinite`인 점이다. 반영한 것: `confirmValueChange`에 `allowHide` 플래그를 두는 시트 예시, 스낵바 duration 명시, 레이아웃 단계 예외(`Modifier.layout`·`animateContentSize`), `animateItem`·`Crossfade`의 fade에 토큰 명시, 공유 요소 규칙의 예외와 근거 정정, 오버레이 복원 범위의 구분, Konsist 테스트의 주석 제거·모듈 경로 판정 강화, SEED Scale 출처 등록, `modalPush`를 공식 `KeepUntilTransitionsFinished`를 쓰는 확장 함수로 교체. 반영하지 않은 것: 없음("가설"로 남긴 지적은 규칙에 미확인으로 적었다).

## 검증 (스크래치 복제본, 2026-10-14)

`InfocarEV_Android`를 복제해 `:app`에 템플릿 3개(`Motion`·`NavTransitions`·`Feedback`)를 넣고 `ConsoleApp`의 `NavDisplay`에 전환 3종을 꽂아 `compileDebugKotlin`·`ktlintCheck`·`detektDebug`를 통과시켰다(JDK 25.0.3, AGP 9.4.0, Compose BOM 2026.08.00). Konsist 0.17.3 테스트는 리뷰 반영 뒤 실제 모듈 배치(`core/designsystem/src/main/…`)에서 깨끗한 코드 통과, `tween<Float>(300)` 실패, `tween(easing = …, durationMillis = 300)` 실패, 주석·문자열 안에만 있는 `tween(300)` 통과, 문자열 리터럴(`"https://…"`) 뒤의 `tween(300)` 실패, `:app` 안의 `core/designsystem` **패키지 폴더**에 둔 리터럴 실패를 확인했다(2차 리뷰의 문자열 안 `//` 지적까지 반영). 규칙 예시의 API(`BackHandler`·`dropUnlessResumed`·`DialogProperties`·`ModalBottomSheetProperties`·`SnackbarHostState`·`Modifier.animateItem`·`Crossfade`·`LocalHapticFeedback` 등)는 같은 환경에서 컴파일했다. 실기기·시각 확인은 하지 않았다.

## 실기기 녹화로 푸시·팝을 다시 정함 (2026-10-15)

**왜 다시 봤나.** 2026-10-14에 글로 설명한 선택지에서 푸시·팝을 "shared axis X, 폭 1/10 슬라이드"로 정했다. 사용자가 실기기에서 보고 "화면이 오른쪽에서 왼쪽으로 덮이고, 뒤로 가면 왼쪽에서 오른쪽으로 나가는 Activity 전환 느낌"이 아니라고 했다. 그 시점까지 이 전환은 한 번도 움직이는 모습으로 본 적이 없었다(규칙 근거에 "실기기에서 조정해야 한다"고 적어 두기만 했다).

**방법.** S22+(Android 16)에서 `adb shell screenrecord`로 푸시와 뒤로를 녹화하고 영상의 프레임을 시간표와 함께 뽑아 나란히 봤다. 전환이 300ms라 프레임이 4~5장뿐이어서 `adb shell settings put global animator_duration_scale 5`로 5배 느리게 해서 다시 녹화했다(끝나면 1로 되돌린다). 같은 방법으로 시스템 설정 앱의 전환(기준)과 우리 앱을 비교했다. 토스·카카오톡은 기기에 설치돼 있지 않아 보지 못했다.

**관찰.**
- 기준(시스템 설정 앱): 푸시는 새 화면이 오른쪽에서 폭 전체로 **불투명하게** 들어와 이전 화면을 덮고, 이전 화면은 왼쪽으로 약 1/4 밀리며 어두워진다(전체 약 220~330ms, 페이드 없음). 뒤로는 위 화면이 오른쪽으로 빠지고 아래 화면이 왼쪽에서 돌아온다.
- 처음 구현(shared axis X): 이전 화면이 투명해지고 새 화면이 그 위로 페이드인하며 옆으로는 거의 밀리지 않아 두 화면이 겹쳐 비치는 크로스페이드로 보였다.
- 뒤로 키: 이 앱은 예측형 뒤로가기가 켜져 있어 뒤로 키가 `popTransitionSpec`이 아니라 `predictivePopTransitionSpec`(공식 수치: 아래 화면이 110%에서 줄어들며 나타나고 위 화면은 90%로 줄며 사라짐)을 탔다. `push`만 고쳐서는 뒤로 동작이 바뀌지 않는다.
- 덮는 모양이 되려면 엔트리가 불투명해야 한다(엔트리 데코레이터로 앱 배경색을 깔았다). 푸시는 새 화면이, 팝은 나가는 화면이 위에 그려지도록 `targetContentZIndex`를 둔다. 같은 곡선·시간을 두 화면에 쓰지 않으면 두 화면 사이에 틈이 보일 수 있다.

**결정.** 푸시·팝·예측형 뒤로가기를 모두 위 "덮고 밀어내기" 한 가지로 통일했다(R-28-01, R-28-03). 밀리는 거리 1/4과 300ms는 녹화를 눈으로 맞춘 값이다. 모달(아래→위)과 최상위(fade through)는 그대로다.

**교훈.** 모션은 글로 정한 값만으로는 모양을 알 수 없다. 규칙에 "기기에서 한 번 보라"는 체크를 넣었다(R-28-01 체크). 서로 다른 두 규칙(R-28-01의 pop과 R-28-03의 예측형)이 같은 화면 동작에 겹치는지는 규칙 작성 때 확인해야 한다.

## 하단 바를 탭 루트 화면의 일부로 (2026-10-15)

**문제.** 하단 바를 `Scaffold(bottomBar)`로 두고 상세로 갈 때 `AnimatedVisibility`(페이드+접기)로 숨겼다. 사용자가 "홈에서 최근 충전을 누르면 사라지는 화면이 왼쪽으로 밀리는 게 아니라 밀리면서 아래로 떨어지는 것처럼 보인다"고 했다. 원인은 바가 접힐 때 `Scaffold`의 `innerPadding.bottom`이 함께 줄어, 푸시 중에도 계속 컴포지션되는 나가는 화면이 새 패딩으로 다시 배치된다는 것이었다. 목록 끝까지 스크롤한 홈은 뷰포트가 늘며 내용이 아래로 내려앉는다(최근 충전은 홈 맨 아래라 항상 그 상태).

**사용자가 설명한 기준.** 토스·카카오톡은 화면이 추가될 때 하단 바를 따로 내리지 않는다. 새 화면이 오른쪽에서 그 위를 덮고 원래 화면이 (바째로) 왼쪽으로 밀린다. 토스·카카오톡은 시험 기기에 없어 직접 보지 못했고 사용자의 설명과 시스템 설정 앱 녹화로 정했다.

**구현.** 바를 탭 루트 엔트리 안(엔트리 데코레이터)에 그린다. 아래 여백은 엔트리마다 정한다 — 탭 루트는 "내비게이션 인셋 + 바 본체 81dp", 그 밖은 "내비게이션 인셋"이고 엔트리에 내려보내는 `contentPadding`의 아래는 0. 바가 레이아웃에 관여하지 않으니 나가는 화면의 레이아웃은 전환 중 변하지 않는다. 상태 바·내비게이션 바 보호막도 엔트리마다 그려 전환 중 색이 번지지 않게 했다. 탭 사이 이동은 fade through(나가는 화면을 먼저 지움)로 하면 바가 잠깐 사라지므로 `tabSwitch`(`fadeIn togetherWith fadeOut(targetAlpha = 1f)`, 들어오는 화면이 위)로 바꿨다 — 나가는 쪽 `fadeOut(targetAlpha = 1f)`는 투명도를 바꾸지 않고 전환 시간 동안 화면을 붙들어 두는 용도다. 탭 루트 엔트리를 표시하는 값은 `NavEntry` 메타데이터에 넣었고 탭 전환 키도 `:app`이 같은 곳에서 덮어쓴다(feature는 탭 루트인지 모른다).

**기기 확인(S22+, 애니메이터 배율 5배 녹화).** 푸시는 새 화면이 오른쪽에서 들어와 홈을 덮고 홈이 바째로 밀린다(프레임에 왼쪽 아래로 밀려나는 탭이 보임). 뒤로는 위 화면이 오른쪽으로 빠지고 홈이 바와 함께 왼쪽에서 돌아온다. 탭 전환 때 바는 사라지지 않고 선택 표시만 바뀐다(전환 중 두 화면의 글자가 겹쳐 보이는 크로스페이드 잔상은 있다). 확인하지 못한 것: 제스처 뒤로가기의 손가락 추종, 큰 글꼴·가로 화면, 하단 바 높이 상수(81dp)가 기기마다 맞는지(Material3 `NavigationBar` 컨테이너 80dp + 구분선 1dp 가정), 창 크기별 레일 전환(미결정).

## 스낵바 호스트의 의미(semantics) (2026-10-15)

Material3 `SnackbarHost.kt`(S350) 소스: 항목마다 `if (isVisible) { liveRegion = LiveRegionMode.Polite }`, `dismiss { key.dismiss(); true }`, 표시 시간은 `accessibilityManager.calculateRecommendedTimeoutMillis`. 항목은 퇴장 애니메이션이 끝날 때까지 남는다. Semantics 문서(S348)는 alert 성격 컴포넌트에 `liveRegion`(대부분 `Polite`)을 쓰라고 한다. InfocarEV `AppSnackbarHost`는 호스트 `Box`에 의미를 붙였다가 스낵바가 사라진 뒤 빈 노드가 TalkBack에 잡혀 `current != null`일 때만 붙이도록 고쳤다 → R-28-11.


## 2026-10-20 사고 기록 — 전역 푸시 움직임이 모달에 걸려 대각선으로 들어왔다

- **무엇이 있었나**: 푸시 첫 프레임의 컴포지션·레이아웃 비용 때문에 끊긴다는 트레이스(S22+ Perfetto, 2026-10-19)에 따라 푸시를 "새 화면의 첫 배치가 끝난 다음 프레임부터 움직이기"로 바꿨다. `Navigator.navigate`가 모든 푸시에 `PushMotion`(새 화면 오른쪽에서 들어옴 + 이전 화면 1/4 밀림)을 걸고 엔트리 데코레이터가 `graphicsLayer.translationX`로 그렸다.
- **무엇이 깨졌나**: 영구소장(`SubscriptionKey`)·스캐너 연결(`ScannerSearchKey`)은 `NavDisplay.TransitionKey = modalPush()`(아래→위)로 전환을 따로 지정한 모달인데, 그 위에 푸시 움직임이 겹쳐 5시→11시 방향 대각선으로 들어오고 아래 화면까지 왼쪽으로 밀렸다. 사용자가 실기기에서 발견했다. 10배 느린 캡처(`animator_duration_scale`·`transition_animation_scale` 10)의 중간 프레임에서 재현했다.
- **같은 날 앞서 있었던 사고**: 푸시 붙들기를 알파 1→1(`fadeIn(initialAlpha=1) togetherWith fadeOut(targetAlpha=1)`)로 만들었더니 Compose가 값이 안 바뀌는 애니메이션을 돌리지 않아 이전 화면이 첫 프레임에 사라지고 슬라이드 내내 빈 배경이 보였다. 해법은 새 화면 1px 슬라이드 + `KeepUntilTransitionsFinished`였다.
- **원인(공통)**: 전역 메커니즘(`Navigator`의 모든 푸시에 거는 움직임)을 추가하면서 **그 메커니즘을 거치는 예외 경로가 몇 개이고 어떻게 건너뛰는지**를 먼저 나열하지 않았다. 코드에는 이미 "엔트리 metadata로 따로 지정한 전환이 기본보다 우선한다"는 규칙(R-28-02)이 있었지만, 새 움직임은 전환이 아니라 데코레이터 상태로 그려 그 우선순위를 타지 않았다.
- **고침**: 새 화면 엔트리가 `NavDisplay.TransitionKey`를 가지면(`hasOwnTransition(metadata)`) 푸시 움직임을 주지 않고 바로 비운다(이전 화면은 진행도 0이라 밀리지 않는다). 판정은 화면 목록이 아니라 metadata다. 시험은 모달 엔트리가 쓰는 것과 같은 방식으로 만든 metadata로 한다.
- **교훈**: ① 전환 위에 앱이 얹는 모션은 전환 metadata를 보고 비켜야 한다(R-28-01). ② 횡단 변경은 소비자·예외 경로를 grep으로 전수 나열하고 변형마다 느린 배율 캡처로 본다(`checklists/bugfix.md`). ③ 값이 바뀌지 않는 애니메이션은 Compose가 돌리지 않는다.
- 출처 상태: 위는 공식 문서가 다루지 않는 것이라 이 프로젝트의 재현과 수정으로만 뒷받침된다. 엔트리 metadata 전환 계약은 S288이다.

## 출처 조사 기록 (2026-10-20)

- **S358 `androidx.compose.ui.graphics.lerp(Color, Color, Float)`**: androidx `Color.kt` 소스의 KDoc "Interpolation is done in the [ColorSpaces.Oklab] color space"와 구현(두 색을 Oklab으로 변환해 L·a·b와 알파를 **각각** 선형 보간, 결과는 `stop` 색 공간으로 변환, 알파 별도 성분이라 premultiplied 가정 없음)을 읽어 확인했다. 확인하지 못한 것: `animateColorAsState` 의 내부 구현(색 보간에 이 `lerp` 를 쓴다는 문서·소스는 직접 읽지 않았다), 점멸의 크기. 점멸 자체는 이 프로젝트 S22+ 화면 녹화 프레임의 픽셀로만 확인했다(2026-10-19).
- **긴 Column + Crossfade 레이어(R-28-17)**: 프로젝트 관찰값 — `drawLayer [graphicsLayer] 990 x 14940`(기록 탭 본문 항목 하나 안의 Column 약 5,300dp), 프레임 20~50ms, 항목으로 쪼갠 뒤 전환 6회 프레임 430→226개·버벅임 프레임 26~28→13~14개·p99 61→40ms(S22+ 릴리스, 2026-10-19). 공식 문서는 레이어 크기가 효과를 건 컴포저블의 측정 경계라는 것(S325)까지만 말한다.

## 2026-10-20 사고 기록 — 긴 바텀시트가 열린 뒤 윗면이 서서히 올라갔다

- **무엇이 있었나**: 사용자가 대시보드 "배터리 건강" ⓘ 도움말 시트가 "슝 떴다가 화면 전체로 조금씩 올라간다"고 물었다. 시트는 프로토타입 때(2026-10-19) `AppBottomSheet` 래퍼로 만들었고 Material3 1.4.0 기본값을 그대로 썼다.
- **측정(S22+ 화면 녹화 약 66fps, 프레임별 윗면 y·제목 y)**: 열림 300ms tween으로 윗면이 y=84에 닿은 뒤 약 2.2초 동안 y=13까지 올라갔다. 그동안 제목 행(y=191)과 마지막 줄(y=1986)은 프레임 33 이후 1px 안에서 움직이지 않았다 — 내용은 그대로이고 면만 위로 늘어났다. 고친 뒤(`statusBarsPadding()`)는 y=87에서 멈췄다.
- **원인(소스 추론)**: Material3 1.4.0 `ModalBottomSheetContent`가 안쪽 `Column`에 `windowInsetsPadding(contentWindowInsets())`(기본값 `safeDrawing`의 위·아래)를 걸고 바깥 `Surface`에 `consumeWindowInsets(WindowInsets(top = sheetState.offset))`를 건다. 시트가 화면 높이만큼 커지면 위 인셋 패딩이 시트 위치에서 정해지고, 시트 높이는 패딩을 포함하며, `Expanded` 앵커는 `fullHeight - sheetSize.height`다 — 서로 물린 되먹임이라 열린 뒤에도 윗면이 계속 올라간다는 추론이다. 내용 위치가 그대로인 것과 `statusBarsPadding()`으로 사라진 것은 측정했지만 단계별 동작은 추적해 확인하지 않았다(미확인).
- **왜 만들었고 왜 안 걸렸나**: 래퍼를 짤 때 "시트의 최대 높이가 어디서 정해지나"를 보지 않고 라이브러리 기본값을 믿었다. 시트는 별도 창이라 스크린샷 테스트가 내용(`HelpSheetContent`를 고정 높이 1100dp 상자에 담아)만 찍어 시트 자체의 높이 계산은 시험 밖이었다. 열리는 순간을 녹화해 윗면을 재는 절차가 없었다.
- **고침**: `AppBottomSheet`가 `modifier.statusBarsPadding()`을 건다(InfocarEV_Android 워킹트리, 커밋 전). 짧은 시트(단위 선택)는 그대로 아래에 붙는 것을 확인했다. 사용처 13곳 중 긴 시트(도움말)와 짧은 시트(단위)만 기기로 보았다.
- **교훈**: 별도 창 컴포넌트를 감싼 래퍼는 내용뿐 아니라 컨테이너의 높이·위치를 긴 내용으로 열어 보고 열린 뒤 몇 초를 지켜본다(R-28-22, `checklists/bugfix.md`).
- 출처 상태: 공식 시트 문서는 이 상호작용을 적지 않는다. 근거는 Material3 1.4.0 소스(S360)와 이 프로젝트의 측정이다.
