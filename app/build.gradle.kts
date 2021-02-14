plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(Constants.CompileSdkVersion)

    defaultConfig {
        applicationId = "app.reitan.nearby_mobility"
        minSdkVersion(23)
        targetSdkVersion(Constants.TargetSdkVersion)
        versionCode = System.getenv("BUILD_NUMBER")?.toIntOrNull() ?: 1
        versionName = "1.0"

        resConfigs("en", "nb", "nn")
        System.getenv("GOOGLE_MAPS_KEY")?.let {
            resValue("string", "google_maps_key", it)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        useIR = true
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-Xopt-in=androidx.compose.runtime.ExperimentalComposeApi",
            "-Xopt-in=dev.chrisbanes.accompanist.insets.ExperimentalAnimatedInsets",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlin.time.ExperimentalTime"
        )
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        val versionsProperties = rootProject.propertiesFromFile("versions.properties")
        kotlinCompilerExtensionVersion =
            versionsProperties.getProperty("version.androidx.compose.foundation")
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
            signingConfig = signingConfigs.getByName("release")
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
    implementation(Libs.koin.core)
    implementation(Libs.koin.android)
    implementation(Libs.koin.androidX.viewModel)
    implementation(Libs.koin.androidX.compose)

    compileOnly(Google.android.wearable)
    implementation(Google.android.supportWearable)

    implementation(Google.android.playServices.wearOS)
    implementation(Google.android.playServices.location)
    implementation(Google.android.playServices.maps)
    implementation(Google.android.maps.mapsKtx)
    implementation(Google.android.maps.mapsUtils)
    implementation(Google.android.maps.mapsUtilsKtx)

    implementation(AndroidX.core.ktx)
    implementation(AndroidX.wear)

    // Compose
    implementation(AndroidX.compose.runtime)
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.uiTooling)
    implementation(AndroidX.compose.foundation)
    implementation(AndroidX.compose.foundation.layout)
    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.material.icons.core)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(Libs.accompanist.insets)
}