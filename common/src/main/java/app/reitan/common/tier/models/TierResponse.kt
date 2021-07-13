package app.reitan.common.tier.models

import kotlinx.serialization.Serializable

@Serializable
data class TierResponse(val data: List<Scooter>)
