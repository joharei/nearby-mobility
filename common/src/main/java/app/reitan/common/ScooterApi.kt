package app.reitan.common

import app.reitan.common.models.Scooter

interface ScooterApi {
    suspend fun fetchScooters(
        centerLat: Double,
        centerLon: Double,
        radiusMeters: Double,
    ): List<Scooter>
}