package app.reitan.common

import app.reitan.common.models.LatLon
import app.reitan.common.models.LatLonBounds
import app.reitan.common.models.Scooter
import app.reitan.common.ryde.RydeApi
import app.reitan.common.tier.TierApi
import app.reitan.common.tools.distance
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.transformLatest
import kotlin.time.Duration

class Repository internal constructor(
    private val rydeApi: RydeApi,
    private val tierApi: TierApi,
) {
    val visibleRegion = MutableStateFlow<LatLonBounds?>(null)

    val scooters = visibleRegion.filterNotNull()
        .transformLatest { visibleRegion ->
            coroutineScope {
                do {
                    val scooters = listOf(rydeApi, tierApi)
                        .map { async { fetchScooters(it, visibleRegion) } }
                        .awaitAll()
                        .flatten()
                    emit(scooters)
                    delay(Duration.minutes(1))
                } while (true)
            }
        }.distinctUntilChanged()

    private suspend fun fetchScooters(
        scooterApi: ScooterApi,
        visibleRegion: LatLonBounds,
    ): List<Scooter> {
        val centerLat = (visibleRegion.southWest.latitude + visibleRegion.northEast.latitude) / 2
        val centerLon = (visibleRegion.southWest.longitude + visibleRegion.northEast.longitude) / 2
        val radiusKm = distance(LatLon(centerLat, centerLon), visibleRegion.southWest)
            .coerceAtMost(1000.0)
        return scooterApi.fetchScooters(centerLat, centerLon, radiusKm)
    }
}