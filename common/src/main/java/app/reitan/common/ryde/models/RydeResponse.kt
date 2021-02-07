package app.reitan.common.ryde.models

import kotlinx.serialization.Serializable

@Serializable
internal data class RydeResponse(val message: String, val scooters: List<Scooter>)