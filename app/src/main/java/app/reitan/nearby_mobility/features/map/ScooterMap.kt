package app.reitan.nearby_mobility.features.map

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog
import app.reitan.nearby_mobility.R
import app.reitan.nearby_mobility.components.ComposeMapView
import app.reitan.nearby_mobility.components.rememberGoogleMapState
import app.reitan.nearby_mobility.tools.alignZoomButtonsToBottom
import app.reitan.nearby_mobility.tools.lastLocation
import app.reitan.nearby_mobility.tools.latLng
import app.reitan.nearby_mobility.tools.latLonBounds
import app.reitan.nearby_mobility.ui.LocalWearMode
import app.reitan.nearby_mobility.ui.WearMode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScooterMap(viewModel: ScooterMapViewModel = koinViewModel()) {
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
    val systemInsets = WindowInsets.systemBars
    val left = systemInsets.getLeft(LocalDensity.current, LocalLayoutDirection.current)
    val top = systemInsets.getTop(LocalDensity.current)
    val right = systemInsets.getRight(LocalDensity.current, LocalLayoutDirection.current)
    val bottom = systemInsets.getBottom(LocalDensity.current)
    val bottomPaddingPx = with(LocalDensity.current) { 8.dp.roundToPx() }
    SideEffect {
        googleMap?.setPadding(left, top, right, bottom + bottomPaddingPx)
        @SuppressLint("MissingPermission")
        googleMap?.isMyLocationEnabled = locationPermission.status.isGranted
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
    when (locationPermission.status) {
        is PermissionStatus.Denied -> {
            if (doNotShowRationale) {
                content()
            } else {
                Dialog(
                    showDialog = true,
                    onDismissRequest = {},
                ) {
                    Alert(
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
                        item {
                            CompactChip(
                                modifier = Modifier.width(150.dp),
                                onClick = locationPermission::launchPermissionRequest,
                                label = { Text(stringResource(R.string.location_rationale_grant)) },
                                colors = ChipDefaults.primaryChipColors(),
                            )
                        }
                        item { Spacer(Modifier.height(4.dp)) }
                        item {
                            CompactChip(
                                modifier = Modifier.width(150.dp),
                                onClick = { doNotShowRationale = true },
                                label = { Text(stringResource(R.string.location_rationale_cancel)) },
                                colors = ChipDefaults.secondaryChipColors(),
                            )
                        }
                    }
                }
            }
        }

        PermissionStatus.Granted -> {
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
                Crossfade(gotLocation, label = "Got location") {
                    if (!it) {
                        Box(modifier = Modifier.background(MaterialTheme.colors.background))
                    }
                }
            }
        }
    }
}
