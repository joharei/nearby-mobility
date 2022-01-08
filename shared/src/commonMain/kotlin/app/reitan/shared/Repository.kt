package app.reitan.shared

import app.reitan.shared.entur.EnturApi
import app.reitan.shared.models.LatLon
import app.reitan.shared.models.LatLonBounds
import app.reitan.shared.models.Scooter
import app.reitan.shared.ryde.RydeApi
import app.reitan.shared.tools.distance
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.transformLatest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.minutes

class Repository : KoinComponent {
    private val rydeApi: RydeApi by inject()
    private val enturApi: EnturApi by inject()

    val visibleRegion = MutableStateFlow<LatLonBounds?>(null)

    val scooters = visibleRegion.filterNotNull()
        .transformLatest { visibleRegion ->
            coroutineScope {
                do {
                    val scooters = listOf(rydeApi, enturApi)
                        .map { async { fetchScooters(it, visibleRegion) } }
                        .awaitAll()
                        .flatten()
                    emit(scooters)
                    delay(1.minutes)
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
