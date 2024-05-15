package app.reitan.nearby_mobility

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import app.reitan.nearby_mobility.features.map.ScooterMap
import app.reitan.nearby_mobility.features.map.ScooterMapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun App(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        timeText = { TimeText() }
    ) {
        val viewModel: ScooterMapViewModel = koinViewModel()
        val scooters by viewModel.scooters.collectAsStateWithLifecycle(
            initialValue = emptyList(),
            minActiveState = Lifecycle.State.RESUMED
        )

        ScooterMap(
            scooters = scooters,
            locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION),
            onNavigateUp = onNavigateUp,
            onCameraMoved = viewModel::onCameraMoved
        )
    }
}
