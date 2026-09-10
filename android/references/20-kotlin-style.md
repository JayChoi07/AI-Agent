# 20 Kotlin 스타일

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

포맷 수치(들여쓰기·줄 길이)는 ktlint가, 복잡도 수치는 detekt가 강제한다. 이 문서는 표기·선언·파일 구성 판단을 다루고, 함수 내부 설계는 21, 코루틴은 22, 에러 타입은 23이 소유한다.

### R-20-01 `!!`는 컴파일러가 증명하지 못하는 자리로만 한정한다
- 규칙: 기본 도구는 `?.`·`?:`·`as?`·조기 반환이다. 값이 반드시 있어야 하는 자리에는 실패 원인이 남는 `requireNotNull()`/`checkNotNull()`을 쓴다. `!!`는 detekt `UnsafeCallOnNullableType`(`warningsAsErrors`)이 빌드를 깨뜨리므로 주석만으로는 통과하지 않는다. 정말 남겨야 하면 그 줄에 `@Suppress("UnsafeCallOnNullableType")`와 사유 주석을 함께 붙이는 것이 유일한 예외 경로다.
- 근거: Kotlin 널 안전 문서는 `!!`를 "값이 null이 아님을 확신하지만 컴파일러가 보증하지 못할 때"의 도구로 설명하고 금지하지 않는다 [S62](https://kotlinlang.org/docs/null-safety.html). Kodeco 가이드는 초기화가 보장된 인스턴스 변수로만 허용한다 [S106](https://github.com/kodecocodes/kotlin-style-guide). 전면 금지를 명시한 출처가 없어 조건부 규칙으로 두되, 이 팩의 게이트(`enforcement/config/detekt/detekt.yml`)가 문서보다 우선하므로 예외는 `@Suppress` 한 줄로만 낸다. 실패 원인을 남기는 `requireNotNull`/`checkNotNull`은 [S63](https://kotlinlang.org/docs/exceptions.html).
- 예시:
  ```kotlin
  // Good
  val user = repository.findUser(id) ?: return LoginResult.NoSuchUser
  val token = checkNotNull(session.token) { "login() 이후에만 호출된다" }
  // Good — 남겨야 할 때의 유일한 형태
  @Suppress("UnsafeCallOnNullableType") // onCreate 이후에만 호출된다
  val view = binding!!.root
  // Bad — 주석만 붙이면 detekt 가 빌드를 깨뜨린다
  val token = session.token!! // 로그인 후라 non-null
  ```
- 체크: `!!`가 `@Suppress("UnsafeCallOnNullableType")` + 사유 주석 없이 남아 있는가. `?:`나 `requireNotNull`로 바꿔도 동작이 같지 않은가.

### R-20-02 와일드카드 import를 쓰지 않는다
- 규칙: import는 정렬된 단일 목록으로 두고 각 선언을 개별 import한다. 와일드카드(`*`)로 묶지 않는다.
- 근거: Android Kotlin 스타일 가이드가 "No wildcard imports"를 명시한다 [S35](https://developer.android.com/kotlin/style-guide). detekt `WildcardImport`도 기본 활성이며 예외는 `excludeImports` 기본값 `java.util.*`뿐이다 [S81](https://detekt.dev/docs/rules/style).
- 예시:
  ```kotlin
  // Good
  import androidx.compose.foundation.layout.Column
  import androidx.compose.foundation.layout.Row
  // Bad
  import androidx.compose.foundation.layout.*
  ```
- 체크: IDE의 "import 개수 초과 시 자동 접기" 옵션이 꺼져 있는가.

### R-20-03 블록 들여쓰기는 4 스페이스, 탭은 쓰지 않는다
- 규칙: 블록 들여쓰기 4 스페이스, 줄바꿈 연속 들여쓰기도 스페이스만 쓴다. 탭 문자를 넣지 않는다.
- 근거: Android 스타일 가이드와 Kotlin 코딩 컨벤션이 4 스페이스로 일치한다 [S35](https://developer.android.com/kotlin/style-guide), [S58](https://kotlinlang.org/docs/coding-conventions.html). 2 스페이스를 쓰는 서드파티 가이드가 있으나 [S106](https://github.com/kodecocodes/kotlin-style-guide), 공식 두 출처와 Android Studio 기본값을 따른다.
- 예시:
  ```kotlin
  // Good
  fun sync() {
      if (enabled) {
          repository.refresh()
      }
  }
  // Bad: 2 스페이스 또는 탭 혼용
  ```
- 체크: `.editorconfig`에 `indent_size = 4`가 있고 diff에 탭이 섞이지 않았는가.

### R-20-04 한 줄은 100자를 넘기지 않는다
- 규칙: 열 제한 100자. 도구 기본값이 다르면 프로젝트 설정에서 100으로 재설정한다.
- 근거: Android 스타일 가이드가 열 제한 100자를 규정한다 [S35](https://developer.android.com/kotlin/style-guide). detekt `MaxLineLength` 기본값은 120이므로 [S81](https://detekt.dev/docs/rules/style) 설정에서 100으로 낮춘다(90-sources `LINE_LENGTH`).
- 예시:
  ```kotlin
  // Good: 인자를 줄바꿈해 100자 안에 둔다
  fun refresh(
      userId: UserId,
      forceNetwork: Boolean = false,
  ): Flow<SyncState>
  ```
- 체크: detekt 설정의 `MaxLineLength.maxLineLength`가 100인가.

### R-20-05 포맷은 손이 아니라 포맷터가 정한다
- 규칙: 포맷터는 ktlint-gradle 단독으로 두고 `ktlintCheck`를 CI 게이트에 넣는다. 스타일 설정은 `.editorconfig`(`ktlint_code_style = android_studio`)에 두고 엔진 버전은 빌드 스크립트에서 `ktlint { version.set("1.8.0") }`으로 고정한다. 리뷰에서 포맷을 손으로 지적하지 않는다.
- 근거: ktlint-gradle이 `ktlintCheck`/`ktlintFormat` 태스크와 엔진 버전 고정을 제공한다 [S85](https://raw.githubusercontent.com/JLLeitschuh/ktlint-gradle/main/README.md). 코드 스타일 선택지와 `.editorconfig` 설정 키는 ktlint 문서에 있다 [S83](https://raw.githubusercontent.com/pinterest/ktlint/master/documentation/release-latest/docs/rules/code-styles.md), [S84](https://raw.githubusercontent.com/pinterest/ktlint/master/documentation/release-latest/docs/rules/configuration-ktlint.md). 포맷터 단일화는 90-sources `FORMATTER` 결정이다.
- 예시:
  ```kotlin
  // Good: 빌드 스크립트
  ktlint { version.set("1.8.0") }
  // Bad: 포맷터 두 개(spotless + ktlint)를 동시에 걸어 서로 되돌린다
  ```
- 체크: CI에 `ktlintCheck`가 있고 포맷 도구가 하나뿐인가.

### R-20-06 타입은 PascalCase, 함수·프로퍼티는 camelCase, 약어도 한 단어로 쓴다
- 규칙: 패키지는 소문자 연속(언더스코어 금지), 타입은 PascalCase, 함수·프로퍼티·지역 변수는 camelCase. 약어는 단어로 취급해 `XmlHttpRequest`·`newCustomerId`로 적는다.
- 근거: Android 스타일 가이드의 명명 규칙과 약어 처리 예시 [S35](https://developer.android.com/kotlin/style-guide), Kotlin 코딩 컨벤션의 명명 규칙 [S58](https://kotlinlang.org/docs/coding-conventions.html).
- 예시:
  ```kotlin
  // Good
  class XmlHttpRequest
  val newCustomerId: CustomerId
  // Bad
  class XMLHTTPRequest
  val newCustomerID: CustomerId
  ```
- 체크: 새로 만든 타입·프로퍼티 이름에 연속 대문자 약어가 남아 있지 않은가.

### R-20-07 `UPPER_SNAKE_CASE`는 진짜 상수에만 쓴다
- 규칙: `const val`, 또는 커스텀 게터가 없고 깊은 불변인 최상위·`object`의 `val`만 `UPPER_SNAKE_CASE`로 쓴다. 그 밖의 프로퍼티는 camelCase다.
- 근거: Android 스타일 가이드가 상수의 조건(`const`, 커스텀 게터 없음, 깊은 불변, `object`/최상위)을 명시한다 [S35](https://developer.android.com/kotlin/style-guide), [S58](https://kotlinlang.org/docs/coding-conventions.html).
- 예시:
  ```kotlin
  // Good
  private const val RETRY_LIMIT = 3
  val defaultFormatter: DateTimeFormatter get() = ...
  // Bad: 가변 컬렉션이라 상수가 아니다
  val ALLOWED_TYPES = mutableListOf("a", "b")
  ```
- 체크: `UPPER_SNAKE_CASE` 이름 중 가변 객체를 담은 것이 있는가.

### R-20-08 백킹 프로퍼티는 밑줄 접두 + 공개 이름과 정확히 일치시킨다
- 규칙: 내부 가변 프로퍼티는 `_name`, 공개 읽기 전용은 `name`으로 이름을 맞춘다. 다른 이름을 붙이지 않는다.
- 근거: Android 스타일 가이드의 백킹 프로퍼티 규칙 [S35](https://developer.android.com/kotlin/style-guide), [S58](https://kotlinlang.org/docs/coding-conventions.html). 상태 홀더에 적용한 형태는 22와 12가 다룬다.
- 예시:
  ```kotlin
  // Good
  private val _uiState = MutableStateFlow(HomeUiState())
  val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
  // Bad
  private val stateInternal = MutableStateFlow(HomeUiState())
  ```
- 체크: `_`로 시작하는 프로퍼티마다 같은 이름의 공개 프로퍼티가 있는가.

### R-20-09 `val`과 불변 컬렉션 타입을 기본으로 쓴다
- 규칙: 재할당이 필요 없으면 `val`, 컬렉션은 `List`·`Set`·`Map` 타입으로 선언하고 `listOf()`·`mapOf()`로 만든다. 가변 컬렉션은 만드는 지역 범위 안에서만 쓰고 밖으로는 불변 타입으로 반환한다.
- 근거: Kotlin 코딩 컨벤션이 `val` 우선과 불변 컬렉션 타입 선언을 권한다 [S58](https://kotlinlang.org/docs/coding-conventions.html), [S106](https://github.com/kodecocodes/kotlin-style-guide).
- 예시:
  ```kotlin
  // Good
  fun visibleItems(): List<Item> = items.filter { it.visible }
  // Bad
  fun visibleItems(): MutableList<Item> = items.filterTo(mutableListOf()) { it.visible }
  ```
- 체크: 공개 API 반환 타입에 `Mutable*`이 있는가.

### R-20-10 공개 선언은 가시성과 타입을 눈에 보이게 적는다
- 규칙: 모듈 밖에서 쓰지 않는 선언은 `internal`/`private`로 좁힌다. 모듈 경계를 넘는 선언은 가시성·반환 타입·프로퍼티 타입을 생략하지 않고 명시한다.
- 근거: Kotlin 코딩 컨벤션이 라이브러리 성격 코드에서 가시성·반환 타입·프로퍼티 타입 명시를 요구하고, 확장 함수 가시성도 필요한 범위로 제한하라고 한다 [S58](https://kotlinlang.org/docs/coding-conventions.html), [S35](https://developer.android.com/kotlin/style-guide). 멀티모듈에서는 이 표면이 모듈 API가 된다.
- 예시:
  ```kotlin
  // Good
  internal class DefaultUserRepository(...) : UserRepository
  public fun UserRepository.observeMe(): Flow<User> = observe(Me)
  // Bad: 모듈 내부 구현이 public으로 새어 나간다
  class DefaultUserRepository(...)
  ```
- 체크: 다른 모듈이 실제로 참조하는 선언만 public인가.

### R-20-11 클래스 본문 순서를 고정한다
- 규칙: 프로퍼티·초기화 블록 → 보조 생성자 → 메서드 → `companion object` 순서로 배치하고, 관련된 선언은 붙여 둔다. 알파벳순으로 흩뜨리지 않는다.
- 근거: Kotlin 코딩 컨벤션의 클래스 레이아웃과 "put related stuff together" [S58](https://kotlinlang.org/docs/coding-conventions.html).
- 예시:
  ```kotlin
  // Good
  class Session(private val clock: Clock) {
      private var startedAt: Instant? = null
      fun start() { ... }
      companion object { const val TIMEOUT_MINUTES = 30 }
  }
  ```
- 체크: `companion object`가 클래스 끝에 있고 관련 메서드가 흩어져 있지 않은가.

### R-20-12 파일은 한 주제로 유지하고 이름으로 내용을 드러낸다
- 규칙: 한 파일은 수백 줄을 넘지 않게 유지하고, 최상위 선언이 여럿이면 파일명이 그 묶음을 설명해야 한다. `Util`·`Helper`·`Manager` 같은 무의미한 파일명·클래스명은 쓰지 않는다.
- 근거: Kotlin 코딩 컨벤션이 파일 크기("not exceeding a few hundred lines")와 무의미한 파일명 금지를 명시한다 [S58](https://kotlinlang.org/docs/coding-conventions.html).
- 예시:
  ```kotlin
  // Good: DateFormatting.kt — 날짜 포맷 확장 함수 모음
  fun Instant.toDisplayDate(locale: Locale): String = ...
  // Bad: Utils.kt — 서로 무관한 확장 20개
  ```
- 체크: 파일명만 보고 안에 무엇이 있는지 말할 수 있는가.

### R-20-13 한 표현식으로 끝나는 함수는 표현식 본문으로 쓴다
- 규칙: 본문이 단일 표현식이면 `=` 표현식 본문을 쓴다. 여러 문장이거나 조건 분기 안에서 부수효과가 일어나면 블록 본문 + 명시적 반환 타입을 쓴다.
- 근거: Kotlin 코딩 컨벤션이 표현식 본문 사용과 줄바꿈 형식을 규정한다 [S58](https://kotlinlang.org/docs/coding-conventions.html). 공개 API에서 타입을 생략하지 않는 것은 R-20-10과 같은 근거다.
- 예시:
  ```kotlin
  // Good
  fun isExpired(now: Instant): Boolean = expiresAt <= now
  // Bad: 표현식 본문에 여러 부수효과를 밀어 넣는다
  fun save(item: Item) = dao.insert(item).also { analytics.log(it) }.let { cache.put(it) }
  ```
- 체크: 표현식 본문 안에서 부수효과가 두 번 이상 일어나지 않는가.

### R-20-14 KDoc은 공개 표면에 달고 설명은 본문 문장으로 쓴다
- 규칙: `public`·`protected` 타입과 멤버에는 KDoc을 단다. 자명한 접근자와 오버라이드는 예외다. 설명은 본문 문장에 통합하고 `@param`/`@return`은 본문으로 담기 어려울 만큼 설명이 길 때만 쓴다.
- 근거: Android 스타일 가이드는 공개 표면 KDoc을 최소 요구로 두고 [S35](https://developer.android.com/kotlin/style-guide), Kotlin 코딩 컨벤션은 "Avoid `@param` and `@return` tags"로 본문 통합을 권한다 [S58](https://kotlinlang.org/docs/coding-conventions.html). 90-sources `KDOC_TAGS` 결정을 따른다.
- 예시:
  ```kotlin
  // Good
  /** 로그인 세션을 갱신하고 만료 시각을 [now] 기준으로 다시 계산한다. */
  fun refresh(now: Instant): Session
  // Bad: 시그니처를 그대로 옮긴 태그만 나열
  ```
- 체크: 새 public 선언에 KDoc이 있고, 태그가 본문 설명을 반복하지 않는가.

### R-20-15 마커 주석은 머지 전에 정리한다
- 규칙: `TODO:`·`FIXME:`·`STOPSHIP:` 주석은 머지 전에 해결하거나 이슈 링크가 있는 형태로 바꾼다. 코드에 남겨 릴리스로 넘기지 않는다.
- 근거: detekt `ForbiddenComment` 기본값이 `['FIXME:','STOPSHIP:','TODO:']`이며 기본 활성이다 [S81](https://detekt.dev/docs/rules/style). 근거는 도구 기본값이다.
- 예시:
  ```kotlin
  // Good
  // 재시도 정책은 이슈 #421에서 다룬다. 현재는 1회만 시도한다.
  // Bad
  // TODO: 나중에 고치기
  ```
- 체크: 이번 변경에서 새로 들어온 `TODO:`가 있는가.
