# 18 앱 셸

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈). 출처 번호(S..)는 90-sources.md.

이 문서는 화면 하나가 아니라 앱 껍데기 — `Application`, 루트 Activity, 시작 초기화, 테마, 창 크기 — 만 다룬다. 컴포저블 작성 규칙은 17, 백스택·엔트리는 13, Hilt 진입점 개수는 R-14-03, 모듈 소유권은 10이 소유한다. 시작 시간 회귀 기준은 R-40-02, 매니페스트 컴포넌트 노출 기본값은 R-40-07이다. 인셋과 적응형 레이아웃의 구현 레시피는 로컬 `edge-to-edge`·`adaptive` 스킬을 본다.

## 초기화 배치 결정 매트릭스

| 판단 기준 | 선택지 A: App Startup `Initializer` | 선택지 B: `Application.onCreate` | 기본값 |
|---|---|---|---|
| 라이브러리가 자체 `ContentProvider`로 자동 초기화하나 | A(하나로 합침) | B | A |
| 첫 프레임 전에 반드시 끝나야 하나 | A | B | 아니면 둘 다 아님 |
| 처음 쓰이는 시점까지 미룰 수 있나 | 지연(`AppInitializer`) | 지연(Hilt 첫 주입) | 지연 |

시작 경로에 무엇을 두든 비용은 콜드 스타트로 돌아온다. 위 표에서 "지연"이 나오면 그쪽이 정답이다.

## 규칙

### R-18-01 `Application` 클래스는 `@HiltAndroidApp`만 달고 `onCreate`에서 블로킹 작업을 하지 않는다
- 규칙: `Application` 하위 클래스는 `@HiltAndroidApp` 선언과, 프로세스 첫 줄에서 반드시 걸어야 하는 등록(크래시 핸들러 같은 것)만 갖는다. 디스크·네트워크 I/O, DB 열기, 큰 객체 그래프 생성을 여기서 하지 않는다. 앱 데이터·세션 상태를 `Application` 필드에 보관하지 않고, 필요한 객체는 Hilt가 첫 주입 시점에 만들게 둔다.
- 근거: "Launch performance can suffer when your code overrides the `Application` object and executes heavy work or complex logic when initializing that object"이고, 해법은 지연 초기화이며 "Use a dependency injection framework like Hilt to create objects and dependencies when injected for the first time"라고 안내한다 [S36](https://developer.android.com/topic/performance/vitals/launch-time). 앱 컴포넌트에 앱 데이터나 상태를 저장하지 말라는 원칙 [S01](https://developer.android.com/topic/architecture). Hilt 앱은 `@HiltAndroidApp`이 붙은 `Application`을 반드시 갖는다 [S05](https://developer.android.com/training/dependency-injection/hilt-android) — 진입점 개수는 R-14-03이 소유한다.
- 예시:
  ```kotlin
  // Good
  @HiltAndroidApp
  class App : Application()
  // Bad — 시작 경로에서 디스크를 열고 상태를 들고 있다
  @HiltAndroidApp
  class App : Application() {
      lateinit var session: Session
      override fun onCreate() {
          super.onCreate()
          session = database.openSession()
      }
  }
  ```
- 체크: `Application.onCreate` 본문에 I/O·DB·네트워크 호출이 있는가. `Application`이 `lateinit var` 상태를 들고 있는가.

### R-18-02 시작 초기화는 매트릭스대로 배치하고 순서는 `dependencies()`로만 표현한다
- 규칙: 자체 `ContentProvider`로 자동 초기화되는 컴포넌트는 App Startup의 `Initializer<T>`로 옮겨 `InitializationProvider` 하나에 모은다. 초기화 순서가 필요하면 `dependencies()`에 선행 Initializer를 적고, 호출 위치나 지연으로 순서를 맞추지 않는다. 시작에 필요 없는 컴포넌트는 매니페스트 `meta-data`를 `tools:node="remove"`로 꺼 두고 `AppInitializer.initializeComponent()`로 실제 필요 시점에 초기화한다.
- 근거: "Instead of defining separate content providers for each component you need to initialize, App Startup allows you to define component initializers that share a single content provider. This can significantly improve app startup time"이고, `dependencies()`가 초기화 순서를 결정하며, "You can also use `AppInitializer` directly in order to manually initialize components that your app doesn't need at startup. This is called lazy initialization"라고 명시한다 [S143](https://developer.android.com/topic/libraries/app-startup). 콘텐츠 프로바이더로 시작 초기화를 한다면 App Startup을 고려하라는 권고 [S36](https://developer.android.com/topic/performance/vitals/launch-time)
- 예시:
  ```kotlin
  // Good — 순서는 dependencies() 로만 표현한다
  class LoggerInitializer : Initializer<Logger> {
      override fun create(context: Context): Logger = Logger.install(context)
      override fun dependencies() = listOf(CrashReporterInitializer::class.java)
  }
  // Good — 시작에 필요 없는 것은 쓰는 시점에 부른다
  AppInitializer.getInstance(context).initializeComponent(ImageLoaderInitializer::class.java)
  // Bad — 호출 순서로 의존을 맞추고 시작 경로에서 디스크를 읽는다
  override fun onCreate() { super.onCreate(); CrashReporter.install(this); Logger.install(this) }
  ```
- 체크: 라이브러리마다 별도 `ContentProvider`가 매니페스트에 쌓이는가. `Initializer`의 선후 관계가 `dependencies()`가 아니라 호출 순서로 표현돼 있는가.

### R-18-03 스플래시는 `installSplashScreen()`으로만 만들고 전용 Activity를 두지 않는다
- 규칙: `androidx.core:core-splashscreen`을 넣고 루트 Activity의 `onCreate`에서 `super.onCreate()` **앞에** `installSplashScreen()`을 부른다. 모양은 `windowSplashScreenBackground`·`windowSplashScreenAnimatedIcon`으로, 이어질 테마는 `postSplashScreenTheme`으로 지정한다. `SplashActivity` 같은 전용 Activity나 스플래시용 레이아웃을 만들지 않는다.
- 근거: "Call `installSplashScreen` in the starting activity before calling `super.onCreate()`"이고 `postSplashScreenTheme` 지정은 "This is required"이며, "Generally, we recommend removing your previous custom splash screen `Activity` altogether to avoid the duplication of splash screens, to increase efficiency, and to reduce splash screen loading times" [S145](https://developer.android.com/develop/ui/views/launch/splash-screen/migrate). Android 12부터 플랫폼 기본 동작이고 compat 라이브러리가 하위 버전을 덮는다("The core `SplashScreen` library brings the Android 12 splash screen to all devices from API 23") [S144](https://developer.android.com/develop/ui/views/launch/splash-screen) — minSdk 26이므로 분기 없이 쓴다. 안정판은 `core-splashscreen` 1.2.0 [S157](https://developer.android.com/jetpack/androidx/releases/core)
- 예시:
  ```kotlin
  // Good
  override fun onCreate(savedInstanceState: Bundle?) {
      installSplashScreen()
      super.onCreate(savedInstanceState)
  }
  // Bad — 전용 Activity 로 스플래시를 그리고 넘긴다
  class SplashActivity : ComponentActivity() {
      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          startActivity(Intent(this, MainActivity::class.java)); finish()
      }
  }
  ```
- 체크: 매니페스트에 스플래시 전용 Activity가 있는가. `installSplashScreen()`이 `super.onCreate()` 뒤에 있는가.

### R-18-04 `setKeepOnScreenCondition`은 로컬 상태를 읽는 동안만 참으로 둔다
- 규칙: 스플래시 유지 조건은 첫 프레임을 그리는 데 필요한 로컬 값(설정·로그인 여부 같은 작은 데이터)을 읽는 동안에만 참이다. 네트워크 응답, 원격 설정, 동기화 완료를 조건으로 걸지 않는다. 조건이 풀린 뒤 곧바로 스피너가 또 나오면 조건 위치가 잘못된 것이다.
- 근거: "If you need to load a small amount of data, such as loading in-app settings from a local disk asynchronously, you can use `ViewTreeObserver.OnPreDrawListener` to suspend the app to draw its first frame"이고, "The splash screen must only be dismissed with `onResume()` when the app is stable from a visual standpoint, so no additional spinners are needed" [S144](https://developer.android.com/develop/ui/views/launch/splash-screen). 유지 조건 API는 `splashScreen.setKeepOnScreenCondition { }` [S145](https://developer.android.com/develop/ui/views/launch/splash-screen/migrate). 시작 경로의 네트워크 대기는 콜드 스타트 회귀로 잡힌다(R-40-02) [S36](https://developer.android.com/topic/performance/vitals/launch-time)
- 예시:
  ```kotlin
  // Good — 로컬 설정을 읽는 동안만
  splashScreen.setKeepOnScreenCondition { !viewModel.localSettingsLoaded.value }
  // Bad — 서버 응답을 기다린다
  splashScreen.setKeepOnScreenCondition { !viewModel.remoteConfigFetched.value }
  ```
- 체크: 유지 조건이 참조하는 값이 로컬에서 오는가. 스플래시가 끝난 직후 전체 화면 로딩이 또 나오는가.

### R-18-05 루트 Activity는 런처 하나뿐이고 `launchMode`는 기본값으로 둔다
- 규칙: 매니페스트에 `<activity>`는 루트 `ComponentActivity` 하나만 등록하고 `MAIN`/`LAUNCHER` 인텐트 필터를 여기에만 붙인다. 이 항목이 `android:exported="true"`가 되는 유일한 예외다(기본값 false는 R-40-07). `android:launchMode`는 적지 않아 `standard`로 두고 `singleTask`·`singleInstance`는 쓰지 않는다. IME 인셋을 받으려면 이 항목에 `android:windowSoftInputMode="adjustResize"`를 적는다. **예외(2026-10-15)**: 외부·알림 딥링크 인텐트 필터를 이 루트 Activity가 받는 단일 Activity 앱은 `MainActivity`에만 `android:launchMode="singleTask"`를 둘 수 있다 — 링크가 두 번째 인스턴스를 만들지 않고 `onNewIntent`(`addOnNewIntentListener`)로 들어오게 하기 위해서다. 조건은 셋이다: Activity가 하나뿐이라 task 안에 "위에 쌓인 Activity를 지우는" 부작용이 생길 곳이 없고, `onNewIntent`를 처리하며, 링크 해석·보류는 R-13-08·R-13-10을 따른다. 두 번째 Activity가 생기면 이 예외는 다시 본다.
- 근거: 런처 Activity처럼 인텐트 필터를 가진 컴포넌트는 `android:exported="true"`여야 하고 API 31부터 선언이 필수다. "The modes `singleTask`, `singleInstance`, and `singleInstancePerTask` are not appropriate for most applications"이며 `standard`가 대부분의 Activity에 맞는 기본값이다 [S156](https://developer.android.com/guide/topics/manifest/activity-element). "Set `android:windowSoftInputMode="adjustResize"` in your Activity's `AndroidManifest.xml` entry. This setting allows your app to receive IME insets" [S146](https://developer.android.com/develop/ui/compose/system/setup-e2e). 화면 전환은 Activity가 아니라 컴포저블로 한다(R-14-03) [S05](https://developer.android.com/training/dependency-injection/hilt-android). 예외의 근거 — `standard`는 "every time there's a new intent for a `"standard"` activity, a new instance of the class is created"이고 `singleTop`도 "a new instance of a `"singleTop"` activity can also be created to handle a new intent"이며 대상 task의 맨 위에 있을 때만 `onNewIntent`로 재사용된다. `singleTask`는 기기에 인스턴스가 하나뿐이고 task 루트에 놓인다. 문서가 `singleTask`를 "not appropriate for most applications"라 한 이유는 "an interaction model that is likely to be unfamiliar to users"인데, Activity가 하나뿐이면 그 모델 차이가 드러날 곳이 없다 [S156](https://developer.android.com/guide/topics/manifest/activity-element). 암시적 딥링크는 "If the flag is set, the task back stack is cleared and replaced with the deep link destination" / "If the flag is not set, you remain on the task stack of the previous app … the Back button takes you back to the previous app"라서 호출하는 앱이 어느 플래그로 부를지를 우리가 정할 수 없다 [S344](https://developer.android.com/guide/navigation/design/deep-link). 공식 문서가 단일 Activity를 예외로 적지는 않는다 — InfocarEV 사례(`docs/design/app-entry-and-links.md` §2, 2026-10-15)에서 나온 팩 결정(91-decisions SINGLE_TASK)이다. 플래그 없이 오는 외부 앱(브라우저) 링크에서의 Back 동작은 실기기로 확인하지 않았다(미확인 — `adb shell am start`는 항상 `FLAG_ACTIVITY_NEW_TASK`를 붙여 재현하지 못한다).
- 예시:
  ```xml
  <!-- Good -->
  <activity android:name=".MainActivity" android:exported="true"
      android:windowSoftInputMode="adjustResize" android:theme="@style/Theme.App.Starting">
      <intent-filter>
          <action android:name="android.intent.action.MAIN" />
          <category android:name="android.intent.category.LAUNCHER" />
      </intent-filter>
  </activity>
  <!-- Good(예외) — 단일 Activity 앱이 딥링크를 받는다: singleTask + onNewIntent, 필터는 스킴·호스트까지(경로·id 는 코드 화이트리스트) -->
  <activity android:name=".MainActivity" android:exported="true" android:launchMode="singleTask"> <!-- + VIEW/BROWSABLE 필터 --> </activity>
  <!-- Bad — 화면마다 Activity 를 만들고 launchMode 로 중복을 막으려 한다 -->
  <activity android:name=".DetailActivity" android:exported="false" android:launchMode="singleTask" />
  ```
- 체크: 매니페스트의 `<activity>` 개수가 1인가. `launchMode`가 적힌 항목이 있는가 — 있다면 `singleTask`이고 Activity가 하나뿐이며 딥링크 필터가 그 항목에 있고 `onNewIntent`를 처리하는가.

### R-18-06 `setContent`는 테마 컴포저블과 앱 루트 컴포저블만 호출한다
- 규칙: 루트 `onCreate`의 순서는 `installSplashScreen()` → `super.onCreate()` → `enableEdgeToEdge()` → `setContent`로 고정한다. `setContent` 블록 안은 `AppTheme { AppRoot() }` 한 줄이다. 상태 수집, 로그인 여부 분기, 백스택 조작을 Activity에 두지 않고 `AppRoot` 아래로 내린다.
- 근거: 테마를 앱 진입점에서 감싸는 형태가 `setContent { ReplyTheme { /* App content */ } }`다 [S151](https://developer.android.com/develop/ui/compose/designsystems/material3). `installSplashScreen`은 `super.onCreate()` 앞 [S145](https://developer.android.com/develop/ui/views/launch/splash-screen/migrate), `enableEdgeToEdge()`는 `Activity.onCreate()` 안에서 호출한다 [S146](https://developer.android.com/develop/ui/compose/system/setup-e2e). 백스택과 feature 조합을 `:app` 최상위 컴포저블이 소유한다는 규정은 R-13-03이고 [S21](https://developer.android.com/guide/navigation/navigation-3/basics), 로그인 분기 같은 상태 소비는 UI 계층의 상태 홀더 몫이다 [S08](https://developer.android.com/topic/architecture/ui-layer)
- 예시:
  ```kotlin
  // Good
  override fun onCreate(savedInstanceState: Bundle?) {
      installSplashScreen()
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      setContent { AppTheme { AppRoot() } }
  }
  // Bad — Activity 가 상태를 수집하고 화면을 고른다
  setContent { if (viewModel.isLoggedIn) HomeScreen() else LoginScreen() }
  ```
- 체크: `setContent` 블록이 두 줄을 넘는가. Activity 본문에 `collectAsStateWithLifecycle`이나 `backStack`이 있는가.

### R-18-07 `enableEdgeToEdge()`를 `onCreate`에서 호출하고 edge-to-edge opt-out을 쓰지 않는다
- 규칙: 루트 Activity의 `onCreate`에서 `enableEdgeToEdge()`를 부른다. `windowOptOutEdgeToEdgeEnforcement`, `Window.setDecorFitsSystemWindows(true)` 같은 회피 수단을 쓰지 않는다. 시스템 바를 불투명하게 만들어 인셋 처리를 건너뛰지 않는다. `androidx.activity`의 `enableEdgeToEdge()`(ComponentActivity)는 시스템 바 아이콘 색을 자동으로 처리하므로 그 함수를 쓴다. `WindowCompat` 쪽 함수를 쓰면 `isAppearanceLightStatusBars`·`isAppearanceLightNavigationBars`를 테마에서 직접 맞춰야 한다.
- 근거: "If you target devices running Android 15 (API level 35) or higher, edge-to-edge is enforced by default"이고 이전 버전에서는 "call `enableEdgeToEdge()` in your `Activity.onCreate()` method"로 맞춘다 [S146](https://developer.android.com/develop/ui/compose/system/setup-e2e). "For apps targeting Android 16 (API level 36), `R.attr#windowOptOutEdgeToEdgeEnforcement` is deprecated and disabled, and your app can't opt-out of going edge-to-edge" [S148](https://developer.android.com/about/versions/16/behavior-changes-16) — 단 같은 문서가 실행 OS를 구분한다: Android 16 타깃 앱도 "running on an Android 15 device"면 opt-out이 "continues to work"하고, Android 16 기기에서만 "is disabled"다. 구버전 기기에서도 opt-out을 쓰지 않는 것은 이 팩의 정책이다. `Window#setDecorFitsSystemWindows(boolean)`는 Android 15에서 deprecated + disabled 목록에 있다 [S147](https://developer.android.com/about/versions/15/behavior-changes-15). "DO NOT do this if the Activities use `enableEdgeToEdge` from `ComponentActivity` because it handles the icon colors automatically" [S274](https://developer.android.com/agents/skills/system/edge-to-edge/skill).
- 예시:
  ```kotlin
  // Good
  enableEdgeToEdge()
  setContent { AppTheme { AppRoot() } }
  // Bad — targetSdk 35+ 에서 효과가 없는 우회
  window.setDecorFitsSystemWindows(true)
  ```
- 체크: `enableEdgeToEdge()` 호출이 루트 `onCreate`에 있는가. 테마나 코드에 edge-to-edge 회피 설정이 남아 있는가.

### R-18-08 인셋은 앱 루트나 `Scaffold` 한 곳에서 소비하고 화면은 다시 더하지 않는다
- 규칙: 시스템 바 인셋은 `Scaffold`의 `innerPadding` 또는 루트의 `Modifier.safeDrawingPadding()` 한 곳에서만 소비한다. `Scaffold`는 인셋을 소비하지 않으므로 `innerPadding`을 적용한 그 자리에서 `consumeWindowInsets(innerPadding)`으로 소비를 표시한다. 그 아래 화면은 남은 인셋(IME 등)만 처리하고 같은 인셋을 다시 더하지 않는다. `:app`은 `innerPadding`을 엔트리 빌더 인자(`contentPadding`)로 Route에 넘기고 Route가 Screen에 내려보내며(R-17-01), 화면은 그것을 한 번만 쓴다 — 스크롤 목록이 있으면 `Modifier.padding`이 아니라 목록의 `contentPadding`으로 받아 콘텐츠가 시스템 바 뒤로 스크롤되게 하고, 목록이 없으면 루트 컨테이너의 `Modifier.padding`으로 쓴다. 소비 표시는 `:app`이 `NavDisplay`에 한 번만 한다(`Modifier.consumeWindowInsets(innerPadding)`). 목록이 `Scaffold` 바로 아래에 있을 때만 목록에 그 표시를 한다. 단, 목록 안에 텍스트 입력이 있으면 마지막 입력창이 IME에 가려지지 않도록 아래쪽은 `contentPadding` 대신 목록 끝의 `Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))`로 채우고 목록에 `imePadding()`을 건다(근거의 S149). 인셋 값을 숫자로 읽어 계산하지 말고 패딩·크기 modifier를 쓴다. 플로팅 액션 버튼은 `Scaffold`의 `floatingActionButton` 슬롯 안에 두거나 `Modifier.safeDrawingPadding()`을 적용해 내비게이션 바 위에 그린다. `NavigationSuiteScaffold`(R-18-13)가 루트이면 `PaddingValues`를 내려보내지 않아 `:app`이 넘길 `innerPadding`이 없으므로 화면이 자기 목록 `contentPadding`·FAB 패딩에 직접 인셋을 적용하고 (소비 없이 원본 인셋이 필요하면 `WindowInsets.asPaddingValues()`를 쓸 수 있다 — 근거의 S149), 그 부모에는 `safeDrawingPadding`을 걸지 않는다. 이 경우에 한해 아래 체크의 `WindowInsets` 직접 읽기 금지를 완화한다.
- 근거: 인셋 padding modifier는 자동으로 인셋을 consume해 중복 적용을 막고, 직접 소비를 표시할 때는 `Modifier.consumeWindowInsets()`를 쓴다. "The value of insets is updated after the composition phase, but before the layout phase"이고 소비 없이 원본 인셋이 필요하면 "use the `WindowInsets` values directly, or use `WindowInsets.asPaddingValues()`"이지만 "prefer to use the window insets padding modifiers and window insets size modifiers wherever possible"다 [S149](https://developer.android.com/develop/ui/compose/system/insets-ui). 루트에서 한 번 소비하는 형태(`Box(Modifier.safeDrawingPadding())`)가 기본 예시다 [S146](https://developer.android.com/develop/ui/compose/system/setup-e2e). "`Scaffold` does not apply the insets to content; this responsibility is yours"이고 예시가 `Modifier.consumeWindowInsets(innerPadding)`으로 소비를 표시한다 [S159](https://developer.android.com/develop/ui/compose/system/material-insets). 체크리스트가 "Do FABs draw above the navigation bars by either being inside a Scaffold or by applying `Modifier.safeDrawingPadding()`?"로 묻는다 [S274](https://developer.android.com/agents/skills/system/edge-to-edge/skill). "the adaptive scaffolds (e.g. `NavigationSuiteScaffold`, `ListDetailPaneScaffold`) don't propagate PaddingValues to their inner contents"이고 "DO NOT apply `safeDrawingPadding` or similar modifiers to the `NavigationSuiteScaffold` parent" [S274](https://developer.android.com/agents/skills/system/edge-to-edge/skill).
- 예시:
  ```kotlin
  // Good — 루트에서 한 번 소비하고 contentPadding 으로 화면에 내려보낸다
  Scaffold { innerPadding ->
      AppNavDisplay(Modifier.consumeWindowInsets(innerPadding), contentPadding = innerPadding)
  }
  // Good — 스크롤 목록은 innerPadding 을 contentPadding 으로 받는다 (S159 예시와 같은 형태)
  Scaffold { innerPadding ->
      LazyColumn(modifier = Modifier.consumeWindowInsets(innerPadding), contentPadding = innerPadding) { /* ... */ }
  }
  // Good — Scaffold 를 쓰지 않는 루트
  Box(Modifier.safeDrawingPadding()) { AppRoot() }
  // Bad — 화면에서 같은 인셋을 또 더한다
  Column(Modifier.padding(innerPadding).safeDrawingPadding()) { /* ... */ }
  ```
- 체크: 한 경로에서 인셋 패딩이 두 번 붙는가. 화면이 `WindowInsets` 값을 직접 읽어 dp 계산을 하는가. 스크롤 목록이 `innerPadding`을 `Modifier.padding`으로 받아 콘텐츠가 시스템 바 영역 안에서 잘리는가. FAB가 `Scaffold` 밖에서 인셋 없이 그려지는가.

### R-18-09 시스템 바 대비는 색 설정 API가 아니라 콘텐츠 계층의 보호 컴포저블로 만든다
- 규칙: 상태 바·내비게이션 바 뒤로 콘텐츠가 지나가 아이콘이 읽히지 않으면, 인셋 높이만큼의 스크림 컴포저블을 콘텐츠 위에 그려 대비를 만든다. `window.statusBarColor`·`navigationBarColor` 같은 색 설정으로 해결하려 하지 않는다. 3버튼 내비게이션 스크림을 끌 때만 `window.isNavigationBarContrastEnforced = false`를 쓴다. `NavigationBar`·`BottomAppBar` 같은 하단 바가 있는 화면은 SDK 29 이상에서 이 설정을 꺼 하단 바 색이 화면 끝까지 이어지게 한다.
- 근거: 시스템 기본값이 모든 경우를 덮지 않으므로 앱이 보호를 그려야 하며, 공식 예시가 `WindowInsets.statusBars` 높이만큼의 `Spacer`에 그라데이션을 깔아 콘텐츠 뒤에 배치한다. 3버튼 내비게이션 대비는 `Window.setNavigationBarContrastEnforced`로 조절한다 [S150](https://developer.android.com/develop/ui/compose/system/system-bars). `R.attr#statusBarColor`, `Window#setStatusBarColor(int)`, `Window#setNavigationBarColor(int)`(제스처 내비게이션)는 Android 15에서 deprecated + disabled라 효과가 없다 [S147](https://developer.android.com/about/versions/15/behavior-changes-15). "set `window.isNavigationBarContrastEnforced = false` ... for SDK 29+. This prevents the system from adding a translucent background to the navigation bar" [S274](https://developer.android.com/agents/skills/system/edge-to-edge/skill).
- 예시:
  ```kotlin
  // Good — 콘텐츠 위에 상태 바 보호를 그린다
  Box {
      AppRoot()
      Spacer(
          Modifier.fillMaxWidth()
              .windowInsetsTopHeight(WindowInsets.statusBars)
              .background(MaterialTheme.colorScheme.surfaceContainer),
      )
  }
  // Bad — targetSdk 35+ 에서 무시된다
  window.statusBarColor = MaterialTheme.colorScheme.surface.toArgb()
  ```
- 체크: `statusBarColor`·`navigationBarColor` 대입이 남아 있는가. 밝은 콘텐츠 위 시스템 아이콘이 읽히는가.

### R-18-10 테마는 `:core:designsystem`의 `Color`·`Type`·`Shape`·`Theme` 파일로 나누고 테마 진입점은 `AppTheme` 하나로 둔다
- 규칙: 색·타이포·모양 정의는 `:core:designsystem`의 `theme/` 패키지에 `Color.kt`·`Type.kt`·`Shape.kt`·`Theme.kt`로 나눈다. `theme/` 패키지의 공개 API는 `AppTheme` 컴포저블 하나이고 팔레트 상수는 `internal`로 막는다. 버튼·카드 같은 공통 컴포넌트는 같은 모듈의 `component/` 패키지에서 따로 공개한다(R-17-14). feature나 화면에서 `MaterialTheme(...)`을 다시 호출하지 않는다. Compose Styles API(공식 `styles` 스킬)는 실험 API라 이 팩의 범위 밖이다. 도입하면 필요한 `-opt-in`은 모듈 빌드 파일이 아니라 컨벤션 플러그인(`KotlinAndroid.kt`)에서 준다(JVM 타깃을 컨벤션에서 주는 R-19-11과 같은 방식).
- 근거: Material 3 테마는 `MaterialTheme(colorScheme, typography, shapes)` 하나로 구성되고 공식 도구가 `Color.kt`·`Theme.kt` 분리 형태를 만든다 [S151](https://developer.android.com/develop/ui/compose/designsystems/material3). 커스텀 디자인 시스템 가이드는 테마 컴포저블 하나가 `MaterialTheme`을 감싸고 앱 루트가 그것을 호출하는 형태를 보이되 테마 중첩도 허용한다 [S152](https://developer.android.com/develop/ui/compose/designsystems/custom) — 진입점을 하나로 제한하는 것은 이 팩의 결정이다. `designsystem`을 core 모듈로 두는 구성 [S52](https://raw.githubusercontent.com/android/nowinandroid/main/docs/ModularizationLearningJourney.md). 공통 컴포넌트를 이 모듈에서 가져다 쓰는 규칙은 R-17-14다. `Shape.kt` 분리는 출처가 색·테마 두 파일까지만 보여 준 것을 같은 기준으로 확장한 이 팩의 결정이다. `styles` 스킬이 "this skill is EXPERIMENTAL", "This skill does not support Material Design component Styles"라고 하고 opt-in을 모듈 `build.gradle.kts`에 적게 한다 [S285](https://developer.android.com/agents/skills/jetpack-compose/theming/styles/skill). 이 팩은 JVM 타깃을 모듈에서 개별로 바꾸지 않고 컨벤션 플러그인에서 준다(R-19-11).
- 예시:
  ```kotlin
  // Good — theme/ 패키지의 공개 API 는 AppTheme 하나
  @Composable
  fun AppTheme(content: @Composable () -> Unit) {
      MaterialTheme(
          colorScheme = AppColorScheme, typography = AppTypography, shapes = AppShapes, content = content,
      )
  }
  // Bad — 화면이 자기 테마를 연다
  @Composable
  fun SettingsScreen() { MaterialTheme(colorScheme = lightColorScheme()) { /* ... */ } }
  ```
- 체크: `MaterialTheme(` 호출이 `:core:designsystem` 밖에 있는가. 색 상수가 feature에서 import 되는가.

### R-18-11 다크 모드는 `isSystemInDarkTheme()` 기본값으로 받고 색은 시맨틱 역할로만 참조한다
- 규칙: `AppTheme`은 `darkTheme: Boolean = isSystemInDarkTheme()` 파라미터를 받아 라이트·다크 `ColorScheme`을 고른다. 화면과 컴포넌트는 `MaterialTheme.colorScheme.*` 역할 이름으로만 색을 읽고, `Color(0xFF…)` 리터럴이나 밝기 분기를 직접 쓰지 않는다. 다이나믹 컬러는 브랜드 색을 포기해도 되는 화면에서만 켠다.
- 근거: 공식 테마 형태가 `darkTheme: Boolean = isSystemInDarkTheme()`를 받아 `LightColorScheme`/`DarkColorScheme`을 고르고, 다이나믹 컬러는 `dynamicDarkColorScheme`/`dynamicLightColorScheme`으로 분기한다 [S151](https://developer.android.com/develop/ui/compose/designsystems/material3). 색은 16진값이 아니라 시맨틱 역할로 부른다("Stop naming colors by their hex values; name them by their semantic role") [S158](https://developer.android.com/develop/ui/compose/designsystems/views-to-compose). 커스텀 값이 필요하면 `CompositionLocal`로 테마에 실어 내린다 [S152](https://developer.android.com/develop/ui/compose/designsystems/custom). 다크 프리뷰 의무는 R-17-02가 소유한다
- 예시:
  ```kotlin
  // Good
  @Composable
  fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
      val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
      MaterialTheme(colorScheme = colorScheme, content = content)
  }
  Text(text = title, color = MaterialTheme.colorScheme.onSurface)
  // Bad — 다크에서 안 보이는 고정 색
  Text(text = title, color = Color(0xFF212121))
  ```
- 체크: `Color(0x…)` 리터럴이 `:core:designsystem` 밖에 있는가. `isSystemInDarkTheme()` 호출이 테마 밖에 흩어져 있는가.

### R-18-12 XML 테마는 윈도우와 스플래시가 요구하는 최소만 두고 색 값을 이중 정의하지 않는다
- 규칙: `res/values/themes.xml`에는 시작 테마(`Theme.App.Starting`)와 그 뒤 테마(`Theme.App`) 둘만 둔다. 이 테마에는 스플래시 속성, `postSplashScreenTheme`, 윈도우 배경 정도만 적고 브랜드 색·타이포를 다시 정의하지 않는다. 색의 단일 출처는 `:core:designsystem`의 Compose 테마다.
- 근거: `postSplashScreenTheme`을 가리키는 XML 시작 테마는 스플래시 API가 요구하는 필수 항목이다("This is required") [S145](https://developer.android.com/develop/ui/views/launch/splash-screen/migrate). XML 테마와 Compose 테마를 동시에 두면 "two sources of truth"가 되어 변경이 두 곳으로 갈라지고, Compose 테마 값은 "Don't hardcode any migrated values"라는 지침 아래 한 출처에서만 관리해야 한다 [S158](https://developer.android.com/develop/ui/compose/designsystems/views-to-compose)
- 예시:
  ```xml
  <!-- Good — 시작 테마와 그 뒤 테마 둘뿐 -->
  <style name="Theme.App" parent="android:Theme.Material.NoActionBar" />
  <style name="Theme.App.Starting" parent="Theme.SplashScreen">
      <item name="windowSplashScreenBackground">@color/splash_background</item>
      <item name="postSplashScreenTheme">@style/Theme.App</item>
  </style>
  <!-- Bad — Compose 팔레트를 XML 에 다시 적어 두 곳이 갈라진다 -->
  <style name="Theme.App" parent="android:Theme.Material.NoActionBar">
      <item name="android:colorPrimary">#476810</item>
  </style>
  ```
- 체크: `themes.xml`에 색·타이포 항목이 있는가. 같은 브랜드 색이 XML과 Kotlin 양쪽에 적혀 있는가.

### R-18-13 `WindowSizeClass`는 앱 루트에서 한 번 계산해 상태로 내려보낸다
- 규칙: 창 크기는 앱 루트 컴포저블에서 `currentWindowAdaptiveInfoV2().windowSizeClass`로 한 번 읽고, 그 아래로는 "목록을 몇 열로 그릴지" 같은 결정 결과를 파라미터로 내려보낸다. 하단 바와 내비게이션 레일의 전환은 직접 분기하지 않고 `NavigationSuiteScaffold`에 맡기며, 바 표시 여부는 `rememberNavigationSuiteScaffoldState()`의 `show`·`hide`로 제어한다. 단 하단 바가 화면과 함께 움직여야 하는 앱(R-28-01 — 바를 탭 루트 엔트리 안에 그린다)은 `NavigationSuiteScaffold`가 바를 화면 바깥에 두므로 쓰지 않고 `NavigationBar`를 엔트리 데코레이터에서 직접 그린다. 그때 창 크기별 레일 전환은 정해 두지 않았다(미결정). 화면이나 컴포넌트가 `LocalConfiguration`으로 창을 직접 재지 않는다.
- 근거: 창 크기 클래스는 material3-adaptive의 `currentWindowAdaptiveInfoV2()`로 얻는다 — 1.3.0-alpha10이 "Deprecate `currentWindowAdaptiveInfo` and introduce V2 of it"이고 구 함수는 `ReplaceWith("currentWindowAdaptiveInfoV2")` 경고라 이 팩의 `allWarningsAsErrors`에서는 컴파일이 막힌다 [S160](https://developer.android.com/jetpack/androidx/releases/compose-material3-adaptive); 가이드 페이지는 아직 구 함수를 보여 준다. 브레이크포인트는 compact(<600dp)·medium(600~840dp)·expanded(840~1200dp)다. 공식 가이드는 "Localize the logic for handling display size changes by passing window size classes down as state to nested composables just like any other app state"라 하고, 예시에서 앱 루트 컴포저블의 기본 인자로 한 번 계산해 결정 결과(`showTopAppBar`)만 내려보낸다 [S153](https://developer.android.com/develop/ui/compose/layouts/adaptive/use-window-size-classes). 상태를 읽는 컴포저블들의 최소 공통 부모로 올리는 호이스팅 원칙과 같다(R-17-03) [S12](https://developer.android.com/develop/ui/compose/state-hoisting). "루트에서 정확히 한 번"으로 좁힌 것은 이 팩의 결정이다. 내비게이션 영역은 "Replace the container that held the navigation bar (often a `Scaffold`) with `NavigationSuiteScaffold` from the Material 3 adaptive layouts library"이고 표시 여부는 "`NavigationSuiteScaffold`'s `state` parameter"로 제어한다 [S279](https://developer.android.com/agents/skills/jetpack-compose/adaptive/skill).
- 예시:
  ```kotlin
  // Good — 루트에서 한 번 계산해 결정 결과만 내려보낸다
  @Composable
  fun AppRoot(windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass) {
      val gridColumns = if (windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) 2 else 1
      AppNavDisplay(gridColumns = gridColumns)
  }
  // Bad — 화면이 스스로 창을 잰다
  @Composable
  fun HomeScreen() { val wide = LocalConfiguration.current.screenWidthDp >= 600 }
  ```
- 체크: `currentWindowAdaptiveInfo()` 호출이 앱 루트 밖에 있는가. 화면이 `LocalConfiguration`으로 폭을 재는가. 하단 바와 레일의 전환을 `useNavRail` 같은 직접 분기로 만들었는가(`NavigationSuiteScaffold`를 쓰는가).

### R-18-14 구성 변경과 프로세스 사망은 `android:configChanges`로 피하지 않고 상태 복원으로 대응한다
- 규칙: 회전·다크 전환·다중 창에서 Activity가 재생성되는 것을 전제로 만든다. 재생성을 막으려고 `android:configChanges`를 적지 않는다. 백스택은 `rememberNavBackStack`, UI 상태는 `rememberSaveable`·`SavedStateHandle`로 복원하고, 저장 상태에는 ID 같은 작은 값만 넣는다. Activity 필드에 상태를 두지 않는다.
- 근거: "Don't opt out of `Activity` recreation as a shortcut to avoid state loss … It is impossible to entirely disable `Activity` recreation"이며 `ViewModel`과 `rememberSaveable`로 상태를 보존하라고 안내한다 [S154](https://developer.android.com/guide/topics/resources/runtime-changes). `ViewModel`은 구성 변경은 넘기지만 시스템이 죽인 프로세스는 넘기지 못하고, saved state는 둘 다 넘기는 대신 "only for primitive types and simple, small objects such as `String`"이다 [S155](https://developer.android.com/topic/libraries/architecture/saving-states). Nav3 백스택은 `rememberNavBackStack`과 `@Serializable` 키로 복원된다 [S22](https://developer.android.com/guide/navigation/navigation-3/save-state) — 소유 규칙은 R-13-03, 복원 대상 UI 상태 판단은 R-17-12다
- 예시:
  ```kotlin
  // Good — 재생성돼도 복원된다
  val backStack = rememberNavBackStack(HomeKey)
  var query by rememberSaveable { mutableStateOf("") }
  // Bad — Activity 필드에 상태를 두고 configChanges="orientation|screenSize" 로 재생성을 막는다
  class MainActivity : ComponentActivity() { private var query: String = "" }
  ```
- 체크: 매니페스트에 `android:configChanges`가 있는가. 회전 후 사라지면 곤란한 값이 `remember`나 Activity 필드에 있는가.

### R-18-15 `Scaffold` 기본 인셋에는 IME가 없으므로 텍스트 입력 화면은 키보드 인셋을 한 번만 처리한다
- 규칙: `Scaffold`의 기본 `contentWindowInsets`는 시스템 바와 디스플레이 컷아웃만 담고 IME(키보드)는 담지 않는다. 텍스트 입력이 있는 화면은 키보드 인셋을 다음 중 하나로 한 번만 처리한다 — `Scaffold(contentWindowInsets = WindowInsets.safeDrawing)`(IME 포함, 이때 `imePadding()`을 또 쓰지 않는다), `Modifier.fitInside(WindowInsetsRulers.Ime.current)`, 또는 `innerPadding`을 적용한 컨테이너에 `Modifier.imePadding()`. 같은 인셋을 `innerPadding`과 `imePadding()`으로 두 번 더하지 않는다(R-18-08). `windowSoftInputMode`는 `adjustResize`로 둔다(R-18-05 예시). 목록 안에 텍스트 입력이 있으면 아래쪽 처리는 R-18-08의 `Spacer` 단서를 따른다.
- 근거: "Scaffold's default `contentWindowInsets` does NOT contain IME insets"이고, IME가 이미 `innerPadding`에 들어 있는데 `imePadding()`을 또 쓰면 "there will be excess padding when the IME opens" [S274](https://developer.android.com/agents/skills/system/edge-to-edge/skill). 이 값은 우리 의존성에서도 `ScaffoldDefaults.contentWindowInsets`가 `WindowInsets.systemBars`와 `displayCutout`의 합집합으로 확인된다(스크래치 바이트코드 확인 2026-10-07). IME가 열릴 때 `imePadding()`이 아래쪽 패딩을 적용해 목록을 줄인다 [S149](https://developer.android.com/develop/ui/compose/system/insets-ui).
- 예시:
  ```kotlin
  // Good — IME 를 contentWindowInsets 로 받고 imePadding 은 쓰지 않는다
  Scaffold(contentWindowInsets = WindowInsets.safeDrawing) { innerPadding ->
      Column(Modifier.padding(innerPadding).consumeWindowInsets(innerPadding).verticalScroll(rememberScrollState())) { /* ... */ }
  }
  // Good — 기본 contentWindowInsets 에 imePadding 을 더한다
  Scaffold { innerPadding ->
      Column(Modifier.padding(innerPadding).consumeWindowInsets(innerPadding).imePadding().verticalScroll(rememberScrollState())) { /* ... */ }
  }
  // Bad — IME 인셋이 두 번 더해진다
  Scaffold(contentWindowInsets = WindowInsets.safeDrawing) { innerPadding ->
      Column(Modifier.padding(innerPadding).imePadding().verticalScroll(rememberScrollState())) { /* ... */ }
  }
  ```
- 체크: 텍스트 입력 화면에서 IME 인셋을 처리하는 방식이 하나뿐인가. `contentWindowInsets = WindowInsets.safeDrawing`과 `imePadding()`이 함께 있는가. 기본 `Scaffold`에 `imePadding()`도 없이 입력창이 키보드에 가려지는가.

### R-18-16 풀스크린 다이얼로그는 `decorFitsSystemWindows = false`로 edge-to-edge로 만든다
- 규칙: `DialogProperties(usePlatformDefaultWidth = false)`이고 내용이 `Modifier.fillMaxSize()`인 풀스크린 다이얼로그는 `decorFitsSystemWindows = false`를 지정해 상태 바·내비게이션 바 뒤까지 그린다. 일반 크기의 다이얼로그에는 쓰지 않는다.
- 근거: "If both the following conditions are true, then the Dialog is full screen and must be made edge-to-edge" — `DialogProperties`에 `usePlatformDefaultWidth = false`가 있고 `Modifier.fillMaxSize()`를 부를 때이며, 해법은 "set `decorFitsSystemWindows = false` in the `DialogProperties`"다 [S274](https://developer.android.com/agents/skills/system/edge-to-edge/skill).
- 예시:
  ```kotlin
  // Good
  Dialog(
      onDismissRequest = { /* ... */ },
      properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
  ) { Box(Modifier.fillMaxSize()) { /* ... */ } }
  // Bad — 풀스크린인데 decorFitsSystemWindows 가 기본값이라 바 뒤로 그려지지 않는다
  Dialog(onDismissRequest = { }, properties = DialogProperties(usePlatformDefaultWidth = false)) { Box(Modifier.fillMaxSize()) { } }
  ```
- 체크: `usePlatformDefaultWidth = false`와 `fillMaxSize()`를 함께 쓰는 다이얼로그에 `decorFitsSystemWindows = false`가 있는가.
