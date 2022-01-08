package app.reitan.common.ryde

import app.reitan.common.ScooterApi
import app.reitan.common.models.LatLon
import app.reitan.common.models.Operator
import app.reitan.common.models.Scooter
import app.reitan.common.ryde.models.RydeResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

internal class RydeApi(private val client: HttpClient) : ScooterApi {
    override suspend fun fetchScooters(
        centerLat: Double,
        centerLon: Double,
        radiusMeters: Double,
    ): List<Scooter> =
        client
            .submitForm(
                url = "https://qw-test.ryde.vip/appRyde/getNearScooters",
                formParameters = Parameters.build {
                    append("iotLa", centerLat.toString())
                    append("iotLo", centerLon.toString())
                    append("nearRadius", (radiusMeters / 1000).toString())
                    append("cityId", "7")
                },
            )
            .body<RydeResponse>()
            .scooters
            .map {
                Scooter(Operator.Ryde, LatLon(it.coordinate.latitude, it.coordinate.longitude))
            }
}
