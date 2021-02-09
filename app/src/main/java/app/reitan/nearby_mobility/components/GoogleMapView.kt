package app.reitan.nearby_mobility.components

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.runtime.*
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import app.reitan.nearby_mobility.tools.permissionState
import app.reitan.nearby_mobility.ui.AmbientWearMode
import app.reitan.nearby_mobility.ui.AppTheme
import app.reitan.nearby_mobility.ui.WearMode
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.buildGoogleMapOptions
import dev.chrisbanes.accompanist.insets.AmbientWindowInsets
import kotlinx.coroutines.launch


/**
 * Remembers a MapView and gives it the lifecycle of the current LifecycleOwner
 */
@Composable
private fun rememberMapViewWithLifecycle(
    ambientEnabled: Boolean,
    zoomControlsEnabled: Boolean,
    initialPosition: LatLng? = null,
    onCameraIdle: ((visibleBounds: LatLngBounds) -> Unit)? = null,
    setUpClusterManager: ((GoogleMap) -> ClusterManager<out ClusterItem>)? = null,
): MapView {
    val context = AmbientContext.current
    val scope = rememberCoroutineScope()
    val mapView = remember(ambientEnabled, zoomControlsEnabled) {
        MapView(
            context,
            buildGoogleMapOptions {
                ambientEnabled(ambientEnabled)
                zoomControlsEnabled(zoomControlsEnabled)
            }
        ).also { mapView ->
            scope.launch {
                val map = mapView.awaitMap()
                map.moveCamera(
                    if (initialPosition != null)
                        CameraUpdateFactory.newLatLngZoom(initialPosition, 14f)
                    else
                        CameraUpdateFactory.newLatLng(LatLng(58.9109397, 5.7244898))
                )
                val clusterManager = setUpClusterManager?.invoke(map)
                map.setOnCameraIdleListener {
                    onCameraIdle?.invoke(map.projection.visibleRegion.latLngBounds)
                    clusterManager?.onCameraIdle()
                }
            }
        }
    }

    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = AmbientLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    val wearMode = AmbientWearMode.current
    SideEffect {
        when (wearMode) {
            WearMode.Active -> mapView.onExitAmbient()
            is WearMode.Ambient -> mapView.onEnterAmbient(wearMode.ambientDetails)
        }
    }

    return mapView
}

@Composable
private fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }
    }

@SuppressLint("MissingPermission")
@Composable
private fun GoogleMapContainer(
    mapView: MapView,
    markers: List<MarkerOptions> = emptyList(),
) {
    val locationPermission = permissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val systemInsets = AmbientWindowInsets.current.systemBars
    val scope = rememberCoroutineScope()
    AndroidView({ mapView }) { map ->
        scope.launch {
            val googleMap = map.awaitMap()
            googleMap.setPadding(
                systemInsets.left,
                (systemInsets.top + 24.dp.value).toInt(),
                systemInsets.right,
                systemInsets.bottom
            )
            googleMap.isMyLocationEnabled = locationPermission.hasPermission

            googleMap.clear()
            for (marker in markers) {
                googleMap.addMarker(marker)
            }
        }
    }
}

@Composable
fun GoogleMapView(
    initialPosition: LatLng? = null,
    ambientEnabled: Boolean = false,
    zoomControlsEnabled: Boolean = false,
    markers: List<MarkerOptions> = emptyList(),
    onCameraIdle: (visibleBounds: LatLngBounds) -> Unit = {},
    setUpClusterManager: ((GoogleMap) -> ClusterManager<out ClusterItem>)? = null,
) {
    val mapView = rememberMapViewWithLifecycle(
        ambientEnabled,
        zoomControlsEnabled,
        initialPosition,
        onCameraIdle,
        setUpClusterManager,
    )
    GoogleMapContainer(mapView, markers)
}

@Preview
@Composable
private fun GoogleMapViewPreview() {
    AppTheme {
        GoogleMapView()
    }
}
