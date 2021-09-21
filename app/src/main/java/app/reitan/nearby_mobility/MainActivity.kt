package app.reitan.nearby_mobility

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.DismissibleFrameLayout
import app.reitan.nearby_mobility.databinding.ActivityMainBinding
import app.reitan.nearby_mobility.ui.AppTheme
import app.reitan.nearby_mobility.ui.LocalWearMode
import app.reitan.nearby_mobility.ui.WearMode


class MainActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {

    private var ambientMode: WearMode by mutableStateOf(WearMode.Active)

    public override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)

            swipeDismissRootContainer.registerCallback(object :
                DismissibleFrameLayout.Callback() {
                override fun onDismissFinished(layout: DismissibleFrameLayout) {
                    // Hides view before exit to avoid stutter.
                    layout.visibility = View.GONE
                    finish()
                }
            })

            composeView.setContent {
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
