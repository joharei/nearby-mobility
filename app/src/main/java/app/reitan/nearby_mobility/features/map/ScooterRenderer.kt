package app.reitan.nearby_mobility.features.map

import android.content.Context
import app.reitan.common.models.Operator
import app.reitan.nearby_mobility.R
import app.reitan.nearby_mobility.tools.bitmapDescriptorFromVector
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.BitmapDescriptor
import com.google.android.libraries.maps.model.Marker
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class ScooterRenderer(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<ScooterClusterItem>,
) : DefaultClusterRenderer<ScooterClusterItem>(context, map, clusterManager) {

    private var zoom = map.cameraPosition.zoom

    private val rydeIcon = bitmapDescriptorFromVector(context, R.drawable.ic_map_pin_ryde)
    private val tierIcon = bitmapDescriptorFromVector(context, R.drawable.ic_map_pin_tier)
    private val voiIcon = bitmapDescriptorFromVector(context, R.drawable.ic_map_pin_voi)
    private val unknownIcon = bitmapDescriptorFromVector(context, R.drawable.ic_map_pin_unknown)

    init {
        map.setOnCameraMoveListener { zoom = map.cameraPosition.zoom }
    }

    private val ScooterClusterItem.icon: BitmapDescriptor
        get() = when (scooter.operator) {
            Operator.Voi -> voiIcon
            Operator.Tier -> tierIcon
            Operator.Ryde -> rydeIcon
            else -> unknownIcon
        }

    override fun onBeforeClusterItemRendered(
        item: ScooterClusterItem,
        markerOptions: MarkerOptions,
    ) {
        markerOptions.icon(item.icon)
    }

    override fun onClusterItemUpdated(item: ScooterClusterItem, marker: Marker) {
        marker.setIcon(item.icon)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ScooterClusterItem>): Boolean {
        return zoom < 15 && super.shouldRenderAsCluster(cluster)
    }
}