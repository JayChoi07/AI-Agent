# 15 데이터 계층

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

## 결정 매트릭스

오프라인 우선 여부 (R-15-07)

| 판단 기준 | 선택지 A: 오프라인 우선(로컬 SSOT) | 선택지 B: 네트워크 직결(로컬 저장 없음) | 기본값 |
|---|---|---|---|
| 같은 데이터를 화면 재진입마다 다시 읽는가 | A | B | A |
| 네트워크가 없을 때도 읽기가 의미 있는가 | A | B | A |
| 호출 시점마다 서버 값이 달라지는 일회성 조회인가(결제 승인·인증 코드) | B | B | B |
| 로컬 스키마와 마이그레이션 비용을 감당하는가 | A | B | A |

저장소 선택 (R-15-08)

| 판단 기준 | 선택지 A: Room | 선택지 B: DataStore | 기본값 |
|---|---|---|---|
| 질의·정렬·조인이 필요한 대량 레코드인가 | A | B | A |
| 소량 key-value(설정·플래그·토큰)인가 | A | B | B |
| 이미지·로그처럼 큰 바이너리인가 | 둘 다 아님 → File | 둘 다 아님 → File | File |

## 규칙

### R-15-01 데이터 계층의 유일한 진입점은 repository이고 `model` 패키지 타입만 노출한다
- 규칙: 다른 계층은 DataSource·DAO·API 서비스에 직접 접근하지 않고 `[DataType]Repository`만 호출한다. repository는 그 데이터 타입의 단일 진실 공급원이며, 공개 시그니처에는 `model` 패키지(멀티모듈에서는 `:core:model`)의 모델·결과 타입만 쓴다. DTO·Entity·`Cursor` 같은 소스 전용 타입을 노출하지 않는다. 데이터 계층은 ui·domain 패키지를 import하지 않는다(R-11-01).
- 근거: "The entry points to the data layer are always the repository classes" [S06](https://developer.android.com/topic/architecture/data-layer), repository가 앱 데이터 접근의 유일한 경로이고 모델은 별도 모듈에 두는 구성 [S51](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ArchitectureLearningJourney.md), 계층별 모델 분리 권고 [S02](https://developer.android.com/topic/architecture/recommendations)
- 예시:
  ```kotlin
  // Good
  interface NewsRepository { fun getNewsStream(): Flow<List<News>> }   // News = model 패키지 모델
  // Bad: DTO 노출 + DataSource 직접 주입
  class NewsViewModel @Inject constructor(private val api: NewsApi) { /* ... */ }
  interface NewsRepository { suspend fun load(): List<NewsDto> }
  ```
- 체크: repository 공개 시그니처에 `*Dto`·`*Entity`가 있는가. ViewModel·UseCase가 DataSource나 DAO를 주입받는가.

### R-15-02 repository와 DataSource 이름은 데이터 타입 + 역할로 짓는다
- 규칙: repository는 `[DataType]Repository`, 데이터 소스는 `[DataType][Remote|Local]DataSource`로 명명한다. 구현 기술(Retrofit·Room)을 이름에 넣지 않는다.
- 근거: 데이터 계층 네이밍 규약 [S06](https://developer.android.com/topic/architecture/data-layer), 기본 구현 `Default` 접두 등 네이밍 권고 [S02](https://developer.android.com/topic/architecture/recommendations)
- 예시:
  ```kotlin
  // Good
  class NewsRemoteDataSource @Inject constructor(private val api: NewsApi)
  class NewsLocalDataSource @Inject constructor(private val dao: NewsDao)
  // Bad
  class NewsRetrofitHelper
  class NewsRoomManager
  ```
- 체크: 이름이 소스 종류(Remote/Local)를 드러내는가. 구현 라이브러리 이름이 클래스명에 들어갔는가.

### R-15-03 DataSource는 Remote·Local로 나누고 하나는 하나의 소스만 담당한다
- 규칙: 원격 조회와 로컬 저장을 한 클래스에 섞지 않는다. 캐시 조회 후 네트워크로 넘어가는 판단은 DataSource가 아니라 repository가 한다. 데이터 소스 하나는 네트워크·DB·파일 중 하나만 다룬다.
- 근거: 데이터 소스는 하나의 소스만 담당한다는 규정 [S06](https://developer.android.com/topic/architecture/data-layer), 데이터 소스 은닉을 data 모듈 책임으로 두는 패턴 [S04](https://developer.android.com/topic/modularization/patterns)
- 예시:
  ```kotlin
  // Good: 선택은 repository가 한다
  override fun getNewsStream(): Flow<List<News>> = local.observeAll().map { it.toDomain() }
  override suspend fun refresh(): RefreshResult { /* remote → local 저장 */ }
  // Bad: 한 DataSource가 캐시 유무를 보고 네트워크로 폴백한다
  class NewsDataSource { suspend fun get(): List<News> = cache ?: api.fetch() }
  ```
- 체크: DataSource 하나가 두 종류 소스를 참조하는가. 폴백 분기가 DataSource 안에 있는가.

### R-15-04 일회성 작업은 suspend 함수, 지속 관찰은 `Flow<T>`로 노출한다
- 규칙: 일회성 조회·쓰기는 `suspend` 함수로 노출하고, 단건 반환 타입은 `model` 패키지에 선언한 `sealed` 결과 타입이나 nullable로 정한다(`kotlin.Result` 금지). 변화 관찰은 `Flow<T>`로 노출하고 스트림 함수는 `get{Model}Stream()`으로 명명한다.
- 근거: one-shot은 suspend, 지속 관찰은 Flow [S06](https://developer.android.com/topic/architecture/data-layer), 계층 간 통신은 coroutines·Flow [S02](https://developer.android.com/topic/architecture/recommendations), `Result`는 도메인 실패 표현용이 아니며 구분이 필요하면 sealed class를 쓰라는 제안서 [S65](https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md)
- 예시:
  ```kotlin
  // Good
  interface NewsRepository {
      fun getNewsStream(): Flow<List<News>>
      suspend fun refresh(): RefreshResult          // model 패키지의 sealed interface
      suspend fun findById(id: String): News?       // 구분 무의미한 부재는 nullable
  }
  // Bad
  suspend fun refresh(): Result<Unit>
  fun loadNews(callback: (List<News>) -> Unit)
  ```
- 체크: 단건 반환이 `Result`나 raw 예외 전파에 기대고 있는가. 관찰 API가 suspend 폴링으로 구현됐는가.

### R-15-05 디스패처는 DataSource가 주입받아 스스로 이동한다
- 규칙: repository와 DataSource의 공개 함수는 main-safe여야 한다. 실제 블로킹이 일어나는 DataSource가 qualifier로 주입받은 `CoroutineDispatcher`로 `withContext` 하고, 호출자(repository·UseCase·ViewModel)는 다시 감싸지 않는다. `Dispatchers.IO`를 하드코딩하지 않는다.
- 근거: repository·데이터 소스는 main-safe해야 한다 [S06](https://developer.android.com/topic/architecture/data-layer), "Don't hardcode `Dispatchers`" [S40](https://developer.android.com/kotlin/coroutines/coroutines-best-practices)
- 예시:
  ```kotlin
  // Good
  class NewsLocalDataSource @Inject constructor(
      private val dao: NewsDao,
      @IoDispatcher private val ioDispatcher: CoroutineDispatcher,   // qualifier 형태는 R-14-08
  ) {
      suspend fun loadAll(): List<NewsEntity> = withContext(ioDispatcher) { dao.getAll() }
  }
  // Bad: 호출부가 감싼다
  viewModelScope.launch { withContext(Dispatchers.IO) { repository.refresh() } }
  ```
- 체크: `Dispatchers.IO` 리터럴이 코드에 남아 있는가. 같은 호출 경로에서 `withContext`가 두 번 겹치는가.

### R-15-06 repository 인터페이스와 `Default*` 구현은 모두 data 계층에 두고 DI로 바인딩한다
- 규칙: 인터페이스와 구현 모두 data 패키지(멀티모듈에서는 `:core:data`)에 둔다. 인터페이스를 domain으로 옮기는 의존 역전을 하지 않는다(R-11-02). 공개 표면은 인터페이스만 두고 구현 클래스는 `DefaultNewsRepository`처럼 `Default` 접두로 만들어 `internal`로 감춘다. 바인딩은 Hilt `@Binds`로 한다.
- 근거: 데이터 계층 컴포넌트를 인터페이스로 정의하고 구현을 바인딩하는 NiA 구성 [S54](https://raw.githubusercontent.com/android/nowinandroid/main/README.md), repository를 data 계층의 공개 API로 두는 계층 구성 [S51](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ArchitectureLearningJourney.md), 기본 구현 `Default` 접두 네이밍 [S02](https://developer.android.com/topic/architecture/recommendations), 공개 인터페이스 외에는 `internal`로 막는다 [S03](https://developer.android.com/topic/modularization)
- 예시:
  ```kotlin
  // Good
  internal class DefaultNewsRepository @Inject constructor(
      private val remote: NewsRemoteDataSource,
      private val local: NewsLocalDataSource,
  ) : NewsRepository
  @Module @InstallIn(SingletonComponent::class)
  internal interface DataModule {   // @Binds만 있으면 interface (R-14-05)
      @Binds fun bindNewsRepository(impl: DefaultNewsRepository): NewsRepository
  }
  ```
- 체크: 구현 클래스가 모듈 밖에서 참조 가능한가. `@Binds` 대신 구현 타입을 직접 주입받는 곳이 있는가.

### R-15-07 오프라인 우선을 기본값으로 두고 읽기의 SSOT는 로컬 저장소로 한다
- 규칙: 읽기는 로컬 저장소를 관찰하고 네트워크 응답은 로컬에 쓴 뒤 그 변경으로 UI에 흘려보낸다. 네트워크 직결을 택하려면 위 결정 매트릭스의 어느 줄에 해당하는지 근거를 남긴다.
- 근거: 오프라인 우선과 읽기의 로컬 수행 [S51](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ArchitectureLearningJourney.md), SSOT 지정과 캐시·영속화 지침 [S06](https://developer.android.com/topic/architecture/data-layer)
- 예시:
  ```kotlin
  // Good
  override fun getNewsStream(): Flow<List<News>> = dao.observeAll().map { it.toDomain() }
  override suspend fun refresh(): RefreshResult { dao.upsert(remote.fetch().toEntity()); /* ... */ }
  // Bad: 화면이 볼 데이터를 네트워크 응답에서 바로 만든다(재진입 시 빈 화면)
  override fun getNewsStream(): Flow<List<News>> = flow { emit(remote.fetch().toDomain()) }
  ```
- 체크: 화면 데이터가 로컬 관찰에서 나오는가. 새로고침 실패가 이미 저장된 데이터를 지우는가.

### R-15-08 저장 매체는 질의형 Room, key-value DataStore, 대형 객체 File로 나눈다
- 규칙: 질의·정렬이 필요한 대량 레코드는 Room, 소형 key-value 설정은 DataStore, 이미지·로그 같은 큰 객체는 File에 둔다. `SharedPreferences`를 새로 도입하지 않는다.
- 근거: 영속화 수단 선택 기준 [S06](https://developer.android.com/topic/architecture/data-layer)
- 예시:
  ```kotlin
  // Good
  @Dao interface NewsDao { @Query("SELECT * FROM news ORDER BY published DESC") fun observeAll(): Flow<List<NewsEntity>> }
  val darkTheme: Flow<Boolean> = dataStore.data.map { it[DARK_THEME] ?: false }
  // Bad: 목록 전체를 JSON 문자열로 DataStore에 저장하고 앱에서 정렬한다
  ```
- 체크: 질의 대상 데이터를 key-value에 통째로 넣었는가. 큰 바이너리를 DB 컬럼에 넣었는가.

### R-15-09 프로세스 사망을 넘겨야 하는 작업은 WorkManager로 처리한다
- 규칙: 동기화·업로드처럼 앱이 죽어도 끝나야 하는 비즈니스 작업은 WorkManager로 예약하고 재시도 정책을 붙인다. 화면 수명에 묶인 `viewModelScope`에서 실행하지 않는다.
- 근거: business-oriented 작업의 WorkManager 위임 [S06](https://developer.android.com/topic/architecture/data-layer), `SyncWorker` + 지수 백오프 구성 [S51](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ArchitectureLearningJourney.md)
- 예시:
  ```kotlin
  // Good
  WorkManager.getInstance(context).enqueueUniqueWork(SYNC, KEEP, SyncWorker.request())
  // Bad: 화면이 사라지면 취소되는 동기화
  viewModelScope.launch { repository.syncAll() }
  ```
- 체크: 화면을 벗어나도 끝나야 하는 작업이 화면 스코프에서 돌고 있는가. 재시도·백오프가 정의됐는가.

### R-15-10 인메모리 캐시는 스레드 안전하게 보호한다
- 규칙: repository가 인메모리 캐시를 들고 있으면 `Mutex` 등으로 접근을 직렬화한다. 평범한 `MutableList`·`HashMap`을 여러 코루틴에서 그대로 읽고 쓰지 않는다.
- 근거: 인메모리 캐시의 스레드 안전 보호 요구 [S06](https://developer.android.com/topic/architecture/data-layer)
- 예시:
  ```kotlin
  // Good
  private val mutex = Mutex()
  private var cache: List<News> = emptyList()
  suspend fun update(items: List<News>) = mutex.withLock { cache = items }
  // Bad
  private val cache = mutableListOf<News>()   // 여러 코루틴이 동시에 add
  ```
- 체크: 캐시 필드가 락 없이 여러 코루틴에서 수정되는가.

### R-15-11 예외는 데이터 계층 경계에서 잡아 `model` 타입으로 바꾼다
- 규칙: `IOException`·`HttpException`·`SQLiteException` 등 구현 예외를 위 계층으로 던지지 않는다. suspend 함수는 try/catch, Flow는 `catch` 연산자로 잡아 `model` 패키지의 `sealed` 결과·에러 타입이나 nullable로 변환한다. 예외 타입은 구체 → 일반 순서로 잡는다.
- 근거: try/catch와 Flow `catch`로 데이터 계층에서 처리한다 [S06](https://developer.android.com/topic/architecture/data-layer), 구분이 필요한 실패는 sealed 계층으로 모델링 [S65](https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md), catch 블록 순서 [S63](https://kotlinlang.org/docs/exceptions.html)
- 예시:
  ```kotlin
  // Good
  override suspend fun refresh(): RefreshResult = try {
      local.upsert(remote.fetch()); RefreshResult.Success
  } catch (e: IOException) {
      RefreshResult.NetworkUnavailable
  }
  // Bad: Retrofit 예외를 ViewModel까지 그대로 올린다
  override suspend fun refresh() = local.upsert(remote.fetch())
  ```
- 체크: ViewModel의 catch가 네트워크 라이브러리 예외 타입을 알고 있는가. Flow 수집부에서 try/catch로 막고 있는가.

### R-15-12 데이터 계층 테스트는 fake DataSource로, 통합 테스트는 in-memory Room과 MockWebServer로 구성한다
- 규칙: repository 단위 테스트는 인터페이스를 구현한 fake DataSource를 주입해 작성하고, DAO·직렬화까지 검증할 때만 in-memory Room + MockWebServer(또는 WireMock) 통합 테스트를 추가한다.
- 근거: fake 데이터 소스 주입과 in-memory Room·MockWebServer 조합 [S06](https://developer.android.com/topic/architecture/data-layer), 모킹 라이브러리 대신 동일 인터페이스 테스트 더블 사용 [S54](https://raw.githubusercontent.com/android/nowinandroid/main/README.md)
- 예시:
  ```kotlin
  // Good
  class FakeNewsRemoteDataSource(private val items: List<NewsDto>) : NewsRemoteDataSource
  val repository = DefaultNewsRepository(FakeNewsRemoteDataSource(items), local, testDispatcher)
  // Bad: DAO 호출 횟수를 mock으로 검증하는 repository 테스트
  ```
- 체크: 테스트가 구현 세부(호출 횟수)에 묶여 있는가. fake가 실제 인터페이스를 구현하는가.

### R-15-13 매퍼는 데이터 모듈 안 확장 함수로 두고 `toDomain()`·`toEntity()`로 명명한다
- 규칙: 소스 모델과 `model` 패키지 모델 사이의 변환은 데이터 모듈 안의 `internal` 확장 함수로 두고 이름을 `toDomain()`(→ `model` 타입), `toEntity()`·`toDto()`(→ 소스 타입)로 고정한다. UI·domain 계층에서 변환 함수를 호출하지 않는다.
- 근거: 소스별 모델과 공용 모델 분리 [S06](https://developer.android.com/topic/architecture/data-layer), 계층별 모델 분리와 경계 매핑 권고 [S02](https://developer.android.com/topic/architecture/recommendations), 확장 함수는 대상 타입 옆에 두고 가시성을 제한한다 [S58](https://kotlinlang.org/docs/coding-conventions.html)
- 예시:
  ```kotlin
  // Good
  internal fun NewsEntity.toDomain(): News = News(id = id, title = title)
  internal fun News.toEntity(): NewsEntity = NewsEntity(id = id, title = title)
  // Bad: ViewModel이 DTO를 직접 변환한다
  val news = api.fetch().map { News(it.id, it.title) }
  ```
- 체크: 매퍼가 public으로 열려 있는가. 변환 코드가 feature 모듈에 있는가.

### R-15-14 Room·DataStore·Retrofit 인스턴스는 전용 core 모듈에 가두고 타입을 밖으로 노출하지 않는다
- 규칙: DB·DataStore·네트워크 클라이언트와 그 Hilt 모듈은 각각 `:core:database`, `:core:datastore`, `:core:network`에 두고, repository 인터페이스와 구현이 함께 있는 `:core:data`가 이들을 `implementation`으로만 의존한다. 모델·에러·결과 타입은 아무것도 참조하지 않는 `:core:model`에 두고 `:core:data`가 이를 의존한다. feature 모듈은 database·datastore·network에 의존하지 않는다.
- 근거: core 모듈 예시(database·network·datastore·model)와 배치 기준 [S52](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ModularizationLearningJourney.md), repository와 모델 모듈 구성 [S51](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ArchitectureLearningJourney.md), data 모듈이 데이터 소스를 은닉하고 `implementation`을 우선한다 [S04](https://developer.android.com/topic/modularization/patterns), `@Module`+`@InstallIn` 선언 규약 [S05](https://developer.android.com/training/dependency-injection/hilt-android)
- 예시:
  ```kotlin
  // Good (:core:data/build.gradle.kts)
  api(projects.core.model)                  // 공개 시그니처에 쓰는 타입만 전이 노출
  implementation(projects.core.database)
  implementation(projects.core.network)
  // Bad (:feature:news/build.gradle.kts): DAO·API를 화면에서 직접 쓴다
  implementation(projects.core.database)
  ```
- 체크: feature 모듈 빌드 스크립트에 database·network 의존이 있는가. DAO·Retrofit 서비스가 `api`로 전이 노출되는가.
