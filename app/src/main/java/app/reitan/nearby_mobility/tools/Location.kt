package app.reitan.nearby_mobility.tools

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import app.reitan.common.models.LatLon
import app.reitan.common.models.LatLonBounds
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

@RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
suspend fun lastLocation(context: Context): Location? {
    return withTimeoutOrNull(1.seconds) {
        LocationServices.getFusedLocationProviderClient(context).lastLocation.await()
    }
}

val Location.latLng: LatLng get() = LatLng(latitude, longitude)

val LatLon.latLng: LatLng get() = LatLng(latitude, longitude)

val LatLng.latLon: LatLon get() = LatLon(latitude, longitude)

val LatLngBounds.latLonBounds: LatLonBounds get() = LatLonBounds(southwest.latLon, northeast.latLon)
