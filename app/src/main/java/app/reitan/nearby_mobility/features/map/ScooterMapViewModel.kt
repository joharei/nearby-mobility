package app.reitan.nearby_mobility.features.map

import android.util.Log
import androidx.lifecycle.ViewModel
import app.reitan.common.Repository
import app.reitan.common.models.LatLonBounds
import kotlinx.coroutines.flow.catch

class ScooterMapViewModel(private val repo: Repository) : ViewModel() {
    val scooters = repo.scooters
        // TODO: handle errors properly
        .catch { Log.e("NearbyMobility", "Fetching scooters failed", it) }

    fun onCameraMoved(latLonBounds: LatLonBounds?) {
        repo.visibleRegion.value = latLonBounds
    }
}
