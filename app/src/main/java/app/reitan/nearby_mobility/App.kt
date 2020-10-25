package app.reitan.nearby_mobility

import androidx.compose.runtime.Composable
import androidx.ui.tooling.preview.Preview
import app.reitan.nearby_mobility.ui.AppTheme

@Composable
fun App() {
    AppTheme {
        GoogleMapView(ambientEnabled = true, zoomControlsEnabled = true)
    }
}

@Preview
@Composable
private fun AppPreview() {
    App()
}