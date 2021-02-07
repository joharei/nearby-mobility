package app.reitan.common.ryde.models

import kotlinx.serialization.Serializable

@Serializable
internal data class Coordinate(val latitude: Double, val longitude: Double)