package app.reitan.common.entur.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class Operator {
    @SerialName("voi")
    Voi,

    @SerialName("tier")
    Tier,

    @SerialName("zvipp")
    Zvipp,

    @SerialName("lime")
    Lime,

    @SerialName("bolt")
    Bolt,

    Unknown,
}