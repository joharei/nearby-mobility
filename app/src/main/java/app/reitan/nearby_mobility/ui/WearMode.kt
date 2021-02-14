package app.reitan.nearby_mobility.ui

import android.os.Bundle
import androidx.compose.runtime.compositionLocalOf

val LocalWearMode = compositionLocalOf<WearMode> { WearMode.Active }

sealed class WearMode {
    object Active : WearMode()
    data class Ambient(val ambientDetails: Bundle?) : WearMode()
}