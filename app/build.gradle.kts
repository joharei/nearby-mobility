plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "app.reitan.nearby_mobility"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        resConfigs("en", "nb", "nn")
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
            "-Xopt-in=dev.chrisbanes.accompanist.insets.ExperimentalAnimatedInsets"
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

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            isDebuggable = true
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(Google.android.wearable)
    implementation(Google.android.supportWearable)

    implementation(Google.android.playServices.wearOS)
    implementation(Google.android.playServices.maps)
    implementation(Google.android.playServices.location)

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