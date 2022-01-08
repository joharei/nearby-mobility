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
    implementation("io.ktor:ktor-client-content-negotiation:_")
    implementation("io.ktor:ktor-serialization-kotlinx-json:_")
    implementation(Ktor.client.logging)
}
