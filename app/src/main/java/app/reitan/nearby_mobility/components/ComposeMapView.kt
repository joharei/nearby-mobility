package app.reitan.nearby_mobility.components

import android.os.Bundle
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.maps.android.ktx.*
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
        viewBlock = {
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
                    onGoogleMapCreated(awaitMap())
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
    private val cameraMoveListener: (GoogleMap) -> Unit,
    private val cameraMoveCanceledListener: (GoogleMap) -> Unit,
    private val cameraMoveStartedListener: (GoogleMap, Int) -> Unit,
) : State<GoogleMap?> {
    private val googleMapState: MutableState<GoogleMap?> = mutableStateOf(null)

    suspend fun onGoogleMapCreated(googleMap: GoogleMap) {
        googleMapState.value = googleMap
        onMapReady(googleMap)
        googleMap.cameraEvents()
            .collect { event ->
                when (event) {
                    CameraIdleEvent -> cameraIdleListener(googleMap)
                    CameraMoveCanceledEvent -> cameraMoveCanceledListener(googleMap)
                    CameraMoveEvent -> cameraMoveListener(googleMap)
                    is CameraMoveStartedEvent -> cameraMoveStartedListener(googleMap, event.reason)
                }
            }
    }

    override val value: GoogleMap?
        get() = googleMapState.value
}

@Composable
fun rememberGoogleMapState(
    onMapReady: (GoogleMap) -> Unit = {},
    cameraIdleListener: (GoogleMap) -> Unit = {},
    cameraMoveListener: (GoogleMap) -> Unit = {},
    cameraMoveCanceledListener: (GoogleMap) -> Unit = {},
    cameraMoveStartedListener: (GoogleMap, Int) -> Unit = { _, _ -> },
): GoogleMapState {
    return remember {
        GoogleMapState(
            onMapReady = onMapReady,
            cameraIdleListener = cameraIdleListener,
            cameraMoveListener = cameraMoveListener,
            cameraMoveCanceledListener = cameraMoveCanceledListener,
            cameraMoveStartedListener = cameraMoveStartedListener
        )
    }
}