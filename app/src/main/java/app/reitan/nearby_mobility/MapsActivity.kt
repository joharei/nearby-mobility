package app.reitan.nearby_mobility

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.wear.widget.SwipeDismissFrameLayout
import app.reitan.nearby_mobility.databinding.ActivityMapsBinding

class MapsActivity : Activity(), OnMapReadyCallback {

    /**
     * Map is initialized when it"s fully loaded and ready to be used.
     * See [onMapReady]
     */
    private lateinit var mMap: GoogleMap
    private lateinit var swipeDismissRootContainer: SwipeDismissFrameLayout
    private lateinit var binding: ActivityMapsBinding

    public override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enables the Swipe-To-Dismiss Gesture via the root layout (SwipeDismissFrameLayout).
        // Swipe-To-Dismiss is a standard pattern in Wear for closing an app and needs to be
        // manually enabled for any Google Maps Activity. For more information, review our docs:
        // https://developer.android.com/training/wearables/ui/exit.html
        swipeDismissRootContainer = binding.swipeDismissRootContainer
        swipeDismissRootContainer.addCallback(object : SwipeDismissFrameLayout.Callback() {
            override fun onDismissed(layout: SwipeDismissFrameLayout?) {
                // Hides view before exit to avoid stutter.
                layout?.visibility = View.GONE
                finish()
            }
        })

        // Adjusts margins to account for the system window insets when they become available.
        swipeDismissRootContainer.setOnApplyWindowInsetsListener { _, insetsArg ->
            val insets = swipeDismissRootContainer.onApplyWindowInsets(insetsArg)
            val mapContainer = binding.mapContainer
            val params = mapContainer.layoutParams as FrameLayout.LayoutParams

            // Add Wearable insets to FrameLayout container holding map as margins
            params.setMargins(
                insets.systemWindowInsetLeft,
                insets.systemWindowInsetTop,
                insets.systemWindowInsetRight,
                insets.systemWindowInsetBottom
            )
            mapContainer.layoutParams = params

            insets
        }

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
        @Suppress("Deprecation")
        // Suppressing deprecation since WearableActivity doesn't extend FragmentManager, thus
        // getSupportFragmentManager can't be used
        val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Map is ready to be used.
        mMap = googleMap

        // Inform user how to close app (Swipe-To-Close).
        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(getApplicationContext(), R.string.intro_text, duration)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()

        // Adds a marker in Sydney, Australia and moves the camera.
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}