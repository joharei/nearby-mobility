package app.reitan.nearby_mobility.tools

import android.view.View
import android.widget.RelativeLayout
import com.google.android.gms.maps.MapView

fun MapView.alignZoomButtonsToBottom() {
    val zoomIn: View = findViewWithTag("GoogleMapZoomInButton") ?: return
    val zoomInOut: View = zoomIn.parent as View
    val rlp = RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT,
    )
    rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
    rlp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
    zoomInOut.layoutParams = rlp
}