package app.reitan.nearby_mobility

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.reitan.nearby_mobility.features.map.ScooterMap
import app.reitan.nearby_mobility.tools.LocationResult
import app.reitan.nearby_mobility.tools.lastLocation

@Composable
fun App() {
    when (val lastLocation = lastLocation()) {
        LocationResult.Loading -> Scaffold {}
        is LocationResult.Success -> {
            ScooterMap(location = lastLocation.location)
        }
    }
}

@Preview
@Composable
private fun AppPreview() {
    App()
}