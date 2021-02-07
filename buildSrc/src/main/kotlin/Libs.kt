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

val AndroidX.Compose.Ui.uiTooling: String
    get() = "androidx.compose.ui:ui-tooling:_"

val Google.Android.PlayServices.maps: String
    get() = "com.google.android.gms:play-services-maps:_"