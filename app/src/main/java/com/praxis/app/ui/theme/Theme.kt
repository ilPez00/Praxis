package com.praxis.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Praxis brand colors
private val PraxisPrimary = Color(0xFF6750A4)
private val PraxisSecondary = Color(0xFF625B71)
private val PraxisTertiary = Color(0xFF7D5260)

private val DarkColorScheme = darkColorScheme(
    primary = PraxisPrimary,
    secondary = PraxisSecondary,
    tertiary = PraxisTertiary
)

private val LightColorScheme = lightColorScheme(
    primary = PraxisPrimary,
    secondary = PraxisSecondary,
    tertiary = PraxisTertiary
)

@Composable
fun PraxisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
