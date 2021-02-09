package app.reitan.nearby_mobility.features.map

import android.location.Location
import androidx.compose.runtime.*
import androidx.compose.ui.platform.AmbientContext
import app.reitan.nearby_mobility.components.GoogleMapView
import app.reitan.nearby_mobility.tools.latLng
import app.reitan.nearby_mobility.tools.latLonBounds
import com.google.maps.android.clustering.ClusterManager
import org.koin.androidx.compose.getViewModel

@Composable
fun ScooterMap(viewModel: ScooterMapViewModel = getViewModel(), location: Location?) {
    val scooters by viewModel.scooters.collectAsState(initial = emptyList())
    val context = AmbientContext.current
    var clusterManager by remember { mutableStateOf<ClusterManager<ScooterClusterItem>?>(null) }
    DisposableEffect(scooters, clusterManager) {
        clusterManager?.apply {
            clearItems()
            addItems(scooters.map(::ScooterClusterItem))
            cluster()
        }
        onDispose { }
    }
    GoogleMapView(
        initialPosition = location?.latLng,
        ambientEnabled = true,
        zoomControlsEnabled = true,
        onCameraIdle = {
            viewModel.visibleRegion.value = it.latLonBounds
        },
        setUpClusterManager = {
            ClusterManager<ScooterClusterItem>(context, it).apply {
                renderer = ScooterRenderer(context, it, this)
                clusterManager = this
            }
        }
    )
}