# 출처와 채택 결정

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

노트 지역 번호 합계 130건(architecture 22 + state-nav 27 + kotlin-style 26 + testing-ci 43 + korea 12) → URL 중복 7건 제거 → 123건 → testing-ci 지역 S19가 getting-started 문서 3건을 한 번호로 묶고 있어 URL 단위로 분리(+2) → **전역 125건**. 제거된 중복은 S02(architecture+state-nav), S08(architecture+state-nav), S09(state-nav+kotlin-style), S35(kotlin-style+testing-ci), S43(state-nav+testing-ci), S51(architecture+testing-ci), S93(architecture+state-nav)이다.

S123~S125는 `testing-ci.md`가 리뷰 반영으로 개정된 뒤(지역 S19 확장 + 지역 S43 신설) 추가한 행이다. **S01~S122의 번호와 URL은 바꾸지 않았다** — 이미 인용된 번호가 어긋나지 않게 하려고 뒤에 이어 붙였으므로, 주제 순서와 번호 순서가 이 세 행에서만 어긋난다. S123·S124는 S76(의존성 추가)과 같은 getting-started 묶음이고, S125는 S72~S77과 같은 Konsist 문서군이다. `raw.githubusercontent.com/LemonAppDev/konsist-documentation/.../verify-classes.md`는 S125와 같은 문서의 원본이라 별도 번호를 주지 않았다.

## 결정 표

`키`는 규칙 문서들이 참조하는 확정값 이름이다. 33건 모두 확정됐고 빈 칸은 없다. 아래 표의 `근거 출처`·`결정자` 칸이 선택지별 판단 근거를 그대로 담고 있으며, 짧은 요약은 `android/README.md`의 확정 결정 요약에 있다.

| 키 | 주제 | 선택지 | 결정 | 근거 출처 | 결정자 |
|---|---|---|---|---|---|
| MVI_IMPL | MVI 구현체 | 순수(ViewModel+StateFlow) / Orbit / Circuit | **순수 구현** — ViewModel + StateFlow + 단일 UiState, 라이브러리 의존 0. MVI가 필요한 화면만 같은 골격 안에 sealed Intent + `reduce` 함수 + Channel SideEffect를 더한다 | S02, S09, S91, S93, S98 | 사용자 |
| SCREENSHOT_LIB | 스크린샷 테스트 도구 | Roborazzi / Paparazzi / com.android.compose.screenshot | **Roborazzi 1.74.0**. 골든 경로는 `<모듈>/src/test/screenshots/`, 렌더링은 `@Config(sdk = [35])`(실빌드 실증 2026-09-09) | S87, S88, S34, S56, S46 | 사용자 |
| ERROR_TYPE | 에러 타입 | kotlin.Result / 커스텀 sealed / Arrow Either | **도메인별 커스텀 `sealed` 계층 + 단순 부재는 nullable**. `kotlin.Result`·Arrow 미채택 | S65, S63, S105 | 사용자 |
| USECASE_POLICY | UseCase 정책 | 항상 생성 / 로직 있을 때만 | **로직이 있을 때만 생성**. 승격 조건은 R-16-07(ViewModel 2개 이상 공유 또는 repository 2개 이상 조합) | S07, S02, S51 | 사용자 |
| FORMATTER | 포맷터 | ktlint-gradle 단독 / spotless+ktlint / detekt-formatting | **ktlint-gradle 14.2.0 단독**. ktlint 엔진 버전은 `ktlint { version.set("1.8.0") }`으로 고정. 최종 조문은 20-kotlin-style.md의 R-20-05가 소유하며 도구 중립이 아니라 ktlint 단독을 명시한다(초안의 R-20-22·ktfmt 언급은 폐기). ktlint-gradle는 Maven Central에 없어 저장소에 `gradlePluginPortal()`이 필요하다(실빌드 실증 2026-09-09) | S85, S56, S78, S83 | 사용자 |
| NAV3_VERSION | Navigation 3 버전 기준선 | 1.1.7 stable / 1.2.0-beta01 | **stable 1.1.7**. 결과 반환은 공유 상태·상위 ViewModel, 딥링크는 `DeepLinkPattern`+`KeyDecoder` 직접 구현 | S43, S26, S27, S28, S57 | 사용자 |
| DETEKT_LINE | detekt 계열 | 1.23.8 / 2.0.0-alpha.6 | **2.0.0-alpha.6, 플러그인 id `dev.detekt`**. AGP 9.4.0 조합은 스크래치 빌드로 실증됐다(실빌드 실증 2026-09-09). CI가 쓰는 태스크는 타입 해석이 있는 **`detektDebug`** — plain `detekt`는 타입 해석이 없어 `UnsafeCallOnNullableType`·`InjectDispatcher`·`GlobalCoroutineUsage`를 조용히 통과시킨다 | S78, S79, S80, S82, S66 | 사용자 |
| FEATURE_MODULE_SPLIT | feature 모듈 분할 기본값 | 단일 모듈 / api+impl 분할 | **단일 `:feature:*` 모듈로 시작**. 다른 feature가 키를 직접 참조해야 할 때만 그 feature를 api/impl로 분할하고, feature 간 이동은 콜백 + `:app` 조합층으로 한다 | S04, S24, S52, S03 | 사용자 |
| DI_FRAMEWORK | DI 프레임워크 | Hilt / Anvil / Metro | Hilt | S02, S05 vs S109, S103, S104 | 규칙 |
| BUILD_LOGIC_LOC | convention plugin 위치 | build-logic included build / buildSrc | build-logic included build | S53, S109 vs S69, S110 | 규칙 |
| FEATURE_BOUNDARY | feature 경계 결정 주체 | 화면 단위 / 비즈니스 로직 트리(RIBs) | 화면(또는 밀접한 화면군) 단위 | S04 vs S100 | 규칙 |
| DATA_IMPL | 데이터 계층 구현 | 직접 작성 repository / Store5 | 직접 작성 repository, Store5는 오프라인 동기화 요구 시 옵션 | S06 vs S101, S102 | 규칙 |
| STATE_HOLDER | 화면 상태 홀더 실행 주체 | AAC ViewModel / Compose 런타임 presenter | AAC ViewModel | S02, S08, S22 vs S93, S98, S91 | 규칙 |
| ONE_SHOT_EVENT | ViewModel→UI 일회성 이벤트 | UiState 환원 / Channel·SideEffect | UiState 환원. 유실돼도 무해한 Toast·햅틱만 예외. **MVI_IMPL이 허용한 Channel SideEffect는 MVI를 적용한 화면에 한정된 예외**이며, 12가 그 경계를 문장으로 못 박는다 | S02, S09, S41 vs S91 | 규칙 |
| NAV_LIBRARY | 내비게이션 라이브러리 | Navigation 3 / Navigation 2 | Navigation 3 (S29는 2025-05 시점 글이라 낡음) | S02, S43 vs S29 | 규칙 |
| BACKSTACK_API | 백스택 조작 추상화 | 리스트 직접 조작 / Navigator 래퍼 | `NavigationState`+`Navigator` 래퍼 | S25 vs S21 | 규칙 |
| UISTATE_GRANULARITY | 화면 상태 단위 | 단일 uiState / 스트림 분리 | 단일 기본, 분리는 근거 기록. `PagingData`는 항상 분리 | S02, S08 | 규칙 |
| NAV_RESULT | 화면 간 결과 전달 | 공유 상태 / ResultEventBus | NAV3_VERSION=1.1.7이므로 **ResultEventBus를 쓸 수 없다**(1.2.0-alpha02 이상 필요). 공유 상태·상위 ViewModel만 사용 | S27, S28, S43 | 규칙(NAV3_VERSION 확정 반영) |
| INDENT | 블록 들여쓰기 | 4 스페이스 / 2 스페이스 | 4 스페이스 | S35, S58 vs S106, S86 | 규칙 |
| LINE_LENGTH | 최대 줄 길이 | 100자 / 120자 | 100자. detekt `MaxLineLength`를 100으로 재설정 | S35, S106 vs S81 | 규칙 |
| KDOC_TAGS | KDoc `@param`/`@return` | 항상 태그 / 본문 통합 | 본문 통합 우선, 설명이 길 때만 태그 | S58 vs S35 | 규칙 |
| COLLECTION_ITER | 컬렉션 순회 | 고차 함수 / `for` | 변환은 `filter`·`map`, 단독 순회는 `for`(`forEach` 아님) | S58 | 규칙 |
| COMPLEXITY_NUMBERS | 복잡도 수치 임계값 | detekt 1.23.8 값 / 2.0.0-alpha.6 값 | DETEKT_LINE=2.0.0-alpha.6의 문서 확인 기본값을 그대로 쓴다. **함수 길이 60(`LongMethod`, 키 `allowedLines`) / 중첩 블록 깊이 4(`NestedBlockDepth`) / 파라미터 함수 5·생성자 6 / 복합조건 3 / 순환복잡도 14**. `MaxLineLength`만 LINE_LENGTH에 맞춰 100으로 재설정 | S80(2.0.0-alpha.6), S82(1.23.8 대조) | 규칙(도구 기본값, DETEKT_LINE 확정 반영) |
| TEST_DOUBLE | 테스트 더블 | fake / mock | fake 기본, mock은 인터랙션 검증이 목적일 때만 | S31 vs S32 | 규칙 |
| PYRAMID_RATIO | 테스트 피라미드 비율 | 70/20/10 수치화 / 수치 없음 | 수치를 규칙화하지 않는다. "small 중심, medium·big은 대표 플로우" | S30 | 규칙 |
| CI_JDK | CI JDK | 17 / 21 | 17 고정. Robolectric 4.16.1은 SDK 34·35가 Java 17, SDK 36이 Java 21을 요구하고 37은 미지원이므로, JDK 17을 지키는 스크린샷 렌더링 SDK는 35다(실빌드 실증 2026-09-09). SDK 36이 필요해지면 그 잡만 21로 올린다 | S46 vs S56 | 규칙 |
| OFFICIAL_SCREENSHOT | 공식 스크린샷 플러그인 취급 | 기본 채택 / 관찰 | 관찰 상태로만 문서화(0.0.1-alpha15) | S34 | 규칙(SCREENSHOT_LIB 종속) |
| UI_PATTERN | UI 패턴 | MVP / MVVM+UDF | MVVM+UDF. S117은 2019년 글이라 계층 원칙만 취한다 | S02, S08 vs S117 | 규칙 |
| DATASOURCE_SHAPE | DataSource 구성 | 단일 DataSource가 Remote/Cache 선택 / Remote·Local 분리 | Remote·Local 분리 | S06 vs S117 | 규칙 |
| ASYNC_TYPE | 비동기 노출 타입 | LiveData / StateFlow | StateFlow 단일화 | S41, S02 vs S122 | 규칙 |
| COMPOSE_SCOPE | Compose 적용 범위 | 신규 모듈 한정 / 전면 | 그린필드는 전면 Compose. 우회(AndroidView 래핑)는 근거와 재검토 시점 기록 시에만 | S111 | 규칙 |
| MODULE_5WAY | 기능당 5모듈 분할 | 채택 / 미채택 | 미채택. 700모듈 규모 전제이고 iOS 사례다 | S115 vs S04, S03 | 규칙 |
| ENFORCEMENT_ORDER | 규칙 집행 수단 순서 | 린터만 / 린터+CI+에이전트 | 린터(기계 판정) → CI 게이트 → 에이전트 주입(판단 필요 규칙) | S112, S114, S39 | 규칙 |

## R-ID 중복·겹침 정리

조사 노트가 각자 붙인 후보 ID 중 같은 규칙을 가리키는 것들이다. 원칙: **UiState 형태·상태 홀더 규칙은 12, 계층 의존 규칙은 11, Compose 컴포저블 규칙은 17이 소유**한다. 소유 파일이 아닌 파일은 그 규칙을 다시 쓰지 않고 참조만 한다.

> **이 표의 번호는 조사 노트의 초안(DRAFT) 후보 ID이고, 최종 `references/` 파일의 R-ID와 일치하지 않는다.** 규칙을 찾을 때는 최종 파일을 보고 이 표는 "초안에서 어느 파일이 그 주제를 가져갔나"의 기록으로만 읽는다. 특히 초안 R-11-04~R-11-08은 최종 11-architecture-layers.md에서 전혀 다른 규칙(Android 타입 의존 금지·data의 상위 참조 금지·계층 간 통신·모델 매핑·`AndroidViewModel` 금지)이 쓰고 있고, 초안 R-10-11·R-13-09가 가리키던 feature 모듈 조건부 분할 규칙은 최종 **R-10-13**이다(최종 R-10-11은 `subprojects {}`·`allprojects {}` 주입 금지).

| 겹치는 후보 ID | 관계 | 소유 파일 | 처리 |
|---|---|---|---|
| R-11-04 ↔ R-12-04 + R-12-05 | 동일(화면=ViewModel, 재사용=plain holder) | 12 | 11에서 삭제 |
| R-11-05 ↔ R-12-03 | 동일(단일 uiState + 관련 상태 묶기) | 12 | 11에서 삭제 |
| R-11-06 ↔ R-12-11 ↔ R-23-12 | 동일(일회성 이벤트 push 금지) | 12 | 11·23에서 삭제 |
| R-11-08 ↔ R-12-06 | 동일(`collectAsStateWithLifecycle` 수집) | 12 | 11에서 삭제 |
| R-11-07 (`AndroidViewModel` 금지) | 대응 없음. 프레임워크 의존 차단이므로 계층 규칙 | 11 | 유지. 12는 재기술 금지 |
| R-12-13 ↔ R-17-19 | 동일(호이스팅 위치). 17이 3원칙으로 더 완전 | 17 | 12에서 삭제 |
| R-12-14 (애니메이션 suspend 스코프) | Compose 전용 규칙 | 17 | 12 → 17 이관 |
| R-12-17 ↔ R-22-02 ↔ R-15-05 | 대상이 다름(상태 홀더 / suspend 함수 / data 계층) | 12·22·15 | 각자 유지, 문장에 대상 명시 |
| R-00-03 ↔ R-11-02 ↔ R-12 전반 | 원칙(UDF) vs 적용(의존 방향·상태 흐름) | 00·11 | 12는 참조만 |
| R-00-04 ↔ R-12-01 | 원칙(불변 노출) vs 적용(UiState 형태) | 00·12 | 둘 다 유지, 12가 UiState 문장 소유 |
| R-00-06 ↔ R-15-05 / R-16-06 / R-22-02 | 원칙(main-safe) vs 계층별 적용 | 00 원칙 1줄, 나머지는 계층 파일 | 유지 |
| R-00-08 ↔ R-30-02 ↔ R-15-12 | 동일(fake 우선) | 30 | 00은 원칙 1줄, 15는 data 테스트 구성만 |
| R-11-03 ↔ R-15-04 ↔ R-22-05 | 다름(계층 간 통신 / data 노출 형태 / 코루틴 사용법) | 11·15·22 | 유지 |
| R-11-10 ↔ R-20-05 · R-20-09 | 다름(아키텍처 네이밍 / 언어 네이밍) | 11·20 | 유지 |
| R-10-11 ↔ R-13-09 | 해소됨. FEATURE_MODULE_SPLIT=단일 시작이 초안 R-10-11의 조건부 분할을 채택 | 10 | **완료. 흡수처의 최종 번호는 R-10-13**("`:feature:*`는 단일 모듈로 시작하고 조건이 맞을 때만 나눈다"). 초안 R-13-09는 독립 규칙으로 쓰지 않았고 최종 13-navigation.md에 없다 |
| R-20-22 ↔ R-32-03 · R-32-06 | 해소됨. FORMATTER=ktlint-gradle 단독 | 20 | **완료.** 포맷터 조문은 최종 파일에서 **R-20-05**("포맷은 손이 아니라 포맷터가 정한다")가 소유한다. 초안의 ktfmt 언급과 2-스페이스 전제는 지웠고 INDENT·LINE_LENGTH를 따른다. 초안 번호 R-20-22는 최종 파일에 없다 |
| R-21-02 · R-21-06 · R-21-08 | 해소됨. DETEKT_LINE=2.0.0-alpha.6 | 21 | 파라미터 5·6 / 복합조건 3 / 순환복잡도 14로 확정 기입 |
| R-30-08 · R-30-09 | 해소됨. SCREENSHOT_LIB=Roborazzi 1.74.0 | 30 | 초안 문장 그대로 확정. `@GraphicsMode(NATIVE)` 포함 |
| R-13-02 · R-13-13 · R-13-15 | 해소됨. NAV3_VERSION=1.1.7 | 13 | 1.1.7 고정, 결과는 공유 상태, 딥링크는 직접 구현으로 확정 |
| R-12-15 · R-12-16 | 해소됨. MVI_IMPL=순수 구현 | 12 | MVI 라이브러리 미도입. R-12-15는 "라이브러리 없이 sealed Intent+`reduce`를 더한다"로 재작성 |
| R-16-01 · R-16-07 | 해소됨. USECASE_POLICY=로직 있을 때만 | 16 | 초안 문장 그대로 확정 |
| R-23-02 · R-23-03 · R-23-04 · R-23-05 | 해소됨. ERROR_TYPE=커스텀 sealed + nullable | 23 | 초안 문장 그대로 확정. `kotlin.Result`·Arrow는 규칙에서 배제 |

## 보류 (출처 없는 관행)

| 관행 | 어디서 왔나 | 왜 보류 |
|---|---|---|
| 컬렉션 체이닝 단계 상한 | kotlin-style R-21-13 | S58은 포맷만, S60은 "multiple steps"만. detekt에 체인 길이 규칙 없음. 전 출처 침묵 |
| 함수 길이·중첩 깊이·파라미터 수 상한을 **언어/스타일 권위가 정해주지 않는다** | kotlin-style R-21-01·R-21-02·R-21-03 | 수치 자체는 보류가 아니다. detekt 문서 확인 기본값(S80)을 COMPLEXITY_NUMBERS에서 확정했다. 보류로 남는 것은 S35·S58·S106이 어떤 수치도 제시하지 않아 **린터 기본값을 그대로 채택한 것이 팩의 판단**이라는 점뿐이다. 팀이 다른 수치를 원하면 근거 없이 바꿔도 되는 자리 |
| `!!` 전면 금지 | kotlin-style R-23-01 | S62는 금지하지 않고 조건부 허용, S106은 초기화 보장 인스턴스 변수에만. 전면 금지를 명시한 출처 없음 |
| `when`의 `else` 필수 | kotlin-style 질문표 | S106(Kodeco)만 명시. S35·S58 침묵 |
| Sequence 전환 임계(크기·단계 수) | kotlin-style R-21-14 | S60이 수치 제시를 명시적으로 거부("decide which one is better for your case") |
| 테스트 피라미드 비율(70/20/10) | testing-ci 충돌표 | S30에 그림만 있고 수치 없음. 업계 통설 |
| 커버리지 임계값 % | testing-ci R-30-12 | NiA(S56)는 리포트만 생성하고 게이트가 없음. 임계값 근거 출처 없음 |
| "예외 삼키기 금지" 정적 규칙 | kotlin-style 미확인 | detekt exceptions 룰셋 페이지 조회 실패. `SwallowedException` 기본값 미확인 |
| ktlint 스타일별 `max_line_length` 기본값 | kotlin-style 미확인 | pinterest.github.io 문서 404. 스타일별 기본값 확인 실패 |
| MVI 도입 판단선("상태 전이 검증 필요 시") | state-nav 섹션 3 | 노트의 종합 해석. 이 기준선을 문장으로 제시한 출처 없음 |
| Nav3 `entry` 안의 `hiltViewModel()` 사용 | state-nav R-13-07 | **해소됨.** 공식 navigation-3 스킬의 Hilt 연동 레시피가 `entry` 안 `hiltViewModel()` + `rememberViewModelStoreNavEntryDecorator()` 조합을 제시하고, 스크래치 빌드로 동작을 확인했다(2026-09-09) |
| Mavericks "저활동" 판정 | state-nav 비교표 | 릴리스·push 날짜에서 추론. 공식 유지보수 선언 미확인(사이트 본문 비어 반환) |
| Konsist `withAllParentsOf`·`hasDataModifier`·`hasOperatorModifier`·단수형 `primaryConstructor` | testing-ci 미확인 | **부분 해소.** `hasDataModifier`·`hasOperatorModifier`는 Konsist 0.17.3 공개 소스로 존재를 확인했고 `enforcement/konsist/ArchitectureTest.kt`가 실제로 쓴다(스크래치 빌드 통과 — 검증 기록은 `enforcement/README.md`). 보류로 남는 것은 `withAllParentsOf`·단수형 `primaryConstructor` 둘뿐이고, 이 둘은 `withParentClassOf`·`hasPrimaryConstructor`·`primaryConstructors`로 대체한다 |
| androidx.test(core/runner/rules/ext-junit) 안정 버전 | testing-ci 미확정 | NiA 카탈로그 값이 전부 `-rc01`. 계측 테스트를 팩에 넣으려면 추가 조사 필요 |
| 버전 조합 검증(KSP 2.3.11+Kotlin 2.4.20, detekt 2.0.0-alpha.6+AGP 9.4.0, Robolectric 4.16.1+SDK 36, Hilt 2.60.1+Kotlin 2.4.20) | testing-ci 리스크 목록 | **해소됨(실빌드 실증 2026-09-09).** 공식 문서 명시는 여전히 없지만 스크래치 빌드로 전부 통과 확인. 단 Robolectric×SDK 36은 Java 21을 요구해 SDK 35로 내렸고, compileSdk는 36→**37**, Gradle 래퍼는 **9.7.1**로 올려야 했다 |
| 국내 사례(P1~P13)의 조문 승격 | korea 요약 | 12건 중 문서화된 스타일 가이드 0건. 전부 경험 공유 글이라 단일 조직 1~2건 근거 |
| 에이전트 동시 주입 규칙 수 상한(2개·4개) | korea P7 | 근거가 S114 한 건. 다른 조직 교차 확인 없음 |
