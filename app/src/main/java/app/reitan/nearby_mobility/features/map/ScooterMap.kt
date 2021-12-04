package app.reitan.nearby_mobility.features.map

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import app.reitan.nearby_mobility.R
import app.reitan.nearby_mobility.components.ComposeMapView
import app.reitan.nearby_mobility.components.rememberGoogleMapState
import app.reitan.nearby_mobility.tools.alignZoomButtonsToBottom
import app.reitan.nearby_mobility.tools.lastLocation
import app.reitan.nearby_mobility.tools.latLng
import app.reitan.nearby_mobility.tools.latLonBounds
import app.reitan.nearby_mobility.ui.LocalWearMode
import app.reitan.nearby_mobility.ui.WearMode
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.getViewModel

@Composable
fun ScooterMap(viewModel: ScooterMapViewModel = getViewModel()) {
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

    val googleMapState = rememberGoogleMapState(
        onMapReady = {
            it.moveCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(58.9631, 5.731), 12f)
            )
        },
        cameraIdleListener = {
            viewModel.visibleRegion.value = it.projection.visibleRegion.latLngBounds.latLonBounds
            clusterManager?.onCameraIdle()
        }
    )
    val googleMap = googleMapState.value

    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val systemInsets = LocalWindowInsets.current.systemBars
    val bottomPaddingPx = with(LocalDensity.current) { 8.dp.roundToPx() }
    SideEffect {
        googleMap?.setPadding(
            systemInsets.left,
            systemInsets.top,
            systemInsets.right,
            systemInsets.bottom + bottomPaddingPx,
        )
        @SuppressLint("MissingPermission")
        googleMap?.isMyLocationEnabled = locationPermission.hasPermission
    }

    val context = LocalContext.current

    if (googleMap != null && clusterManager == null) {
        clusterManager = ClusterManager<ScooterClusterItem>(context, googleMap).apply {
            renderer = ScooterRenderer(context, googleMap, this)
        }
    }

    var mapView by remember { mutableStateOf<MapView?>(null) }
    SideEffect {
        mapView?.alignZoomButtonsToBottom()
        when (wearMode) {
            WearMode.Active -> {
                mapView?.onExitAmbient()
                googleMap?.uiSettings?.isZoomControlsEnabled = true
            }
            is WearMode.Ambient -> {
                mapView?.onEnterAmbient(wearMode.ambientDetails)
                googleMap?.uiSettings?.isZoomControlsEnabled = false
            }
        }
    }

    val content = @Composable {
        ComposeMapView(
            modifier = Modifier.fillMaxSize(),
            onMapViewCreated = { mapView = it },
            onGoogleMapCreated = { googleMapState.onGoogleMapCreated(it) },
        )
    }

    var doNotShowRationale by rememberSaveable { mutableStateOf(false) }
    PermissionRequired(
        permissionState = locationPermission,
        permissionNotGrantedContent = {
            if (doNotShowRationale) {
                content()
            } else {
                AlertDialog(
                    title = {
                        Text(
                            text = stringResource(R.string.location_rationale_title),
                            textAlign = TextAlign.Center,
                        )
                    },
                    message = {
                        Text(
                            text = stringResource(R.string.location_rationale),
                            textAlign = TextAlign.Center,
                        )
                    },
                ) {
                    CompactChip(
                        modifier = Modifier.width(150.dp),
                        onClick = locationPermission::launchPermissionRequest,
                        label = { Text(stringResource(R.string.location_rationale_grant)) },
                        colors = ChipDefaults.primaryChipColors(),
                    )
                    Spacer(Modifier.height(4.dp))
                    CompactChip(
                        modifier = Modifier.width(150.dp),
                        onClick = { doNotShowRationale = true },
                        label = { Text(stringResource(R.string.location_rationale_cancel)) },
                        colors = ChipDefaults.secondaryChipColors(),
                    )
                }
            }
        },
        permissionNotAvailableContent = content,
    ) {
        var gotLocation by rememberSaveable { mutableStateOf(false) }
        LaunchedEffect(context, gotLocation, googleMap) {
            if (!gotLocation && googleMap != null) {
                @SuppressLint("MissingPermission")
                val lastLocation = lastLocation(context)
                if (lastLocation != null) {
                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(lastLocation.latLng, 16f)
                    )
                }
                gotLocation = true
            }
        }
        Box {
            content()
            Crossfade(gotLocation) {
                if (!it) {
                    Box(modifier = Modifier.background(MaterialTheme.colors.background))
                }
            }
        }
    }
}
