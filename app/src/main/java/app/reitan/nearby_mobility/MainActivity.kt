package app.reitan.nearby_mobility

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import app.reitan.nearby_mobility.ui.AppTheme


class MainActivity : ComponentActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                App()
            }
        }
    }
}
