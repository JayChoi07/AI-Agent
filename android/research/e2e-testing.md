# 전체 플로우(E2E)·계측 테스트의 도구와 실행 환경 조사 노트
조사일: 2026-09-30

이 노트는 규칙 문장을 쓰지 않는다. 규칙을 쓸 때 인용할 출처와, 출처가 정하지 않아 팩이 골라야 하는 지점을 정리한다.

표기 규칙
- 큰따옴표 안의 영어 문장은 해당 URL 원문을 내려받아 글자 그대로 대조한 것이다(줄바꿈만 이어 붙임).
- "요약"이라고 적은 것은 원문을 읽고 옮긴 것이며 인용이 아니다.
- "관찰"은 저장소 파일·API 응답을 직접 읽은 사실이다.
- "조사자 평가"는 출처가 아니라 조사자의 판단이다. 규칙 근거로 쓰지 않는다.
- 버전과 날짜는 전부 2026-09-30에 해당 페이지에서 확인한 값이다.

## 출처
| # | 조직 | 문서명 | URL | 종류 | 최종 갱신/버전 |
|---|---|---|---|---|---|
| S1 | Google | Test your Compose layout | https://developer.android.com/develop/ui/compose/testing | 공식 가이드 | 2026-09-22 |
| S2 | Google | Compose 테스트 — Interoperability | https://developer.android.com/develop/ui/compose/testing/interoperability | 공식 가이드 | 2026-09-22 |
| S3 | Google | Compose 테스트 — Synchronization | https://developer.android.com/develop/ui/compose/testing/synchronization | 공식 가이드 | 2026-09-22 |
| S4 | Google | Configure the test environment with v2 testing APIs | https://developer.android.com/develop/ui/compose/testing/migrate-v2 | 공식 가이드 | 2026-09-23 |
| S5 | Google | Espresso / Espresso setup instructions | https://developer.android.com/training/testing/espresso , https://developer.android.com/training/testing/espresso/setup | 공식 가이드 | 2026-03-05 / 2026-09-16 |
| S6 | Google | Write automated tests with UI Automator | https://developer.android.com/training/testing/other-components/ui-automator | 공식 가이드 | 2026-09-21 |
| S7 | Google | Test Uiautomator 릴리스 노트 | https://developer.android.com/jetpack/androidx/releases/test-uiautomator | 릴리스 노트 | 2.4.0 (2026-07-01) |
| S8 | Google | Test(androidx.test) 릴리스 노트 | https://developer.android.com/jetpack/androidx/releases/test | 릴리스 노트 | 페이지 갱신 2026-01-14 |
| S9 | Google | Testing strategies | https://developer.android.com/training/testing/fundamentals/strategies | 공식 가이드 | 2026-09-01 |
| S10 | Google | Big test stability | https://developer.android.com/training/testing/instrumented-tests/stability | 공식 가이드 | 2026-03-05 |
| S11 | Google | Build instrumented tests | https://developer.android.com/training/testing/instrumented-tests | 공식 가이드 | 2026-03-05 |
| S12 | Google | AndroidJUnitRunner | https://developer.android.com/training/testing/instrumented-tests/androidx-test-libraries/runner | 공식 가이드 | 2026-03-05 |
| S13 | Google | Advanced test setup | https://developer.android.com/studio/test/advanced-test-setup | 공식 가이드 | 2026-06-23 |
| S14 | Google | Scale your tests with build-managed devices | https://developer.android.com/studio/test/gradle-managed-devices | 공식 가이드 | 2026-01-16 |
| S15 | Google | AGP DSL 레퍼런스 9.4 — TestOptions / ManagedDevices / ManagedVirtualDevice / ApplicationBuildType / ApplicationExtension | https://developer.android.com/reference/tools/gradle-api/9.4/com/android/build/api/dsl/TestOptions , …/ManagedDevices , …/ManagedVirtualDevice , …/ApplicationBuildType , …/ApplicationExtension | API 레퍼런스 | 2026-09-18 / 2026-06-18 |
| S16 | Google | Hilt testing guide | https://developer.android.com/training/dependency-injection/hilt-testing | 공식 가이드 | 2026-09-16 |
| S17 | Google(Dagger) | Hilt — Gradle Build Setup / Testing / Instrumentation testing / Testing Philosophy, Dagger KSP | https://dagger.dev/hilt/gradle-setup.html , https://dagger.dev/hilt/testing.html , https://dagger.dev/hilt/instrumentation-testing.html , https://dagger.dev/hilt/testing-philosophy.html , https://dagger.dev/dev-guide/ksp.html | 공식 문서 | 예제 버전 2.60.1 |
| S18 | Google | Continuous Integration basics / CI features / Types of CI automation | https://developer.android.com/training/testing/continuous-integration , …/continuous-integration/features , …/continuous-integration/automation | 공식 가이드 | 2026-03-05 |
| S19 | Google | 앱 최적화 — Enable app optimization / Test the optimization | https://developer.android.com/topic/performance/app-optimization/enable-app-optimization , https://developer.android.com/topic/performance/app-optimization/test-the-optimization | 공식 가이드 | 2026-08-27 / 2026-09-21 |
| S20 | Google | Write a Macrobenchmark / Benchmark in Continuous Integration / Benchmark 릴리스 노트 | https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview , https://developer.android.com/topic/performance/benchmarking/benchmarking-in-ci , https://developer.android.com/jetpack/androidx/releases/benchmark | 공식 가이드·릴리스 노트 | 2026-09-21 / 1.5.0 (2026-09-09) |
| S21 | Google | Create Baseline Profiles / Configure Baseline Profile generation | https://developer.android.com/topic/performance/baselineprofiles/create-baselineprofile , https://developer.android.com/topic/performance/baselineprofiles/configure-baselineprofiles | 공식 가이드 | 2026-09-21 / 2026-05-19 |
| S22 | Google | Now in Android — `.github/workflows/Build.yaml`, `NightlyBaselineProfiles.yaml` | https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/Build.yaml , https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/NightlyBaselineProfiles.yaml | 오픈소스 코드 | main. Build.yaml 최종 커밋 5ba16ed (2026-01-30) |
| S23 | Google | Now in Android — `gradle/libs.versions.toml`, `build-logic/.../GradleManagedDevices.kt`, `AndroidApplicationConventionPlugin.kt`, `AndroidLibraryConventionPlugin.kt`, `HiltConventionPlugin.kt`, `app/build.gradle.kts`, `benchmarks/build.gradle.kts`, `core/testing/.../NiaTestRunner.kt`, `app/src/androidTest/.../NavigationTest.kt`, `README.md` | https://github.com/android/nowinandroid (위 경로를 raw.githubusercontent.com 의 main 에서 직접 받음) | 오픈소스 코드 | main (저장소 최종 커밋 2026-09-22) |
| S24 | Google | architecture-samples — `app/build.gradle.kts` | https://raw.githubusercontent.com/android/architecture-samples/main/app/build.gradle.kts | 오픈소스 코드 | main (저장소 최종 커밋 2025-07-17) |
| S25 | mobile.dev | Maestro README / Releases / CHANGELOG | https://raw.githubusercontent.com/mobile-dev-inc/maestro/main/README.md , https://github.com/mobile-dev-inc/maestro/releases , https://raw.githubusercontent.com/mobile-dev-inc/maestro/main/CHANGELOG.md | 오픈소스 README·릴리스 | CLI 2.11.0 (2026-09-29) |
| S26 | mobile.dev | Maestro 문서 — Android / Android Native / Jetpack Compose / How to install Maestro CLI / Maestro solutions / Detect Maestro / CI/CD integration, 가격 페이지 | https://docs.maestro.dev/get-started/supported-platform/android , …/android/android-native , …/android/jetpack , https://docs.maestro.dev/maestro-cli/how-to-install-maestro-cli , https://docs.maestro.dev/get-started/maestro-solutions , https://docs.maestro.dev/maestro-flows/flow-control-and-logic/detect-maestro , https://docs.maestro.dev/maestro-cloud/ci-cd-integration , https://maestro.dev/pricing | 공식 문서(서드파티) | 미표기 |
| S27 | ReactiveCircus | android-emulator-runner README / Releases | https://raw.githubusercontent.com/ReactiveCircus/android-emulator-runner/main/README.md , https://github.com/ReactiveCircus/android-emulator-runner/releases | 오픈소스 README·릴리스 | v2.38.0 (2026-07-05) |
| S28 | GitHub | GitHub-hosted runners reference | https://docs.github.com/en/actions/reference/runners/github-hosted-runners | 공식 문서 | 미표기 |
| S29 | GitHub | Changelog — Hardware accelerated Android virtualization (2024-04-02 / 2023-02-23) | https://github.blog/changelog/2024-04-02-github-actions-hardware-accelerated-android-virtualization-now-available/ , https://github.blog/changelog/2023-02-23-hardware-accelerated-android-virtualization-on-actions-windows-and-linux-larger-hosted-runners/ | 공식 변경 기록 | 2024-04-02 / 2023-02-23 |
| S30 | GitHub | GitHub Actions billing / Actions runner pricing | https://docs.github.com/en/billing/concepts/product-billing/github-actions , https://docs.github.com/en/billing/reference/actions-runner-pricing | 공식 문서 | 미표기 |
| S31 | GitHub | Self-hosted runners(개념·레퍼런스) / Secure use reference | https://docs.github.com/en/actions/concepts/runners/self-hosted-runners , https://docs.github.com/en/actions/reference/runners/self-hosted-runners , https://docs.github.com/en/actions/reference/security/secure-use | 공식 문서 | 미표기 |
| S32 | Google | Firebase Test Lab — Usage levels, quotas, and pricing | https://firebase.google.com/docs/test-lab/usage-quotas-pricing | 공식 문서 | 2026-09-24 |
| S33 | Google | Android SDK 저장소 색인 — 시스템 이미지 목록(aosp_atd / google_atd / android / google_apis) | https://dl.google.com/android/repository/sys-img/aosp_atd/sys-img2-4.xml , …/google_atd/sys-img2-4.xml , …/android/sys-img2-4.xml , …/google_apis/sys-img2-4.xml | SDK 저장소 메타데이터 | 2026-09-30 조회 |
| S34 | Google | AGP 릴리스 노트(9.4.0 / 9.0.0) | https://developer.android.com/build/releases/gradle-plugin , https://developer.android.com/build/releases/agp-9-0-0-release-notes | 릴리스 노트 | 9.4.0 (2026년 9월), 페이지 갱신 2026-09-24 |
| S35 | Google | Compose UI 릴리스 노트 / BOM 매핑 | https://developer.android.com/jetpack/androidx/releases/compose-ui , https://developer.android.com/develop/ui/compose/bom/bom-mapping | 릴리스 노트 | ui 1.12.1 (2026-09-09), BOM 목록 최상단 2026.09.00 |
| S36 | Sonatype | Maven Central — `com.google.dagger` 아티팩트 메타데이터 | https://repo1.maven.org/maven2/com/google/dagger/hilt-android-testing/maven-metadata.xml | 저장소 메타데이터 | release 2.60.1 |

## 핵심 내용 (출처별)

### 질문 1. 도구 후보

#### (a) Compose UI Test + AndroidJUnitRunner
- 의존성과 규칙 생성 함수: "This module includes a ComposeTestRule and an implementation for Android called AndroidComposeTestRule." 규칙은 "either createComposeRule or, if you need access to an activity, createAndroidComposeRule" 로 만든다. `ui-test-junit4` 는 `androidTestImplementation`, `ui-test-manifest` 는 `debugImplementation`. [S1]
- 동기화: "Compose tests are synchronized by default with your UI." 가상 시계로 진행하므로 실시간으로 돌지 않는다(요약). [S3]
- 러너: "The AndroidJUnitRunner class is a JUnit test runner that lets you run instrumented JUnit 4 tests on Android devices, including those using the Espresso, UI Automator, and Compose testing frameworks." [S12]
- 공식 권고: "Note: Many third-party testing frameworks use UI Automator to run tests, so the same principles apply. Prefer Espresso and Compose Test APIs to create UI tests." [S10]
- 큰 테스트 작성 방식: "To create big tests using Compose or Espresso, you typically start one of your activities and navigate as a user would, verifying that the UI behaves correctly using assertions or screenshot tests." [S10]
- 5계층 표의 예시에서 "Critical user journey: Signing in" 항목의 테스트 종류가 "End-to-end Compose UI behavior test running on device" 로 적혀 있다. [S9]
- 버전: `androidx.compose.ui:ui-test-junit4` 는 Compose BOM이 관리한다. Compose UI 최신 안정은 1.12.1(2026-09-09), 알파는 1.13.0-alpha03. BOM 매핑 페이지 기본 표시값(BOM 2026.09.00)에서 `ui-test`·`ui-test-junit4`·`ui-test-manifest` 모두 1.12.1. [S35]
- **팩 밖 관찰 — v2 테스트 API**: `androidx.compose.ui.test.junit4.v2.create*ComposeRule` 이 Compose UI 1.11.0-alpha03에서 도입됐다(릴리스 노트: "Introduced androidx.compose.ui.test.junit4.v2.create*ComposeRule APIs."). 가이드 문서는 "The v1 APIs are deprecated, and it's strongly recommended to migrate to the new APIs." 라고 하면서 같은 페이지 상단에 "Note: The v2 testing APIs are in alpha and are subject to change." 라고 적는다. R-30-11(`createComposeRule` 기본)에 영향이 있을 수 있어 기록한다. [S4][S35]

#### (b) Maestro
- 정의: "**Maestro** is an open-source framework that makes UI and end-to-end testing for Android, iOS, and web apps simple and fast." 라이선스 Apache 2.0. [S25]
- 최신 안정 버전: CLI 2.11.0 (2026-09-29 게시, prerelease=false). 직전 2.10.0 (2026-08-31). 2.11.0 변경 기록 첫 줄: "Android: support Android 17 (API 37) in `start-device`" 로 시작한다. [S25]
- 작성 언어: YAML. "**Human-readable YAML flows** – express interactions as commands like `launchApp`, `tapOn`, and `assertVisible`." [S25]
- 실행 요건: "Maestro requires Java 17 or higher to be installed on your system." Windows는 GitHub 릴리스의 `maestro.zip` 을 풀어 PATH에 넣는 방식이며, WSL 방식은 문서가 "Use the Windows (WSL) option only if it is strictly necessary." 라고 제한한다. [S25][S26]
- Compose 지원 방식: 앱 프로세스 밖에서 접근성 계층으로 조작한다. "Unlike instrumentation-based tools such as Espresso or Robolectric, which execute inside the app process and depend on framework-specific APIs, Maestro operates externally using Android’s system automation interfaces." 선택자는 보이는 텍스트, `contentDescription`, 리소스 ID. [S26]
- 앱 내부 상태 접근: 없음(블랙박스). "**Zero Instrumentation**: You don't need to add test dependencies to your `build.gradle` or compile a special \"test APK.\"" 앱은 이미 설치돼 있어야 한다 — "**Installation**: Maestro assumes the application is already installed on the target device/emulator before the test begins." [S26]
- 테스트 전용 동작이 필요하면 `launchApp` 의 `arguments` 로 플래그를 넘기고 앱 코드가 그 값을 읽어 분기하는 방식을 안내한다(요약). 즉 가짜 데이터 계층으로 바꾸려면 프로덕션 코드에 분기가 들어간다. [S26]
- CI: 문서의 "CI/CD integration" 절은 전부 Maestro Cloud용이다(GitHub Actions 항목 설명이 "Official GitHub Action for Maestro Cloud."). 오픈소스 CLI를 GitHub Actions의 에뮬레이터에서 돌리는 공식 안내는 찾지 못했다 → "출처가 침묵하는 것" 참조. [S26]
- 비용: 로컬 실행은 무료("Test for free on your own devices with Maestro Studio or the CLI."), Cloud는 "$250/device/mo". [S26]
- 로컬 실행은 한 번에 기기 1대, 순차 실행이다(문서 표: Local Android — "1 device at a time", "Slow (Sequential)"). [S26]

#### (c) UI Automator
- 최신 안정 버전: 2.4.0 (2026-07-01). 같은 날짜로 `uiautomator-shell`, `uiautomator-shell-android` 도 2.4.0. [S7]
- 2.4의 변경: "The API surface of UiAutomator has been overhauled to provide:" 아래에 `uiAutomator` 테스트 스코프, `onElement`/`onElements`/`onElementOrNull`, 내장 대기(`onElement*(timeoutMs: Long = 10000)`), `waitForStable`/`waitForAppToBeVisible`, 다중 창, 스크린샷과 `ResultsReporter` 가 나열된다. [S7]
- Compose 지원: 2.4.0-alpha04 릴리스 노트 "Renamed onView to onElement to clarify it works with compose (I53a3b, b/419006806)". Compose의 `Modifier.testTag` 를 UI Automator에서 찾으려면 `testTagsAsResourceId` 를 켜야 한다 — "If you want to access any composable that uses Modifier.testTag, you need to enable the semantic property testTagsAsResourceId for the particular composable's subtree." [S7][S2]
- 공식 문서가 말하는 용도: "UI Automator lets you test an app from outside of the app's process. This lets you test release versions with minification applied. UI Automator also helps when writing macrobenchmark tests." [S6]
- 신뢰성에 대한 공식 평가: "However, UI Automator tests might require more manual synchronization so they tend to be less reliable." [S10]
- 문서 상태: 가이드 상단 "The API is under development, and we strongly recommend using it for any new development with UI Automator." 가이드의 의존성 예제는 아직 `2.4.0-alpha05` 로 적혀 있다(릴리스 노트의 안정 2.4.0과 어긋남 — 문서 갱신 지연으로 보이며 버전은 릴리스 노트를 따를 것). [S6][S7]
- 작성 언어: Kotlin(DSL). 레거시 API는 Java/Kotlin. [S6]

#### (d) Espresso
- 최신 안정 버전: 3.7.0 (2025-07-30). [S8]
- 대상: View. Compose 문서의 표현은 "Espresso: While intended for View-based UIs, Espresso knowledge can still be helpful for some aspects of Compose testing." [S1]
- Compose와 혼용: "You match views with Espresso's onView, and Compose elements with the ComposeTestRule." [S2]
- 대상 사용자: "While it can be used for black-box testing, Espresso’s full power is unlocked by those who are familiar with the codebase under test." [S5]
- 관찰: NiA의 `NavigationTest` 는 Compose 규칙을 쓰면서 뒤로 가기에만 `androidx.test.espresso.Espresso` 를 import한다. [S23]

### 질문 2. androidx.test 안정 버전
릴리스 노트 상단 표와 각 버전 절의 날짜를 직접 확인했다. 페이지 표기: "This library was last updated on: January 14, 2026". [S8]

| 아티팩트 | 최신 안정 | 안정 릴리스 날짜 | 최신 사전 릴리스 |
|---|---|---|---|
| `androidx.test:core` (`core-ktx`) | 1.7.0 | 2025-07-30 | 1.7.0-rc01 |
| `androidx.test:runner` | 1.7.0 | 2025-07-30 | 1.7.0-rc01 |
| `androidx.test:rules` | 1.7.0 | 2025-07-30 | 1.7.0-rc01 |
| `androidx.test.ext:junit` (`junit-ktx`) | 1.3.0 | 2025-07-30 | 1.3.0-rc01 |
| `androidx.test.ext:truth` | 1.7.0 | 2025-07-30 | 1.7.0-rc01 |
| `androidx.test:orchestrator` | 1.6.1 | 2025-07-31 | 1.6.0-rc01 |
| `androidx.test.espresso:espresso-core` | 3.7.0 | 2025-07-30 | 3.7.0-rc01 |
| `androidx.test:monitor` | 1.8.0 | 표의 값(절 날짜 미대조) | 1.9.0-alpha01 (2026-01-14) |
| `androidx.test.services:test-services` | 1.6.0 | 표의 값(절 날짜 미대조) | 1.6.0-rc01 |
| `androidx.test.uiautomator:uiautomator` | 2.4.0 | 2026-07-01 | 없음 [S7] |
| `androidx.benchmark:benchmark-macro-junit4` | 1.5.0 | 2026-09-09 | 없음 [S20] |

- 기존 노트(testing-ci.md)의 보류 항목 "androidx.test 안정 버전 미확정"은 이 표로 해소된다. NiA 카탈로그는 여전히 `androidxTestCore = "1.7.0-rc01"`, `androidxTestExt = "1.3.0-rc01"`, `androidxTestRules = "1.7.0-rc01"`, `androidxTestRunner = "1.7.0-rc01"`, `androidxEspresso = "3.6.1"`, `androidxUiAutomator = "2.3.0"`, `androidxMacroBenchmark = "1.5.0-alpha01"` 이다 — NiA 값을 복사하지 말고 릴리스 노트 값을 쓸 것. [S23][S8]
- 릴리스 노트의 의존성 예제는 오케스트레이터를 `androidTestUtil "androidx.test:orchestrator:1.6.1"` 로 선언한다. [S8]

### 질문 3. Hilt 계측 테스트
- 의존성(KSP): Android 문서 예제는 `androidTestImplementation("com.google.dagger:hilt-android-testing:2.57.1")` + `kspAndroidTest("com.google.dagger:hilt-android-compiler:2.57.1")`. Dagger 문서 예제는 `androidTestImplementation 'com.google.dagger:hilt-android-testing:2.60.1'` + `kspAndroidTest 'com.google.dagger:hilt-compiler:2.60.1'`. 컴파일러 아티팩트 이름이 두 문서에서 다르다(`hilt-android-compiler` 대 `hilt-compiler`). 두 아티팩트 모두 2.60.1 POM이 Maven Central에 존재한다(관찰). [S16][S17][S36]
- KSP 안정성: "Note: Dagger’s KSP support is stable as of Dagger 2.60+ and KSP 2.3.9+." [S17]
- `androidx.hilt:hilt-compiler` 도 KSP로 옮겨야 한다: "KSP support for androidx.hilt:hilt-compiler is available in version 1.4.x." Android 문서도 "you must also add their annotation processors to your test dependencies" 라고 적는다. [S17][S16]
- `@HiltAndroidTest` / `HiltAndroidRule`: "You must annotate any UI test that uses Hilt with @HiltAndroidTest. This annotation is responsible for generating the Hilt components for each test." [S16]
- 테스트 애플리케이션: "You must execute instrumented tests that use Hilt in an Application object that supports Hilt. The library provides HiltTestApplication for use in tests." [S16]
- 커스텀 러너: "To use the Hilt test application in instrumented tests, you need to configure a new test runner." `AndroidJUnitRunner` 를 상속해 `newApplication` 에서 `HiltTestApplication::class.java.name` 을 넘기고, `testInstrumentationRunner` 에 전체 클래스 경로를 적는다. [S16][S17]
- `@TestInstallIn`: "A Dagger module annotated with @TestInstallIn allows users to replace an existing @InstallIn module for all tests in a given source set." Android 문서의 권고: "The recommendation is to use @TestInstallIn whenever possible." [S17][S16]
- `@UninstallModules`·`@BindValue` 의 비용: "Test classes that use @UninstallModules, @BindValue, or nested @InstallIn modules result in a custom component being generated for that test. While this may be fine in most cases, it does have an impact on build speed." [S17]
- 여러 모듈이 쓰는 교체 모듈의 위치: "if a particular @TestInstallIn module is needed in multiple Gradle modules, we recommend putting it in its own Gradle module (usually the same one as the fake)". [S17]
- 규칙 순서: "make sure HiltAndroidRule runs first. Declare the execution order with the order attribute on @Rule". [S16]
- Compose 호스트 액티비티: Android 문서는 `createAndroidComposeRule<HiltTestActivity>()` 를 쓰고 "you must create an empty activity named HiltTestActivity in your androidTest source set and annotate it with @AndroidEntryPoint" 라고 안내한다. [S16]
- Hilt의 테스트 관점: 의존성은 "Use the real code for a dependency", "Use a standard fake provided by the library", "Use a mock as a last resort" 순으로 고른다. [S17]
- 관찰 — NiA: `NiaTestRunner` 가 `HiltTestApplication` 을 넘기고, `app/build.gradle.kts` 의 `testInstrumentationRunner` 가 그 러너를 가리킨다. 전체 플로우 테스트 `NavigationTest` 는 `@HiltAndroidTest` + `HiltAndroidRule(order = 0)` + `createAndroidComposeRule<MainActivity>()(order = 2)` 조합이며 저장소를 `@Inject` 로 받는다. [S23]
- 관찰 — NiA에는 `kspAndroidTest` 선언이 없다(GitHub 코드 검색 0건, `app/build.gradle.kts` 에는 `ksp(libs.hilt.compiler)` 와 `kspTest(libs.hilt.compiler)` 만 있음). architecture-samples에는 `kspAndroidTest(libs.hilt.compiler)` 가 있다. 공식 문서 두 곳이 모두 `kspAndroidTest` 를 요구하므로 팩은 문서를 따르고, NiA가 없이도 도는 이유는 "미확인"에 남긴다. [S23][S24]

### 질문 4. 실행 환경

#### (a) Gradle Managed Devices (문서 제목은 "build-managed devices")
- 지원 범위: "This feature, available for API levels 27 and higher, lets you configure virtual or remote physical test devices in your project's Gradle files." → **minSdk 26 기기는 이 방식으로 만들 수 없다.** [S14]
- 하는 일: "The Android Gradle plugin uses the configurations to fully manage—that is, create, deploy, and tear down—those devices when executing your automated tests." [S14]
- DSL(가이드 예제): `android { testOptions { managedDevices { localDevices { create("pixel2api30") { device = "Pixel 2"; apiLevel = 30; systemImageSource = "aosp" } } } } }`. [S14]
- DSL(AGP 9.4 레퍼런스): `ManagedDevices` 의 공개 프로퍼티는 `allDevices`, `groups`, `localDevices` 세 개뿐이다. `ManagedVirtualDevice` 의 `apiLevel` 설명은 "This annotation is deprecated, use sdkVersion instead." 이고 `sdkVersion` 은 8.9.0에 추가됐다. `systemImageSource` 는 "Either \"google\", \"google-atd\", \"aosp\", or \"aosp-atd\"." 이며 기본값은 "google" 이다. API 전체에 "These APIs are experimental and may change without notice." 가 붙어 있고 `managedDevices` 는 `@Incubating` 이다. [S15]
- **문서 간 불일치**: 가이드의 그룹 예제는 `targetDevices.add(devices["pixel2api29"])`, Baseline Profile 가이드는 `testOptions.managedDevices.devices { … }` 를 쓰지만, AGP 9.4 레퍼런스에는 `devices` 프로퍼티가 없다. NiA는 `allDevices { … }` 와 `localDevices[...]` 를 쓴다. AGP 9.4에서는 `localDevices`/`allDevices` 를 쓰고 실빌드로 확인해야 한다. [S14][S21][S15][S23]
- 태스크 이름: 기기 하나는 `./gradlew device-nameBuildVariantAndroidTest` (예: `pixel2api30DebugAndroidTest`), 그룹은 `./gradlew group-nameGroupBuildVariantAndroidTest`. NiA는 설정 태스크 `:benchmarks:pixel6Api33Setup` 도 호출한다. [S14][S10][S22]
- CI에서의 필수 플래그: "Note: When using build-managed devices on servers that don't support hardware rendering, such as GitHub Actions, you need to specify the following flag: -Pandroid.testoptions.manageddevices.emulator.gpu=swiftshader_indirect." [S14]
- ATD: "Automated Test Device (ATD), which is optimized to reduce CPU and memory resources when running your instrumented tests". 제약: "Warning: Screenshot tests that depend on hardware rendering currently aren't supported when using ATDs." 가이드 예제 주석은 "ATDs currently support only API level 30." 이라고 적는다. [S14]
- **가이드와 SDK 저장소의 불일치(관찰)**: SDK 저장소 색인에는 `aosp_atd` 와 `google_atd` 이미지가 API 30, 31, 32, 33, 34, 35, 36에 대해 올라와 있다(x86_64·arm64-v8a, API 30은 x86 포함). API 37용 ATD는 없다. 일반 `default`(aosp) 이미지는 API 36까지, `google_apis` 는 37.0까지 있다. [S33]
- 안정성 기능: "Gradle-managed devices contain mechanisms to retry in the event of device disconnections and other improvements." [S10]
- 샤딩: `android.experimental.androidTest.numManagedDeviceShards=<number_of_shards>`. [S14]

#### (b) GitHub Actions에서 에뮬레이터 실행
- GitHub 공식 문서: "GitHub-hosted Linux runners support hardware acceleration for Android SDK tools, which makes running Android tests much faster and consumes fewer minutes." [S28]
- 2-vCPU 러너 지원(2024-04-02): "Available now, Actions users of our 2-vCPU GitHub-hosted Linux runners will be able to make use of hardware acceleration for Android testing. Previously this feature was only available on runners with 4 or more vCPUs." [S29]
- KVM 요건: "To make use of this on Linux, Actions users will need to add the runner user to the KVM user group" — udev 규칙 세 줄(`99-kvm4all.rules` 작성 → `udevadm control --reload-rules` → `udevadm trigger --name-match=kvm`). NiA와 emulator-runner README가 같은 스텝을 쓴다. [S29][S22][S27]
- 속도 수치(2023-02-23): "Testing on a 4-core machine with hardware acceleration is around 2-3 times faster than not using hardware acceleration and around 2 times faster than using MacOS." [S29]
- macOS 러너: arm64 macOS 러너는 "Nested-virtualization is not supported due to the limitation of Apple's Virtualization Framework." [S28]
- 러너 사양: 비공개 저장소의 `ubuntu-latest` 는 2 CPU / 8 GB RAM / 14 GB SSD, 공개 저장소는 4 CPU / 16 GB RAM / 14 GB SSD. [S28]
- `reactivecircus/android-emulator-runner`: 최신 v2.38.0 (2026-07-05), 워크플로에서는 메이저 태그 `@v2` 로 참조. README의 권고: "It is now recommended to use the **Ubuntu** (`ubuntu-latest`) runners which are 2-3 times faster than the **macOS** ones which are also a lot more expensive." [S27]
- 주요 입력값(README 표): `api-level`(필수, "**Minimum API level supported is 15**"), `target`(기본 `default`, `aosp_atd`·`google_atd` 포함), `arch`(기본 `x86`), `disable-animations`(기본 `true`), `emulator-boot-timeout`(기본 `600`), `script`(필수). 기본 에뮬레이터 옵션은 `-no-window -gpu swiftshader_indirect -no-snapshot -noaudio -no-boot-anim`. [S27]
- AVD 스냅숏 캐시: `actions/cache` 로 `~/.android/avd/*` 와 `~/.android/adb*` 를 캐시하고, 스냅숏 생성 스텝과 테스트 스텝을 나누는 예제가 README에 있다. [S27]
- Google 문서도 이 방식을 선택지로 든다: "Most CI systems come with a third-party plugin (also called \"action\", \"integration\" or \"step\") to handle Android emulators." [S18]

#### (c) Firebase Test Lab
- 무료 할당량(Spark): "Spark plan (no-cost): The resource limits are listed for up to 15 test runs per day in total:" — "10 test runs per day on virtual devices", "5 test runs per day on physical devices". [S32]
- Blaze: 무료 구간 "30 minutes of test time per day on physical devices", "60 minutes of test time per day on virtual devices", 초과분은 "$5 per hour for each physical device", "$1 per hour for each virtual device". 과금 단위: "Charges are calculated on a per-minute basis, rounded up to the nearest minute." [S32]
- GMD 연동 플러그인: `id("com.google.firebase.testlab") version "0.0.1-alpha05"` — 알파다. `gradle.properties` 에 `android.experimental.testOptions.managedDevices.customDevice=true` 가 필요하고, 인증은 서비스 계정 JSON을 권한다("We recommend authorizing by passing a service account JSON file to Gradle"). [S14]
- 제약: "Note that Gradle doesn't run tests in parallel or support other Google Cloud CLI configurations for Test Lab devices." [S14]
- 무료 구간 초과 조건: "Note: To access quota beyond the no-cost threshold, your Firebase project must be on the Blaze pricing plan." [S14]
- 오케스트레이터: "Both Android Studio and Firebase Test Lab have Android Test Orchestrator pre-installed". [S12]
- Google의 위치 설정: "Delegate instrumented tests to a device farm such as Firebase Test Lab. Device farms are used for their high reliability and they can run on emulators or physical devices." [S18]

#### (d) 자체 호스팅 러너
- 비용: 자체 호스팅 러너는 "Are free to use with GitHub Actions, but you are responsible for the cost of maintaining your runner machines." 과금 문서도 "GitHub Actions usage is free for self-hosted runners and for public repositories that use standard GitHub-hosted runners." [S31][S30]
- 지원 OS에 Windows 10 64-bit·Windows 11 64-bit가 포함된다. [S31]
- 환경: 작업마다 깨끗한 인스턴스가 아니다("Don't need to have a clean instance for every job execution."). [S31]
- 보안: "As a result, self-hosted runners should almost never be used for public repositories on GitHub, because any user can open pull requests against the repository and compromise the environment." 비공개 저장소도 포크·PR 권한이 있는 사람에 대해 같은 주의를 요구한다. [S31]
- 갱신 의무: 자동 업데이트를 끄면 "you will be required to update your runner version within 30 days of a new version being made available." [S31]

### 질문 5. Now in Android의 실제 구성
`Build.yaml`(main, 최종 커밋 2026-01-30)을 직접 읽은 결과다. [S22]
- 잡은 둘이다: `test_and_apk`(로컬 테스트·린트·APK), `androidTest`(계측). 둘 다 `runs-on: ubuntu-latest`. 트리거는 `workflow_dispatch`, `push`(main), `pull_request` — **계측 잡도 PR마다 돈다.**
- `androidTest` 잡: `timeout-minutes: 55`, 매트릭스 `api-level: [26, 34]`.
- 스텝 순서: `jlumbroso/free-disk-space@v1.3.1`(디스크 확보) → "Enable KVM group perms" → `actions/checkout@v4` → CI용 `gradle.properties` 복사 → `actions/setup-java@v5`(zulu 21) → `gradle/actions/setup-gradle@v4` → `reactivecircus/android-emulator-runner@v2`.
- 에뮬레이터 설정: `arch: x86_64`, `disable-animations: true`, `disk-size: 6000M`, `heap-size: 600M`, `script: ./gradlew connectedDemoDebugAndroidTest --daemon`. `target` 미지정이므로 액션 기본값 `default` 이미지다. [S22][S27]
- **계측 테스트에는 Gradle Managed Devices를 쓰지 않는다.** `connected…AndroidTest` 를 emulator-runner 위에서 돌린다. 매트릭스에 API 26이 있는데 GMD는 API 27 이상만 지원한다. [S22][S14]
- GMD는 정의돼 있고 다른 곳에서 쓴다: 컨벤션 플러그인이 `pixel4api30aospatd`(Pixel 4, API 30, `aosp-atd`), `pixel6api31aosp`(Pixel 6, API 31, `aosp`), `pixelcapi30aospatd`(Pixel C, API 30, `aosp-atd`)를 만들고 `ci` 그룹에 Pixel 4·Pixel C를 넣는다. 실제로 CI에서 GMD를 호출하는 곳은 `NightlyBaselineProfiles.yaml` 하나이며, `:benchmarks:pixel6Api33Setup` 을 `-Pandroid.testoptions.manageddevices.emulator.gpu="swiftshader_indirect"` 와 함께 실행한다(cron `42 4 * * *`, JDK 17). [S23][S22]
- 애니메이션: 컨벤션 플러그인이 앱·라이브러리·기능 모듈 모두에 `testOptions.animationsDisabled = true` 를 넣는다. emulator-runner의 `disable-animations: true` 와 이중으로 건다. [S23][S22]
- 테스트 리포트는 `**/build/reports/androidTests` 를 `actions/upload-artifact@v4` 로 올린다(`if: ${{ !cancelled() }}`). [S22]
- 재시도 설정·Test Orchestrator는 없다(워크플로와 `app/build.gradle.kts` 에 해당 설정 없음). [S22][S23]
- 테스트 대상 변이는 `demoDebug` 하나다. README: "`connectedDemoDebugAndroidTest` run all instrumented tests against the `demoDebug` variant." [S23]
- 테스트 더블: "In tests, **Now in Android** notably does _not_ use any mocking libraries. Instead, the production implementations can be replaced with test doubles using Hilt's testing APIs (or via manual constructor injection for `ViewModel` tests)." [S23]
- 관찰 — 실제 소요 시간(GitHub API, main 브랜치 push 실행): 2026-09-02 실행에서 `androidTest (34)` 5분 52초, `androidTest (26)` 7분 15초. 2026-09-01 실행에서 각각 21분 29초, 20분 43초. 2026-09-22 실행은 `androidTest (26)` 이 실패했다. 공개 저장소(4 CPU 러너) 기준이며 비공개 저장소(2 CPU)에서는 더 느릴 수 있다. [S22][S28]

### 질문 6. 불안정(flaky) 대응
- 주요 대책 세 가지: "Configure devices correctly", "Prevent synchronization issues", "Implement retries". [S10]
- 임의 대기 금지: "Warning: You should avoid pausing your tests for an arbitrary period (sleep) to let the app run and stabilize." [S10]
- 큰 테스트는 조건 대기: "Key Point: Use Idling Resources if needed in small UI tests, and wait-until APIs in bigger UI tests." `ComposeTestRule` 의 `waitUntilAtLeastOneExists`, `waitUntilDoesNotExist`, `waitUntilExactlyOneExists`, `waitUntilNodeCount`, `waitUntil` 이 나열된다. [S10]
- 애니메이션 끄기(기기): "To avoid flakiness, we highly recommend that you turn off system animations on the virtual or physical devices used for testing." 대상은 Window animation scale, Transition animation scale, Animator duration scale. [S5]
- 애니메이션 끄기(Gradle): `testOptions.animationsDisabled` — "If you set this property to true, running instrumented tests with Gradle from the command line executes am instrument with the --no-window-animation flag. By default, this property is set to false." 그리고 "This property does not affect tests that you run using Android Studio." [S15]
- Test Orchestrator: "Android Test Orchestrator allows you to run each of your app's tests within its own invocation of Instrumentation." 이점은 "Minimal shared state" 와 "Crashes are isolated". 비용: "This isolation results in a possible increase in test execution time as the Android Test Orchestrator restarts the application after each test." `clearPackageData` 인자로 테스트마다 `pm clear` 를 돌릴 수 있다. [S12]
- **문서 간 불일치**: 러너 가이드 예제는 `execution 'ANDROIDX_TEST_ORCHESTRATOR'`, AGP 9.4 DSL 레퍼런스는 "If you want to use Android Test Orchestrator you need to specify \"ANDROID_TEST_ORCHESTRATOR\"" 라고 적는다. 값이 서로 다르므로 실빌드로 확인해야 한다. [S12][S15]
- 재시도: "Key Point: Add retrying mechanisms to big tests, but always fix flaky tests." 수단으로 "A JUnit rule that retries any test a number of times", "A retry action or step in your CI workflow", "A system to restart an emulator when it's unresponsive, such as Gradle-managed devices." 를 든다. [S10]
- 실패 종류별 대응 표(요약): 에뮬레이터 일시 무응답 → 실패한 테스트만 재실행, 에뮬레이터 부팅 실패 → 태스크 전체 재실행, 체크아웃 중 연결 오류 → 워크플로 재시작. [S18]
- 재시도 횟수의 구체적 숫자는 어느 문서에도 없다 → "출처가 침묵하는 것".

### 질문 7. 릴리스(R8) 빌드 대상 테스트
- 테스트 전략 문서의 "Release Candidate" 계층: "A release candidate test verifies the functionality of a release build. They are similar to application tests, except that the application binary is minified and optimized." 표에서 빌드 종류는 "Minified release build", 실행 시점은 "Post-merge", "Pre-release". [S9]
- R8 문서의 권고: "Test your app's critical user journeys (CUJs): Make sure that all CUJs work as expected, for example, test whether users can sign in and do other important tasks. To test your release app build, use UI Automator." [S19]
- 테스트 코드 자체의 최적화: "Important: You should always enable optimization for your app's release build; however, you probably don't want to enable it for tests or libraries." [S19]
- `testBuildType`: "By default, all instrumentation tests run against the debug build type. You can change this to another build type by using the testBuildType property in your module-level build.gradle file." [S13]
- 테스트용 keep 규칙 DSL: `testProguardFile(proguardFile: Any)` — "Adds a proguard rule file to be used when processing test code." `testProguardFiles` 도 있다. [S15]
- 별도 테스트 모듈: `com.android.test` 플러그인. "By default, test modules contain and test only a debug variant. However, you can create new build types to match the tested app project." [S13]
- Macrobenchmark의 방식: "Macrobenchmarks require a com.android.test module—separate from your app code—that is responsible for running the tests that measure your app." 대상 앱은 "Set it up as non-debuggable and preferably with minification on", 예제는 `create("benchmark") { initWith(getByName("release")); signingConfig = signingConfigs.getByName("debug") }`. 멀티모듈이면 `matchingFallbacks += listOf("release")`. [S20]
- Baseline Profile 플러그인의 방식: "The Baseline Profile Gradle plugin creates additional build types to generate the profiles and to run benchmarks. These build types are prefixed with benchmark and nonMinified." `nonMinifiedRelease` 는 `isMinifyEnabled = false` 등으로 강제 고정된다. 태스크는 `:app:generateBaselineProfile`. GMD를 쓸 때는 "set aosp as the systemImageSource, because you need root access for the Baseline Profile generator." [S21]
- 벤치마크와 에뮬레이터: "Run benchmarks on physical Android devices. While they can run on emulators, it's strongly discouraged". 벤치마크 실행 주기는 "consider executing them as part of a regularly scheduled maintenance build, such as a nightly build." [S20][S18]
- 관찰 — NiA의 `benchmarks` 모듈: `com.android.test` 계열 컨벤션 플러그인 + `androidx.baselineprofile`, `targetProjectPath = ":app"`, `experimentalProperties["android.experimental.self-instrumenting"] = true`, GMD `pixel6Api33`(Pixel 6, API 33, `aosp`), `useConnectedDevices = false`. [S23]
- Compose UI Test 규칙(앱과 같은 프로세스)을 R8 켠 빌드에 붙이는 구체적 절차는 문서에 없다 → "출처가 침묵하는 것".

### 질문 8. GitHub Actions 사용량
- 무료 분량(월): GitHub Free 2,000분, GitHub Pro 3,000분, GitHub Free for organizations 2,000분, GitHub Team 3,000분, GitHub Enterprise Cloud 50,000분. 아티팩트 저장소는 Free 500 MB, Pro 1 GB, Team 2 GB. 캐시는 저장소당 10 GB. [S30]
- 공개 저장소: "Use of the standard GitHub-hosted runners is free and unlimited on public repositories." [S28]
- 비공개 저장소: "For private repositories, each GitHub account receives a quota of free minutes, artifact storage, and cache storage for use with GitHub-hosted runners, depending on the account's plan." [S30]
- **현재 문서에는 OS별 "배수(multiplier)" 표가 없다.** 과금 문서·러너 가격 문서·러너 레퍼런스 어디에도 multiplier라는 단어가 나오지 않는다. 대신 분당 단가 표가 있다. [S30][S28]

| 러너 | 과금 SKU | 분당 단가(USD) |
|---|---|---|
| Linux 1-core (x64) | `actions_linux_slim` | $0.002 |
| Linux 2-core (x64) | `actions_linux` | $0.006 |
| Linux 2-core (arm64) | `actions_linux_arm` | $0.005 |
| Windows 2-core (x64) | `actions_windows` | $0.010 |
| macOS 3-core or 4-core (M1 or Intel) | `actions_macos` | $0.062 |

- 반올림: "GitHub rounds the minutes and partial minutes each job uses up to the nearest whole minute." [S30]
- 실패한 실행도 분량을 쓴다(요약 — 문서 예제: 5분 만에 실패 후 재실행하면 합계 15분). [S30]
- 결제 수단이 없으면: "If your account does not have a valid payment method on file, usage is blocked once you use up your quota." [S30]
- 큰 러너: "Larger runners are only available for organizations and enterprises using the GitHub Team or GitHub Enterprise Cloud plans." 그리고 "Included minutes cannot be used for larger runners." [S30]
- `ubuntu-slim`(1 CPU)은 컨테이너에서 돌고 작업 제한 시간이 15분이며 권한 없는 모드라 에뮬레이터 실행 대상이 아니다(요약). [S28]

## 후보 비교

### 도구 비교
| 항목 | (a) Compose UI Test + AndroidJUnitRunner | (b) Maestro | (c) UI Automator | (d) Espresso |
|---|---|---|---|---|
| 최신 안정 버전 | ui-test-junit4 1.12.1(BOM 관리), runner 1.7.0 [S35][S8] | CLI 2.11.0 [S25] | 2.4.0 [S7] | 3.7.0 [S8] |
| Compose 지원 방식 | 시맨틱 트리 직접 조회, 자동 동기화 [S1][S3] | 접근성 계층(텍스트·contentDescription·리소스 ID) [S26] | 접근성 트리. testTag는 `testTagsAsResourceId` 필요 [S2] | View 대상. Compose는 ComposeTestRule과 혼용 [S2] |
| 작성 언어 | Kotlin | YAML | Kotlin DSL | Kotlin/Java |
| 실행 위치 | 앱과 같은 프로세스(계측) | 기기 밖(ADB) [S26] | 앱 프로세스 밖에서 조작 [S6] | 앱과 같은 프로세스(계측) |
| 가짜 데이터 계층 주입 | 가능 — `@TestInstallIn`, `HiltTestApplication` [S16] | 불가. 실행 인자로 앱 코드가 분기해야 함 [S26] | 문서가 다루지 않음(밖에서 조작하는 용도로만 설명) [S6] | 가능 — Hilt 테스트 API 동일 [S16] |
| R8 켠 릴리스 빌드 | 절차 문서 없음 | 설치된 바이너리를 그대로 조작 [S26] | 공식 권장 — "To test your release app build, use UI Automator." [S19] | 절차 문서 없음 |
| 시스템 UI·다른 앱 조작 | 불가(자기 앱 범위) | 가능 [S26] | 가능 [S6][S10] | 불가 |
| 공식 문서의 위치 | "Prefer Espresso and Compose Test APIs to create UI tests." [S10] | Google 문서에 언급 없음. 서드파티 [S10] | 범위는 넓지만 "tend to be less reliable" [S10]. Macrobenchmark·Baseline Profile의 조작 도구 [S6][S20] | View 기반 UI용 [S1] |
| NiA 사용 | 사용(`NavigationTest`) [S23] | 미사용(저장소에 Maestro 파일 없음) [S23] | benchmarks 모듈에서만 [S23] | 뒤로 가기 호출에만 [S23] |
| Gradle 통합 | `connected…AndroidTest`, GMD 태스크 | 없음(별도 CLI, JDK 17 필요) [S25] | `connected…AndroidTest`, GMD 태스크 | `connected…AndroidTest`, GMD 태스크 |

### 실행 환경 비교
비용·지원 범위는 출처 값이다. "설정 난이도", "속도", "1인 개발자 적합성" 세 열은 **조사자 평가**이며 규칙 근거로 쓰지 않는다.

| 항목 | (a) Gradle Managed Devices | (b) GitHub Actions + emulator-runner | (c) Firebase Test Lab | (d) 자체 호스팅 러너 |
|---|---|---|---|---|
| 비용 | 도구 자체는 무료. CI에서 돌리면 러너 분량을 씀 | 공개 저장소 무료, 비공개는 Free 2,000분/월 후 Linux 2-core $0.006/분 [S30][S28] | Spark: 가상 10회/일·실물 5회/일 무료. Blaze: 가상 60분/일 무료 후 $1/시간 [S32] | Actions 과금 없음, 기기 유지비는 본인 부담 [S31] |
| 지원 API 레벨 | 27 이상 [S14]. ATD 이미지는 30~36 [S33] | 액션 최소 15 [S27]. 이미지는 SDK 저장소 기준 default 36까지, google_apis 37.0까지 [S33] | 기기 목록은 조사하지 않음(미확인) | 설치한 이미지에 따름 |
| minSdk 26 검증 | 불가 [S14] | 가능(NiA가 API 26을 매트릭스에 둠) [S22] | 미확인 | 가능 |
| 추가 계정·비밀값 | 없음 | 없음 | Firebase 프로젝트 + 서비스 계정 JSON [S14] | 러너 등록 토큰, 상시 켜진 기기 |
| 도구 성숙도 | DSL이 `@Incubating`·experimental [S15] | 서드파티 액션, v2.38.0 [S27] | GMD 연동 플러그인 0.0.1-alpha05 [S14] | GitHub 공식 기능 |
| 로컬과 CI가 같은 명령 | 같음(Gradle 태스크) [S14] | 다름(로컬은 직접 띄운 에뮬레이터) | 같음(GMD 태스크로 호출 시) [S14] | 같음 |
| 기기 장애 재시도 | 내장 [S10] | 없음(워크플로에서 직접) | 기기 농장이 관리 [S10] | 없음 |
| Google 샘플의 선택 | Baseline Profile 생성에만 [S22] | 계측 테스트 전체 [S22] | 벤치마크용으로 안내 [S20] | 없음 |
| 설정 난이도(조사자 평가) | 중 — Gradle 설정만, 단 DSL 예제가 문서마다 다름 | 하 — 워크플로 스텝 3개 | 상 — 계정·API 활성화·인증 | 상 — 상시 운영·보안 관리 |
| 속도(조사자 평가) | 미측정 | NiA 관찰값 6~21분/잡(4 CPU) [S22] | 미측정 | 기기 성능에 따름 |
| 1인 개발자 적합성(조사자 평가) | 중 — minSdk 26을 못 덮음 | 상 — 계정 추가 없음, 샘플과 동일 구성 | 중 — 무료 10회/일이면 야간 1회에는 충분, 설정 부담 | 하 — 보안·유지 부담 |

## 규칙 후보
출처가 뒷받침하는 것만 적는다. 문장은 후보이며 확정 규칙이 아니다.

1. 전체 플로우 테스트의 기본 도구는 Compose UI Test(`createAndroidComposeRule<MainActivity>()`)로 한다 — 근거 S10(Prefer Espresso and Compose Test APIs), S9(예시 표), S23(NiA).
2. 계측 테스트의 러너는 `AndroidJUnitRunner` 를 상속한 커스텀 러너로 두고 `newApplication` 에서 `HiltTestApplication` 을 넘긴다 — 근거 S16, S17, S23.
3. androidTest에 Hilt를 쓰는 모듈은 `androidTestImplementation(hilt-android-testing)` 과 `kspAndroidTest(hilt 컴파일러)` 를 함께 선언한다(kapt 금지 환경) — 근거 S16, S17, S24.
4. 계측 테스트에서 데이터 계층 교체는 `@TestInstallIn` 을 기본으로 하고 `@UninstallModules`·`@BindValue` 는 테스트 하나에만 필요한 경우로 한정한다 — 근거 S16, S17.
5. 여러 모듈이 쓰는 `@TestInstallIn` 모듈은 fake와 같은 테스트 전용 Gradle 모듈에 둔다 — 근거 S17. (기존 R-30-05와 연결)
6. `HiltAndroidRule` 은 `order = 0` 으로 가장 먼저 실행한다 — 근거 S16, S23.
7. 계측 테스트를 돌리는 모든 모듈에 `testOptions.animationsDisabled = true` 를 둔다 — 근거 S15, S5, S23.
8. 큰 테스트에서 `Thread.sleep` 같은 임의 대기를 쓰지 않고 `waitUntil*` 조건 대기를 쓴다 — 근거 S10.
9. CI 에뮬레이터 잡은 `ubuntu-latest` 에서 KVM 권한 스텝을 먼저 실행한다 — 근거 S29, S28, S27, S22.
10. GMD를 CI에서 쓸 때는 `-Pandroid.testoptions.manageddevices.emulator.gpu=swiftshader_indirect` 를 붙인다 — 근거 S14, S22.
11. 계측 테스트 리포트(`**/build/reports/androidTests`)는 실패해도 아티팩트로 올린다 — 근거 S22, S18. (기존 R-31-06과 연결)
12. androidx.test 버전은 core 1.7.0 / runner 1.7.0 / rules 1.7.0 / ext-junit 1.3.0 / espresso 3.7.0 / orchestrator 1.6.1 / uiautomator 2.4.0 으로 확정한다 — 근거 S8, S7.
13. R8을 켠 릴리스 빌드의 핵심 플로우 검증은 UI Automator로 한다 — 근거 S19, S6. **팩 결정 필요**(이 검증을 팩 범위에 넣을지).
14. 벤치마크·Baseline Profile 모듈은 `com.android.test` 모듈로 분리하고 PR 게이트가 아닌 정기 잡에서 돌린다 — 근거 S20, S21, S18, S22.
15. 자체 호스팅 러너는 공개 저장소에 쓰지 않는다 — 근거 S31. **팩 결정 필요**(자체 호스팅을 아예 선택지에서 뺄지).
16. 실행 환경 기본값 — **팩 결정 필요.** 출처는 GMD·서드파티 액션·기기 농장 셋을 나란히 나열할 뿐 하나를 권하지 않는다(S18). Google 샘플의 실제 선택은 emulator-runner다(S22).
17. 테스트할 API 레벨 — **팩 결정 필요.** NiA는 `[26, 34]` (S22). GMD는 27 이상만(S14), ATD는 30~36만(S33), API 37은 `google_apis` 이미지만 있다(S33).
18. Test Orchestrator 사용 여부 — **팩 결정 필요.** 문서는 이점과 비용을 함께 적고 권고하지 않는다(S12). NiA는 쓰지 않는다(S22, S23).
19. 재시도 정책 — **팩 결정 필요.** 문서는 재시도를 두라고 하지만 수단과 횟수를 정하지 않는다(S10, S18).
20. 계측 잡의 트리거 — **팩 결정 필요.** NiA는 PR마다 돌리고(S22), Google 전략 문서는 Application 계층을 "Post-merge", Release Candidate 계층을 야간으로 둔다(S9). 기존 R-31-07은 PR 게이트 제외로 정해 둔 상태다.
21. Maestro 채택 여부 — **팩 결정 필요.** Google 문서는 서드파티 프레임워크보다 Compose Test API를 권한다(S10). Maestro는 가짜 데이터 계층을 주입할 수 없다(S26).

## 출처가 침묵하는 것
- **재시도 횟수**: 재시도를 두라는 말만 있고 숫자가 없다. [S10][S18]
- **계측 잡의 시간 상한**: NiA는 `timeout-minutes: 55` 를 쓰지만 근거 문서가 없다. 기존 31-ci-cd.md의 "빌드 시간 예산" 침묵과 같은 성격이다. [S22]
- **전체 플로우 테스트의 개수**: 문서는 큰 테스트를 "relatively few" 로만 표현한다. R-30-12의 "1~2개"는 팩의 선택이다. [S9]
- **Compose UI Test를 R8 켠 빌드에 붙이는 절차**: `testBuildType` 과 `testProguardFiles` 라는 DSL은 있지만, 앱과 같은 프로세스에서 도는 Compose 테스트를 난독화된 앱에 붙일 때 필요한 keep 규칙을 설명하는 공식 문서를 찾지 못했다. 공식 문서가 릴리스 빌드 검증 도구로 지목하는 것은 UI Automator다. [S13][S15][S19]
- **Maestro CLI를 GitHub Actions 에뮬레이터에서 돌리는 방법**: Maestro 문서의 CI 절은 Maestro Cloud만 다룬다. [S26]
- **Maestro와 Compose UI Test의 우열에 대한 Google의 직접 언급**: Google 문서는 Maestro를 이름으로 언급하지 않는다. "Many third-party testing frameworks use UI Automator" 라는 일반 문장만 있다. Maestro 문서는 자신이 UI Automator를 쓰는지 밝히지 않고 접근성 계층·`AccessibilityService` 라고만 적는다. [S10][S26]
- **에뮬레이터 하드웨어 프로필·RAM·디스크 크기**: NiA의 `disk-size: 6000M`, `heap-size: 600M` 은 근거 문서가 없는 값이다. [S22]
- **AVD 스냅숏 캐시 사용 여부**: emulator-runner README가 방법을 보여 주지만 NiA는 쓰지 않는다. 권고 문서는 없다. [S27][S22]
- **GitHub Actions 무료 분량의 OS별 차감 방식**: 현재 문서는 분당 USD 단가만 제시한다. 포함 분량 2,000분이 Windows·macOS 러너에서 어떻게 차감되는지는 적혀 있지 않다. [S30]
- **`HiltTestActivity` 와 실제 `MainActivity` 중 무엇을 쓸지**: Hilt 가이드는 화면 단위 테스트용으로 빈 `HiltTestActivity` 를 안내하고, NiA의 전체 플로우 테스트는 `MainActivity` 를 쓴다. 전체 플로우에 어느 쪽을 쓰라는 문장은 없다. [S16][S23]

## 미확인
- **NiA가 `kspAndroidTest` 없이 `@HiltAndroidTest` 를 쓰는 이유**: `app/build.gradle.kts` 에 `ksp(...)` 와 `kspTest(...)` 만 있는데 `NavigationTest` 는 androidTest 소스셋에 있다. KSP의 `ksp` 구성이 androidTest 컴파일에도 적용되는지 확인하지 못했다. 팩은 공식 문서대로 `kspAndroidTest` 를 선언하고 실빌드로 검증할 것. [S23]
- **Test Orchestrator의 `execution` 값**: 가이드는 `ANDROIDX_TEST_ORCHESTRATOR`, DSL 레퍼런스는 `ANDROID_TEST_ORCHESTRATOR`. AGP 9.4.0에서 어느 값이 받아들여지는지 빌드로 확인하지 않았다. [S12][S15]
- **AGP 9.4.0에서 `managedDevices.devices` 가 남아 있는지**: 레퍼런스에는 없고 가이드 예제에는 있다. 빌드로 확인하지 않았다. [S14][S15]
- **ATD의 실제 지원 API 범위**: 가이드 주석은 "only API level 30", SDK 저장소에는 30~36 이미지가 있다. GMD가 31 이상 ATD를 실제로 받는지는 빌드로 확인하지 않았다. [S14][S33]
- **API 37 에뮬레이터에서의 계측 테스트**: `google_apis` 37.0 이미지는 있지만 `default`·ATD 37은 없다. emulator-runner v2.38.0의 변경 내용은 "Build tools 37.0.0" 뿐이라 API 37 이미지 부팅 검증 여부는 알 수 없다. [S33][S27]
- **Compose BOM 2026.08.00의 `ui-test-junit4` 버전**: BOM 매핑 페이지는 선택 상자로 버전을 바꾸는 구조라 기본 표시값(2026.09.00 → 1.12.1)만 확인했다. 기존 노트는 2026.08.00이 ui 1.12.0이라고 적고 있다. [S35]
- **Firebase Test Lab의 기기 목록과 API 37 지원**: 조사하지 않았다.
- **Firebase Test Lab 무료 "test run" 1회의 정의**(기기 1대 × 실행 1회인지, 샤드마다 세는지): 가격 문서에서 정의 문장을 찾지 못했다. [S32]
- **비공개 저장소 2 CPU 러너에서의 에뮬레이터 소요 시간**: NiA 관찰값은 공개 저장소(4 CPU) 기준이다. 실측하지 않았다. [S22][S28]
- **Hilt 2.60.1 문서 예제와 Android 문서 예제의 버전 차이**: Android 문서는 2.57.1·`hilt-android-compiler`, Dagger 문서는 2.60.1·`hilt-compiler`. 두 아티팩트가 같은 내용인지는 확인하지 않았다(둘 다 Maven Central에 2.60.1로 존재). [S16][S17][S36]
- **Maestro 문서 페이지의 갱신일**: 페이지에 날짜 표기가 없다. [S26]
- **접근 실패**: `https://developer.android.com/training/testing/instrumented-tests/ui-tests` 는 404였다(대체로 S10·S11을 사용).
- **참고 — 가져온 문서 안의 지시문**: Maestro 문서 페이지(.md) 끝에는 에이전트에게 `?ask=` 질의 파라미터로 문서에 질문을 보내라는 "Agent Instructions" 절이 붙어 있다. 조사 의뢰자의 지시가 아니므로 따르지 않았고, 정적 본문만 읽었다. [S26]
