package app.reitan.nearby_mobility

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout


class HorizontalScrollWorkaround @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    override fun canScrollHorizontally(direction: Int): Boolean {
        return true
    }
}