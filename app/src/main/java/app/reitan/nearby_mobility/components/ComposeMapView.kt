package app.reitan.nearby_mobility.components

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import app.reitan.nearby_mobility.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.buildGoogleMapOptions
import com.google.maps.android.ktx.cameraIdleEvents
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun ComposeMapView(
    onMapViewCreated: (MapView) -> Unit,
    onGoogleMapCreated: suspend (GoogleMap) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    val googleMapOptions = remember {
        buildGoogleMapOptions {
            ambientEnabled(true)
            zoomControlsEnabled(true)
        }
    }

    AndroidView(
        factory = {
            MapView(it, googleMapOptions).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_CREATE -> onCreate(Bundle())
                        Lifecycle.Event.ON_START -> onStart()
                        Lifecycle.Event.ON_RESUME -> onResume()
                        Lifecycle.Event.ON_PAUSE -> onPause()
                        Lifecycle.Event.ON_STOP -> onStop()
                        Lifecycle.Event.ON_DESTROY -> onDestroy()
                        else -> throw IllegalStateException()
                    }
                })

                onMapViewCreated(this)

                coroutineScope.launch {
                    val map = awaitMap()
                    val success = runCatching {
                        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(it, R.raw.map_style))
                    }.getOrDefault(false)
                    if (!success) {
                        Log.w("NearbyMobility", "Failed to load map style")
                    }
                    onGoogleMapCreated(map)
                }
            }
        },
        modifier = modifier
    )
}

@Stable
class GoogleMapState(
    private val onMapReady: (GoogleMap) -> Unit,
    private val cameraIdleListener: (GoogleMap) -> Unit,
) : State<GoogleMap?> {
    private val googleMapState: MutableState<GoogleMap?> = mutableStateOf(null)

    suspend fun onGoogleMapCreated(googleMap: GoogleMap) {
        googleMapState.value = googleMap
        onMapReady(googleMap)
        googleMap.cameraIdleEvents()
            .collect {
                cameraIdleListener(googleMap)
            }
    }

    override val value: GoogleMap?
        get() = googleMapState.value
}

@Composable
fun rememberGoogleMapState(
    onMapReady: (GoogleMap) -> Unit = {},
    cameraIdleListener: (GoogleMap) -> Unit = {},
): GoogleMapState {
    return remember {
        GoogleMapState(
            onMapReady = onMapReady,
            cameraIdleListener = cameraIdleListener,
        )
    }
}