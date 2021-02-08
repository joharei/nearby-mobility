package app.reitan.nearby_mobility.features.map

import android.location.Location
import androidx.compose.runtime.*
import androidx.compose.ui.platform.AmbientContext
import app.reitan.nearby_mobility.R
import app.reitan.nearby_mobility.components.GoogleMapView
import app.reitan.nearby_mobility.tools.bitmapDescriptorFromVector
import app.reitan.nearby_mobility.tools.latLng
import app.reitan.nearby_mobility.tools.latLonBounds
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.androidx.compose.getViewModel

@Composable
fun ScooterMap(viewModel: ScooterMapViewModel = getViewModel(), location: Location?) {
    val scooters by viewModel.scooters.collectAsState(initial = emptyList())
    val context = AmbientContext.current
    var mapReady by remember { mutableStateOf(false) }
    val mapPinIcon = remember(mapReady) {
        if (mapReady) bitmapDescriptorFromVector(context, R.drawable.ic_map_pin)
        else null
    }
    val markers = scooters.map {
        MarkerOptions().position(it.position.latLng).icon(mapPinIcon)
    }
    GoogleMapView(
        initialPosition = location?.latLng,
        ambientEnabled = true,
        zoomControlsEnabled = true,
        markers = markers,
        onCameraIdle = {
            mapReady = true
            viewModel.visibleRegion.value = it.latLonBounds
        }
    )
}