package app.reitan.nearby_mobility

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.compose.runtime.*
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.SwipeDismissFrameLayout
import app.reitan.nearby_mobility.databinding.ActivityMainBinding
import app.reitan.nearby_mobility.tools.permissionState
import app.reitan.nearby_mobility.ui.AppTheme
import app.reitan.nearby_mobility.ui.LocalWearMode
import app.reitan.nearby_mobility.ui.WearMode


class MainActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {

    private var ambientMode: WearMode by mutableStateOf(WearMode.Active)

    public override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        ActivityMainBinding.inflate(layoutInflater).apply {
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
                SideEffect {
                    // TODO: handle locationPermission.shouldShowRationale
                    if (!locationPermission.hasPermission) {
                        locationPermission.launchPermissionRequest()
                    }
                }

                CompositionLocalProvider(LocalWearMode provides ambientMode) {
                    AppTheme {
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
