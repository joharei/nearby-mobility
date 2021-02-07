package app.reitan.common.ryde

import app.reitan.common.ryde.models.RydeResponse
import io.ktor.client.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class RydeApi internal constructor(private val client: HttpClient) {
    internal suspend fun fetchScooters(centerLat: Double, centerLon: Double, radiusKm: Double): RydeResponse =
        client.submitForm(
            url = "https://qw-test.ryde.vip/appRyde/getNearScooters",
            formParameters = Parameters.build {
                append("iotLa", centerLat.toString())
                append("iotLo", centerLon.toString())
                append("nearRadius", radiusKm.toString())
                append("cityId", "7")
            },
        )
}