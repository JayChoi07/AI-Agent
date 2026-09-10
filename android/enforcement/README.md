# enforcement — 규칙을 빌드로 강제하는 장치

> 적용: 그린필드 Android(Kotlin·Compose·Navigation 3 1.1.7·Hilt·멀티모듈), AGP 9.4.0 / Gradle 9.7.1 / JDK 17.
> 버전 근거는 `android/research/testing-ci.md` "확인 버전 표"(조사일 2026-09-09).

문서로 적은 규칙은 지켜지지 않는다. 이 폴더는 규칙 문서(`10~40번대`)의 R-ID 중 기계로 검사 가능한 것을
convention plugin · detekt · ktlint · Konsist · GitHub Actions 로 옮긴 것이다.

## 설치 순서

1. **build-logic 복사** — `build-logic/` 을 프로젝트 루트에 그대로 둔다.
2. **included build 등록 + 타입세이프 프로젝트 접근자** — 루트 `settings.gradle.kts` 최상단에 넣는다.
   ```kotlin
   pluginManagement {
       includeBuild("build-logic")
       repositories { google(); mavenCentral(); gradlePluginPortal() }
   }

   // 템플릿·문서가 쓰는 `projects.core.common` 형태의 접근자에 필요하다.
   // Gradle 9.7.1 에서도 incubating 이라 이 줄이 없으면 첫 feature 모듈에서 `Unresolved reference: projects` 가 난다.
   enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
   ```
3. **버전 카탈로그** — `build-logic/libs.versions.toml.snippet` 의 `[versions]` · `[libraries]` · `[plugins]`
   블록을 `gradle/libs.versions.toml` 에 병합한다. 별칭은 convention plugin 의 `libs.findLibrary(...)`
   문자열과 1:1이므로 바꾸지 않는다.
4. **루트 build.gradle.kts** — 컨벤션 플러그인이 `pluginManager.apply(id)` 로 적용하는 플러그인을 **전부**
   클래스패스에만 올린다. `build-logic` 은 이들을 `compileOnly` 로만 참조하므로 루트가 선언하지 않으면
   플러그인 적용 시점에 `NoClassDefFoundError`(예: `com/android/build/api/dsl/ApplicationExtension`)가 난다.
   실제 적용과 설정은 컨벤션 플러그인과 `Quality.kt` 의 `configureQuality()` 가 하고,
   R-10-11("`subprojects {}` / `allprojects {}` 로 설정을 주입하지 않는다")을 지키기 위한 구조다.
   ```kotlin
   plugins {
       alias(libs.plugins.android.application) apply false
       alias(libs.plugins.android.library) apply false
       alias(libs.plugins.kotlin.compose) apply false
       alias(libs.plugins.kotlin.serialization) apply false
       alias(libs.plugins.ksp) apply false
       alias(libs.plugins.hilt) apply false
       alias(libs.plugins.roborazzi) apply false
       alias(libs.plugins.detekt) apply false
       alias(libs.plugins.ktlint) apply false
   }
   ```
   `configureQuality()` 가 모듈마다 하는 일:
   - `dev.detekt` · `org.jlleitschuh.gradle.ktlint` 적용
   - `buildUponDefaultConfig.set(true)`, `config.setFrom(rootProject.file("config/detekt/detekt.yml"))`
     (detekt 2.0 확장은 전부 Gradle `Property` 라 대입이 아니라 `set()`)
   - `ktlint { version.set("1.8.0"); android.set(true) }` — 플러그인 기본 ktlint 버전이 패치 사이에도 바뀌므로 고정
5. **설정 파일 배치** — `config/detekt/detekt.yml` → 루트 `config/detekt/`, `.editorconfig` → 루트.
6. **아키텍처 테스트** — `konsist/ArchitectureTest.kt` 를 `:app/src/test/java/<패키지>/` 로 복사하고
   `package` 선언을 프로젝트 패키지로 바꾼다. `:app` 에 `testImplementation(libs.konsist)` 와
   `testImplementation(libs.junit4)` 를 추가한다(`convention.android.feature` 를 쓰는 모듈은 이미 포함).
7. **CI** — `.github/workflows/android-ci.yml` 을 그대로 복사한다.
8. **스크린샷 골든 커밋** — compose 컨벤션 플러그인이 Roborazzi 출력을 `<모듈>/src/test/screenshots/` 로 보낸다.
   `./gradlew recordRoborazziDebug` 로 만든 png 를 **VCS 에 커밋**해야 CI 의 `verifyRoborazziDebug` 가 돈다.
   (기본값인 `build/outputs/roborazzi` 에 두면 체크아웃마다 골든이 없어 항상 실패한다.)

## 모듈별 플러그인 적용 예

컨벤션 플러그인은 6종이다 — `convention.android.application` · `convention.android.application.compose` ·
`convention.android.library` · `convention.android.library.compose` · `convention.android.feature` ·
`convention.android.hilt`(R-10-10). `*.compose` 두 개는 각각 `com.android.application` / `com.android.library` 쪽
전용이라 서로 바꿔 쓸 수 없다.

```kotlin
// :app (Compose 화면을 직접 그린다면 application.compose 를 함께)
plugins {
    alias(libs.plugins.convention.android.application)
    alias(libs.plugins.convention.android.application.compose)
    alias(libs.plugins.convention.android.hilt)
}

// :feature:login
// convention.android.library.compose 는 com.android.library 를 적용할 뿐 compileSdk·minSdk·JVM·품질 설정을
// 하지 않는다. 반드시 library(또는 그것을 포함하는 feature) 컨벤션과 **짝으로** 쓴다(NiA 패턴).
// 단독으로 쓰면 compileSdk 가 없어 설정 단계에서 실패한다.
plugins {
    alias(libs.plugins.convention.android.feature)      // = library + hilt + 화면 계층 의존성
    alias(libs.plugins.convention.android.library.compose)
}

// :core:designsystem (Compose 는 쓰지만 feature 는 아닌 모듈)
plugins {
    alias(libs.plugins.convention.android.library)
    alias(libs.plugins.convention.android.library.compose)
}

// :core:data
plugins {
    alias(libs.plugins.convention.android.library)
    alias(libs.plugins.convention.android.hilt)
}
```

## R-ID ↔ 강제 장치 매핑

| R-ID | 규칙 | 강제 장치 | 위치 |
|---|---|---|---|
| R-11-01 | ui→domain→data 단방향, model 은 아무것도 참조 안 함 | Konsist `assertArchitecture` (4계층 `dependsOn` + 역방향 `doesNotDependOn`) | `konsist/ArchitectureTest.kt` |
| R-11-02 | Repository 인터페이스·구현 모두 data 계층 | Konsist `resideInPackage("..data..")` (Fake 제외) | `konsist/ArchitectureTest.kt` |
| R-12-01 | UiState 는 ui 패키지의 data class | Konsist `hasDataModifier` + `resideInPackage` | `konsist/ArchitectureTest.kt` |
| R-14-01 | ViewModel 은 `@HiltViewModel` + 생성자 주입 | Konsist `hasAnnotationOf` / `primaryConstructors` | `konsist/ArchitectureTest.kt` |
| R-14-02 | 필드 주입 금지(프레임워크 진입점 제외) | Konsist `withoutAnnotationOf` + `properties().assertFalse` | `konsist/ArchitectureTest.kt` |
| R-16-01 | UseCase 는 public 함수 `operator invoke` 하나 | Konsist `hasOperatorModifier` | `konsist/ArchitectureTest.kt` |
| R-20-01 | `!!` 금지 | detekt `potential-bugs: UnsafeCallOnNullableType` | `config/detekt/detekt.yml` |
| R-20-02 | 와일드카드 import 금지 | detekt `WildcardImport` + ktlint | `config/detekt/detekt.yml`, `.editorconfig` |
| R-20-04 | 줄 길이 100자 | detekt `MaxLineLength.maxLineLength` + `.editorconfig max_line_length` | `config/detekt/detekt.yml`, `.editorconfig` |
| R-21-01 | 함수 길이 상한 60줄 | detekt `LongMethod.allowedLines` | `config/detekt/detekt.yml` |
| R-21-02 | 파라미터 함수 5 / 생성자 6 | detekt `LongParameterList` | `config/detekt/detekt.yml` |
| R-22-01 | `GlobalScope` 금지 | detekt `GlobalCoroutineUsage` | `config/detekt/detekt.yml` |
| R-22-02 | 디스패처 주입 | detekt `InjectDispatcher` | `config/detekt/detekt.yml` |
| R-10-09 | 공통 빌드 설정은 build-logic included build | convention plugin 자체 | `build-logic/` |
| R-10-11 | `subprojects {}` / `allprojects {}` 주입 금지 | `configureQuality()` 가 `dev.detekt` · `org.jlleitschuh.gradle.ktlint` 를 모듈별로 적용 | `build-logic/convention/src/main/kotlin/Quality.kt` |
| R-31-02 | 워크플로 골격 checkout → setup-java(17) → setup-gradle | GitHub Actions 잡 단계 | `.github/workflows/android-ci.yml` |
| R-31-01 | 게이트 순서 ktlintCheck→detektDebug→테스트→assembleDebug | GitHub Actions 잡 단계 | `.github/workflows/android-ci.yml` |
| R-31-06 | 실패한 검사 리포트는 항상 업로드 | `actions/upload-artifact` (`if: always()`) | `.github/workflows/android-ci.yml` |
| R-30-03 · R-30-04 | Screen 마다 스크린샷 테스트, 골든은 record 로만 갱신하고 커밋 | `verifyRoborazziDebug` + compose convention plugin (골든은 `src/test/screenshots`) | 워크플로 · `AndroidLibraryComposeConventionPlugin.kt` |

## AGP 9 때문에 브리프에서 바꾼 것

AGP 9 는 convention plugin 을 깨뜨리는 변경이 여러 개 있다. 근거는 아래 URL.

- **`org.jetbrains.kotlin.android` 적용 제거.** AGP 9 는 built-in Kotlin 이 기본이며 이 플러그인은 새 DSL 과
  호환되지 않는다. 카탈로그 `[plugins]` 에서도 뺐다. `kotlin.plugin.compose` · `kotlin.plugin.serialization` ·
  `com.google.devtools.ksp` 는 그대로 적용한다("Built-in Kotlin replaces the kotlin-android plugin only").
- **`CommonExtension<*, *, *, *, *, *>` → `CommonExtension`.** 타입 파라미터가 제거됐다.
- **블록 메서드가 `CommonExtension` 에서 각 확장으로 이동.** 공통 코드에서는 `compileOptions { }` 대신
  `compileOptions.apply { }`, `defaultConfig { }` 대신 `defaultConfig.minSdk = …` 처럼 프로퍼티로 접근한다.
- **`com.android.build.gradle.LibraryExtension` 사용 금지.** 구 DSL 구현 클래스(`BaseExtension`,
  `LibraryExtension`, `AppExtension`)는 더 이상 노출되지 않아 `ClassCastException` 이 난다.
  `com.android.build.api.dsl.LibraryExtension` / `ApplicationExtension` 을 쓴다.
- **`android.kotlinOptions` → `kotlin.compilerOptions`.**
- **`compileOnly` 대상이 `com.android.tools.build:gradle-api`.** 공식 문서가 내부 구현 대신 공개 API 아티팩트를
  권한다.
- **`-Xcontext-parameters` 삭제.** Kotlin 2.4.20 릴리스 노트에 context parameters 안정화 언급이 없어 뺐다.
- **kapt 금지.** built-in Kotlin 과 `kotlin-kapt` 는 호환되지 않는다. Hilt 는 KSP 로만 붙인다.

## detekt 2.0 때문에 바꾼 것

- `build:` / `maxIssues` 블록이 없어졌다. 빌드 실패 임계값은 Gradle 확장의 `failOnSeverity`.
- 1.x 임계값 키가 전부 개명됐다: `threshold` → `allowedLines`(LongMethod) / `allowedDepth`(NestedBlockDepth) /
  `allowedConditions`(ComplexCondition) / `allowedComplexity`(CyclomaticComplexMethod),
  `functionThreshold`·`constructorThreshold` → `allowedFunctionParameters`·`allowedConstructorParameters`,
  `thresholdInClasses` → `allowedFunctionsPerClass`.
- `config.validation: true` 라서 존재하지 않는 키가 하나라도 있으면 detekt 가 실패한다. 위 키는 detekt main
  브랜치의 `default-detekt-config.yml` 에서 확인한 것만 썼다.
- **`ignoreAnnotated` 는 모든 규칙에 쓸 수 있는 범용 프로퍼티다.** 규칙별 기본 설정에 없어도 검증을 통과한다.
  근거는 `detekt-core` 의 `ConfigValidationKt` 가 들고 있는 제외 패턴 목록
  (`.*>.*>active`, `.*>.*>excludes`, `.*>.*>includes`, `.*>.*>severity`, `.*>.*>aliases`,
  `.*>.*>autoCorrect`, `.*>.*>ignoreAnnotated`, `.*>.*>ignoreFunction`)이다.
  실측으로도 확인했다: 없는 키 하나를 넣으면 `Property '…>bogusKeyXyz' is misspelled or does not exist` 로
  즉시 실패하지만 현재 설정은 통과하고, 파라미터 7개짜리 `@Composable` 은 면제되고 같은 함수에서
  `@Composable` 만 떼면 `LongParameterList` 가 뜬다. `ignoreAnnotatedParameter`(파라미터 단위) ·
  `MagicNumber.ignoreAnnotation`(애노테이션 인자 안의 숫자)은 뜻이 달라 대체재가 아니다.
- **타입 해석 주의.** `UnsafeCallOnNullableType`(R-20-01), `InjectDispatcher`(R-22-02),
  `GlobalCoroutineUsage`(R-22-01) 는 타입 해석이 있어야 동작한다. plain `detekt` 태스크에는 타입 해석이 없어
  이 규칙들이 **조용히 통과한다.** CI 는 variant 태스크 `detektDebug` 를 쓴다.
- `formatting:` 섹션은 없다. FORMATTER 확정값이 ktlint-gradle 단독이라 detekt-formatting 을 붙이지 않는다.

## 확인 못 한 것 (Task 16 스크래치 빌드에서 실증할 것)

1. ~~built-in Kotlin 의 확장 타입~~ — **해소됨.** `KotlinAndroidProjectExtension` 으로
   `kotlin { compilerOptions { } }` 설정이 AGP 9.4.0 에서 그대로 동작한다(스크래치 전 모듈 컴파일 성공).
2. ~~Konsist API 이름·경로~~ — **해소됨.** 공식 문서가 침묵한 이름들을 Konsist 공개 소스에서 직접 확인했다
   (`GET repos/LemonAppDev/konsist/contents/lib/src/main/kotlin/com/lemonappdev/konsist/api/**`).
   `dependsOn(layer, vararg layers, strict = false)`, `withoutAnnotationOf(kClass, vararg kClasses)`,
   `withoutNameContaining`, `primaryConstructors`(List 확장 val), `properties()`, `hasDataModifier`,
   `hasOperatorModifier` 모두 `api` 패키지의 공개 선언이다. import 경로는 전부 `…api.ext.list`.
3. ~~`hasAnnotationOf` vs `hasAnnotation`~~ — **해소됨.** 팩이 쓰는 `hasAnnotationOf(Inject::class)` 로
   `ArchitectureTest` 6개가 스크래치 빌드에서 통과한다(R-14-01·R-14-02 포함).
4. **`dependsOn` 은 금지 방향을 잡지 못한다** — 스크래치 빌드로 확인한 **정정 사항**. `strict = false` 가
   기본이라 "의존해도 된다"는 허용 선언일 뿐이고, `data` 가 `ui` 를 import 해도 통과한다(실측).
   그래서 `ArchitectureTest.kt` 에 `data.doesNotDependOn(ui, domain)` · `domain.doesNotDependOn(ui)` 를 넣었고,
   이 두 줄이 있어야 R-11-01 이 실제로 위반을 잡는다.
5. ~~detekt 2.0.0-alpha.6 × AGP 9.4.0~~ — **해소됨.** `detektDebug` 태스크가 등록되고 타입 해석이 동작하며
   (`UnsafeCallOnNullableType`·`InjectDispatcher` 검출 확인), `validation: true` 에서 `ignoreAnnotated` 도 통과한다.
6. ~~`gradle-api` 에 DSL 인터페이스가 다 들어있는지~~ — **해소됨.** `compileOnly(gradle-api)` 로 build-logic 이
   컴파일된다. 다만 실행 시점 클래스패스는 루트 `plugins { … apply false }` 가 공급해야 한다(설치 4단계).
7. ~~KSP 2.3.11 × built-in Kotlin, Hilt 2.60.1~~ — **해소됨.** KSP·Hilt 생성과 `assembleDebug` 까지 통과.
8. ~~Robolectric 4.16.1 의 최대 지원 SDK~~ — **해소됨.** 지원 상한은 36이고 **SDK 36 이미지는 Java 21을 요구**한다.
   팩은 JDK 17 · compileSdk 37 이므로 스크린샷 테스트에 `@Config(sdk = [35])` 를 붙였다(템플릿에 반영).
9. ~~줄 길이 불일치~~ — **해소됨.** `.editorconfig` `max_line_length` 와 detekt `MaxLineLength.maxLineLength`
   둘 다 **100** 으로 맞췄다(팩 확정값). detekt 기본값 120 을 덮어쓰는 설정이다.
10. ~~`configureQuality()` 의 확장 접근 시점~~ — **해소됨.** `pluginManager.apply(...)` 직후 같은 함수 안에서
    `extensions.configure` 로 잡는 방식이 스크래치 전 모듈에서 동작한다(`ktlintCheck`·`detektDebug` 등록·실행 확인).
    확장 타입은 `dev.detekt.gradle.extensions.DetektExtension`(interface, 멤버 전부 Gradle `Property`) ·
    `org.jlleitschuh.gradle.ktlint.KtlintExtension`(open class, `version`·`android` 모두 `Property`).
    다른 플러그인에서 같은 방식이 실패하면 `pluginManager.withPlugin("dev.detekt") { … }` 로 감싼다.

## 참고한 문서 URL

- AGP 9.0 릴리스 노트(구 DSL 제거·CommonExtension 파라미터 제거·built-in Kotlin): https://developer.android.com/build/releases/agp-9-0-0-release-notes
- AGP 릴리스 노트(최소 Gradle 9.6.0 · JDK 17 · buildTools 36.0.0 · 최대 API 37): https://developer.android.com/build/releases/gradle-plugin
- built-in Kotlin 마이그레이션(kotlinOptions→compilerOptions, kapt 비호환, "replaces the kotlin-android plugin only"): https://developer.android.com/build/migrate-to-built-in-kotlin
- AGP DSL/API 마이그레이션 타임라인(compileSdk/minSdk 정수 대입 유지): https://developer.android.com/build/releases/gradle-plugin-roadmap
- Gradle 플러그인 작성(convention plugin 은 `com.android.tools.build:gradle-api` 에 컴파일): https://developer.android.com/build/extend-agp
- Kotlin 2.4.20 릴리스 노트(context parameters 언급 없음 → `-Xcontext-parameters` 제외): https://kotlinlang.org/docs/whatsnew2420.html
- detekt 2.0 기본 설정 원본(모든 키·기본값의 근거): https://raw.githubusercontent.com/detekt/detekt/main/detekt-core/src/main/resources/default-detekt-config.yml
- detekt 2.0 Gradle(태스크 이름·타입 해석 범위·failOnSeverity): https://detekt.dev/docs/next/gettingstarted/gradle
- detekt 2.0 설정 구조(`config:` 블록, per-rule excludes/includes): https://detekt.dev/docs/next/introduction/configurations
- detekt 2.0 규칙셋: https://detekt.dev/docs/next/rules/complexity · https://detekt.dev/docs/next/rules/style · https://detekt.dev/docs/next/rules/potential-bugs · https://detekt.dev/docs/next/rules/coroutines · https://detekt.dev/docs/next/rules/naming
- detekt Compose 가이드(`ignoreAnnotated: ['Composable']` 가 범용 키임을 확인): https://detekt.dev/docs/next/introduction/compose
- Konsist 클래스 검증(`hasPrimaryConstructor`, `primaryConstructors`): https://docs.konsist.lemonappdev.com/veryfying-codebase/verify-classes.md
- Konsist 선언 어서션(`assertTrue`/`assertFalse`, `properties().assertFalse { it.hasAnnotationOf(Inject::class) }`): https://docs.konsist.lemonappdev.com/writing-tests/declaration-assert.md
- Konsist Android 스니펫(`withParentClassOf(ViewModel::class)`, `resideInPackage`): https://docs.konsist.lemonappdev.com/inspiration/snippets/android-snippets.md
- Konsist 아키텍처 어서션(`Layer`, `dependsOn`, `dependsOnNothing`): https://docs.konsist.lemonappdev.com/writing-tests/architecture-assert.md
- Konsist 공개 소스(문서가 침묵한 API 이름·시그니처의 최종 근거): https://github.com/LemonAppDev/konsist/tree/main/lib/src/main/kotlin/com/lemonappdev/konsist/api
  — `architecture/LayerDependencies.kt`, `ext/list/KoAnnotationProviderListExt.kt`, `ext/list/KoNameProviderListExt.kt`,
  `ext/list/KoPrimaryConstructorProviderListExt.kt`, `ext/list/KoPropertyProviderListExt.kt`,
  `provider/modifier/KoDataModifierProvider.kt`, `provider/modifier/KoOperatorModifierProvider.kt`
- GitHub Actions 버전(checkout v7 · setup-java v6 · setup-gradle v6 · upload-artifact v7): `android/research/testing-ci.md` 확인 버전 표

## 검증 기록 (2026-09-09)

스크래치 프로젝트(`:app` + `:core:{common,designsystem,model,testing}` + `:feature:login`)로 실빌드 검증했다.
- 버전: AGP 9.4.0 / Gradle 9.7.1 / JDK 17 / **compileSdk·targetSdk 37** / Kotlin 2.4.20 / KSP 2.3.11 /
  Hilt 2.60.1 / compose BOM 2026.08.00 / Nav3 1.1.7 / detekt 2.0.0-alpha.6 / ktlint 1.8.0(gradle 14.2.0) /
  Konsist 0.17.3 / Roborazzi 1.74.0 / Robolectric 4.16.1
- 통과 게이트(`clean` 후 CI 순서 그대로): `ktlintCheck` → `detektDebug` → `testDebugUnitTest verifyRoborazziDebug`
  (ArchitectureTest 6 · ViewModelTest 3 · ScreenshotTest 2, 전부 통과) → `assembleDebug`.
  MVI 변형으로 바꿔도 `:feature:login` 컴파일·detekt·테스트 통과.
- 위반 감지 확인: `data` → `ui` import 는 R-11-01 이, `!!` 는 detekt `UnsafeCallOnNullableType` 이 잡는다.
- 이번 검증으로 고친 파일: `build-logic/settings.gradle.kts`(gradlePluginPortal), `KotlinAndroid.kt`
  (compileSdk 37 · `failOnNoDiscoveredTests` off), `AndroidFeatureConventionPlugin.kt`(hilt-navigation-compose
  → hilt-lifecycle-viewmodel-compose), `AndroidLibraryComposeConventionPlugin.kt`(Roborazzi 골든 경로),
  `convention/build.gradle.kts`·`libs.versions.toml.snippet`(라이브러리 3개), `detekt.yml`
  (`UnusedPrivateFunction ignoreAnnotated: Preview`), `.editorconfig`(trailing comma 허용 · class-signature off),
  `konsist/ArchitectureTest.kt`(중첩 주석 버그 · `doesNotDependOn` 2줄), 이 README(설치 4·8단계),
  `templates/` 12개 파일(`ui/{{Feature}}Key.kt` 신설 포함). 고친 이유는 아래 "AGP 9 때문에 바꾼 것" ·
  "detekt 2.0 때문에 바꾼 것" 절과 이 절의 항목별 설명에 그대로 옮겨 두었다.
- 정적 리뷰 지적 5건 반영: `doesNotDependOn` 2줄 추가(리뷰 4번, 실측으로도 구멍 확인),
  `hilt-gradlePlugin`·`roborazzi-gradlePlugin` compileOnly 추가(리뷰 3번),
  `library.compose` 는 `library`/`application` 과 짝으로 써야 한다는 안내 추가(리뷰 5번).
  **리뷰 1·2번(`ignoreAnnotated` → `ignoreAnnotatedParameter` / `ignoreAnnotation`)은 반영하지 않았다.**
  현재 키가 검증도 동작도 통과함을 실측했고, 제안된 키는 의미가 달라 면제가 깨진다(위 detekt 절 참조).
- **2026-09-10 추가 — `AndroidApplicationComposeConventionPlugin`.** 검증 당시 `:app` 은 Compose 설정을 손으로
  적고 있었다(`kotlin.compose` + `buildFeatures.compose` + BOM·material3). `library.compose` 는
  `com.android.library` 를 적용해 앱 모듈에 못 쓰기 때문이다. 같은 스크래치에서 `:app` 의 손 설정을 지우고
  `convention.android.application.compose` 하나로 바꿔 `:app:assembleDebug` · `:app:testDebugUnitTest` 통과를 확인했다.
