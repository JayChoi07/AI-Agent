// :baselineprofile — Baseline Profile 생성기와 매크로벤치마크를 가진 com.android.test 모듈 (R-41-17).
// R-10-01 의 예외 한 건이다. 공식 구조가 앱 모듈과 분리된 com.android.test 모듈을 요구한다(R-40-03).
// 이 모듈의 코드는 앱에 들어가지 않는다. 기기에서만 돌고 게이트(scripts/check.sh)는 포맷 검사만 한다.
//
// 컨벤션 플러그인 6종(R-10-10)에는 com.android.test 용이 없다. 사용처가 이 모듈 하나뿐이라
// 새 컨벤션 플러그인을 만들지 않고(R-10-14) 직접 적용한다. 그래서 build-logic 의 KotlinAndroid.kt
// (compileSdk·JVM 17·allWarningsAsErrors)와 Quality.kt(detekt·ktlint)가 하는 일을 여기서 되풀이한다.
// 그 파일들을 바꾸면 이 파일도 함께 본다.
//
// 아래 별칭 4개(android-test · androidx-baselineprofile · detekt · ktlint)와 라이브러리 별칭 2개는
// enforcement/build-logic/libs.versions.toml.snippet 에 있다. 루트 build.gradle.kts 에는
// android-test · androidx-baselineprofile 을 apply false 로 올려야 한다(templates/README.md).
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.androidx.baselineprofile)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

android {
    // 모듈마다 다른 namespace (R-19-03). 앱의 applicationId 와 섞지 않는다.
    namespace = "{{package}}.baselineprofile"
    // build-logic 의 COMPILE_SDK 와 같은 값이다 (R-19-01).
    compileSdk = 37

    defaultConfig {
        // 앱 minSdk(R-19-02)보다 높아도 된다 — 이 모듈은 앱에 들어가지 않고 시험 기기에서만 돈다.
        // 28 은 InfocarEV_Android 가 고른 값이고(시험 기기는 API 34 이상) 공식 문서로 확인하지 못했다(미확인).
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // 프로파일을 만들고 재는 대상 앱 모듈.
    targetProjectPath = ":app"
    // 매크로벤치마크는 앱과 따로 도는 프로세스에서 앱을 조작한다(공식 모듈 템플릿 그대로).
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        allWarningsAsErrors.set(true)
    }
}

detekt {
    buildUponDefaultConfig.set(true)
    parallel.set(true)
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
}

ktlint {
    // Quality.kt 의 KTLINT_VERSION 과 같은 값(FORMATTER 확정값).
    version.set("1.8.0")
    android.set(true)
}

dependencies {
    // BaselineProfileRule · MacrobenchmarkRule · UI Automator(modern DSL). junit4 도 이 아티팩트가 끌고 온다.
    implementation(libs.androidx.benchmark.macro.junit4)
    // @RunWith(AndroidJUnit4::class). 계측 테스트 그룹에 이미 있는 별칭이다.
    implementation(libs.androidx.test.ext.junit)
}
