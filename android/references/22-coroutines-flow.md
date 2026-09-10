# 22 코루틴과 Flow

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

이 문서는 suspend 함수·스코프·Flow 자체의 사용법을 다룬다. UiState의 형태와 화면 상태 수집 규칙은 12, 데이터 계층 노출 형태는 15, 에러 타입은 23이 소유한다.

### R-22-01 `GlobalScope`를 쓰지 않는다
- 규칙: `GlobalScope`로 코루틴을 시작하지 않는다. 화면 수명은 `viewModelScope`, 그보다 오래 살아야 하는 작업은 생성자로 주입한 애플리케이션 범위 `CoroutineScope`를 쓴다.
- 근거: Android 코루틴 모범 사례가 `GlobalScope` 회피를 명시한다. 스코프를 하드코딩하면 취소 지점이 사라지고 테스트에서 교체할 수 없다 [S40](https://developer.android.com/kotlin/coroutines/coroutines-best-practices).
- 예시:
  ```kotlin
  // Good
  class SyncRepository @Inject constructor(@ApplicationScope private val scope: CoroutineScope)
  // Bad
  GlobalScope.launch { sync() }
  ```
- 체크: 이 코루틴을 누가 취소하는가. 답이 "아무도"라면 스코프가 잘못됐다.

### R-22-02 `Dispatchers`를 하드코딩하지 않고 주입한다
- 규칙: `Dispatchers.IO`·`Dispatchers.Default`를 함수 안에서 직접 참조하지 않는다. 생성자로 `CoroutineDispatcher`를 받아 `withContext(dispatcher)`로 쓴다.
- 근거: Android 코루틴 모범 사례의 "Don't hardcode `Dispatchers`" — 주입하면 테스트에서 `TestDispatcher`로 바꿔 실행을 제어할 수 있다 [S40](https://developer.android.com/kotlin/coroutines/coroutines-best-practices).
- 예시:
  ```kotlin
  // Good
  class UserRepository @Inject constructor(@IoDispatcher private val io: CoroutineDispatcher) {
      suspend fun load(): User = withContext(io) { dao.get() }
  }
  // Bad
  suspend fun load(): User = withContext(Dispatchers.IO) { dao.get() }
  ```
- 체크: 프로덕션 코드에서 `Dispatchers.` 참조가 DI 모듈 밖에 있는가.

### R-22-03 모든 `suspend` 함수는 main-safe하게 만든다
- 규칙: `suspend` 함수는 메인 스레드에서 호출해도 안전해야 한다. 블로킹 I/O·무거운 계산은 함수 안에서 `withContext`로 옮기고, 호출자에게 "IO에서 불러 달라"고 요구하지 않는다.
- 근거: Android 코루틴 모범 사례가 suspend 함수의 main-safe 원칙을 규정한다 [S40](https://developer.android.com/kotlin/coroutines/coroutines-best-practices).
- 예시:
  ```kotlin
  // Good
  suspend fun parse(file: File): Report = withContext(io) { file.readText().toReport() }
  // Bad: 호출자가 스레드를 책임진다
  suspend fun parse(file: File): Report = file.readText().toReport()
  ```
- 체크: 이 함수를 메인 스레드에서 바로 호출해도 프레임이 밀리지 않는가.

### R-22-04 ViewModel은 코루틴을 만들고 `suspend` 함수를 노출하지 않는다
- 규칙: ViewModel의 공개 함수는 일반 함수로 두고 내부에서 `viewModelScope.launch`로 작업을 시작한다. UI가 코루틴 스코프를 관리하게 만들지 않는다.
- 근거: Android 코루틴 모범 사례가 "ViewModel이 코루틴을 만들고 suspend 함수를 노출하지 않는다"를 명시한다 [S40](https://developer.android.com/kotlin/coroutines/coroutines-best-practices).
- 예시:
  ```kotlin
  // Good
  fun refresh() { viewModelScope.launch { _uiState.update { it.copy(items = repo.load()) } } }
  // Bad
  suspend fun refresh() { _uiState.update { it.copy(items = repo.load()) } }
  ```
- 체크: Composable이나 Activity가 ViewModel의 suspend 함수를 호출하고 있는가.

### R-22-05 가변 스트림 타입을 밖으로 내보내지 않는다
- 규칙: `MutableStateFlow`·`MutableSharedFlow`는 `private`으로 두고 공개는 `StateFlow`·`SharedFlow`로 한다. `asStateFlow()`로 캐스팅 우회를 막는다.
- 근거: 코루틴 모범 사례의 "가변 타입 노출 금지"와 StateFlow 문서의 백킹 프로퍼티 패턴 [S40](https://developer.android.com/kotlin/coroutines/coroutines-best-practices), [S41](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow). 이름 규칙은 R-20-08.
- 예시:
  ```kotlin
  // Good
  private val _uiState = MutableStateFlow(HomeUiState())
  val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
  // Bad
  val uiState = MutableStateFlow(HomeUiState())
  ```
- 체크: 공개 프로퍼티 타입에 `Mutable`이 남아 있는가.

### R-22-06 한 번 끝나는 작업은 `suspend`, 계속 변하는 값은 `Flow`로 노출한다
- 규칙: data·domain 계층에서 결과가 한 번 나오고 끝나는 작업은 `suspend` 함수로, 값이 바뀔 때마다 알려야 하는 것은 `Flow`로 노출한다. 두 형태를 섞지 않는다.
- 근거: Android 코루틴 모범 사례가 계층별 노출 형태(one-shot은 suspend, 변화 알림은 Flow)를 규정한다 [S40](https://developer.android.com/kotlin/coroutines/coroutines-best-practices), [S42](https://developer.android.com/kotlin/flow).
- 예시:
  ```kotlin
  // Good
  suspend fun submitOrder(order: Order): OrderResult
  fun observeCart(): Flow<Cart>
  // Bad: 한 번 쓰는 결과를 Flow로 감싸 구독을 강요한다
  fun submitOrder(order: Order): Flow<OrderResult>
  ```
- 체크: 이 Flow가 값을 두 번 이상 내보내는가. 아니면 suspend가 맞다.

### R-22-07 cold Flow를 hot으로 바꿀 때는 `stateIn`/`shareIn` + `WhileSubscribed()`를 쓴다
- 규칙: 여러 구독자가 공유해야 하는 스트림은 `stateIn`(현재 값 필요) 또는 `shareIn`(값 보관 불필요)으로 변환하고 시작 정책은 `SharingStarted.WhileSubscribed()`로 둔다. 구독자가 없을 때 업스트림을 계속 돌리지 않는다.
- 근거: StateFlow/SharedFlow 문서가 cold→hot 변환과 `WhileSubscribed()` 사용을 제시한다 [S41](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow).
- 예시:
  ```kotlin
  // Good
  val cart: StateFlow<Cart> = repo.observeCart()
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Cart.Empty)
  // Bad: 구독자가 없어도 도는 SharingStarted.Eagerly
  ```
- 체크: 화면을 벗어난 뒤에도 업스트림이 계속 도는가.

### R-22-08 UI에서는 수명주기를 아는 방법으로만 Flow를 수집한다
- 규칙: Compose에서는 `collectAsStateWithLifecycle()`, View에서는 `repeatOnLifecycle(STARTED)`/`flowWithLifecycle`로 수집한다. `launch`나 `launchIn`으로 직접 수집하지 않는다. 화면 상태 수집의 세부 규칙은 12가 소유한다.
- 근거: StateFlow/SharedFlow 문서가 "Never collect a flow from the UI directly from `launch` or the `launchIn`"이라며 `repeatOnLifecycle(STARTED)`를 지시한다 [S41](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow), [S42](https://developer.android.com/kotlin/flow).
- 예시:
  ```kotlin
  // Good
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  // Bad
  LaunchedEffect(Unit) { viewModel.uiState.collect { render(it) } }
  ```
- 체크: 백그라운드로 보낸 화면이 계속 수집하고 있지 않은가.

### R-22-09 오래 도는 작업은 `ensureActive()`로 취소에 협조한다
- 규칙: 반복문·긴 계산 안에서 주기적으로 `ensureActive()`(또는 `isActive` 확인)를 호출해 취소 요청에 빠져나온다. 취소는 협조적이므로 확인하지 않으면 멈추지 않는다.
- 근거: Android 코루틴 모범 사례가 취소의 협조적 성격과 `ensureActive()` 사용을 명시한다 [S40](https://developer.android.com/kotlin/coroutines/coroutines-best-practices).
- 예시:
  ```kotlin
  // Good
  for (chunk in chunks) {
      ensureActive()
      process(chunk)
  }
  ```
- 체크: 이 루프가 도는 중에 화면을 닫으면 즉시 멈추는가.

### R-22-10 `CancellationException`은 잡지 말고 다시 던진다
- 규칙: `catch (e: Exception)`처럼 넓게 잡지 말고 처리할 수 있는 구체 예외만 잡는다. 넓게 잡아야 한다면 `CancellationException`을 먼저 다시 던져 취소가 삼켜지지 않게 한다.
- 근거: 코루틴 예외 문서는 `CancellationException`이 취소 신호이며 핸들러가 무시한다고 설명하고 [S64](https://kotlinlang.org/docs/exception-handling.html), 모범 사례는 구체 예외 처리를 권한다 [S40](https://developer.android.com/kotlin/coroutines/coroutines-best-practices). `catch` 블록 순서는 R-23-06.
- 예시:
  ```kotlin
  // Good
  try { api.sync() } catch (e: IOException) { emit(SyncState.Offline) }
  // Bad
  try { api.sync() } catch (e: Exception) { emit(SyncState.Failed) }
  ```
- 체크: 넓은 `catch` 위에 `CancellationException` 재던지기가 있는가.

### R-22-11 `flow {}` 안에서 컨텍스트를 바꾸지 않고 `flowOn`을 쓴다
- 규칙: `flow {}` 빌더 안에서 `withContext`로 감싸 `emit`하지 않는다. 업스트림 실행 컨텍스트는 `flowOn(dispatcher)`으로 지정한다.
- 근거: Kotlin flows on Android 문서가 다른 컨텍스트에서의 `emit` 금지와 `flowOn` 사용을 명시한다 [S42](https://developer.android.com/kotlin/flow).
- 예시:
  ```kotlin
  // Good
  fun observeReports(): Flow<Report> = flow { emit(parse(file)) }.flowOn(io)
  // Bad
  fun observeReports(): Flow<Report> = flow { withContext(io) { emit(parse(file)) } }
  ```
- 체크: `flow {}` 본문 안에 `withContext`가 있는가.

### R-22-12 Flow 예외는 `catch` 연산자로 업스트림에서 처리한다
- 규칙: 업스트림에서 나는 예외는 `catch` 연산자로 잡아 도메인 값(에러 상태)으로 바꿔 흘려보낸다. 수집부에 `try/catch`를 두르는 방식을 기본으로 삼지 않는다.
- 근거: Kotlin flows on Android 문서가 예외 처리에 `catch` 연산자를 제시한다 [S42](https://developer.android.com/kotlin/flow). 데이터 계층 경계에서 도메인 타입으로 변환하는 규칙은 R-23-05.
- 예시:
  ```kotlin
  // Good
  repo.observeCart()
      .map { CartUiState.Loaded(it) }
      .catch { emit(CartUiState.Failed(NetworkError)) }
  // Bad: 수집부에서 try/catch로 전부 감싼다
  ```
- 체크: `catch`가 변환 연산자 뒤, 수집 직전에 놓여 있는가.

### R-22-13 콜백 API는 `callbackFlow` + `awaitClose`로 감싼다
- 규칙: 리스너·브로드캐스트 같은 콜백 소스는 `callbackFlow`로 감싸고 `trySend`로 값을 보내며 `awaitClose { }`에서 반드시 해제한다.
- 근거: Kotlin flows on Android 문서가 콜백 기반 소스의 변환 방법으로 `callbackFlow`와 `awaitClose`를 제시한다 [S42](https://developer.android.com/kotlin/flow).
- 예시:
  ```kotlin
  // Good
  fun observeGps(): Flow<Location> = callbackFlow {
      val cb = LocationCallback { trySend(it) }
      manager.register(cb)
      awaitClose { manager.unregister(cb) }
  }
  ```
- 체크: `awaitClose`에서 리스너를 해제하는가. 없으면 누수다.

### R-22-14 병렬 작업은 스코프로 감싸고 `async` 결과는 반드시 `await`한다
- 규칙: 한 함수 안에서 여러 작업을 동시에 돌릴 때는 `coroutineScope`(하나 실패 시 전체 취소) 또는 `supervisorScope`(형제 격리)로 감싼다. `async`로 만든 `Deferred`는 같은 스코프 안에서 `await`한다.
- 근거: 모범 사례가 병렬 분해에 `coroutineScope`/`supervisorScope`를 제시하고 [S40](https://developer.android.com/kotlin/coroutines/coroutines-best-practices), 코루틴 예외 문서는 `async` 예외가 `await` 시점에 드러나며 `SupervisorJob`은 자식 실패를 전파하지 않는다고 설명한다 [S64](https://kotlinlang.org/docs/exception-handling.html).
- 예시:
  ```kotlin
  // Good
  suspend fun loadHome(): Home = coroutineScope {
      val banners = async { repo.banners() }
      val items = async { repo.items() }
      Home(banners.await(), items.await())
  }
  ```
- 체크: 만든 `Deferred`를 전부 `await`하는가. 형제 실패를 격리해야 하는 자리인가.

### R-22-15 `CoroutineExceptionHandler`는 루트 코루틴에만 설치한다
- 규칙: 예외 핸들러는 스코프의 루트 코루틴(또는 스코프 컨텍스트)에 단다. 자식 `launch`에 달아 봐야 호출되지 않는다.
- 근거: 코루틴 예외 문서가 자식은 예외를 부모에 위임하므로 자식 컨텍스트의 핸들러는 "never used"라고 명시한다 [S64](https://kotlinlang.org/docs/exception-handling.html).
- 예시:
  ```kotlin
  // Good
  val scope = CoroutineScope(SupervisorJob() + handler)
  // Bad
  viewModelScope.launch(handler) { child() }
  ```
- 체크: 핸들러가 달린 코루틴이 정말 루트인가.

### R-22-16 `runBlocking`은 테스트에서만 쓴다
- 규칙: 프로덕션 코드에서 `runBlocking`으로 스레드를 막지 않는다. 필요한 것은 `suspend` + `withContext`다. 테스트는 주입한 `TestDispatcher`와 `runTest`로 실행을 제어한다.
- 근거: 모범 사례가 suspend 함수의 main-safe 원칙과 테스트에서의 `TestDispatcher` 주입을 규정한다 [S40](https://developer.android.com/kotlin/coroutines/coroutines-best-practices). 스레드를 막는 호출은 이 원칙과 정면으로 어긋난다.
- 예시:
  ```kotlin
  // Good
  @Test fun loads() = runTest { assertEquals(expected, repo.load()) }
  // Bad
  fun load(): User = runBlocking { repo.load() }
  ```
- 체크: `src/main`에 `runBlocking` 호출이 있는가.
