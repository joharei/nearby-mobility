plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "app.reitan.nearby_mobility"
    compileSdk = libs.versions.compileSdkVersion.get().toInt()

    defaultConfig {
        applicationId = "app.reitan.nearby_mobility"
        minSdk = libs.versions.minSdkVersion.get().toInt()
        targetSdk = libs.versions.targetSdkVersion.get().toInt()
        versionCode = System.getenv("BUILD_NUMBER")?.toIntOrNull() ?: 1
        versionName = "1.0"

        resourceConfigurations += listOf("en", "nb", "nn")
        System.getenv("GOOGLE_MAPS_KEY")?.let {
            resValue("string", "google_maps_key", it)
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }

    signingConfigs {
        create("release") {
            if (System.getenv("CI") == "true") {
                storeFile = file(System.getenv("FCI_KEYSTORE_PATH"))
                storePassword = System.getenv("FCI_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("FCI_KEY_ALIAS")
                keyPassword = System.getenv("FCI_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig =
                if (System.getenv("CI") == "true") signingConfigs.getByName("release")
                else getByName("debug").signingConfig
        }

        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            isDebuggable = true
        }
    }
}

dependencies {
    implementation(project(":common"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    implementation(libs.google.android.play.services.location)
    implementation(libs.google.android.maps.compose)
    implementation(libs.google.android.maps.compose.utils)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.wear)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.foundation.layout)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.androidx.wear.compose.material)
    implementation(libs.activity.compose)

    implementation(libs.accompanist.permissions)
}
