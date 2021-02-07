package app.reitan.nearby_mobility

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import app.reitan.nearby_mobility.components.GoogleMapView
import app.reitan.nearby_mobility.tools.LocationResult
import app.reitan.nearby_mobility.tools.lastLocation
import app.reitan.nearby_mobility.tools.latLng
import app.reitan.nearby_mobility.tools.latLonBounds
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.androidx.compose.getViewModel

@Composable
fun App(
    viewModel: AppViewModel = getViewModel()
) {
    when (val lastLocation = lastLocation()) {
        LocationResult.Loading -> Scaffold {}
        is LocationResult.Success -> {
            val scooters by viewModel.scooters.collectAsState(initial = emptyList())
            val markers = scooters.map {
                MarkerOptions().position(it.position.latLng)
            }
            GoogleMapView(
                initialPosition = lastLocation.location?.latLng,
                ambientEnabled = true,
                zoomControlsEnabled = true,
                markers = markers,
                onCameraIdle = { viewModel.visibleRegion.value = it.latLonBounds }
            )
        }
    }
}

@Preview
@Composable
private fun AppPreview() {
    App()
}