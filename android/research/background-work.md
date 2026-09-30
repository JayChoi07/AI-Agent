# 조사 노트 — 백그라운드 실행·포그라운드 서비스

조사일: 2026-09-30. 대상: 팩에 새로 넣을 "백그라운드 실행·포그라운드 서비스" 규칙의 근거 조사(규칙 문장은 이 노트에 쓰지 않는다).
확정 전제: AGP 9.4.0 / Kotlin 2.4.20 / compileSdk·targetSdk 37 / minSdk 26 / Hilt 2.60.1(KSP) / Compose·Navigation 3 / 단일 Activity.
기존 규칙과의 접점: R-14-02(필드 주입은 Hilt 진입점 예외), R-14-03(진입점은 Application 하나와 루트 Activity 하나), R-14-09(@EntryPoint), R-15-09(프로세스 사망을 넘기는 작업은 WorkManager), R-40-07(exported 명시), R-40-08(서비스는 명시적 인텐트).

조사 방식
- 웹 문서는 `curl`로 HTML 원문을 받아 script·style·태그를 제거한 본문에서 읽었다. WebFetch 요약 모델은 쓰지 않았다(Android 14 알림 해제 변경의 존재 확인에 WebSearch 결과를 한 번 참고했고, 문장은 S25 본문에서 다시 확인했다).
- 큰따옴표 인용은 위 텍스트에 글자 그대로(공백 차이만 무시) 있는지 스크립트로 재확인했다. 큰따옴표가 없는 서술은 요약이다.
- "갱신일"은 각 페이지 하단의 "Last updated" 값이다. 표기가 없는 페이지는 "미표기"로 적었다.

## 출처

| # | 조직 | 문서명 | URL | 종류 | 갱신일/버전 |
|---|---|---|---|---|---|
| S1 | Google | Background tasks overview | https://developer.android.com/develop/background-work/background-tasks | 공식 가이드 | 2026-02-26 |
| S2 | Google | Foreground services overview | https://developer.android.com/develop/background-work/services/fgs | 공식 가이드 | 2026-09-16 |
| S3 | Google | Declare foreground services and request permissions | https://developer.android.com/develop/background-work/services/fgs/declare | 공식 가이드 | 2026-09-16 |
| S4 | Google | Launch a foreground service | https://developer.android.com/develop/background-work/services/fgs/launch | 공식 가이드 | 2026-09-16 |
| S5 | Google | Foreground service types | https://developer.android.com/develop/background-work/services/fgs/service-types | 공식 가이드 | 2026-09-21 |
| S6 | Google | Restrictions on starting a foreground service from the background | https://developer.android.com/develop/background-work/services/fgs/restrictions-bg-start | 공식 가이드 | 2026-09-16 |
| S7 | Google | Foreground service timeout behavior | https://developer.android.com/develop/background-work/services/fgs/timeout | 공식 가이드 | 2026-09-16 |
| S8 | Google | Changes to foreground services | https://developer.android.com/develop/background-work/services/fgs/changes | 공식 가이드 | 2026-09-16 |
| S9 | Google | Stop a foreground service | https://developer.android.com/develop/background-work/services/fgs/stop-fgs | 공식 가이드 | 2026-09-16 |
| S10 | Google | Handle user-stopped foreground service | https://developer.android.com/develop/background-work/services/fgs/handle-user-stopping | 공식 가이드 | 2026-09-16 |
| S11 | Google | Troubleshoot foreground services | https://developer.android.com/develop/background-work/services/fgs/troubleshooting | 공식 가이드 | 2026-09-16 |
| S12 | Google | Data transfer background task options | https://developer.android.com/develop/background-work/background-tasks/data-transfer-options | 공식 가이드 | 2026-02-26 |
| S13 | Google | User-initiated data transfer jobs | https://developer.android.com/develop/background-work/background-tasks/uidt | 공식 가이드 | 2026-09-16 |
| S14 | Google | Support for long-running workers (WorkManager) | https://developer.android.com/develop/background-work/background-tasks/persistent/how-to/long-running | 공식 가이드 | 2026-09-16 |
| S15 | Google | Schedule alarms | https://developer.android.com/develop/background-work/services/alarms | 공식 가이드 | 2026-09-16 |
| S16 | Google | Services overview | https://developer.android.com/develop/background-work/services | 공식 가이드 | 2025-01-28 |
| S17 | Google | Bound services overview | https://developer.android.com/develop/background-work/services/bound-services | 공식 가이드 | 2024-05-20 |
| S18 | Google | Dependency injection with Hilt | https://developer.android.com/training/dependency-injection/hilt-android | 공식 가이드 | 2026-09-16 |
| S19 | Google | Hilt testing guide | https://developer.android.com/training/dependency-injection/hilt-testing | 공식 가이드 | 2026-09-16 |
| S20 | Google | LifecycleService API 레퍼런스 | https://developer.android.com/reference/androidx/lifecycle/LifecycleService | 공식 레퍼런스 | 2026-08-06 |
| S21 | Google | androidx.lifecycle 패키지 요약(Kotlin) | https://developer.android.com/reference/kotlin/androidx/lifecycle/package-summary | 공식 레퍼런스 | 2026-08-12 |
| S22 | Google | Test your Service | https://developer.android.com/training/testing/other-components/services | 공식 가이드 | 2026-03-05 |
| S23 | Robolectric | Robolectric 4.16 Javadoc — `Robolectric` 클래스 | https://robolectric.org/javadoc/4.16/org/robolectric/Robolectric.html | 오픈소스 Javadoc | 미표기 |
| S24 | Google | Notification runtime permission | https://developer.android.com/develop/ui/views/notifications/notification-permission | 공식 가이드 | 2026-09-16 |
| S25 | Google | Behavior changes: all apps (Android 14) | https://developer.android.com/about/versions/14/behavior-changes-all | 공식 문서 | 2026-09-16 |
| S26 | Google | Behavior changes: apps targeting Android 14 or higher | https://developer.android.com/about/versions/14/behavior-changes-14 | 공식 문서 | 2026-09-16 |
| S27 | Google | Behavior changes: apps targeting Android 15 or higher | https://developer.android.com/about/versions/15/behavior-changes-15 | 공식 문서 | 2026-09-16 |
| S28 | Google | Behavior changes: apps targeting Android 16 or higher | https://developer.android.com/about/versions/16/behavior-changes-16 | 공식 문서 | 2026-09-16 |
| S29 | Google | Behavior changes: apps targeting Android 17 or higher | https://developer.android.com/about/versions/17/behavior-changes-17 | 공식 문서 | 2026-09-16 |
| S30 | Google | Behavior changes: all apps (Android 17) | https://developer.android.com/about/versions/17/behavior-changes-all | 공식 문서 | 2026-09-16 |
| S31 | Google | Communicate in the background (BLE) | https://developer.android.com/develop/connectivity/bluetooth/ble/background | 공식 가이드 | 2026-02-26 |
| S32 | Google | Companion device pairing | https://developer.android.com/develop/connectivity/bluetooth/companion-device-pairing | 공식 가이드 | 2026-09-16 |
| S33 | Google | Optimize for Doze and App Standby | https://developer.android.com/training/monitoring-device-state/doze-standby | 공식 가이드 | 2026-08-18 |
| S34 | Google | App Standby Buckets | https://developer.android.com/topic/performance/appstandby | 공식 가이드 | 2026-09-16 |
| S35 | Google | System restrictions on background tasks | https://developer.android.com/develop/background-work/background-tasks/bg-work-restrictions | 공식 가이드 | 2026-09-21 |
| S36 | Google | Understanding foreground service and full-screen intent requirements | https://support.google.com/googleplay/android-developer/answer/13392821 | Play Console 도움말 | 미표기 |
| S37 | Google | Device and Network Abuse (Permissions for Foreground Services 절) | https://support.google.com/googleplay/android-developer/answer/9888379 | Play 정책 | 미표기 |
| S38 | Google | Use of permissions and APIs that access sensitive information (위치 절) | https://support.google.com/googleplay/android-developer/answer/9799150 | Play 정책 도움말 | 미표기 |
| S39 | Google | Best practices for accessing location in the background | https://developer.android.com/develop/sensors-and-location/location/background | 공식 가이드 | 2026-02-26 |
| S40 | Google | Lifecycle 릴리스 노트, WorkManager 릴리스 노트 | https://developer.android.com/jetpack/androidx/releases/lifecycle , https://developer.android.com/jetpack/androidx/releases/work | 릴리스 노트 | lifecycle 2.11.0 / work 2.12.0 (둘 다 2026-09-23) |

## 핵심 내용 (출처별)

### 질문 1 — 작업 종류별 선택 기준

- 기본값은 WorkManager다. "In most cases, your best option for running background tasks is to use WorkManager." [S1]
- 세 범주로 나눈다: 비동기 작업(코루틴·스레드), 작업 스케줄링 API(WorkManager·JobScheduler), 포그라운드 서비스. [S1]
- 비동기 작업은 앱이 백그라운드로 가면 끝까지 간다는 보장이 없다. "unlike the background task APIs, asynchronous work is not guaranteed to finish if the app stops being in a valid lifecycle stage" [S1]
- 사용자가 화면에서 시작한 작업의 결정 흐름(요약): ① 앱이 백그라운드에서도 계속 돌아야 하는가 → 아니면 비동기 작업. ② 미뤄지거나 끊기면 사용자 경험이 나빠지는가 → 아니면 백그라운드 작업 API. ③ 짧고 중요한 작업이면 shortService. ④ 그 목적 전용 API가 있는가 → 없고 끊기면 안 되면 포그라운드 서비스. [S1]
- 10분 기준: "If a background work task takes longer than 10 minutes to complete, it's highly likely to be interrupted." 쪼갤 수 없고 끊기면 안 되는 장기 작업이면 포그라운드 서비스가 적합할 수 있다고 한다. [S1]
- 포그라운드 서비스는 사용자가 인지하는 작업에만 쓴다. "Only use a foreground service when your app needs to perform a task that is noticeable by the user, even when they're not directly interacting with the app." [S2]
- 전용 API가 있으면 그것을 먼저 쓴다. "If an alternative API exists for your use case, we recommend using that API instead of a foreground service as it should help your app perform better." [S1]
- 대표 대체 관계: 대용량 업로드·다운로드는 사용자 시작 데이터 전송(UIDT)이 dataSync 서비스를 대신하고, 블루투스 페어링·데이터 전송은 Companion Device Manager가 connectedDevice 서비스를 대신하고, 영상 재생은 PiP가 mediaPlayback 서비스를 대신하고, 「도착했을 때」 동작은 지오펜스가 위치 추적 서비스를 대신한다. [S1]
- 이벤트(브로드캐스트·FCM·알람)로 시작하는 작업의 흐름(요약): 몇 초 안에 끝나면 비동기 작업, 몇 초보다 길면 포그라운드 서비스가 적절할 수 있다(단 백그라운드 시작 예외에 해당해야 함), 그 밖에는 작업 스케줄링 API. [S1]
- 데이터 전송 선택 표(요약): WorkManager는 「10분 미만·앱이 안 보일 때 실행되는」 작업, UIDT는 사용자가 시작하고 진행을 알려야 하는 전송, 포그라운드 서비스는 "For short, critical tasks, or when WorkManager is not an option.", 전용 API가 있으면 그것. 예시로 connectedDevice는 「연결된 기기와 데이터 동기화」다. [S12]
- UIDT 조건 세 가지(사용자가 시작, 진행 알림 필요, 시스템이 끊으면 경험이 나쁨) 중 하나라도 아니면 WorkManager를 쓰라고 한다. [S12]
- 장기 작업 WorkManager: 10분을 넘는 작업은 WorkManager가 포그라운드 서비스를 대신 띄운다. "Under the hood, WorkManager manages and runs a foreground service on your behalf to execute the WorkRequest , while also showing a configurable notification." 백그라운드 시작 제한과 실행 한도는 그대로 적용된다. [S14] [S12]
- AlarmManager: 정밀 알람은 예외적이다. "It's highly recommended that you create an inexact alarm whenever possible." "Your app should use exact alarms, and declare either SCHEDULE_EXACT_ALARM or USE_EXACT_ALARM permission, only if a user-facing function in your app requires precisely-timed actions." 긴 작업은 알람의 리시버에서 WorkManager나 JobScheduler로 넘긴다. "To perform longer work, schedule it using WorkManager or JobScheduler from your alarm's BroadcastReceiver." [S15]
- 앱이 살아 있는 동안의 타이밍은 알람이 아니라 Handler를 권한다(요약). [S15]

### 질문 2 — 포그라운드 서비스 시작·선언·타입

- 시작 절차: "First, you must start the service by calling context.startForegroundService()." 다음에 서비스가 `ServiceCompat.startForeground()`로 승격한다. 보통 `onStartCommand()`에서 부른다. [S4]
- 제한 시간: "Once the service has been created, the service must call its startForeground() method within five seconds." [S16] 다른 문서는 "a few seconds"로만 적는다. 초과하면 `ForegroundServiceDidNotStartInTimeException`이다(요약). [S11]
- 알림 필수, 우선순위: "Note: The status bar notification must use a priority of PRIORITY_LOW or higher." 알림 ID는 0이면 안 된다(예제 주석). [S4]
- Android 12부터 대부분의 포그라운드 서비스 알림은 10초가 지나야 보인다(요약, 짧은 서비스가 알림 없이 끝날 수 있음). [S1]
- 타입 선언(Android 14, targetSdk 34 이상): 매니페스트 `<service>`에 `android:foregroundServiceType`을 쓰고 타입별 권한을 요청한다. "You must declare all foreground services with their service types." [S8] 미선언 시 `startForeground()`에서 예외다. "If you try to create a foreground service and its type isn't declared in the manifest, the system throws a MissingForegroundServiceTypeException upon calling startForeground()." [S3]
- `startForeground()`에 넘기는 타입은 매니페스트에 선언한 것 안에 있어야 한다(요약). 타입이 여럿이면 `|`로 잇는다. [S3] [S4]
- 권한 요건: 타입별 `FOREGROUND_SERVICE_*` 권한은 물론 공통 `FOREGROUND_SERVICE`도 선언한다. 빠지면 SecurityException이다(요약). [S3] [S8] [S11]
- 타입별 권한은 install-time normal 권한이라 사용자가 거절·철회할 수 없다. "Foreground service permissions that refer to a specific foreground service type are defined as normal permissions and are granted by default at install time." [S36]
- 런타임 사전 조건은 타입마다 다르고, 승격 시점에 시스템이 검사한다. 예: location은 위치 권한이 있어야 한다(요약). 서비스를 시작하기 전에 직접 확인하라고 한다. [S4] [S5]
- 서비스 타입 전체와 사전 조건은 아래 「후보 비교 — 서비스 타입 표」에 있다. [S5]
- targetSdk 37: 포그라운드 서비스 자체의 새 요건은 Android 17 동작 변경 페이지에서 찾지 못했다. 관련 변경은 두 가지다. 백그라운드 오디오 강화("If one of these apps interacts with audio while it is in the background, it must have a foreground service running."), RFCOMM 소켓 읽기("the read() method of the InputStream obtained from an RFCOMM-based BluetoothSocket now returns -1 when the socket is closed or the connection is dropped."). [S29] `changes` 페이지에도 Android 17 절은 없고 Android 16까지만 있다. [S8]
- 6시간 타임아웃(dataSync·mediaProcessing, targetSdk 35 이상): "The system permits dataSync and mediaProcessing foreground services to run for a total of 6 hours in a 24-hour period, after which the system calls the running service's Service.onTimeout(int, int) method (introduced in Android 15)." [S7]
- 시간 초과 후 몇 초 안에 `stopSelf()`를 부르지 않으면 앱이 죽는다. "If the service does not call Service.stopSelf() , the system throws an internal exception." 사용자가 앱을 전면으로 가져오면 타이머가 리셋된다. 한도를 다 쓰면 새 dataSync 서비스 시작이 `ForegroundServiceStartNotAllowedException`이다. [S7]
- 타임아웃 회피책으로 문서가 적은 것: `onTimeout` 구현, 6시간 이내로 유지, 사용자 상호작용으로만 시작, 대체 API(WorkManager 등) 사용. [S7]
- shortService: 약 3분, 스티키 불가, 다른 포그라운드 서비스를 시작할 수 없고 타입별 권한이 없다. [S5]
- Android 16(모든 앱): "Background jobs started from a foreground service now must adhere to their respective runtime quotas." 즉 서비스 안에서 예약한 WorkManager·JobScheduler 잡도 한도를 받는다. UIDT 잡은 예외다(요약). [S8]

### 질문 3 — 백그라운드에서 포그라운드 서비스 시작 제한

- 원칙: "Apps that target Android 12 (API level 31) or higher can't start foreground services while the app is running in the background, except for a few special cases." 위반하면 `ForegroundServiceStartNotAllowedException`이다. [S6]
- 허용 예외(문서 목록 요약): 활동 등 사용자에게 보이는 상태에서 전이, 높은 우선순위 FCM 수신, 알림·위젯·버블·액티비티 등 앱 관련 UI 요소에 대한 사용자 조작, "Your app invokes an exact alarm to complete an action that the user requests.", 현재 입력기, 지오펜스·활동 인식 전이 이벤트, 재부팅 후 BOOT_COMPLETED·LOCKED_BOOT_COMPLETED·MY_PACKAGE_REPLACED 수신, 시간대·시간·로케일 변경 수신, NFC 이벤트, 기기 소유자·프로필 소유자 등 시스템 역할, Companion Device Manager와 `REQUEST_COMPANION_START_FOREGROUND_SERVICES_FROM_BACKGROUND`(가능하면 이것) 또는 `REQUEST_COMPANION_RUN_IN_BACKGROUND` 선언, 사용자가 배터리 최적화를 끈 앱, SYSTEM_ALERT_WINDOW 보유 앱. [S6]
- 높은 우선순위 FCM은 강등될 수 있다. 시작 전에 `RemoteMessage.getPriority()`가 높음인지 확인하라고 한다(요약). [S6]
- 배터리 최적화 제외 예외는 명시돼 있다. "The user turns off battery optimizations for your app." 다만 shortService 타임아웃은 그래도 적용된다(요약). [S6] [S5]
- 입력기·위젯 등 나머지 예외는 범용 앱에 드물다. 팩이 다룰 후보는 알림·위젯 조작, 재부팅 수신, 정밀 알람, CDM, 배터리 최적화 제외다(팩 결정 필요).
- 앞선 while-in-use 함정: 카메라·마이크·위치·바디 센서 권한이 필요한 서비스는 위 예외에 해당해도 백그라운드에서 만들 수 없다. "if an app wants to launch a foreground service that needs while-in-use permissions (for example, body sensor, camera, microphone, or location permissions), it cannot create the service while the app is in the background, even if the app falls into one of the exemptions from background start restrictions." `checkSelfPermission()`은 이 상황을 막아주지 못한다. 위치는 `ACCESS_BACKGROUND_LOCATION`이 있으면 예외다. [S6] [S5]
- connectedDevice의 사전 조건(블루투스 연결·스캔·광고 런타임 권한 등)은 while-in-use 목록에 없다. 따라서 위 예외에 해당하면 백그라운드에서도 시작할 수 있다(문서 목록에서 추론한 것이며 문서가 connectedDevice를 직접 언급하지는 않는다).
- BOOT_COMPLETED 제한(targetSdk 35 이상): "BOOT_COMPLETED receivers are not allowed to launch the following types of foreground services:" 뒤에 dataSync, camera, mediaPlayback, phoneCall, mediaProjection, microphone이 나온다(microphone은 Android 14부터). 어기면 `ForegroundServiceStartNotAllowedException`이다. connectedDevice와 location은 이 목록에 없다. [S27] 위치·마이크·카메라는 while-in-use 규칙도 별도로 걸린다. [S5]
- SYSTEM_ALERT_WINDOW(targetSdk 35 이상): "If your app targets Android 15 or higher, it must have the SYSTEM_ALERT_WINDOW permission and the app must currently have a visible overlay window." [S6] [S27]
- Android 16·17에서 추가된 시작 제한은 찾지 못했다. Android 16은 잡 한도, Android 17은 오디오 강화만 있다. [S8] [S29] [S30]
- 테스트용 adb: `adb shell am compat enable FGS_BOOT_COMPLETED_RESTRICTIONS <pkg>`, `adb shell am broadcast -a android.intent.action.BOOT_COMPLETED <pkg>`. [S27]

### 질문 4 — Play 정책

- 선언 요건: targetSdk 34 이상이면 Play Console 앱 콘텐츠 페이지에 서비스 타입을 선언한다. "declare any foreground service types that you use in a new declaration on the App content page" [S36] [S5]
- 타입마다 낼 것: "Provide a description of the app functionality that is using each foreground service type." "Describe the user impact if: the task is deferred by the system (does not start immediately); and/or the task is interrupted by the system (paused and/or restarted)." "Include a link to a video demonstrating each foreground service feature." 미리 정해진 사용 사례 목록에서 고른다. [S36]
- 사용 사례 표에서 connectedDevice는 "Continuous Data Transfer to an External Device"로, 예시는 웨어러블·베이비 모니터·헤드셋·자동차다. [S36]
- 정책 조건: "Apps are only allowed to declare a foreground service permission if the use:" 뒤에 사용자에게 이롭고 핵심 기능과 관련, 사용자가 시작했거나 사용자가 인지, 사용자가 종료할 수 있음, 시스템이 미루거나 끊으면 경험이 나빠짐, 필요한 동안만 실행 — 이 다섯 조건이 붙는다. 이 정책 조건에서 systemExempted·shortService는 제외된다. [S37]
- 정책의 Do/Don't: "Consider alternatives like WorkManager." 시스템 관리가 경험을 깨지 않으면 포그라운드 서비스를 쓰지 말라는 취지다(요약). [S37]
- UIDT도 정책이 있다: "Don't initiate transfers automatically." "Use for network data transfer tasks only." [S37]
- 위치: 포그라운드 서비스로 위치를 쓰려면 심사를 받는다. "The use of foreground service must be initiated as a continuation of an in-app, user-initiated action." "The use of foreground service must be terminated immediately after the application completes the intended use case of the user-initiated action." 백그라운드 위치와 사실상 같으면 백그라운드 위치 요건이 적용된다(요약). [S38]
- 백그라운드 위치는 권한 선언 양식 승인이 필요하다. "apps that access location in the background must be approved via the permission declaration process in the developer console" [S38] [S39]
- 블루투스 백그라운드 사용에 대한 Play 정책 조항은 찾지 못했다(S36~S38에 없음).

### 질문 5 — 서비스와 Hilt

- Hilt는 Service를 지원한다. "Hilt currently supports the following Android classes:" 뒤에 Application, ViewModel, Activity, Service, BroadcastReceiver가 나온다. Service용 컴포넌트는 `ServiceComponent`이고 `Service#onCreate()`에서 만들어져 `Service#onDestroy()`에서 사라진다(표 요약). BroadcastReceiver는 `SingletonComponent`에서 직접 주입한다. [S18]
- R-14-03과의 양립: 공식 문장은 진입점을 UI 계층에 한정한다. "This serves as the single DI entry point for your entire UI hierarchy" — 즉 「하나」는 Compose UI 한정이고, 서비스·리시버는 별도 진입점으로 나란히 지원된다. 공식 문서에 진입점 총개수 상한은 없다. [S18]
- 필드 주입: `@AndroidEntryPoint` 클래스는 `@Inject` 필드 주입을 쓴다. "Fields injected by Hilt cannot be private." R-14-02가 이미 Service를 필드 주입 예외에 넣고 있다. [S18]
- `LifecycleService`: "A Service that is also a LifecycleOwner." [S20] `LifecycleOwner.lifecycleScope`는 "CoroutineScope tied to this LifecycleOwner's Lifecycle." [S21] 아티팩트는 `androidx.lifecycle:lifecycle-service`이며 안정 버전은 lifecycle-* 2.11.0이다(2026-09-23). [S20] [S40]
- `@AndroidEntryPoint`를 `LifecycleService`에 붙이는 것, 서비스 수명에 묶는 스코프로 `lifecycleScope`를 권하는 것은 공식 문서에서 명시적 문장을 찾지 못했다. 서비스 안 코루틴 스코프 권고 문서도 못 찾았다(아래 침묵 절).
- 서비스와 UI의 상태 공유: 같은 프로세스의 로컬 서비스는 Binder 방식이 권장이다. "This is the preferred technique when your service is merely a background worker for your own application." [S17] 단 바인드만으로는 서비스가 UI보다 오래 못 산다. 시작 서비스가 아니면 "After the service is unbound from all of its clients, the system destroys it." [S16] 싱글턴 Repository의 StateFlow로 공유하는 방식은 서비스 맥락에서 공식 문서가 직접 권하는 문장을 못 찾았다.

### 질문 6 — 서비스 종료·재시작

- 반환값 의미(원문 인용):
  - "START_NOT_STICKY If the system kills the service after onStartCommand() returns, do not recreate the service unless there are pending intents to deliver." [S16]
  - "START_STICKY If the system kills the service after onStartCommand() returns, recreate the service and call onStartCommand() , but do not redeliver the last intent." 재시작 시 인텐트는 null이다. "Instead, the system calls onStartCommand() with a null intent unless there are pending intents to start the service." 미디어 플레이어처럼 「실행 중이며 명령을 기다리는」 서비스에 맞다고 한다. [S16]
  - START_REDELIVER_INTENT: "recreate the service and call onStartCommand() with the last intent that was delivered to the service." 파일 다운로드처럼 하던 작업을 즉시 이어야 하는 서비스에 맞다. [S16]
- 시작 서비스는 재시작에 대비해야 한다. "if your service is started, you must design it to gracefully handle restarts by the system." [S16]
- 포그라운드 서비스는 메모리 압박에서도 잘 안 죽는다. "if the service is declared to run in the foreground , it's rarely killed" [S16]
- 시작 서비스는 스스로 종료해야 한다. `stopSelf()`·`stopService()`. 여러 시작 요청이 동시에 들어오면 `stopSelf(int)`로 최신 요청 기준으로 끈다(요약). 포그라운드에서 내려오기만 하려면 `stopForeground()`를 쓰고, 서비스를 멈추면 알림도 사라진다. [S16] [S9]
- 시스템이 재시작한 서비스가 백그라운드 상태에서 다시 포그라운드로 승격할 수 있는지에 대한 문장은 찾지 못했다. 재시작 경로는 백그라운드 시작 제한 예외 목록에 없다(S6 목록 기준). 이는 목록에서의 추론이며 문서가 재시작을 직접 언급하지는 않는다. [S6]
- 사용자가 작업 관리자(Task Manager)의 Stop 버튼으로 종료하면(Android 13+, targetSdk 무관): "The system removes your app from memory. Therefore, your entire app stops, not just the running foreground service." 콜백은 오지 않는다. "The system doesn't send your app any callbacks when the user taps the Stop button." 예약된 잡과 알람은 그대로 실행된다. 다음 시작에서 `ApplicationExitInfo`의 `REASON_USER_REQUESTED`를 확인하라고 한다. [S10]
- 배터리·Doze·대기 버킷: 앱 대기 상태 판정에서 포그라운드 서비스가 있으면 유휴로 보지 않는다. 그러나 이 목적으로 서비스를 쓰지 말라고 못박는다. "Don't start a foreground service just to prevent the system from determining that your app is idle." [S33] 활성 버킷 조건에 "Runs a long running foreground service."가 들어 있다. [S34] Android 16부터 활성 버킷 앱이 시작한 잡은 넉넉한 실행 한도를 받는다(요약). [S34]
- Doze가 포그라운드 서비스 실행 자체를 막는지에 대해서는 Doze 문서가 언급하지 않는다(S33에 포그라운드 서비스 언급은 App Standby 조건뿐).
- 사용자가 앱을 제한하면(안드로이드 vitals 나쁜 행동 기준) 잡·알람·네트워크가 막힌다. 이를 피하려면 WorkManager를 쓰라고 한다(요약). [S35]

### 질문 7 — 알림

- 포그라운드 서비스 알림은 필수다. POST_NOTIFICATIONS 권한 요청은 필수가 아니다. "Apps don't need to request the POST_NOTIFICATIONS permission in order to launch a foreground service. However, apps must include a notification when they start a foreground service, just as they do on previous versions of Android." [S24]
- 권한을 거절하면 알림 서랍에는 안 보이고 작업 관리자에만 보인다. "if the user denies the notification permission, they still see notices related to foreground services in the Task Manager but don't see them in the notification drawer." [S24] 서비스는 계속 돈다(요약, 문서가 서비스 중단을 언급하지 않음).
- 채널: 알림은 채널을 지정해 만든다(예제의 `NotificationCompat.Builder(this, "CHANNEL_ID")`). 포그라운드 서비스 전용 채널 중요도에 대한 공식 권고 문장은 못 찾았다. 우선순위 하한은 PRIORITY_LOW다. [S4]
- 사용자가 알림을 없앨 수 있다(Android 14). "If your app shows non-dismissable foreground notifications to users, Android 14 has changed the behavior to allow users to dismiss such notifications." 단 "When the phone is locked"와 "If the user selects a Clear all notification action"에서는 여전히 못 없앤다. CallStyle·DPC·미디어 알림 등은 이 변경의 대상이 아니다. [S25] 알림을 없애도 서비스는 계속 돈다는 명시 문장은 못 찾았다.
- 사용자가 알림을 탭하면 대기 버킷 상호작용으로 치지만, 스와이프로 없애기는 상호작용이 아니다. "If the user swipes away the notification without tapping on it, the system doesn't consider that action to be an interaction with your app." [S34]

### 질문 8 — 테스트

- 계측 테스트: "If you are implementing a local Service as a component of your app, you can create instrumented tests to verify that its behavior is correct." `ServiceTestRule`은 "a JUnit 4 rule that starts your service before your unit test methods run, and shuts down the service after tests complete." [S22]
- IntentService 한정 권고: "If you need to test an IntentService object, you should encapsulate the logic in a separate class and create a corresponding unit test instead." 일반 서비스에 대한 같은 권고는 아니다. [S22]
- Robolectric: `Robolectric.buildService(Class<T> serviceClass)`가 `ServiceController<T>`를 돌려준다(API 존재 확인). [S23] Robolectric 사용 가이드에는 서비스 절이 없다(S23은 Javadoc).
- Hilt 테스트 문서는 서비스 전용 절이 없다. 일반 클래스는 생성자에 fake를 넣어 Hilt 없이 만든다. "You don't need Hilt to create an instance of AnalyticsAdapter." 예제 주석이다. [S19]
- 서비스 로직을 서비스 클래스 밖으로 빼라는 일반 공식 권고는 찾지 못했다(위 IntentService 문장이 유일한 근접 사례).

### 질문 9 — 블루투스 연결 유지

- 일반 원칙: "Note: The general guidance for background work on Android applies for Bluetooth-related work too." [S31]
- 연결 자체는 백그라운드에서 제한이 없다. "There is no restriction on connecting to a device while the app is in the background, although the connection is closed if your process is killed." 다만 Android 12부터 백그라운드에서 포그라운드 서비스를 못 시작한다. [S31]
- 연결을 오래 유지해야 할 때(문서의 두 경우: 앱 전환 중, 주변기기 알림 수신 중) 선택지는 둘이다. ① `CompanionDeviceService` + `REQUEST_COMPANION_RUN_IN_BACKGROUND` + `startObservingDevicePresence()`, ② 앱이 전면에 있을 때(또는 예외에 해당할 때) 시작한 `connectedDevice` 포그라운드 서비스. [S31]
- 알림 수신용은 CDM 쪽을 우선한다. "For most apps, it's best to support this use case with CompanionDeviceService because the app will likely need to keep listening for long periods of time." 포그라운드 서비스도 쓸 수 있다고 한다. [S31]
- 필요한 만큼만 연결한다. "Ideally, apps should maintain connections to peripheral devices only as long as necessary, and disconnect once the task is completed." [S31]
- 주기 스캔은 비권장이다. "Scheduling periodic scans to find devices is discouraged." 프로세스가 죽었을 때는 `startScan()`에 PendingIntent를 넘기거나 CDM을 쓴다. [S31]
- 서비스 자동 기동(Android 16+): 이전 API가 폐기됐다. "Starting with Android 16 (API level 36), CompanionDeviceManager.startObservingDevicePresence(String) and CompanionDeviceService.onDeviceAppeared() are deprecated." 대신 `startObservingDevicePresence(ObservingDevicePresenceRequest)`를 쓴다. 바인딩 상태는 자동 관리된다. "The service is bound when the companion device is within BLE range or connected using Bluetooth." [S32]
- 문서 간 불일치: S31(갱신 2026-02-26)은 폐기 표기가 없는 `startObservingDevicePresence()`를 적는다. S32(갱신 2026-09-16)의 폐기 안내를 따른다.
- 포그라운드 서비스 시작 예외로 CDM 권한 두 개가 나온다. 가능하면 `REQUEST_COMPANION_START_FOREGROUND_SERVICES_FROM_BACKGROUND`를 쓰라고 한다(질문 3). [S6]
- connectedDevice 서비스는 CDM이 대체할 수 있는 경우 CDM을 먼저 고려하라고 한다. "If your app needs to do continuous data transfer to an external device, consider using the companion device manager instead." [S5] 블루투스 스캔만 필요하면 스캔 API를 쓴다(요약). [S5]
- 블루투스 클래식(RFCOMM) 연결의 백그라운드 유지 방법은 S31·S32가 BLE 중심이라 명시가 없다. Android 17(targetSdk 37)에서 RFCOMM 소켓 읽기가 연결 종료 시 -1을 돌려준다. [S29]

## 후보 비교

### 작업 종류별 선택표

| 작업 성격 | 고를 도구 | 근거 |
|---|---|---|
| 화면이 보이는 동안만 필요한 계산·요청 | 코루틴(수명 스코프) | S1 |
| 미뤄지거나 끊겨도 되는 동기화·업로드, 앱이 죽어도 끝나야 하는 작업 | WorkManager(제약·재시도) | S1, S12 |
| 10분 넘고 쪼갤 수 없으며 끊기면 안 되는 작업 | 포그라운드 서비스 또는 장기 실행 Worker(내부적으로 포그라운드 서비스) | S1, S14 |
| 사용자가 시작한 큰 네트워크 전송, 진행 알림 필요 | UIDT 잡(JobScheduler, `RUN_USER_INITIATED_JOBS`) | S12, S13 |
| 몇 초 안에 끝나야 하는 짧고 중요한 작업 | shortService 포그라운드 서비스(약 3분) | S1, S5 |
| 블루투스 기기와 연결을 계속 유지하며 데이터 수집 | CDM 존재 감지 + CompanionDeviceService, 또는 connectedDevice 포그라운드 서비스 | S31, S32, S5 |
| 지정한 시각에 동작(사용자가 지정) | 부정확 알람 우선, 정밀 알람은 사용자 기능이 요구할 때만 | S15 |
| 알람·FCM 등 이벤트 후 긴 작업 | 알람 리시버에서 WorkManager·JobScheduler로 위임 | S15, S1 |

### 서비스 타입 표

| 타입(매니페스트 값) | 필요 권한(모두 `FOREGROUND_SERVICE_*`) | 런타임 사전 조건 | 특이 사항 | 근거 |
|---|---|---|---|---|
| camera | `_CAMERA` | CAMERA 런타임 권한 | while-in-use, BOOT_COMPLETED 시작 불가 | S5, S27 |
| connectedDevice | `_CONNECTED_DEVICE` | 매니페스트에 CHANGE_NETWORK_STATE·CHANGE_WIFI_STATE·CHANGE_WIFI_MULTICAST_STATE·NFC·TRANSMIT_IR 중 하나, 또는 런타임 BLUETOOTH_CONNECT·BLUETOOTH_ADVERTISE·BLUETOOTH_SCAN·UWB_RANGING 중 하나, 또는 `UsbManager.requestPermission()` | 대체: CDM 존재 감지, 스캔 API | S5 |
| dataSync | `_DATA_SYNC` | 없음 | targetSdk 35+ 6시간/24시간 한도, `onTimeout`, BOOT_COMPLETED 시작 불가 | S5, S7, S27 |
| health | `_HEALTH` | HIGH_SAMPLING_RATE_SENSORS 선언 또는 READ_HEART_RATE 등·ACTIVITY_RECOGNITION 런타임 권한 중 하나 | 센서 권한은 while-in-use | S5, S28 |
| location | `_LOCATION` | 위치 서비스 켜짐 + ACCESS_COARSE 또는 FINE_LOCATION | while-in-use, `ACCESS_BACKGROUND_LOCATION`이 있으면 백그라운드 시작 가능 | S5, S6 |
| mediaPlayback | `_MEDIA_PLAYBACK` | 없음 | BOOT_COMPLETED 시작 불가 | S5, S27 |
| mediaProcessing | `_MEDIA_PROCESSING` | 없음 | Android 15 신설, 6시간 한도 | S5, S7 |
| mediaProjection | `_MEDIA_PROJECTION` | `createScreenCaptureIntent()` 후 사용자 승인 | BOOT_COMPLETED 시작 불가 | S5, S27 |
| microphone | `_MICROPHONE` | RECORD_AUDIO 런타임 권한 | while-in-use, BOOT_COMPLETED 시작 불가 | S5, S27 |
| phoneCall | `_PHONE_CALL` | MANAGE_OWN_CALLS 선언 또는 기본 전화 앱 역할 | BOOT_COMPLETED 시작 불가 | S5, S27 |
| remoteMessaging | `_REMOTE_MESSAGING` | 없음 | 기기 간 문자 전달 | S5 |
| shortService | 없음(공통 `FOREGROUND_SERVICE`만) | 없음 | 약 3분, 스티키 불가 | S5 |
| specialUse | `_SPECIAL_USE` | 없음 | `<property android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE">`로 사용 사례 서술, Play 심사 | S5 |
| systemExempted | `_SYSTEM_EXEMPTED` | 없음 | 시스템·특정 역할 앱 전용, 아니면 `ForegroundServiceTypeNotAllowedException` | S5 |

### 상태 공유 방식 비교(서비스 ↔ UI)

| 후보 | 공식 문서 근거 | 비고 |
|---|---|---|
| Binder(같은 프로세스 로컬 바인드) | S17이 "preferred technique when your service is merely a background worker for your own application" | UI가 없을 때는 시작 서비스가 따로 필요(S16) |
| 싱글턴 Repository의 StateFlow | 서비스 맥락 근거 없음 | R-15 계열 데이터 계층 규칙과 결이 같으나 공식 서비스 문서는 침묵 |
| 브로드캐스트·Messenger | S17이 프로세스 간 용도로만 소개 | 같은 앱 내부용으로는 근거 없음 |

## 규칙 후보

- 백그라운드 작업 API는 코루틴·WorkManager·포그라운드 서비스·UIDT·알람 중에서, 사용자가 화면에서 시작했는지·끊겨도 되는지·전용 API가 있는지로 고른다 — 근거 S1, S12.
- 기본값은 WorkManager로 두고, 포그라운드 서비스는 사용자가 인지하는 작업이면서 시스템이 끊으면 경험이 나빠지는 경우에만 쓴다 — 근거 S1, S2, S37.
- 전용 API가 있으면 포그라운드 서비스 대신 그것을 쓴다(대용량 전송은 UIDT, 블루투스 존재 감지는 CDM, 도착 감지는 지오펜스) — 근거 S1, S5, S12.
- 모든 포그라운드 서비스는 `foregroundServiceType`과 타입별 `FOREGROUND_SERVICE_*` 권한을 매니페스트에 선언한다(Android 14+) — 근거 S3, S8, S5.
- 사전 조건이 있는 타입은 서비스를 시작하기 전에 권한 보유를 확인하고, 없으면 시작하지 않는다 — 근거 S4, S11, S5.
- 포그라운드 서비스는 `startForegroundService()` 뒤 `onStartCommand()`에서 즉시 `ServiceCompat.startForeground()`로 승격하고, 알림 우선순위는 LOW 이상이며 타입을 함께 넘긴다 — 근거 S4, S16, S11.
- 서비스는 시작 전에 백그라운드 시작 예외에 해당하는지 확인하며, 시작 실패(`ForegroundServiceStartNotAllowedException`)를 잡아 UI나 상태로 알린다 — 근거 S6, S4, S11.
- 카메라·마이크·위치·바디 센서 타입 서비스는 화면이 보일 때 시작한다 — 근거 S6, S5.
- 재부팅 직후 리시버에서 dataSync·카메라·미디어 재생·전화·미디어 프로젝션·마이크 타입 서비스를 시작하지 않는다 — 근거 S27.
- dataSync·mediaProcessing 서비스는 `onTimeout()`을 구현해 즉시 `stopSelf()`를 부르고, 사용자 조작으로 시작한다 — 근거 S7.
- 서비스는 작업이 끝나면 `stopSelf()`로 스스로 멈춘다 — 근거 S16, S9, S37.
- 종료 시 재시작 정책은 서비스 성격에 맞춰 명시한다(계속 대기하는 서비스는 START_STICKY, 작업을 이어야 하면 START_REDELIVER_INTENT, 그 밖은 START_NOT_STICKY) 그리고 `onStartCommand()`의 null 인텐트를 처리한다 — 근거 S16.
- 사용자의 작업 관리자 종료에는 콜백이 없으므로 다음 시작에서 `ApplicationExitInfo`의 종료 이유를 확인해 상태를 복구한다 — 근거 S10.
- 서비스가 살아 있으려고 포그라운드 서비스를 쓰지 않는다(앱 대기 회피 목적 금지) — 근거 S33.
- `@AndroidEntryPoint` Service는 진입점으로 허용한다(R-14-03의 「하나」는 UI 계층에 한정한다고 고쳐 읽는다) — 근거 S18.
- 서비스의 의존은 필드 주입(private 아님)으로 받고, 서비스 안 로직은 생성자 주입 클래스에 둔다 — 근거 S18, R-14-02.
- 포그라운드 서비스 알림은 채널을 지정하고, 알림 권한이 없어도 서비스는 시작하되 UI가 알림 권한 상태를 다루며, 사용자가 알림을 없앨 수 있음을 전제로 설계한다 — 근거 S24, S25, S4.
- 서비스를 예약하는 잡은 Android 16부터 한도를 받으므로, 서비스 안에서 WorkManager 잡에 의존하지 않는다 — 근거 S8, S14.
- 장기 블루투스 연결 유지는 CDM 존재 감지(`ObservingDevicePresenceRequest`)와 `connectedDevice` 포그라운드 서비스 중에서 고르고, 연결은 필요한 동안만 유지한다 — 근거 S31, S32, S5.
- 주기 스캔으로 기기를 찾지 않는다 — 근거 S31.
- 서비스 테스트: 로컬 서비스의 계측 테스트에 `ServiceTestRule`을 쓴다 — 근거 S22.
- Play 제출 시 포그라운드 서비스 타입별 선언(설명·지연 영향·영상)을 준비한다 — 근거 S36, S37.
- 팩 결정 필요(출처가 침묵): 서비스와 UI의 상태 공유 방식(Binder 대 싱글턴 Repository의 StateFlow), 서비스 안 코루틴 스코프(LifecycleService의 `lifecycleScope` 대 `SupervisorJob` 스코프), 서비스 로직 분리 규칙, 알림 채널 중요도, 재시작된 서비스의 상태 복구 방식, 블루투스 클래식 연결 유지 방식, 포그라운드 서비스 시작을 어떤 계층(ViewModel·Repository·UseCase)이 요청하는지.
- 팩 결정 필요(R-15-09 조정): "프로세스 사망을 넘겨야 하는 작업은 WorkManager"라는 기존 문장은 연속 수집형 작업에는 맞지 않는다. WorkManager는 끊겨도 되는 작업용이고(S1, S12), 끊기면 안 되는 연속 작업은 포그라운드 서비스 쪽이다(S1, S14).
- 팩 결정 필요(R-40-07·R-40-08): 서비스에 `android:exported="false"`를 적는 것은 그대로 맞다(S3 예제도 `false`). 명시적 인텐트는 `startForegroundService(Intent(context, X::class.java))`로 충족된다(S4 예제 참고).

## 출처가 침묵하는 것

- 서비스 안 코루틴 스코프를 어떻게 만들고 취소하라는 공식 권고(서비스 수명과 스코프 결합, `SupervisorJob`, `lifecycleScope` 사용 여부). `LifecycleService`와 `lifecycleScope` 각각의 정의는 있으나(S20, S21) 서비스에서 쓰라는 문장은 없다. UIDT 예제만 `CoroutineScope(Dispatchers.IO)`를 서비스 필드로 만든다(S13).
- `@AndroidEntryPoint`를 `LifecycleService`에 붙이는 공식 예시.
- 서비스와 UI 사이 상태 공유에서 싱글턴 Repository의 StateFlow를 권하는 문장.
- 서비스 로직을 서비스 클래스 밖으로 빼라는 일반 권고(IntentService 한정 문장만 있음, S22).
- Doze가 포그라운드 서비스 실행에 미치는 영향(S33은 App Standby 조건에서만 언급).
- 시스템이 죽인 START_STICKY 서비스가 재시작될 때 백그라운드 시작 제한과의 관계.
- 알림을 사용자가 없앴을 때 서비스가 계속 도는지에 대한 명시 문장.
- 포그라운드 서비스 알림 채널의 권장 중요도.
- 블루투스 클래식(RFCOMM) 연결의 백그라운드 유지 방식(S31, S32는 BLE·CDM 중심).
- Play 정책상 블루투스 백그라운드 사용 조항.
- targetSdk 37에서 포그라운드 서비스 시작·타입에 관한 새 요건(Android 17 동작 변경 페이지와 `changes` 페이지에 없음).
- Robolectric 사용 가이드의 서비스 테스트 절(Javadoc에 `buildService`가 있을 뿐).

## 미확인

- 확인일 2026-09-30에 열지 못한 것은 없다. 다만 다음은 확인 범위를 밝혀 둔다.
- S36·S37·S38의 Play 정책 페이지는 갱신일이 표기돼 있지 않다. 정책 문구가 이후 바뀌었을 수 있다. S37은 "Full Policy" 본문을 그대로 읽었다.
- 앞선 while-in-use 예외 절(서비스가 위젯·알림 상호작용으로 시작되는 경우 등)은 요약했고 전체 목록은 S6 원문을 봐야 한다.
- connectedDevice의 while-in-use 여부와 BOOT_COMPLETED 시작 가능 여부는 문서 목록에서 추론한 것이다. 문서가 connectedDevice를 직접 언급한 문장은 없다. 실기기 확인이 필요하다.
- S31 본문의 "Use WorkManager to connect to your device" 등 세부 선택지는 이 노트에 옮기지 않았다.
- Android 17 정식 릴리스 노트(`about/versions/17` 하위 다른 페이지: features·release notes)는 열지 않았다. 동작 변경 페이지 두 개와 `changes` 페이지만 확인했다.
- Hilt 2.60.1과 `LifecycleService`의 호환은 공식 문서로 확인하지 못했고 실빌드로도 검증하지 않았다.
- WebSearch 결과에 든 "Foreground service types are required"(https://developer.android.com/about/versions/14/changes/fgs-types-required)는 직접 열지 않았다. 같은 내용을 S26으로 확인했다.
- 가져온 페이지 안에 에이전트에게 무언가를 하라고 지시하는 문구는 없었다. 페이지 하단의 "AI Prompt" 상자(S12)는 Android Studio용 프롬프트 예시이며 따르지 않았다.
