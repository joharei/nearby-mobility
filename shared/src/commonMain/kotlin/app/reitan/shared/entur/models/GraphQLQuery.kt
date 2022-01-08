package app.reitan.shared.entur.models

import kotlinx.serialization.Serializable

@Serializable
internal data class GraphQLQuery(val query: String)
