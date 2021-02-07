package app.reitan.common.ryde.models

import kotlinx.serialization.Serializable

@Serializable
internal data class Scooter(val memberByString: String, val coordinate: Coordinate, val distance: Double)