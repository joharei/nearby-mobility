package app.reitan.shared

import app.reitan.shared.models.Scooter

interface ScooterApi {
    suspend fun fetchScooters(
        centerLat: Double,
        centerLon: Double,
        radiusMeters: Double,
    ): List<Scooter>
}
