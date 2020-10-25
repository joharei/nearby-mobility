package app.reitan.nearby_mobility

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.SwipeDismissFrameLayout
import app.reitan.nearby_mobility.databinding.ActivityMapsBinding
import app.reitan.nearby_mobility.ui.ProvideDisplayInsets
import app.reitan.nearby_mobility.ui.WearMode
import app.reitan.nearby_mobility.ui.WearModeAmbient
import app.reitan.nearby_mobility.ui.permissionState


class MapsActivity : AppCompatActivity(), AmbientModeSupport.AmbientCallbackProvider {

    private var ambientMode: WearMode by mutableStateOf(WearMode.Active)

    public override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        ActivityMapsBinding.inflate(layoutInflater).apply {
            setContentView(root)

            swipeDismissRootContainer.addCallback(object :
                SwipeDismissFrameLayout.Callback() {
                override fun onDismissed(layout: SwipeDismissFrameLayout?) {
                    // Hides view before exit to avoid stutter.
                    layout?.visibility = View.GONE
                    finish()
                }
            })

            composeView.setContent {
                val locationPermission = permissionState(Manifest.permission.ACCESS_FINE_LOCATION)
                onCommit {
                    if (!locationPermission.hasPermission && !locationPermission.shouldShowRationale) {
                        locationPermission.launchPermissionRequest()
                    }
                }

                ProvideDisplayInsets {
                    Providers(WearModeAmbient provides ambientMode) {
                        App()
                    }
                }
            }
        }

        AmbientModeSupport.attach(this).apply {
            ambientMode = if (isAmbient) WearMode.Ambient(null) else WearMode.Active
        }
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback =
        object : AmbientModeSupport.AmbientCallback() {
            override fun onEnterAmbient(ambientDetails: Bundle?) {
                ambientMode = WearMode.Ambient(ambientDetails)
            }

            override fun onExitAmbient() {
                ambientMode = WearMode.Active
            }
        }
}