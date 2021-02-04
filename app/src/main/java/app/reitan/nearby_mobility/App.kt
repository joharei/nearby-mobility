package app.reitan.nearby_mobility

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun App() {
    GoogleMapView(ambientEnabled = true, zoomControlsEnabled = true)
}

@Preview
@Composable
private fun AppPreview() {
    App()
}