package app.reitan.nearby_mobility

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.*
import app.reitan.nearby_mobility.features.map.ScooterMap

@Composable
fun App(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        timeText = { TimeText() }
    ) {
        ScooterMap()
    }
}

@Preview
@Composable
private fun AppPreview() {
    App()
}
