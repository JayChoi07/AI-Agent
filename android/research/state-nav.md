# UI 상태 관리(UDF·UiState·MVI)와 Navigation 3 조사 노트

조사일: 2026-09-09
담당 references 파일 번호: 12(상태 관리), 13(네비게이션)

## 출처

| # | 조직 | 문서명 | URL | 문서 종류 | 최종 갱신 |
|---|---|---|---|---|---|
| S1 | Google | Architecture recommendations (우선순위 라벨 포함) | https://developer.android.com/topic/architecture/recommendations | 공식 가이드 | 확인 못 함 |
| S2 | Google | UI layer | https://developer.android.com/topic/architecture/ui-layer | 공식 가이드 | 2026-07-21 |
| S3 | Google | UI events | https://developer.android.com/topic/architecture/ui-layer/events | 공식 가이드 | 확인 못 함 |
| S4 | Google | State production (state-production) | https://developer.android.com/topic/architecture/ui-layer/state-production | 공식 가이드 | 확인 못 함 |
| S5 | Google | State hoisting (Compose) | https://developer.android.com/develop/ui/compose/state-hoisting | 공식 가이드 | 확인 못 함 |
| S6 | Google | Navigation 3 개요 | https://developer.android.com/guide/navigation/navigation-3 | 공식 가이드 | 2026-09-01 |
| S7 | Google | Nav3 basics | https://developer.android.com/guide/navigation/navigation-3/basics | 공식 가이드 | 확인 못 함 |
| S8 | Google | Nav3 save-state | https://developer.android.com/guide/navigation/navigation-3/save-state | 공식 가이드 | 확인 못 함 |
| S9 | Google | Nav3 NavEntryDecorators | https://developer.android.com/guide/navigation/navigation-3/naventrydecorators | 공식 가이드 | 확인 못 함 |
| S10 | Google | Nav3 modularize | https://developer.android.com/guide/navigation/navigation-3/modularize | 공식 가이드 | 확인 못 함 |
| S11 | Google | Nav3 migration-guide (Nav2→Nav3) | https://developer.android.com/guide/navigation/navigation-3/migration-guide | 공식 가이드 | 확인 못 함 |
| S12 | Google | Nav3 get-started (의존성·버전) | https://developer.android.com/guide/navigation/navigation-3/get-started | 공식 가이드 | 확인 못 함 |
| S13 | Google | Nav3 recipe: Returning a Result (Event-Based) | https://developer.android.com/guide/navigation/navigation-3/recipes/results-event | 공식 가이드 | 확인 못 함 |
| S14 | Google | Nav3 recipe: Returning a Result (State-Based) | https://developer.android.com/guide/navigation/navigation-3/recipes/results-state | 공식 가이드 | 확인 못 함 |
| S15 | Google | androidx.navigation3 릴리스 노트 | https://developer.android.com/jetpack/androidx/releases/navigation3 | 공식 릴리스 노트 | 2026-08-26 |
| S16 | Google | Announcing Jetpack Navigation 3 for Compose | https://android-developers.googleblog.com/2025/05/announcing-jetpack-navigation-3-for-compose.html | 기술 블로그 | 2025-05 |
| S17 | Google(android/nav3-recipes) | deeplink-guide.md | https://github.com/android/nav3-recipes/blob/main/docs/deeplink-guide.md | 오픈소스 문서 | 확인 못 함 |
| S18 | Orbit MVI | 공식 사이트 + README | https://orbit-mvi.org/ , https://raw.githubusercontent.com/orbit-mvi/orbit-mvi/main/README.md | OSS 문서 | 12.x |
| S19 | Orbit MVI | 12.0.0 릴리스 노트 | https://api.github.com/repos/orbit-mvi/orbit-mvi/releases/tags/12.0.0 | 릴리스 노트 | 2026-07-13 |
| S20 | Slack | Circuit 문서(개요·states-and-events·testing) | https://slackhq.github.io/circuit/ , https://slackhq.github.io/circuit/getting-started/states-and-events/ , https://slackhq.github.io/circuit/docs/testing/ | OSS 문서 | 확인 못 함 |
| S21 | Airbnb | Mavericks README | https://raw.githubusercontent.com/airbnb/mavericks/main/README.md | OSS README | 확인 못 함 |
| S22 | Spotify | Mobius README | https://raw.githubusercontent.com/spotify/mobius/master/README.md | OSS README | 확인 못 함 |
| S23 | Bumble/Badoo | MVICore README | https://raw.githubusercontent.com/badoo/MVICore/master/README.md | OSS README | 확인 못 함 |
| S24 | Freeletics | FlowRedux README | https://raw.githubusercontent.com/freeletics/FlowRedux/main/README.md | OSS README | 확인 못 함 |
| S25 | Cash App | Molecule README | https://raw.githubusercontent.com/cashapp/molecule/trunk/README.md | OSS README | 확인 못 함 |
| S26 | Tinder | StateMachine README | https://raw.githubusercontent.com/Tinder/StateMachine/main/README.md | OSS README | 확인 못 함 |
| S27 | GitHub / Maven Central | 릴리스·아티팩트 메타데이터(전 라이브러리 버전·날짜 확정용) | https://api.github.com/repos/{owner}/{repo}/releases , https://repo1.maven.org/maven2/ | 배포 메타데이터 | 2026-09-09 조회 |

## 핵심 규칙 (출처별)

### S1 Google — Architecture recommendations (우선순위 라벨이 붙은 유일한 문서)

- UDF는 "Strongly recommended": "Follow Unidirectional Data Flow (UDF) principles, where ViewModels expose UI state using the observer pattern and receive actions from the UI through method calls." [S1]
- 비즈니스 로직·UI 상태 노출은 AAC ViewModel로. "Strongly recommended" [S1]
- UI 수집은 `collectAsStateWithLifecycle`로. "Strongly recommended" [S1]
- ViewModel→UI 이벤트 전송 금지. "Process the event immediately in the ViewModel and cause a state update with the result of handling the event." "Strongly recommended" [S1]
- 단일 Activity + "Use Navigation 3 to navigate between screens and deep link to your app if your app has more than one screen." "Strongly recommended" [S1]
- `uiState` 단일 프로퍼티 노출은 "Recommended"(강제 아님): "If the UI shows multiple, unrelated pieces of data, the VM can expose multiple UI state properties." [S1]
- 재사용 UI 컴포넌트에 ViewModel 금지, 대신 plain state holder. 둘 다 "Strongly recommended" [S1]
- 스트림 기반이면 `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue)`, 스트림이 없으면 `MutableStateFlow`를 불변 `StateFlow`로 노출해도 됨 [S1]

### S2 Google — UI layer

- 서로 관련된 상태는 하나로: "Use a single UI state object to handle states that are related to each other." 무관한 데이터는 분리 가능 [S2]
- UiState는 불변. "Never modify the UI state in the UI directly unless the UI itself is the sole source of its data." [S2]
- 명명 규칙은 `기능명 + UiState` (`NewsUiState`) [S2]
- UI 로직은 UI에, 비즈니스 로직은 "Never in the UI layer" [S2]
- `PagingData`는 가변이므로 불변 UiState에 넣지 말고 "Expose it from the ViewModel independently in its own stream" [S2]
- ViewModel 내 작업은 main-safe 여야 하고 스레드 전환 책임은 data/domain 레이어 [S2]

### S3 Google — UI events

- Channel/reactive stream으로 ViewModel 이벤트를 내보내면 "When the producer (the ViewModel) outlives the consumer (Compose UI), these solutions don't guarantee the delivery and processing of those events." [S3]
- 규범: "ViewModel events should always result in a UI state update." [S3]
- 사고방식 전환: "don't think about what actions the UI needs to make; think about how those actions affect the UI state." [S3]

### S4 Google — State production

- 출력 API보다 입력 API가 파이프라인 성격을 결정. 스트림이 섞이면 "streams are the defining constraint" → one-shot을 스트림으로 변환 [S4]
- `WhileSubscribed`는 UI가 보일 때만 활성, `Lazily`는 백스택/오프스크린 탭에서 되돌아올 가능성이 있을 때 [S4]
- `stateIn`에는 반드시 `initialValue`. "the UI always has a UI state to display on the screen." [S4]
- 경고: "Don't launch asynchronous operations in the `init` block or constructor of a `ViewModel`." → 멱등 `initialize()` 패턴 권장 [S4]

### S5 Google — State hoisting (Compose)

- "You should hoist UI state to the lowest common ancestor between all the composables that read and write it." [S5]
- Screen UI state(앱 데이터) vs UI element state(가시성·스크롤 등) 구분 [S5]
- UI 로직이 복잡하면 plain state holder, 비즈니스 로직이 걸리면 ViewModel [S5]
- `LazyListState.animateScrollTo()`·`DrawerState.close()` 같은 애니메이션 suspend 함수는 Composition에 스코프된 `CoroutineScope`에서 호출해야 함. `viewModelScope`에서 부르면 예외 [S5]

### S6~S15 Google — Navigation 3

- 핵심 전제: 백스택을 개발자가 소유. `NavDisplay`가 백스택을 관찰해 렌더링 [S6][S16]
- 백스택 원형은 관찰 가능한 리스트(`SnapshotStateList<T>`), push는 `backStack.add(...)`, pop은 `backStack.removeLastOrNull()`, `onBack` 람다로 시스템 백 처리 [S7]
- 키→콘텐츠 해석은 `entryProvider { entry<T> { ... } }` DSL 또는 람다 [S7]. DSL 빌더 타입은 1.0.0-alpha11에서 `EntryProviderBuilder`→`EntryProviderScope`로 개명 [S15]
- 상태 저장: "Every key in the back stack must implement the `NavKey` interface" + "must be marked with the `@Serializable` annotation", 생성은 `rememberNavBackStack(Home)` [S8]
- `NavEntry.key`는 private이 되었고 식별은 `contentKey` 사용(1.0.0-alpha04). 1.1.6부터 `contentKey` 기본값이 `key.toString()`+`key::class.toString()` 합성 [S15]
- 데코레이터 순서 규칙: `rememberSaveableStateHolderNavEntryDecorator()`를 첫 번째로 넣어야 함. ViewModel 스코프는 `rememberViewModelStoreNavEntryDecorator()`(`androidx.lifecycle:lifecycle-viewmodel-navigation3`), 엔트리가 제거될 때 store가 clear됨 [S8][S9]
- `SceneSetupNavEntryDecorator`는 1.0.0-alpha11부터 기본 포함이라 수동 추가 불필요 [S15]
- 커스텀 데코레이터는 `NavEntryDecorator` 상속 후 `decorate`(반드시 `entry.Content()` 호출)·`onPop` 구현 [S9]
- 모듈화: 피처마다 `api`(NavKey) / `impl`(NavEntry·entryProvider) 분리, `impl`→`api` 단방향. 엔트리 빌더는 `EntryProviderScope<NavKey>` 확장 함수로 정의하고 Hilt `@IntoSet` + `@InstallIn(ActivityRetainedComponent::class)`로 수집 [S10]
- 마이그레이션 매핑: `NavHost`→`NavDisplay`, `composable<T>`→`entry<T>`, `dialog<T>`→`entry<T>` + `metadata = DialogSceneStrategy.dialog()`, base route용 `navigation<T>`는 삭제("There is no need for 'base routes'") [S11]
- 마이그레이션 금지 조건: compileSdk < 36, Fragment/View 사용, 문자열 라우트, 그리고 미지원 기능인 "More than one level of nested navigation", "Shared destinations", "Custom destination types", "Deep links" [S11]
- 결과 반환: `rememberResultEventBusNavEntryDecorator()` + `LocalResultEventBus.current`. 이벤트형은 `resultBus.sendResult(...)` / `ResultEffect<T> { }`, 상태형은 `conflateAsState<T?>(null)` [S13][S14]
- 상태형 결과의 한계: "The result state does not survive configuration change or process death." [S14]
- 딥링크 API(`DeepLinkRequest`, `DeepLinkMatcher`, `UriDeepLinkMatcher`, `DeepLinkUri`)는 1.2.0-alpha03(2026-05-19)에 도입 [S15]. 1.1.x에서는 nav3-recipes처럼 `DeepLinkPattern`+`KeyDecoder`를 직접 구현하고 부모 키를 거슬러 합성 백스택을 만든 뒤 `rememberNavBackStack(*syntheticBackStack.toTypedArray())`로 시드 [S17]

### S18~S26 MVI 라이브러리

- Orbit: "streamlines state management within MVVM—think of it as MVVM+" — 순수 MVI가 아니라 MVVM 보강으로 스스로를 규정 [S18]
- Orbit 12.0.0에서 `ContainerHost`/`ContainerHostWithExternalState` 이원 구조를 `OrbitContainerHost<INTERNAL, EXTERNAL, SIDE_EFFECT>` 하나로 통합, `container()`→`orbitContainer()` 개명, 구 이름은 deprecated typealias로 유지 [S19]
- Orbit은 ViewModel 필수 아님: 컨테이너를 "simple Kotlin class"에 둘 수 있어 UI 비의존 컴포넌트·KMP에서 사용 가능 [S18]
- Circuit: "compose from the ground up", `Presenter`와 `Ui`는 "cannot directly access each other" [S20]
- Circuit 상태 규칙: state는 "should be `@Stable`", event는 "should be `@Immutable`", 이벤트는 state 안의 `eventSink: (Event) -> Unit`로 전달 — 근거는 "No risk of dropping events (unlike `Flow`)" [S20]
- Circuit 테스트: "Its core components are not mockable nor do they need to be mocked." `presenterTestOf()`, `Presenter.test()`, `FakeNavigator`, `TestEventSink` 제공. UI가 상태만 받으므로 Paparazzi/Roborazzi 스냅샷에 유리 [S20]
- Mavericks: "the Android framework from Airbnb that we use for nearly all product development at Airbnb", `MavericksState`/`MavericksViewModel`/`invalidate()` 기반 [S21]
- Mobius: "functional reactive framework for managing state evolution and side-effects", Java 7 호환을 목표로 만든 Java-first 설계, `mobius-coroutines`·`mobius-rx3` 확장 제공, "used in production in Spotify Android applications" [S22]
- MVICore: `Feature`/`Reducer`/`Actor`/`NewsPublisher`/`Bootstrapper` 구성, "Time Travel Debugger" 보유, 배포는 JitPack [S23]
- FlowRedux: "Building async. running Kotlin Multiplatform state machine made easy" — `inState`/`on`/`collectWhileInState` DSL, compose 확장 아티팩트 별도 [S24]
- Molecule: "Molecule is not a framework, just a headless compose ui" — `launchMolecule`로 Compose 런타임을 써서 `StateFlow`를 만들고, 테스트는 `moleculeFlow(mode = Immediate)`+Turbine [S25]
- Tinder StateMachine: "A state machine library in Kotlin and Swift" — 아키텍처 프레임워크가 아니라 전이 DSL만 제공 [S26]

## [섹션 1] Navigation 3 확정 API·버전 (2026-09-09 기준)

버전 채널 [S15]:

| 채널 | 버전 | 날짜 |
|---|---|---|
| stable | 1.1.7 | 2026-08-26 |
| beta | 1.2.0-beta01 | 2026-08-26 |
| alpha | 1.2.0-alpha07 | 2026-07-29 |

S12의 `libs.versions.toml`이 명시한 좌표(공식 get-started 기준):

```
androidx.navigation3:navigation3-runtime:1.1.7
androidx.navigation3:navigation3-ui:1.1.7
androidx.lifecycle:lifecycle-viewmodel-navigation3:2.12.0-alpha02
androidx.compose.material3.adaptive:adaptive-navigation3:1.4.0-alpha01
org.jetbrains.kotlinx:kotlinx-serialization-core:1.9.0
plugin org.jetbrains.kotlin.plugin.serialization:2.2.21   // compileSdk 36 이상
```

확정된 API 이름:

| 역할 | API | 근거 |
|---|---|---|
| 목적지 키 마커 | `NavKey` (+ `@Serializable` 필수) | S8 |
| 백스택 | `NavBackStack<T>`(1.0.0-alpha09부터 제네릭·`@Serializable`), 원형은 `SnapshotStateList<T>` | S7, S15 |
| 백스택 생성 | `rememberNavBackStack(vararg elements: NavKey)` | S8, S15 |
| 렌더러 | `NavDisplay(backStack, onBack, entryProvider, entryDecorators, sceneStrategies)` | S7, S9 |
| 키→콘텐츠 | `entryProvider { entry<T> { } }`, 스코프 타입 `EntryProviderScope` | S7, S10, S15 |
| 목적지 단위 | `NavEntry`(식별자는 `contentKey`, `NavEntry.Content()` 공개) | S15, S9 |
| 상태 보존 | `rememberSaveableStateHolderNavEntryDecorator()` — 반드시 첫 번째 | S8, S9 |
| ViewModel 스코프 | `rememberViewModelStoreNavEntryDecorator()` | S8 |
| 커스텀 래핑 | `NavEntryDecorator`(open class) / `rememberDecoratedNavEntries` | S9, S15 |
| 결과 반환 | `rememberResultEventBusNavEntryDecorator()`, `LocalResultEventBus`, `sendResult`, `ResultEffect<T>`, `conflateAsState` — **1.2.0-alpha02 이상** | S13, S14, S15 |
| 딥링크 | `DeepLinkRequest`, `DeepLinkMatcher`, `UriDeepLinkMatcher`, `DeepLinkUri` — **1.2.0-alpha03 이상** | S15 |
| 다이얼로그 | `metadata = DialogSceneStrategy.dialog()` | S11 |

**중요**: 결과 반환과 딥링크의 공식 API는 stable 1.1.7에 없다. 1.1.7만 쓰면 결과 전달은 공유 ViewModel/상위 상태로, 딥링크는 S17처럼 직접 구현해야 한다.

Hilt 연동으로 확인된 것은 모듈화 패턴뿐이다 — `@IntoSet`으로 `EntryProviderScope<NavKey>.() -> Unit`을 모아 `Set<@JvmSuppressWildcards ...>`로 주입 [S10]. `hilt-navigation-compose`의 `hiltViewModel()`을 `entry` 내부에서 쓰는 방식은 이번 조사에서 문서로 확인하지 못했다(아래 미확인 참조).

## [섹션 2] MVI 라이브러리 비교표

버전·날짜·저장소 활동은 2026-09-09에 GitHub Releases API와 Maven Central 메타데이터로 직접 조회한 값이다 [S27].

| 라이브러리 | 최신 릴리스 (날짜) | 저장소 최근 push | 유지보수 상태 | Compose 친화 | 테스트 지원 | 학습 곡선 | 알려진 채택처 |
|---|---|---|---|---|---|---|---|
| Orbit MVI | 12.0.1 (2026-08-28) | 2026-08-31 | 활발. 12.0.0에서 API 통합 리네이밍 | 높음 (`orbit-compose`, `collectAsState`/`collectSideEffect`) | 전용 `orbit-test`(`testWithInternalState`/`testWithExternalState`) | 낮음~중간. MVVM 위 얇은 층 | 명시 없음(KMP 지향) |
| 순수 구현 (ViewModel+StateFlow) | 해당 없음 | 해당 없음 | Google 공식 권장 경로 | 최상 | Turbine + 코루틴 테스트, 의존성 0 | 가장 낮음 | Now in Android 등 Google 샘플 |
| Circuit (Slack) | 0.38.0 (2026-08-26) | 2026-09-09 | 매우 활발. 다만 여전히 0.x | 최상. presenter도 Compose 런타임 | 최상. `presenterTestOf`, `FakeNavigator`, `TestEventSink`, 스냅샷 테스트 | 중간~높음. Presenter/UI/Screen 재구성 필요 | Slack (프로덕션) |
| Mavericks (Airbnb) | v3.1.0 (2026-02-07) | 2026-02-07 | 저활동. 7개월간 커밋 없음 | 중간 (`mavericks-compose` 3.1.0 동시 배포, 그러나 Fragment 우선 설계) | `mavericks-testing` 존재 | 중간. `Async`/`withState` 등 고유 개념 | Airbnb ("nearly all product development") |
| Mobius (Spotify) | v2.1.1 (2024-12-12) | 2026-08-07 | 유지보수 위주. 릴리스 21개월 공백, "Production status" | 낮음. Java-first, Compose 통합 언급 없음 | Update 함수가 순수 함수라 단위 테스트 용이 | 높음. Model/Event/Effect/Next 개념 | Spotify Android |
| MVICore (Bumble/Badoo) | 2.0.0 (2025-04-02) | 2026-09-08 | 저조. JitPack 배포, 신규 채택 비권장 | 낮음. Rx 기반 | Time Travel Debugger 등 도구 | 높음. 컴포넌트 5종 | Bumble/Badoo |
| FlowRedux (Freeletics) | 2.1.1 (2026-06-24) | 2026-09-07 | 활발 | 중간 (compose 확장 아티팩트) | 상태 기계라 전이 테스트 명확 | 중간~높음. DSL 상태 기계 사고 필요 | Freeletics |
| Molecule (Cash App) | 2.2.0 (2025-09-24) | 2026-09-06 | 활발하나 릴리스 간격 김 | 최상. Compose 런타임이 곧 구현체 | `moleculeFlow(Immediate)` + Turbine | 중간. Compose 사고를 presenter에 적용 | Cash App, Circuit 내부 | 
| Tinder StateMachine | 0.3.0 (2021-11-19) | 2024-07-22 | 사실상 정지. 5년간 릴리스 없음 | 없음 | 전이 단위 테스트만 | 낮음(범위가 좁음) | Tinder Scarlet |
| Appyx (Bumble) | 1.7.1 (2025-05-02) | 2026-09-08 | 저조. 릴리스 16개월 공백, open issues 82 | 중간~높음 (Compose 내비게이션) | 자체 테스트 도구 | 높음. 내비게이션까지 대체 | Bumble |

## [섹션 3] MVVM-UDF vs MVI 판단 기준, 그리고 one-shot 이벤트

**MVVM-UDF를 쓰라는 근거 (Google)**: S1은 UDF·AAC ViewModel·`collectAsStateWithLifecycle`을 "Strongly recommended"로 못 박지만, reducer를 요구하지 않는다. 단일 `uiState` 조차 "Recommended" 등급이며 "the VM can expose multiple UI state properties"라는 예외를 명시한다 [S1]. S2는 "관련된 상태끼리" 한 객체로 묶으라고 할 뿐 화면당 하나를 강제하지 않는다 [S2]. 즉 Google 문서 어디에도 Intent/Reducer 계층을 요구하는 문장은 없다.

**MVI 쪽 근거**: Orbit은 스스로를 "MVVM+"로 규정해 MVVM을 대체가 아니라 보강한다고 말한다 [S18]. FlowRedux는 "상태 기계"가 필요한 복잡도, 즉 상태별로 허용 전이가 다르고 진행 중 작업을 상태 이탈 시 취소해야 하는 경우(`collectWhileInState`)를 겨냥한다 [S24]. Circuit은 상태 관리보다 "Presenter와 UI가 서로 접근 불가"라는 분리와 테스트 용이성을 판매 논거로 삼는다 [S20]. Mobius는 "separation of concerns, testability, and isolating stateful parts"를 목적으로 든다 [S22].

정리하면 출처들이 제시하는 분기선은 화면 규모가 아니라 **상태 전이의 합법성 검증이 필요한가**이다. 단순 로딩/성공/실패는 Google 경로로 충분하고, 상태별 허용 이벤트가 다르거나 전이 이력이 감사 대상이면 reducer 계열이 값을 한다.

**one-shot 이벤트**: Google은 가장 강한 표현을 쓴다. Channel/reactive stream은 "the producer (the ViewModel) outlives the consumer" 상황에서 전달을 보장하지 못하며, "ViewModel events should always result in a UI state update" [S3]. S1도 같은 항목을 "Strongly recommended"로 반복한다. 반면 Orbit은 `postSideEffect`로 Toast 같은 "one-time events"를 상태와 분리해 내보내는 것을 1급 기능으로 제공한다 [S18]. Circuit은 방향이 반대라 직접 충돌은 아니다 — `eventSink`는 UI→Presenter 방향이며, 근거로 "No risk of dropping events (unlike `Flow`)"를 들어 오히려 Google과 같은 논리를 쓴다 [S20]. Nav3의 `ResultEventBus`는 Google 자신이 만든 화면 간 one-shot 이벤트 채널이지만, 상태형 사용 시 "does not survive configuration change or process death"라고 한계를 명시한다 [S14].

## 출처 간 충돌

| 주제 | 입장 A (출처) | 입장 B (출처) | 팩 추천과 근거 |
|---|---|---|---|
| ViewModel→UI one-shot 이벤트 | 이벤트를 상태로 모델링, Channel 금지 [S1][S3] | `postSideEffect`로 상태와 분리된 일회성 이벤트 [S18] | A 채택. 전달 보장·프로세스 사망 복원이 우선. B는 화면 이탈 시 유실돼도 무해한 Toast/햅틱에 한해 예외 허용 |
| Nav3 채택 시점 | "currently in alpha", 실험 후 피드백 권장 [S16] | Nav3 사용을 "Strongly recommended"로 등재, 1.1.7 stable [S1][S15] | B 채택. S16은 2025-05 글이라 낡음. 신규 프로젝트 기본값은 Nav3 |
| Nav3 딥링크 지원 여부 | "Deep links"는 미지원, 있으면 마이그레이션 중단 [S11] | 1.2.0-alpha03에 `UriDeepLinkMatcher` 등 도입 [S15], 수동 구현 레시피 존재 [S17] | 버전으로 분기. 1.1.7 고정이면 S17 수동 구현, 딥링크가 핵심이면 1.2.0-beta 채택을 별도 결정 |
| 백스택 조작 추상화 수준 | 백스택 리스트를 직접 `add`/`removeLastOrNull` [S7] | `NavigationState`+`Navigator`(`navigate()`/`goBack()`)를 만들어 사용 [S11] | B 채택. 다중 top-level 백스택이 필요한 순간 A는 화면마다 재구현을 부른다 |
| 화면 상태 단위 | 단일 `uiState` 권장(등급 Recommended) [S1] | 무관한 데이터는 스트림 분리, `PagingData`는 반드시 분리 [S1][S2] | 단일을 기본값, 분리는 근거를 남기고 허용 |
| presenter의 실행 주체 | 화면 상태 홀더는 AAC ViewModel [S1] | Compose 런타임 presenter, ViewModel 불필요 [S20][S25]; Orbit도 ViewModel 비필수 [S18] | A를 기본. Nav3의 `rememberViewModelStoreNavEntryDecorator`가 ViewModel 전제로 설계됨 [S8] |
| 결과 반환 방식 | 상태형: 최신 값만 필요할 때, 구성 변경 미보존 [S14] | 이벤트형: "transient... one-time events"에 적합 [S13] | 화면 간 결과는 공유 상태를 1순위로, `ResultEventBus`는 1.2.0 채택 시에만 |

## 팩 규칙 초안

| 후보 ID | 규칙 한 줄 | 근거 출처 | 확신도 |
|---|---|---|---|
| R-12-01 | 화면 상태는 불변 data class로 정의하고 UI에서 직접 수정하지 않는다 | S2 | 높음 |
| R-12-02 | UiState 타입 이름은 `<기능명>UiState` 형식을 쓴다 | S2 | 높음 |
| R-12-03 | 서로 관련된 상태는 단일 `uiState` 프로퍼티로 노출하고, 분리할 때는 이유를 코드 주석 또는 ADR로 남긴다 | S1, S2 | 높음 |
| R-12-04 | 화면 수준 상태 홀더는 AAC ViewModel을 쓰고, 재사용 UI 컴포넌트에는 ViewModel을 쓰지 않는다 | S1, S5 | 높음 |
| R-12-05 | 재사용 컴포넌트의 UI 로직은 plain state holder 클래스로 분리하고 상태는 호출자에게 hoist한다 | S1, S5 | 높음 |
| R-12-06 | 상태는 `StateFlow`로 노출하고 UI는 `collectAsStateWithLifecycle`로만 수집한다 | S1, S2 | 높음 |
| R-12-07 | 스트림 기반 파이프라인은 `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue)`를 기본값으로 한다 | S1, S4 | 높음 |
| R-12-08 | 백스택/오프스크린 탭에서 되돌아올 화면은 `WhileSubscribed` 대신 `Lazily`를 검토한다 | S4 | 중간 |
| R-12-09 | `stateIn`에는 항상 명시적 `initialValue`(보통 Loading)를 준다 | S4 | 높음 |
| R-12-10 | ViewModel의 `init` 블록·생성자에서 비동기 작업을 시작하지 않고, 필요하면 멱등 `initialize()`를 쓴다 | S4 | 높음 |
| R-12-11 | ViewModel→UI one-shot 이벤트에 `Channel`·`SharedFlow`를 쓰지 않고 UiState 필드 + 소비 확인 콜백으로 모델링한다 | S1, S3 | 높음 |
| R-12-12 | `PagingData`는 UiState에 넣지 않고 별도 스트림으로 노출한다 | S2 | 높음 |
| R-12-13 | 상태를 hoist할 위치는 그 상태를 읽고 쓰는 컴포저블들의 최소 공통 조상으로 한다 | S5 | 높음 |
| R-12-14 | Compose 애니메이션 suspend 함수(`animateScrollTo`, `DrawerState.close`)는 `viewModelScope`가 아니라 Composition 스코프에서 호출한다 | S5 | 높음 |
| R-12-15 | MVI 라이브러리는 기본 도입하지 않는다. 상태별 허용 전이가 달라 검증이 필요한 화면에 한해 도입하고 근거를 남긴다 | S1, S18, S24 | 중간 |
| R-12-16 | 새 프로젝트에서 Mavericks·MVICore·Mobius·Tinder StateMachine은 선택하지 않는다(릴리스 공백 또는 비Compose 설계) | S21~S23, S26, S27 | 중간 |
| R-12-17 | ViewModel은 main-safe를 유지하고 스레드 전환은 data/domain 레이어가 책임진다 | S2 | 높음 |
| R-13-01 | Compose 전용 신규 앱은 단일 Activity + Navigation 3를 기본 내비게이션으로 채택한다 | S1, S6 | 높음 |
| R-13-02 | navigation3 버전은 stable 채널 1.1.7로 고정하고, 1.2.0-beta 채택은 딥링크·결과반환 요구가 확인될 때만 별도 결정한다 | S12, S15 | 중간 |
| R-13-03 | 모든 목적지 키는 `NavKey`를 구현하고 `@Serializable`을 붙인다 | S8 | 높음 |
| R-13-04 | 앱 전용 `sealed interface`로 NavKey 계층을 만들어 백스택을 타입 안전하게 좁힌다 | S8 | 중간 |
| R-13-05 | 백스택은 `rememberNavBackStack`으로 만들어 구성 변경·프로세스 사망을 넘겨 보존한다 | S8 | 높음 |
| R-13-06 | `NavDisplay`의 `entryDecorators` 첫 항목은 `rememberSaveableStateHolderNavEntryDecorator()`로 둔다 | S8, S9 | 높음 |
| R-13-07 | 화면 ViewModel은 `rememberViewModelStoreNavEntryDecorator()`로 NavEntry에 스코프한다 | S8 | 높음 |
| R-13-08 | 키→콘텐츠 매핑은 `entryProvider { entry<T> { } }` DSL로 작성하고 `when` 분기 람다는 쓰지 않는다 | S7 | 중간 |
| R-13-09 | 피처는 NavKey를 담는 `api` 모듈과 NavEntry를 담는 `impl` 모듈로 나누고 `impl`→`api` 단방향 의존만 허용한다 | S10 | 높음 |
| R-13-10 | 피처 엔트리 빌더는 `EntryProviderScope<NavKey>` 확장 함수로 정의하고 Hilt `@IntoSet` 멀티바인딩으로 앱 모듈에서 수집한다 | S10 | 높음 |
| R-13-11 | 엔트리 식별에 `NavEntry.key`를 쓰지 않고 `contentKey`를 쓴다 | S9, S15 | 높음 |
| R-13-12 | 다이얼로그·바텀시트는 별도 화면이 아니라 `entry` + `metadata = DialogSceneStrategy.dialog()`로 표현한다 | S11 | 중간 |
| R-13-13 | 화면 간 결과 전달은 공유 상태를 1순위로 하고, `ResultEventBus`는 1.2.0 채택 시에만 허용한다 | S13, S14, S15 | 중간 |
| R-13-14 | 결과 상태는 프로세스 사망을 견디지 못하므로 복원이 필요한 값은 결과 채널이 아니라 저장소를 경유한다 | S14 | 높음 |
| R-13-15 | 1.1.7에서 딥링크는 `DeepLinkPattern`+`KeyDecoder`로 직접 구현하고, 부모 키를 거슬러 합성 백스택을 만들어 `rememberNavBackStack`에 시드한다 | S17 | 중간 |
| R-13-16 | 다중 top-level 백스택이 필요하면 리스트를 직접 조작하지 말고 `NavigationState`+`Navigator` 래퍼를 만들어 그 API로만 이동한다 | S11 | 중간 |
| R-13-17 | 로깅·인증 게이트처럼 여러 엔트리에 공통 적용할 로직은 `NavEntryDecorator`로 구현하고, 단일 엔트리 의존성은 데코레이터로 넣지 않는다 | S9 | 중간 |
| R-13-18 | Nav3 사용 모듈은 compileSdk 36 이상, Compose 전용이어야 한다(Fragment/View 혼용 금지) | S11, S12 | 높음 |

## 미확인·접근 불가

- `https://medium.com/androiddevelopers/viewmodel-one-off-event-antipatterns-16a1da869b95` — HTTP 403. S3가 이 글을 근거로 링크하지만 본문은 읽지 못했다. R-12-11은 S1·S3만으로 세웠다.
- `https://developer.android.com/guide/navigation/navigation-3/deeplinks` — HTTP 404. 딥링크 문서는 `recipes/deeplinks-basic`·`recipes/deeplinks-advanced` 경로로 옮겨간 것으로 보이며, 이번 조사에서 두 레시피 페이지 본문은 열지 않았다(S15·S17로 대체).
- `https://mavericks.airbnb.tech/` — 리다이렉트 후 본문이 비어 반환됨. Mavericks의 유지보수 상태 공식 선언, Compose 지원 범위, deprecation 여부를 문서로 확인하지 못했다. 비교표의 "저활동" 판정은 릴리스 날짜와 push 날짜(S27)에서 추론한 것이며 공식 입장이 아니다.
- `hilt-navigation-compose`의 `hiltViewModel()`을 Nav3 `entry` 안에서 쓰는 공식 예제를 찾지 못했다. 확인된 Hilt 연동은 S10의 `@IntoSet` 엔트리 빌더 수집뿐이다. R-13-07은 `rememberViewModelStoreNavEntryDecorator` 문서(S8)에만 근거한다.
- Orbit·Circuit·FlowRedux·Molecule의 KMP/Android 최소 지원 버전, Compose 버전 호환 매트릭스는 확인하지 않았다.
- Circuit이 0.x 버전 정책상 API 안정성을 어떻게 약속하는지(semver 적용 여부) 공식 문장을 찾지 못했다. 비교표의 "여전히 0.x" 지적은 버전 번호에서만 나온 관찰이다.
- S1·S3·S4·S5 및 Nav3 하위 페이지 대부분은 페이지 하단 최종 갱신일을 확보하지 못했다(S2 2026-07-21, S6 2026-09-01, S15 2026-08-26만 확인).
