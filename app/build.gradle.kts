import io.github.reactivecircus.appversioning.toSemVer

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.app.versioning)
}

android {
    namespace = "app.reitan.nearby_mobility"
    compileSdk = libs.versions.compileSdkVersion.get().toInt()

    defaultConfig {
        applicationId = "app.reitan.nearby_mobility"
        minSdk = libs.versions.minSdkVersion.get().toInt()
        targetSdk = libs.versions.targetSdkVersion.get().toInt()

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

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (System.getenv("CI") == null) {
                signingConfig = getByName("debug").signingConfig
            }
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

appVersioning {
    overrideVersionName { gitTag, _, variantInfo ->
        val version = gitTag.rawTagName.drop(1)
        if (gitTag.commitsSinceLatestTag == 0) {
            version
        } else {
            "$version-${gitTag.commitsSinceLatestTag}-${gitTag.commitHash}"
        }.let {
            if (variantInfo.isDebugBuild) "$it-DEBUG" else it
        }
    }
    overrideVersionCode { gitTag, _, _ ->
        val semVer = gitTag.toSemVer(allowPrefixV = true)
        semVer.major * 10000000 + semVer.minor * 100000 + semVer.patch * 100 + gitTag.commitsSinceLatestTag
    }
}
