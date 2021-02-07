plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.serialization")
}

android {
    compileSdkVersion(Constants.CompileSdkVersion)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(Constants.TargetSdkVersion)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlin.time.ExperimentalTime"
        )
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(Libs.koin.core)

    implementation(KotlinX.serialization.json)
    implementation(Ktor.client.cio)
    implementation(Ktor.client.json)
    implementation(Ktor.client.serialization)
    implementation(Ktor.client.logging)
}