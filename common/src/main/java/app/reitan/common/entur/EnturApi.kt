package app.reitan.common.entur

import app.reitan.common.entur.models.Scooter
import io.ktor.client.*
import io.ktor.client.request.*

internal class EnturApi(private val client: HttpClient) {
    suspend fun fetchScooters(
        centerLat: Double,
        centerLon: Double,
        radiusM: Int,
    ): List<Scooter> =
        client.get("https://api.entur.io/mobility/v1/scooters") {
            parameter("lat", centerLat)
            parameter("lon", centerLon)
            parameter("range", radiusM)
            parameter("max", 50)

            header("ET-Client-Name", "johan_reitan-nearby_mobility")
        }
}