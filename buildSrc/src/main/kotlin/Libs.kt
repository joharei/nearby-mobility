@file:Suppress("unused", "UnstableApiUsage")

object Libs {
    val accompanist = Accompanist

    object Accompanist {
        const val insets = "dev.chrisbanes.accompanist:accompanist-insets:_"
    }

    val koin = Koin

    object Koin {
        private const val groupPrefix = "org.koin:koin"
        const val core = "$groupPrefix-core:_"
        const val android = "$groupPrefix-android:_"
        val androidX = AndroidX

        object AndroidX {
            private const val artifactPrefix = "$groupPrefix-androidx"
            const val viewModel = "$artifactPrefix-viewmodel:_"
            const val compose = "$artifactPrefix-compose:_"
        }
    }
}

object Maps {
    const val mapsV3 = "com.google.android.libraries.maps:maps:_"
    const val mapsKtx = "com.google.maps.android:maps-v3-ktx:_"
    const val mapsUtils = "com.google.maps.android:android-maps-utils-v3:_"
}

val AndroidX.activityCompose: String
    get() = "androidx.activity:activity-compose:_"

val AndroidX.Compose.Ui.uiTooling: String
    get() = "androidx.compose.ui:ui-tooling:_"

val AndroidX.Lifecycle.viewModelCompose: String
    get() = "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha02"

val Google.Android.PlayServices.maps: String
    get() = "com.google.android.gms:play-services-maps:_"

val Google.Android.maps: Maps get() = Maps