package app.reitan.common.entur

import app.reitan.common.ScooterApi
import app.reitan.common.models.LatLon
import app.reitan.common.models.Operator
import app.reitan.common.models.Scooter
import io.ktor.client.*
import io.ktor.client.request.*
import kotlin.math.roundToInt
import app.reitan.common.entur.models.Operator as EnturOperator
import app.reitan.common.entur.models.Scooter as EnturScooter

internal class EnturApi(private val client: HttpClient) : ScooterApi {
    override suspend fun fetchScooters(
        centerLat: Double,
        centerLon: Double,
        radiusMeters: Double,
    ): List<Scooter> =
        client.get<List<EnturScooter>>("https://api.entur.io/mobility/v1/scooters") {
            parameter("lat", centerLat)
            parameter("lon", centerLon)
            parameter("range", radiusMeters.roundToInt())
            parameter("max", 50)

            header("ET-Client-Name", "johan_reitan-nearby_mobility")
        }
            .map {
                Scooter(
                    when (it.operator) {
                        EnturOperator.Voi -> Operator.Voi
                        EnturOperator.Tier -> Operator.Tier
                        EnturOperator.Zvipp -> Operator.Zvipp
                        EnturOperator.Lime -> Operator.Lime
                        EnturOperator.Bolt -> Operator.Bolt
                        EnturOperator.Unknown -> Operator.Unknown
                    },
                    LatLon(it.lat, it.lon),
                )
            }
}