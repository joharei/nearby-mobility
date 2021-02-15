package app.reitan.nearby_mobility

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import app.reitan.nearby_mobility.features.map.ScooterMap
import app.reitan.nearby_mobility.tools.LocationResult
import app.reitan.nearby_mobility.tools.lastLocation

@Composable
fun App() {
    when (val lastLocation = lastLocation()) {
        LocationResult.Loading -> Surface(color = colorResource(R.color.ic_launcher_background)) {}
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