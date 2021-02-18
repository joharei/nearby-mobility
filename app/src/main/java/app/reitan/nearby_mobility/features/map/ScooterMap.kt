package app.reitan.nearby_mobility.features.map

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.reitan.nearby_mobility.components.ComposeMapView
import app.reitan.nearby_mobility.components.rememberGoogleMapState
import app.reitan.nearby_mobility.di.getViewModel
import app.reitan.nearby_mobility.tools.latLng
import app.reitan.nearby_mobility.tools.latLonBounds
import app.reitan.nearby_mobility.tools.permissionState
import app.reitan.nearby_mobility.ui.LocalWearMode
import app.reitan.nearby_mobility.ui.WearMode
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import kotlinx.coroutines.flow.flowOf

@Composable
fun ScooterMap(viewModel: ScooterMapViewModel = getViewModel(), location: Location?) {
    val wearMode = LocalWearMode.current
    val scooters by remember(wearMode) {
        when (wearMode) {
            WearMode.Active -> viewModel.scooters
            is WearMode.Ambient -> flowOf(emptyList())
        }
    }.collectAsState(initial = emptyList())
    var clusterManager by remember { mutableStateOf<ClusterManager<ScooterClusterItem>?>(null) }

    clusterManager?.apply {
        clearItems()
        addItems(scooters.map(::ScooterClusterItem))
        cluster()
    }

    val locationPermission = permissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val systemInsets = LocalWindowInsets.current.systemBars
    val googleMapState = rememberGoogleMapState(
        onMapReady = {
            it.setPadding(
                systemInsets.left,
                (systemInsets.top + 24.dp.value).toInt(),
                systemInsets.right,
                systemInsets.bottom
            )
            @SuppressLint("MissingPermission")
            it.isMyLocationEnabled = locationPermission.hasPermission
            it.moveCamera(
                if (location != null)
                    CameraUpdateFactory.newLatLngZoom(location.latLng, 14f)
                else
                    CameraUpdateFactory.newLatLngZoom(LatLng(58.9109397, 5.7244898), 16f)
            )
        },
        cameraIdleListener = {
            viewModel.visibleRegion.value = it.projection.visibleRegion.latLngBounds.latLonBounds
            clusterManager?.onCameraIdle()
        }
    )
    val googleMap = googleMapState.value

    if (googleMap != null && clusterManager == null) {
        val context = LocalContext.current
        clusterManager = ClusterManager<ScooterClusterItem>(context, googleMap).apply {
            renderer = ScooterRenderer(context, googleMap, this)
        }
    }

    var mapView by remember { mutableStateOf<MapView?>(null) }
    SideEffect {
        when (wearMode) {
            WearMode.Active -> mapView?.onExitAmbient()
            is WearMode.Ambient -> mapView?.onEnterAmbient(wearMode.ambientDetails)
        }
    }


    ComposeMapView(
        modifier = Modifier.fillMaxSize(),
        onMapViewCreated = { mapView = it },
        onGoogleMapCreated = { googleMapState.onGoogleMapCreated(it) },
    )
}
