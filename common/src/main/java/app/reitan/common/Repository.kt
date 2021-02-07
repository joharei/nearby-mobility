package app.reitan.common

import app.reitan.common.models.LatLon
import app.reitan.common.models.LatLonBounds
import app.reitan.common.models.Scooter
import app.reitan.common.ryde.RydeApi
import app.reitan.common.tools.distance
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.transformLatest
import kotlin.time.minutes

class Repository(private val rydeApi: RydeApi) {
    val visibleRegion = MutableStateFlow<LatLonBounds?>(null)

    val scooters = visibleRegion.filterNotNull()
        .transformLatest {
            do {
                emit(fetchRydeScooters(it))
                delay(1.minutes)
            } while (true)
        }

    private suspend fun fetchRydeScooters(visibleRegion: LatLonBounds): List<Scooter> {
        val centerLat = (visibleRegion.southWest.latitude + visibleRegion.northEast.latitude) / 2
        val centerLon = (visibleRegion.southWest.longitude + visibleRegion.northEast.longitude) / 2
        val radiusKm = distance(LatLon(centerLat, centerLon), visibleRegion.southWest) / 1000
        return rydeApi.fetchScooters(centerLat, centerLon, radiusKm).scooters.map {
            Scooter(LatLon(it.coordinate.latitude, it.coordinate.longitude))
        }
    }
}