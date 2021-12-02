package app.reitan.common.entur.models

import kotlinx.serialization.Serializable

@Serializable
internal data class Scooter(
    val lat: Double,
    val lon: Double,
    val system: System,
)

@Serializable
internal data class System(val operator: OperatorObject)

@Serializable
internal data class OperatorObject(val id: Operator = Operator.Unknown)