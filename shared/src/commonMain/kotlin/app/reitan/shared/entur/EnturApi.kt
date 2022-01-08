package app.reitan.shared.entur

import app.reitan.shared.ScooterApi
import app.reitan.shared.entur.models.EnturResponse
import app.reitan.shared.entur.models.GraphQLQuery
import app.reitan.shared.models.LatLon
import app.reitan.shared.models.Operator
import app.reitan.shared.models.Scooter
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.math.roundToInt
import app.reitan.shared.entur.models.Operator as EnturOperator

internal class EnturApi(private val client: HttpClient) : ScooterApi {
    override suspend fun fetchScooters(
        centerLat: Double,
        centerLon: Double,
        radiusMeters: Double,
    ): List<Scooter> =
        client.post("https://api.entur.io/mobility/v2/graphql") {
            contentType(ContentType.Application.Json)
            setBody(
                GraphQLQuery(
                    query = """
                    {
                      vehicles(lat:$centerLat, lon:$centerLon, range: ${radiusMeters.roundToInt()}, count: 50, formFactors: SCOOTER) {
                        lat
                        lon
                        system {
                          operator {
                            id
                          }
                        }
                      }
                    }
                    """
                        .trimIndent()
                        .replace(" ", "")
                )
            )

            header("ET-Client-Name", "johan_reitan-nearby_mobility")
        }
            .body<EnturResponse>()
            .data.vehicles
            .map {
                Scooter(
                    when (it.system.operator.id) {
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
