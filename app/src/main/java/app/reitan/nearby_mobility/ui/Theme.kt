package app.reitan.nearby_mobility.ui

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme
import com.google.accompanist.insets.ProvideWindowInsets

@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    ProvideWindowInsets {
        MaterialTheme(
            content = content
        )
    }
}