package app.reitan.nearby_mobility.features.map

import app.reitan.shared.models.Scooter
import app.reitan.nearby_mobility.tools.latLng
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ScooterClusterItem(val scooter: Scooter) : ClusterItem {
    override fun getPosition(): LatLng = scooter.position.latLng

    override fun getTitle(): String? = null

    override fun getSnippet(): String? = null
}
