# 21 함수 설계

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

수치 임계값(60줄·5개·깊이 4·조건 3·복잡도 14)은 detekt 2.0.0-alpha.6 기본값을 그대로 채택한 것이다. 언어·스타일 권위(S35·S58)는 함수 길이·파라미터 수·중첩 깊이에 어떤 수치도 제시하지 않으므로, 팀이 근거를 갖고 다른 수치를 정하면 바꿔도 되는 자리다.

### R-21-01 함수는 한 가지 일만 하고 본문 60줄을 넘기지 않는다
- 규칙: 함수 이름이 하는 일을 한 문장으로 설명하지 못하면 나눈다. 본문(시그니처·주석 제외)이 60줄을 넘으면 책임이 섞였다는 신호이므로 분리한다.
- 근거: detekt `LongMethod`의 확인된 기본값 `allowedLines` 60을 채택한다 [S80](https://detekt.dev/docs/rules/complexity). 1.23.8도 같은 60이다 [S82](https://detekt.dev/docs/1.23.8/rules/complexity). 근거는 도구 기본값이며, 공식 스타일 가이드는 함수 길이에 침묵한다.
- 예시:
  ```kotlin
  // Good
  fun submit(form: SignUpForm): SignUpResult {
      val validated = validate(form) ?: return SignUpResult.Invalid
      return register(validated)
  }
  // Bad: 검증·변환·네트워크·로깅을 한 함수에서 80줄로 처리
  ```
- 체크: 함수 이름에 "and"나 "그리고"를 넣어야 설명이 되는가.

### R-21-02 파라미터는 함수 5개·생성자 6개까지, 넘으면 data class로 묶는다
- 규칙: 함수 파라미터 5개, 생성자 파라미터 6개를 상한으로 둔다(기본값이 있는 파라미터도 센다). 넘으면 함께 움직이는 값들을 의미 있는 data class 하나로 묶어 전달한다.
- 근거: detekt `LongParameterList`의 2.0.0-alpha.6 기본값 함수 5·생성자 6을 채택한다 [S80](https://detekt.dev/docs/rules/complexity). 1.23.8은 6·7이라 값이 다르므로 [S82](https://detekt.dev/docs/1.23.8/rules/complexity) 설정에 명시한다. 근거는 도구 기본값이다.
- 예시:
  ```kotlin
  // Good
  data class SignUpForm(val email: String, val password: String, val nickname: String, val agreedTerms: Boolean)
  fun submit(form: SignUpForm, source: SignUpSource): SignUpResult
  // Bad
  fun submit(email: String, password: String, nickname: String, agreedTerms: Boolean, source: SignUpSource, retry: Boolean)
  ```
- 체크: 파라미터 중 항상 함께 바뀌는 묶음이 있는가. 있으면 타입 하나로 묶을 수 있다.

### R-21-03 중첩 블록 깊이는 4를 넘기지 않는다
- 규칙: `if`/`for`/`when`/람다가 4단계보다 깊어지면 중첩을 푼다. 안쪽 블록을 함수로 뽑거나 가드절로 되돌린다.
- 근거: detekt `NestedBlockDepth` 기본값 4를 채택한다 [S80](https://detekt.dev/docs/rules/complexity), [S82](https://detekt.dev/docs/1.23.8/rules/complexity). 근거는 도구 기본값이다.
- 예시:
  ```kotlin
  // Good
  items.filter { it.isActive }.forEach { notify(it) }
  // Bad
  for (g in groups) { if (g.enabled) { for (i in g.items) { if (i.isActive) { if (i.new) notify(i) } } } }
  ```
- 체크: 가장 깊은 들여쓰기 블록이 화면 오른쪽으로 밀려 있지 않은가.

### R-21-04 가드절로 먼저 빠져나오되 `return`은 2개까지로 둔다
- 규칙: 정상 경로를 들여쓰기 안쪽에 두지 말고, 입력 검증은 `require()`, 상태 전제는 `check()`, 도달 불가 분기는 `error()`로 함수 앞부분에서 끝낸다. 그렇게 해도 `return`이 2개를 넘으면 함수를 나눈다.
- 근거: `require()`→`IllegalArgumentException`, `check()`/`error()`→`IllegalStateException` 사용 구분은 Kotlin 예외 문서 [S63](https://kotlinlang.org/docs/exceptions.html). `return` 2개는 detekt `ReturnCount` 기본값이다 [S81](https://detekt.dev/docs/rules/style). 근거는 도구 기본값이다.
- 예시:
  ```kotlin
  // Good
  fun award(points: Int, user: User?): Reward {
      require(points > 0) { "points must be positive" }
      val target = user ?: return Reward.None
      return grant(target, points)
  }
  ```
- 체크: 정상 경로가 함수의 마지막 줄에 평평하게 놓여 있는가.

### R-21-05 조건이 3개를 넘으면 이름 붙인 `val`로 분해한다
- 규칙: 하나의 `if`/`while` 조건에 `&&`·`||`로 이어진 항이 3개를 넘으면 의미 있는 이름의 `val`로 쪼갠다. 함수의 순환 복잡도는 14를 상한으로 둔다.
- 근거: detekt `ComplexCondition` 기본값 3, `CyclomaticComplexMethod` 기본값 14를 채택한다 [S80](https://detekt.dev/docs/rules/complexity). 1.23.8은 각각 4·15로 다르다 [S82](https://detekt.dev/docs/1.23.8/rules/complexity). 근거는 도구 기본값이다.
- 예시:
  ```kotlin
  // Good
  val isEligible = user.isVerified && user.age >= MIN_AGE
  val isAvailable = stock > 0 && !item.discontinued
  if (isEligible && isAvailable) { ... }
  // Bad
  if (user.isVerified && user.age >= 19 && stock > 0 && !item.discontinued) { ... }
  ```
- 체크: 조건식을 소리 내어 읽었을 때 한 호흡에 끝나는가.

### R-21-06 상태를 바꾸지 않는 계산은 프로퍼티로, 상태를 바꾸는 동작만 함수로 만든다
- 규칙: 예외를 던지지 않고, 저렴하며, 호출할 때마다 같은 결과를 주는 계산은 프로퍼티로 노출한다. 부수효과가 있는 동작은 함수로 두고 이름에 그 동작을 드러낸다.
- 근거: Kotlin 코딩 컨벤션의 함수 대신 프로퍼티 기준(예외 없음·저비용·상태 불변 시 같은 결과) [S58](https://kotlinlang.org/docs/coding-conventions.html).
- 예시:
  ```kotlin
  // Good
  val isEmpty: Boolean get() = items.isEmpty()
  fun clearCache()
  // Bad: 계산인데 함수, 부수효과인데 프로퍼티처럼 보이는 이름
  fun getIsEmpty(): Boolean
  val refreshedUser: User get() = api.fetchUser()
  ```
- 체크: 프로퍼티 게터 안에서 네트워크·DB·로깅이 일어나지 않는가.

### R-21-07 변환은 고차 함수로, 단독 순회는 `for`로 쓴다
- 규칙: 걸러내기·변환·집계는 `filter`·`map`·`sumOf` 같은 고차 함수를 쓴다. 원소마다 부수효과만 실행하는 단독 순회는 `forEach` 대신 일반 `for` 루프를 쓴다.
- 근거: Kotlin 코딩 컨벤션이 루프보다 고차 함수를 권하면서 `forEach`만 예외로 `for`를 선호한다 [S58](https://kotlinlang.org/docs/coding-conventions.html). 90-sources `COLLECTION_ITER` 결정을 따른다.
- 예시:
  ```kotlin
  // Good
  val names = users.filter { it.active }.map { it.name }
  for (user in users) logger.log(user.id)
  // Bad
  users.forEach { logger.log(it.id) }
  ```
- 체크: 체인 끝이 아닌 자리에서 `forEach`를 부수효과 전용으로 쓰고 있는가.

### R-21-08 체인이 한 문장으로 읽히지 않으면 중간 결과에 이름을 붙인다
- 규칙: 단계 수 상한은 두지 않는다. 대신 체인이 서로 다른 관심사를 섞거나(필터링 + 도메인 계산 + 포맷팅), 한 문장으로 설명되지 않으면 중간 결과를 이름 있는 `val`로 끊는다. 줄바꿈은 점을 다음 줄 앞에 두고 한 단계 들여쓴다.
- 근거: Kotlin 코딩 컨벤션은 체인의 줄바꿈 형식만 규정하고 단계 수에는 침묵하며, Sequence 문서도 "multiple steps"라고만 한다 [S58](https://kotlinlang.org/docs/coding-conventions.html), [S60](https://kotlinlang.org/docs/sequences.html). 수치 상한을 제시한 출처가 없어 판단 기준으로 둔다(90-sources 보류 표).
- 예시:
  ```kotlin
  // Good
  val activeOrders = orders.filter { it.isActive }
  val totalAmount = activeOrders.sumOf { it.amount }
  // Bad: 필터·집계·포맷을 한 체인에 이어 붙여 이름이 하나도 남지 않는다
  ```
- 체크: 체인 결과에 이름을 붙인다면 뭐라고 부를지 바로 떠오르는가. 떠오르는데 이름이 없다면 끊는다.

### R-21-09 `Sequence`는 여러 단계를 큰 컬렉션에 적용할 때만 쓴다
- 규칙: 단계가 여럿이고 원소 수가 많아 중간 컬렉션 생성이 부담일 때만 `asSequence()`로 바꾼다. 작은 컬렉션이나 단순 연산은 그냥 `Iterable`로 둔다.
- 근거: Sequence 문서는 지연 처리의 오버헤드가 "작은 컬렉션이나 단순 연산에서는 무시하기 어렵다"고 하면서 전환 임계 수치는 명시적으로 제시하지 않고 사례별 판단을 요구한다 [S60](https://kotlinlang.org/docs/sequences.html).
- 예시:
  ```kotlin
  // Good
  val firstMatch = hugeLogLines.asSequence().map(::parse).firstOrNull { it.isError }
  // Bad: 원소 3개짜리 리스트에 Sequence 오버헤드를 얹는다
  val names = threeUsers.asSequence().map { it.name }.toList()
  ```
- 체크: 컬렉션 크기가 실제로 큰지, 단계가 실제로 여럿인지 확인했는가.

### R-21-10 스코프 함수는 반환값·수신자·용도 표대로 고른다
- 규칙: 아래 표대로 고른다. 고민되면 스코프 함수를 쓰지 않고 지역 `val` + 일반 호출로 쓴다.

  | 판단 기준 | 수신자 `this` | 수신자 `it` | 기본값 |
  |---|---|---|---|
  | 람다 결과를 반환 | `run`(확장) / `with`(인자) | `let` | `let` |
  | 객체 자신을 반환 | `apply`(설정) | `also`(부가 효과) | `apply` |
  | null 아닐 때만 실행 | — | `?.let` | `?.let` |
- 근거: Kotlin 스코프 함수 문서의 선택표와 용도 설명(`let`=non-null 실행·지역 변수화, `run`=설정+결과 계산, `with`=호출 묶기, `apply`=객체 설정, `also`=부가 효과) [S59](https://kotlinlang.org/docs/scope-functions.html).
- 예시:
  ```kotlin
  // Good
  val request = Request.Builder().apply { url(endpoint); tag(traceId) }.build()
  user?.let { analytics.identify(it.id) }
  // Bad: 반환값이 필요한데 apply를 써서 객체 자신이 돌아온다
  val body = response.apply { parse() }
  ```
- 체크: 이 자리에서 필요한 것이 람다 결과인가 객체 자신인가.

### R-21-11 스코프 함수를 중첩하지 않는다
- 규칙: 스코프 함수 안에 다른 스코프 함수를 넣지 않는다. 중첩이 필요해 보이면 안쪽을 이름 있는 함수나 지역 `val`로 뽑는다.
- 근거: 스코프 함수 문서가 "avoid overusing them: it can make your code hard to read and lead to errors"와 "avoid nesting scope functions and be careful when chaining them"을 명시한다 [S59](https://kotlinlang.org/docs/scope-functions.html).
- 예시:
  ```kotlin
  // Good
  val profile = user.toProfile()
  profile.also { cache.put(it) }
  // Bad
  user.let { u -> u.profile?.apply { u.also { logger.log(it.id) } } }
  ```
- 체크: `it`이 어느 스코프의 것인지 한 번에 알 수 있는가.

### R-21-12 분기가 3개 이상이면 `when`, 2개면 `if`를 쓰고 표현식으로 만든다
- 규칙: 분기 3개 이상은 `when`으로, 2개는 `if`로 쓴다. 각 분기가 값을 만든다면 문장 대신 표현식(`return if (...) a else b`)으로 쓴다.
- 근거: Kotlin 코딩 컨벤션이 분기 수에 따른 `if`/`when` 선택과 표현식형 조건문 선호를 명시한다 [S58](https://kotlinlang.org/docs/coding-conventions.html).
- 예시:
  ```kotlin
  // Good
  val label = when (state) {
      Idle -> "대기"
      Loading -> "불러오는 중"
      is Failed -> state.reason
  }
  // Bad: var를 먼저 만들고 분기마다 대입한다
  ```
- 체크: 분기마다 같은 변수에 대입하고 있다면 표현식으로 바꿀 수 있는가.

### R-21-13 확장 함수는 대상 타입 옆에 두고 가시성을 좁힌다
- 규칙: 확장 함수는 수신 타입을 소유한 모듈·파일에 두거나, 그럴 수 없으면 사용하는 코드 바로 옆에 둔다. 가시성은 지역·멤버·`private` 최상위 중 가장 좁은 것으로 둔다.
- 근거: Kotlin 코딩 컨벤션이 확장 함수의 배치(대상 클래스와 같은 파일 또는 사용처)와 가시성 제한을 규정한다 [S58](https://kotlinlang.org/docs/coding-conventions.html).
- 예시:
  ```kotlin
  // Good: UserMappers.kt (data 모듈) — 이 모듈만 쓰는 매핑
  internal fun UserEntity.toDomain(): User = User(id = UserId(id), name = name)
  // Bad: core 공용 모듈에 public 확장으로 올려 어디서나 보이게 만든다
  ```
- 체크: 이 확장이 실제로 다른 모듈에서 쓰이는가. 아니면 `internal`로 좁힌다.

### R-21-14 오버로드 대신 기본 인자를 쓴다
- 규칙: 같은 동작의 변형은 오버로드 대신 기본 인자 값으로 표현한다. 그래도 오버로드가 필요하면 이름당 6개를 넘기지 않는다.
- 근거: Kotlin 코딩 컨벤션이 "오버로드보다 기본 인자 값"을 권한다 [S58](https://kotlinlang.org/docs/coding-conventions.html). 오버로드 6개는 detekt `MethodOverloading` 기본값이다 [S80](https://detekt.dev/docs/rules/complexity).
- 예시:
  ```kotlin
  // Good
  fun refresh(force: Boolean = false, source: Source = Source.User)
  // Bad
  fun refresh()
  fun refresh(force: Boolean)
  fun refresh(force: Boolean, source: Source)
  ```
- 체크: 오버로드들의 본문이 서로를 호출하는 껍데기뿐인가.

### R-21-15 같은 타입이 연달아 오거나 `Boolean` 인자를 넘길 때는 명명 인자를 쓴다
- 규칙: 호출부에서 같은 원시 타입 인자가 연달아 오거나 `Boolean`·`null` 리터럴을 넘길 때는 이름을 붙여 호출한다. 인자가 3개를 넘으면 기본적으로 명명 인자를 쓴다.
- 근거: Kotlin 코딩 컨벤션이 같은 원시 타입 다수·`Boolean` 파라미터에 명명 인자를 권한다 [S58](https://kotlinlang.org/docs/coding-conventions.html). 3개 기준은 detekt `NamedArguments` 기본값이다 [S80](https://detekt.dev/docs/rules/complexity).
- 예시:
  ```kotlin
  // Good
  createUser(name = "kim", nickname = "kk", isAdmin = false)
  // Bad
  createUser("kim", "kk", false)
  ```
- 체크: 호출부만 보고 각 인자가 무엇인지 알 수 있는가.

### R-21-16 `infix`는 역할이 대칭인 두 객체에만 쓴다
- 규칙: `infix`는 `and`·`to`·`zip`처럼 두 대상의 역할이 대칭일 때만 선언한다. 수신자를 변경하는 동작에는 쓰지 않는다.
- 근거: Kotlin 코딩 컨벤션의 infix 사용 기준(대칭 역할, 수신자 변경 시 금지) [S58](https://kotlinlang.org/docs/coding-conventions.html).
- 예시:
  ```kotlin
  // Good
  infix fun Filter.and(other: Filter): Filter
  // Bad: 수신자를 바꾸는 동작
  infix fun Cart.add(item: Item)
  ```
- 체크: 좌우를 바꿔 읽어도 말이 되는 관계인가.

### R-21-17 매직 넘버 대신 이름 붙인 상수를 쓴다
- 규칙: `-1`·`0`·`1`·`2`를 제외한 숫자 리터럴은 의미를 드러내는 상수로 뽑는다.
- 근거: detekt `MagicNumber`의 기본 `ignoreNumbers` 값이 `['-1','0','1','2']`이다 [S81](https://detekt.dev/docs/rules/style). 근거는 도구 기본값이다.
- 예시:
  ```kotlin
  // Good
  private const val SESSION_TIMEOUT_MINUTES = 30
  if (idleMinutes > SESSION_TIMEOUT_MINUTES) logout()
  // Bad
  if (idleMinutes > 30) logout()
  ```
- 체크: 리터럴이 무슨 단위인지 코드만 보고 알 수 있는가.

### R-21-18 예상된 실패는 예외 대신 도메인 타입으로 돌려준다
- 규칙: 호출자가 분기해야 하는 실패(입력 거절·권한 없음·중복)는 예외로 던지지 말고 도메인별 `sealed` 결과 타입으로 반환한다. 구분이 필요 없는 단순 부재는 nullable로 둔다. 상세 모델과 계층 경계는 23이 소유한다.
- 근거: `kotlin.Result` KEEP은 "The `Result` class is not designed to represent domain-specific error conditions"라며 로컬 처리·의미 없는 실패는 nullable, 구분이 필요한 도메인 실패는 sealed class를 제시한다 [S65](https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md). 예외는 프로그래밍 오류·전제 위반에 쓴다 [S63](https://kotlinlang.org/docs/exceptions.html). 90-sources `ERROR_TYPE` 결정을 따른다.
- 예시:
  ```kotlin
  // Good
  fun login(id: String, pw: String): LoginResult   // sealed interface LoginResult
  fun findUser(id: UserId): User?                  // 없으면 null
  // Bad
  fun login(id: String, pw: String): User          // 실패는 예외로 던진다
  ```
- 체크: 호출자가 `try/catch`로 정상 흐름을 분기하고 있지 않은가.
