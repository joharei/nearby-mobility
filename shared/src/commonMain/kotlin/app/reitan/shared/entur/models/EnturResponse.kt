package app.reitan.shared.entur.models

import kotlinx.serialization.Serializable

@Serializable
internal data class EnturResponse(val data: EnturData)

@Serializable
internal data class EnturData(val vehicles: List<Scooter>)
