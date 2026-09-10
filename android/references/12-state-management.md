# 12 상태 관리

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

상태 홀더의 형태와 UiState 문장은 이 문서가 소유한다. 계층 의존 방향은 11, 컴포저블 호이스팅 위치는 17, 에러 타입 설계(ERROR_TYPE)는 23을 따른다. `AndroidViewModel` 금지는 R-11-08이 소유하므로 여기서 다시 쓰지 않는다.

### R-12-01 화면 상태는 `<기능명>UiState` 불변 data class 하나로 정의한다
- 규칙: 서로 관련된 상태는 기본값을 가진 불변 `data class` 하나로 묶어 노출하고, UI는 그 객체를 읽기만 한다. 무관한 데이터를 별도 프로퍼티로 분리할 때는 이유를 KDoc이나 ADR에 남긴다.
- 근거: 관련 상태는 단일 UI state 객체로 묶고 UI가 직접 수정하지 않는다, 이름은 `NewsUiState` 형식 [S08](https://developer.android.com/topic/architecture/ui-layer). 단일 `uiState` 노출은 Recommended 등급이라 분리가 금지는 아니다 [S02](https://developer.android.com/topic/architecture/recommendations)
- 예시:
  ```kotlin
  // Good
  data class LoginUiState(
      val email: String = "",
      val isSubmitting: Boolean = false,
      val error: LoginError? = null,
  )
  // Bad: 가변 필드 + UI가 직접 수정
  class LoginUiState { var email: String = "" }
  ```
- 체크: UiState에 `var`·가변 컬렉션이 없는가. 프로퍼티를 분리해 노출했다면 이유가 적혀 있는가.

### R-12-02 상태 아키텍처는 결정 매트릭스로 고른다. 기본값은 MVVM-UDF
- 규칙: 아래 다섯 기준 중 MVI 열에 2개 이상 해당하면 MVI 골격(R-12-11)을 쓰고, 아니면 MVVM-UDF를 쓴다. 판정 결과와 해당 개수를 화면 KDoc 또는 ADR에 남긴다.

  | 판단 기준 | MVVM-UDF | MVI | 기본값 |
  |---|---|---|---|
  | 상태별로 허용되는 이벤트가 달라 잘못된 전이를 막아야 한다 | 아니오 | 예 | MVVM-UDF |
  | 상태를 벗어날 때 진행 중이던 작업을 취소해야 한다 | 아니오 | 예 | |
  | 상태 전이 이력을 재현·감사해야 한다 | 아니오 | 예 | |
  | 상태 변경 로직을 코루틴 없이 순수 함수로 단위 테스트해야 한다 | 아니오 | 예 | |
  | 부수효과(네비·다이얼로그·분석)를 상태와 분리해 1급으로 다뤄야 한다 | 아니오 | 예 | |
- 근거: Google은 UDF·AAC ViewModel을 Strongly recommended로 두면서 Intent/Reducer 계층을 요구하는 문장을 두지 않는다 [S02](https://developer.android.com/topic/architecture/recommendations), [S09](https://developer.android.com/topic/architecture/ui-layer/events). 매트릭스 행은 MVI 계열이 실제로 내세우는 도입 사유에서 가져왔다 — 상태별 허용 전이와 이탈 시 취소 [S97](https://raw.githubusercontent.com/freeletics/FlowRedux/main/README.md), 전이 이력 재현 [S96](https://raw.githubusercontent.com/badoo/MVICore/master/README.md), 순수 update 함수의 테스트 용이성 [S95](https://raw.githubusercontent.com/spotify/mobius/master/README.md), 부수효과 1급 취급 [S91](https://orbit-mvi.org/). 출발 표의 "필드 수 5개", "이벤트 4종", "부수효과 2종" 같은 규모 수치는 근거 출처가 없어(90-sources 보류 표 "MVI 도입 판단선") 삭제하고 도입 사유 기준으로 바꿨다. 남긴 "2개 이상"이라는 개수 기준 자체는 출처가 아니라 팩의 운영 판단이며, 판정 근거를 표준 준수 보고에 남겨 사후 조정한다
- 예시:
  ```kotlin
  // 로그인 화면: 해당 0개 → MVVM-UDF
  // 결제 화면: 단계별 허용 이벤트 다름 + 단계 이탈 시 승인 요청 취소 + 전이 감사 로그 → 해당 3개 → MVI
  ```
- 체크: 산출물 "표준 준수 보고"에 매트릭스 판정 행이 있는가. MVI를 골랐다면 해당한 기준이 2개 이상 적혀 있는가.

### R-12-03 ViewModel에서 UI로 일회성 이벤트를 push하지 않는다
- 규칙: 네비게이션·다이얼로그·스낵바 같은 일회성 결과는 UiState의 필드로 모델링하고, UI가 처리한 뒤 소비 콜백으로 그 필드를 되돌린다. `Channel`·`SharedFlow`로 UI에 이벤트를 흘려보내지 않는다. 예외는 두 가지뿐이다 — MVI를 채택한 화면의 `Channel<SideEffect>`(R-12-11), 그리고 유실돼도 무해한 Toast·햅틱.
- 근거: 생산자(ViewModel)가 소비자(Compose UI)보다 오래 살면 Channel·reactive stream은 전달과 처리를 보장하지 못하며, "ViewModel events should always result in a UI state update" [S09](https://developer.android.com/topic/architecture/ui-layer/events). 같은 항목이 Strongly recommended로 반복된다 [S02](https://developer.android.com/topic/architecture/recommendations). `SharedFlow`는 구독자가 없을 때의 전달을 보장하지 않는다 [S41](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
- 예시:
  ```kotlin
  // Good
  data class LoginUiState(val navigateToHome: Boolean = false)
  fun onNavigatedToHome() { _uiState.update { it.copy(navigateToHome = false) } }
  // Bad
  private val _events = Channel<LoginEvent>()
  val events = _events.receiveAsFlow()
  ```
- 체크: ViewModel에 UI로 나가는 `Channel`·`SharedFlow`가 있는가. 있다면 MVI 판정(R-12-02) 근거가 함께 있는가.

### R-12-04 화면 단위 상태 홀더는 ViewModel, 재사용 컴포넌트는 plain state holder로 한다
- 규칙: 화면(내비게이션 목적지) 하나에 ViewModel 하나를 두고, 여러 화면에서 재사용되는 UI 컴포넌트에는 ViewModel을 붙이지 않는다. 재사용 컴포넌트의 UI 로직은 plain state holder 클래스로 빼고 상태는 호출자에게 hoist한다.
- 근거: 재사용 컴포넌트에 ViewModel을 쓰지 말고 plain state holder를 쓰라는 항목이 Strongly recommended다 [S02](https://developer.android.com/topic/architecture/recommendations). UI 로직만 복잡하면 plain state holder, 비즈니스 로직이 걸리면 ViewModel [S12](https://developer.android.com/develop/ui/compose/state-hoisting)
- 예시:
  ```kotlin
  // Good: 재사용 컴포넌트는 remember로 만든 plain holder
  class SearchBarState(initialQuery: String) { var query by mutableStateOf(initialQuery) }
  // Bad: 재사용 컴포넌트가 ViewModel을 직접 얻는다
  @Composable fun SearchBar(vm: SearchViewModel)
  ```
- 체크: ViewModel을 파라미터로 받는 컴포저블이 Route(R-13-06) 외에 있는가.

### R-12-05 상태는 private MutableStateFlow를 `asStateFlow()`로 노출하고 UI는 라이프사이클 인지 API로 수집한다
- 규칙: ViewModel은 `private val _uiState = MutableStateFlow(...)`와 `val uiState: StateFlow<T> = _uiState.asStateFlow()` 쌍으로 상태를 노출한다. UI는 `collectAsStateWithLifecycle()`로만 수집하고 `collectAsState()`를 쓰지 않는다.
- 근거: 스트림이 없으면 `MutableStateFlow`를 불변 `StateFlow`로 노출해도 되고, UI 수집을 `collectAsStateWithLifecycle`로 하는 것은 Strongly recommended다 [S02](https://developer.android.com/topic/architecture/recommendations). 상태 홀더는 UI가 직접 수정할 수 없어야 한다 [S08](https://developer.android.com/topic/architecture/ui-layer), [S41](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
- 예시:
  ```kotlin
  // Good
  private val _uiState = MutableStateFlow(LoginUiState())
  val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
  // Bad: 가변 타입을 그대로 공개
  val uiState = MutableStateFlow(LoginUiState())
  ```
- 체크: 공개 프로퍼티 타입이 `MutableStateFlow`인 곳이 있는가. 컴포저블이 `collectAsState()`를 쓰는가.

### R-12-06 스트림 기반 상태는 `stateIn(viewModelScope, WhileSubscribed(5_000), initialValue)`를 기본값으로 한다
- 규칙: 데이터 계층 Flow를 상태로 바꿀 때는 `stateIn`을 쓰고 `initialValue`를 반드시 명시한다. 백스택이나 오프스크린 탭에서 되돌아올 가능성이 큰 화면만 `SharingStarted.Lazily`를 검토하고 이유를 남긴다.
- 근거: 스트림 기반 파이프라인의 권장 형태가 `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue)`다 [S02](https://developer.android.com/topic/architecture/recommendations). `WhileSubscribed`는 UI가 보일 때만 활성이고 `Lazily`는 되돌아올 가능성이 있을 때이며, `initialValue`가 있어야 "the UI always has a UI state to display" [S10](https://developer.android.com/topic/architecture/ui-layer/state-production)
- 예시:
  ```kotlin
  // Good
  val uiState = repository.newsStream()
      .map(::toUiState)
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewsUiState(isLoading = true))
  // Bad: initialValue 없이 Eagerly
  val uiState = repository.newsStream().stateIn(viewModelScope, SharingStarted.Eagerly, null)
  ```
- 체크: `stateIn` 호출에 `initialValue`가 있는가. `Lazily`·`Eagerly`를 썼다면 이유가 적혀 있는가.

### R-12-07 ViewModel의 `init` 블록과 생성자에서 비동기 작업을 시작하지 않는다
- 규칙: 생성 시점에 데이터를 불러야 하면 `init`에서 `launch`하지 말고, UI가 부르는 멱등 `initialize()`를 두거나 상태 스트림으로 전환한다.
- 근거: "Don't launch asynchronous operations in the `init` block or constructor of a `ViewModel`" — 테스트가 어려워지고 시작 시점을 UI가 통제할 수 없다. 대안으로 멱등 `initialize()` 패턴이 제시된다 [S10](https://developer.android.com/topic/architecture/ui-layer/state-production)
- 예시:
  ```kotlin
  // Good
  private var initialized = false
  fun initialize() { if (initialized) return; initialized = true; viewModelScope.launch { load() } }
  // Bad
  init { viewModelScope.launch { load() } }
  ```
- 체크: `init` 블록에 `viewModelScope.launch`나 suspend 호출이 있는가.

### R-12-08 로딩과 에러는 UiState의 명시적 필드로 표현한다
- 규칙: 로딩·에러를 별도 스트림이나 이벤트로 빼지 않고 UiState 안에서 표현한다. UiState는 R-12-01대로 data class 하나이므로 `isLoading: Boolean`과 `error: <도메인 에러>?`(ERROR_TYPE, 23이 소유) 필드로 표현하고 sealed 계층으로 쪼개지 않는다. 초기값은 `isLoading = false`이고 로드를 시작하는 `initialize()`가 `true`로 바꾼다(R-12-07).
- 근거: `stateIn`의 `initialValue`가 있어야 UI가 언제나 그릴 상태를 갖는다 [S10](https://developer.android.com/topic/architecture/ui-layer/state-production). 화면이 필요로 하는 상태는 하나의 UI state 객체가 담는다 [S08](https://developer.android.com/topic/architecture/ui-layer)
- 예시:
  ```kotlin
  // Good
  data class NewsUiState(
      val isLoading: Boolean = false,
      val items: List<News> = emptyList(),
      val error: NewsError? = null,
  )
  // Bad: 로딩만 따로 흘려보낸다
  val isLoading: SharedFlow<Boolean>
  // Bad: 상태를 sealed 계층으로 쪼개 R-12-01의 단일 data class 규칙과 Konsist 검사를 벗어난다
  sealed interface NewsUiState { data object Loading : NewsUiState /* ... */ }
  ```
- 체크: 로딩·에러가 UiState 밖의 별도 스트림으로 나가 있는가.

### R-12-09 `PagingData`는 UiState에 넣지 않고 별도 스트림으로 노출한다
- 규칙: `PagingData`는 가변이므로 불변 UiState의 필드로 담지 않고 ViewModel이 자기 스트림으로 따로 내보낸다.
- 근거: "`PagingData` ... Expose it from the ViewModel independently in its own stream" [S08](https://developer.android.com/topic/architecture/ui-layer)
- 예시:
  ```kotlin
  // Good
  val uiState: StateFlow<FeedUiState>
  val pagedItems: Flow<PagingData<Post>> = pager.flow.cachedIn(viewModelScope)
  // Bad
  data class FeedUiState(val pagedItems: PagingData<Post>)
  ```
- 체크: UiState 프로퍼티에 `PagingData`가 있는가.

### R-12-10 ViewModel과 UiState의 시그니처에 Android 프레임워크 타입을 두지 않는다
- 규칙: `Context`·`Resources`·`View`·`Intent`·`Uri`·`Bundle` 같은 프레임워크 타입을 ViewModel 생성자 파라미터, 공개 함수 시그니처, UiState 필드에 두지 않는다. 문구는 리소스 ID나 도메인 타입으로 넘기고 포맷팅은 컴포저블에서 한다.
- 근거: Android 프레임워크 의존을 줄이고 앱 데이터를 app component에 저장하지 않는다 [S01](https://developer.android.com/topic/architecture). `AndroidViewModel` 사용 금지가 Recommended로 등재돼 있다 [S02](https://developer.android.com/topic/architecture/recommendations) — 금지 문장 자체는 R-11-07이 소유하고 이 규칙은 시그니처 범위만 다룬다
- 예시:
  ```kotlin
  // Good
  data class LoginUiState(@StringRes val errorMessage: Int? = null)
  // Bad
  class LoginViewModel(private val context: Context)
  ```
- 체크: ViewModel 생성자와 UiState 필드에 `android.*` 타입이 있는가.

### R-12-11 MVI를 채택한 화면은 라이브러리 없이 sealed Intent + private reduce + Channel SideEffect로 구성한다
- 규칙: R-12-02가 MVI를 지시한 화면에만, 같은 ViewModel + `StateFlow` 골격 안에 `sealed interface Intent`, private `reduce(state, intent): UiState`, `Channel<SideEffect>`를 더한다. 세 요소는 그 화면의 ViewModel 파일에 함께 둔다. Orbit·Circuit·Mavericks 등 MVI 라이브러리는 도입하지 않는다.
- 근거: MVI_IMPL 확정값이 순수 구현이다 [S02](https://developer.android.com/topic/architecture/recommendations), [S09](https://developer.android.com/topic/architecture/ui-layer/events). Orbit은 스스로를 대체가 아닌 "MVVM+"로 규정하고 [S91](https://orbit-mvi.org/), Circuit은 Presenter/UI/Screen 재구성을 요구하며 [S93](https://slackhq.github.io/circuit/), Molecule은 "not a framework"라고 밝힌다 [S98](https://raw.githubusercontent.com/cashapp/molecule/trunk/README.md) — 어느 쪽도 골격을 바꿀 만큼의 이득을 문서로 제시하지 않는다
- 예시:
  ```kotlin
  // Good: 같은 ViewModel 안에 세 요소를 둔다
  sealed interface CheckoutIntent { data object Submit : CheckoutIntent }
  private fun reduce(state: CheckoutUiState, intent: CheckoutIntent): CheckoutUiState = ...
  private val sideEffects = Channel<CheckoutSideEffect>(Channel.BUFFERED)
  // Bad: MVI 라이브러리 의존을 추가한다
  class CheckoutViewModel : ContainerHost<CheckoutState, CheckoutSideEffect>
  ```
- 체크: MVI 화면의 `reduce`가 private 순수 함수인가. 빌드 파일에 MVI 라이브러리 의존이 추가됐는가.

### R-12-12 상태 홀더는 main-safe를 유지하고 스레드 전환은 data·domain 계층이 책임진다
- 규칙: ViewModel과 plain state holder는 디스패처를 바꾸지 않고 호출만 한다. `withContext(Dispatchers.IO)`는 repository·data source 쪽에 둔다.
- 근거: ViewModel 내 작업은 main-safe여야 하며 스레드 전환 책임은 data·domain 계층에 있다 [S08](https://developer.android.com/topic/architecture/ui-layer). 같은 원칙의 data 계층 적용은 R-15-05, suspend 함수 일반 규칙은 R-22-02가 소유한다
- 예시:
  ```kotlin
  // Good
  viewModelScope.launch { _uiState.update { it.copy(items = repository.load()) } }
  // Bad
  viewModelScope.launch(Dispatchers.IO) { ... }
  ```
- 체크: ViewModel에 `Dispatchers.IO`·`Dispatchers.Default` 지정이 있는가.
