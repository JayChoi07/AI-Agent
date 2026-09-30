# 26 시간 다루기

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

시각 버그는 대개 세 곳에서 난다. 현재 시각을 코드 곳곳에서 직접 읽어 테스트가 불가능해지는 것, 간격 측정에 사용자가 바꿀 수 있는 벽시계를 쓰는 것, "하루"와 "한 달"을 시간대와 무관하게 계산하는 것이다. 이 문서는 시각 API 선택(R-26-01~02), 현재 시각의 주입(R-26-03~05), 시계 종류와 간격 측정(R-26-06), 시간대·경계 계산(R-26-07~09), 저장·직렬화·표시(R-26-10~12), 예약 작업(R-26-13)을 정한다.

디스패처를 주입하는 방식(R-14-08, R-22-02, R-30-09)과 같은 원리를 `Clock`에 적용한다. 매직 넘버(R-21-17)와 Room·DataStore 선택(R-15-08)은 해당 규칙이 소유한다.

확정 결정 `TIME_API`(사용자 확정, 2026-09-30): `java.time` + `java.time.Clock`. `TIME_STORAGE`(팩 기본값, 사용자 승인): Room에는 epoch 밀리초 `Long`. `TIME_LINT`(팩 기본값, 사용자 승인): 레거시 시각 API는 detekt 설정으로 막는다. 후보 비교는 `research/time-handling.md`.

## 결정 매트릭스 — 시각 API

| 판단 기준 | A: `java.time` + `java.time.Clock` | B: `kotlin.time.Clock` + kotlinx-datetime | C: `Date`·`Calendar`·`System.currentTimeMillis()` | 기본값 |
|---|---|---|---|---|
| minSdk 26에서 desugaring | 불필요, 패키지가 "Added in API level 26" [S238](https://developer.android.com/reference/java/time/package-summary) | 불필요(API 26 이상) [S239](https://raw.githubusercontent.com/Kotlin/kotlinx-datetime/master/README.md) | 해당 없음 | A |
| 안정성 | 플랫폼 표준 | 0.8.0, README가 experimental이라 적음 [S239](https://raw.githubusercontent.com/Kotlin/kotlinx-datetime/master/README.md) | `Date`의 문자열 메서드는 deprecated [S240](https://developer.android.com/reference/java/util/Date) | A |
| Clock 주입 | `java.time.Clock`(API 26). `InstantSource`는 API 34라 불가 | `kotlin.time.Clock` | 직접 호출 | A |
| 추가 의존성 | 없음 | 있음 | 없음 | A |

## 규칙

### R-26-01 날짜·시각 타입은 `java.time`을 쓰고 desugaring은 켜지 않는다
- 규칙: 새 코드의 시점·날짜·기간은 `Instant`·`LocalDate`·`ZonedDateTime`·`Duration`·`Period` 같은 `java.time` 타입으로 표현한다. `kotlin.time.Clock`·`kotlinx-datetime`은 쓰지 않는다. minSdk 26이므로 시각 API만을 위해 core library desugaring을 켜지 않고, API 34가 필요한 `java.time.InstantSource`는 쓰지 않는다.
- 근거: `TIME_API` 결정이다. `java.time` 패키지는 API 26부터 있다 [S238](https://developer.android.com/reference/java/time/package-summary). desugaring은 minSdk 미만 기기에서 `java.time`을 쓰기 위한 도구다 [S139](https://developer.android.com/studio/write/java8-support). `InstantSource`는 "Added in API level 34"라 minSdk 26에서 쓸 수 없다 [S241](https://developer.android.com/reference/java/time/InstantSource). kotlinx-datetime의 README는 "Note that the library is experimental, and the API is subject to change."라고 적는다 [S239](https://raw.githubusercontent.com/Kotlin/kotlinx-datetime/master/README.md). 이 팩이 그 라이브러리를 채택하지 않는 이유는 이 문장이다.
- 예시:
  ```kotlin
  // Good
  data class Session(val startedAt: Instant, val timeout: Duration)
  // Bad: 시점을 Long 밀리초 맨몸으로 들고 다닌다
  data class Session(val startedAtMillis: Long, val timeoutMillis: Long)
  // Bad: API 34 타입
  class Timer(private val source: java.time.InstantSource)
  ```
- 체크: 새 코드에 `kotlinx.datetime`·`kotlin.time.Clock`·`InstantSource` import가 있는가. 시각 때문에 `coreLibraryDesugaring`을 추가했는가.

### R-26-02 `System.currentTimeMillis()`·`Date`·`Calendar`·`SimpleDateFormat`은 detekt 설정으로 막는다
- 규칙: 프로덕션 코드에서 네 API와 `Clock`을 받지 않는 시각 호출(인자 없는 `now()`, `Clock.systemDefaultZone()`)을 쓰지 않는다. 사람의 기억에 맡기지 않고 detekt `ForbiddenMethodCall`(`System.currentTimeMillis`, 값 끝에 `()`를 붙인 인자 없는 `now()`, 그리고 전체 이름으로 부르는 `Calendar.getInstance`·`Date`·`SimpleDateFormat` 생성 호출)과 `ForbiddenImport`(`java.util.Date`, `java.util.Calendar`, `java.text.SimpleDateFormat`) 설정으로 게이트(`scripts/check.sh`)에서 실패시킨다. 벽시계 값이 필요하면 `Clock`을 통해 얻는다(R-26-03).
- 근거: `TIME_LINT` 결정이다. Android 공식 문서에는 이 API들 전체를 쓰지 말라는 문장이 없다. `Date`의 문자열 포맷·파싱 메서드가 deprecated라는 문장만 있다 [S240](https://developer.android.com/reference/java/util/Date). `SimpleDateFormat`은 "Date formats are not synchronized. It is recommended to create separate format instances for each thread." 라고 스레드 안전하지 않음을 적는다 [S242](https://developer.android.com/reference/java/text/SimpleDateFormat). Android Lint에도 `System.currentTimeMillis`·`Calendar` 직접 사용을 잡는 점검이 없다 [S243](https://googlesamples.github.io/android-custom-lint-rules/checks/index.md.html). detekt에도 시각 전용 규칙은 없고, `ForbiddenMethodCall`은 `methods` 설정으로 임의 메서드를 막는다(기본 비활성, 타입 해석 필요) [S244](https://detekt.dev/docs/next/rules/style). 참조한 detekt 문서는 `next` 경로이지만 설정 자체는 detekt-cli 2.0.0-alpha.6을 `--analysis-mode full`로 샘플 코드에 돌려 확인했다(2026-09-30) — `methods`는 `reason`/`value` 형식이고, `ForbiddenImport`의 키는 `imports`가 아니라 `forbiddenImports`이며, 값 끝의 `()`가 인자 없는 오버로드만 골라 막는다. Gradle `detektDebug`로는 아직 돌려 보지 않았다.
- 예시:
  ```kotlin
  // Good
  val now: Instant = Instant.now(clock)
  // Bad
  val now = System.currentTimeMillis()
  val cal = java.util.Calendar.getInstance()
  ```
- 체크: `scripts/check.sh`가 위 API 호출·import에서 실패하는가. 예외를 두었다면 이유가 주석에 있는가.

### R-26-03 현재 시각은 `Clock`을 생성자로 받아 읽는다
- 규칙: 현재 시각이 필요한 클래스는 `java.time.Clock`을 생성자로 주입받고 `Instant.now(clock)`·`ZonedDateTime.now(clock)`처럼 읽는다. 인자 없는 `now()`와 `Clock.systemUTC()`를 로직 안에서 직접 부르지 않는다. 순수 함수는 `Clock`이 아니라 계산에 필요한 `Instant`를 인자로 받아도 된다.
- 근거: `java.time.Clock` 문서는 "Best practice for applications is to pass a Clock into any method that requires the current instant and time-zone."라고 적고, "This approach allows an alternative clock, such as fixed or offset to be used during testing."라고 이유를 든다 [S235](https://developer.android.com/reference/java/time/Clock). 디스패처를 생성자로 주입하는 규칙(R-14-08)과 같은 방식이라는 것은 팩 결정이다. 순수 함수가 `Instant` 인자를 받아도 된다는 허용은 팩 결정이다.
- 예시:
  ```kotlin
  // Good
  class SessionTracker @Inject constructor(private val clock: Clock) {
      fun start(): Session = Session(startedAt = Instant.now(clock))
  }
  // Bad: 테스트가 실제 시각에 묶인다
  class SessionTracker { fun start() = Session(startedAt = Instant.now()) }
  ```
- 체크: 인자 없는 `now()`·`Clock.system*` 호출이 DI 모듈 밖에 있는가.

### R-26-04 기본 `Clock`은 `:core:common`의 Hilt 모듈이 `@Provides`로 제공한다
- 규칙: `Clock`은 우리가 소유하지 않은 타입이므로 `:core:common`의 `di/` 패키지에 둔 모듈에서 `@Provides`로 제공한다(R-14-04·R-14-05). 바인딩이 하나뿐이므로 qualifier를 붙이지 않는다(R-14-07). 시스템 시계는 상태가 없으므로 스코프를 주지 않는다(R-14-06). 제공하는 `Clock`은 UTC 기준으로 하고, 지역 날짜·시간대가 필요한 계산은 표시 시점에 시간대를 붙인다(R-26-08).
- 근거: `@Provides`는 소유하지 않은 타입에 쓰고 `@Binds`는 인터페이스 바인딩에 쓴다 [S05](https://developer.android.com/training/dependency-injection/hilt-android). 디스패처 qualifier도 `:core:common`의 `di/`에 둔다(R-14-08). Clock 제공 위치와 UTC 기준은 팩 결정이다. 출처는 Clock을 어떻게 만들어 주입하라고 정하지 않는다. 시간대가 바뀔 수 있다는 이유는 `ZoneId.systemDefault()` 문서가 "If the system default time-zone is changed, then the result of this method will also change"라고 적기 때문이다 [S236](https://developer.android.com/reference/java/time/ZoneId). 이 모듈 코드를 실행으로 확인하지 않았다.
- 예시:
  ```kotlin
  // Good — :core:common 의 di/TimeModule.kt
  @Module @InstallIn(SingletonComponent::class)
  internal interface TimeModule {
      companion object {
          @Provides fun provideClock(): Clock = Clock.systemUTC()
      }
  }
  // Bad: 기능 모듈이 자기 안에서 시스템 시계를 만든다
  class FeatureModule { val clock: Clock = Clock.systemDefaultZone() }
  ```
- 체크: `Clock`을 제공하는 모듈이 `:core:common`에 하나뿐인가. 다른 모듈이 `Clock`을 직접 만드는가.

### R-26-05 시간 의존 테스트는 고정 `Clock`을 주입한다
- 규칙: 현재 시각에 의존하는 테스트는 `Clock.fixed(...)`(또는 `Clock.offset(...)`)를 생성자로 넣어 결과를 결정적으로 만든다. `runTest`의 가상 시간(`currentTime`)을 실제 날짜 계산에 쓰지 않는다. 실제 시각과 `Thread.sleep`으로 시간이 흐르기를 기다리는 테스트를 쓰지 않는다.
- 근거: `Clock` 문서가 테스트에서 fixed·offset 시계를 쓸 수 있다고 적는다 [S235](https://developer.android.com/reference/java/time/Clock). 디스패처를 주입해 테스트에서 교체하는 R-30-09와 같은 방향이다. 코루틴 테스트의 가상 시간(`TestCoroutineScheduler`)은 지연 건너뛰기를 위한 것이고 `currentTime`은 "The current virtual time in milliseconds."라고만 정의되어 실제 날짜와의 관계는 문서에 없다 [S259](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/-test-coroutine-scheduler/). 완성된 고정 시계 테스트 예시를 공식 문서에서 찾지 못했다. 위 두 번째·세 번째 문장은 팩 결정이다.
- 예시:
  ```kotlin
  // Good
  private val clock = Clock.fixed(Instant.parse("2026-01-31T00:00:00Z"), ZoneOffset.UTC)
  @Test fun start_recordsFixedInstant() =
      assertEquals(Instant.parse("2026-01-31T00:00:00Z"), SessionTracker(clock).start().startedAt)
  // Bad: 실제 시각에 기대어 가끔 실패한다
  assertTrue(SessionTracker(Clock.systemUTC()).start().startedAt <= Instant.now())
  ```
- 체크: 시간 의존 클래스의 테스트가 `Clock.systemUTC()`·`Instant.now()`를 쓰는가. 테스트에 `Thread.sleep`이 있는가.

### R-26-06 경과 시간은 `elapsedRealtime()`으로 재고 벽시계는 날짜가 필요한 곳에만 쓴다
- 규칙: 간격·타임아웃·경과 시간 측정은 `SystemClock.elapsedRealtime()`으로 한다. 벽시계(`Clock`이 돌려주는 시각)는 달력 날짜·표시·서버 시각처럼 실제 날짜와 시각의 대응이 중요한 곳에만 쓰고, 두 시각의 차로 경과 시간을 구하지 않는다. 슬립을 넘지 않는 짧은 측정에만 `uptimeMillis()`·`System.nanoTime()`을 쓴다.
- 근거: `SystemClock`은 "Three different clocks are available, and they should not be confused"라고 시작한다. 벽시계는 "the time may jump backwards or forwards unpredictably"이고 "Interval or elapsed time measurements should use a different clock."이다. `elapsedRealtime()`은 "guaranteed to be monotonic, and continues to tick even when the CPU is in power saving modes, so is the recommend basis for general purpose interval timing"이다(원문 표기 그대로). `uptimeMillis()`는 "This clock stops when the system enters deep sleep"이며 "suitable for interval timing when the interval does not span device sleep"이다 [S237](https://developer.android.com/reference/android/os/SystemClock). 단위 테스트에서 `SystemClock` 호출을 어떻게 대체할지는 출처가 침묵한다.

  | 시계 | API | 특성 | 쓰는 곳 |
  |---|---|---|---|
  | 벽시계 | `Clock`(내부적으로 `currentTimeMillis`) | 사용자·통신망이 바꿔 점프할 수 있음 | 실제 날짜·시각이 필요한 곳 |
  | 단조(슬립 제외) | `uptimeMillis()`, `System.nanoTime()` | 단조, 깊은 슬립 중 정지 | 슬립을 넘지 않는 간격 |
  | 부팅 후 시간 | `elapsedRealtime()` | 단조, 슬립 중에도 진행 | 일반 간격 측정 |
- 예시:
  ```kotlin
  // Good
  val start = SystemClock.elapsedRealtime()
  val elapsed = (SystemClock.elapsedRealtime() - start).milliseconds
  // Bad: 사용자가 시계를 바꾸면 음수·급증
  val elapsed = Instant.now(clock).toEpochMilli() - startedAtMillis
  ```
- 체크: 경과 시간·타임아웃 계산에 벽시계 두 값의 차를 쓰는가. `System.nanoTime()`·`uptimeMillis()` 사용처가 슬립을 넘지 않는 측정인가.

### R-26-07 "정확히 24시간"은 `Duration`, "하루"는 `Period`로 구분한다
- 규칙: 정확한 초 단위 길이가 의미인 경우(만료 시각, 타임아웃)는 `Duration`을, 달력의 하루·한 달이 의미인 경우(다음 날 같은 시각, 다음 달 같은 날)는 `Period`나 `ZonedDateTime.plusDays`를 쓴다. `Duration.ofDays`를 "다음 날"로 쓰지 않는다.
- 근거: `Duration`은 "the DAYS unit can be used and is treated as exactly equal to 24 hours, thus ignoring daylight savings effects"라 적는다 [S245](https://developer.android.com/reference/java/time/Duration). 두 클래스의 차이는 "A Duration will add an exact number of seconds, thus a duration of one day is always exactly 24 hours. By contrast, a Period will add a conceptual day, trying to maintain the local time."이다 [S246](https://developer.android.com/reference/java/time/Period). `ZonedDateTime.plusDays`는 지역 시간선 위에서 날짜를 더한다 [S247](https://developer.android.com/reference/java/time/ZonedDateTime). DST 겹침·갭 처리 세부는 이 문서가 다루지 않는다(문서를 그대로 따른다).
- 예시:
  ```kotlin
  // Good
  val expiresAt = issuedAt + Duration.ofHours(24)            // 정확히 24시간
  val sameTimeTomorrow = zoned.plus(Period.ofDays(1))         // 지역 시각 유지
  // Bad: DST 전환일에 시각이 한 시간 밀린다
  val sameTimeTomorrow = zoned.plus(Duration.ofDays(1))
  ```
- 체크: `Duration.ofDays`가 달력상 "다음 날" 계산에 쓰였는가.

### R-26-08 지역 날짜 경계는 표시 시점의 시간대로 `atStartOfDay(zone)`을 만들고 반개구간으로 다룬다
- 규칙: "오늘"·"이번 주" 같은 지역 날짜 범위는 `Instant`를 표시 시점의 `ZoneId`로 `LocalDate`로 바꿔 계산한다. 하루의 시작은 `LocalDate.atStartOfDay(zone)`이 돌려주는 `ZonedDateTime`을 그대로 쓰고 자정이라고 가정하지 않는다. 하루의 끝은 다음 날 `atStartOfDay(zone)`을 미포함 끝으로 하는 반개구간 `[시작, 다음 날 시작)`으로 표현한다. 시간대는 `Clock`이 아니라 계산 시점에 얻는다(R-26-04). 저장 값에는 시간대를 섞지 않는다(R-26-10).
- 근거: `atStartOfDay(ZoneId)` 문서는 "Time-zone rules, such as daylight savings, mean that not every local date-time is valid for the specified zone, thus the local date-time may not be midnight."라고 경고한다 [S248](https://developer.android.com/reference/java/time/LocalDate). `ChronoUnit.between`과 `LocalDate.until`의 끝 인자는 이름이 `Exclusive`라 끝을 포함하지 않는다 [S249](https://developer.android.com/reference/java/time/temporal/ChronoUnit). 반개구간으로 하루를 표현하라는 명시 문장은 java.time에 없고 이 규칙은 그 매개변수 이름에서 이끈 팩 결정이다.
- 예시:
  ```kotlin
  // Good
  val zone = ZoneId.systemDefault()
  val today = Instant.now(clock).atZone(zone).toLocalDate()
  val start = today.atStartOfDay(zone).toInstant()
  val endExclusive = today.plusDays(1).atStartOfDay(zone).toInstant()
  fun contains(t: Instant) = t >= start && t < endExclusive
  // Bad: 하루를 24시간으로 가정하고 끝을 포함한다
  val end = start.plus(Duration.ofHours(24)); fun contains(t: Instant) = t in start..end
  ```
- 체크: 하루의 끝을 `start + 24h`로 만드는가. 범위 판정이 `..`(닫힌 구간)으로 되어 있어 자정 이벤트가 두 날에 잡히는가.

### R-26-09 월 단위 계산은 `YearMonth`·`atEndOfMonth`를 쓰고 `plusMonths`의 말일 보정을 전제한다
- 규칙: 월 단위 값은 `YearMonth`로 다루고, 월말은 `YearMonth.atEndOfMonth()`로 구한다. 일수를 손으로 세거나(28~31) `plusDays(30)`으로 한 달을 흉내 내지 않는다. `LocalDate.plusMonths`는 결과 월에 없는 날이면 그 달의 마지막 날로 자르므로, 월말에 한 달을 더한 뒤 다시 빼서 원래 날짜가 돌아온다고 가정하지 않는다.
- 근거: `YearMonth.atEndOfMonth()`는 "The day-of-month is set to the last valid day of the month, taking into account leap years"이다 [S250](https://developer.android.com/reference/java/time/YearMonth). `LocalDate.plusMonths`는 2007-03-31에 한 달을 더하면 "the last valid day of the month, 2007-04-30, is selected instead"이다 [S248](https://developer.android.com/reference/java/time/LocalDate). 되돌려 빼면 원래 날짜가 안 돌아온다는 문장은 없고 클램프 동작만 명시되어 있다(요약). "월 값은 `YearMonth`로 다룬다"는 팩 결정이다.
- 예시:
  ```kotlin
  // Good
  val month = YearMonth.from(date)
  val last = month.atEndOfMonth()
  val next = month.plusMonths(1)
  // Bad
  val last = date.withDayOfMonth(30)   // 2월·31일 달에 틀린다
  ```
- 체크: 월말·다음 달 계산에 하드코딩된 일수(28·30·31)가 있는가.

### R-26-10 Room에는 시점을 epoch 밀리초 `Long`으로 저장한다
- 규칙: 시점(`Instant`)은 `toEpochMilli()`로 바꾼 `Long` 컬럼에 저장하고 변환은 컨버터 한 곳에 모은다. 시간대 ID나 `LocalDateTime` 문자열을 시점 대신 저장하지 않는다. 컨버터 어노테이션은 Room3의 `@ColumnTypeConverter`를 쓴다.
- 근거: `TIME_STORAGE` 결정이다. Room 문서의 타입 컨버터 예시는 "Room can't natively persist Date objects, so you need to define type converters"에 이은 `Date`↔`Long`뿐이고 `Instant`·`LocalDate`·`LocalDateTime` 저장을 언급하지 않는다 [S251](https://developer.android.com/training/data-storage/room/referencing-data). 컨버터 어노테이션은 Room3 릴리스 노트가 "Rename @TypeConverter to @ColumnTypeConverter to better distinguish the scope of the conversion"이라 적는다 [S252](https://developer.android.com/jetpack/androidx/releases/room3). `LocalDateTime`은 "It cannot represent an instant on the time-line without additional information such as an offset or time-zone." [S253](https://developer.android.com/reference/java/time/LocalDateTime)이고, `Instant`는 "This might be used to record event time-stamps in the application." [S254](https://developer.android.com/reference/java/time/Instant)이다. Room 3 컨버터 코드를 실행으로 확인하지 않았다.
- 예시:
  ```kotlin
  // Good
  class InstantConverters {
      @ColumnTypeConverter fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()
      @ColumnTypeConverter fun toInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)
  }
  // Bad: 시점을 지역 시각 문자열로 저장한다
  val createdAt: String = LocalDateTime.now(clock).toString()
  ```
- 체크: 엔티티의 시각 컬럼이 `Long`(또는 컨버터로 `Instant`)인가. `LocalDateTime`만으로 시점을 저장한 컬럼이 있는가.

### R-26-11 문자열로 주고받는 시점은 ISO-8601 표기를 쓰고 직접 만든 패턴을 쓰지 않는다
- 규칙: 파일·로그·네트워크로 시점을 문자열로 내보낼 때는 `Instant.toString()`이 만드는 ISO-8601 표기를 쓴다. `SimpleDateFormat`이나 `ofPattern`으로 기계용 표기를 새로 정의하지 않는다. 기계용 문자열을 파싱·포맷해야 해서 로캘 지정 API를 쓰면 `Locale.US`를 명시한다.
- 근거: `Instant.toString()`은 ISO-8601 표기를 돌려준다 [S254](https://developer.android.com/reference/java/time/Instant). Lint `SimpleDateFormat` 설명은 기계 가독 형식을 만들려면 "you almost certainly want to explicitly ask for US to ensure that you get ASCII digits (rather than, say, Arabic digits)"라고 적는다 [S258](https://googlesamples.github.io/android-custom-lint-rules/checks/SimpleDateFormat.md.html). 서버 API와 오프셋 표기에 대한 Android 공식 규범은 찾지 못했고, 서버가 오프셋 표기를 보낼 때 `Instant.parse`가 받는지는 확인하지 않았다(출처가 침묵하는 것). ISO-8601을 쓰자는 선택은 팩 결정이다.
- 예시:
  ```kotlin
  // Good
  val wire: String = instant.toString()   // 2026-09-30T01:02:03Z
  // Bad: 기계용 표기를 임의 패턴으로 만든다
  val wire = SimpleDateFormat("yyyy/MM/dd HH:mm").format(Date())
  ```
- 체크: 저장·전송용 문자열을 만드는 패턴 리터럴이 있는가.

### R-26-12 화면에 보이는 날짜·시각은 로캘 포맷터로 만들고 패턴을 하드코딩하지 않는다
- 규칙: 화면 표시는 `DateTimeFormatter.ofLocalizedDate`·`ofLocalizedTime`·`ofLocalizedDateTime`, `DateFormat.getBestDateTimePattern`, `DateUtils.formatDateTime` 중 하나로 만든다. 화면 문자열에 `"yyyy-MM-dd"` 같은 고정 패턴을 쓰지 않는다. 사용자의 12/24시간제 설정을 따라야 하는 표시는 `DateFormat`·`DateUtils`의 시간 포맷을 쓴다.
- 근거: `DateTimeFormatter`는 "the ofLocalizedDate provides a formatter that uses the locale specific date format"이라 적고 [S255](https://developer.android.com/reference/java/time/format/DateTimeFormatter), `DateFormat.getBestDateTimePattern`은 "Returns the best possible localized form of the given skeleton for the given locale."이다 [S256](https://developer.android.com/reference/android/text/format/DateFormat). Android의 ICU는 사용자의 12/24시간제 설정을 따르지 않으며 설정을 따르려면 `DateFormat`·`DateUtils`를 쓰라고 문서가 적는다 [S256](https://developer.android.com/reference/android/text/format/DateFormat). `DateTimeFormatter`가 그 설정을 따르는지는 문서에 없고 실기기로도 확인하지 않았다. 화면에 고정 패턴을 금지하는 명시 문장은 없고 Lint `SimpleDateFormat` 설명이 가장 가깝다 [S258](https://googlesamples.github.io/android-custom-lint-rules/checks/SimpleDateFormat.md.html). 규칙 자체는 팩 결정이다.
- 예시:
  ```kotlin
  // Good
  val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
  val label = formatter.format(localDate)
  // Bad: 로캘과 무관하게 같은 순서로 보인다
  val label = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDate)
  ```
- 체크: 화면에 쓰이는 문자열이 `ofPattern` 리터럴에서 나오는가. 시각 표시에 12/24시간제 설정을 반영해야 하는 화면이 `DateFormat`·`DateUtils`를 쓰는가.

### R-26-13 예약 작업의 시계는 간격형이면 `ELAPSED_REALTIME`, 날짜형이면 `RTC`를 고른다
- 규칙: "30초마다"처럼 시간 경과에 기반한 알람은 `ELAPSED_REALTIME(_WAKEUP)`을, "매일 오전 7시"처럼 실제 날짜와 로캘에 의존하는 알람은 `RTC(_WAKEUP)`를 쓴다. 알람은 재부팅하면 사라지므로 재부팅 뒤 다시 등록하는 경로를 함께 둔다. 프로세스 사망을 넘겨야 하는 작업은 R-15-09의 WorkManager를 우선한다.
- 근거: `RTC`·`RTC_WAKEUP`은 "Alarm time in System.currentTimeMillis() (wall clock time in UTC)"이다 [S257](https://developer.android.com/reference/android/app/AlarmManager). 가이드는 경과 시간 기준이 시간대·로캘의 영향을 받지 않아 시간 경과 기반 알람에 맞고, RTC는 로캘에 의존하는 알람에 낫다고 하며, 알람은 "will be cleared if it is turned off and rebooted"라고 적는다 [S201](https://developer.android.com/develop/background-work/services/alarms). 이 규칙은 정확한 알람(`SCHEDULE_EXACT_ALARM`)을 다루지 않는다. WorkManager의 시간대·시계 변경 처리는 출처가 침묵한다. WorkManager 우선은 R-15-09를 따른 팩 결정이다.
- 예시:
  ```kotlin
  // Good
  alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtElapsed, 30_000L, pi)
  // Bad: 간격형 알람에 벽시계를 쓰면 사용자가 시계를 바꿀 때 어긋난다
  alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + 30_000L, 30_000L, pi)
  ```
- 체크: 간격형 알람이 `RTC`를 쓰는가. 날짜형 알람이 재부팅 뒤 다시 등록되는가.

## 출처가 침묵하는 것 (규칙으로 쓰지 않음)

- **시간대·시계 변경 시 앱의 행동**: `SystemClock`은 `ACTION_TIME_CHANGED`·`ACTION_TIMEZONE_CHANGED` 브로드캐스트를 "consider listening" 하라고만 하고, 암시적 브로드캐스트 예외 목록 문서는 시계 앱이 알람을 갱신할 때 필요하다는 예와 함께 "avoid registering listeners"를 적는다. 무엇을 다시 계산할지는 출처에 없어 규칙으로 쓰지 않는다.
- **`ZoneId.systemDefault()` 저장 금지 여부**: 저장하지 말라는 문장은 없고 결과가 바뀔 수 있다는 사실만 있다(R-26-04는 이 사실을 Clock 제공 방식의 이유로만 쓴다).
- **먼 미래의 지역 시각 이벤트**: 대안으로 `LocalDateTime`과 시간대를 따로 저장하라는 권고는 kotlinx-datetime README의 것이다. 이 팩이 채택하지 않은 라이브러리의 문서라 규칙으로 옮기지 않았다.
- **서버 시각 표기(UTC 오프셋 포함 여부)**: Android·JetBrains 공식 규범이 없다. 서버가 오프셋 표기를 보낼 때 `java.time.Instant.parse`가 받는지도 확인하지 않았다.
- **Room의 `LocalDate`·`YearMonth` 저장**: Room 문서는 `Date`↔`Long` 예시뿐이다. R-26-10은 `Instant`만 정한다.
- **`DateTimeFormatter`의 12/24시간제 반영**: 문서에 없고 실기기 검증도 하지 않았다.
- **`SystemClock` 호출의 단위 테스트 대체**: 공식 테스트 문서에 시계·현재 시각 언급이 없다. 고정 시계 테스트의 공식 완성 예시도 없다.
- **`TimeSource.Monotonic`의 Android 구현**, **`runTest`의 `currentTime`과 실제 날짜의 관계**, **반개구간·오프-바이-원을 경고하는 java.time 문장**: 출처에 없다.
- **`System.currentTimeMillis`·`Calendar`를 잡는 내장 lint·detekt 규칙**: 없다. R-26-02는 설정으로 만드는 방식이다.
- **Konsist 시각 규칙, DataStore의 시각 저장**: 조사하지 않았다.
