# 런타임 권한 요청 흐름 조사 노트
조사일: 2026-09-30 (모든 "갱신일"은 이 날 페이지 하단 "Last updated" 값을 확인한 것이다)

범위: R-40-12(권한 최소화) 아래 비워 둔 "런타임 권한을 요청할 화면·시점"을 채울 수 있는지 조사한다. 규칙 문장은 쓰지 않고 후보만 적는다. 인용부호 안의 영어 문장은 curl로 받은 HTML에서 태그를 제거한 텍스트에 글자 그대로 있는 것만 썼다(공백·줄바꿈·따옴표 모양만 정규화하고 스크립트로 재확인함). 한글은 요약이다.

## 출처
| # | 조직 | 문서명 | URL | 종류 | 갱신일/버전 |
|---|---|---|---|---|---|
| S1 | Google | Request runtime permissions | https://developer.android.com/training/permissions/requesting | 공식 가이드 | 2026-09-16 |
| S2 | Google | Minimize your permission requests | https://developer.android.com/privacy-and-security/minimize-permission-requests | 공식 가이드 | 2026-08-19 |
| S3 | Google | App permissions best practices | https://developer.android.com/training/permissions/usage-notes | 공식 가이드 | 2026-09-16 |
| S4 | Google | Explain access to more sensitive information | https://developer.android.com/training/permissions/explaining-access | 공식 가이드 | 2026-09-16 |
| S5 | Google | Compose and other libraries (Activity Result API·권한 절) | https://developer.android.com/develop/ui/compose/libraries | 공식 가이드 | 2026-09-22 |
| S6 | Google | Accompanist — Jetpack Compose Permissions 문서, 저장소 README, Maven 메타데이터 | https://google.github.io/accompanist/permissions/ , https://raw.githubusercontent.com/google/accompanist/main/docs/permissions.md , https://raw.githubusercontent.com/google/accompanist/main/README.md , https://repo1.maven.org/maven2/com/google/accompanist/accompanist-permissions/maven-metadata.xml | 오픈소스 문서 | 0.37.3 (2025-04-28 배포) |
| S7 | Google | Get a result from an activity (Activity Result API) | https://developer.android.com/training/basics/intents/result | 공식 가이드 | 2026-03-05 |
| S8 | Google | UI events (UI layer) | https://developer.android.com/topic/architecture/ui-layer/events | 공식 가이드 | 2026-05-18 |
| S9 | Google | State holders and UI state | https://developer.android.com/topic/architecture/ui-layer/stateholders | 공식 가이드 | 2026-05-11 |
| S10 | Google | Bluetooth permissions | https://developer.android.com/develop/connectivity/bluetooth/bt-permissions | 공식 가이드 | 2026-09-16 |
| S11 | Google | Companion device pairing | https://developer.android.com/develop/connectivity/bluetooth/companion-device-pairing | 공식 가이드 | 2026-09-16 |
| S12 | Google | Request location permissions | https://developer.android.com/training/location/permissions | 공식 가이드 | 2026-09-16 |
| S13 | Google | Location button | https://developer.android.com/guide/topics/permissions/private-alternatives/location-button | 공식 가이드(Jetpack 라이브러리는 experimental) | 2026-09-08 |
| S14 | Google | Notification runtime permission | https://developer.android.com/develop/ui/views/notifications/notification-permission | 공식 가이드 | 2026-09-16 |
| S15 | Google | Photo picker | https://developer.android.com/training/data-storage/shared/photopicker | 공식 가이드 | 2026-09-16 |
| S16 | Google | Android 11 permissions changes | https://developer.android.com/about/versions/11/privacy/permissions | 공식 가이드 | 2026-09-16 |
| S17 | Google | App hibernation and permission auto-reset | https://developer.android.com/topic/performance/app-hibernation | 공식 가이드 | 2026-09-16 |
| S18 | Google Play | Permissions and APIs that Access Sensitive Information | https://support.google.com/googleplay/android-developer/answer/9888170 | 정책 | 2026-09-30 확인(개정 예고문 포함) |
| S19 | Google Play | User Data (Prominent Disclosure & Consent Requirement 절) | https://support.google.com/googleplay/android-developer/answer/10144311 | 정책 | 2026-09-30 확인 |
| S20 | Google Play | Best practices for prominent disclosure and consent | https://support.google.com/googleplay/android-developer/answer/11150561 | 정책 도움말 | 2026-09-30 확인 |
| S21 | Google Play | Understanding location in the background permissions | https://support.google.com/googleplay/android-developer/answer/9799150 | 정책 도움말 | 2026-09-30 확인 |
| S22 | Google | UI Automator | https://developer.android.com/training/testing/other-components/ui-automator | 공식 가이드 | 2026-09-21 |
| S23 | Google | GrantPermissionRule (androidx.test:rules) | https://developer.android.com/reference/androidx/test/rule/GrantPermissionRule | API 레퍼런스 | 2026-06-24 |
| S24 | Google | Activity 릴리스 노트 | https://developer.android.com/jetpack/androidx/releases/activity | 릴리스 노트 | 안정 1.13.0 (2026-09-23) |
| S25 | Google | platform-samples/privacy/permissions README | https://github.com/android/platform-samples/tree/main/samples/privacy/permissions | 공식 샘플 | main |
| S26 | Google | Find BLE devices | https://developer.android.com/develop/connectivity/bluetooth/ble/find-ble-devices | 공식 가이드 | 2026-02-26 |
| S27 | Google | Save and restore UI state | https://developer.android.com/topic/libraries/architecture/saving-states | 공식 가이드 | 2026-04-22 |
| S28 | Google | Storage Access Framework | https://developer.android.com/guide/topics/providers/document-provider | 공식 가이드 | 2025-05-07 |

## 핵심 내용 (출처별)

### 질문 1. 공식 권한 워크플로 [S1]
- 기본 원칙 첫째: "Ask for a permission in context, when the user starts to interact with the feature that requires it." [S1]
- 워크플로 순서(요약): 매니페스트 선언 → 특정 동작과 특정 권한을 UX에서 연결 → 사용자가 동작을 호출할 때까지 대기 → 이미 허용됐는지 확인 → 필요하면 근거(rationale) UI → 요청 → 응답 확인 → 거부 시 기능 축소. [S1]
- 확인 주기: "You must check whether you have a permission every time you perform an operation that requires that permission." [S1]
- 근거 UI 조건: `checkSelfPermission()`이 `PERMISSION_DENIED`이면 `shouldShowRequestPermissionRationale()`을 호출하고 true일 때만 교육용 UI를 보인다. false면 UI 없이 곧바로 요청한다(요약). [S1]
- 공식 코드는 `when { granted -> 실행; shouldShowRationale -> 교육 UI; else -> launcher.launch }` 3분기다. 교육 UI에는 "cancel" 또는 "no thanks" 버튼을 둔다(코드 주석). [S1]
- 차단 금지: "Don't block the user. Always provide the option to cancel an educational UI flow, such as a flow that explains the rationale for requesting permissions." [S1]
- 두 번 거부: "if the user taps Deny for a specific permission more than once during your app's lifetime of installation on a device, the user will no longer see the system permissions dialog if your app requests that permission again." 이어서 "It is very important to only prompt users for permissions when they need access to a specific feature" [S1]. Android 11부터의 동작이다 [S16].
- 자동 거부·허용 가능: "Each time your app needs to access functionality that requires a permission, check that your app is still granted that permission." [S1]
- 설정 화면 안내: 요청 콜백의 공식 코드 주석은 사용자가 결정을 바꾸도록 설득할 목적으로 시스템 설정으로 링크하지 말라고 한다(요약; 주석이 줄마다 `//`로 끊겨 있어 글자 그대로 인용하지 않음). [S1]. 설정으로 보내는 API·패턴을 설명하는 문장은 S1에 없다. Play 쪽 도움말은 동의 흐름 위치로 "The ideal location would be in the user flow where users are informed of the steps to grant permission in Android Settings." 라고만 한다 [S20].
- 거부 후 처리: "Don't block the user interface. In other words, don't display a full-screen warning message that prevents users from continuing to use your app at all." / "Be specific. Don't display a generic message." / "persistent nagging to reconsider is not respectful of their choice." [S1]
- 디버그: 영구 거부는 `USER_FIXED`, 한 번 거부는 `USER_SET` 플래그이며 `adb shell pm clear-permission-flags PACKAGE_NAME PERMISSION_NAME user-set user-fixed`로 초기화한다. [S1]

### 질문 2. Compose 구현 [S5][S1][S14][S6][S7][S8][S9][S24]
- 공식 Compose 문서: "The same Activity Result API and rememberLauncherForActivityResult() explained above can be used to request runtime permissions using the RequestPermission contract for a single permission or RequestMultiplePermissions contract for multiple permissions." [S5]
- 공식 Compose 예시(알림 권한): `rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted -> ... }`를 만들고 `Button(onClick = { permissionLauncher.launch(...) })`에서 시작한다. [S14]
- launch 위치: "If you attempt to launch a request from inside the composable you’ll get a runtime error because the ActivityResultLauncher has not been initialized at this point." 조합 뒤에 실행해야 하면 "LaunchedEffect" 또는 "DisposableEffect" 안에서 `launch()`를 부른다. [S5]
- 권장도: `RequestPermission` 계약은 "it is the recommended solution when possible" 이다(직접 요청 코드 관리보다). [S1] 요구 의존성은 `androidx.activity` 1.2.0 이상 / `androidx.fragment` 1.3.0 이상이다. [S1] Activity 안정 릴리스는 1.13.0이다. [S24]
- Accompanist Permissions: 공식 Compose 문서가 "The Accompanist Permissions library can also be used as a layer above those APIs to map the current granted state for permissions into State that your Compose UI can use." 라고 여전히 언급한다. [S5] 라이브러리 자체는 deprecated가 아니다. 저장소 README는 Navigation-Animation·Navigation-Material·System UI Controller에만 "(Deprecated & Removed)"를 붙이고 Permissions는 붙이지 않는다. [S6] 다만 문서에 "The permission APIs are currently experimental and they could change at any time." 경고가 있고 모든 API가 `@ExperimentalPermissionsApi`이다. 최신 배포는 0.37.3(2025-04-28)이며 저장소 마지막 push는 2025-08-18이다(GitHub API 조회). [S6] 공식 샘플 README도 Compose 예시로 accompanist-permissions를 쓴다. [S25]
- Accompanist 한계: "it's not possible to differentiate between the it's the first time requesting the permission vs the user doesn't want to be asked again use cases." [S6] (이 한계는 `shouldShowRequestPermissionRationale`가 false일 때 '처음'과 '영구 거부'를 구분 못 하는 플랫폼 제약이다.)
- ViewModel·UI 분담: "The UI is in charge of screen-specific behavior logic such as navigation calls, click events, and obtaining permission requests. The ViewModel contains business logic and converts the results from lower layers of the hierarchy into UI state." [S8] 런타임 권한은 UI 수명주기에 종속된 상태의 예다: "Examples of this include runtime permissions and getting configuration dependent resources like localized strings." [S9] 권한 API는 UI 로직이 읽는 데이터 원본이다: "UI data sources like the permissions API or Resources." [S9] ViewModel에 넘길 값의 형태(예: 허용 여부 Boolean)를 정한 공식 문장은 없다.
- 재생성·프로세스 사망: "the callback must be unconditionally registered every time your activity is created, even if the logic of launching the other activity only happens based on user input or other business logic." [S7] "any additional state needed to handle the result must be saved and restored separately from these APIs." [S7] `rememberLauncherForActivityResult`는 등록·해제를 자동 처리한다(릴리스 노트 요약). [S24] `rememberLauncherForActivityResult`가 Compose 호출 순서에 맞춰 등록되므로 조건부 분기 안에서 부르지 않는다는 문장은 이 문서들에서 확인하지 못했다.

### 질문 3. 블루투스 권한 [S10][S26][S11]
- Android 12(API 31) 이상을 타깃으로 하면 `BLUETOOTH_SCAN`(기기 탐색), `BLUETOOTH_ADVERTISE`(기기를 다른 기기에 보이게 함), `BLUETOOTH_CONNECT`(이미 페어링된 기기와 통신)를 선언한다. 셋 다 런타임 권한이며 요청 시 시스템이 "Nearby devices" 접근으로 묻는다. [S10]
- 레거시 선언: "For your legacy Bluetooth-related permission declarations, set android:maxSdkVersion to 30." 예시 매니페스트는 `BLUETOOTH`·`BLUETOOTH_ADMIN`에 `android:maxSdkVersion="30"`을 준다. [S10]
- 위치: "If your app uses Bluetooth scan results to derive physical location, declare the ACCESS_FINE_LOCATION permission. Otherwise, you can strongly assert that your app doesn't derive physical location and set android:maxSdkVersion to 30 for the ACCESS_FINE_LOCATION permission." [S10]
- `neverForLocation`: `BLUETOOTH_SCAN` 선언에 `android:usesPermissionFlags="neverForLocation"`을 준다. 부작용: "If you include neverForLocation in your android:usesPermissionFlags, some BLE beacons are filtered from the scan results." [S10]
- Android 11 이하를 타깃으로 하는 경우: `BLUETOOTH`가 통신에 필요하고, 스캔에는 `ACCESS_FINE_LOCATION`(런타임)이 필요하며, 탐색을 시작하려면 `BLUETOOTH_ADMIN`이 필요하다. "ACCESS_FINE_LOCATION is necessary because, on Android 11 and lower, a Bluetooth scan could potentially be used to gather information about the location of the user." [S10] (원문은 "Because location permissions are runtime permissions, you must request these permissions at runtime along with declaring them in your manifest.") [S10]
- 해석(원문 조합): targetSdk 37이면서 minSdk 26인 앱은 "12 이상 타깃" 매니페스트를 쓰되 `maxSdkVersion="30"` 덕에 Android 8~11 기기에서는 레거시 권한과 위치가 계속 적용된다. 따라서 Android 8~11 기기에서 스캔하려면 위치를 런타임으로 요청해야 한다는 결론이 나오지만, 이 조합을 명시한 문장은 없다. 그 SDK 분기를 코드로 어떻게 쓰는지의 공식 예시도 이 페이지에 없다.
- BLE 스캔 페이지(S26)는 권한 문장이 없고 스캔 수칙만 있다: "Never scan on a loop, and always set a time limit on your scan." [S26]
- 대안: "On Android 8.0 (API level 26) and higher, the Companion Device Manager (CDM) provides a more streamlined method of connecting to companion devices, compared to the permissions described in this section. The CDM system provides a pairing UI on behalf of your app and doesn't require location permissions." [S10] CDM 문서: "companion device pairing performs a Bluetooth or Wi-Fi scan of nearby devices on behalf of your app without requiring the ACCESS_FINE_LOCATION permission." "Companion device pairing doesn't create connections on its own nor enable continuous scanning." [S11] CDM 문서는 페어링 뒤 연결 단계에서 `BLUETOOTH_CONNECT`가 필요한지 말하지 않는다(미확인 절 참조).

### 질문 4. 위치 권한 [S12][S4][S13][S21][S18]
- 포그라운드 위치는 `ACCESS_COARSE_LOCATION` 또는 `ACCESS_FINE_LOCATION`을 요청할 때 선언되고, 매니페스트 주석은 "Include only if your app benefits from precise location access." 이다. [S12]
- 정확도: "If the user grants the approximate location permission, your app only has access to approximate location, regardless of which location permissions your app declares." "Your app should still work when the user grants only approximate location access." 정밀이 꼭 필요한 기능이면 "you can ask the user to allow your app to access precise location." [S12] Android 12+ 정밀/대략 선택 다이얼로그 자체를 그리는 문장은 최신 S12·S1에 없다(질문 4의 '선택 UI' 부분은 위 정확도 문장으로만 뒷받침된다).
- 두 단계 요청: "Wait until the user grants your app either the ACCESS_COARSE_LOCATION permission or the ACCESS_FINE_LOCATION permission before you request the ACCESS_BACKGROUND_LOCATION permission." [S4] 같은 페이지에서 시점: "Wait until the user interacts with a feature in your app that requires location before you request the ACCESS_COARSE_LOCATION permission or the ACCESS_FINE_LOCATION permission" [S4]
- 백그라운드: Android 10+에서 `ACCESS_BACKGROUND_LOCATION`을 선언해야 런타임 요청이 가능하다. Android 9 이하는 포그라운드 허용 시 자동으로 백그라운드도 받는다. [S12] 일회성/일정 시간 공유 기능은 포그라운드, 지속 공유·지오펜싱은 백그라운드로 분류한다(요약). [S12]
- 원타임 접근: "For these cases, apps should use the location button to request location access for that individual session of app usage." Android 17(API 37) 이상 타깃이면서 세션 기반 위치 기능만 있으면 "Google Play policy requires you to use the location button." [S12] 위치 버튼 문서도 같은 요건을 적는다. [S13] Jetpack 라이브러리(`androidx.core.locationbutton.compose.LocationButton`)는 "Location button is an experimental Jetpack library and is subject to change." 이고 `USE_LOCATION_BUTTON` 권한이 필요하며 Android 16 이하에서는 일반 권한 프롬프트로 대체된다. [S13]
- Play 정책: "Apps should request the minimum scope necessary (for example, coarse instead of fine, and foreground instead of background) to provide the current feature or service requiring location" [S18] 위치 버튼 요구는 정책 페이지 개정 예고에 "(effective January 27, 2027)" 로 적혀 있다. [S18]
- 백그라운드 위치 Play 요건: 핵심 기능에만 허용 — "Your app should only request access to the location in the background if it’s required for the core functionality of the app." 승인 없이는 "app updates may be blocked and your app may be removed from Google Play." 권한 선언 양식·동영상 시연·프롬프트 공개(prominent in-app disclosure)·앱 안팎의 개인정보처리방침이 필요하다(도움말 목차 요약). [S21] 포그라운드 서비스로 위치를 쓰는 경우: 앱 안 사용자 행동의 연속으로 시작하고 그 용도가 끝나면 즉시 종료해야 한다(요약). [S21][S18]

### 질문 5. 알림 권한 [S14]
- `POST_NOTIFICATIONS`는 Android 13(API 33) 이상의 런타임 권한이다. 새로 설치된 앱은 기본으로 알림이 꺼져 있다. [S14]
- 시점: 타깃이 13 이상이면 "your app has complete control over when the permission dialog is displayed." 타깃이 12L 이하이면 시스템이 채널 생성 뒤 첫 액티비티 시작 때(단일 액티비티 앱은 MainActivity 시작 때) 대화상자를 띄운다. [S14]
- 권고: "Before you ask users to grant any permissions, let them familiarize themselves with your app." 사용자 동작(알림 벨 버튼 탭, 팔로우, 주문 제출)이 좋은 시점의 예다. [S14] 대안으로 "you might wait until the third or fourth time the user launches your app." [S14]
- 근거 화면: "Unless shouldShowRequestPermissionRationale() returns true, your app doesn't need to display the middle screen" [S14]
- 거부 시: "your app can't send notifications unless it qualifies for an exemption." 대화상자를 스와이프로 닫으면 권한 상태는 바뀌지 않는다. [S14] 포그라운드 서비스 시작에는 이 권한이 필요 없으나 알림은 반드시 있어야 한다(요약). [S14]
- 발송 전 확인: `areNotificationsEnabled()`. 사용자는 언제든 권한을 회수할 수 있다("they can revoke the permission at any time"). [S14]
- 12 이하(minSdk 26 ~ 32)에서는 런타임 요청이 없다(위 조건에서 도출; 요청 API 조건은 `Build.VERSION.SDK_INT >= TIRAMISU` 가드로 S14 예시에 나온다). [S14]

### 질문 6. 권한이 필요 없는 대안 [S2][S15][S28][S11]
- 총론: 대안 방식을 나열한다 — "Use pickers and permission alternatives which grant access to sensitive permissions in scoped circumstances such as the photo picker, contact picker, and location button" / "Call APIs which allow your app to perform the chosen functionality without declaring permissions." / "Invoke specific intents or event handlers to perform functionality, instead of declaring permissions." [S2]
- 사진 첨부: "Your app might allow users to choose from their photos and videos, such as for message attachments or profile pictures. To support this functionality, use the photo picker. The photo picker doesn't require any runtime permissions to use." [S2] 계약은 `PickVisualMedia`/`PickMultipleVisualMedia`(`androidx.activity` 1.7.0 이상), Compose 예시는 `rememberLauncherForActivityResult(PickVisualMedia())`이다. [S15] 피커가 없는 기기에서는 "the library automatically invokes the ACTION_OPEN_DOCUMENT intent action instead." [S15] Android 4.4~10과 일부 Go 기기는 Google Play services가 백포트 모듈을 설치할 수 있다(매니페스트에 `ModuleDependencies` 서비스 항목 추가). [S15]
- 사진 촬영: "In this situation, don't declare the CAMERA permission. Instead, invoke the ACTION_IMAGE_CAPTURE intent action." [S2]
- 문서·파일: 내 앱이 만든 파일은 직접 접근하고 다른 앱이 만든 문서는 Storage Access Framework를 쓴다. `READ_EXTERNAL_STORAGE`는 구형 호환용이며 `android:maxSdkVersion`을 28로 둔다(요약). [S2] SAF는 `ACTION_OPEN_DOCUMENT` 또는 `ACTION_CREATE_DOCUMENT` 인텐트로 시작한다. [S28] 파일 저장에 대해 "Android lets you create and access files without needing to declare any permissions related to storage or sensors." [S2]
- 블루투스 페어링: "To support this functionality, don't declare the ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATIION, or BLUETOOTH_ADMIN permissions. Instead, use companion device pairing." [S2] (원문 철자 그대로: ACCESS_COARSE_LOCATIION) 사용자 선택 후 권한은 "revoked if you uninstall the app or call disassociate()" 이고 앱이 연결 해제 시 스스로 연결(association)을 정리해야 한다(요약). [S11]
- Play 정책: Android 13+ 타깃 앱은 시스템 피커가 핵심 기능에 부족할 때만 `READ_MEDIA_IMAGES`·`READ_MEDIA_VIDEO`를 요청할 수 있고 Play Console 선언이 필요하다. [S18]

### 질문 7. 자동 재설정·실행 중 회수 [S1][S16][S17][S14]
- 자동 재설정: "If your app targets Android 11 (API level 30) or higher and is not used for a few months, the system protects user data by automatically resetting the sensitive runtime permissions that the user had granted your app." [S1] 같은 효과는 사용자가 설정에서 Deny로 바꾼 것과 같다. 모범 사례를 따르면 앱 변경은 필요 없다: "as the user interacts with features in your app, you should verify that the features have the permissions that they need." [S16]
- 앱 수면(hibernation): 타깃 12 이상·Android 12 이상 기기에서는 권한 재설정과 함께 백그라운드 작업·푸시가 막힌다. 앱이 수면에서 깨어나도 "Re-grant your app's runtime permissions." 는 하지 않으며 사용자가 다시 허용해야 한다(요약). 재예약에는 WorkManager 또는 `ACTION_BOOT_COMPLETED` 수신기를 권장한다. [S17] 자동 재설정을 끄도록 사용자를 설정 화면으로 보내는 `IntentCompat.createManageUnusedAppRestrictionsIntent` 코드가 있다. [S17]
- 사용자가 설정에서 회수하면 프로세스가 종료된다: "As with any permission, if the user revokes your app's one-time permission, your app's process terminates." (원타임 권한 절) [S1] 즉 회수 뒤 복귀는 프로세스 재생성 경로다.
- 앱 스스로 권한 제거: Android 13+ `revokeSelfPermissionOnKill()`·`revokeSelfPermissionsOnKill()`(요약). [S1]
- 원타임 권한(Android 11+ 위치·마이크·카메라): "Only this time" 선택 시 화면이 보이는 동안 접근하고, 백그라운드로 가면 잠시 뒤 접근이 끊긴다. 모범 사례를 따르면 추가 로직은 필요 없다(요약). [S1][S16]
- 알림도 사용자가 언제든 회수한다. [S14]

### 질문 8. Play 정책 [S19][S20][S18][S21]
- 요청 직전 사전 안내는 정책상 필요한 경우가 있다: "Requests for in-app user consent and runtime permission requests must be immediately preceded by an in-app disclosure that meets the requirement of this policy." 이 조항은 접근·수집이 "may not be within the reasonable expectation of the user" 인 경우에 걸린다(예: 백그라운드 수집). [S19]
- 공개 화면 요건: 앱 안에 있고, 설정·메뉴로 들어가지 않아도 보이고, 접근·수집 데이터와 사용·공유 방식을 설명하고, 개인정보처리방침에만 두면 안 된다. 동의는 명시적 행동이어야 하고 "Must not interpret navigation away from the disclosure (including tapping away or pressing the back or home button) as consent" 이다. [S19]
- 위반 사례: "An app has a runtime permission requesting access to data before the prominent disclosure which specifies what the data is used for." [S19]
- 대상 예: 백그라운드 위치, Accessibility API, 패키지(앱) 가시성 — "you must provide a separate in-app disclosure indicating the use of the permission or sensitive API to users." [S20]
- 공개 화면 UX: "Present the disclosure to the user in the app, right before requesting permission or capability." "Give the user an option to decline providing consent." "Use at least two options." "Don't use disclosure prompts that are similar to the Android System UI notifications and requests, as this may confuse users." [S20]
- 데이터 보안 양식과의 관계: "The prominent disclosure requirement is not a substitute for an app’s privacy policy or Data Safety section in Play Console." [S20]
- 점진 요청: "Request permissions and APIs that access sensitive information to access data in context (via incremental requests), so that users understand why your app is requesting the permission." [S18]
- 제한 권한 거부: "Respect users’ decisions if they decline a request for a Restricted Permission, and users may not be manipulated or forced into consenting to any non-critical permission." 대체 수단을 제공한다. [S18]
- 정리: 위치·블루투스 등 모든 권한에 사전 안내를 강제하는 문장은 없다. 사용자가 기대하는 기능(예: 사진 첨부 버튼 → 사진)에는 정책상 별도 공개가 필수라는 문장이 없고, 기대 밖 수집(백그라운드 등)에 필수다. S1은 사용자가 이유를 이해하기 어려울 때 설명을 권한다: "It is a good idea to explain to the user why your app wants the permissions before you call requestPermissions()." [S1]

### 질문 9. 테스트 [S23][S22][S1][S3]
- `GrantPermissionRule`: "The GrantPermissionRule Rule allows granting of runtime permissions on Android M (API 23) and above." 다이얼로그가 UI를 가로막는 것을 피하려 쓴다. "Once a permission is granted it will apply for all tests running in the current Instrumentation. There is no way of revoking a permission after it was granted." "For tests running on Android SDKs >= API 28, use grantRuntimePermission instead." [S23]
- UI Automator 2.4 신 DSL: `watchFor(PermissionDialog) { clickAllow() }` / `clickDeny()`; `clearAppData(...)` 뒤 다시 거부 흐름을 시험할 수 있다. 문서가 "strongly recommend" 하는 방식이다(요약). [S22]
- 수동·adb 시험: 허용 `adb shell pm grant`, 회수 `pm revoke`, 설치 시 전체 허용 `adb shell install -g`, 영구 거부 상태는 `dumpsys package`. [S1][S3]
- S3 권고: "You should test to ensure your app functions correctly across various permission scenarios." "Test with various combinations of granted or revoked permissions." [S3]
- 로컬(JVM) 테스트에서 권한 상태를 fake로 대체하라는 권고는 확인하지 못했다. fake를 우선하라는 일반 원칙은 다른 조사 노트(testing-ci)의 테스트 더블 문서 정리에 있으나 권한에 적용한 문장은 확인하지 못했다.

### 질문 10. 권한 UX 패턴 (Google 디자인 가이드)
- Material "Permissions" 패턴 페이지(material.io)는 JavaScript 렌더링이라 curl로 본문을 얻지 못했다(미확인 절). 대신 공식 개발 문서가 같은 원칙을 문장으로 준다.
- 요청 전 이유 설명: "In this rationale, clearly explain what data your app is trying to access and what benefits the app can provide to the user if they grant the runtime permission." [S1]
- 이유는 기능 시작 시: "only prompt when a specific feature is required. For instance, only prompt for microphone access when a user clicks on the microphone button." [S3]
- 시작 시 일괄 요청 금지: "Don't overburden the user by requesting every permission at app startup." [S3]
- 시스템 다이얼로그 커스터마이즈 불가: 시스템 다이얼로그는 앱이 바꿀 수 없다: "Your app cannot customize the dialog that appears when you call launch()." 대신 UI(예: 버튼 문구)로 맥락을 준다. [S1]
- 거부 뒤 대체 경로: "Minimize functionality loss. Users should be able to access the app to whatever extent is possible without the requested permissions." "Highlight a specific part of your app's UI where there is limited functionality because your app doesn't have the necessary permission." [S1]
- 거부 이유 설명은 요청 시점과 거부 후 모두: "you should provide an explanation of your request both at the time of the request and in a follow-up dialog if the user denies the request." [S3]

## 후보 비교

### 권한별 선언·요청 조건
| 대상 | 선언(minSdk 26, targetSdk 37 기준) | 런타임 요청 여부 | 요청 시점 문장 | 출처 |
|---|---|---|---|---|
| Bluetooth, Android 12(API 31)+ | `BLUETOOTH_SCAN`(+`neverForLocation` 가능), `BLUETOOTH_CONNECT`, 필요 시 `BLUETOOTH_ADVERTISE` | 셋 다 런타임("Nearby devices") | 탐색·광고·페어링된 기기 통신 전 | S10 |
| Bluetooth, Android 11 이하(8~11) | `BLUETOOTH`·`BLUETOOTH_ADMIN`에 `maxSdkVersion="30"`, `ACCESS_FINE_LOCATION`(스캔용, `maxSdkVersion="30"` 상태에서 적용) | `ACCESS_FINE_LOCATION`만 런타임. `BLUETOOTH`·`BLUETOOTH_ADMIN`은 런타임 아님(문서가 런타임이라 부르는 것은 위치 권한뿐) | 스캔 시작 전 | S10 |
| BLE 스캔에 위치 필요 조건 | 12+: 스캔 결과로 물리 위치를 도출하면 `ACCESS_FINE_LOCATION`, 아니면 `neverForLocation`+`maxSdkVersion="30"`. 11 이하: 항상 필요 | 위 행 | | S10 |
| 위치 포그라운드 | `ACCESS_COARSE_LOCATION`(+정밀이 필요하면 `ACCESS_FINE_LOCATION`), 서비스면 `foregroundServiceType="location"` | 런타임 | 기능 상호작용 시 | S12, S4 |
| 위치 세션(일회) | 위치 버튼(`USE_LOCATION_BUTTON`+위치 권한), Play 정책: 타깃 Android 17+·세션 기능만이면 필수 | 버튼 탭으로 요청 | 사용자 탭 | S13, S12, S18 |
| 위치 백그라운드 | `ACCESS_BACKGROUND_LOCATION`(Android 10+) | 런타임, 포그라운드 허용 뒤에만 | 포그라운드 허용 후 | S4, S12 |
| 알림 | `POST_NOTIFICATIONS`(Android 13+) | 33+에서 런타임, 12 이하 없음 | 사용자 동작·앱에 익숙해진 뒤 | S14 |
| 사진 선택 | 없음(photo picker) | 불필요 | | S2, S15 |
| 사진 촬영 | 없음(`ACTION_IMAGE_CAPTURE`, `CAMERA` 선언 금지) | 불필요 | | S2 |
| 파일 열기·저장 | 없음(SAF `ACTION_OPEN_DOCUMENT`/`ACTION_CREATE_DOCUMENT`) | 불필요 | | S2, S28 |
| BT 페어링 | `uses-feature companion_device_setup`, 위치·`BLUETOOTH_ADMIN` 선언 안 함 | 페어링 UI가 대신함(API 26+) | | S2, S11 |

### 권한 없는 대안
| 하고 싶은 일 | 권한 없는 방법 | 그 방법이 안 맞을 때 | 출처 |
|---|---|---|---|
| 사진·동영상 첨부 | photo picker(`PickVisualMedia`) | Play는 핵심 기능이 피커로 안 될 때만 `READ_MEDIA_*` 허용 | S2, S15, S18 |
| 문서 저장·열기 | SAF | 브로드 접근은 별도 심사(All files access) | S2, S28, S18 |
| 근처 BT 기기 페어링 | CDM | 페어링·연결을 직접 제어하려면 BT 권한 세트 | S10, S11 |
| 현재 위치 한 번 | 위치 버튼 / 주소 직접 입력 | 지속 위치는 위치 권한 | S2, S13 |

## 규칙 후보
(출처가 뒷받침하는 것. 문장화는 팩이 한다.)
1. 권한은 그 권한이 필요한 기능을 사용자가 시작하는 시점에 요청한다. 앱 시작 시 일괄 요청하지 않는다. — S1, S3, S18
2. 요청 전에 `checkSelfPermission` 결과를 확인하고, 거부 상태에서 `shouldShowRequestPermissionRationale`가 true일 때만 근거 UI를 보인 뒤 요청한다. 근거 UI에는 취소 수단을 둔다. — S1
3. 권한 확인은 그 권한이 필요한 동작을 수행할 때마다 한다(허용 상태를 캐시해 믿지 않는다). 시스템이 자동으로 거부·회수할 수 있다. — S1, S16, S17, S14
4. 요청 코드는 `RequestPermission`/`RequestMultiplePermissions` 계약(Compose에서는 `rememberLauncherForActivityResult`)으로 쓴다. 직접 요청 코드 관리는 쓰지 않는다. — S1, S5
5. `launch()`는 사용자 상호작용 콜백에서 부르고, 조합 도중에 부르지 않는다. 조합 뒤 실행이 필요하면 `LaunchedEffect`/`DisposableEffect` 안에서 부른다. — S5
6. 권한 요청(launcher, 시스템 상태 읽기)은 UI 계층이 담당하고 ViewModel은 UI 상태로 변환된 결과만 다룬다. — S8, S9 (+ 팩 R-12-10)
7. 결과 콜백은 Activity가 만들어질 때마다 무조건 등록한다. 결과 처리에 필요한 추가 상태는 별도로 저장·복원한다. — S7, S27
8. 거부되면 기능을 축소해 계속 쓸 수 있게 하고, 막힌 기능의 자리에서 구체적으로 알린다. 전체 화면 차단·반복 재요청은 하지 않는다. — S1, S18
9. 권한이 필요 없는 방법이 있으면 그것을 쓴다: 사진·동영상은 photo picker, 촬영은 `ACTION_IMAGE_CAPTURE`, 문서는 SAF, 근처 BT 기기 초기 연결은 CDM. — S2, S15, S11, S28
10. Bluetooth 선언: Android 12+ 권한(`BLUETOOTH_SCAN`·`BLUETOOTH_CONNECT`·`BLUETOOTH_ADVERTISE`)은 필요한 것만 선언하고, 레거시 `BLUETOOTH`·`BLUETOOTH_ADMIN`에는 `maxSdkVersion="30"`을 준다. 스캔 결과로 위치를 도출하지 않으면 `neverForLocation`을 주고 `ACCESS_FINE_LOCATION`에 `maxSdkVersion="30"`을 준다. — S10
11. 위치는 대략(`ACCESS_COARSE_LOCATION`) 우선, 정밀은 필요할 때만 추가하고, 사용자가 대략만 허용해도 동작해야 한다. — S12, S2
12. `ACCESS_BACKGROUND_LOCATION`은 포그라운드 위치가 허용된 뒤에만 요청한다. — S4
13. 세션 단위 위치만 필요하면 위치 버튼을 쓴다(타깃 Android 17+·Play 정책). — S12, S13, S18
14. `POST_NOTIFICATIONS`는 타깃 13+에서 사용자 동작 등 맥락 있는 시점에 요청하고, 발송 전 `areNotificationsEnabled()`로 확인한다. Android 13 미만에서는 런타임 요청을 호출하지 않는다(`SDK_INT` 가드). — S14
15. 백그라운드 위치·기대 밖 민감 데이터 수집은 권한 요청 직전에 앱 안 공개 화면을 두고, 사용자가 거절할 수 있으며 뒤로가기·화면 밖 탭을 동의로 보지 않는다. — S19, S20, S21
16. 테스트: 계측 UI 테스트는 `GrantPermissionRule`(API 28+는 `grantRuntimePermission`) 또는 UI Automator `watchFor(PermissionDialog)`로 다이얼로그를 처리하고, 허용·거부 조합을 모두 시험한다. — S23, S22, S3
17. 자동 재설정을 앱이 회피하기보다 복귀 시 권한을 다시 확인해 대응한다(사용자를 설정으로 안내하는 API는 존재함). — S16, S17

팩 결정 필요(출처가 정하지 않음):
- 권한 상태를 ViewModel에 어떻게 넘길지(Boolean 같은 프레임워크 무관 값, 허용 여부를 UiState에 넣을지). 출처는 UI가 요청하고 VM은 UI 상태로 변환된 결과만 다룬다는 원칙까지만 말한다.
- Accompanist Permissions 허용 여부. 공식 문서는 계속 언급하나 experimental이고 0.37.3(2025-04-28)이 마지막 배포다. `rememberLauncherForActivityResult`만 쓰는 쪽이 공식 기본 경로다.
- 권한 요청을 트리거하는 이벤트를 R-12-03(일회성 이벤트 push 금지)과 어떻게 맞출지. 출처가 이 충돌을 다루지 않는다.
- Bluetooth 권한의 SDK 분기 구현(12+ vs 8~11). 출처는 매니페스트 선언까지만 준다.
- 영구 거부 감지와 설정 화면 안내 흐름. 출처는 안내하되 설득 목적으로 설정 링크를 걸지 말라는 코드 주석만 있고, 영구 거부 감지법도 제시하지 않는다(Accompanist는 구분 불가라고 명시).
- 사전 안내(근거) 화면을 모든 권한에 강제할지. 플랫폼은 `shouldShowRequestPermissionRationale`가 true일 때만, Play는 기대 밖 수집에만 요구한다.
- 알림을 앱 시작 몇 번째 실행에 요청할지 같은 수치. 출처는 "third or fourth time" 예시만 든다.

## 출처가 침묵하는 것 (규칙으로 쓰지 않음)
- ViewModel·UiState에 권한 상태를 어떤 타입으로 담는지, 권한 결과를 화면 상태로 바꾸는 구체 패턴.
- 로컬(JVM) 테스트에서 권한 상태를 fake로 대체하는 방법. 권한 확인을 인터페이스로 감싸라는 문장은 권한 문서에 없다.
- `rememberLauncherForActivityResult`를 조건 분기 안에서 부르지 말라는 명시 문장(Activity Result API 문서의 "unconditionally registered" 원칙에서 유추만 가능).
- 영구 거부를 감지하는 공식 API. 공식 문서는 두 번 거부 뒤 시스템 대화상자가 안 뜬다는 사실과 디버그 플래그(`USER_FIXED`)만 알려 준다.
- 정밀/대략 위치 선택 다이얼로그의 구성과 앱 쪽 처리 절차(최신 S12에는 정확도 설명만 남아 있다).
- CDM 페어링 후 연결 단계에서 `BLUETOOTH_CONNECT`가 필요한지.
- 권한 요청 화면을 별도 Navigation 3 목적지로 둘지 기능 화면 안에 인라인으로 둘지.
- 멀티 권한 요청을 한 번에 묶을지 단계별로 나눌지(위치 두 단계 외).

## 미확인
| 대상 | URL | 이유 |
|---|---|---|
| Material Design 권한 패턴 페이지 | https://m2.material.io/design/platform-guidance/android-permissions.html (구 URL https://material.io/design/platform-guidance/android-permissions.html 은 위 주소로 이동) | JavaScript 렌더링 페이지라 curl 본문이 "This website requires JavaScript." (curl 텍스트) 뿐이고 web.archive.org 사본도 같음. WebFetch도 내용을 못 얻음. 질문 10은 대신 S1·S3·S20 문장으로 채움 |
| Compose 전용 "Request runtime permissions" 페이지 | https://developer.android.com/develop/ui/compose/system/request-permissions 외 3개 추정 URL | 404. Compose 예시는 S5, S14로 대체 |
| Android 12 "approximate location" 변경 문서 | https://developer.android.com/about/versions/12/approximate-location | S12로 이동해 정밀/대략 선택 UI 서술이 사라짐. Android 12 동작 변경 페이지(behavior-changes-12)에는 관련 문장을 찾지 못함 |
| "권한 그룹"에 대한 Compose 무관 공식 문장 | https://developer.android.com/guide/topics/permissions/overview | 열었으나 이번 질문(그룹·자동 재설정)에는 S1·S16·S17로 충분해 별도 인용 안 함. 그룹 문장 "permissions can change groups without notice, so don't assume that a particular permission is grouped with any other permission." 은 페이지에서 확인했으나 본 노트 인용에는 쓰지 않음 |
| GrantPermissionRule 대체 API(`grantRuntimePermission`) 시그니처 | 미조회 | S23 문장 한 줄로만 확인. 클래스·메서드 위치는 열어 보지 않음 |
| Play "Minimum Scope: Foreground Location Access and the Location Button" 도움말 | 링크만 S21에서 확인 | 본문을 열지 않음. 위치 버튼 정책 상세(적용 대상 판정 기준)는 미확인, S18의 "(effective January 27, 2027)"만 확인 |
| 보정된 사실 | | S6의 "저장소 마지막 push 2025-08-18"과 "0.37.3 배포 2025-04-28"은 GitHub API·Maven 메타데이터 값이며 WebFetch 요약이 아님 |
| 페이지 안 에이전트 지시 문구 | | 열어 본 페이지 중 에이전트에게 무언가를 하라는 지시 문구는 없었다. |
