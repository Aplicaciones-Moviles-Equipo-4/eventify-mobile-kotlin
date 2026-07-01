package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = BrandIndigo,
    onPrimary = Color.White,
    primaryContainer = BrandIndigoContainer,
    onPrimaryContainer = BrandIndigoDark,
    secondary = BrandIndigo,
    onSecondary = Color.White,
    tertiary = BrandGold,
    background = NeutralBackground,
    onBackground = Color(0xFF1C1B1F),
    surface = NeutralBackground,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = NeutralSurfaceVariant,
    outline = NeutralBorder
)

@Composable
fun EventifyfrontendkotlinTheme(
    // Force the brand identity: keep the app on-brand regardless of system dark mode
    // or Android 12+ dynamic (wallpaper-based) colors, so it looks consistent everywhere.
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
