package app.reitan.shared.ryde.models

import kotlinx.serialization.Serializable

@Serializable
internal data class Scooter(val memberByString: String, val coordinate: Coordinate, val distance: Double)
