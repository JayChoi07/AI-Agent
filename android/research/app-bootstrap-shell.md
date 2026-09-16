# 앱 부트스트랩·셸 조사 노트

조사일: 2026-09-16
담당 references 파일 번호: 18(앱 셸)
전제(바꾸지 않음): compileSdk·targetSdk 37 · minSdk 26 · AGP 9.4.0 · Gradle 9.7.1 · JDK 17 · Kotlin 2.4.20 · Compose BOM 2026.08.00 · Hilt 2.60.1 · Navigation 3 stable 1.1.7

조사 범위는 "0에서 새 앱을 만들 때 껍데기로 정해야 하는 것"이다 — `Application`, 시작 초기화, 스플래시, 루트 Activity, edge-to-edge, 테마·디자인 시스템, 창 크기, 재생성 복원. 다국어·접근성 상세·테스트·빌드 설정(19가 소유)·BLE·Android Auto는 범위 밖으로 두었다.

## 확인한 문서와 확인 내용

모두 2026-09-16에 WebFetch로 본문을 열어 확인했다. 인용문은 원문 그대로다.

### App Startup (S143)
- URL: https://developer.android.com/topic/libraries/app-startup
- 존재 이유: "Instead of defining separate content providers for each component you need to initialize, App Startup allows you to define component initializers that share a single content provider. This can significantly improve app startup time."
- 순서: `dependencies()`가 선후를 결정한다. `create(context)`가 인스턴스를 만든다.
- 지연: "You can also use `AppInitializer` directly in order to manually initialize components that your app doesn't need at startup. This is called _lazy initialization_, and it can help minimize startup costs." 자동 초기화 해제는 매니페스트 `meta-data`에 `tools:node="remove"`.
- → R-18-02의 근거.

### 앱 시작 시간(Vitals) (기존 S36)
- URL: https://developer.android.com/topic/performance/vitals/launch-time
- "Launch performance can suffer when your code overrides the `Application` object and executes heavy work or complex logic when initializing that object."
- 해법: "the solution is lazy initialization … Instead of creating global static objects, move to a singleton pattern where the app initializes objects only the first time it needs them." + Hilt로 첫 주입 시 생성 + 콘텐츠 프로바이더 초기화라면 App Startup 고려.
- → R-18-01·R-18-02·R-18-04의 근거. 기존 S번호를 재사용했다(R-40-02가 같은 문서를 시작 시간 임계값 근거로 쓴다).

### 스플래시 화면 (S144) / 마이그레이션 (S145)
- URL: https://developer.android.com/develop/ui/views/launch/splash-screen , .../migrate
- Android 12부터 플랫폼 기본. compat: "The core `SplashScreen` library brings the Android 12 splash screen to all devices from API 23." minSdk 26이라 분기 없이 쓴다.
- 호출 위치: "Call `installSplashScreen` in the starting activity before calling `super.onCreate()`."
- `postSplashScreenTheme`: "Set the theme of the Activity that directly follows your splash screen. This is required."
- 전용 Activity: "Generally, we recommend removing your previous custom splash screen `Activity` altogether to avoid the duplication of splash screens, to increase efficiency, and to reduce splash screen loading times."
- 유지 조건: 로컬 소량 데이터일 때만("If you need to load a small amount of data, such as loading in-app settings from a local disk asynchronously…"), 그리고 "The splash screen must only be dismissed with `onResume()` when the app is stable from a visual standpoint, so no additional spinners are needed."
- 모양 속성: `windowSplashScreenBackground`, `windowSplashScreenAnimatedIcon`, `windowSplashScreenAnimationDuration`, `windowSplashScreenIconBackgroundColor`.
- → R-18-03·R-18-04·R-18-12의 근거.

### androidx.core 릴리스 노트 (S157)
- URL: https://developer.android.com/jetpack/androidx/releases/core
- `androidx.core:core-splashscreen` 안정판 **1.2.0**. (참고: `core-ktx` 1.19.0은 2026-06-03, core 본체로 병합돼 빈 아티팩트가 됨.)
- → R-18-03에서 버전만 인용. 실제 좌표·버전 고정은 19(빌드 설정)·버전 카탈로그 소관이라 규칙으로 수치를 강제하지 않았다.

### Compose edge-to-edge 설정 (S146)
- URL: https://developer.android.com/develop/ui/compose/system/setup-e2e
- "If you target devices running Android 15 (API level 35) or higher, edge-to-edge is enforced by default."
- "call `enableEdgeToEdge()` in your `Activity.onCreate()` method"
- 기본 동작: "makes the system bars transparent, except in 3-button navigation mode, where it applies a translucent scrim to the navigation bar for better contrast."
- IME: "Set `android:windowSoftInputMode="adjustResize"` in your Activity's `AndroidManifest.xml` entry."
- → R-18-05·R-18-06·R-18-07·R-18-08의 근거.

### Android 15 동작 변경 (S147) / Android 16 동작 변경 (S148)
- URL: https://developer.android.com/about/versions/15/behavior-changes-15 , .../16/behavior-changes-16
- 15: `R.attr#statusBarColor`, `Window#setStatusBarColor(int)`, `Window#setNavigationBarColor(int)`(제스처), `Window#setDecorFitsSystemWindows(boolean)` 등은 **deprecated + disabled**(효과 없음). `Window#setStatusBarContrastEnforced`, `setNavigationBarColor`(3버튼)는 deprecated이나 동작은 남음.
- 16: "For apps targeting Android 16 (API level 36), `R.attr#windowOptOutEdgeToEdgeEnforcement` is deprecated and disabled, and your app can't opt-out of going edge-to-edge."
- → targetSdk 37인 이 팩에는 opt-out 경로가 아예 없다. R-18-07·R-18-09의 근거.

### Compose 윈도우 인셋 (S149)
- URL: https://developer.android.com/develop/ui/compose/system/insets-ui
- padding modifier는 인셋을 자동 consume해 중복 적용을 막고, 직접 표시가 필요하면 `Modifier.consumeWindowInsets()`.
- "Insets are updated after composition but before layout phase … prefer using window insets padding modifiers and size modifiers wherever possible."
- → R-18-08의 근거.

### 시스템 바 보호 (S150)
- URL: https://developer.android.com/develop/ui/compose/system/system-bars
- 시스템 기본값이 모든 경우를 덮지 않으므로 앱이 보호를 그린다. 공식 예시는 `WindowInsets.statusBars` 높이 기반 `Spacer` + 그라데이션을 **콘텐츠 뒤에 배치**.
- 3버튼 내비게이션 대비는 `Window.setNavigationBarContrastEnforced(false/true)`.
- → R-18-09의 근거.

### Compose Material 3 테마 (S151) / 커스텀 디자인 시스템 (S152)
- URL: https://developer.android.com/develop/ui/compose/designsystems/material3 , .../custom
- `MaterialTheme(colorScheme, typography, shapes)` 한 곳. 공식 도구 산출물이 `Color.kt`·`Theme.kt` 분리.
- 다크: `darkTheme: Boolean = isSystemInDarkTheme()` 파라미터 → `LightColorScheme`/`DarkColorScheme`. 다이나믹 컬러는 `dynamicDarkColorScheme`/`dynamicLightColorScheme`(Android 12+).
- 앱 진입: `setContent { ReplyTheme { /* App content */ } }`.
- 커스텀 값: `CompositionLocal`로 내리고 "Define a single app theme composable as the entry point for your design system".
- → R-18-06·R-18-10·R-18-11의 근거.

### XML 테마 → Compose 테마 (S158)
- URL: https://developer.android.com/develop/ui/compose/designsystems/views-to-compose
- 이행 기간에는 XML 테마가 Activity/윈도우 배경·스플래시 때문에 남아 "two sources of truth"가 된다. "Don't hardcode any migrated values." 색은 "name them by their semantic role".
- → R-18-11·R-18-12의 근거. 다만 이 문서는 **기존 View 앱의 이행 가이드**이고 그린필드 문서가 아니다(아래 판단 갈린 지점 참조).

### 윈도우 크기 클래스 (S153)
- URL: https://developer.android.com/develop/ui/compose/layouts/adaptive/use-window-size-classes
- `currentWindowAdaptiveInfo().windowSizeClass`. large·xlarge는 `supportLargeAndXLargeWidth = true`.
- 브레이크포인트: compact <600dp / medium 600~840 / expanded 840~1200 / large 1200~1600 / xlarge ≥1600.
- "Window size classes should be computed at the top level and passed down to nested composables, not measured by individual composables themselves."
- → R-18-13의 근거.

### 런타임 구성 변경 (S154) / UI 상태 저장 (S155)
- URL: https://developer.android.com/guide/topics/resources/runtime-changes , https://developer.android.com/topic/libraries/architecture/saving-states
- "Don't opt out of `Activity` recreation as a shortcut to avoid state loss … It is impossible to entirely disable `Activity` recreation."
- 생존 표: ViewModel = 구성 변경 O / 시스템 프로세스 사망 X. Saved state = 둘 다 O, 단 "only for primitive types and simple, small objects such as `String`". 영속 저장소 = 전부 O.
- → R-18-14의 근거. Nav3 백스택 복원은 기존 S22를 재사용했다.

### `<activity>` 매니페스트 요소 (S156)
- URL: https://developer.android.com/guide/topics/manifest/activity-element
- `android:exported` 기본값은 인텐트 필터가 없을 때 `false`, 런처처럼 인텐트 필터가 있으면 `true`로 적어야 하며 API 31부터 선언이 필수.
- "The modes `singleTask`, `singleInstance`, and `singleInstancePerTask` are not appropriate for most applications." `standard`가 기본이자 대부분에 적합.
- → R-18-05의 근거.

## 기존 규칙과의 경계(중복을 피한 지점)

| 주제 | 이미 소유한 규칙 | 18에서 한 일 |
|---|---|---|
| Hilt 진입점 개수(`@HiltAndroidApp` + 루트 Activity 1개) | R-14-03 | 인용만. 18은 그 Application이 **무엇을 하면 안 되는지**만 정함 |
| 필드 주입 금지 | R-14-02 | 건드리지 않음 |
| 백스택 소유·조작 | R-13-03 | 인용만. 18은 `setContent` 안의 호출 형태만 정함 |
| `entryDecorators` 순서 | R-13-05 | 인용 없음(겹치지 않음) |
| 디자인 시스템 컴포넌트 우선 사용 | R-17-14 | 인용만. 18은 **테마 파일 구성과 진입점**만 정함 |
| 다크·폰트 배율 프리뷰 | R-17-02 | 인용만 |
| `rememberSaveable` 사용 판단 | R-17-12 | 인용만. 18은 재생성 회피 금지(`configChanges`)만 정함 |
| 상태 호이스팅 위치 | R-17-03 | R-18-13 근거에서 인용 |
| `android:exported` 기본값 false | R-40-07 | 런처 Activity가 유일한 예외임을 명시(값 자체는 R-40-07 소유) |
| 시작 시간 임계값·회귀 판정 | R-40-02 | 인용만. 18은 임계값을 다시 정하지 않음 |
| 모듈 유형·`:core:designsystem` 존재 | R-10-01, R-10-04 | 인용 없이 배치 대상으로만 사용 |

## 판단이 갈린 지점

1. **App Startup vs `Application.onCreate`** — 출처는 어느 쪽을 기본값으로 쓰라고 말하지 않는다. S150은 "여러 ContentProvider를 하나로 합친다"는 목적만, S36은 "콘텐츠 프로바이더로 초기화한다면 App Startup을 고려하라"만 말한다. 그래서 규칙 대신 **결정 매트릭스**로 두고, 두 선택지 모두 아닌 경우(=지연)를 기본값으로 삼았다. 매트릭스의 "첫 프레임 전에 반드시 끝나야 하나" 행은 출처 문장이 아니라 위 두 문장에서 끌어낸 이 팩의 판단이다.
2. **Hilt와 App Startup의 실행 순서** — `InitializationProvider`는 ContentProvider라 `Application.onCreate`와의 선후가 문제가 될 수 있는데, 두 공식 문서 어디에도 Hilt 그래프 접근 가능 시점에 대한 문장이 없었다. 근거 없는 주장을 만들지 않으려고 **규칙으로 쓰지 않았다**. Initializer 안에서 Hilt 주입 객체를 쓰려는 설계가 나오면 그때 실빌드로 확인해야 한다.
3. **`Shape.kt` 분리** — S158이 보여 주는 공식 산출물은 `Color.kt`·`Theme.kt` 두 개다. `Type.kt`·`Shape.kt`까지 4파일로 나눈 것은 같은 기준(서브시스템별 분리)을 확장한 이 팩의 결정이며, R-18-10 근거 줄에 그 사실을 적었다.
4. **XML 테마 출처의 성격** — S165는 그린필드가 아니라 **View 앱 이행 가이드**다. "XML 테마가 남는다"는 서술도 이행 기간을 전제한다. 그린필드에서도 매니페스트 `android:theme`와 스플래시 때문에 XML 테마가 필요하다는 사실은 S145(`postSplashScreenTheme` required)로 독립 확인되므로, R-18-12는 두 출처를 함께 인용했다.
5. **`Theme.App`의 parent** — `Theme.Material3.DayNight.NoActionBar` 같은 Material Components XML 테마를 쓰라는 공식 문장을 확인하지 못했고, 그 테마는 별도 의존성(`com.google.android.material`)을 요구한다. Compose 전용 그린필드에 불필요한 의존성을 끌어들이지 않으려고 예시는 플랫폼 테마(`android:Theme.Material.NoActionBar`)로 적었다. 규칙 본문은 parent를 강제하지 않는다.
6. **시스템 바 보호의 배치** — S150 예시는 `Spacer`를 콘텐츠 **뒤에** 그려 위로 덮는다. R-18-09 예시도 같은 순서로 적었지만, 이 형태는 "루트에서 인셋을 소비한다"(R-18-08)와 겹쳐 보일 수 있다. 둘은 다른 일이다 — 08은 콘텐츠가 가려지지 않게 밀어내는 것이고, 09는 밀어내지 않고 지나가게 둔 영역의 대비를 만드는 것이다.
7. **`WindowSizeClass`의 large/xlarge** — `supportLargeAndXLargeWidth = true` 파라미터는 S160에 있으나 기본값을 어느 쪽으로 둘지는 앱의 대상 폼팩터에 달려 규칙으로 고정하지 않았다. R-18-13 예시는 기본 호출만 보여 준다.
8. **`android:windowSoftInputMode="adjustResize"`** — S153이 요구하는 것은 확인했지만, Compose 전용 앱에서 이 값이 IME 인셋에 어떻게 반영되는지 버전별 차이는 확인하지 못했다. 규칙은 "적는다"까지만 정했다.

## 확인하지 못한 것 / 규칙으로 만들지 않은 것

- **`ProcessLifecycleOwner`·`Application`의 `ActivityLifecycleCallbacks` 등록 기준** — 공식 문서에서 "앱 셸이 이것을 해야 한다"는 규범 문장을 찾지 못해 규칙으로 만들지 않았다.
- **StrictMode를 debug에서 켜는 관행** — 널리 쓰이지만 이번에 연 문서 중 이를 규범으로 적은 것이 없었다. 빌드 변이 설정이라 19(빌드 설정) 소관이기도 하다.
- **Baseline Profile / `profileinstaller`** — 시작 성능 주제지만 이미 R-40-03이 소유한다. 중복해서 쓰지 않았다.
- **`Activity`의 `onNewIntent`·딥링크 진입** — Nav3 1.1.7 딥링크는 R-13-08이 소유하므로 18에서 다루지 않았다.
- **화면 회전 제한(`screenOrientation`)** — S161이 "Don't restrict orientation/resizability"라고 말하지만, 대형 화면 대응 정책 전반은 `adaptive` 스킬과 13/17의 범위와 겹쳐 18에서는 규칙으로 세우지 않았다.
- 각 페이지의 최종 갱신일은 확보하지 못했다(본문만 확인).

## 신규 출처 표

90-sources.md 병합용. 대역 S143~S169 중 S143~S165를 썼다.

| S143 | Google | App Startup 라이브러리 | https://developer.android.com/topic/libraries/app-startup | 공식 가이드 |
| S144 | Google | 스플래시 화면 | https://developer.android.com/develop/ui/views/launch/splash-screen | 공식 가이드 |
| S145 | Google | 스플래시 화면 API로 마이그레이션 | https://developer.android.com/develop/ui/views/launch/splash-screen/migrate | 공식 가이드 |
| S146 | Google | Compose edge-to-edge 설정 | https://developer.android.com/develop/ui/compose/system/setup-e2e | 공식 가이드 |
| S147 | Google | Android 15 동작 변경(타깃 앱) | https://developer.android.com/about/versions/15/behavior-changes-15 | 공식 문서 |
| S148 | Google | Android 16 동작 변경(타깃 앱) | https://developer.android.com/about/versions/16/behavior-changes-16 | 공식 문서 |
| S149 | Google | Compose 윈도우 인셋 설정 | https://developer.android.com/develop/ui/compose/system/insets-ui | 공식 가이드 |
| S150 | Google | Compose 시스템 바 보호 | https://developer.android.com/develop/ui/compose/system/system-bars | 공식 가이드 |
| S151 | Google | Compose의 Material Design 3 | https://developer.android.com/develop/ui/compose/designsystems/material3 | 공식 가이드 |
| S152 | Google | Compose 커스텀 디자인 시스템 | https://developer.android.com/develop/ui/compose/designsystems/custom | 공식 가이드 |
| S153 | Google | 윈도우 크기 클래스 사용 | https://developer.android.com/develop/ui/compose/layouts/adaptive/use-window-size-classes | 공식 가이드 |
| S154 | Google | 런타임 구성 변경 처리 | https://developer.android.com/guide/topics/resources/runtime-changes | 공식 가이드 |
| S155 | Google | UI 상태 저장 | https://developer.android.com/topic/libraries/architecture/saving-states | 공식 가이드 |
| S156 | Google | 매니페스트 `<activity>` 요소 | https://developer.android.com/guide/topics/manifest/activity-element | 공식 문서 |
| S157 | Google | androidx.core 릴리스 노트(core-splashscreen 1.2.0) | https://developer.android.com/jetpack/androidx/releases/core | 릴리스 노트 |
| S158 | Google | XML 테마를 Compose 테마로 마이그레이션 | https://developer.android.com/develop/ui/compose/designsystems/views-to-compose | 공식 가이드 |

재사용한 기존 출처: S01(앱 아키텍처), S05(Hilt), S08(UI 계층), S12(상태 호이스팅), S21(Nav3 basics), S22(Nav3 save-state), S36(앱 시작 시간), S52(NiA 모듈화).
| S159 | Google | Material Components and layouts — insets (Scaffold) | https://developer.android.com/develop/ui/compose/system/material-insets | 공식 가이드 |
| S160 | Google | Compose Material 3 Adaptive 릴리스 노트 | https://developer.android.com/jetpack/androidx/releases/compose-material3-adaptive | 공식 문서 |

2026-09-16 astra 리뷰 반영: S159(`Scaffold`는 인셋을 소비하지 않음 → `consumeWindowInsets(innerPadding)`)·S160(1.3.0-alpha10 `currentWindowAdaptiveInfo` deprecated → V2; 소스 확인 `currentWindowAdaptiveInfoV2(): WindowAdaptiveInfo`, 구 함수 `ReplaceWith` WARNING) 추가. R-18-10·R-18-13의 직접 인용 2건이 원문에 없어 요약으로 교체하고 "진입점 하나"·"루트에서 한 번"은 팩 결정으로 표기. R-18-07에 실행 OS 조건(Android 15 기기에서는 opt-out 유효) 추가. R-18-10 공개 범위를 `theme/` 패키지로 좁혀 R-17-14(공통 컴포넌트 공개)와의 충돌 해소.
