package app.reitan.nearby_mobility.tools

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

object InsetDefaults {
    val RoundScreenInset = 48.dp
}

/**
 * Applies the given [PaddingValues] only if the device has a round screen.
 */
@Stable
fun Modifier.roundScreenPadding(): Modifier = composed {
    if (isScreenRound()) {
        padding(PaddingValues(
            top = InsetDefaults.RoundScreenInset,
            bottom = InsetDefaults.RoundScreenInset,
        ))
    } else {
        this
    }
}

@Composable
fun isScreenRound(): Boolean {
    return LocalContext.current.resources.configuration.isScreenRound
}