@file:Suppress("unused", "UnstableApiUsage")

import de.fayard.refreshVersions.core.DependencyGroup
import de.fayard.refreshVersions.core.DependencyNotation

object Maps : DependencyGroup("com.google.maps.android") {
    val mapsKtx = module("maps-ktx")
    val mapsUtils = module("android-maps-utils")
    val mapsUtilsKtx = module("maps-utils-ktx")
}

val Google.Android.PlayServices.maps: DependencyNotation
    get() = module("play-services-maps")

val Google.Android.maps: Maps get() = Maps

val Google.Accompanist.permissions: DependencyNotation
    get() = module("accompanist-permissions")