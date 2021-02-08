package app.reitan.nearby_mobility.features.map

import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.reitan.nearby_mobility.components.GoogleMapView
import app.reitan.nearby_mobility.tools.latLng
import app.reitan.nearby_mobility.tools.latLonBounds
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.androidx.compose.getViewModel

@Composable
fun ScooterMap(viewModel: ScooterMapViewModel = getViewModel(), location: Location?) {
    val scooters by viewModel.scooters.collectAsState(initial = emptyList())
    val markers = scooters.map {
        MarkerOptions().position(it.position.latLng)
    }
    GoogleMapView(
        initialPosition = location?.latLng,
        ambientEnabled = true,
        zoomControlsEnabled = true,
        markers = markers,
        onCameraIdle = { viewModel.visibleRegion.value = it.latLonBounds }
    )
}