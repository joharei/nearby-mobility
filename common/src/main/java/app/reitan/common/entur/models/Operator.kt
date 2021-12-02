package app.reitan.common.entur.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class Operator {
    @SerialName("YVO:Operator:voi")
    Voi,

    @SerialName("YTI:Operator:Tier")
    Tier,

    @SerialName("YZV:Operator:zvipp")
    Zvipp,

    @SerialName("YLI:Operator:lime")
    Lime,

    @SerialName("YBO:Operator:bolt")
    Bolt,

    Unknown,
}