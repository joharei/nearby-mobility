@file:Suppress("unused", "UnstableApiUsage")

object Maps {
    const val mapsKtx = "com.google.maps.android:maps-ktx:_"
    const val mapsUtils = "com.google.maps.android:android-maps-utils:_"
    const val mapsUtilsKtx = "com.google.maps.android:maps-utils-ktx:_"
}

val Google.Android.PlayServices.maps: String
    get() = "com.google.android.gms:play-services-maps:_"

val Google.Android.maps: Maps get() = Maps

val Google.Accompanist.permissions: String get() =
    "com.google.accompanist:accompanist-permissions:_"