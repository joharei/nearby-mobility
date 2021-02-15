package app.reitan.common

import app.reitan.common.entur.EnturApi
import app.reitan.common.models.LatLon
import app.reitan.common.models.LatLonBounds
import app.reitan.common.models.Operator
import app.reitan.common.models.Scooter
import app.reitan.common.ryde.RydeApi
import app.reitan.common.tools.distance
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.transformLatest
import kotlin.math.roundToInt
import kotlin.time.minutes
import app.reitan.common.entur.models.Operator as EnturOperator

class Repository internal constructor(
    private val rydeApi: RydeApi,
    private val enturApi: EnturApi,
) {
    val visibleRegion = MutableStateFlow<LatLonBounds?>(null)

    val scooters = visibleRegion.filterNotNull()
        .transformLatest {
            coroutineScope {
                do {
                    val ryde = async { fetchRydeScooters(it) }
                    val entur = async { fetchEnturScooters(it) }
                    emit(listOf(ryde, entur).awaitAll().flatten())
                    delay(1.minutes)
                } while (true)
            }
        }.distinctUntilChanged()

    private suspend fun fetchRydeScooters(visibleRegion: LatLonBounds): List<Scooter> {
        val centerLat = (visibleRegion.southWest.latitude + visibleRegion.northEast.latitude) / 2
        val centerLon = (visibleRegion.southWest.longitude + visibleRegion.northEast.longitude) / 2
        val radiusKm = distance(LatLon(centerLat, centerLon), visibleRegion.southWest)
            .coerceAtMost(1000.0) / 1000
        return rydeApi.fetchScooters(centerLat, centerLon, radiusKm).scooters.map {
            Scooter(Operator.Ryde, LatLon(it.coordinate.latitude, it.coordinate.longitude))
        }
    }

    private suspend fun fetchEnturScooters(visibleRegion: LatLonBounds): List<Scooter> {
        val centerLat = (visibleRegion.southWest.latitude + visibleRegion.northEast.latitude) / 2
        val centerLon = (visibleRegion.southWest.longitude + visibleRegion.northEast.longitude) / 2
        val radiusM =
            distance(LatLon(centerLat, centerLon), visibleRegion.southWest)
                .coerceAtMost(1000.0)
                .roundToInt()
        return enturApi.fetchScooters(centerLat, centerLon, radiusM).map {
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
}