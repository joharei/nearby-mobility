package app.reitan.common.tier

import app.reitan.common.ScooterApi
import app.reitan.common.models.LatLon
import app.reitan.common.models.Operator
import app.reitan.common.models.Scooter
import app.reitan.common.tier.models.TierResponse
import io.ktor.client.*
import io.ktor.client.request.*
import kotlin.math.roundToInt

internal class TierApi(private val client: HttpClient) : ScooterApi {
    override suspend fun fetchScooters(
        centerLat: Double,
        centerLon: Double,
        radiusMeters: Double,
    ): List<Scooter> {
        return client.get<TierResponse>("https://platform.tier-services.io/v1/vehicle") {
            parameter("lat", centerLat)
            parameter("lng", centerLon)
            parameter("radius", radiusMeters.roundToInt())

            header("X-Api-Key", "bpEUTJEBTf74oGRWxaIcW7aeZMzDDODe1yBoSxi2")
        }.data.map {
            Scooter(
                operator = Operator.Tier,
                position = LatLon(it.attributes.lat, it.attributes.lng),
            )
        }
    }
}