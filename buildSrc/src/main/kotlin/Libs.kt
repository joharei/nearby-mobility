@file:Suppress("unused", "UnstableApiUsage")

object Libs {
    val koin = Koin

    object Koin {
        private const val groupPrefix = "io.insert-koin:koin"
        const val core = "$groupPrefix-core:_"
        const val android = "$groupPrefix-android:_"
        val androidX = AndroidX

        object AndroidX {
            private const val artifactPrefix = "$groupPrefix-androidx"
            const val compose = "$artifactPrefix-compose:_"
        }
    }
}

object Maps {
    const val mapsV3 = "com.google.android.libraries.maps:maps:_"
    const val mapsKtx = "com.google.maps.android:maps-v3-ktx:_"
    const val mapsUtils = "com.google.maps.android:android-maps-utils-v3:_"
}

val Google.Android.PlayServices.maps: String
    get() = "com.google.android.gms:play-services-maps:_"

val Google.Android.maps: Maps get() = Maps