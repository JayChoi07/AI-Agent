plugins {
    alias(libs.plugins.convention.android.feature)
    alias(libs.plugins.convention.android.library.compose)
}

android {
    namespace = "{{package}}.feature.{{feature}}"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    testImplementation(projects.core.testing)
}
