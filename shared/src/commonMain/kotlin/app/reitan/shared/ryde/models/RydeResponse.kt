package app.reitan.shared.ryde.models

import kotlinx.serialization.Serializable

@Serializable
internal data class RydeResponse(val message: String, val scooters: List<Scooter>)
