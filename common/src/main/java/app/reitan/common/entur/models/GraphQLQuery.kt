package app.reitan.common.entur.models

import kotlinx.serialization.Serializable

@Serializable
internal data class GraphQLQuery(val query: String)
