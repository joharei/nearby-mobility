package app.reitan.nearby_mobility.features.map

import android.content.Context
import app.reitan.nearby_mobility.R
import app.reitan.nearby_mobility.tools.bitmapDescriptorFromVector
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class ScooterRenderer(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<ScooterClusterItem>,
) : DefaultClusterRenderer<ScooterClusterItem>(context, map, clusterManager) {

    private val pinIcon = bitmapDescriptorFromVector(context, R.drawable.ic_map_pin)

    override fun onBeforeClusterItemRendered(
        item: ScooterClusterItem,
        markerOptions: MarkerOptions
    ) {
        markerOptions.icon(pinIcon)
    }

    override fun onClusterItemUpdated(item: ScooterClusterItem, marker: Marker) {
        marker.setIcon(pinIcon)
    }
}