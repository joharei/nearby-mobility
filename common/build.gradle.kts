plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "app.reitan.common"
    compileSdk = Constants.CompileSdkVersion

    defaultConfig {
        minSdk = Constants.MinSdkVersion
        targetSdk = Constants.TargetSdkVersion
        consumerProguardFile("proguard-rules.pro")
    }
}

dependencies {
    implementation(libs.koin.core)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
}
