package app.reitan.common.tier.models

import kotlinx.serialization.Serializable

@Serializable
data class Attributes(
    val lat: Double,
    val lng: Double,
    val batteryLevel: Int,
)
