package app.reitan.nearby_mobility

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.reitan.nearby_mobility.features.map.ScooterMap

@Composable
fun App() {
    ScooterMap()
}

@Preview
@Composable
private fun AppPreview() {
    App()
}