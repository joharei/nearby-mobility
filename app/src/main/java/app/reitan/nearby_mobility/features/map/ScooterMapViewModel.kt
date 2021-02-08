package app.reitan.nearby_mobility.features.map

import androidx.lifecycle.ViewModel
import app.reitan.common.Repository

class ScooterMapViewModel(repo: Repository) : ViewModel() {
    val visibleRegion = repo.visibleRegion

    val scooters = repo.scooters
}