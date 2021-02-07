package app.reitan.nearby_mobility

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.reitan.nearby_mobility.components.GoogleMapView
import app.reitan.nearby_mobility.tools.LocationResult
import app.reitan.nearby_mobility.tools.lastLocation
import app.reitan.nearby_mobility.tools.latLng

@Composable
fun App() {
    when (val lastLocation = lastLocation()) {
        LocationResult.Loading -> Scaffold {}
        is LocationResult.Success -> GoogleMapView(
            position = lastLocation.location?.latLng,
            ambientEnabled = true,
            zoomControlsEnabled = true
        )
    }
}

@Preview
@Composable
private fun AppPreview() {
    App()
}