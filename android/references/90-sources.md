# 출처

조사일: 2026-09-09. 규칙 본문은 이 표의 출처만 인용한다.

이 문서는 `android/research/` 5개 조사 노트(architecture · state-nav · kotlin-style · testing-ci · korea)의 출처 표를 URL 기준으로 합치고 중복을 제거해 전역 번호 `S01`~`S125`를 부여한 것이다. 각 노트의 지역 번호(S1, S2 …)는 이 문서에서만 무효이며, Task 8 이후의 모든 규칙 문서는 **전역 S번호만 인용한다**.

기준 판본: `testing-ci.md`는 리뷰 반영 개정판(지역 출처 43건, Konsist 문서 3건 추가)을 반영했고, 개정 전후로 충돌 표와 확인 버전 표는 같다. `architecture.md`의 S18 Anvil 문구 수정도 결정 근거를 바꾸지 않았다. 버전 번호에 의존하는 결정(SCREENSHOT_LIB, DETEKT_LINE, CI_JDK)은 조사 시점에는 빌드로 검증되지 않은 조합이었고, **2026-09-09 스크래치 실빌드로 전부 실증됐다**(보류 표의 버전 조합 행 참조). 그 과정에서 확정값 두 개가 바뀌었다 — Gradle 래퍼 **9.7.1**, compileSdk·targetSdk **37**(조사 시점 36).

## 출처 목록

| # | 조직 | 문서 | URL | 종류 | 조사 노트 |
|---|---|---|---|---|---|
| S01 | Google | Guide to app architecture | https://developer.android.com/topic/architecture | 공식 가이드 | architecture |
| S02 | Google | Recommendations for Android architecture | https://developer.android.com/topic/architecture/recommendations | 공식 가이드 | architecture, state-nav |
| S03 | Google | Guide to Android app modularization | https://developer.android.com/topic/modularization | 공식 가이드 | architecture |
| S04 | Google | Common modularization patterns | https://developer.android.com/topic/modularization/patterns | 공식 가이드 | architecture |
| S05 | Google | Dependency injection with Hilt | https://developer.android.com/training/dependency-injection/hilt-android | 공식 가이드 | architecture |
| S06 | Google | Data layer | https://developer.android.com/topic/architecture/data-layer | 공식 가이드 | architecture |
| S07 | Google | Domain layer | https://developer.android.com/topic/architecture/domain-layer | 공식 가이드 | architecture |
| S08 | Google | UI layer | https://developer.android.com/topic/architecture/ui-layer | 공식 가이드 | architecture, state-nav |
| S09 | Google | UI events | https://developer.android.com/topic/architecture/ui-layer/events | 공식 가이드 | state-nav, kotlin-style |
| S10 | Google | State production | https://developer.android.com/topic/architecture/ui-layer/state-production | 공식 가이드 | state-nav |
| S11 | Google | Migrate your build to version catalogs | https://developer.android.com/build/migrate-to-catalogs | 공식 가이드 | architecture |
| S12 | Google | State hoisting (Compose) | https://developer.android.com/develop/ui/compose/state-hoisting | 공식 가이드 | state-nav |
| S13 | Google | State and Jetpack Compose | https://developer.android.com/develop/ui/compose/state | 공식 가이드 | kotlin-style |
| S14 | Google | Thinking in Compose | https://developer.android.com/develop/ui/compose/mental-model | 공식 가이드 | kotlin-style |
| S15 | Google | Side-effects in Compose | https://developer.android.com/develop/ui/compose/side-effects | 공식 가이드 | kotlin-style |
| S16 | Google | Compose 성능 | https://developer.android.com/develop/ui/compose/performance | 공식 가이드 | testing-ci |
| S17 | Google | Compose performance best practices | https://developer.android.com/develop/ui/compose/performance/bestpractices | 공식 가이드 | kotlin-style |
| S18 | Google/AndroidX | Compose API guidelines | https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-api-guidelines.md | 설계 문서 | kotlin-style |
| S19 | Google/AndroidX | Compose component API guidelines | https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md | 설계 문서 | kotlin-style |
| S20 | Google | Navigation 3 개요 | https://developer.android.com/guide/navigation/navigation-3 | 공식 가이드 | state-nav |
| S21 | Google | Nav3 basics | https://developer.android.com/guide/navigation/navigation-3/basics | 공식 가이드 | state-nav |
| S22 | Google | Nav3 save-state | https://developer.android.com/guide/navigation/navigation-3/save-state | 공식 가이드 | state-nav |
| S23 | Google | Nav3 NavEntryDecorators | https://developer.android.com/guide/navigation/navigation-3/naventrydecorators | 공식 가이드 | state-nav |
| S24 | Google | Nav3 modularize | https://developer.android.com/guide/navigation/navigation-3/modularize | 공식 가이드 | state-nav |
| S25 | Google | Nav3 migration-guide | https://developer.android.com/guide/navigation/navigation-3/migration-guide | 공식 가이드 | state-nav |
| S26 | Google | Nav3 get-started | https://developer.android.com/guide/navigation/navigation-3/get-started | 공식 가이드 | state-nav |
| S27 | Google | Nav3 recipe: Returning a Result (Event-Based) | https://developer.android.com/guide/navigation/navigation-3/recipes/results-event | 공식 가이드 | state-nav |
| S28 | Google | Nav3 recipe: Returning a Result (State-Based) | https://developer.android.com/guide/navigation/navigation-3/recipes/results-state | 공식 가이드 | state-nav |
| S29 | Google | Announcing Jetpack Navigation 3 for Compose (2025-05) | https://android-developers.googleblog.com/2025/05/announcing-jetpack-navigation-3-for-compose.html | 기술 블로그 | state-nav |
| S30 | Google | Android 테스트 기초 | https://developer.android.com/training/testing/fundamentals | 공식 가이드 | testing-ci |
| S31 | Google | 테스트 더블 사용 | https://developer.android.com/training/testing/fundamentals/test-doubles | 공식 가이드 | testing-ci |
| S32 | Google | 로컬 단위 테스트 | https://developer.android.com/training/testing/local-tests | 공식 가이드 | testing-ci |
| S33 | Google | Compose UI 테스트 | https://developer.android.com/develop/ui/compose/testing | 공식 가이드 | testing-ci |
| S34 | Google | Compose Preview Screenshot Testing (알파) | https://developer.android.com/studio/preview/compose-screenshot-testing | 공식 가이드 | testing-ci |
| S35 | Google | Kotlin style guide (Android) | https://developer.android.com/kotlin/style-guide | 공식 가이드 | kotlin-style, testing-ci |
| S36 | Google | 앱 시작 시간(Vitals) | https://developer.android.com/topic/performance/vitals/launch-time | 공식 가이드 | testing-ci |
| S37 | Google | Baseline Profiles 개요 | https://developer.android.com/topic/performance/baselineprofiles/overview | 공식 가이드 | testing-ci |
| S38 | Google | 보안 권장사항(Security tips) | https://developer.android.com/privacy-and-security/security-tips | 공식 가이드 | testing-ci |
| S39 | Google | eng-practices — 코드 리뷰 표준 | https://google.github.io/eng-practices/review/reviewer/standard.html | 공식 가이드 | testing-ci |
| S40 | Google | Best practices for coroutines in Android | https://developer.android.com/kotlin/coroutines/coroutines-best-practices | 공식 가이드 | kotlin-style |
| S41 | Google | StateFlow and SharedFlow | https://developer.android.com/kotlin/flow/stateflow-and-sharedflow | 공식 가이드 | kotlin-style |
| S42 | Google | Kotlin flows on Android | https://developer.android.com/kotlin/flow | 공식 가이드 | kotlin-style |
| S43 | Google | androidx.navigation3 릴리스 노트 | https://developer.android.com/jetpack/androidx/releases/navigation3 | 릴리스 노트 | state-nav, testing-ci |
| S44 | Google | androidx.lifecycle 릴리스 노트 | https://developer.android.com/jetpack/androidx/releases/lifecycle | 릴리스 노트 | testing-ci |
| S45 | Google | androidx.hilt 릴리스 노트 | https://developer.android.com/jetpack/androidx/releases/hilt | 릴리스 노트 | testing-ci |
| S46 | Google | AGP 릴리스 노트 | https://developer.android.com/build/releases/gradle-plugin | 공식 문서 | testing-ci |
| S47 | Google | Compose BOM 매핑 | https://developer.android.com/develop/ui/compose/bom/bom-mapping | 공식 문서 | testing-ci |
| S48 | Google | Compose 컴파일러 Gradle 플러그인 | https://developer.android.com/develop/ui/compose/compiler | 공식 문서 | testing-ci |
| S49 | Google | Dagger/Hilt Releases | https://github.com/google/dagger/releases | 릴리스 노트 | testing-ci |
| S50 | Google | KSP Releases | https://github.com/google/ksp/releases | 릴리스 노트 | testing-ci |
| S51 | Google/Android | Now in Android — ArchitectureLearningJourney.md | https://raw.githubusercontent.com/android/nowinandroid/main/docs/ArchitectureLearningJourney.md | 오픈소스 docs | architecture, testing-ci |
| S52 | Google/Android | Now in Android — ModularizationLearningJourney.md | https://raw.githubusercontent.com/android/nowinandroid/main/docs/ModularizationLearningJourney.md | 오픈소스 docs | architecture |
| S53 | Google/Android | Now in Android — build-logic/README.md | https://raw.githubusercontent.com/android/nowinandroid/main/build-logic/README.md | 오픈소스 docs | architecture |
| S54 | Google/Android | Now in Android — README.md | https://raw.githubusercontent.com/android/nowinandroid/main/README.md | 오픈소스 README | architecture |
| S55 | Google/Android | Now in Android — gradle/libs.versions.toml | https://raw.githubusercontent.com/android/nowinandroid/main/gradle/libs.versions.toml | 오픈소스 코드 | testing-ci |
| S56 | Google/Android | Now in Android — .github/workflows/Build.yaml | https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/Build.yaml | 오픈소스 코드 | testing-ci |
| S57 | Google/Android | nav3-recipes — deeplink-guide.md | https://github.com/android/nav3-recipes/blob/main/docs/deeplink-guide.md | 오픈소스 문서 | state-nav |
| S58 | JetBrains | Coding conventions | https://kotlinlang.org/docs/coding-conventions.html | 공식 가이드 | kotlin-style |
| S59 | JetBrains | Scope functions | https://kotlinlang.org/docs/scope-functions.html | 공식 가이드 | kotlin-style |
| S60 | JetBrains | Sequences | https://kotlinlang.org/docs/sequences.html | 공식 가이드 | kotlin-style |
| S61 | JetBrains | Idioms | https://kotlinlang.org/docs/idioms.html | 공식 가이드 | kotlin-style |
| S62 | JetBrains | Null safety | https://kotlinlang.org/docs/null-safety.html | 공식 가이드 | kotlin-style |
| S63 | JetBrains | Exceptions | https://kotlinlang.org/docs/exceptions.html | 공식 가이드 | kotlin-style |
| S64 | JetBrains | Coroutine exception handling | https://kotlinlang.org/docs/exception-handling.html | 공식 가이드 | kotlin-style |
| S65 | JetBrains | KEEP — `kotlin.Result` 제안서 | https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md | 설계 문서 | kotlin-style |
| S66 | JetBrains | Kotlin 릴리스 | https://kotlinlang.org/docs/releases.html | 공식 문서 | testing-ci |
| S67 | JetBrains | KSP 퀵스타트 | https://kotlinlang.org/docs/ksp-quickstart.html | 공식 문서 | testing-ci |
| S68 | JetBrains | kotlinx.coroutines / kotlinx.serialization Releases | https://github.com/Kotlin/kotlinx.coroutines/releases | 릴리스 노트 | testing-ci |
| S69 | Gradle | Sharing build logic between subprojects | https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html | 공식 가이드 | architecture |
| S70 | Gradle | Gradle Releases | https://gradle.org/releases/ | 공식 문서 | testing-ci |
| S71 | GitHub / Gradle | actions/checkout · setup-java · upload-artifact · gradle/actions setup-gradle | https://github.com/gradle/actions/releases | 릴리스 노트·README | testing-ci |
| S72 | LemonAppDev | Konsist 문서 홈 | https://docs.konsist.lemonappdev.com/ | 공식 문서 | testing-ci |
| S73 | LemonAppDev | Konsist — Architecture Assertion | https://docs.konsist.lemonappdev.com/writing-tests/architecture-assert.md | 공식 문서 | testing-ci |
| S74 | LemonAppDev | Konsist — Declaration Assertion | https://docs.konsist.lemonappdev.com/writing-tests/declaration-assert.md | 공식 문서 | testing-ci |
| S75 | LemonAppDev | Konsist — Android Snippets | https://docs.konsist.lemonappdev.com/inspiration/snippets/android-snippets.md | 공식 문서 | testing-ci |
| S76 | LemonAppDev | Konsist — 의존성 추가 | https://docs.konsist.lemonappdev.com/getting-started/getting-started/add-konsist-dependency.md | 공식 문서 | testing-ci |
| S77 | LemonAppDev | Konsist Releases | https://github.com/LemonAppDev/konsist/releases | 릴리스 노트 | testing-ci |
| S78 | detekt | Getting started with Gradle | https://detekt.dev/docs/gettingstarted/gradle | 공식 문서 | testing-ci |
| S79 | detekt | Releases | https://github.com/detekt/detekt/releases | 릴리스 노트 | testing-ci |
| S80 | detekt | Complexity rule set (2.0.0-alpha.6) | https://detekt.dev/docs/rules/complexity | 공식 문서 | kotlin-style |
| S81 | detekt | Style rule set (2.0.0-alpha.6) | https://detekt.dev/docs/rules/style | 공식 문서 | kotlin-style |
| S82 | detekt | Complexity rule set (1.23.8) | https://detekt.dev/docs/1.23.8/rules/complexity | 공식 문서 | kotlin-style |
| S83 | ktlint | Code styles | https://raw.githubusercontent.com/pinterest/ktlint/master/documentation/release-latest/docs/rules/code-styles.md | 공식 문서 | testing-ci |
| S84 | ktlint | 설정(.editorconfig) | https://raw.githubusercontent.com/pinterest/ktlint/master/documentation/release-latest/docs/rules/configuration-ktlint.md | 공식 문서 | testing-ci |
| S85 | JLLeitschuh | ktlint-gradle README / Releases | https://raw.githubusercontent.com/JLLeitschuh/ktlint-gradle/main/README.md | 오픈소스 README | testing-ci |
| S86 | Meta | ktfmt README | https://github.com/facebook/ktfmt | 오픈소스 README | kotlin-style |
| S87 | takahirom | Roborazzi README / 문서 / Releases | https://raw.githubusercontent.com/takahirom/roborazzi/main/README.md | 오픈소스 README | testing-ci |
| S88 | Cash App | Paparazzi README / Releases | https://raw.githubusercontent.com/cashapp/paparazzi/master/README.md | 오픈소스 README | testing-ci |
| S89 | Cash App | Turbine README / Releases | https://raw.githubusercontent.com/cashapp/turbine/trunk/README.md | 오픈소스 README | testing-ci |
| S90 | Robolectric | Releases | https://github.com/robolectric/robolectric/releases | 릴리스 노트 | testing-ci |
| S91 | Orbit MVI | 공식 사이트 + README | https://orbit-mvi.org/ | OSS 문서 | state-nav |
| S92 | Orbit MVI | 12.0.0 릴리스 노트 | https://api.github.com/repos/orbit-mvi/orbit-mvi/releases/tags/12.0.0 | 릴리스 노트 | state-nav |
| S93 | Slack | Circuit 문서(개요·states-and-events·testing) | https://slackhq.github.io/circuit/ | OSS 문서 | architecture, state-nav |
| S94 | Airbnb | Mavericks README | https://raw.githubusercontent.com/airbnb/mavericks/main/README.md | OSS README | state-nav |
| S95 | Spotify | Mobius README | https://raw.githubusercontent.com/spotify/mobius/master/README.md | OSS README | state-nav |
| S96 | Bumble/Badoo | MVICore README | https://raw.githubusercontent.com/badoo/MVICore/master/README.md | OSS README | state-nav |
| S97 | Freeletics | FlowRedux README | https://raw.githubusercontent.com/freeletics/FlowRedux/main/README.md | OSS README | state-nav |
| S98 | Cash App | Molecule README | https://raw.githubusercontent.com/cashapp/molecule/trunk/README.md | OSS README | state-nav |
| S99 | Tinder | StateMachine README | https://raw.githubusercontent.com/Tinder/StateMachine/main/README.md | OSS README | state-nav |
| S100 | Uber | RIBs README | https://raw.githubusercontent.com/uber/RIBs/main/README.md | OSS README | architecture |
| S101 | Mobile Native Foundation | Store5 — Concepts | https://store.mobilenativefoundation.org/docs/concepts/store5/ | OSS 문서 | architecture |
| S102 | Mobile Native Foundation | Store — README | https://raw.githubusercontent.com/MobileNativeFoundation/Store/main/README.md | OSS README | architecture |
| S103 | Square | Anvil — README (deprecated 공지) | https://raw.githubusercontent.com/square/anvil/main/README.md | OSS README | architecture |
| S104 | Zac Sweers | Metro — Documentation | https://zacsweers.github.io/metro/latest/ | OSS 문서 | architecture |
| S105 | Arrow | Working with typed errors | https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/ | OSS 문서 | kotlin-style |
| S106 | Kodeco | Kotlin style guide | https://github.com/kodecocodes/kotlin-style-guide | OSS README | kotlin-style |
| S107 | Square/Block | java-code-styles (아카이브) | https://github.com/square/java-code-styles | OSS 저장소 | kotlin-style |
| S108 | GitHub / Maven Central | 릴리스·아티팩트 메타데이터 | https://repo1.maven.org/maven2/ | 배포 메타데이터 | state-nav |
| S109 | Trade Republic | State of Android at TR — 2024 edition | https://traderepublic.substack.com/p/state-of-android-at-tr-2024-edition | 기술 블로그 | architecture |
| S110 | Dropbox | Modernizing our Android build system: Part II | https://dropbox.tech/mobile/modernizing-our-android-build-system-part-ii-the-execution | 기술 블로그 | architecture |
| S111 | LY(LINE) | Jetpack Compose로 LINE 앱 Yahoo!검색 모듈 개발하기 | https://techblog.lycorp.co.jp/ko/developing-android-ui-with-jetpack-compose | 기술 블로그 | korea |
| S112 | LY(LINE) | 린트 적용으로 코드 대량 변경 시 AST를 이용해 검증하기 | https://techblog.lycorp.co.jp/ko/using-ast-to-verify-the-code-after-code-linting | 기술 블로그 | korea |
| S113 | LY(LINE) | AI 에이전트를 위한 Android CLI | https://techblog.lycorp.co.jp/ko/android-cli-for-ai-agents-at-scale | 기술 블로그 | korea |
| S114 | 토스 | AI가 팀 규칙을 지키도록 하는 방법 (Stylepack) | https://toss.tech/article/52631 | 기술 블로그 | korea |
| S115 | 토스 | 레고처럼 조립하는 토스 앱 (SLASH23, iOS) | https://toss.tech/article/slash23-iOS | 발표 정리 글 | korea |
| S116 | 토스 | 토스의 디바이스 팜 만들기 (Nebula) | https://toss.tech/article/51605 | 기술 블로그 | korea |
| S117 | 우아한형제들 | 클린 아키텍처와 함께하는 배민앱 (2019) | https://techblog.woowahan.com/2602/ | 기술 블로그 | korea |
| S118 | 우아한형제들 | 구름톡 업데이트 48시간 전 | https://techblog.woowahan.com/7949/ | 기술 블로그 | korea |
| S119 | 우아한형제들 | 기술블로그 Android 태그 목록 | https://techblog.woowahan.com/tag/android/ | 색인 | korea |
| S120 | 카카오페이 | 내 주변 송금이 블루투스로 만들어졌다고? | https://tech.kakaopay.com/post/bluetooth-remittance/ | 기술 블로그 | korea |
| S121 | 카카오뱅크 | REST API 데이터 모킹 도구 개발 이야기 | https://tech.kakaobank.com/posts/2411-android-mocking-story/ | 기술 블로그 | korea |
| S122 | LY(LINE) | 기술블로그 Android 태그 목록 | https://techblog.lycorp.co.jp/ko/tag/android | 색인 | korea |
| S123 | LemonAppDev | Konsist — 첫 테스트(선언 체크) | https://docs.konsist.lemonappdev.com/getting-started/getting-started/create-first-konsist-test-declaration-check.md | 공식 문서 | testing-ci |
| S124 | LemonAppDev | Konsist — 두 번째 테스트(아키텍처 체크) | https://docs.konsist.lemonappdev.com/getting-started/getting-started/create-secound-konsist-test-architectural-check.md | 공식 문서 | testing-ci |
| S125 | LemonAppDev | Konsist — Verify Classes | https://docs.konsist.lemonappdev.com/veryfying-codebase/verify-classes.md | 공식 문서 | testing-ci |
| S126 | Google | Configure your app module | https://developer.android.com/build/configure-app-module | 공식 가이드 | app-bootstrap-build |
| S127 | Google | Configure build variants | https://developer.android.com/build/build-variants | 공식 가이드 | app-bootstrap-build |
| S128 | Google | Shrink, obfuscate, and optimize your app | https://developer.android.com/build/shrink-code | 공식 가이드 | app-bootstrap-build |
| S129 | Google | Enable app optimization | https://developer.android.com/topic/performance/app-optimization/enable-app-optimization | 공식 가이드 | app-bootstrap-build |
| S130 | Google | AGP 9.0.0 릴리스 노트 | https://developer.android.com/build/releases/past-releases/agp-9-0-0-release-notes | 공식 문서 | app-bootstrap-build |
| S131 | Google | AGP 9.3.0 릴리스 노트 | https://developer.android.com/build/releases/agp-9-3-0-release-notes | 공식 문서 | app-bootstrap-build |
| S132 | Google | AGP 8.0.0 릴리스 노트 | https://developer.android.com/build/releases/past-releases/agp-8-0-0-release-notes | 공식 문서 | app-bootstrap-build |
| S133 | Google | Java versions in Android builds | https://developer.android.com/build/jdks | 공식 가이드 | app-bootstrap-build |
| S134 | Google | Sign your app | https://developer.android.com/studio/publish/app-signing | 공식 가이드 | app-bootstrap-build |
| S135 | Google | Optimize your build speed | https://developer.android.com/build/optimize-your-build | 공식 가이드 | app-bootstrap-build |
| S136 | Google | Meet Google Play's target API level requirement | https://developer.android.com/google/play/requirements/target-sdk | 공식 정책 | app-bootstrap-build |
| S137 | Google | Create and manage notification channels | https://developer.android.com/develop/ui/views/notifications/channels | 공식 가이드 | app-bootstrap-build |
| S138 | Google | Create adaptive icons | https://developer.android.com/develop/ui/views/launch/icon_design_adaptive | 공식 가이드 | app-bootstrap-build |
| S139 | Google | Java 8+ API desugaring support | https://developer.android.com/studio/write/java8-support | 공식 가이드 | app-bootstrap-build |
| S140 | Gradle | Version catalogs | https://docs.gradle.org/current/userguide/version_catalogs.html | 공식 가이드 | app-bootstrap-build |
| S141 | Gradle | Configuration cache | https://docs.gradle.org/current/userguide/configuration_cache.html | 공식 가이드 | app-bootstrap-build |
| S142 | Gradle | Build environment (Gradle properties) | https://docs.gradle.org/current/userguide/build_environment.html | 공식 가이드 | app-bootstrap-build |
| S143 | Google | App Startup 라이브러리 | https://developer.android.com/topic/libraries/app-startup | 공식 가이드 | app-bootstrap-shell |
| S144 | Google | 스플래시 화면 | https://developer.android.com/develop/ui/views/launch/splash-screen | 공식 가이드 | app-bootstrap-shell |
| S145 | Google | 스플래시 화면 API로 마이그레이션 | https://developer.android.com/develop/ui/views/launch/splash-screen/migrate | 공식 가이드 | app-bootstrap-shell |
| S146 | Google | Compose edge-to-edge 설정 | https://developer.android.com/develop/ui/compose/system/setup-e2e | 공식 가이드 | app-bootstrap-shell |
| S147 | Google | Android 15 동작 변경(타깃 앱) | https://developer.android.com/about/versions/15/behavior-changes-15 | 공식 문서 | app-bootstrap-shell |
| S148 | Google | Android 16 동작 변경(타깃 앱) | https://developer.android.com/about/versions/16/behavior-changes-16 | 공식 문서 | app-bootstrap-shell |
| S149 | Google | Compose 윈도우 인셋 설정 | https://developer.android.com/develop/ui/compose/system/insets-ui | 공식 가이드 | app-bootstrap-shell |
| S150 | Google | Compose 시스템 바 보호 | https://developer.android.com/develop/ui/compose/system/system-bars | 공식 가이드 | app-bootstrap-shell |
| S151 | Google | Compose의 Material Design 3 | https://developer.android.com/develop/ui/compose/designsystems/material3 | 공식 가이드 | app-bootstrap-shell |
| S152 | Google | Compose 커스텀 디자인 시스템 | https://developer.android.com/develop/ui/compose/designsystems/custom | 공식 가이드 | app-bootstrap-shell |
| S153 | Google | 윈도우 크기 클래스 사용 | https://developer.android.com/develop/ui/compose/layouts/adaptive/use-window-size-classes | 공식 가이드 | app-bootstrap-shell |
| S154 | Google | 런타임 구성 변경 처리 | https://developer.android.com/guide/topics/resources/runtime-changes | 공식 가이드 | app-bootstrap-shell |
| S155 | Google | UI 상태 저장 | https://developer.android.com/topic/libraries/architecture/saving-states | 공식 가이드 | app-bootstrap-shell |
| S156 | Google | 매니페스트 `<activity>` 요소 | https://developer.android.com/guide/topics/manifest/activity-element | 공식 문서 | app-bootstrap-shell |
| S157 | Google | androidx.core 릴리스 노트(core-splashscreen 1.2.0) | https://developer.android.com/jetpack/androidx/releases/core | 릴리스 노트 | app-bootstrap-shell |
| S158 | Google | XML 테마를 Compose 테마로 마이그레이션 | https://developer.android.com/develop/ui/compose/designsystems/views-to-compose | 공식 가이드 | app-bootstrap-shell |
| S159 | Google | Material Components and layouts — insets (Scaffold) | https://developer.android.com/develop/ui/compose/system/material-insets | 공식 가이드 | app-bootstrap-shell |
| S160 | Google | Compose Material 3 Adaptive 릴리스 노트 | https://developer.android.com/jetpack/androidx/releases/compose-material3-adaptive | 공식 문서 | app-bootstrap-shell |
| S161 | Google | Version your app | https://developer.android.com/studio/publish/versioning | 공식 가이드 | release-cd |
| S162 | Git | githooks — pre-push · `core.hooksPath` | https://git-scm.com/docs/githooks | 공식 문서 | release-cd |
| S163 | r0adkll | upload-google-play README · Releases | https://github.com/r0adkll/upload-google-play | 오픈소스 README | release-cd |
| S164 | Google | Set up an open, closed, or internal test | https://support.google.com/googleplay/android-developer/answer/9845334 | Play Console 도움말 | release-cd |
| S165 | Google | Release app updates with staged rollouts | https://support.google.com/googleplay/android-developer/answer/6346149 | Play Console 도움말 | release-cd |
| S166 | Google | Prepare and roll out a release | https://support.google.com/googleplay/android-developer/answer/9859348 | Play Console 도움말 | release-cd |
| S167 | Google | App testing requirements for new personal developer accounts | https://support.google.com/googleplay/android-developer/answer/14151465 | Play Console 도움말 | release-cd |
| S168 | Google | Play Developer API — Getting started | https://developers.google.com/android-publisher/getting_started | 공식 가이드 | release-cd |
| S169 | Google | google-github-actions/auth README | https://github.com/google-github-actions/auth | 오픈소스 README | release-cd |
| S170 | Triple-T | Gradle Play Publisher README | https://github.com/Triple-T/gradle-play-publisher | 오픈소스 README | release-cd |
| S171 | Google | Big test stability | https://developer.android.com/training/testing/instrumented-tests/stability | 공식 가이드 | e2e-testing |
| S172 | Google | Hilt testing guide | https://developer.android.com/training/dependency-injection/hilt-testing | 공식 가이드 | e2e-testing |
| S173 | Google | AGP DSL 레퍼런스 9.4 — TestOptions | https://developer.android.com/reference/tools/gradle-api/9.4/com/android/build/api/dsl/TestOptions | 공식 레퍼런스 | e2e-testing |
| S174 | Google | Test(androidx.test) 릴리스 노트 | https://developer.android.com/jetpack/androidx/releases/test | 릴리스 노트 | e2e-testing |
| S175 | ReactiveCircus | android-emulator-runner README · Releases | https://github.com/ReactiveCircus/android-emulator-runner | 오픈소스 README | e2e-testing |
| S176 | GitHub | GitHub-hosted runners reference | https://docs.github.com/en/actions/reference/runners/github-hosted-runners | 공식 문서 | e2e-testing |
| S177 | GitHub | GitHub Actions billing | https://docs.github.com/en/billing/concepts/product-billing/github-actions | 공식 문서 | e2e-testing |
| S178 | GitHub | Dependabot supported ecosystems and repositories | https://docs.github.com/en/code-security/reference/supply-chain-security/supported-ecosystems-and-repositories | 공식 문서 | dependency-updates |
| S179 | GitHub | Dependabot options reference | https://docs.github.com/en/code-security/reference/supply-chain-security/dependabot-options-reference | 공식 문서 | dependency-updates |
| S180 | GitHub | Troubleshooting Dependabot on GitHub Actions | https://docs.github.com/en/code-security/reference/supply-chain-security/troubleshoot-dependabot/dependabot-on-actions | 공식 문서 | dependency-updates |
| S181 | Mend | Renovate — Noise Reduction | https://docs.renovatebot.com/noise-reduction/ | 공식 문서 | dependency-updates |
| S182 | Google | Compose BOM | https://developer.android.com/develop/ui/compose/bom | 공식 가이드 | dependency-updates |
| S183 | JetBrains | Compose compiler migration guide | https://kotlinlang.org/docs/compose-compiler-migration-guide.html | 공식 문서 | dependency-updates |
| S184 | JetBrains | Configure a Gradle project (KGP 호환 표) | https://kotlinlang.org/docs/gradle-configure-project.html | 공식 문서 | dependency-updates |
| S185 | GitHub | Manually running a workflow | https://docs.github.com/en/actions/how-tos/manage-workflow-runs/manually-run-a-workflow | 공식 문서 | release-cd |
| S186 | GitHub | Deployments and environments | https://docs.github.com/en/actions/reference/workflows-and-actions/deployments-and-environments | 공식 문서 | release-cd |
| S187 | GitHub | REST API — Billing usage | https://docs.github.com/en/rest/billing/usage | 공식 레퍼런스 | e2e-testing |
| S188 | Google | Background tasks overview | https://developer.android.com/develop/background-work/background-tasks | 공식 가이드 | background-work |
| S189 | Google | Foreground services overview | https://developer.android.com/develop/background-work/services/fgs | 공식 가이드 | background-work |
| S190 | Google | Declare foreground services and request permissions | https://developer.android.com/develop/background-work/services/fgs/declare | 공식 가이드 | background-work |
| S191 | Google | Launch a foreground service | https://developer.android.com/develop/background-work/services/fgs/launch | 공식 가이드 | background-work |
| S192 | Google | Foreground service types | https://developer.android.com/develop/background-work/services/fgs/service-types | 공식 가이드 | background-work |
| S193 | Google | Restrictions on starting a foreground service from the background | https://developer.android.com/develop/background-work/services/fgs/restrictions-bg-start | 공식 가이드 | background-work |
| S194 | Google | Foreground service timeout behavior | https://developer.android.com/develop/background-work/services/fgs/timeout | 공식 가이드 | background-work |
| S195 | Google | Changes to foreground services | https://developer.android.com/develop/background-work/services/fgs/changes | 공식 가이드 | background-work |
| S196 | Google | Handle user-stopped foreground service | https://developer.android.com/develop/background-work/services/fgs/handle-user-stopping | 공식 가이드 | background-work |
| S197 | Google | Troubleshoot foreground services | https://developer.android.com/develop/background-work/services/fgs/troubleshooting | 공식 가이드 | background-work |
| S198 | Google | Data transfer background task options | https://developer.android.com/develop/background-work/background-tasks/data-transfer-options | 공식 가이드 | background-work |
| S199 | Google | User-initiated data transfer jobs | https://developer.android.com/develop/background-work/background-tasks/uidt | 공식 가이드 | background-work |
| S200 | Google | Support for long-running workers (WorkManager) | https://developer.android.com/develop/background-work/background-tasks/persistent/how-to/long-running | 공식 가이드 | background-work |
| S201 | Google | Schedule alarms | https://developer.android.com/develop/background-work/services/alarms | 공식 가이드 | background-work |
| S202 | Google | Services overview | https://developer.android.com/develop/background-work/services | 공식 가이드 | background-work |
| S203 | Google | Bound services overview | https://developer.android.com/develop/background-work/services/bound-services | 공식 가이드 | background-work |
| S204 | Google | Test your Service | https://developer.android.com/training/testing/other-components/services | 공식 가이드 | background-work |
| S205 | Google | Notification runtime permission | https://developer.android.com/develop/ui/views/notifications/notification-permission | 공식 가이드 | background-work |
| S206 | Google | Behavior changes: all apps (Android 14) | https://developer.android.com/about/versions/14/behavior-changes-all | 공식 문서 | background-work |
| S207 | Google | Communicate in the background (BLE) | https://developer.android.com/develop/connectivity/bluetooth/ble/background | 공식 가이드 | background-work |
| S208 | Google | Companion device pairing | https://developer.android.com/develop/connectivity/bluetooth/companion-device-pairing | 공식 가이드 | background-work |
| S209 | Google | Optimize for Doze and App Standby | https://developer.android.com/training/monitoring-device-state/doze-standby | 공식 가이드 | background-work |
| S210 | Google | Understanding foreground service and full-screen intent requirements | https://support.google.com/googleplay/android-developer/answer/13392821 | Play Console 도움말 | background-work |
| S211 | Google | Device and Network Abuse (Permissions for Foreground Services 절) | https://support.google.com/googleplay/android-developer/answer/9888379 | Play 정책 | background-work |
| S212 | Google | Request runtime permissions | https://developer.android.com/training/permissions/requesting | 공식 가이드 | runtime-permissions |
| S213 | Google | Minimize your permission requests | https://developer.android.com/privacy-and-security/minimize-permission-requests | 공식 가이드 | runtime-permissions |
| S214 | Google | App permissions best practices | https://developer.android.com/training/permissions/usage-notes | 공식 가이드 | runtime-permissions |
| S215 | Google | Explain access to more sensitive information | https://developer.android.com/training/permissions/explaining-access | 공식 가이드 | runtime-permissions |
| S216 | Google | Compose and other libraries (Activity Result API·권한 절) | https://developer.android.com/develop/ui/compose/libraries | 공식 가이드 | runtime-permissions |
| S217 | Google | Accompanist — Jetpack Compose Permissions | https://google.github.io/accompanist/permissions/ | 오픈소스 문서 | runtime-permissions |
| S218 | Google | accompanist-permissions Maven 메타데이터(최신 0.37.3, 2025-04-28) | https://repo1.maven.org/maven2/com/google/accompanist/accompanist-permissions/maven-metadata.xml | 배포 메타데이터 | runtime-permissions |
| S219 | Google | Get a result from an activity (Activity Result API) | https://developer.android.com/training/basics/intents/result | 공식 가이드 | runtime-permissions |
| S220 | Google | State holders and UI state | https://developer.android.com/topic/architecture/ui-layer/stateholders | 공식 가이드 | runtime-permissions |
| S221 | Google | Bluetooth permissions | https://developer.android.com/develop/connectivity/bluetooth/bt-permissions | 공식 가이드 | runtime-permissions |
| S222 | Google | Request location permissions | https://developer.android.com/training/location/permissions | 공식 가이드 | runtime-permissions |
| S223 | Google | Location button | https://developer.android.com/guide/topics/permissions/private-alternatives/location-button | 공식 가이드(Jetpack 라이브러리는 experimental) | runtime-permissions |
| S224 | Google | Photo picker | https://developer.android.com/training/data-storage/shared/photopicker | 공식 가이드 | runtime-permissions |
| S225 | Google | Android 11 permissions changes | https://developer.android.com/about/versions/11/privacy/permissions | 공식 가이드 | runtime-permissions |
| S226 | Google | App hibernation and permission auto-reset | https://developer.android.com/topic/performance/app-hibernation | 공식 가이드 | runtime-permissions |
| S227 | Google Play | Permissions and APIs that Access Sensitive Information | https://support.google.com/googleplay/android-developer/answer/9888170 | 정책 | runtime-permissions |
| S228 | Google Play | User Data (Prominent Disclosure & Consent Requirement 절) | https://support.google.com/googleplay/android-developer/answer/10144311 | 정책 | runtime-permissions |
| S229 | Google Play | Best practices for prominent disclosure and consent | https://support.google.com/googleplay/android-developer/answer/11150561 | 정책 도움말 | runtime-permissions |
| S230 | Google Play | Understanding location in the background permissions | https://support.google.com/googleplay/android-developer/answer/9799150 | 정책 도움말 | runtime-permissions |
| S231 | Google | UI Automator | https://developer.android.com/training/testing/other-components/ui-automator | 공식 가이드 | runtime-permissions |
| S232 | Google | GrantPermissionRule (androidx.test:rules) | https://developer.android.com/reference/androidx/test/rule/GrantPermissionRule | API 레퍼런스 | runtime-permissions |
| S233 | Google | Storage Access Framework | https://developer.android.com/guide/topics/providers/document-provider | 공식 가이드 | runtime-permissions |
| S234 | Google | Activity 릴리스 노트 | https://developer.android.com/jetpack/androidx/releases/activity | 릴리스 노트 | runtime-permissions |
| S235 | Google | java.time.Clock (Android 레퍼런스) | https://developer.android.com/reference/java/time/Clock | 공식 API 레퍼런스 | time-handling |
| S236 | Google | java.time.ZoneId (Android 레퍼런스) | https://developer.android.com/reference/java/time/ZoneId | 공식 API 레퍼런스 | time-handling |
| S237 | Google | SystemClock (android.os) | https://developer.android.com/reference/android/os/SystemClock | 공식 API 레퍼런스 | time-handling |
| S238 | Google | java.time 패키지 요약 (Android 레퍼런스, API 26) | https://developer.android.com/reference/java/time/package-summary | 공식 API 레퍼런스 | time-handling |
| S239 | JetBrains | kotlinx-datetime README (0.8.0) | https://raw.githubusercontent.com/Kotlin/kotlinx-datetime/master/README.md | 오픈소스 README | time-handling |
| S240 | Google | java.util.Date (Android 레퍼런스) | https://developer.android.com/reference/java/util/Date | 공식 API 레퍼런스 | time-handling |
| S241 | Google | java.time.InstantSource (Android 레퍼런스, API 34) | https://developer.android.com/reference/java/time/InstantSource | 공식 API 레퍼런스 | time-handling |
| S242 | Google | java.text.SimpleDateFormat (Android 레퍼런스) | https://developer.android.com/reference/java/text/SimpleDateFormat | 공식 API 레퍼런스 | time-handling |
| S243 | Google (Android Lint) | Lint 점검 목록 | https://googlesamples.github.io/android-custom-lint-rules/checks/index.md.html | 공식 문서(lint 규칙집) | time-handling |
| S244 | detekt | Style rule set (docs/next, ForbiddenMethodCall·ForbiddenImport) | https://detekt.dev/docs/next/rules/style | 공식 문서 | time-handling |
| S245 | Google | java.time.Duration (Android 레퍼런스) | https://developer.android.com/reference/java/time/Duration | 공식 API 레퍼런스 | time-handling |
| S246 | Google | java.time.Period (Android 레퍼런스) | https://developer.android.com/reference/java/time/Period | 공식 API 레퍼런스 | time-handling |
| S247 | Google | java.time.ZonedDateTime (Android 레퍼런스) | https://developer.android.com/reference/java/time/ZonedDateTime | 공식 API 레퍼런스 | time-handling |
| S248 | Google | java.time.LocalDate (Android 레퍼런스) | https://developer.android.com/reference/java/time/LocalDate | 공식 API 레퍼런스 | time-handling |
| S249 | Google | java.time.temporal.ChronoUnit (Android 레퍼런스) | https://developer.android.com/reference/java/time/temporal/ChronoUnit | 공식 API 레퍼런스 | time-handling |
| S250 | Google | java.time.YearMonth (Android 레퍼런스) | https://developer.android.com/reference/java/time/YearMonth | 공식 API 레퍼런스 | time-handling |
| S251 | Google | Room — 복합 데이터 참조(타입 컨버터) | https://developer.android.com/training/data-storage/room/referencing-data | 공식 가이드 | time-handling |
| S252 | Google | Room3 릴리스 노트 | https://developer.android.com/jetpack/androidx/releases/room3 | 공식 릴리스 노트 | time-handling |
| S253 | Google | java.time.LocalDateTime (Android 레퍼런스) | https://developer.android.com/reference/java/time/LocalDateTime | 공식 API 레퍼런스 | time-handling |
| S254 | Google | java.time.Instant (Android 레퍼런스) | https://developer.android.com/reference/java/time/Instant | 공식 API 레퍼런스 | time-handling |
| S255 | Google | java.time.format.DateTimeFormatter (Android 레퍼런스) | https://developer.android.com/reference/java/time/format/DateTimeFormatter | 공식 API 레퍼런스 | time-handling |
| S256 | Google | android.text.format.DateFormat | https://developer.android.com/reference/android/text/format/DateFormat | 공식 API 레퍼런스 | time-handling |
| S257 | Google | AlarmManager 레퍼런스 | https://developer.android.com/reference/android/app/AlarmManager | 공식 API 레퍼런스 | time-handling |
| S258 | Google (Android Lint) | Lint 점검 SimpleDateFormat | https://googlesamples.github.io/android-custom-lint-rules/checks/SimpleDateFormat.md.html | 공식 문서(lint 규칙집) | time-handling |
| S259 | JetBrains | TestCoroutineScheduler (kotlinx-coroutines-test) | https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/-test-coroutine-scheduler/ | 공식 API 문서 | time-handling |
| S260 | Google | Migrate your Room database | https://developer.android.com/training/data-storage/room/migrating-db-versions | 공식 가이드 | room-migrations |
| S261 | Google | API 레퍼런스 androidx.room3.RoomDatabase.Builder | https://developer.android.com/reference/kotlin/androidx/room3/RoomDatabase.Builder | 공식 API 문서 | room-migrations |
| S262 | Google | API 레퍼런스 androidx.room3.Database | https://developer.android.com/reference/kotlin/androidx/room3/Database | 공식 API 문서 | room-migrations |
| S263 | Google | API 레퍼런스 androidx.room3.migration.Migration | https://developer.android.com/reference/kotlin/androidx/room3/migration/Migration | 공식 API 문서 | room-migrations |
| S264 | Google | API 레퍼런스 androidx.room3.testing.MigrationTestHelper | https://developer.android.com/reference/kotlin/androidx/room3/testing/MigrationTestHelper | 공식 API 문서 | room-migrations |
| S265 | Google(AOSP) | androidx 저장소 소스 room3 (androidx-main) | https://github.com/androidx/androidx/tree/androidx-main/room3 | 공식 저장소 소스 | room-migrations |
| S266 | Google | Room 2.x(Legacy) 가이드 | https://developer.android.com/training/data-storage/room/v2 | 공식 가이드 | room-migrations |
| S267 | Google | API 레퍼런스 androidx.room3.AutoMigration | https://developer.android.com/reference/kotlin/androidx/room3/AutoMigration | 공식 API 문서 | room-migrations |

노트 지역 번호 합계 130건(architecture 22 + state-nav 27 + kotlin-style 26 + testing-ci 43 + korea 12) → URL 중복 7건 제거 → 123건 → testing-ci 지역 S19가 getting-started 문서 3건을 한 번호로 묶고 있어 URL 단위로 분리(+2) → **전역 125건**. 제거된 중복은 S02(architecture+state-nav), S08(architecture+state-nav), S09(state-nav+kotlin-style), S35(kotlin-style+testing-ci), S43(state-nav+testing-ci), S51(architecture+testing-ci), S93(architecture+state-nav)이다.

S123~S125는 `testing-ci.md`가 리뷰 반영으로 개정된 뒤(지역 S19 확장 + 지역 S43 신설) 추가한 행이다. **S01~S122의 번호와 URL은 바꾸지 않았다** — 이미 인용된 번호가 어긋나지 않게 하려고 뒤에 이어 붙였으므로, 주제 순서와 번호 순서가 이 세 행에서만 어긋난다. S123·S124는 S76(의존성 추가)과 같은 getting-started 묶음이고, S125는 S72~S77과 같은 Konsist 문서군이다. `raw.githubusercontent.com/LemonAppDev/konsist-documentation/.../verify-classes.md`는 S125와 같은 문서의 원본이라 별도 번호를 주지 않았다. S161~S187은 2026-09-30 조사 노트 3건(release-cd · e2e-testing · dependency-updates)에서 규칙이 실제로 인용하는 출처만 옮긴 행이다. 노트의 나머지 출처와 후보 비교는 각 노트에 남아 있고, 기존 번호와 겹치는 문서(AGP 9.0.0 릴리스 노트 = S130, Sign your app = S134, NiA Build.yaml = S56, Compose 컴파일러 플러그인 = S48)는 기존 번호를 재사용한다.

S188~S267는 2026-09-30 조사 노트 4건(background-work · runtime-permissions · time-handling · room-migrations)에서 규칙이 실제로 인용하는 출처만 옮긴 행이다.

채택 결정 표·R-ID 겹침 정리·보류 표는 [`91-decisions.md`](91-decisions.md)로 옮겼다(파일당 300줄 한도, 2026-09-30).
