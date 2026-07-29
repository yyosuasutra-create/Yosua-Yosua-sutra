package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val DarkColorScheme = darkColorScheme(
    primary = NeonViolet,
    onPrimary = TextPrimary,
    primaryContainer = GalaxyBlue,
    onPrimaryContainer = GlowingCyan,
    secondary = GlowingCyan,
    onSecondary = SpaceBlack,
    secondaryContainer = SpaceCardBg,
    onSecondaryContainer = TextPrimary,
    tertiary = LaserPink,
    onTertiary = TextPrimary,
    background = SpaceBlack,
    onBackground = TextPrimary,
    surface = SpaceDarkBg,
    onSurface = TextPrimary,
    surfaceVariant = SpaceCardBg,
    onSurfaceVariant = TextSecondary,
    outline = SpaceCardBorder
)

@Composable
fun GalaksiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SpaceBlack.toArgb()
            window.navigationBarColor = SpaceBlack.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep MyApplicationTheme alias so existing imports won't break
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    GalaksiTheme(darkTheme = darkTheme, content = content)
}
