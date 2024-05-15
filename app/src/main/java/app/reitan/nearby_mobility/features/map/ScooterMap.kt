package app.reitan.nearby_mobility.features.map

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import app.reitan.common.models.LatLonBounds
import app.reitan.common.models.Scooter
import app.reitan.nearby_mobility.R
import app.reitan.nearby_mobility.tools.lastLocation
import app.reitan.nearby_mobility.tools.latLng
import app.reitan.nearby_mobility.tools.latLonBounds
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class,
    ExperimentalWearFoundationApi::class
)
@Composable
fun ScooterMap(
    scooters: List<Scooter>,
    locationPermission: PermissionState,
    onNavigateUp: () -> Unit,
    onCameraMoved: (latLonBounds: LatLonBounds?) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(58.9631, 5.731), 12f)
    }

    val latLonBounds = cameraPositionState.projection?.visibleRegion?.latLngBounds?.latLonBounds
    LaunchedEffect(latLonBounds, cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            onCameraMoved(latLonBounds)
        }
    }

    val context = LocalContext.current

    val googleMapOptions = GoogleMapOptions()

    val properties = remember(context, locationPermission.status.isGranted) {
        MapProperties(
            isMyLocationEnabled = locationPermission.status.isGranted,
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style),
        )
    }

    val uiSettings = remember {
        MapUiSettings(
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false
        )
    }

    var mapClickedToggle by remember { mutableStateOf(false) }

    val content = @Composable {
        val focusRequester = rememberActiveFocusRequester()
        Box {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .onRotaryScrollEvent {
                        cameraPositionState.move(CameraUpdateFactory.zoomBy(it.verticalScrollPixels / 100f))
                        true
                    }
                    .focusRequester(focusRequester)
                    .focusable(),
                cameraPositionState = cameraPositionState,
                googleMapOptionsFactory = { googleMapOptions },
                properties = properties,
                uiSettings = uiSettings,
                onMapClick = {
                    mapClickedToggle = !mapClickedToggle
                },
                contentPadding = WindowInsets.systemBars.add(
                    WindowInsets(left = 8.dp, top = 8.dp, right = 8.dp, bottom = 8.dp)
                ).asPaddingValues(),
            ) {
                Clustering(
                    items = scooters.map(::ScooterClusterItem),
                    clusterItemContent = { item ->
                        ScooterMarker(operator = item.scooter.operator)
                    },
                )
            }

            val displayButtons = !mapClickedToggle &&
                    !(cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE)

            AnimatedVisibility(
                visible = displayButtons,
                modifier = Modifier.align(Alignment.CenterStart),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                CompactButton(
                    colors = ButtonDefaults.secondaryButtonColors(),
                    onClick = onNavigateUp,
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
            }

            AnimatedVisibility(
                visible = displayButtons,
                modifier = Modifier.align(Alignment.CenterEnd),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                CompactButton(
                    colors = ButtonDefaults.secondaryButtonColors(),
                    onClick = {
                        scope.launch {
                            @SuppressLint("MissingPermission")
                            val lastLocation = lastLocation(context)
                            if (lastLocation != null) {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(lastLocation.latLng, 16f)
                                )
                            }
                        }
                    },
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = null)
                }
            }
        }
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
            LaunchedEffect(context, gotLocation) {
                if (!gotLocation) {
                    @SuppressLint("MissingPermission")
                    val lastLocation = lastLocation(context)
                    if (lastLocation != null) {
                        cameraPositionState.move(
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

@OptIn(ExperimentalPermissionsApi::class)
@WearPreviewDevices
@Composable
private fun Preview() {
    MaterialTheme {
        ScooterMap(
            scooters = emptyList(),
            locationPermission = object : PermissionState {
                override val permission: String
                    get() = TODO("Not yet implemented")
                override val status: PermissionStatus = PermissionStatus.Granted

                override fun launchPermissionRequest() {
                    TODO("Not yet implemented")
                }

            },
            onNavigateUp = {},
            onCameraMoved = {},
        )
    }
}
