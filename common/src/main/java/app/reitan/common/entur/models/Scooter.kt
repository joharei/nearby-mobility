package app.reitan.common.entur.models

import kotlinx.serialization.Serializable

@Serializable
internal data class Scooter(
    val operator: Operator = Operator.Unknown,
    val lat: Double,
    val lon: Double,
    val code: String? = null,
    val battery: Int? = null,
)