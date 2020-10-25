package app.reitan.nearby_mobility

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.LifecycleOwnerAmbient
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.ui.tooling.preview.Preview
import app.reitan.nearby_mobility.ui.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


/**
 * Remembers a MapView and gives it the lifecycle of the current LifecycleOwner
 */
@Composable
private fun rememberMapViewWithLifecycle(
    ambientEnabled: Boolean,
    zoomControlsEnabled: Boolean,
): MapView {
    val context = ContextAmbient.current
    val mapView = remember(ambientEnabled, zoomControlsEnabled) {
        MapView(
            context,
            GoogleMapOptions()
                .ambientEnabled(ambientEnabled)
                .zoomControlsEnabled(zoomControlsEnabled)
        )
    }

    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LifecycleOwnerAmbient.current.lifecycle
    onCommit(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    val wearMode = WearModeAmbient.current
    onCommit(wearMode) {
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
fun GoogleMapView(
    latitude: Double = 58.9109397,
    longitude: Double = 5.7244898,
    ambientEnabled: Boolean = false,
    zoomControlsEnabled: Boolean = false,
) {
    val locationPermission = permissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val mapView = rememberMapViewWithLifecycle(ambientEnabled, zoomControlsEnabled)
    val systemInsets = InsetsAmbient.current.systemBars
    AndroidView(
        { mapView }
    ) { map ->
        map.getMapAsync {
            it.setPadding(
                systemInsets.left,
                (systemInsets.top + 24.dp.value).toInt(),
                systemInsets.right,
                systemInsets.bottom
            )
            it.isMyLocationEnabled = locationPermission.hasPermission
            it.clear()
            val position = LatLng(latitude, longitude)
            it.addMarker(
                MarkerOptions().position(position)
            )
            it.moveCamera(CameraUpdateFactory.newLatLng(position))
        }
    }
}

@Preview
@Composable
private fun GoogleMapViewPreview() {
    AppTheme {
        GoogleMapView()
    }
}
