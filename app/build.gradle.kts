import de.fayard.refreshVersions.core.versionFor

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "app.reitan.nearby_mobility"
    compileSdk = Constants.CompileSdkVersion

    defaultConfig {
        applicationId = "app.reitan.nearby_mobility"
        minSdk = Constants.MinSdkVersion
        targetSdk = Constants.TargetSdkVersion
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
        kotlinCompilerExtensionVersion = versionFor("version.androidx.compose.compiler")
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

    implementation(kotlin("stdlib"))
    implementation(KotlinX.coroutines.android)
    implementation(KotlinX.coroutines.playServices)
    implementation(Koin.core)
    implementation(Koin.android)
    implementation(Koin.compose)

    implementation(Google.android.playServices.location)
    implementation(Google.android.playServices.maps)
    implementation(Google.android.maps.ktx)
    implementation(Google.android.maps.utils)
    implementation(Google.android.maps.utils.ktx)

    implementation(AndroidX.core.ktx)
    implementation(AndroidX.wear)
    implementation(AndroidX.activity.ktx)
    implementation(AndroidX.fragment.ktx)
    implementation(AndroidX.lifecycle.viewModelCompose)

    // Compose
    implementation(AndroidX.compose.compiler)
    implementation(AndroidX.compose.runtime)
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.tooling)
    implementation(AndroidX.compose.foundation)
    implementation(AndroidX.compose.foundation.layout)
    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(AndroidX.wear.compose.material)
    implementation(AndroidX.activity.compose)
    implementation(Google.accompanist.insets)
    implementation(Google.accompanist.permissions)
}
