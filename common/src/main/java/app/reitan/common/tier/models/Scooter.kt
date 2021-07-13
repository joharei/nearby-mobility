package app.reitan.common.tier.models

import kotlinx.serialization.Serializable

@Serializable
data class Scooter(
    val id: String,
    val attributes: Attributes,
)
