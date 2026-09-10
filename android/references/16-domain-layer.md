# 16 도메인 계층

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

도메인 계층은 선택 사항이다. 이 팩의 기본값은 **로직이 있을 때만 UseCase를 만드는 것**이고, 단순 위임이면 ViewModel이 Repository를 직접 호출한다(R-16-02·R-16-07). repository 인터페이스와 구현은 data 계층에 있고, UseCase는 그 인터페이스와 `model` 패키지 타입만 참조한다(R-11-02).

## 결정 매트릭스

UseCase를 만들 것인가 (R-16-02·R-16-07)

| 판단 기준 | 선택지 A: UseCase 생성 | 선택지 B: ViewModel → Repository 직접 호출 | 기본값 |
|---|---|---|---|
| repository 호출을 그대로 넘기기만 하는가 | A | B | B |
| repository 2개 이상을 조합하는가 | A | B | A |
| ViewModel 2개 이상이 같은 로직을 공유하는가 | A | B | A |
| 화면과 무관한 비즈니스 규칙(정렬·검증·정책 판정)이 있는가 | A | B | A |

## 규칙

### R-16-01 UseCase는 public 함수 하나만 두고 `operator fun invoke`로 노출한다
- 규칙: UseCase 클래스의 공개 API는 `operator fun invoke` 하나다. 공개 보조 함수·공개 프로퍼티를 추가하지 않고, 다른 동작이 필요하면 별도 UseCase로 나눈다.
- 근거: use case 하나는 단일 기능만 책임지고 `operator fun invoke`로 호출한다 [S07](https://developer.android.com/topic/architecture/domain-layer), 단일 메서드 `operator fun invoke` 구성 [S51](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ArchitectureLearningJourney.md)
- 예시:
  ```kotlin
  // Good
  class GetBookmarkedNewsUseCase @Inject constructor(private val repository: NewsRepository) {
      operator fun invoke(): Flow<List<News>> = repository.getNewsStream().map { it.filter(News::bookmarked) }
  }
  // Bad: 공개 함수가 둘
  class NewsUseCase {
      fun getBookmarked(): Flow<List<News>> = TODO()
      fun toggleBookmark(id: String) = TODO()
  }
  ```
- 체크: 클래스에 public 선언이 `invoke` 말고 또 있는가. 이름이 기능 하나를 가리키는가.

### R-16-02 로직이 있을 때만 UseCase를 만든다
- 규칙: repository 호출을 그대로 전달하기만 하는 UseCase는 만들지 않고 ViewModel이 repository를 직접 호출한다. UseCase는 조합·규칙·변환처럼 실제 로직이 있을 때만 추가한다.
- 근거: 단순 위임뿐이면 도메인 계층을 만들지 않는다 [S07](https://developer.android.com/topic/architecture/domain-layer), 도메인 계층은 대형 앱에 권장되는 선택 항목 [S02](https://developer.android.com/topic/architecture/recommendations)
- 예시:
  ```kotlin
  // Good: 로직이 없으므로 UseCase를 두지 않는다
  class NewsViewModel @Inject constructor(private val repository: NewsRepository)
  // Bad: 위임만 하는 껍데기
  class GetNewsUseCase @Inject constructor(private val repository: NewsRepository) {
      operator fun invoke(): Flow<List<News>> = repository.getNewsStream()
  }
  ```
- 체크: `invoke` 본문이 repository 호출 한 줄인가. 그 UseCase를 지우면 무엇이 깨지는가.

### R-16-03 UseCase 이름은 `현재형 동사 + 명사 + UseCase`로 짓는다
- 규칙: `GetLatestNewsUseCase`, `FormatDateUseCase`처럼 현재형 동사로 시작해 대상 명사를 붙이고 `UseCase`로 끝낸다. `NewsManager`·`NewsHelper` 같은 이름을 쓰지 않는다.
- 근거: use case 네이밍 규약 `[동사 현재형][명사]UseCase` [S07](https://developer.android.com/topic/architecture/domain-layer)
- 예시:
  ```kotlin
  // Good
  class GetLatestNewsWithAuthorsUseCase
  // Bad
  class NewsUseCaseImpl
  class NewsInteractor
  ```
- 체크: 이름만 읽고 무엇을 하는지 아는가. 접미가 `UseCase`인가.

### R-16-04 UseCase는 상태를 갖지 않는다
- 규칙: UseCase에 가변 프로퍼티나 캐시를 두지 않는다. 필요한 값은 파라미터로 받고 결과는 반환값으로만 돌려준다. 자체 라이프사이클이 없으므로 사용하는 쪽 스코프를 따른다.
- 근거: use case는 mutable data를 갖지 않고 자체 라이프사이클 없이 호출자에 스코프된다 [S07](https://developer.android.com/topic/architecture/domain-layer)
- 예시:
  ```kotlin
  // Good
  class FilterNewsUseCase @Inject constructor() {
      operator fun invoke(items: List<News>, query: String): List<News> = items.filter { it.title.contains(query) }
  }
  // Bad
  class FilterNewsUseCase {
      private var lastQuery: String = ""      // 호출 간 상태 유지
  }
  ```
- 체크: 클래스에 `var` 프로퍼티나 캐시 필드가 있는가. 같은 입력에 항상 같은 결과가 나오는가.

### R-16-05 UseCase는 순수 Kotlin으로 두고 UI·Android 타입에 의존하지 않는다
- 규칙: 도메인 계층은 Android 타입에 의존하지 않는 Kotlin 코드(패키지 또는 모듈)로 두고 `Context`·`Resources`·`View`·ViewModel 같은 타입을 참조하지 않는다. 별도 JVM 모듈로 분리하는 것은 선택이며, feature 모듈 안의 `domain` 패키지도 이 규칙을 만족한다. 의존은 data 계층에 선언된 repository 인터페이스, `model` 패키지 타입, 다른 UseCase, 주입된 디스패처로 제한한다. repository 인터페이스를 도메인으로 옮기는 의존 역전을 하지 않는다(R-11-02).
- 근거: use case는 repository·다른 use case·디스패처에만 의존하고 UI 계층 의존은 금지되며 의존 방향은 UI → Domain → Data [S07](https://developer.android.com/topic/architecture/domain-layer), Android 모듈보다 순수 Kotlin 모듈 선호 [S04](https://developer.android.com/topic/modularization/patterns)
- 예시:
  ```kotlin
  // Good
  class FormatPriceUseCase @Inject constructor(private val locale: Locale)
  // Bad
  class FormatPriceUseCase @Inject constructor(@ApplicationContext private val context: Context) {
      operator fun invoke(v: Int) = context.getString(R.string.price, v)
  }
  ```
- 체크: 도메인 코드의 import에 `android.*`·`androidx.*`가 있는가. 별도 모듈로 분리했다면 그 빌드 스크립트에 Android 플러그인이 붙어 있는가.

### R-16-06 UseCase는 main-safe하며 디스패처를 주입받는다
- 규칙: UseCase의 공개 함수는 main 스레드에서 호출해도 안전해야 한다. CPU 집약 작업은 주입받은 `CoroutineDispatcher`로 `withContext` 하고, 단순 위임 구간에서는 이미 main-safe한 repository를 그대로 호출한다(R-15-05와 중복 감싸기 금지).
- 근거: use case는 main-safe해야 하며 장시간 작업은 스스로 디스패처를 옮긴다 [S07](https://developer.android.com/topic/architecture/domain-layer), 디스패처 하드코딩 금지 [S40](https://developer.android.com/kotlin/coroutines/coroutines-best-practices)
- 예시:
  ```kotlin
  // Good
  class SortNewsUseCase @Inject constructor(@DefaultDispatcher private val dispatcher: CoroutineDispatcher) {
      suspend operator fun invoke(items: List<News>): List<News> = withContext(dispatcher) { items.sortedBy(News::published) }
  }
  // Bad
  suspend operator fun invoke(items: List<News>) = withContext(Dispatchers.Default) { /* ... */ }
  ```
- 체크: 무거운 계산이 호출자 스레드에서 도는가. 디스패처가 하드코딩됐는가.

### R-16-07 ViewModel 2개 이상이 공유하거나 repository 2개 이상을 조합하면 UseCase로 승격한다
- 규칙: 같은 로직을 ViewModel 두 곳 이상에서 쓰게 되거나 repository 두 개 이상의 결과를 합쳐야 하면 그 시점에 UseCase를 만든다. 미리 만들지 않고, 조건이 생겼을 때 옮긴다.
- 근거: repository 조합·재사용을 도메인 계층의 목적으로 제시 [S07](https://developer.android.com/topic/architecture/domain-layer), 대형 앱에서 use case 계층을 권장 [S02](https://developer.android.com/topic/architecture/recommendations), 여러 repository를 합치는 use case 사례 [S51](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ArchitectureLearningJourney.md)
- 예시:
  ```kotlin
  // Good: repository 2개 조합 → UseCase
  class GetNewsWithAuthorsUseCase @Inject constructor(
      private val news: NewsRepository,
      private val authors: AuthorRepository,
  ) {
      operator fun invoke(): Flow<List<NewsWithAuthor>> =
          combine(news.getNewsStream(), authors.getAuthorStream(), ::merge)
  }
  ```
- 체크: 두 번째 ViewModel이 같은 로직을 복사했는가. 조합 로직이 ViewModel에 남아 있는가.

### R-16-08 UseCase 반환 타입은 `model` 패키지의 모델과 에러 타입으로만 구성한다
- 규칙: 단건 결과는 `model` 패키지 모델, 실패는 같은 패키지에 선언한 도메인별 `sealed` 계층이나 nullable로 돌려준다. `kotlin.Result`·플랫폼 예외·DTO를 반환하지 않고, 지속 관찰은 `Flow<T>`를 그대로 전달한다.
- 근거: `Result`는 도메인 실패 표현용이 아니며 구분이 필요하면 sealed class를 쓰라는 제안서 [S65](https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md), 계층별 모델 분리 [S02](https://developer.android.com/topic/architecture/recommendations), 예외는 데이터 계층 경계에서 변환 [S06](https://developer.android.com/topic/architecture/data-layer)
- 예시:
  ```kotlin
  // Good (model 패키지에 선언)
  sealed interface LoginError { data object InvalidCredentials : LoginError; data object Network : LoginError }
  suspend operator fun invoke(id: String, pw: String): LoginError?
  // Bad
  suspend operator fun invoke(id: String, pw: String): Result<UserDto>
  ```
- 체크: 반환 타입에 `Result`나 `*Dto`가 있는가. 실패 종류를 호출자가 분기할 수 있는가.
