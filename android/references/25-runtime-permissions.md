# 25 런타임 권한

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

어떤 권한을 매니페스트에 넣을지(최소화, 커스텀 권한)는 R-40-12가 소유한다. 이 문서는 넣기로 한 권한을 **언제·어디서·어떤 모양으로 요청하는지**만 정한다. 화면 구조는 R-13-06(Route/Screen), 상태 모양은 R-12-03·R-12-10을 따른다.

확정 결정 `PERMISSION_LIB`(2026-09-30): Accompanist Permissions를 쓰지 않고 공식 기본 경로인 `rememberLauncherForActivityResult`를 직접 쓴다. `PERMISSION_STATE`(2026-09-30): 런처는 UI가 갖고 ViewModel은 프레임워크 타입 없는 일반 enum 값만 다룬다. 조사 노트는 `research/runtime-permissions.md`. 이 문서의 코드는 아직 실행으로 확인하지 않았다.

## 결정 매트릭스 — 권한 대신 쓸 방법 (R-25-02)

| 하고 싶은 일 | 권한 없는 방법(기본값) | 이 방법이 맞지 않을 때 | 근거 |
|---|---|---|---|
| 사진·동영상 선택 | photo picker(`PickVisualMedia`) | 핵심 기능이 피커로 안 될 때만 `READ_MEDIA_*`, Play Console 선언 필요 | [S213](https://developer.android.com/privacy-and-security/minimize-permission-requests) [S224](https://developer.android.com/training/data-storage/shared/photopicker) [S227](https://support.google.com/googleplay/android-developer/answer/9888170) |
| 사진 촬영 | `ACTION_IMAGE_CAPTURE` 인텐트(`CAMERA` 선언 안 함) | 앱 안 카메라 화면이 기능이면 `CAMERA` | [S213](https://developer.android.com/privacy-and-security/minimize-permission-requests) |
| 문서 열기·저장 | Storage Access Framework | 넓은 파일 접근은 별도 심사 | [S233](https://developer.android.com/guide/topics/providers/document-provider) [S227](https://support.google.com/googleplay/android-developer/answer/9888170) |
| 근처 블루투스 기기 페어링 | Companion Device Manager(API 26+) | 페어링·연결을 직접 제어하면 R-25-09의 권한 세트 | [S221](https://developer.android.com/develop/connectivity/bluetooth/bt-permissions) [S208](https://developer.android.com/develop/connectivity/bluetooth/companion-device-pairing) |
| 현재 위치 한 번 | 위치 버튼(R-25-11, 위치 권한·`USE_LOCATION_BUTTON` 선언은 필요) 또는 주소 직접 입력 | 지속 위치는 R-25-10의 위치 권한 | [S213](https://developer.android.com/privacy-and-security/minimize-permission-requests) [S223](https://developer.android.com/guide/topics/permissions/private-alternatives/location-button) |

## 결정 매트릭스 — 권한이 필요할 때의 요청 조건

| 대상 | 런타임 요청 | 요청 시점 | 규칙 |
|---|---|---|---|
| Bluetooth, Android 12+ | `BLUETOOTH_SCAN`·`BLUETOOTH_CONNECT`·`BLUETOOTH_ADVERTISE`(필요한 것만) | 탐색·광고·통신 직전 | R-25-09 |
| Bluetooth, Android 8~11 | `ACCESS_FINE_LOCATION`(스캔용) | 스캔 직전 | R-25-09 |
| 위치, 포그라운드 | `ACCESS_COARSE_LOCATION`(정밀은 필요할 때만 추가) | 기능 조작 시 | R-25-10 |
| 위치, 백그라운드 | `ACCESS_BACKGROUND_LOCATION` | 포그라운드 허용 뒤, 공개 화면 다음 | R-25-10·R-25-12 |
| 알림, Android 13+ | `POST_NOTIFICATIONS` | 사용자 동작·앱에 익숙해진 뒤 | R-25-13 |

## 규칙

### R-25-01 권한은 그 권한이 필요한 기능을 사용자가 시작할 때 요청하고 앱 시작 때 일괄 요청하지 않는다
- 규칙: 요청은 권한이 필요한 기능을 사용자가 조작한 시점에 한다. `Application`·`MainActivity` 시작이나 첫 화면 진입 때 여러 권한을 한꺼번에 요청하지 않는다. 한 기능이 쓰는 권한만 그 기능을 시작할 때 요청한다.
- 근거: 공식 첫 원칙이 "Ask for a permission in context, when the user starts to interact with the feature that requires it." 이다 [S212](https://developer.android.com/training/permissions/requesting). "Don't overburden the user by requesting every permission at app startup." [S214](https://developer.android.com/training/permissions/usage-notes). Play 정책도 민감한 권한을 맥락 안에서 점진적으로(incremental requests) 요청하라고 한다 [S227](https://support.google.com/googleplay/android-developer/answer/9888170).
- 예시:
  ```kotlin
  // Good: 사용자가 누른 버튼에서 요청
  Button(onClick = onScanClick) { Text(stringResource(R.string.scan)) }
  // Bad: 화면에 들어오자마자 요청
  LaunchedEffect(Unit) { launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) }
  ```
- 체크: `Application`·`MainActivity`·최상위 Route의 진입 effect에서 `launch()`·`requestPermissions`를 부르는가.

### R-25-02 권한 없이 되는 방법이 있으면 그 방법을 쓰고 권한 요청 흐름은 맞지 않을 때만 만든다
- 규칙: 위 첫 매트릭스의 "권한 없는 방법"이 기능 요구를 채우면 그 방법을 쓰고 런타임 요청 흐름을 만들지 않는다. 대안으로 대체된 권한 선언은 넣지 않는다(예: Photo Picker를 쓰면 `READ_MEDIA_*`, 촬영을 인텐트로 하면 `CAMERA`). 대안 자체가 요구하는 선언은 넣는다(예: 위치 버튼의 `USE_LOCATION_BUTTON`과 위치 권한, [S223](https://developer.android.com/guide/topics/permissions/private-alternatives/location-button)). 권한을 쓰는 쪽을 고르면 PR 설명에 대안이 맞지 않는 이유를 적는다. 쓰지 않는 권한을 지우는 일 자체는 R-40-12가 소유하고 이 규칙은 기능별 대안 매핑만 정한다.
- 근거: 공식 문서가 대안을 나열한다 — "Use pickers and permission alternatives which grant access to sensitive permissions in scoped circumstances such as the photo picker, contact picker, and location button" 이고 "The photo picker doesn't require any runtime permissions to use." 이며 촬영은 "don't declare the CAMERA permission. Instead, invoke the ACTION_IMAGE_CAPTURE intent action." 이다 [S213](https://developer.android.com/privacy-and-security/minimize-permission-requests). 페어링은 "The CDM system provides a pairing UI on behalf of your app and doesn't require location permissions." [S221](https://developer.android.com/develop/connectivity/bluetooth/bt-permissions). Play는 Android 13+ 타깃 앱이 시스템 피커로 핵심 기능이 안 될 때만 `READ_MEDIA_*`를 허용한다(요약) [S227](https://support.google.com/googleplay/android-developer/answer/9888170). PR 설명 요구는 팩 선택이다.
- 예시:
  ```kotlin
  // Good: 권한 선언 없이 사진 선택
  val pick = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> onPicked(uri) }
  // Bad: 프로필 사진 첨부 하나를 위해 READ_MEDIA_IMAGES 를 요청한다
  ```
- 체크: `READ_MEDIA_*`·`CAMERA`·`READ_EXTERNAL_STORAGE`·페어링 목적의 위치 권한이 매니페스트에 있다면 매트릭스의 대안으로 못 하는 이유가 적혀 있는가.

### R-25-03 런타임 권한은 rememberLauncherForActivityResult와 RequestPermission 계약으로 요청한다
- 규칙: 단일 권한은 `RequestPermission`, 여러 권한은 `RequestMultiplePermissions` 계약을 `rememberLauncherForActivityResult`로 만든다. `launch()`는 클릭 같은 사용자 콜백에서 부르고 컴포지션 본문에서 부르지 않는다. 조합 뒤 실행이 필요하면 `LaunchedEffect`·`DisposableEffect` 안에서 부른다. `ActivityCompat.requestPermissions`·`onRequestPermissionsResult` 직접 관리와 `accompanist-permissions` 의존은 쓰지 않는다(`PERMISSION_LIB`).
- 근거: 공식 Compose 문서가 "The same Activity Result API and rememberLauncherForActivityResult() explained above can be used to request runtime permissions using the RequestPermission contract for a single permission or RequestMultiplePermissions contract for multiple permissions." 라고 하고, 컴포지션 안 호출은 "If you attempt to launch a request from inside the composable you’ll get a runtime error because the ActivityResultLauncher has not been initialized at this point." 라고 한다 [S216](https://developer.android.com/develop/ui/compose/libraries). `RequestPermission` 계약은 "it is the recommended solution when possible" 이다 [S212](https://developer.android.com/training/permissions/requesting). Accompanist Permissions는 공식 문서가 여전히 언급하고 deprecated도 아니지만 "The permission APIs are currently experimental and they could change at any time." 라는 경고가 있고 [S217](https://google.github.io/accompanist/permissions/) 최신 배포가 2025-04-28의 0.37.3이다 [S218](https://repo1.maven.org/maven2/com/google/accompanist/accompanist-permissions/maven-metadata.xml). 쓰지 않기로 한 것은 팩 결정(`PERMISSION_LIB`)이다.
- 예시:
  ```kotlin
  // Good
  val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> onResult(granted) }
  Button(onClick = { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }) { /* ... */ }
  // Bad
  launcher.launch(Manifest.permission.POST_NOTIFICATIONS) // 컴포지션 본문 — 런타임 오류
  ActivityCompat.requestPermissions(activity, arrayOf(permission), 1)
  ```
- 체크: `requestPermissions`·`onRequestPermissionsResult`·`accompanist-permissions`가 코드나 카탈로그에 있는가. `launch()`가 컴포지션 본문에서 직접 불리는가.

### R-25-04 런처는 Route 컴포저블이 갖고 ViewModel은 프레임워크 타입 없는 권한 상태 값만 다룬다
- 규칙: `rememberLauncherForActivityResult`·`shouldShowRequestPermissionRationale`는 Route 컴포저블(R-13-06)에서만 부른다. `checkSelfPermission`은 Route와, 권한이 필요한 작업의 진입점(서비스·리시버·작업 시작 직전, R-24-06)에서 부를 수 있다 — 후자는 그 컴포넌트가 직접 하거나 주입받은 확인 인터페이스로 한다. ViewModel과 UiState는 `Granted`·`RationaleNeeded`·`Denied` 같은 일반 enum 값만 갖는다(R-12-10). ViewModel이 "지금 요청하라"는 이벤트를 `Channel`·`SharedFlow`로 내보내지 않는다(R-12-03). 권한이 필요하면 UiState가 그 상태를 담고 Screen이 버튼을 그리며, 요청은 그 버튼 클릭에서 Route가 `launch()`한다(`PERMISSION_STATE`).
- 근거: "The UI is in charge of screen-specific behavior logic such as navigation calls, click events, and obtaining permission requests. The ViewModel contains business logic and converts the results from lower layers of the hierarchy into UI state." [S09](https://developer.android.com/topic/architecture/ui-layer/events). 런타임 권한은 UI 수명주기에 종속된 상태의 예로 든다 — "Examples of this include runtime permissions and getting configuration dependent resources like localized strings." [S220](https://developer.android.com/topic/architecture/ui-layer/stateholders). ViewModel에 넘길 값의 형태를 정한 공식 문장은 없다. enum 값으로 한정하고 트리거를 UiState와 클릭으로 정한 것은 팩 결정(`PERMISSION_STATE`)이다.
- 예시:
  ```kotlin
  // Good
  enum class PermissionStatus { Granted, RationaleNeeded, Denied }
  data class ScanUiState(val permission: PermissionStatus = PermissionStatus.Denied)
  @Composable fun ScanRoute(viewModel: ScanViewModel = hiltViewModel()) {
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()
      val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
          viewModel.onPermissionResult(if (granted) PermissionStatus.Granted else PermissionStatus.Denied)
      }
      ScanScreen(uiState, onGrantClick = { launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) })
  }
  // Bad
  private val _requests = Channel<String>() // ViewModel 이 요청을 push
  class ScanViewModel(private val activity: Activity)
  ```
- 체크: ViewModel·UiState에 `android.*`·`androidx.activity.*` 타입이나 권한 문자열 요청 이벤트 스트림이 있는가. 런처가 Route 밖에서 만들어지는가.

### R-25-05 권한이 필요한 동작을 실행할 때마다 허용 여부를 새로 확인한다
- 규칙: 동작 직전(클릭 처리 시점)에 `checkSelfPermission`으로 허용 여부를 확인한다. 이전 확인 결과를 `remember`·필드·저장소에 두고 이후 동작의 허용 근거로 쓰지 않는다. UiState의 권한 값은 Route가 시스템에서 읽어 넣은 값이지 저장해 둔 값이 아니다.
- 근거: "You must check whether you have a permission every time you perform an operation that requires that permission." [S212](https://developer.android.com/training/permissions/requesting). 권한은 실행 중에도 사라진다 — "If your app targets Android 11 (API level 30) or higher and is not used for a few months, the system protects user data by automatically resetting the sensitive runtime permissions that the user had granted your app." [S212](https://developer.android.com/training/permissions/requesting). 대응은 기능을 쓸 때 필요한 권한을 확인하는 것이다(요약) [S225](https://developer.android.com/about/versions/11/privacy/permissions). 원타임 권한을 사용자가 회수하면 "your app's process terminates." 라서 복귀는 프로세스 재생성 경로다 [S212](https://developer.android.com/training/permissions/requesting). 앱 수면에서 깨어나도 권한은 자동으로 되돌아오지 않고 사용자가 다시 허용해야 한다(요약) [S226](https://developer.android.com/topic/performance/app-hibernation). 알림 권한도 언제든 회수된다 [S205](https://developer.android.com/develop/ui/views/notifications/notification-permission).
- 예시:
  ```kotlin
  // Good
  onClick = { if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) viewModel.onScan() else onNeedPermission() }
  // Bad: 처음 한 번 읽은 값을 계속 믿는다
  val granted = remember { context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED }
  ```
- 체크: 권한 확인 결과를 `remember`·`SharedPreferences`·DataStore에 캐시하는가.

### R-25-06 근거 UI는 shouldShowRequestPermissionRationale이 true일 때만 보이고 취소할 수 있게 한다
- 규칙: 클릭 시 분기는 셋이다. 허용됨이면 기능을 실행하고, `shouldShowRequestPermissionRationale`이 true이면 근거 UI를 보이고, 그 외에는 곧바로 `launch()`한다. 근거 UI에는 무엇에 접근하고 허용하면 무엇을 얻는지 적고 취소 수단을 둔다. false일 때 근거 UI를 끼우지 않는다. 시스템 대화상자는 바꿀 수 없으므로 맥락은 버튼 문구와 주변 UI로 준다.
- 근거: 공식 코드는 허용됨 → 실행, `shouldShowRationale` → 교육 UI, 그 외 → `launch` 의 3분기이고 교육 UI에 취소 버튼을 둔다(요약) [S212](https://developer.android.com/training/permissions/requesting). "Don't block the user. Always provide the option to cancel an educational UI flow, such as a flow that explains the rationale for requesting permissions." 이고 근거 문구는 "clearly explain what data your app is trying to access and what benefits the app can provide to the user if they grant the runtime permission." 이며 "Your app cannot customize the dialog that appears when you call launch()." 이다 [S212](https://developer.android.com/training/permissions/requesting). 알림 문서도 "Unless shouldShowRequestPermissionRationale() returns true, your app doesn't need to display the middle screen" 라고 한다 [S205](https://developer.android.com/develop/ui/views/notifications/notification-permission). 모든 권한에 사전 안내를 강제하는 공식 문장은 없다(R-25-12는 Play가 요구하는 경우만 다룬다).
- 예시:
  ```kotlin
  // Good: Route 안 — activity 는 Route 에서 얻는다
  when {
      context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED -> viewModel.onScan()
      activity.shouldShowRequestPermissionRationale(permission) -> viewModel.onRationaleNeeded() // Screen 이 [계속][취소] 를 그린다
      else -> launcher.launch(permission)
  }
  // Bad: 근거 화면을 거부 이력과 무관하게 항상 끼우고 취소를 두지 않는다
  ```
- 체크: 근거 UI 표시 조건이 `shouldShowRequestPermissionRationale`인가. 근거 UI에 취소 수단이 있는가.

### R-25-07 거부되면 막힌 기능만 줄이고 전체 화면 차단이나 반복 재요청을 하지 않는다
- 규칙: 거부 결과가 오면 그 권한이 필요한 기능만 비활성하거나 대체 경로로 바꾸고 나머지 앱은 계속 쓰게 한다. 막힌 기능이 있는 자리에 무엇이 왜 안 되는지 구체적으로 알린다. 전체 화면 경고로 앱 사용을 막지 않고, 거부 직후 같은 요청을 반복하지 않는다.
- 근거: "Don't block the user interface. In other words, don't display a full-screen warning message that prevents users from continuing to use your app at all." / "Be specific. Don't display a generic message." / "persistent nagging to reconsider is not respectful of their choice." / "Minimize functionality loss. Users should be able to access the app to whatever extent is possible without the requested permissions." [S212](https://developer.android.com/training/permissions/requesting). 같은 권한을 한 기기에서 두 번 넘게 거부하면 시스템 대화상자가 더는 뜨지 않으므로("the user will no longer see the system permissions dialog") 재요청은 효과도 없다 [S212](https://developer.android.com/training/permissions/requesting), Android 11부터의 동작이다 [S225](https://developer.android.com/about/versions/11/privacy/permissions). Play도 제한 권한 거부를 존중하라고 한다 [S227](https://support.google.com/googleplay/android-developer/answer/9888170).
- 예시:
  ```kotlin
  // Good: 스캔 버튼 자리에서만 안내하고 나머지 화면은 그대로 쓴다
  if (uiState.permission == PermissionStatus.Denied) Text(stringResource(R.string.scan_needs_permission))
  // Bad: 거부하면 화면 전체를 덮는 경고
  if (denied) FullScreenWarning(onRetry = { launcher.launch(permission) })
  ```
- 체크: 거부 상태에서 화면 전체를 덮거나 앱 진행을 막는 UI가 있는가. 거부 콜백이 곧바로 `launch()`를 다시 부르는가.

### R-25-08 결과 콜백은 화면이 만들어질 때마다 무조건 등록하고 결과 처리에 필요한 상태는 따로 저장한다
- 규칙: `rememberLauncherForActivityResult`는 Route 컴포저블 본문 최상위에서 매번 같은 순서로 부른다. `if`·`when` 분기 안이나 요청을 시작한 뒤에만 도는 경로 안에서 만들지 않는다. 결과 처리에 필요한 상태(어떤 기능을 위해 요청했는지 등)는 ViewModel의 `SavedStateHandle`이나 `rememberSaveable`(R-17-12)에 두어 프로세스 재생성 뒤에도 남긴다. 일반 ViewModel 필드는 프로세스 사망을 넘기지 못한다([S155](https://developer.android.com/topic/libraries/architecture/saving-states) 요약). 콜백은 결과를 ViewModel에 전달하기만 한다(R-25-04).
- 근거: "the callback must be unconditionally registered every time your activity is created, even if the logic of launching the other activity only happens based on user input or other business logic." 이고 "any additional state needed to handle the result must be saved and restored separately from these APIs." 이다 [S219](https://developer.android.com/training/basics/intents/result). `rememberLauncherForActivityResult`가 등록·해제를 자동으로 처리한다는 것은 릴리스 노트의 요약이다 [S234](https://developer.android.com/jetpack/androidx/releases/activity). 컴포저블의 조건 분기 안에서 부르지 말라는 명시 문장은 확인하지 못했고 위 "unconditionally registered" 원칙에서 이끈 팩 결정이다. 상태 저장 방법은 [S155](https://developer.android.com/topic/libraries/architecture/saving-states).
- 예시:
  ```kotlin
  // Good: 조건과 무관하게 최상위에서 등록
  val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> viewModel.onPermissionResult(granted) }
  // Bad: 상태에 따라 등록 여부가 달라진다
  if (uiState.permission != PermissionStatus.Granted) { val launcher = rememberLauncherForActivityResult(/* ... */) { /* ... */ } }
  ```
- 체크: `rememberLauncherForActivityResult`가 `if`·`when`·람다 안에서 불리는가. 결과 처리에 필요한 상태가 저장되지 않는 지역 변수에만 있는가.

### R-25-09 블루투스 권한은 매니페스트의 maxSdkVersion으로 SDK를 나누고 요청 코드는 SDK_INT 분기 한 곳에 모은다
- 규칙: 매니페스트에는 Android 12+ 권한을 필요한 것만 선언하고, 레거시 `BLUETOOTH`·`BLUETOOTH_ADMIN`에는 `maxSdkVersion="30"`을 준다. 스캔 결과로 물리 위치를 도출하지 않으면 `BLUETOOTH_SCAN`에 `neverForLocation`을 주고, 앱의 다른 기능(R-25-10·R-25-11)도 정밀 위치를 쓰지 않을 때만 `ACCESS_FINE_LOCATION`에 `maxSdkVersion="30"`을 준다. 다른 기능이 정밀 위치를 쓰면 그 제한을 걸지 않는다(문서는 이 경우를 따로 다루지 않으며 이 조건은 팩 결정이다). 요청할 권한 배열은 `Build.VERSION.SDK_INT` 분기가 든 함수 한 곳이 돌려주고, 다른 곳에서 SDK로 다시 나누지 않는다.
- 근거: Android 12+ 권한 셋은 런타임 권한이고 "For your legacy Bluetooth-related permission declarations, set android:maxSdkVersion to 30." 이다. 위치는 "If your app uses Bluetooth scan results to derive physical location, declare the ACCESS_FINE_LOCATION permission. Otherwise, you can strongly assert that your app doesn't derive physical location and set android:maxSdkVersion to 30 for the ACCESS_FINE_LOCATION permission." 이고, `neverForLocation`의 부작용은 "some BLE beacons are filtered from the scan results." 이며, 11 이하는 "ACCESS_FINE_LOCATION is necessary because, on Android 11 and lower, a Bluetooth scan could potentially be used to gather information about the location of the user." 이다 [S221](https://developer.android.com/develop/connectivity/bluetooth/bt-permissions). minSdk 26 앱이 Android 8~11에서 스캔하려면 위치를 런타임으로 요청해야 한다는 결론은 위 문장들의 조합이고 그 조합을 명시한 문장과 SDK 분기 코드의 공식 예시는 없다. 분기를 한 곳에 모으는 것은 팩 결정이다. 아직 실행으로 확인하지 않았다.
- 예시:
  ```xml
  <!-- Good -->
  <uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
  <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
  <uses-permission android:name="android.permission.BLUETOOTH_SCAN" android:usesPermissionFlags="neverForLocation" />
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" android:maxSdkVersion="30" />
  ```
  ```kotlin
  // Good: 분기는 이 함수 하나
  fun scanPermissions(): Array<String> =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
      else arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
  // Bad: 화면마다 SDK_INT 로 권한 문자열을 따로 고른다
  ```
- 체크: `BLUETOOTH`·`BLUETOOTH_ADMIN`에 `maxSdkVersion="30"`이 있는가. 블루투스 권한 문자열을 고르는 `SDK_INT` 분기가 한 함수에만 있는가. `neverForLocation`을 쓴 근거(위치 도출 안 함)가 있는가.

### R-25-10 위치 권한은 대략 위치부터 요청하고 백그라운드 위치는 포그라운드 허용 뒤에 따로 요청한다
- 규칙: 기본은 `ACCESS_COARSE_LOCATION`이다. `ACCESS_FINE_LOCATION`은 정밀 위치가 기능에 꼭 필요할 때만 더하고, 사용자가 대략 위치만 허용해도 기능이 동작해야 한다. `ACCESS_BACKGROUND_LOCATION`은 포그라운드 위치가 허용된 뒤에 별도 요청으로 하고, 앱의 핵심 기능이 백그라운드 접근을 필요로 할 때만 선언한다.
- 근거: "If the user grants the approximate location permission, your app only has access to approximate location, regardless of which location permissions your app declares." 이고 "Your app should still work when the user grants only approximate location access." 이다 [S222](https://developer.android.com/training/location/permissions). "Wait until the user grants your app either the ACCESS_COARSE_LOCATION permission or the ACCESS_FINE_LOCATION permission before you request the ACCESS_BACKGROUND_LOCATION permission." [S215](https://developer.android.com/training/permissions/explaining-access). Play 정책은 "Apps should request the minimum scope necessary (for example, coarse instead of fine, and foreground instead of background) to provide the current feature or service requiring location" 이라 하고 [S227](https://support.google.com/googleplay/android-developer/answer/9888170), 백그라운드는 "Your app should only request access to the location in the background if it’s required for the core functionality of the app." 이다 [S230](https://support.google.com/googleplay/android-developer/answer/9799150). Android 10+에서는 `ACCESS_BACKGROUND_LOCATION`을 선언해야 런타임 요청이 가능하다(요약) [S222](https://developer.android.com/training/location/permissions).
- 예시:
  ```kotlin
  // Good: 포그라운드 결과를 받은 뒤 별도 요청
  val fg = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> viewModel.onForegroundLocation(granted) }
  // 백그라운드 요청은 uiState.foregroundGranted 인 뒤 사용자가 누른 버튼에서 launch
  // Bad: 한 번에 요청
  launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION))
  ```
- 체크: `ACCESS_BACKGROUND_LOCATION`을 다른 위치 권한과 같은 `launch()`로 요청하는가. `ACCESS_FINE_LOCATION`만 선언하고 대략 허용 때 기능이 멈추는가.

### R-25-11 세션 단위로만 위치가 필요한 기능은 위치 버튼을 쓴다
- 규칙: 앱을 쓰는 동안 한 번만 위치가 필요하고 지속 공유·지오펜싱이 아닌 기능은 위치 권한 대화상자 대신 위치 버튼으로 위치 접근을 요청한다. 위치 버튼 Jetpack 라이브러리는 experimental이므로 버전을 올릴 때 API 변경을 확인한다. 지속 위치 기능은 R-25-10을 따른다.
- 근거: "For these cases, apps should use the location button to request location access for that individual session of app usage." 이고 Android 17(API 37) 이상을 타깃으로 하면서 세션 기반 위치 기능만 있으면 "Google Play policy requires you to use the location button." 이다 [S222](https://developer.android.com/training/location/permissions). 위치 버튼 문서도 같은 요건을 적고, 라이브러리는 "Location button is an experimental Jetpack library and is subject to change." 이며 `USE_LOCATION_BUTTON` 권한이 필요하고 Android 16 이하에서는 일반 권한 프롬프트로 대체된다 [S223](https://developer.android.com/guide/topics/permissions/private-alternatives/location-button). Play 정책 페이지의 개정 예고에는 "(effective January 27, 2027)" 이 적혀 있다 [S227](https://support.google.com/googleplay/android-developer/answer/9888170). 이 팩의 targetSdk 37이 위 조건의 Android 17이라는 적용은 팩 해석이다. 적용 대상 판정 기준을 담은 Play 도움말 본문은 열어 보지 못했다. 아직 실행으로 확인하지 않았다.
- 예시:
  ```kotlin
  // Good: "내 주변 찾기" 는 이번 세션 위치만 쓴다 → 위치 버튼 탭으로 요청
  // Bad: 같은 기능을 위해 ACCESS_FINE_LOCATION 대화상자를 띄우고 그 권한을 계속 보유한다
  ```
- 체크: 세션 단위 기능이 위치 권한 대화상자로 요청하는가. 지속 위치 기능이 없는데 `ACCESS_BACKGROUND_LOCATION`이 선언돼 있는가.

### R-25-12 기대 밖 민감 데이터 접근은 요청 직전에 앱 안 공개 화면을 두고 거절할 수 있게 한다
- 규칙: 백그라운드 위치처럼 사용자가 기대하기 어려운 접근·수집의 권한 요청 바로 앞에 앱 안 공개 화면(어떤 데이터를 왜 쓰고 공유하는지)을 둔다. 공개 화면은 설정·메뉴 안에 숨기지 않고, 동의와 거절 두 선택지를 주며, 뒤로가기·홈·화면 밖 탭을 동의로 처리하지 않는다. 동의했을 때만 `launch()`한다. 사용자가 기대하는 기능(사진 첨부 버튼 → 사진)의 권한에는 이 화면을 강제하지 않는다.
- 근거: Play 정책: "Requests for in-app user consent and runtime permission requests must be immediately preceded by an in-app disclosure that meets the requirement of this policy." 이 조항은 접근·수집이 "may not be within the reasonable expectation of the user" 인 경우에 걸리고, 동의는 명시적 행동이어야 하며 "Must not interpret navigation away from the disclosure (including tapping away or pressing the back or home button) as consent" 이다 [S228](https://support.google.com/googleplay/android-developer/answer/10144311). UX 도움말: "Present the disclosure to the user in the app, right before requesting permission or capability." / "Use at least two options." / "Don't use disclosure prompts that are similar to the Android System UI notifications and requests, as this may confuse users." 이고 공개 요건은 개인정보처리방침·Data Safety를 대체하지 못한다 [S229](https://support.google.com/googleplay/android-developer/answer/11150561). 모든 권한에 공개 화면을 강제하는 문장은 없다 — 대상을 이 유형으로 한정한 것은 출처가 정한 범위를 따른 것이다.
- 예시:
  ```kotlin
  // Good: 공개 화면 [동의][거절] → 동의 콜백에서만 요청
  DisclosureScreen(onAgree = { launcher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION) }, onDecline = onBack)
  // Bad: 시스템 대화상자를 먼저 띄우고 설명은 그 뒤에 보인다
  ```
- 체크: 백그라운드 위치 요청 바로 앞에 공개 화면이 있는가. 공개 화면에서 뒤로가기가 동의로 처리되는가.

### R-25-13 알림 권한은 앱에 익숙해진 뒤 사용자 동작 시점에 요청하고 Android 13 미만에서는 요청하지 않는다
- 규칙: `POST_NOTIFICATIONS`는 알림 벨 탭·팔로우·주문 제출 같은 사용자 동작이나 앱을 써 본 뒤에 요청한다. `Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU` 가드 안에서만 `launch()`하고, 알림을 보내기 전 `areNotificationsEnabled()`로 확인한다. 몇 번째 실행에 요청할지 같은 수치 기준은 규칙으로 두지 않는다.
- 근거: "Before you ask users to grant any permissions, let them familiarize themselves with your app." 이고 사용자 동작(알림 벨 탭, 팔로우, 주문 제출)이 좋은 시점의 예다. 타깃이 13 이상이면 "your app has complete control over when the permission dialog is displayed." 이다. 거부하면 "your app can't send notifications unless it qualifies for an exemption." 이고 포그라운드 서비스 시작에는 이 권한이 필요 없지만 알림은 있어야 한다(요약) [S205](https://developer.android.com/develop/ui/views/notifications/notification-permission). 12 이하 기기에는 런타임 요청이 없다는 것은 이 문서의 `TIRAMISU` 가드 예시에서 도출한 것이다. 문서가 예로 든 "third or fourth time" 은 수치 예시일 뿐이라 규칙으로 쓰지 않는다.
- 예시:
  ```kotlin
  // Good
  onBellClick = { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }
  // Bad: 첫 실행 진입 즉시 (R-25-01 위반), 12 이하에서도 호출
  LaunchedEffect(Unit) { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }
  ```
- 체크: `POST_NOTIFICATIONS` 요청이 `SDK_INT` 가드 안에 있는가. 요청이 사용자 동작이 아닌 시작 코드에서 불리는가.

### R-25-14 권한 흐름은 허용·거부 양쪽을 시험하고 시스템 대화상자는 GrantPermissionRule이나 UI Automator로 처리한다
- 규칙: ViewModel 테스트는 `Granted`·`RationaleNeeded`·`Denied` 값을 직접 넣어 UiState 변화를 검증한다(R-30-01). 계측 테스트에서 허용 상태가 필요하면 `GrantPermissionRule`(API 28+ 대상 테스트는 `grantRuntimePermission`)로 미리 허용하고, 대화상자를 누르는 흐름은 UI Automator의 `watchFor(PermissionDialog)`로 처리한다. 허용 경로와 거부 경로를 모두 시험한다. 전체 플로우의 기본 도구는 R-30-13대로 Compose UI Test다.
- 근거: "The GrantPermissionRule Rule allows granting of runtime permissions on Android M (API 23) and above." / "Once a permission is granted it will apply for all tests running in the current Instrumentation. There is no way of revoking a permission after it was granted." / "For tests running on Android SDKs >= API 28, use grantRuntimePermission instead." [S232](https://developer.android.com/reference/androidx/test/rule/GrantPermissionRule). UI Automator 2.4는 `watchFor(PermissionDialog) { clickAllow() }`·`clickDeny()`를 제공한다(요약) [S231](https://developer.android.com/training/testing/other-components/ui-automator). "Test with various combinations of granted or revoked permissions." [S214](https://developer.android.com/training/permissions/usage-notes). 로컬 테스트에서 권한 확인을 fake로 바꾸라는 권고는 확인하지 못했다. 상태를 enum 값으로 넘기므로(R-25-04) ViewModel 테스트에 프레임워크가 필요 없다는 것은 팩 결정이다. `grantRuntimePermission`의 클래스·시그니처는 열어 보지 않았다.
- 예시:
  ```kotlin
  // Good
  @get:Rule val grant: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION) // 허용 경로
  // 거부 경로: clearAppData 뒤 watchFor(PermissionDialog) { clickDeny() } 로 시험
  // Bad: 허용 경로만 시험하고 거부 화면(R-25-07)은 손으로만 확인한다
  ```
- 체크: 권한을 요청하는 화면마다 거부 경로 테스트가 있는가. 계측 테스트가 대화상자를 임의 대기(`Thread.sleep`)로 넘기는가.

## 출처가 침묵하는 것 (규칙으로 쓰지 않음)

- **영구 거부 감지와 설정 화면 안내**: 공식 문서에 영구 거부를 감지하는 API가 없다. 두 번 넘게 거부하면 시스템 대화상자가 뜨지 않는다는 사실과 디버그 플래그(`USER_FIXED`)만 있다. Accompanist 문서는 처음 요청과 다시 묻지 않기를 구분할 수 없다고 적는다 [S217](https://google.github.io/accompanist/permissions/). 공식 샘플 코드의 주석은 사용자를 설득할 목적으로 시스템 설정 링크를 걸지 말라고 한다(요약) [S212](https://developer.android.com/training/permissions/requesting). 그래서 `PermissionStatus`에 영구 거부 값을 두지 않았다.
- **알림 권한을 몇 번째 실행에 요청할지**: 출처는 "third or fourth time" 예시만 든다.
- **모든 권한에 사전 안내 화면을 강제할지**: 플랫폼은 `shouldShowRequestPermissionRationale`이 true일 때, Play는 기대 밖 수집에만 요구한다. 모든 권한에 강제하는 문장은 없다.
- **권한 요청 화면을 별도 Navigation 3 목적지로 둘지 기능 화면 안에 둘지**, **여러 권한을 한 번에 묶을지 나눌지**(위치 두 단계 제외).
- **CDM 페어링 뒤 연결 단계에서 `BLUETOOTH_CONNECT`가 필요한지**: CDM 문서가 말하지 않는다.
- **정밀/대략 위치 선택 대화상자의 구성과 앱 쪽 처리**: 최신 위치 권한 문서에는 정확도 설명만 남아 있다.
- **로컬 테스트에서 권한 확인을 인터페이스로 감싸 fake로 바꾸는 방법**: 권한 문서에 없다.
- **자동 재설정을 끄도록 사용자를 설정으로 안내하는 코드**: 앱 수면 문서에 코드가 있으나 안내 여부는 앱 판단이라 규칙으로 쓰지 않았다 [S226](https://developer.android.com/topic/performance/app-hibernation).
- **Material 권한 패턴 페이지**: JavaScript 렌더링이라 본문을 얻지 못했다. 같은 원칙은 개발 문서 문장으로 대신했다.
