package app.reitan.nearby_mobility.features.map

import android.util.Log
import androidx.lifecycle.ViewModel
import app.reitan.common.Repository
import kotlinx.coroutines.flow.catch

class ScooterMapViewModel(repo: Repository) : ViewModel() {
    val visibleRegion = repo.visibleRegion

    val scooters = repo.scooters
        // TODO: handle errors properly
        .catch { Log.e("NearbyMobility", "Fetching scooters failed", it) }
}