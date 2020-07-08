package app.reitan.nearby_mobility

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.SwipeDismissFrameLayout
import com.mjohnsullivan.flutterwear.wear.FlutterAmbientCallback
import com.mjohnsullivan.flutterwear.wear.getChannel
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterFragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val fragmentContainer = findViewById<FrameLayout>(609893468)
        (fragmentContainer.parent as ViewGroup).removeView(fragmentContainer)
        val swipeDismissFrameLayout = SwipeDismissFrameLayout(this).apply {
            addView(fragmentContainer)
        }
        setContentView(swipeDismissFrameLayout)
    }
    
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return FlutterAmbientCallback(getChannel(flutterEngine))
    }
}
