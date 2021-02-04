@file:Suppress("unused", "UnstableApiUsage")

object Libs {
    val accompanist = Accompanist

    object Accompanist {
        const val insets = "dev.chrisbanes.accompanist:accompanist-insets:_"
    }
}

val AndroidX.Compose.Ui.uiTooling: String
    get() = "androidx.compose.ui:ui-tooling:_"

val Google.Android.PlayServices.maps: String
    get() = "com.google.android.gms:play-services-maps:_"