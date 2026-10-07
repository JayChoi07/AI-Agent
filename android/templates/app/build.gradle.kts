// 루트 빌드 스크립트. 컨벤션 플러그인이 pluginManager.apply(id) 로 적용하는 플러그인을 클래스패스에만 올린다.
// 실제 적용·설정은 build-logic 의 컨벤션 플러그인과 Quality.kt 가 한다 — 그래야 subprojects {} / allprojects {} 로
// 설정을 주입하지 않는다 (R-10-11). 목록과 별칭은 enforcement/README.md "설치 순서" 4단계와 동일해야 한다.
// 버전은 전부 gradle/libs.versions.toml 한 곳에서 온다 (R-10-12).
// AGP 9 은 KGP 2.2.10 을 런타임 의존으로 갖는다. 그보다 높은 KGP 는 최상위 빌드 파일의 classpath 로 명시한다 (R-19-15).
// 이 선언이 없으면 kotlin.compose 같은 다른 플러그인의 전이 의존이 우연히 버전을 끌어올린다.
buildscript {
    dependencies {
        classpath(libs.kotlin.gradlePlugin)
    }
}

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
