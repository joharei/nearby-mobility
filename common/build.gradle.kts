plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.serialization")
}

android {
    compileSdk = Constants.CompileSdkVersion

    defaultConfig {
        minSdk = Constants.MinSdkVersion
        targetSdk = Constants.TargetSdkVersion
        consumerProguardFile("proguard-rules.pro")
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(Koin.core)

    implementation(KotlinX.serialization.json)
    implementation(Ktor.client.cio)
    implementation(Ktor.client.json)
    implementation(Ktor.client.serialization)
    implementation(Ktor.client.logging)
}