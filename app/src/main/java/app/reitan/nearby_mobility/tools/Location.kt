package app.reitan.nearby_mobility.tools

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.runtime.*
import androidx.compose.ui.platform.AmbientContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
@Composable
fun lastLocation(): LocationResult {
    val locationPermission = permissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val current = AmbientContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(current)
    }
    var result by remember { mutableStateOf<LocationResult>(LocationResult.Loading) }
    LaunchedEffect(locationPermission) {
        if (locationPermission.hasPermission) {
            result = LocationResult.Success(fusedLocationClient.lastLocation.await())
        }
    }
    return result
}

sealed class LocationResult {
    object Loading : LocationResult()
    data class Success(val location: Location?) : LocationResult()
}

val Location.latLng: LatLng get() = LatLng(latitude, longitude)