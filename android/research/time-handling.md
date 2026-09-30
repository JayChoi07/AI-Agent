# 시간 다루기(시계 주입·시간대·기간 계산·저장 형식) 출처 조사 노트
조사일: 2026-09-30

범위: 규칙 문장은 쓰지 않는다. 인용부호 안의 문장은 받은 페이지 본문(HTML 태그 제거 텍스트, 공백 무시 비교)에 글자 그대로 있는 것만 썼고, 스크립트로 재확인했다. 그렇지 않은 것은 "요약"으로 적었다. 버전·날짜의 확인일은 모두 2026-09-30이다.
전제: 팩 확정 버전 AGP 9.4.0 · Kotlin 2.4.20 · kotlinx.coroutines 1.11.0 · compileSdk·targetSdk 37 · **minSdk 26**.

## 출처
| # | 조직 | 문서명 | URL | 종류 | 갱신일/버전 |
|---|---|---|---|---|---|
| S1 | Google | Use Java 8+ language features and APIs (desugaring) | https://developer.android.com/studio/write/java8-support | 공식 가이드 | 미표기 |
| S2 | Google | Java 8+ APIs available through desugaring / Java 11+ default 표 | https://developer.android.com/studio/write/java8-support-table , https://developer.android.com/studio/write/java11-default-support-table | 공식 가이드 | 미표기 |
| S3 | Google | java.time 패키지, Clock, InstantSource, Period, Duration, ZonedDateTime, ZoneId, LocalDate, LocalDateTime, Instant, YearMonth, ChronoUnit, TemporalAdjusters, DateTimeFormatter (Android API 레퍼런스) | https://developer.android.com/reference/java/time/package-summary 및 같은 경로의 `Clock` `InstantSource` `Period` `Duration` `ZonedDateTime` `ZoneId` `LocalDate` `LocalDateTime` `Instant` `YearMonth` , https://developer.android.com/reference/java/time/temporal/ChronoUnit , https://developer.android.com/reference/java/time/temporal/TemporalAdjusters , https://developer.android.com/reference/java/time/format/DateTimeFormatter | 공식 API 레퍼런스 | 패키지 API 26, `InstantSource` API 34 |
| S4 | Google | SystemClock (android.os) | https://developer.android.com/reference/android/os/SystemClock | 공식 API 레퍼런스 | 미표기 (`currentNetworkTimeClock` API 33, `currentGnssTimeClock` API 29) |
| S5 | Google | Intent 레퍼런스(`ACTION_TIMEZONE_CHANGED` 등) / Implicit broadcast exceptions | https://developer.android.com/reference/android/content/Intent , https://developer.android.com/develop/background-work/background-tasks/broadcasts/broadcast-exceptions | 공식 문서 | 미표기 |
| S6 | Google | android.text.format.DateFormat / DateUtils / Unicode and internationalization support | https://developer.android.com/reference/android/text/format/DateFormat , https://developer.android.com/reference/android/text/format/DateUtils , https://developer.android.com/guide/topics/resources/internationalization.html | 공식 문서 | 미표기 |
| S7 | Google | java.text.SimpleDateFormat / java.text.DateFormat / java.util.Date / java.util.Calendar | https://developer.android.com/reference/java/text/SimpleDateFormat , https://developer.android.com/reference/java/text/DateFormat , https://developer.android.com/reference/java/util/Date , https://developer.android.com/reference/java/util/Calendar | 공식 API 레퍼런스 | 미표기 |
| S8 | Google (Android Lint) | Lint 점검 `SimpleDateFormat`·`DefaultLocale`·`NewApi` 및 점검 목록 | https://googlesamples.github.io/android-custom-lint-rules/checks/SimpleDateFormat.md.html , https://googlesamples.github.io/android-custom-lint-rules/checks/DefaultLocale.md.html , https://googlesamples.github.io/android-custom-lint-rules/checks/NewApi.md.html , https://googlesamples.github.io/android-custom-lint-rules/checks/index.md.html | 공식 문서(lint 규칙집) | 미표기 |
| S9 | Google | Room — 복합 데이터 참조(타입 컨버터) / Room 릴리스 노트 / Room3 릴리스 노트 | https://developer.android.com/training/data-storage/room/referencing-data , https://developer.android.com/jetpack/androidx/releases/room , https://developer.android.com/jetpack/androidx/releases/room3 | 공식 가이드·릴리스 노트 | Room 2.8.5 안정(2026-09-09) / Room3 3.0.3 안정(2026-09-09) |
| S10 | Google | AlarmManager 레퍼런스 / Schedule alarms 가이드 | https://developer.android.com/reference/android/app/AlarmManager , https://developer.android.com/develop/background-work/services/alarms | 공식 문서 | 미표기 |
| S11 | Google | WorkManager — PeriodicWorkRequest 레퍼런스 / Define your work / Manage work | https://developer.android.com/reference/androidx/work/PeriodicWorkRequest , https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work , https://developer.android.com/develop/background-work/background-tasks/persistent/how-to/manage-work | 공식 문서 | 미표기 |
| S12 | Google | Test Kotlin coroutines on Android / 테스트 기초·더블·로컬 테스트 | https://developer.android.com/kotlin/coroutines/test , https://developer.android.com/training/testing/fundamentals , https://developer.android.com/training/testing/fundamentals/test-doubles , https://developer.android.com/training/testing/local-tests | 공식 가이드 | 미표기 |
| S13 | Google | Compose 시간 선택기(Time pickers) | https://developer.android.com/develop/ui/compose/components/time-pickers | 공식 가이드 | 미표기 |
| S14 | Google (AOSP) | Time Zone Data(모듈) / Time zone rules / Time zone policy and recommendations | https://source.android.com/docs/core/ota/modular-system/timezone , https://source.android.com/docs/core/permissions/timezone-rules , https://source.android.com/docs/core/connect/time/time-zone-policy-recommendations | 공식 문서 | Time Zone Data 페이지 2026-06-17 |
| S15 | JetBrains | kotlinx-datetime README / Releases | https://raw.githubusercontent.com/Kotlin/kotlinx-datetime/master/README.md , https://github.com/Kotlin/kotlinx-datetime/releases | 오픈소스 README·릴리스 노트 | 0.8.0 (2026-05-07) |
| S16 | JetBrains | Kotlin 2.3 What's new / stdlib API `kotlin.time.Clock`·`Instant`·`TimeSource.Monotonic` | https://kotlinlang.org/docs/whatsnew23.html , https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.time/-clock/ , https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.time/-instant/ , https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.time/-time-source/-monotonic/ | 공식 문서 | Kotlin 2.3(안정화) / 2.4 API |
| S17 | JetBrains | kotlinx.serialization Releases | https://github.com/Kotlin/kotlinx.serialization/releases | 릴리스 노트 | 1.9.0(2025-06-27, Instant 직렬화기 추가) / 1.11.0(2026-04-09) |
| S18 | JetBrains | kotlinx-coroutines-test README / `TestCoroutineScheduler` / `runTest` / `testTimeSource` | https://raw.githubusercontent.com/Kotlin/kotlinx.coroutines/master/kotlinx-coroutines-test/README.md , https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/-test-coroutine-scheduler/ , https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/run-test.html , https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/test-time-source.html | 공식 문서 | 1.11.0 계열 |
| S19 | detekt | 규칙셋 문서 style·potential-bugs·coroutines (`docs/next`) / Releases | https://detekt.dev/docs/next/rules/style , https://detekt.dev/docs/next/rules/potential-bugs , https://detekt.dev/docs/next/rules/coroutines , https://github.com/detekt/detekt/releases | 공식 문서·릴리스 노트 | 2.0.0-alpha.6 (2026-08-04). 문서 경로가 `next`라 alpha.6과 글자 단위 일치는 미확인 |
| S20 | Oracle | java.time Javadoc (JDK 17) — 대조용 | https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/package-summary.html | 공식 API 문서 | JDK 17. 인용은 S3(Android 레퍼런스)에서 확인한 문장만 사용 |
| S21 | Google | Support different languages and cultures(multilingual support) | https://developer.android.com/guide/topics/resources/multilingual-support | 공식 가이드 | 미표기 |

## 핵심 내용 (출처별)

### 1. API 선택
- java.time은 Android 네이티브 API 26+이다. Android 레퍼런스의 패키지 페이지에 "Added in API level 26"이 표기된다. [S3] minSdk 26이므로 desugaring 없이 쓴다.
- desugaring은 minSdk 미만에서 java.time을 쓰기 위한 것이다. desugaring 페이지의 지원 목록에 "A subset of java.time"이 있다. [S1] kotlinx-datetime README도 "If you target Android devices running below API 26, you need to use Android Gradle plugin 4.0 or newer and enable core library desugaring"라고 적는다. [S15]
- `java.time.InstantSource`는 "Added in API level 34"이고 [S3], Java 8+·Java 11 default desugaring 표에는 `InstantSource`가 없다(표 텍스트에서 0회 검색). [S2] 요약: minSdk 26에서 `InstantSource`는 못 쓰고 `java.time.Clock`(API 26)은 쓸 수 있다.
- kotlinx-datetime은 현재 0.8.0(2026-05-07)이며 1.0이 아니다. README에 "Note that the library is experimental, and the API is subject to change."가 있고 상단 배지가 Kotlin 컴포넌트 안정성 표의 Alpha다. Kotlin stdlib 2.3.21 이상과 호환된다. [S15]
- 두 라이브러리 관계(JetBrains 문장): README 요약: JVM에서는 kotlinx-datetime 타입 구현이 `java.time` API에 기댄다(그 외 플랫폼은 ThreeTen 백포트 기반). 설계 원칙 문장: "The library puts a clear boundary between the physical time of an instant and the local, time-zone-dependent civil time". [S15] `Instant`·`Clock`은 kotlinx-datetime 0.7.0부터 stdlib(`kotlin.time`)로 옮겨졌다. [S15][S16]
- `kotlin.time.Clock`·`kotlin.time.Instant`는 Kotlin 2.3.0에서 안정화됐다: "Kotlin 2.3.0 stabilizes the new time tracking functionality, kotlin.time.Clock and kotlin.time.Instant". [S16]
- 레거시 `Date`: Android 레퍼런스에 "The class DateFormat class should be used to format and parse date strings. The corresponding methods in Date are deprecated."가 있다(Date의 문자열 파싱·포맷 메서드가 deprecated라는 뜻). [S7] `Date`·`Calendar`·`System.currentTimeMillis()` 전체를 쓰지 말라는 Android 공식 문장은 찾지 못했다.
- Android 공식 Compose 예제는 `Calendar.getInstance()`를 쓰고 "Enable Java 8+ API desugaring in your project to alternatively use java.time.LocalTime on all Android versions."라고만 적는다(권고가 아니라 대안 제시). [S13]
- `SimpleDateFormat`은 스레드 안전하지 않다: "Date formats are not synchronized. It is recommended to create separate format instances for each thread." [S7]

### 2. Clock 주입
- java.time.Clock 문장(Android 레퍼런스): "Best practice for applications is to pass a Clock into any method that requires the current instant and time-zone." "Applications use an object to obtain the current time rather than a static method. This can simplify testing." 그리고 "This approach allows an alternative clock, such as fixed or offset to be used during testing." [S3]
- Kotlin `kotlin.time.Clock` 문장: "It is not recommended to use Clock.System directly in the implementation. Instead, you can pass a Clock explicitly to the necessary functions or classes. This way, tests can be written deterministically by providing custom Clock implementations to the system under test." [S16] — "현재 시각을 직접 부르지 말라"는 공식 권고 문장은 이 둘이다.
- Android 공식 테스트 문서(테스트 기초·더블·로컬 테스트·코루틴 테스트)에는 시계·현재 시각 관련 언급이 없다("clock", "current time" 검색 0회). [S12]
- coroutines-test: 가상 시간은 `TestCoroutineScheduler`가 담당한다("The shared source of virtual time, used for controlling execution order and skipping delays."). `TestScope.testTimeSource`는 `TimeSource`이지 `Clock`이 아니며 `@ExperimentalCoroutinesApi`다. 스케줄러의 `currentTime`은 "The current virtual time in milliseconds."라고만 정의돼 있고 실제 날짜와의 관계는 언급이 없다. [S18]

### 3. 경과 시간 측정 (세 시계)
- SystemClock 페이지 구조: "Three different clocks are available, and they should not be confused". [S4]
- 벽시계: "System.currentTimeMillis() is the standard "wall" clock (time and date) expressing milliseconds since the epoch. The wall clock can be set by the user or the phone network (see setCurrentTimeMillis(long) ), so the time may jump backwards or forwards unpredictably. This clock should only be used when correspondence with real-world dates and times is important, such as in a calendar or alarm clock application. Interval or elapsed time measurements should use a different clock." [S4]
- `uptimeMillis()`: "This clock stops when the system enters deep sleep" 이며 "guaranteed to be monotonic, and is suitable for interval timing when the interval does not span device sleep". 같은 문단이 `System.nanoTime()`의 기반이라고도 적는다("Thread.sleep(millis) , Object.wait(millis) , and System.nanoTime()"). [S4]
- `elapsedRealtime()`: "This clock is guaranteed to be monotonic, and continues to tick even when the CPU is in power saving modes, so is the recommend basis for general purpose interval timing." (원문 오타 "recommend" 그대로) [S4]
- 시계 변경 감지: "If you are using System.currentTimeMillis(), consider listening to the ACTION_TIME_TICK , ACTION_TIME_CHANGED and ACTION_TIMEZONE_CHANGED Intent broadcasts to find out when the time changes." [S4]
- 사용자가 조정할 수 없는 벽시계 대안: `SystemClock.currentNetworkTimeClock()`(API 33) — "While the time returned by System.currentTimeMillis() can be adjusted by the user, the time returned by this method cannot be adjusted by the user." 단 "the accuracy of the returned times cannot be guaranteed"이고 "the returned time should not be used for security purposes"이며 "it will either return a valid time or throw"(동기화 전 예외). `currentGnssTimeClock()`(API 29)은 위치 fix 전에 `DateTimeException`. [S4]
- `kotlin.time.TimeSource.Monotonic`: "This time source returns its readings from a source of monotonic time when it is available in a target platform, and resorts to a non-monotonic time source otherwise." Since Kotlin 1.9. [S16] Android에서 무엇을 쓰는지는 문서에 없다(미확인).
- 자동 시간 보정·사용자 시계 변경 때의 세부 동작은 SystemClock 페이지의 위 문장("set by the user or the phone network")이 전부다. [S4]

### 4. 시간대
- `ZoneId.systemDefault()`: "If the system default time-zone is changed, then the result of this method will also change" — 저장하지 말라는 문장은 없고 결과가 바뀐다는 사실만 적는다. `ZoneId`는 규칙이 아니라 ID이며 "Time-zone rules are defined by governments and change frequently"라고 한다. [S3]
- 미래 일정 문장(kotlinx-datetime): 먼 미래에 특정 지역 시각에 일어날 이벤트는 `LocalDateTime`으로 두고 `TimeZone`을 따로 관리하라. "Try to avoid converting future events to Instant in advance, because time zone rules might change unexpectedly in the future." 과거 사건·가까운 미래의 확정 시점은 `Instant`. 같은 절에서 `LocalDate`는 birth date, `YearMonth`는 credit card expiration date 예시. [S15]
- 시간대 변경 브로드캐스트: `ACTION_TIMEZONE_CHANGED` = "Broadcast Action: The timezone has changed."(extra `EXTRA_TIMEZONE`), `ACTION_TIMEZONE_OFFSET_CHANGED` = "Indicates that the system's time zone offset has changed without the time zone having changed.", `ACTION_TIME_CHANGED` = "Broadcast Action: The time was set." [S5]
- 이 브로드캐스트들은 암시적 브로드캐스트 제한의 예외 목록에 있다: "Clock apps might need to receive these broadcasts to update alarms when the time, timezone, or alarms change." 같은 페이지 주의: "avoid registering listeners for them". [S5]
- 앱이 해야 할 일에 대한 공식 문장은 위 둘(SystemClock의 "consider listening", 예외 목록)뿐이다. 시간대 변경 시 무엇을 다시 계산해야 하는지 지침은 없다.
- tzdata 갱신: "The Time Zone Data module updates daylight saving time (DST) and time zones on Android devices"; APEX 형식이며 "available for devices running Android 10 or higher". 절차: "The end-user device downloads the update, reboots, then applies the changes". [S14] 요약: Android 10+에서는 Mainline 모듈 업데이트로 전달되는 것이 표준 경로이고 앱이 tzdata를 직접 갱신하는 방법은 문서에 없다. kotlinx-datetime은 기본으로 시스템 정보를 쓰며 "This information may be severely outdated"라 적고, 최신 DB를 번들하는 `kotlinx-datetime-zoneinfo` 아티팩트를 별도로 제공한다(`2026d-spi.0.8.0`). [S15]
- DST 경계의 "하루" 길이: `Duration`은 "the DAYS unit can be used and is treated as exactly equal to 24 hours, thus ignoring daylight savings effects"이고, `Period`와 `Duration`의 차이는 "A Duration will add an exact number of seconds, thus a duration of one day is always exactly 24 hours. By contrast, a Period will add a conceptual day, trying to maintain the local time." [S3] `ZonedDateTime.plusDays`: "This operates on the local time-line, adding days to the local date-time." 같은 절에 겹침(overlap)은 이른 오프셋 유지, 갭(gap)은 갭 길이만큼 앞으로 조정한다고 적혀 있다. [S3]
- `LocalDate.atStartOfDay(ZoneId)`: "Time-zone rules, such as daylight savings, mean that not every local date-time is valid for the specified zone, thus the local date-time may not be midnight." [S3]

### 5. 저장 형식
- Room 안정 버전이 둘 공존한다: `androidx.room` 2.8.5, `androidx.room3` 3.0.3(둘 다 2026-09-09). [S9] 공식 타입 컨버터 문서의 예시는 `Date`↔`Long`이다("Room can't natively persist Date objects, so you need to define type converters"). 이 문서의 어노테이션은 `@ColumnTypeConverter`/`@ColumnTypeConverters`이고 Room3 릴리스 노트에 "Rename @TypeConverter to @ColumnTypeConverter to better distinguish the scope of the conversion"이 있다(요약: 문서 예시는 Room3 기준). **Room 문서는 `Instant`·`LocalDate`·`LocalDateTime` 저장을 언급하지 않는다**(검색 0회). [S9]
- `LocalDateTime`은 시점이 아니다: "It cannot represent an instant on the time-line without additional information such as an offset or time-zone." `Instant`는 "This might be used to record event time-stamps in the application." [S3]
- `Instant.toString()`은 ISO-8601 표기다("A string representation of this instant using ISO-8601"). [S3] Kotlin: "parse and toString methods can be used to obtain an Instant from and convert it to a string in the ISO 8601 extended format , which includes a time zone designator." 예: `Instant.parse("2023-01-02T22:35:01+01:00").toString()`은 `2023-01-02T21:35:01Z`. [S16] 즉 오프셋 표기 입력을 받아들이고 출력은 UTC(Z)로 정규화된다.
- kotlinx.serialization은 1.9.0부터 `kotlin.time.Instant` 직렬화기를 제공한다: "You can choose between the default `InstantSerializer`, which uses its string representation, or specify `InstantComponentSerializer` that represents instant as its components." (Kotlin 2.2 필요) [S17]
- kotlinx-datetime의 기준 표준: "The library is based on the ISO 8601 international standard, other ways to represent dates and times are out of its scope." [S15]
- 서버 API와 UTC 오프셋 표기에 대한 Android·JetBrains 공식 규범 문장은 찾지 못했다. DataStore 문서의 시각 저장 언급은 열어보지 않았다(미확인).

### 6. 기간·경계 계산
- 월말: `YearMonth.atEndOfMonth()` — "The day-of-month is set to the last valid day of the month, taking into account leap years"; `TemporalAdjusters.lastDayOfMonth()` — Returns the "last day of month" adjuster. [S3]
- 월 더하기: `LocalDate.plusMonths` — "the last valid day of the month, 2007-04-30, is selected instead" (2007-03-31 + 1개월). 월말에 더한 뒤 빼면 원래 날짜로 안 돌아온다는 문장은 없다(요약: 클램프 동작만 명시). [S3]
- `ChronoUnit.between`: 시그니처가 `between(Temporal temporal1Inclusive, Temporal temporal2Exclusive)`(끝 미포함), 문장은 "The calculation returns a whole number, representing the number of complete units between the two temporals." 예: 11:30~13:29는 1시간. `LocalDate.until(ChronoLocalDate endDateExclusive)`도 끝 미포함. [S3]
- `Instant.plus(amount, unit)`의 ChronoUnit 지원은 NANOS부터 DAYS까지 열거돼 있고 "DAYS - Returns an Instant with the specified number of days added. This is equivalent to plusSeconds(long) with the amount multiplied by 86,400 (24 hours)"라 적는다. 달·연은 열거에 없다. [S3]
- 오프-바이-원·반개구간 자체를 경고하는 java.time 문장은 찾지 못했다(요약: 위 `Exclusive` 매개변수 이름이 근거의 전부).
- kotlinx-datetime: "Use YearMonth to represent the year and month of an event that does not have a specific day associated with it or has a day-of-month that is inferred from the context (like a credit card expiration date)." 달력 계산은 시간대를 인자로 받는다(`Instant.periodUntil(Instant, TimeZone)`), 달력 없는 산술은 stdlib만으로 가능. [S15]

### 7. 표시(포맷)
- `DateTimeFormatter`: "the ofLocalizedDate provides a formatter that uses the locale specific date format"; 미리 정의된 포맷터 표에 `ofLocalizedDate`·`ofLocalizedTime`·`ofLocalizedDateTime`이 "Formatter with date style from the locale" 등으로 있다. [S3]
- `android.text.format.DateFormat`: `getBestDateTimePattern(Locale, String)` — "Returns the best possible localized form of the given skeleton for the given locale."; `is24HourFormat(Context)` — "Returns true if times should be formatted as 24 hour times, false if times should be formatted as 12 hour (AM/PM) times." `DateUtils.formatDateTime` — "Formats a date or a time according to the local conventions." [S6]
- 12/24시간제: "ICU on Android doesn't observe the user's 24h/12h time format setting, obtained from DateFormat.is24HourFormat() . To observe this setting, either use DateFormat or DateUtils time formatting methods or use ICU time formatting patterns with appropriate hour pattern symbols ('h' for 12h, 'H' for 24h) for different is24HourFormat() return values." [S6] **`java.time.format.DateTimeFormatter`가 이 설정을 따르는지는 문서에 없다.**
- 하드코딩 패턴: Lint `SimpleDateFormat` 설명 — "Almost all callers should use `getDateInstance()`, `getDateTimeInstance()`, or `getTimeInstance()` to get a ready-made instance of SimpleDateFormat suitable for the user's locale. The main reason you'd create an instance this class directly is because you need to format/parse a specific machine-readable format, in which case you almost certainly want to explicitly ask for US to ensure that you get ASCII digits (rather than, say, Arabic digits)." [S8] — 화면 표시에 고정 패턴을 쓰지 말라는 문장에 가장 가깝고, 기계 가독 형식에는 명시적 로캘(`Locale.US`)이 필요하다는 문장이기도 하다.
- 다국어 페이지: "there can be significant differences in formats for dates, times, currencies, and similar information even within a single locale." [S21]

### 8. 정적 검사
- Android Lint: `SimpleDateFormat`(Warning, Correctness, "Implied locale in date format", Kotlin·Java, IDE 실시간 점검). `DefaultLocale`(Warning, "Implied default locale in case conversion")은 대소문자 변환 대상이라 날짜 API 전용이 아니다. `NewApi`(Error, "Calling new methods on older versions"). [S8] minSdk 26이면 java.time 자체는 걸리지 않고 더 높은 API의 신규 메서드(예: `InstantSource` API 34)만 걸린다는 것은 요약(공식 문장 아님). [S8][S3]
- Lint 점검 목록에서 `System.currentTimeMillis`·`Calendar`·`TimeZone` 직접 사용을 잡는 점검은 없었다(목록 텍스트에서 세 단어 모두 0회). [S8]
- detekt(문서 `next`): 시각 API 전용 규칙 없음(style 문서에서 `currentTimeMillis` 0회). 다만 `ForbiddenMethodCall`이 설정 `methods`로 임의 메서드를 금지하고, 문서가 "Methods can be defined without full signature (i.e. java.time.LocalDate.now ) which will report calls of all methods with this name"이라 적는다. 기본 금지 목록은 `kotlin.io.print`, `kotlin.io.println`, `java.math.BigDecimal.<init>(kotlin.Double)`, `java.math.BigDecimal.<init>(kotlin.String)`, `kotlin.system.measureTimeMillis`이며 "Active by default : No", "Requires Type Resolution". `ForbiddenImport`는 `java.util.*` 같은 글롭 금지와 `allowedImports` 예외를 지원한다. `ImplicitDefaultLocale`은 String.format·대소문자 변환의 로캘 누락 규칙(potential-bugs). [S19] 확인한 규칙셋은 style·potential-bugs·coroutines 세 가지다.
- Konsist에 시각 API 관련 규칙이 있는지는 열지 못했다(미확인). 기존 노트 `research/testing-ci.md`의 Konsist 출처는 선언·아키텍처 검사 일반 문서다.

### 9. 백그라운드 예약 시각
- AlarmManager: `RTC`·`RTC_WAKEUP` — "Alarm time in System.currentTimeMillis() (wall clock time in UTC)". [S10] 가이드: "Elapsed real time uses the "time since system boot" as a reference, and real time clock uses UTC (wall clock) time. This means that elapsed real time is suited to setting an alarm based on the passage of time (for example, an alarm that fires every 30 seconds) since it isn't affected by time zone or locale. The real time clock type is better suited for alarms that are dependent on current locale." 알람은 "will be cleared if it is turned off and rebooted". [S10]
- `SCHEDULE_EXACT_ALARM`은 범위 밖이라 조사하지 않았다(`canScheduleExactAlarms()` 존재만 확인).
- WorkManager: "Periodic work has a minimum interval of 15 minutes". 시간대·시계 변경 시 동작에 대한 문장은 열어본 페이지(PeriodicWorkRequest, define-work, manage-work)에 없었다. [S11]

### 10. 테스트
- coroutines-test: "Delay-skipping is achieved by using virtual time." `advanceTimeBy`는 "Moves the virtual clock of this dispatcher forward by the specified amount". 다른 디스패처에서는 건너뛰지 않는다: "Delays in code that runs inside dispatchers that don't use a TestCoroutineScheduler don't get skipped". [S18]
- `runTest`의 `TestCoroutineScheduler.currentTime`을 실제 날짜 로직과 섞으면 안 된다는 문장은 찾지 못했다. 문서상 `currentTime`은 가상 시간(ms)이라는 정의뿐이고 epoch 기준 여부는 적혀 있지 않다. [S18]
- 고정 시계로 시간 의존 로직을 테스트하는 공식 예시: `java.time.Clock`의 `fixed`·`offset` 언급 [S3], Kotlin `Clock` 문서의 "custom Clock implementations" [S16]. 완성된 테스트 코드 예시는 공식 문서에서 찾지 못했다.

## 후보 비교

### java.time vs kotlinx-datetime vs 레거시
| 항목 | java.time | kotlinx-datetime | `Date`·`Calendar`·`SimpleDateFormat`·`currentTimeMillis` |
|---|---|---|---|
| minSdk 26에서 desugaring | 불필요(API 26) [S3] | 불필요(API 26 이상) [S15] | 해당 없음 |
| 안정성 | 플랫폼 표준 | 0.8.0, README "experimental" [S15] | 표준(일부 메서드 deprecated) [S7] |
| Clock 주입 | `java.time.Clock`(API 26). `InstantSource`는 API 34라 불가 [S3] | `kotlin.time.Clock`(stdlib, Kotlin 2.3 안정) [S16] | `System.currentTimeMillis()` 직접 호출 |
| DST·시간대 문서 | Period/Duration/ZonedDateTime 문장 풍부 [S3] | Instant/LocalDateTime 경계 원칙 문장 [S15] | 출처 없음 |
| 직렬화(JSON) | kotlinx.serialization 내장 여부는 이번 조사에서 확인 못 함 | `kotlin.time.Instant` 직렬화기 내장(1.9.0+) [S17] | 해당 없음 |
| 추가 의존성 | 없음 | 있음(최신 tzdata는 `-zoneinfo` 선택) [S15] | 없음 |
| Room 공식 예시 | 없음 [S9] | 없음 [S9] | `Date`↔`Long` 예시 [S9] |

### 세 시계
| 시계 | API | 특성 | 용도(공식 문장 기준) |
|---|---|---|---|
| 벽시계 | `System.currentTimeMillis()` | 사용자·통신망이 바꿀 수 있어 점프 | 달력·알람처럼 실제 날짜 시각이 중요할 때만 [S4] |
| 단조(슬립 제외) | `SystemClock.uptimeMillis()` / `System.nanoTime()` | 단조, 깊은 슬립 중 정지 | 슬립을 넘지 않는 간격 측정 [S4] |
| 부팅 후 시간 | `SystemClock.elapsedRealtime()` | 단조, 슬립 중에도 진행 | "general purpose interval timing"의 권장 기반 [S4] |
| (참고) 사용자 조정 불가 벽시계 | `SystemClock.currentNetworkTimeClock()`(API 33) | 동기화 전 예외, 보안 용도 금지, 정확도 무보장 | 사용자 시계 변경을 넘어선 보정 참고용 [S4] |
| (참고) Kotlin 단조 | `TimeSource.Monotonic` | 가능하면 단조 소스, 아니면 비단조로 대체 | 문서에 Android 구현 언급 없음 [S16] |

### 저장 형식 후보
| 후보 | 근거 | 주의(출처가 말하는 것) |
|---|---|---|
| `Instant`를 epoch `Long` | Room 예시가 `Date`↔`Long` [S9]; `Instant`는 이벤트 타임스탬프용 [S3] | Room 문서는 `Instant` 자체를 다루지 않음 → 컨버터는 팩이 정함 |
| `Instant`를 ISO-8601 문자열 | `toString`·`parse`가 ISO 8601 [S16]; serialization 기본 직렬화기가 문자열 표현 [S17] | 출력은 UTC(Z)로 정규화 [S16] |
| `LocalDate`(날짜만) | birth date 예시 [S15] | 시점이 아님. Room 공식 언급 없음 |
| `YearMonth` | credit card expiration date 예시 [S15] | Room 저장 방식은 출처 침묵 |
| `LocalDateTime` + 시간대 ID 별도 저장 | 먼 미래의 지역 시각 이벤트 [S15] | `LocalDateTime` 단독은 시점을 못 나타냄 [S3] |
| `ZoneId.systemDefault()` 결과 저장 | 저장 금지 문장은 없음 | 시스템 시간대가 바뀌면 결과가 바뀜 [S3] |

## 규칙 후보
출처가 뒷받침하는 것만 적는다. 문장은 아직 쓰지 않는다.
1. 새 코드의 날짜·시각 타입은 java.time 또는 stdlib `kotlin.time.Instant`를 쓰고 minSdk 26에서는 desugaring이 필요 없다 — S3, S15, S16. (java.time과 kotlinx-datetime 중 표준 선택은 팩 결정 필요)
2. 현재 시각을 직접 조회(`now()`·`System.currentTimeMillis()`·`Clock.System`)하지 말고 `Clock`을 생성자(또는 함수 인자)로 받는다 — S3(Clock Javadoc의 Best practice 문장), S16(kotlin.time.Clock의 "not recommended" 문장 — 원문은 위 2절 인용).
3. 테스트는 고정·조작 가능한 `Clock` 구현을 주입한다 — S3, S16. 공식 완성 예시는 없음.
4. `InstantSource`는 API 34라 minSdk 26에서 쓰지 않는다 — S3, S2.
5. 벽시계는 실제 날짜·시각이 필요한 곳에만 쓰고, 경과 시간 측정은 `elapsedRealtime()`(슬립 포함) 또는 `uptimeMillis()`/`nanoTime()`(슬립 제외)을 쓴다 — S4.
6. 벽시계에 의존하는 상태는 `ACTION_TIME_CHANGED`·`ACTION_TIMEZONE_CHANGED`를 고려한다 — S4, S5. (구체 행동은 팩 결정 필요)
7. 먼 미래의 지역 시각 이벤트는 `Instant`로 미리 바꾸지 않고 `LocalDateTime`(또는 `LocalDate`)과 시간대 ID를 따로 둔다 — S15.
8. 하루 시작 시각은 `LocalDate.atStartOfDay(zone)`으로 만들고, "하루 더하기"는 `Period`/`plusDays`(지역 시간선), 정확히 24시간은 `Duration` — S3.
9. 월말·월 단위 계산은 `YearMonth`·`atEndOfMonth`·`TemporalAdjusters.lastDayOfMonth`를 쓰고, `plusMonths`가 월말을 유효한 마지막 날로 자른다는 점을 전제한다 — S3.
10. 기간 차이는 `ChronoUnit.between`(끝 미포함, 완전한 단위 수만 정수로 반환) 또는 `until` 계열을 쓰고 반개구간 [시작, 끝)으로 다룬다 — S3(매개변수명 `Inclusive`/`Exclusive`).
11. 화면 표시는 로캘 기반 API(`DateTimeFormatter.ofLocalized*`, `DateFormat.getBestDateTimePattern`, `DateUtils.formatDateTime`)를 쓰고, 12/24시간제는 `DateFormat.is24HourFormat` 또는 `DateUtils`/`DateFormat` 시간 포맷을 쓴다 — S3, S6.
12. 기계 가독 형식 포맷·파싱에는 명시적 `Locale.US`를 넘기고(`SimpleDateFormat`을 쓰는 경우), `SimpleDateFormat`은 스레드마다 별도 인스턴스를 쓴다 — S8, S7.
13. `kotlin.time.Instant` 직렬화는 kotlinx.serialization 1.9.0+의 기본 `InstantSerializer`(문자열 표현)를 쓴다 — S17.
14. `Instant`의 ISO-8601 문자열 파싱은 오프셋 입력을 받아들이고 UTC(Z)로 출력한다 — S16.
15. `ZoneId.systemDefault()`는 호출 시점의 시스템 시간대를 반환하므로 값이 바뀔 수 있음을 전제한다(저장 금지 문장은 출처에 없음) — S3.
16. `Date`의 문자열 포맷·파싱 메서드는 deprecated이므로 쓰지 않는다 — S7. (`Date` 자체 금지는 팩 결정 필요)
17. 정적 검사: Android Lint `SimpleDateFormat`·`DefaultLocale`을 error로 올릴 수 있고 — S8, detekt `ForbiddenMethodCall`·`ForbiddenImport`로 `now()` 등을 금지 목록에 넣을 수 있다 — S19. (금지 목록 내용은 팩 결정 필요)
18. 백그라운드 알람은 간격형이면 `ELAPSED_REALTIME`, 실제 날짜 시각형이면 `RTC`를 쓴다 — S10. 재부팅하면 알람이 삭제된다 — S10.
19. 팩 결정 필요: 표준 시간 라이브러리(java.time vs kotlinx-datetime), `Clock` 종류(`java.time.Clock` vs `kotlin.time.Clock`)와 기본 Clock 제공 방식(Hilt 바인딩), Room 저장형(epoch `Long` vs ISO 문자열)과 Room 2.x/Room3 선택, 시간대 변경 시 앱 행동, `Date`·`Calendar`·`currentTimeMillis` 금지 범위, detekt·Lint 금지 목록, 서버 시각 표기(오프셋 포함 여부).

## 출처가 침묵하는 것
- `Date`·`Calendar`·`System.currentTimeMillis()`를 쓰지 말라는 Android 공식 문장(Date의 문자열 메서드 deprecated 문장만 있음).
- Android 공식 문서가 "java.time을 권장한다"고 직접 말하는 문장(desugaring 페이지와 Compose 예시는 대안 제시 수준).
- Room의 `Instant`·`LocalDate`·`LocalDateTime` 저장 권고(Room 문서는 `Date`↔`Long` 예시뿐).
- DataStore·JSON에서의 시각 저장 권고(DataStore 페이지는 열지 않음).
- 서버 API와 주고받는 UTC 오프셋 표기 규범.
- `ZoneId.systemDefault()`를 저장에 쓰지 말라는 문장.
- 시간대 변경 시 앱이 다시 계산해야 할 것에 대한 지침.
- `DateTimeFormatter.ofLocalized*`가 사용자 12/24시간제 설정을 따르는지(문서는 "ICU on Android doesn't observe…"만 적고 `DateTimeFormatter`를 직접 언급하지 않음).
- "yyyy-MM-dd" 같은 패턴을 화면에 쓰지 말라는 명시 금지 문장(Lint `SimpleDateFormat` 설명이 가장 가까움).
- `TimeSource.Monotonic`의 Android 실제 구현.
- WorkManager가 시간대·시계 변경을 어떻게 다루는지.
- `runTest`의 `currentTime`과 실제 날짜 로직을 섞지 말라는 문장, 고정 시계 테스트의 공식 완성 코드 예시.
- 반개구간·오프-바이-원을 경고하는 java.time 문장.
- detekt·Lint에 `System.currentTimeMillis` 금지 내장 규칙(detekt style·potential-bugs·coroutines, Lint 점검 목록에서 없음).

## 미확인
- Konsist 문서의 시각 API 관련 규칙 — 열지 않음.
- DataStore 문서의 시각 저장 관련 문장 — 열지 않음.
- detekt 2.0.0-alpha.6 태그 자체의 규칙 문서 — `docs/next` 페이지로 대체 확인(글자 단위 일치 미확인). 확인한 규칙셋은 style·potential-bugs·coroutines.
- https://source.android.com/docs/core/permissions/timezone — 404(없는 경로). 대신 `.../ota/modular-system/timezone`, `.../permissions/timezone-rules`를 열었다.
- `TimeSource.Monotonic`의 Android 구현, `DateTimeFormatter`의 12/24시간제 반영 여부 — 문서에 없고 실기기 검증도 하지 않음.
- Oracle Javadoc(JDK 17)은 대조용으로만 열었고 인용은 Android 레퍼런스에서 확인한 문장만 썼다.
- 페이지 안에서 에이전트에게 무언가를 하라고 지시하는 문구: 없었다.
