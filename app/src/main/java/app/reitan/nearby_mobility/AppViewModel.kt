package app.reitan.nearby_mobility

import androidx.lifecycle.ViewModel
import app.reitan.common.Repository

class AppViewModel(repo: Repository) : ViewModel() {
    val visibleRegion = repo.visibleRegion

    val scooters = repo.scooters
}