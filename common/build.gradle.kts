plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.serialization")
}

android {
    compileSdk = Constants.CompileSdkVersion

    defaultConfig {
        minSdk = 21
        targetSdk = Constants.TargetSdkVersion
        consumerProguardFile("proguard-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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