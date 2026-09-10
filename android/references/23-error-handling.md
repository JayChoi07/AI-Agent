# 23 에러 처리

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

에러 타입은 90-sources `ERROR_TYPE` 결정(도메인별 커스텀 `sealed` 계층 + 단순 부재는 nullable)을 따른다. 일회성 이벤트를 UiState로 환원하는 규칙은 12가 소유하므로 여기서 다시 쓰지 않는다.

### R-23-01 호출자가 분기해야 하는 실패는 도메인별 `sealed` 타입으로 모델링한다
- 규칙: 실패 종류마다 UI 반응이 달라지면 그 도메인 전용 `sealed interface`를 만들어 성공·실패를 한 타입으로 돌려준다. 계층은 도메인 단위로 두고 앱 전역 공용 에러 enum 하나로 합치지 않는다. `sealed`로 두면 분기 누락을 컴파일러가 잡는다.

  | 판단 기준 | 선택지 A: `sealed` 결과 타입 | 선택지 B: nullable | 기본값 |
  |---|---|---|---|
  | 실패 종류에 따라 UI가 달라지는가 | 예 | 아니오 | A |
  | 실패 이유를 화면에 설명해야 하는가 | 예 | 아니오(빈 상태로 충분) | A |
  | 호출부에서 바로 흡수되는 부재인가 | 아니오 | 예 | B |
- 근거: `kotlin.Result` KEEP이 "The `Result` class is not designed to represent domain-specific error conditions"라며 구분이 필요한 도메인 실패에는 sealed class를 제시한다 [S65](https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md). 예외는 전제 위반에 쓰는 도구다 [S63](https://kotlinlang.org/docs/exceptions.html).
- 예시:
  ```kotlin
  // Good
  sealed interface LoginResult {
      data class Success(val user: User) : LoginResult
      data object InvalidCredentials : LoginResult
      data class Locked(val until: Instant) : LoginResult
  }
  // Bad: 전역 enum 하나로 모든 도메인의 실패를 표현한다
  ```
- 체크: 이 실패 종류들이 화면에서 서로 다른 결과를 내는가. 아니라면 묶거나 nullable로 줄인다.

### R-23-02 구분이 의미 없는 부재·실패는 nullable로 둔다
- 규칙: 호출부가 곧바로 기본값·빈 상태로 흡수하는 실패에는 타입을 만들지 않는다. `User?`처럼 nullable로 두고 `?:`로 처리한다.
- 근거: `kotlin.Result` KEEP이 로컬에서 처리되고 구분이 의미 없는 실패는 nullable 반환으로 충분하다고 본다 [S65](https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md). 90-sources `ERROR_TYPE` 결정의 후반부다.
- 예시:
  ```kotlin
  // Good
  fun findUser(id: UserId): User?
  val name = findUser(id)?.name ?: DEFAULT_NAME
  // Bad: 값이 없다는 사실만 담은 sealed 계층
  sealed interface FindUserResult { ... }
  ```
- 체크: 실패 분기 본문이 전부 같은 기본값으로 끝나는가. 그러면 nullable이면 된다.

### R-23-03 `kotlin.Result`나 서드파티 Either를 반환 타입으로 쓰지 않는다
- 규칙: 함수 시그니처에 `Result<T>`를 쓰지 않는다. 외부 라이브러리의 Either·Raise 계열 타입도 도입하지 않는다.
- 근거: `Result` KEEP은 이 타입이 도메인 에러 표현용이 아니며 `Catching` 접미가 붙은 함수 같은 제한된 자리에만 쓰이도록 설계됐다고 밝힌다 [S65](https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md). 타입드 에러 라이브러리는 대안이 되지만 [S105](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/) 90-sources `ERROR_TYPE` 결정에서 미채택했다.
- 예시:
  ```kotlin
  // Good
  suspend fun submit(order: Order): OrderResult
  // Bad
  suspend fun submit(order: Order): Result<Order>
  ```
- 체크: 공개 시그니처에 `Result<`가 남아 있는가.

### R-23-04 도메인 실패와 기술적 예외를 타입 수준에서 나눈다
- 규칙: "비밀번호가 틀렸다"처럼 예상된 도메인 실패는 결과 타입으로, "역직렬화 실패"처럼 프로그래밍 오류·전제 위반은 예외로 다룬다. 정상 흐름의 분기를 `try/catch`로 만들지 않는다.
- 근거: 타입드 에러 문서가 논리적 실패와 기술적 예외의 구분을 전제로 하고 [S105](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/), Kotlin 예외 문서는 `require`·`check`를 전제 위반 신호로 규정한다 [S63](https://kotlinlang.org/docs/exceptions.html), [S65](https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md).
- 예시:
  ```kotlin
  // Good
  when (val result = login(id, pw)) {
      is LoginResult.InvalidCredentials -> showInvalid()
      is LoginResult.Success -> goHome(result.user)
      is LoginResult.Locked -> showLocked(result.until)
  }
  // Bad
  try { login(id, pw) } catch (e: InvalidCredentialsException) { showInvalid() }
  ```
- 체크: `catch` 블록이 사용자에게 보여줄 정상 화면을 만들고 있는가.

### R-23-05 예외는 데이터 계층 경계에서 잡아 도메인 타입으로 바꾼다
- 규칙: 네트워크·DB 예외는 Repository·DataSource 경계에서 `try/catch`(suspend)와 `catch` 연산자(Flow)로 잡아 도메인 결과 타입으로 변환한다. 도메인 계층은 그 타입을 그대로 통과시키고, UI는 결과 타입만 본다. 프레임워크 예외 타입(`HttpException`·`SQLiteException`)이 도메인·UI 모듈로 넘어가지 않게 한다.
- 근거: 데이터 계층 가이드가 "Expose errors" 절에서 suspend는 `try/catch`, Flow는 `catch` 연산자로 처리하고 데이터 계층이 에러 종류를 이해해 노출하라고 규정한다 [S06](https://developer.android.com/topic/architecture/data-layer). 변환 대상 타입은 `ERROR_TYPE` 결정에 따라 커스텀 `sealed`다 [S65](https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md). Flow의 `catch` 연산자는 [S42](https://developer.android.com/kotlin/flow).
- 예시:
  ```kotlin
  // Good
  override suspend fun login(id: String, pw: String): LoginResult = try {
      LoginResult.Success(api.login(id, pw).toDomain())
  } catch (e: HttpException) {
      if (e.code() == 401) LoginResult.InvalidCredentials else LoginResult.Unavailable
  }
  // Bad: HttpException이 ViewModel까지 올라간다
  ```
- 체크: domain·ui 모듈의 import에 네트워크·DB 라이브러리 타입이 있는가.

### R-23-06 `catch`는 구체 → 일반 순서로 두고 리소스는 `use()`로 닫는다
- 규칙: `catch` 블록은 구체 예외를 먼저 쓰고 넓은 타입을 뒤에 둔다. `Closeable` 리소스는 `try-finally` 대신 `.use { }`로 정리한다. 취소 예외 처리는 R-22-10을 따른다.
- 근거: Kotlin 예외 문서가 catch 순서와 `.use()` 사용을 규정한다 [S63](https://kotlinlang.org/docs/exceptions.html).
- 예시:
  ```kotlin
  // Good
  file.inputStream().use { it.readBytes() }
  // Bad
  val stream = file.inputStream()
  try { stream.readBytes() } finally { stream.close() }
  ```
- 체크: 넓은 `catch`가 구체 `catch`보다 앞에 있는가.

### R-23-07 커스텀 예외는 매번 새 인스턴스로 만들고 상위 타입은 `open`으로 둔다
- 규칙: 예외를 `object`로 선언해 재사용하지 않는다(스택 트레이스가 최초 생성 지점으로 고정된다). 앱 전용 예외 계층의 최상위 클래스는 `open`으로 선언한다.
- 근거: Kotlin 예외 문서가 예외의 `object` 선언 금지와 커스텀 예외 상위 클래스의 `open` 선언을 명시한다 [S63](https://kotlinlang.org/docs/exceptions.html).
- 예시:
  ```kotlin
  // Good
  open class SyncException(message: String, cause: Throwable? = null) : Exception(message, cause)
  throw SyncException("manifest 파싱 실패", cause)
  // Bad
  object SyncFailed : Exception("sync failed")
  ```
- 체크: `object`로 선언된 예외가 있는가.

### R-23-08 사용자에게 보여줄 문구는 UI 계층에서 매핑한다
- 규칙: 도메인 결과 타입은 문구를 갖지 않는다. 문자열 리소스로의 변환은 UI 계층(Composable 또는 화면 상태 매퍼)에서 하고, 결과는 UiState의 필드로 담는다. 도메인·데이터 계층에 사용자 문구나 `Context`를 두지 않는다.
- 근거: UI 이벤트 가이드가 에러·메시지를 UI 상태로 모델링하도록 규정한다 [S09](https://developer.android.com/topic/architecture/ui-layer/events). UiState 형태와 일회성 이벤트 처리는 12가 소유한다.
- 예시:
  ```kotlin
  // Good
  @StringRes fun LoginResult.toMessageRes(): Int = when (this) {
      LoginResult.InvalidCredentials -> R.string.login_invalid
      is LoginResult.Locked -> R.string.login_locked
      is LoginResult.Success -> error("성공은 메시지가 없다")
  }
  // Bad: Repository가 "아이디 또는 비밀번호를 확인해 주세요" 문자열을 만든다
  ```
- 체크: data·domain 모듈에서 `R.string`이나 `Context`를 참조하는가.

### R-23-09 로그에 개인정보를 남기지 않는다
- 규칙: 이메일·전화번호·토큰·정확한 위치 같은 식별 정보를 로그에 넣지 않는다. 식별이 필요하면 내부 ID나 해시로 대체한다. 프로덕션 빌드의 로그 레벨은 설정으로 낮추고, 리포팅 도구로 보낼 때도 같은 기준을 적용한다.
- 근거: Android 보안 권장사항이 "inappropriate logging of user information could inadvertently leak user data to other applications"라며 로그가 `READ_LOGS` 권한을 가진 앱에 공유되는 자원임을 지적하고, PII를 남기지 말고 프로덕션에서는 로그 사용 자체를 줄이라고 규정한다 [S38](https://developer.android.com/privacy-and-security/security-tips).
- 예시:
  ```kotlin
  // Good
  logger.w("login failed: reason=INVALID_CREDENTIALS userSn=$userSn")
  // Bad
  logger.w("login failed: email=$email password=$password token=$accessToken")
  ```
- 체크: 예외를 그대로 로그에 넣을 때 메시지에 요청 본문·토큰이 섞여 있지 않은가.

### R-23-10 재시도·에러 변환 정책은 Repository 안에 둔다
- 규칙: 실패 시 재시도할지, 캐시로 폴백할지, 어떤 도메인 타입으로 바꿀지는 Repository가 정한다. ViewModel·UI는 결과 타입만 보고 "다시 시도" 버튼처럼 사용자가 유발하는 재요청만 담당한다. 재시도 횟수·백오프 값은 출처가 정해 주지 않으므로 팀이 정하고 그 자리에 근거를 남긴다.
- 근거: 데이터 계층 가이드가 Repository를 단일 진실 공급원이자 데이터 노출 책임자로 규정하고 에러 종류를 이해해 노출하도록 한다 [S06](https://developer.android.com/topic/architecture/data-layer). 같은 문서가 재시도 알고리즘·횟수는 규정하지 않는다.
- 예시:
  ```kotlin
  // Good
  override suspend fun sync(): SyncResult =
      runWithRetry(times = SYNC_RETRY_LIMIT) { api.sync() }.toSyncResult()
  // Bad: ViewModel이 실패를 세며 launch 안에서 재시도 루프를 돈다
  ```
- 체크: 재시도 루프가 ViewModel이나 Composable 안에 있는가.
