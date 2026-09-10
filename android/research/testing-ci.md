# 테스트·CI/CD·강제 장치·성능/보안 최소 규칙 조사 노트
조사일: 2026-09-09

## 출처
| # | 조직 | 문서명 | URL | 종류 | 최종 갱신 |
|---|---|---|---|---|---|
| S1 | Google | Android 테스트 기초(Fundamentals) | https://developer.android.com/training/testing/fundamentals | 공식 가이드 | 미표기 |
| S2·S3 | Google | 테스트 더블 사용 / 로컬 단위 테스트 | https://developer.android.com/training/testing/fundamentals/test-doubles , https://developer.android.com/training/testing/local-tests | 공식 가이드 | 미표기 |
| S4·S5 | Google | Compose UI 테스트 / Compose Preview Screenshot Testing | https://developer.android.com/develop/ui/compose/testing , https://developer.android.com/studio/preview/compose-screenshot-testing | 공식 가이드(후자 알파) | 미표기 |
| S6 | Google | Kotlin 스타일 가이드 | https://developer.android.com/kotlin/style-guide | 공식 가이드 | 미표기 |
| S7·S8·S9 | Google | 앱 시작 시간(Vitals) / Baseline Profiles / Compose 성능 | https://developer.android.com/topic/performance/vitals/launch-time , https://developer.android.com/topic/performance/baselineprofiles/overview , https://developer.android.com/develop/ui/compose/performance | 공식 가이드 | 미표기 |
| S10 | Google | 보안 권장사항(Security tips) | https://developer.android.com/privacy-and-security/security-tips | 공식 가이드 | 미표기 |
| S11 | Google | 코드 리뷰 표준(eng-practices) | https://google.github.io/eng-practices/review/reviewer/standard.html | 공식 가이드 | 미표기 |
| S12·S13·S14 | Google | Now in Android — 아키텍처 문서 / `gradle/libs.versions.toml` / `.github/workflows/Build.yaml` | https://raw.githubusercontent.com/android/nowinandroid/main/docs/ArchitectureLearningJourney.md , https://raw.githubusercontent.com/android/nowinandroid/main/gradle/libs.versions.toml , https://raw.githubusercontent.com/android/nowinandroid/main/.github/workflows/Build.yaml | 오픈소스 문서·코드 | main |
| S15·S16·S17 | LemonAppDev | Konsist — 문서 홈 / Architecture Assertion / Declaration Assertion | https://docs.konsist.lemonappdev.com/ , https://docs.konsist.lemonappdev.com/writing-tests/architecture-assert.md , https://docs.konsist.lemonappdev.com/writing-tests/declaration-assert.md | 공식 문서 | 미표기 |
| S18 | LemonAppDev | Konsist — Android Snippets | https://docs.konsist.lemonappdev.com/inspiration/snippets/android-snippets.md | 공식 문서 | 미표기 |
| S19·S20 | LemonAppDev | Konsist — 의존성 추가 / 첫 테스트(선언 체크) / 두 번째 테스트(아키텍처 체크) 문서, Releases | https://docs.konsist.lemonappdev.com/getting-started/getting-started/add-konsist-dependency.md , https://docs.konsist.lemonappdev.com/getting-started/getting-started/create-first-konsist-test-declaration-check.md , https://docs.konsist.lemonappdev.com/getting-started/getting-started/create-secound-konsist-test-architectural-check.md , https://github.com/LemonAppDev/konsist/releases | 공식 문서·릴리스 노트 | v0.17.3 |
| S21·S22 | detekt | Getting started with Gradle / Releases | https://detekt.dev/docs/gettingstarted/gradle , https://github.com/detekt/detekt/releases | 공식 문서·릴리스 노트 | 2.0.0-alpha.6 |
| S23·S24 | ktlint | Code styles / ktlint 설정(.editorconfig), `documentation/release-latest` | https://raw.githubusercontent.com/pinterest/ktlint/master/documentation/release-latest/docs/rules/code-styles.md , https://raw.githubusercontent.com/pinterest/ktlint/master/documentation/release-latest/docs/rules/configuration-ktlint.md | 공식 문서 | master |
| S25 | JLLeitschuh | ktlint-gradle README / Releases | https://raw.githubusercontent.com/JLLeitschuh/ktlint-gradle/main/README.md , https://github.com/JLLeitschuh/ktlint-gradle/releases | 오픈소스 README | 14.2.0 |
| S26 | takahirom | Roborazzi README / 문서 / Releases | https://raw.githubusercontent.com/takahirom/roborazzi/main/README.md , https://takahirom.github.io/roborazzi/top.html , https://github.com/takahirom/roborazzi/releases | 오픈소스 README | 1.74.0 |
| S27·S28 | Cash App | Paparazzi / Turbine README·Releases | https://raw.githubusercontent.com/cashapp/paparazzi/master/README.md , https://github.com/cashapp/paparazzi/releases , https://raw.githubusercontent.com/cashapp/turbine/trunk/README.md , https://github.com/cashapp/turbine/releases | 오픈소스 README | 2.0.0-alpha05 / 1.2.1 |
| S29 | Robolectric | Releases | https://github.com/robolectric/robolectric/releases | 릴리스 노트 | 4.16.1 / 4.17-beta |
| S30 | Google | AGP 릴리스 노트 | https://developer.android.com/build/releases/gradle-plugin | 공식 문서 | AGP 9.4.0 |
| S31 | JetBrains | Kotlin 릴리스 | https://kotlinlang.org/docs/releases.html | 공식 문서 | 2.4.20 |
| S32·S33 | JetBrains / Google | KSP 시작하기 / KSP Releases | https://kotlinlang.org/docs/ksp-quickstart.html , https://github.com/google/ksp/releases | 공식 문서·릴리스 노트 | 2.3.11 |
| S34·S35 | Google | Compose BOM 매핑 / Compose 컴파일러 Gradle 플러그인 | https://developer.android.com/develop/ui/compose/bom/bom-mapping , https://developer.android.com/develop/ui/compose/compiler | 공식 문서 | 2026.08.00 |
| S36·S37 | Google | Dagger/Hilt Releases / androidx.hilt 릴리스 노트 | https://github.com/google/dagger/releases , https://developer.android.com/jetpack/androidx/releases/hilt | 릴리스 노트·공식 문서 | 2.60.1 / 1.4.0 |
| S38·S39 | Google | Navigation 3 / Lifecycle 릴리스 노트 | https://developer.android.com/jetpack/androidx/releases/navigation3 , https://developer.android.com/jetpack/androidx/releases/lifecycle | 공식 문서 | 1.1.7 / 2.11.0 |
| S40 | JetBrains | kotlinx.coroutines / kotlinx.serialization Releases | https://github.com/Kotlin/kotlinx.coroutines/releases , https://github.com/Kotlin/kotlinx.serialization/releases | 릴리스 노트 | 1.11.0 / 1.11.0 |
| S41 | Gradle | Gradle Releases | https://gradle.org/releases/ | 공식 문서 | 9.7.1 |
| S42 | GitHub / Gradle | actions/checkout·setup-java·upload-artifact, gradle/actions + setup-gradle README | https://github.com/actions/checkout/releases , https://github.com/actions/setup-java/releases , https://github.com/actions/upload-artifact/releases , https://github.com/gradle/actions/releases , https://raw.githubusercontent.com/gradle/actions/main/setup-gradle/README.md | 릴리스 노트·README | v7.0.1 / v6.0.0 / v7.0.1 / v6.3.0 |
| S43 | LemonAppDev | Konsist — Verify Classes (문서 사이트 `.md` + `konsist-documentation` 원본) | https://docs.konsist.lemonappdev.com/veryfying-codebase/verify-classes.md , https://raw.githubusercontent.com/LemonAppDev/konsist-documentation/main/veryfying-codebase/verify-classes.md | 공식 문서 | main |

## 핵심 규칙 (출처별)

### S1~S3 Google — 테스트 기초·더블·로컬 테스트
- 로컬(host-side)과 계측(instrumented)으로 나뉘며, 범위는 small(unit)/medium(integration)/big(e2e) 셋. "Medium tests: check the **integration** between two or more units." [S1]
- **피라미드 비율(70/20/10 등) 수치는 공식 문서에 없다.** 그림만 있고 숫자 없음 → 비율을 규칙화하면 근거 없는 수치가 된다. [S1]
- "With a testable app architecture, the code follows a structure that allows you to easily test different parts of it in isolation." 프레임워크 클래스에 로직을 두지 말고 인터페이스+DI로 교체 가능하게. [S1]
- 더블 6종(fake, mock, stub, dummy, spy, shadow) 중 **fake 우선**. "Fakes don't require a mocking framework and are lightweight. They are **preferred**." / "Fakes are preferred over stubs for simplicity." / "Fakes or mocks are therefore preferred over spies." fake는 프로덕션에 포함하지 않는다. [S2]
- 소스셋 `module-name/src/test/`, 의존성은 `testImplementation`. `unitTests.returnDefaultValues = true`는 최후 수단 — "might allow failing tests to pass." [S3]

### S4·S5 Google — Compose 테스트
- `createComposeRule()` 기본, 액티비티 필요 시 `createAndroidComposeRule<YourActivity>()`. `ui-test-junit4`는 `androidTestImplementation`, `ui-test-manifest`는 `debugImplementation`이며 `createComposeRule()`에만 필요. [S4]
- "Semantics give meaning to your UI, allowing tests to interact with specific elements." 동기화(idle 대기)가 별도 필수 개념. [S4]
- 공식 스크린샷 도구: 플러그인 `com.android.compose.screenshot` 0.0.1-alpha15+, AGP 9.0+(IDE 통합)/8.5.0+(태스크만), JDK 17+. 태스크 `updateDebugScreenshotTest`/`validateDebugScreenshotTest`, 소스셋 `screenshotTest`. **여전히 알파** — "subject to substantial changes during the alpha phase". [S5]

### S6 Google — Kotlin 스타일 가이드
- "Code has a column limit of 100 characters." 들여쓰기 4칸, 탭 금지. "Wildcard imports (of any type) are **not allowed.**" "Semicolons are **not** used." [S6]
- `@Composable`이면서 `Unit` 반환 함수는 명사형 PascalCase. 상수는 UPPER_SNAKE_CASE + `const`, 백킹 프로퍼티는 `_` 접두. 공개 타입/멤버에 KDoc, 태그 순서 `@constructor @receiver @param @property @return @throws @see`. [S6]

### S7·S8·S9 Google — 성능
- Vitals 과다 기준: "Cold startup takes 5 seconds or longer. Warm startup takes 2 seconds or longer. Hot startup takes 1.5 seconds or longer." TTID·TTFD 둘 다 추적하고 "always optimize based on an assumption of a cold start." [S7]
- Baseline Profile: 첫 실행부터 약 30% 향상, Startup Profile 약 15% 추가, R8 규칙 재작성 약 15% 추가(AGP 8.2+). 생성 변이는 `isMinifyEnabled = false`, 릴리스는 `true` + R8. 산출물 `assets/dexopt/baseline.prof`(최대 1.5MB), `androidx.profileinstaller` 필요. [S8]
- 측정은 **릴리스 + R8**에서 — "Debug mode ... imposes a performance cost". `remember`, lazy `key`, `derivedStateOf`, 람다로 상태 읽기 지연, `Modifier.offset { }`, "Never write to state that has already been read in a composable." [S9]

### S10 Google — 보안
- 민감 데이터는 내부 저장소. 외부 저장소는 "globally readable and writable ... only store non-sensitive information". [S10]
- `android:exported`를 항상 명시하고 기본 false. 서비스는 명시적 인텐트만(암시적 `bindService()`는 API 21+에서 예외). 권한 최소화 — "Minimize the number of permissions that your app requests." 커스텀 dangerous 권한 지양, 동일 개발사 IPC는 `signature`. [S10]
- HTTPS + network security config(`cleartextTrafficPermitted="false"`), localhost 포트 IPC 금지. WebView는 필요 없으면 `setJavaScriptEnabled()` 미호출, `addJavaScriptInterface()`는 APK 내 콘텐츠에만. [S10]
- AES-256 + GCM/HMAC-SHA-256 이상, 키는 KeyStore/Jetpack Security, `SecureRandom` 사용. API 키는 커밋 금지 → secrets-gradle-plugin + `local.properties`, 90일~6개월 로테이션. [S10]

### S11 Google — 코드 리뷰
- 승인 기준: "reviewers should favor approving a CL once it is in a state where it definitely improves the overall code health of the system". [S11]
- "there is no such thing as 'perfect' code—there is only *better* code." 선택적 의견은 `Nit: ` 접두. [S11]
- 원칙 4: 기술적 사실 > 개인 취향, 스타일은 스타일 가이드가 최종 권위, 설계는 공학 원칙 기반(동등한 대안이면 작성자 존중), 그 외엔 기존 코드베이스 일관성. [S11]

### S12·S13·S14 Google — Now in Android
- 레이어 3층(data/domain/ui), "Higher layers react to changes in lower layers. Events flow down. Data flows up." 목표에 "Facilitate local and instrumented tests ... using Continuous Integration (CI)" 포함. [S12]
- CI 액션: `actions/checkout@v4`, `actions/setup-java@v5`(Zulu **JDK 21**), `gradle/actions/setup-gradle@v4`, `actions/upload-artifact@v4`. [S14]
- CI 순서: lint(`:app:lintProdRelease`, `:lint:lint`) → `spotlessCheck` → `testDemoDebugUnitTest` → `verifyRoborazziDemoDebug` → `dependencyGuard` → `connectedDemoDebugAndroidTest` → `createDemoDebugCombinedCoverageReport`. [S14]
- NiA는 **Roborazzi 채택, Paparazzi 미채택**. detekt는 카탈로그에 없고 spotless+ktlint를 쓴다. [S13][S14]

### S15~S20 LemonAppDev — Konsist
- 아키텍처 검증은 `assertArchitecture`, 레이어는 `Layer(name, packagePattern)`, `..`는 하위 패키지 포함. `dependsOn`은 기본 `strict = false`, 무의존은 `dependsOnNothing()`. [S16]
- 선언 검증 어서션은 `assertTrue` / `assertFalse` / `assertEmpty` / `assertNotEmpty` 4종. 의존성은 `com.lemonappdev:konsist:0.17.3`. [S17][S19]
- 0.17.0에서 Layer의 `definedBy` → `rootPackage`, `isGenericType` → `isGeneric`, `KoInitializerProvider` → `KoIsInitializedProvider` 개명. [S20]

### S21~S25 detekt / ktlint
- detekt 최신은 **2.0.0-alpha.6**(플러그인 id `dev.detekt`), 1.x 최종 안정은 **1.23.8**(id `io.gitlab.arturbosch.detekt`, Kotlin 2.0.21 기준 빌드). 2.0.0-alpha.6은 Kotlin 2.4.10 / Gradle 9.6.1 / AGP 9.3.1 기준 빌드. [S21][S22]
- detekt 확장 옵션: `buildUponDefaultConfig`("Applies the config files on top of detekt's default config file"), `allRules`, `config`, `baseline`, `parallel`. 태스크 `detekt`, `detektGenerateConfig`, `detektBaseline`, `detektMain`/`detektTest`. 리포트는 xml/html/md/sarif. [S21]
- ktlint 코드 스타일 3종: `ktlint_official`(기본, "combines the best elements from the Kotlin Coding conventions and Android's Kotlin styleguide"), `intellij_idea`, `android_studio`. [S23]
- .editorconfig 규칙: `[*.{kt,kts}]` 아래 `ktlint_code_style`, `max_line_length`, 규칙 끄기는 `ktlint_standard_<rule> = disabled`, 실험 규칙은 `ktlint_experimental = enabled`. 하위 디렉터리 오버라이드 가능. [S24]
- ktlint-gradle는 id `org.jlleitschuh.gradle.ktlint`, 14.2.0. 태스크 `ktlintCheck` / `ktlintFormat`. **기본 ktlint 버전은 패치 릴리스 사이에도 바뀔 수 있다**("may change between patch versions") → `ktlint { version.set(...) }`로 고정 필요. [S25]

## 출처 간 충돌
| 주제 | 입장 A | 입장 B | 팩 추천과 근거 |
|---|---|---|---|
| 스크린샷 테스트 도구 | Roborazzi: Robolectric 기반, Hilt·상호작용 가능 [S26] | Paparazzi: LayoutLib 직접, Robolectric과 비호환, 2.0.0-alpha05가 "supports pre-AGP 9.0 consumers" [S27] | **Roborazzi**. AGP 9.4 환경에서 Paparazzi는 안정 릴리스가 없고 pre-AGP 9.0 문구가 걸림. NiA도 Roborazzi [S13] |
| 스크린샷 테스트 도구(2) | 서드파티 Roborazzi | Google 공식 `com.android.compose.screenshot` [S5] | 공식은 아직 **0.0.1-alpha15** → 팩 기본은 Roborazzi, 공식 플러그인은 "관찰" 상태로 문서화 |
| 정적 분석 | detekt 1.23.8(안정, Kotlin 2.0.21 빌드) [S22] | detekt 2.0.0-alpha.6(Kotlin 2.4.10 빌드) [S21] | Kotlin 2.4.20을 쓰면 1.23.8은 타입 해석에서 깨질 위험 → **2.0.0-alpha.6 + `dev.detekt`** 채택, 알파임을 명시 |
| 포맷터 | NiA는 spotless + ktlint [S13][S14] | ktlint-gradle 단독 [S25] | **ktlint-gradle 단독**. spotless는 다언어 포매팅까지 떠안아 팩 범위를 넓힘 |
| 테스트 더블 | Google은 fake 우선, 모킹 프레임워크 지양 [S2] | Google 로컬 테스트 문서는 mockito/mockk를 optional 의존성으로 열거 [S3] | **fake 기본**, mock은 인터랙션 검증이 목적일 때만 예외 허용 |
| 피라미드 비율 | 공식 수치 없음 [S1] | 업계 통설 70/20/10 | 팩에서 **비율 수치는 규칙화하지 않음**. "small 중심, medium/big은 대표 플로우만"으로 서술 |
| CI JDK | NiA는 JDK 21 [S14] | AGP 9.4.0 최소·기본 JDK 17 [S30] | **JDK 17**을 최소로 고정하고 CI도 17. 21은 Robolectric SDK 36 실행 시 필요할 수 있어 예외로 기재 |

## 팩 규칙 초안
| 후보 ID | 규칙 한 줄 | 근거 | 확신도 |
|---|---|---|---|
| R-30-01 | 비즈니스 로직 테스트는 `src/test`의 로컬 테스트로 작성하고 계측 테스트는 대표 플로우로 한정한다 | S1,S3 | 높음 |
| R-30-02 | 테스트 더블은 fake를 기본으로 하고, 모킹 라이브러리는 인터랙션 검증이 목적일 때만 쓴다 | S2 | 높음 |
| R-30-03 | fake는 프로덕션 소스셋에 두지 않는다 | S2 | 높음 |
| R-30-04 | `unitTests.returnDefaultValues = true`는 금지(최후 수단으로만 예외 승인) | S3 | 높음 |
| R-30-05 | Flow 테스트는 Turbine `test { awaitItem() … }`으로 하고 미소비 이벤트를 남기지 않는다 | S28 | 높음 |
| R-30-06 | Compose 테스트는 `createComposeRule()` 기본, 액티비티 필요 시에만 `createAndroidComposeRule<T>()` | S4 | 높음 |
| R-30-07 | `ui-test-manifest`는 `debugImplementation`으로만 추가한다 | S4 | 높음 |
| R-30-08 | 스크린샷 테스트는 Roborazzi(JVM)로 하고 `recordRoborazzi*` 결과를 소스 관리에 커밋한다 | S26,S13 | 높음 |
| R-30-09 | Roborazzi 테스트 클래스에는 `@GraphicsMode(GraphicsMode.Mode.NATIVE)`를 붙인다 | S26 | 중간 |
| R-30-10 | 테스트 클래스 이름은 `<대상>Test`로 통일한다 | S6 | 높음 |
| R-30-11 | 계측 테스트는 CI에서 별도 잡으로 분리하고 PR 필수 게이트에서 제외한다 | S14 | 중간 |
| R-30-12 | 커버리지는 게이트가 아니라 리포트로만 운영한다(임계값 미설정) | S14 | 낮음 |
| R-31-01 | CI는 checkout → setup-java(JDK 17, temurin) → `gradle/actions/setup-gradle` → 태스크 순서로 고정한다 | S14,S42 | 높음 |
| R-31-02 | Gradle 실행은 항상 래퍼(`./gradlew`)로 하고 `validate-wrappers`를 켠다 | S42 | 높음 |
| R-31-03 | PR 게이트 순서: `ktlintCheck` → `detekt` → `lintDebug` → `testDebugUnitTest` → `verifyRoborazziDebug` | S14,S21,S25 | 중간 |
| R-31-04 | 액션은 메이저 태그로 고정한다(checkout@v7, setup-java@v6, setup-gradle@v6, upload-artifact@v7) | S42 | 높음 |
| R-31-05 | 실패 시 리포트(HTML/SARIF/스크린샷 diff)를 `actions/upload-artifact`로 항상 업로드한다 | S14,S21 | 높음 |
| R-31-06 | Gradle 캐시는 기본 브랜치에서만 쓰기, PR에서는 `cache-read-only: true` | S42 | 중간 |
| R-31-07 | 아키텍처 테스트(Konsist)를 CI 필수 게이트에 포함한다 | S16 | 중간 |
| R-31-08 | 릴리스 빌드 검증은 R8 활성 상태로 별도 잡에서 수행한다 | S9,S8 | 중간 |
| R-32-01 | 리뷰 승인 기준은 "전체 코드 건강도를 확실히 개선하는가"이며 완벽함을 요구하지 않는다 | S11 | 높음 |
| R-32-02 | 선택적 지적은 `Nit:` 접두를 붙이고 머지를 막지 않는다 | S11 | 높음 |
| R-32-03 | 스타일 논쟁은 리뷰가 아니라 ktlint/detekt 설정으로 해결한다 | S11,S23 | 높음 |
| R-32-04 | 포매팅 지적은 자동화되어 있으므로 리뷰 코멘트로 남기지 않는다 | S11,S25 | 높음 |
| R-32-05 | 동등하게 타당한 설계 대안이 있으면 작성자 선택을 존중한다 | S11 | 높음 |
| R-32-06 | 컬럼 한계 100자, 와일드카드 import 금지, 세미콜론 금지를 린터로 강제한다 | S6,S24 | 높음 |
| R-32-07 | `@Composable`이며 `Unit` 반환인 함수는 명사형 PascalCase | S6 | 높음 |
| R-32-08 | 공개 API에는 KDoc을 붙이고 블록 태그 순서를 지킨다 | S6 | 중간 |
| R-40-01 | 성능 측정과 벤치마크는 릴리스 + R8 빌드에서만 수행한다 | S9 | 높음 |
| R-40-02 | 콜드 5초 / 웜 2초 / 핫 1.5초를 초과하면 회귀로 간주한다 | S7 | 높음 |
| R-40-03 | 릴리스 빌드에 Baseline Profile과 `androidx.profileinstaller`를 포함한다 | S8 | 높음 |
| R-40-04 | 프로파일 생성 변이는 `isMinifyEnabled = false`, 릴리스는 `true`로 둔다 | S8 | 높음 |
| R-40-05 | Compose에서 이미 읽은 상태에 되쓰기(backwards write)를 하지 않는다 | S9 | 높음 |
| R-40-06 | lazy 레이아웃에는 안정적인 `key`를 제공한다 | S9 | 높음 |
| R-40-07 | 자주 바뀌는 값은 람다 기반 modifier(`Modifier.offset { }`)로 읽기를 지연한다 | S9 | 중간 |
| R-40-08 | 모든 컴포넌트에 `android:exported`를 명시하고 기본은 false | S10 | 높음 |
| R-40-09 | 서비스는 명시적 인텐트로만 시작·바인딩한다 | S10 | 높음 |
| R-40-10 | 평문 HTTP를 금지하는 network security config를 기본 포함한다 | S10 | 높음 |
| R-40-11 | 민감 데이터는 내부 저장소 + KeyStore 기반 암호화(AES-256/GCM)로 저장한다 | S10 | 높음 |
| R-40-12 | API 키는 커밋하지 않고 `local.properties` + secrets 플러그인으로 주입한다 | S10 | 높음 |
| R-40-13 | WebView는 필요 없으면 JavaScript를 켜지 않고 `addJavaScriptInterface`를 쓰지 않는다 | S10 | 높음 |
| R-40-14 | 권한은 최소로 요청하고 커스텀 dangerous 권한을 만들지 않는다 | S10 | 높음 |

## 확인 버전 표
조사일 2026-09-09 기준. Gradle 래퍼는 **9.7.1**(AGP 9.4.0 최소 요구 9.6.0 충족) [S41][S30].

```toml
[versions]
agp = "9.4.0"
kotlin = "2.4.20"
ksp = "2.3.11"
composeBom = "2026.08.00"
composeScreenshot = "0.0.1-alpha15"
hilt = "2.60.1"
hiltExt = "1.4.0"
navigation3 = "1.1.7"
lifecycle = "2.11.0"
coroutines = "1.11.0"
serialization = "1.11.0"
turbine = "1.2.1"
konsist = "0.17.3"
detekt = "2.0.0-alpha.6"
ktlintGradle = "14.2.0"
ktlint = "1.8.0"
roborazzi = "1.74.0"
paparazzi = "2.0.0-alpha05"
robolectric = "4.16.1"
junit4 = "4.13.2"

[libraries]
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
# BOM이 버전을 주는 것: androidx.compose.ui:ui-test-junit4(androidTest), ui-test-manifest(debug)
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycle" }
androidx-navigation3-runtime = { group = "androidx.navigation3", name = "navigation3-runtime", version.ref = "navigation3" }
androidx-navigation3-ui = { group = "androidx.navigation3", name = "navigation3-ui", version.ref = "navigation3" }
androidx-hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltExt" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "serialization" }
junit4 = { group = "junit", name = "junit", version.ref = "junit4" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
robolectric = { group = "org.robolectric", name = "robolectric", version.ref = "robolectric" }
konsist = { group = "com.lemonappdev", name = "konsist", version.ref = "konsist" }
# roborazzi: roborazzi / roborazzi-compose / roborazzi-junit-rule 3개 모두 group=io.github.takahirom.roborazzi, version.ref="roborazzi"

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
detekt = { id = "dev.detekt", version.ref = "detekt" }
ktlint = { id = "org.jlleitschuh.gradle.ktlint", version.ref = "ktlintGradle" }
roborazzi = { id = "io.github.takahirom.roborazzi", version.ref = "roborazzi" }
compose-screenshot = { id = "com.android.compose.screenshot", version.ref = "composeScreenshot" }
```

| 항목 | 값 | 확인 URL |
|---|---|---|
| agp | 9.4.0 (최소 Gradle 9.6.0, JDK 17, buildTools 36.0.0, 최대 API 37) | https://developer.android.com/build/releases/gradle-plugin |
| gradle(wrapper) | 9.7.1 | https://gradle.org/releases/ |
| kotlin | 2.4.20 (2026-09-07) | https://kotlinlang.org/docs/releases.html |
| ksp | 2.3.11 (공식 퀵스타트가 Kotlin 2.4.20 + KSP 2.3.10 조합 제시 → 2.3.x 계열이 2.4.20과 호환) | https://github.com/google/ksp/releases , https://kotlinlang.org/docs/ksp-quickstart.html |
| composeBom | 2026.08.00 (ui/foundation 1.12.0, material3 1.4.0) | https://developer.android.com/develop/ui/compose/bom/bom-mapping |
| compose compiler plugin | `org.jetbrains.kotlin.plugin.compose`, 버전 = Kotlin 버전 | https://developer.android.com/develop/ui/compose/compiler |
| composeScreenshot | 0.0.1-alpha15 (알파) | https://developer.android.com/studio/preview/compose-screenshot-testing |
| hilt (Dagger) / hiltExt (androidx.hilt) | 2.60.1 / 1.4.0 | https://github.com/google/dagger/releases , https://developer.android.com/jetpack/androidx/releases/hilt |
| navigation3 / lifecycle | 1.1.7 (stable) / 2.11.0 | https://developer.android.com/jetpack/androidx/releases/navigation3 , https://developer.android.com/jetpack/androidx/releases/lifecycle |
| coroutines / serialization | 1.11.0 / 1.11.0 (serialization 1.12.0-RC는 RC라 제외) | https://github.com/Kotlin/kotlinx.coroutines/releases , https://github.com/Kotlin/kotlinx.serialization/releases |
| turbine | 1.2.1 | https://github.com/cashapp/turbine/releases |
| konsist | 0.17.3 | https://github.com/LemonAppDev/konsist/releases , https://docs.konsist.lemonappdev.com/getting-started/getting-started/add-konsist-dependency.md |
| detekt | 2.0.0-alpha.6 (id `dev.detekt`; Kotlin 2.4.10 / Gradle 9.6.1 / AGP 9.3.1 기준 빌드) | https://detekt.dev/docs/gettingstarted/gradle , https://github.com/detekt/detekt/releases |
| ktlintGradle / ktlint | 14.2.0 (`org.jlleitschuh.gradle.ktlint`) / 1.8.0 — 플러그인 기본 ktlint 버전이 바뀔 수 있어 명시 고정 | https://github.com/JLLeitschuh/ktlint-gradle/releases , https://github.com/pinterest/ktlint/releases |
| roborazzi / paparazzi | 1.74.0 / 2.0.0-alpha05 (paparazzi는 안정 릴리스 없음) | https://github.com/takahirom/roborazzi/releases , https://github.com/cashapp/paparazzi/releases |
| robolectric / junit4 | 4.16.1 (4.17-beta-4가 SDK 37 지원) / 4.13.2 | https://github.com/robolectric/robolectric/releases , https://raw.githubusercontent.com/android/nowinandroid/main/gradle/libs.versions.toml |
| GitHub Actions | checkout v7(7.0.1) · setup-java v6(6.0.0) · gradle/actions/setup-gradle v6(6.3.0) · upload-artifact v7(7.0.1) | https://github.com/actions/checkout/releases , https://github.com/actions/setup-java/releases , https://github.com/gradle/actions/releases , https://github.com/actions/upload-artifact/releases |

## Konsist API 확인본
버전 0.17.3 문서 기준. 아래는 문서 원문 스니펫 그대로.

**의존성** [S19]
```kotlin
testImplementation("com.lemonappdev:konsist:0.17.3")
```

**스코프 + 선언 필터** [S15]
```kotlin
Konsist
    .scopeFromProject() // Define the scope containing all Kotlin files present in the project
    .classes() // Get all class declarations
    .withNameEndingWith("UseCase")
```

**아키텍처 어서션** [S16] — 재사용 시 `architecture { }`로 프로퍼티에 담고 `scope.assertArchitecture(architecture)` 호출
```kotlin
Konsist
    .scopeFromProject()
    .assertArchitecture {
        val presentation = Layer("Presentation", "com.myapp.presentation..")
        val data = Layer("Data", "com.myapp.data..")
        presentation.dependsOn(data)
        data.dependsOnNothing()
    }
```
```kotlin
featureLayer.dependsOn(domainLayer) // strict = false by default
featureLayer.dependsOn(domainLayer, strict = true)
```

**어서션 4종** [S17] — `testName`, `strict`, `additionalMessage` 파라미터를 받는다
```kotlin
koScope.interfaces().assertTrue { it.hasPublicModifier() }
Konsist.scopeFromProject().properties().assertFalse { it.hasAnnotationOf(Inject::class) }
Konsist.scopeFromProject().properties().assertFalse { it.hasPublicOrDefaultModifier }
Konsist.scopeFromProject().classes().assertEmpty()
Konsist.scopeFromProject().classes().assertNotEmpty()
```

**Android 스니펫** [S18]
```kotlin
Konsist.scopeFromProject().classes().withParentClassOf(ViewModel::class)
    .assertTrue { it.name.endsWith("ViewModel") }
Konsist.scopeFromProject().classes().withNameEndingWith("Repository")
    .assertTrue { it.resideInPackage("..repository..") }
Konsist.scopeFromProject().functions().withAnnotationOf(Preview::class)
    .assertTrue { it.hasNameContaining("Preview") }
```

**확인된 이름 정리**
- 스코프·선언 접근: `Konsist.scopeFromProject()`·`.classes()` [S15], `.files` [S16], `.interfaces()`·`.properties()` [S17], `.functions()` [S18], `.parents()`·`.primaryConstructors` [S43]
- 리스트 필터(`with…`): `withNameEndingWith` [S15][S18], `withNameStartingWith` [S16], `withParentClassOf`·`withAnnotationOf` [S18], `withAllAnnotationsOf` [S19]
- 선언 술어(`has…`/`reside…`): `hasPublicModifier()`·`hasPublicOrDefaultModifier`(프로퍼티, 괄호 없음)·`hasAnnotationOf()` [S17], `hasNameContaining()`·`resideInPackage()` [S18], `hasNameEndingWith()`·`hasParentOf()`·`hasPrimaryConstructor`·`hasInternalModifier`·`hasCompanionModifier`·`hasTypeParameters()`·`hasTypeArguments()`·`isVal` [S43]
- 아키텍처: `assertArchitecture { }`, `architecture { }`, `Layer(name, packagePattern)`, `dependsOn(layer, strict)`, `dependsOnNothing()` [S16]
- **개명·폐기 이력** [S20]: `Layer.definedBy` → `rootPackage`(0.17.0), `isGenericType` → `isGeneric`(0.17.0), `KoInitializerProvider` → `KoIsInitializedProvider`(0.17.0), `baseSourceType` → `bareSourceType`(0.14.0), `asClassDeclaration` → `sourceDeclaration().asClassDeclaration()`(0.17.0), 0.16.0에서 다수 deprecated 항목 제거
- **문서에서 확인하지 못한 이름**: `withAllParentsOf`, `hasDataModifier`, `hasOperatorModifier`, 단수형 `primaryConstructor`. 아래 "미확인" 참조. 상속 필터는 문서에 실제로 등장하는 **`withParentClassOf(...)`** 를 쓸 것.

## Roborazzi vs Paparazzi
| 항목 | Roborazzi 1.74.0 | Paparazzi 2.0.0-alpha05 |
|---|---|---|
| 실행 환경 | JVM 단위 테스트(`src/test`), Robolectric 4.10+ 필요 | JVM, LayoutLib 직접(Robolectric 비사용) |
| Android 프레임워크 | Robolectric으로 프레임워크 동작 재현 → Hilt 주입·컴포넌트 상호작용 가능 [S26] | 프레임워크 미실행. "incompatible with Robolectric" [S26] |
| Compose 지원 | `roborazzi-compose` 아티팩트, Compose Test Rule 연동, `@GraphicsMode(NATIVE)` 필요 [S26] | `paparazzi.snapshot { MyComposable() }` [S27] |
| Gradle 태스크 | `recordRoborazziDebug` / `verifyRoborazziDebug` / `compareRoborazziDebug`, 프로퍼티(`roborazzi.test.record` 등)로도 실행 [S26] | `recordPaparazziDebug` / `verifyPaparazziDebug` [S27] |
| 안정성 | 1.74.0 정식 릴리스 | **안정 릴리스 없음**(2.0.0-alpha05), "supports pre-AGP 9.0 consumers" [S27] |
| CI 속도 | 에뮬레이터 불필요. Robolectric SDK jar 다운로드/네이티브 렌더링 비용 존재 | 에뮬레이터·Robolectric 모두 불필요해 이론상 더 가벼움 |
| 알려진 제약 | Robolectric 버전·SDK 조합에 묶임 | `LocalInspectionMode`를 전역 설정하지 않음, Lottie는 메인 스레드 강제 필요 [S27] |
| NiA 채택 | **채택**(`verifyRoborazziDemoDebug`가 CI 게이트) [S13][S14] | 미채택 |
| 팩 권고 | **기본 채택** | 채택하지 않음(AGP 9.4 조합 근거 부족) |

## 미확인·접근 불가
- **접근 불가(404)**: `docs.konsist.lemonappdev.com/getting-started/gettingstarted`, `pinterest.github.io/ktlint/latest/`와 `/1.8.0/rules/*`(버전 경로 미존재 → GitHub raw 문서로 대체), `raw.githubusercontent.com/cashapp/paparazzi/trunk/README.md`(기본 브랜치는 `master`), NiA `docs/TestingLearningJourney.md` — **NiA docs 디렉터리에 테스트 학습 문서가 존재하지 않음**(Architecture/Modularization/PDF만). NiA 테스트 전략은 `libs.versions.toml`·`Build.yaml`로만 확인함.
- **Maven Central solr API 신뢰 불가**: `search.maven.org/solrsearch` 3건 타임아웃, 성공한 2건 중 roborazzi가 1.44.0-alpha03을 최신으로 반환(실제 1.74.0). 버전은 GitHub Releases·공식 문서로만 확정함. 같은 이유로 **GitHub Releases에서 추출한 날짜는 연도가 어긋나 기재하지 않았고**, developer.android.com·kotlinlang.org·gradle.org 출처의 날짜만 남겼다.
- **Konsist 미확인 API**: `withAllParentsOf`는 웹 검색 요약에만 등장하고 공식 문서(`verify-classes.md` [S43], `android-snippets.md` [S18])에서 확인 실패. `hasDataModifier`, `hasOperatorModifier`, 단수형 `primaryConstructor`도 확인 실패(API 레퍼런스 `lemonappdev.github.io/konsist/...`는 미조회). Task 15/16의 `ArchitectureTest.kt`에서 이 4개는 쓰지 말고 확인된 `withParentClassOf` / `hasPrimaryConstructor` / `primaryConstructors`로 대체할 것.
- **버전 조합 리스크(빌드 전 검증 필요)**: ① KSP 2.3.11 + Kotlin 2.4.20 — 공식 퀵스타트는 2.4.20 + KSP 2.3.10 조합을 제시하며 2.3.11 조합의 명시적 확인 문구는 없음. ② detekt 2.0.0-alpha.6은 AGP 9.3.1 기준 빌드라 AGP 9.4.0에서 미검증. ③ Robolectric 4.16.1의 최대 지원 SDK가 35인지 36인지 릴리스 노트 서술이 엇갈림 — compileSdk 36+이면 `@Config(sdk = [...])` 조정 또는 4.17-beta 검토 필요. ④ Hilt 2.60.1의 Kotlin 2.4.20 / KSP 2.3.11 호환 명시는 확인 못 함.
- **미확정 항목**: androidx.test(core/runner/rules/ext-junit)는 NiA 카탈로그 값이 모두 `-rc01`이라 안정 버전 미확인(계측 테스트를 팩에 넣으려면 추가 조사). detekt 기본 규칙셋 임계값은 개별 규칙 페이지를 열지 않아 미확인 — `detekt.yml`은 `detektGenerateConfig` 산출물을 기준으로 삼을 것.
