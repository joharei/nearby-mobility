package app.reitan.nearby_mobility.tools

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import app.reitan.common.models.LatLon
import app.reitan.common.models.LatLonBounds
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.LatLngBounds
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration

@SuppressLint("MissingPermission")
@Composable
fun lastLocation(): LocationResult {
    val locationPermission = permissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val current = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(current)
    }
    var result by remember { mutableStateOf<LocationResult>(LocationResult.Loading) }
    LaunchedEffect(locationPermission) {
        result = LocationResult.Success(
            if (locationPermission.hasPermission) {
                withTimeoutOrNull(Duration.seconds(1)) { fusedLocationClient.lastLocation.await() }
            } else null
        )
    }
    return result
}

sealed class LocationResult {
    object Loading : LocationResult()
    data class Success(val location: Location?) : LocationResult()
}

val Location.latLng: LatLng get() = LatLng(latitude, longitude)

val LatLon.latLng: LatLng get() = LatLng(latitude, longitude)

val LatLng.latLon: LatLon get() = LatLon(latitude, longitude)

val LatLngBounds.latLonBounds: LatLonBounds get() = LatLonBounds(southwest.latLon, northeast.latLon)
