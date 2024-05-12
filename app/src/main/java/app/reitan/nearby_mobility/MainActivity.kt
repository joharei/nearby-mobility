package app.reitan.nearby_mobility

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.wear.widget.DismissibleFrameLayout
import app.reitan.nearby_mobility.databinding.ActivityMainBinding
import app.reitan.nearby_mobility.ui.AppTheme


class MainActivity : ComponentActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                AppTheme {
                    App()
                }
            }
        }
    }
}
