package app.reitan.nearby_mobility

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.*
import app.reitan.nearby_mobility.features.map.ScooterMap

@Composable
fun App() {
    Scaffold(
        timeText = { TimeText(timeTextStyle = TimeTextDefaults.timeTextStyle(color = MaterialTheme.colors.onPrimary)) }
    ) {
        ScooterMap()
    }
}

@Preview
@Composable
private fun AppPreview() {
    App()
}
