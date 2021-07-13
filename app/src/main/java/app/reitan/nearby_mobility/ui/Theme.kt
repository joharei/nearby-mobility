package app.reitan.nearby_mobility.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.google.accompanist.insets.ProvideWindowInsets

private val DarkColorPalette = darkColors()

private val LightColorPalette = lightColors()

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    ProvideWindowInsets {
        MaterialTheme(
            colors = colors,
            content = content
        )
    }
}