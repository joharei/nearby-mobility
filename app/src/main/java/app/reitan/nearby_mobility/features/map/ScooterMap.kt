package app.reitan.nearby_mobility.features.map

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import app.reitan.nearby_mobility.R
import app.reitan.nearby_mobility.components.ComposeMapView
import app.reitan.nearby_mobility.components.rememberGoogleMapState
import app.reitan.nearby_mobility.tools.lastLocation
import app.reitan.nearby_mobility.tools.latLng
import app.reitan.nearby_mobility.tools.latLonBounds
import app.reitan.nearby_mobility.tools.roundScreenPadding
import app.reitan.nearby_mobility.ui.LocalWearMode
import app.reitan.nearby_mobility.ui.WearMode
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.LatLng
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
    SideEffect {
        googleMap?.setPadding(
            systemInsets.left,
            (systemInsets.top + 24.dp.value).toInt(),
            systemInsets.right,
            systemInsets.bottom
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
        when (wearMode) {
            WearMode.Active -> mapView?.onExitAmbient()
            is WearMode.Ambient -> mapView?.onEnterAmbient(wearMode.ambientDetails)
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
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                        .roundScreenPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                ) {
                    Text(
                        text = stringResource(R.string.location_rationale),
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center,
                    )
                    Button(onClick = locationPermission::launchPermissionRequest) {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = stringResource(R.string.location_rationale_grant),
                        )
                    }
                    Button(
                        onClick = { doNotShowRationale = true },
                        colors = ButtonDefaults.secondaryButtonColors(),
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = stringResource(R.string.location_rationale_cancel),
                        )
                    }
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
