# 24 백그라운드 실행

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

이 문서는 화면 밖에서 도는 작업을 어떤 도구로 실행할지(코루틴·WorkManager·포그라운드 서비스·전용 API), 그리고 포그라운드 서비스를 쓸 때의 선언·시작·종료·상태 공유·테스트를 정한다. 코루틴 자체의 사용법은 22, 데이터 계층의 동기화 작업은 15, 매니페스트 노출과 인텐트는 40, 권한 요청 흐름은 별도 문서가 소유하고 여기서는 참조만 한다.

확정 결정 `SERVICE_STATE`(사용자 확정, 2026-09-30): 포그라운드 서비스와 화면은 싱글턴 Repository의 `StateFlow`로 상태를 공유한다. Binder 바인딩은 채택하지 않는다(R-24-11). 그 밖에 이 문서가 "팩 결정"이라고 적은 것은 팩 기본값을 사용자에게 승인받은 것이다: 서비스가 자기 코루틴 스코프를 만든다(R-24-12), 서비스 클래스에는 로직을 두지 않는다(R-24-13). 후보 비교와 출처 원문 대조는 `research/background-work.md`.

## 결정 매트릭스 — 작업 종류별 도구

| 작업 성격 | 고를 도구 | 근거 |
|---|---|---|
| 화면이 보이는 동안만 필요한 계산·요청 | 코루틴(`viewModelScope` 등 수명 스코프, R-22-04) | [S188](https://developer.android.com/develop/background-work/background-tasks) |
| 미뤄지거나 끊겨도 되는 동기화·업로드, 앱이 죽어도 끝나야 하는 작업 | WorkManager(기본값) | [S188](https://developer.android.com/develop/background-work/background-tasks), [S198](https://developer.android.com/develop/background-work/background-tasks/data-transfer-options) |
| 10분 넘고 쪼갤 수 없으며 끊기면 안 되는 작업 | 포그라운드 서비스, 또는 장기 실행 Worker(내부적으로 포그라운드 서비스) | [S188](https://developer.android.com/develop/background-work/background-tasks), [S200](https://developer.android.com/develop/background-work/background-tasks/persistent/how-to/long-running) |
| 사용자가 시작한 큰 네트워크 전송, 진행 알림 필요 | 사용자 시작 데이터 전송(UIDT) 잡 | [S198](https://developer.android.com/develop/background-work/background-tasks/data-transfer-options), [S199](https://developer.android.com/develop/background-work/background-tasks/uidt) |
| 사용자가 인지하고 계속 돌아야 하며 끊기면 경험이 나쁜 연속 작업(위 전용 API 없음) | 포그라운드 서비스 | [S189](https://developer.android.com/develop/background-work/services/fgs), [S211](https://support.google.com/googleplay/android-developer/answer/9888379) |
| 외부 기기와 연결을 오래 유지하며 데이터 수신 | 기기 동반 관리(CDM) 존재 감지 또는 `connectedDevice` 포그라운드 서비스 | [S207](https://developer.android.com/develop/connectivity/bluetooth/ble/background), [S208](https://developer.android.com/develop/connectivity/bluetooth/companion-device-pairing) |
| 사용자가 지정한 시각의 동작 | 부정확 알람 우선, 정밀 알람은 사용자 기능이 요구할 때만 | [S201](https://developer.android.com/develop/background-work/services/alarms) |
| 알람·FCM 같은 이벤트 뒤의 긴 작업 | 이벤트 수신부에서 WorkManager·JobScheduler로 위임 | [S201](https://developer.android.com/develop/background-work/services/alarms) |

기본값은 WorkManager이고 포그라운드 서비스는 그 표에서 마지막에 고르는 도구다. 서비스 안에서 예약한 WorkManager·JobScheduler 잡은 Android 16부터 각자의 실행 한도를 받는다(UIDT 잡은 예외)는 점도 출처가 적는다 [S195](https://developer.android.com/develop/background-work/services/fgs/changes).

## 규칙

### R-24-01 백그라운드 작업 도구는 매트릭스로 고르고 기본값은 WorkManager이며 전용 API가 있으면 그것을 먼저 쓴다
- 규칙: 화면 밖 작업은 위 매트릭스로 도구를 고른다. 끊기거나 미뤄져도 되는 작업은 WorkManager가 기본이다. 그 목적의 전용 API(대용량 전송은 UIDT, 외부 기기 존재 감지는 CDM, 도착 감지는 지오펜스)가 있으면 포그라운드 서비스 대신 그것을 쓴다. 포그라운드 서비스를 골랐다면 어떤 전용 API를 검토했고 왜 못 쓰는지를 PR 설명에 한 줄 적는다(마지막 문장은 팩 결정).
- 근거: 공식 가이드는 "In most cases, your best option for running background tasks is to use WorkManager."라고 하고 [S188](https://developer.android.com/develop/background-work/background-tasks), "If an alternative API exists for your use case, we recommend using that API instead of a foreground service as it should help your app perform better."라고 한다 [S188](https://developer.android.com/develop/background-work/background-tasks). 대표 대체 관계(UIDT가 dataSync를, CDM이 connectedDevice를, 지오펜스가 위치 추적 서비스를 대신함)도 같은 문서가 든다. 도구 선택 표는 공식 데이터 전송 문서의 요약이며 [S198](https://developer.android.com/develop/background-work/background-tasks/data-transfer-options), 사용자 시작·진행 알림·끊기면 경험이 나쁨의 세 조건 중 하나라도 아니면 UIDT 대신 WorkManager를 쓰라고 한다. PR 설명에 적는 절차는 출처가 정하지 않은 팩 결정이다.
- 예시:
  ```kotlin
  // Good: 앱이 죽어도 끝나야 하는 동기화는 WorkManager
  workManager.enqueueUniqueWork(SYNC, ExistingWorkPolicy.KEEP, SyncWorker.request())
  // Bad: 끊겨도 되는 동기화를 포그라운드 서비스로 띄운다
  context.startForegroundService(Intent(context, SyncService::class.java))
  ```
- 체크: 새 서비스가 들어온 PR에 매트릭스의 어느 행인지와 검토한 전용 API가 적혀 있는가. 끊겨도 되는 작업이 포그라운드 서비스에서 도는가.

### R-24-02 포그라운드 서비스는 사용자가 인지하는 작업에만 쓰고 앱이 유휴로 판정되지 않으려는 목적으로 쓰지 않는다
- 규칙: 포그라운드 서비스는 사용자가 알아채는 작업(진행 중임을 알림으로 보여 줄 수 있고 사용자가 멈출 수 있는 작업)에만 쓴다. 시스템이 앱을 유휴로 보지 않게 하려고, 또는 프로세스를 살려 두려고 서비스를 띄우지 않는다. 사용자가 시작하지 않은 백그라운드 자동 시작은 R-24-05의 예외 안에서만 한다.
- 근거: "Only use a foreground service when your app needs to perform a task that is noticeable by the user, even when they're not directly interacting with the app." [S189](https://developer.android.com/develop/background-work/services/fgs). 앱 대기 상태 문서는 "Don't start a foreground service just to prevent the system from determining that your app is idle."라고 못 박는다 [S209](https://developer.android.com/training/monitoring-device-state/doze-standby). Play 정책은 사용자에게 이롭고 사용자가 시작하거나 인지하며 종료할 수 있고 시스템이 미루거나 끊으면 경험이 나쁜 경우에만 포그라운드 서비스 권한 선언을 허용하고 "Consider alternatives like WorkManager."를 권한다 [S211](https://support.google.com/googleplay/android-developer/answer/9888379).
- 예시:
  ```kotlin
  // Good: 사용자가 시작한 기록 세션이 진행되는 동안만 서비스가 산다
  fun onStartClicked() = sessionLauncher.start()        // 화면의 사용자 조작에서 시작
  // Bad: 앱이 종료되지 않도록 앱 시작 때 무조건 서비스를 띄운다
  class App : Application() { override fun onCreate() { startForegroundService(keepAlive) } }
  ```
- 체크: 서비스가 사용자 조작이나 사용자가 알고 있는 진행 중 작업과 연결되는가. 서비스를 띄우는 목적이 "프로세스 유지"인 곳이 있는가.

### R-24-03 모든 포그라운드 서비스는 `foregroundServiceType`과 타입별 권한을 매니페스트에 선언한다
- 규칙: `<service>`마다 `android:foregroundServiceType`(여럿이면 `|`)과 `android:exported`(R-40-07)를 적고, 공통 `FOREGROUND_SERVICE`와 그 타입의 `FOREGROUND_SERVICE_*` 권한을 `<uses-permission>`으로 선언한다. `startForeground()`에 넘기는 타입은 매니페스트에 선언한 타입 안에 있어야 한다. 타입을 못 고르겠으면 `specialUse`로 도망가지 않고 매트릭스로 돌아가 전용 API를 다시 본다(마지막 문장은 팩 결정).
- 근거: "You must declare all foreground services with their service types." [S195](https://developer.android.com/develop/background-work/services/fgs/changes). 타입이 매니페스트에 없으면 "the system throws a MissingForegroundServiceTypeException upon calling startForeground()" [S190](https://developer.android.com/develop/background-work/services/fgs/declare). 타입별 권한과 공통 권한이 빠지면 `SecurityException`이 난다(요약) [S197](https://developer.android.com/develop/background-work/services/fgs/troubleshooting). 타입별 권한은 설치 시 자동으로 부여되는 normal 권한이라 사용자가 거절·철회할 수 없다(요약) [S210](https://support.google.com/googleplay/android-developer/answer/13392821). 타입 전체와 사전 조건은 공식 타입 문서에 있다 [S192](https://developer.android.com/develop/background-work/services/fgs/service-types). targetSdk 37에서 포그라운드 서비스의 새 선언 요건은 동작 변경 문서에서 찾지 못했고 아직 실행으로 확인하지 않았다.
- 예시:
  ```xml
  <!-- Good -->
  <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
  <uses-permission android:name="android.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE" />
  <service
      android:name=".RecordingService"
      android:exported="false"
      android:foregroundServiceType="connectedDevice" />
  <!-- Bad: 타입 미선언 -->
  <service android:name=".RecordingService" android:exported="false" />
  ```
- 체크: `<service>`마다 `foregroundServiceType`이 있는가. 그 타입의 `FOREGROUND_SERVICE_*` 권한과 공통 권한이 매니페스트에 있는가.

### R-24-04 서비스는 `startForegroundService()` 뒤 `onStartCommand()`에서 즉시 타입을 넘겨 승격한다
- 규칙: 서비스는 `startForegroundService()`로 시작하고, 서비스 안에서는 `onStartCommand()`가 반환하기 전에 `ServiceCompat.startForeground()`로 알림 ID(0 금지)·알림·타입을 넘겨 승격한다. 알림 우선순위는 `PRIORITY_LOW` 이상이고 알림은 채널을 지정해 만든다. 승격 전에 네트워크·디스크 작업을 하지 않는다(마지막 문장은 팩 결정: 5초 제한을 지키기 위한 운영 규칙).
- 근거: "First, you must start the service by calling context.startForegroundService()." 다음에 서비스가 `ServiceCompat.startForeground()`로 승격하고 보통 `onStartCommand()`에서 부른다 [S191](https://developer.android.com/develop/background-work/services/fgs/launch). 같은 문서가 "Note: The status bar notification must use a priority of PRIORITY_LOW or higher."라고 적고 알림 ID는 0이면 안 된다(예제 주석). 시간 제한은 "Once the service has been created, the service must call its startForeground() method within five seconds." [S202](https://developer.android.com/develop/background-work/services)이고 초과하면 `ForegroundServiceDidNotStartInTimeException`이 난다(요약) [S197](https://developer.android.com/develop/background-work/services/fgs/troubleshooting). 알림 채널의 권장 중요도는 출처가 침묵한다(아래 절).
- 예시:
  ```kotlin
  // Good
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
      ServiceCompat.startForeground(
          this, NOTIFICATION_ID, buildNotification(),
          ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE,
      )
      collector.start()
      return START_NOT_STICKY
  }
  // Bad: 승격 전에 블로킹 초기화 후 승격(5초 초과 위험)
  override fun onStartCommand(...): Int { loadEverything(); startForeground(...) ; return START_STICKY }
  ```
- 체크: `onStartCommand()`에서 `startForeground` 이전에 오래 걸리는 호출이 있는가. 승격에 타입을 넘기는가. 알림 ID가 0이거나 우선순위가 LOW 미만인가.

### R-24-05 백그라운드에서 서비스를 시작해야 하면 시작 제한의 예외에 해당하는 경로에서만 한다
- 규칙: 포그라운드 서비스는 앱이 사용자에게 보이는 상태(사용자 조작)에서 시작하는 것이 기본이다. 리시버·FCM·알람처럼 백그라운드 경로에서 시작해야 하면 공식 문서의 백그라운드 시작 예외 목록 중 무엇에 해당하는지를 시작 코드 옆 주석에 적는다. 카메라·마이크·위치·바디 센서 권한이 필요한 타입(while-in-use)에는 일반 예외에 해당해도 백그라운드에서 만들 수 없다는 별도 제한이 있고 그 제한에도 자체 예외가 있다. 이 팩은 그 예외에 기대지 않고 화면이 보일 때 시작한다(팩 결정). 재부팅 수신(`BOOT_COMPLETED`)에서는 `dataSync`·`camera`·`mediaPlayback`·`phoneCall`·`mediaProjection`·`microphone` 타입을 시작하지 않는다. 주석에 적는 절차는 팩 결정이다.
- 근거: "Apps that target Android 12 (API level 31) or higher can't start foreground services while the app is running in the background, except for a few special cases." 위반하면 `ForegroundServiceStartNotAllowedException`이다 [S193](https://developer.android.com/develop/background-work/services/fgs/restrictions-bg-start). 같은 문서는 while-in-use 권한이 필요한 서비스는 "it cannot create the service while the app is in the background, even if the app falls into one of the exemptions from background start restrictions"라고 하고, `checkSelfPermission()`은 이 상황을 막아 주지 못한다고 한다(요약). 재부팅 제한은 targetSdk 35 이상에 적용되며 "BOOT_COMPLETED receivers are not allowed to launch the following types of foreground services:" 뒤에 위 여섯 타입이 온다 [S147](https://developer.android.com/about/versions/15/behavior-changes-15). `connectedDevice`의 백그라운드 시작 가능 여부는 문서 목록에서 추론한 것이며 실기기로 확인하지 않았다.
- 예시:
  ```kotlin
  // Good: 예외를 적고, 타입 제한을 지킨다
  // 예외: 사용자가 알림의 동작 버튼을 눌러 시작(알림 상호작용)
  override fun onReceive(context: Context, intent: Intent) {
      context.startForegroundService(Intent(context, RecordingService::class.java))
  }
  // Bad: 어떤 예외인지 모른 채 BOOT_COMPLETED에서 dataSync 서비스를 시작한다
  ```
- 체크: 리시버·FCM·알람에서 서비스를 시작하는 코드마다 해당 예외가 주석으로 있는가. `BOOT_COMPLETED` 리시버가 제한된 여섯 타입 서비스를 시작하는가.

### R-24-06 타입의 사전 조건은 서비스 시작 전에 확인하고 시작 실패를 삼키지 않는다
- 규칙: 타입이 런타임 사전 조건(위치 권한, 블루투스 권한 등)을 요구하면 서비스를 시작하기 전에 그 조건을 확인하고 충족하지 못하면 서비스를 시작하지 않는다. `startForegroundService()` 호출은 `ForegroundServiceStartNotAllowedException`을 잡아 서비스 상태(R-24-11)에 실패로 기록하고 화면이 그 상태를 보여 준다. 예외를 잡을 때 `Exception` 전체를 잡지 않는다(마지막 문장은 R-22-10의 방향을 따르는 팩 결정).
- 근거: 사전 조건은 타입마다 다르고 승격 시점에 시스템이 검사하며 서비스를 시작하기 전에 직접 확인하라고 한다(요약) [S191](https://developer.android.com/develop/background-work/services/fgs/launch), [S192](https://developer.android.com/develop/background-work/services/fgs/service-types). 시작 제한 위반 예외는 [S193](https://developer.android.com/develop/background-work/services/fgs/restrictions-bg-start)이, 문제 해결 문서가 다루는 예외 목록은 [S197](https://developer.android.com/develop/background-work/services/fgs/troubleshooting)이 적는다. 시작 요청을 어느 계층(ViewModel·Repository·UseCase)이 맡을지는 출처가 침묵하므로 정하지 않는다.
- 예시:
  ```kotlin
  // Good
  fun start(context: Context) {
      if (!hasRequiredPermission()) { state.update { it.failed(Reason.PermissionMissing) }; return }
      try {
          context.startForegroundService(Intent(context, RecordingService::class.java))
      } catch (e: ForegroundServiceStartNotAllowedException) {
          state.update { it.failed(Reason.StartNotAllowed) }
      }
  }
  // Bad: 조건 확인 없이 시작하고 예외가 앱을 죽이게 둔다
  context.startForegroundService(Intent(context, RecordingService::class.java))
  ```
- 체크: 사전 조건이 있는 타입의 서비스 시작 앞에 권한 확인이 있는가. `ForegroundServiceStartNotAllowedException`을 처리하는가. 실패가 화면에서 보이는 상태로 이어지는가.

### R-24-07 시간 제한이 있는 타입은 `onTimeout()`을 구현해 즉시 서비스를 멈춘다
- 규칙: `dataSync`·`mediaProcessing` 타입(targetSdk 35 이상)을 쓰는 서비스는 `onTimeout(startId, fgsType)`을 구현하고 안에서 곧바로 `stopSelf()`를 부른다. 이 두 타입은 사용자 조작으로만 시작한다. 타임아웃 시 진행 상태는 서비스 상태(R-24-11)에 남겨 화면이 알 수 있게 한다(마지막 문장은 팩 결정).
- 근거: "The system permits dataSync and mediaProcessing foreground services to run for a total of 6 hours in a 24-hour period, after which the system calls the running service's Service.onTimeout(int, int) method (introduced in Android 15)." 시간 초과 뒤 `stopSelf()`를 부르지 않으면 시스템이 내부 예외로 앱을 끝내고, 한도를 다 쓰면 새 dataSync 서비스 시작이 `ForegroundServiceStartNotAllowedException`이다(요약). 문서가 적는 회피책은 `onTimeout` 구현, 6시간 이내 유지, 사용자 상호작용으로만 시작, 대체 API 사용이다 [S194](https://developer.android.com/develop/background-work/services/fgs/timeout). `shortService`는 약 3분 제한이 따로 있다 [S192](https://developer.android.com/develop/background-work/services/fgs/service-types). 다른 타입의 시간 제한은 이 노트가 확인한 범위에서는 적혀 있지 않다.
- 예시:
  ```kotlin
  // Good
  override fun onTimeout(startId: Int, fgsType: Int) {
      collector.markStopped(StopReason.TimedOut)
      stopSelf()
  }
  // Bad: dataSync 서비스에 onTimeout이 없다
  ```
- 체크: 매니페스트에 `dataSync`·`mediaProcessing`이 있는 서비스에 `onTimeout`이 있는가. 그 안에서 `stopSelf()`를 부르는가.

### R-24-08 시작한 서비스는 재시작 정책을 반환값으로 명시하고 null 인텐트를 처리하며 끝나면 스스로 멈춘다
- 규칙: `onStartCommand()`는 서비스 성격에 맞는 반환값을 명시한다. 계속 대기하는 서비스는 `START_STICKY`, 하던 작업을 즉시 이어야 하면 `START_REDELIVER_INTENT`, 그 밖은 `START_NOT_STICKY`다. `START_STICKY`로 재시작되면 인텐트가 `null`이므로 `null`을 처리한다. 작업이 끝나면 `stopSelf()`를 불러 서비스를 멈춘다. 반환값을 고른 이유는 서비스 KDoc에 한 줄 적는다(마지막 문장은 팩 결정).
- 근거: "if your service is started, you must design it to gracefully handle restarts by the system." 반환값의 뜻은 "START_NOT_STICKY If the system kills the service after onStartCommand() returns, do not recreate the service unless there are pending intents to deliver.", "START_STICKY If the system kills the service after onStartCommand() returns, recreate the service and call onStartCommand() , but do not redeliver the last intent."이고 `START_STICKY` 재시작 시 시스템은 대기 중인 인텐트가 없으면 `null` 인텐트로 부른다 [S202](https://developer.android.com/develop/background-work/services). 시작 서비스는 `stopSelf()`·`stopService()`로 스스로 종료해야 한다(요약) [S202](https://developer.android.com/develop/background-work/services). 시스템이 죽인 서비스가 재시작될 때 백그라운드 시작 제한을 어떻게 받는지는 출처가 침묵한다.
- 예시:
  ```kotlin
  // Good
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
      ServiceCompat.startForeground(/* ... */)
      if (intent == null) collector.resume() else collector.start(intent.toRequest())
      return START_STICKY
  }
  // Bad: 반환값을 아무렇게나 두고 intent!!를 쓴다
  override fun onStartCommand(intent: Intent?, f: Int, id: Int): Int { run(intent!!); return super.onStartCommand(intent, f, id) }
  ```
- 체크: `onStartCommand()`가 반환값을 명시하는가. `intent!!`가 있는가. 작업 완료 경로에 `stopSelf()`가 있는가.

### R-24-09 사용자의 종료·알림 제거·알림 권한 거절이 서비스 동작을 바꾼다고 가정하지 않고 상태의 진실은 Repository에 둔다
- 규칙: (1) 사용자가 작업 관리자의 Stop으로 서비스를 끄면 앱 전체가 종료되고 콜백이 오지 않으므로, 서비스 종료 정리를 `onDestroy()`에만 의존하지 않고 다음 시작에서 `ApplicationExitInfo`의 `REASON_USER_REQUESTED`를 확인해 남은 상태를 정리한다. (2) 포그라운드 서비스 알림은 사용자가 지울 수 있으므로 서비스가 돌고 있는지의 진실은 알림이 아니라 서비스 상태(R-24-11)로 삼는다. (3) `POST_NOTIFICATIONS` 권한을 서비스 시작 조건에 넣지 않는다. 이 권한이 없어도 서비스는 시작되며 알림은 알림 서랍이 아니라 작업 관리자에서 보인다. 정리·복구 방식의 세부는 앱이 정한다.
- 근거: 사용자가 Stop 버튼을 누르면 "The system removes your app from memory. Therefore, your entire app stops, not just the running foreground service." 그리고 "The system doesn't send your app any callbacks when the user taps the Stop button." 다음 시작에서 `REASON_USER_REQUESTED`를 확인하라고 한다 [S196](https://developer.android.com/develop/background-work/services/fgs/handle-user-stopping). Android 14부터 "If your app shows non-dismissable foreground notifications to users, Android 14 has changed the behavior to allow users to dismiss such notifications."이고 잠금 상태와 전체 삭제에서는 여전히 지울 수 없다 [S206](https://developer.android.com/about/versions/14/behavior-changes-all). 알림 권한은 "Apps don't need to request the POST_NOTIFICATIONS permission in order to launch a foreground service. However, apps must include a notification when they start a foreground service, just as they do on previous versions of Android." [S205](https://developer.android.com/develop/ui/views/notifications/notification-permission). 알림을 지웠을 때 서비스가 계속 도는지에 대한 명시 문장은 찾지 못했다. 진실을 Repository에 두는 것은 `SERVICE_STATE`에서 나온 팩 결정이다.
- 예시:
  ```kotlin
  // Good: 다음 시작에서 사용자 종료를 확인해 남은 상태를 정리한다
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {   // getHistoricalProcessExitReasons 는 API 30+
      val reasons = activityManager.getHistoricalProcessExitReasons(null, 0, 1)
      if (reasons.firstOrNull()?.reason == ApplicationExitInfo.REASON_USER_REQUESTED) sessionRepository.clearStale()
  }
  // Bad: onDestroy()가 항상 불린다고 믿고 정리를 그 안에만 둔다 / 알림이 보이니 서비스가 돈다고 판단한다
  ```
- 체크: `getHistoricalProcessExitReasons`를 `SDK_INT >= R` 가드 없이 부르는가(minSdk 26). 서비스 종료 정리가 `onDestroy()`에만 있는가. "알림이 보이는가"로 서비스 실행 여부를 판정하는 코드가 있는가. `POST_NOTIFICATIONS` 거절 시 서비스를 시작하지 않는 분기가 있는가.

### R-24-10 서비스는 `@AndroidEntryPoint`로 의존을 필드 주입받는 별도 진입점으로 허용한다
- 규칙: 포그라운드 서비스 클래스에는 `@AndroidEntryPoint`를 붙이고 의존은 private이 아닌 `@Inject lateinit var` 필드로 받는다(R-14-02의 진입점 예외). 서비스의 UI 진입점은 R-14-03의 루트 Activity 하나가 그대로이며, 서비스는 UI 계층 밖의 진입점으로 나란히 둔다. 서비스 클래스는 `Service`를 상속하고 `LifecycleService`는 규칙으로 요구하지 않는다(팩 결정, Hilt 2.60.1과 `LifecycleService` 조합은 검증되지 않았다). Hilt가 지원하지 않는 컴포넌트는 R-14-09의 `@EntryPoint`를 쓴다.
- 근거: Hilt 문서는 지원 클래스로 Application·ViewModel·Activity·Service·BroadcastReceiver를 나열하고 Service용 컴포넌트는 `ServiceComponent`(`Service#onCreate()`에서 생성, `Service#onDestroy()`에서 소멸)라고 한다(표 요약). "This serves as the single DI entry point for your entire UI hierarchy"라는 문장의 범위는 UI 계층이며 진입점의 총개수 상한은 문서에 없다. 주입 필드는 "Fields injected by Hilt cannot be private." [S05](https://developer.android.com/training/dependency-injection/hilt-android). `@AndroidEntryPoint`를 `LifecycleService`에 붙이는 공식 예시는 찾지 못했고 아직 실행으로 확인하지 않았다.
- 예시:
  ```kotlin
  // Good
  @AndroidEntryPoint
  class RecordingService : Service() {
      @Inject lateinit var collector: SessionCollector
      override fun onBind(intent: Intent?): IBinder? = null
  }
  // Bad: 서비스가 생성자 주입을 기대하거나 private 필드로 주입받는다
  @AndroidEntryPoint class RecordingService : Service() { @Inject private lateinit var collector: SessionCollector }
  ```
- 체크: `@AndroidEntryPoint` Service의 `@Inject` 필드가 private인가. 서비스가 `@Inject`로 받는 타입이 로직을 담은 클래스가 아니라 Repository·DAO를 직접 받는가.

### R-24-11 서비스와 화면의 상태 공유는 싱글턴 Repository의 `StateFlow`로 하고 Binder 바인딩을 쓰지 않는다
- 규칙: 서비스가 만든 상태(진행 중 여부, 최신 값, 실패 사유)는 `@Singleton` Repository가 가진 `StateFlow`에 서비스가 쓰고, ViewModel은 같은 Repository를 관찰한다. Repository는 `MutableStateFlow`를 private으로 두고 갱신 함수를 공개한다(R-22-05). 화면이 서비스를 `bindService()`로 붙잡거나 서비스 인스턴스를 직접 참조하지 않는다. ViewModel이 서비스를 시작하는 경로는 R-24-06을 따른다.
- 근거: 확정 결정 `SERVICE_STATE`(사용자 확정, 2026-09-30)이다. 공식 문서는 같은 프로세스의 로컬 서비스에 Binder를 "This is the preferred technique when your service is merely a background worker for your own application."이라 적는다 [S203](https://developer.android.com/develop/background-work/services/bound-services). 이 팩은 그 권장을 채택하지 않고 UDF와 계층 규칙(R-11-03: UI는 Repository까지만 부른다)에 맞춰 Repository 공유를 골랐다. 시작되지 않은 서비스는 "After the service is unbound from all of its clients, the system destroys it."라는 문장이 있어 바인딩만으로 서비스를 유지할 수 없다 [S202](https://developer.android.com/develop/background-work/services). 싱글턴 Repository의 `StateFlow`로 서비스 상태를 공유하라는 공식 문장은 없으며 아직 실행으로 확인하지 않았다.
- 예시:
  ```kotlin
  // Good
  @Singleton
  class DefaultSessionRepository @Inject constructor() : SessionRepository {
      private val _state = MutableStateFlow(SessionState.Idle)
      override val state: StateFlow<SessionState> = _state.asStateFlow()
      override fun update(transform: (SessionState) -> SessionState) = _state.update(transform)
  }
  // Bad: 화면이 Binder로 서비스 인스턴스를 붙잡고 직접 읽는다
  bindService(intent, connection, BIND_AUTO_CREATE)
  ```
- 체크: UI 계층에 `bindService`나 서비스 클래스 참조가 있는가. 서비스가 쓰는 상태가 Repository 밖의 필드에 있는가.

### R-24-12 서비스는 자기 코루틴 스코프를 만들고 `onDestroy()`에서 취소한다
- 규칙: 서비스가 코루틴을 띄울 때는 서비스가 `SupervisorJob()`과 주입한 디스패처(R-22-02)로 자기 `CoroutineScope`를 만들고 `onDestroy()`에서 취소한다. `GlobalScope`(R-22-01)나 다른 수명의 스코프에서 서비스 작업을 시작하지 않는다.
- 근거: 팩 결정이다. 서비스 안 코루틴 스코프를 만들고 취소하라는 공식 권고는 찾지 못했다(`LifecycleService`·`lifecycleScope`의 정의만 있고 서비스에서 쓰라는 문장은 없다). 스코프를 취소 가능한 수명에 묶고 디스패처를 주입한다는 방향은 R-22-01·R-22-02이며, 출처가 든 서비스 예제 중 UIDT 예제가 `CoroutineScope(Dispatchers.IO)`를 서비스 필드로 만든다 [S199](https://developer.android.com/develop/background-work/background-tasks/uidt). 이 조합은 아직 실행으로 확인하지 않았다.
- 예시:
  ```kotlin
  // Good
  @Inject @IoDispatcher lateinit var io: CoroutineDispatcher
  private val scope by lazy { CoroutineScope(SupervisorJob() + io) }
  override fun onDestroy() { scope.cancel(); super.onDestroy() }
  // Bad
  GlobalScope.launch { collector.collect() }
  ```
- 체크: 서비스가 만든 스코프를 `onDestroy()`에서 취소하는가. 서비스 안에 `Dispatchers.` 직접 참조나 `GlobalScope`가 있는가.

### R-24-13 서비스 클래스에는 수집·판정 로직을 두지 않고 로컬 단위 테스트가 되는 일반 클래스에 둔다
- 규칙: 서비스는 알림 승격·수명 콜백·스코프 관리만 맡고, 수집·판정·상태 갱신 로직은 생성자 주입 일반 클래스(예: `SessionCollector`)에 둔다. 그 클래스는 fake를 넣어 `src/test`에서 검증한다(R-14-10). 서비스 자체는 계측 테스트로 승격과 정지 배선이 동작하는지만 대표 흐름 한두 개로 확인한다(R-30-12, `ServiceTestRule`, R-30-14).
- 근거: 팩 결정이다. 공식 서비스 테스트 문서는 로컬 서비스에 계측 테스트를 만들 수 있고 `ServiceTestRule`이 서비스를 시작·종료해 준다고 하며 [S204](https://developer.android.com/training/testing/other-components/services), 로직을 별도 클래스로 캡슐화하라는 문장은 `IntentService` 한정이다. Hilt 문서는 일반 클래스를 fake와 함께 Hilt 없이 만들 수 있다는 예를 든다 [S172](https://developer.android.com/training/dependency-injection/hilt-testing). 서비스 로직 분리를 일반 권고로 적은 출처는 없으므로 R-30-01과 같은 방향의 팩 결정이다.
- 예시:
  ```kotlin
  // Good: 로직은 일반 클래스, 로컬 테스트
  class SessionCollector @Inject constructor(private val repository: SessionRepository, private val source: SensorSource)
  class SessionCollectorTest { private val collector = SessionCollector(FakeSessionRepository(), FakeSensorSource()) }
  // Bad: 서비스 안에 수집 루프와 판정 분기를 직접 쓴다
  class RecordingService : Service() { fun onSample(v: Int) { if (v > LIMIT) { /* ... */ } } }
  ```
- 체크: 서비스 클래스에 조건 분기·수집 루프·상태 계산이 있는가. 서비스에서 뺀 클래스에 `src/test` 테스트가 있는가.

### R-24-14 포그라운드 서비스를 넣는 릴리스는 Play 콘솔의 타입별 선언 자료를 준비한다
- 규칙: targetSdk 34 이상 앱이 포그라운드 서비스 타입을 쓰면 Play Console 앱 콘텐츠 페이지에 타입별로 사용 기능 설명, 시스템이 미루거나 끊었을 때의 사용자 영향, 기능을 보여 주는 영상 링크를 낸다. 타입이 늘거나 바뀌면 선언도 함께 고친다. 선언 자료 준비를 릴리스 체크에 넣는다(마지막 문장은 팩 결정).
- 근거: 타입을 새 선언으로 앱 콘텐츠 페이지에 선언해야 한다("declare any foreground service types that you use in a new declaration on the App content page"). 타입마다 "Provide a description of the app functionality that is using each foreground service type.", "Describe the user impact if: the task is deferred by the system (does not start immediately); and/or the task is interrupted by the system (paused and/or restarted).", "Include a link to a video demonstrating each foreground service feature."를 내야 하고 미리 정해진 사용 사례 목록에서 고른다 [S210](https://support.google.com/googleplay/android-developer/answer/13392821). 정책 페이지는 갱신일이 표기돼 있지 않아 문구가 바뀌었을 수 있다.
- 예시:
  ```text
  # Good: 릴리스 PR 설명
  connectedDevice — 설명: 외부 기기에서 수신하는 값을 화면이 꺼져도 기록 / 지연 영향: 기록 공백 발생 / 영상: <링크>
  # Bad: 새 타입을 매니페스트에만 추가하고 콘솔 선언은 심사 반려 뒤에 안다
  ```
- 체크: 릴리스에 새 `foregroundServiceType`이 들어왔는가. 그렇다면 그 타입의 설명·영향·영상이 준비됐는가.

## 출처가 침묵하는 것 (규칙으로 쓰지 않음)

- **알림 채널 중요도**: 포그라운드 서비스 전용 채널의 권장 중요도가 없다. 알림 우선순위 하한 `PRIORITY_LOW`만 규칙(R-24-04)이다.
- **재시작된 서비스의 상태 복구**: 시스템이 죽인 `START_STICKY` 서비스가 재시작할 때 백그라운드 시작 제한과의 관계, 이어서 무엇을 복구할지는 출처가 침묵한다. R-24-08·R-24-09는 `null` 인텐트 처리와 사용자 종료 확인까지만 정한다.
- **블루투스 클래식(RFCOMM) 연결의 백그라운드 유지**: 출처가 BLE·CDM 중심이다. Android 17(targetSdk 37)에서 RFCOMM 소켓 읽기가 연결 종료 시 -1을 돌려준다는 변경만 있다.
- **서비스 시작 요청 계층**: 포그라운드 서비스 시작을 ViewModel·Repository·UseCase 중 누가 요청할지는 출처가 침묵한다(R-24-06은 예외 처리만 요구).
- **`LifecycleService`와 Hilt 2.60.1 조합**: 공식 예시가 없고 실빌드로 검증하지 않았다. 규칙으로 요구하지 않는다(R-24-10).
- **서비스 코루틴 스코프·서비스 로직 분리의 공식 권고**: 팩 결정으로만 존재한다(R-24-12, R-24-13).
- **Doze가 포그라운드 서비스 실행에 미치는 영향**: Doze 문서는 App Standby 조건에서만 포그라운드 서비스를 언급한다.
- **알림을 지웠을 때 서비스가 계속 도는지**에 대한 명시 문장이 없다(R-24-09는 알림을 진실의 원천으로 삼지 않는 데까지만 정한다).
- **Play 정책의 블루투스 백그라운드 사용 조항**: 찾지 못했다.
- **targetSdk 37에서 포그라운드 서비스 시작·타입의 새 요건**: 동작 변경 문서에 없다. 백그라운드 오디오 강화만 있다.
- **`connectedDevice`의 while-in-use·`BOOT_COMPLETED` 시작 가능 여부**: 문서 목록에서의 추론이며 실기기로 확인하지 않았다.
- **Robolectric 서비스 테스트**: `Robolectric.buildService`의 존재만 확인했고 사용 가이드가 없어 규칙으로 쓰지 않았다.
