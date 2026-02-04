package com.praxis.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Praxis brand colors - refined Apple-style palette
private val PraxisPrimary = Color(0xFF007AFF)  // iOS Blue
private val PraxisSecondary = Color(0xFF5856D6) // iOS Purple
private val PraxisTertiary = Color(0xFFFF9500)  // iOS Orange

// Light theme colors
private val LightSurface = Color(0xFFFAFAFA)
private val LightSurfaceVariant = Color(0xFFF2F2F7)
private val LightBackground = Color(0xFFFFFFFF)
private val LightOnSurface = Color(0xFF1C1C1E)
private val LightOnSurfaceVariant = Color(0xFF8E8E93)

// Dark theme colors
private val DarkSurface = Color(0xFF1C1C1E)
private val DarkSurfaceVariant = Color(0xFF2C2C2E)
private val DarkBackground = Color(0xFF000000)
private val DarkOnSurface = Color(0xFFFFFFFF)
private val DarkOnSurfaceVariant = Color(0xFF8E8E93)

private val DarkColorScheme = darkColorScheme(
    primary = PraxisPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0051A8),
    onPrimaryContainer = Color(0xFFD4E3FF),
    
    secondary = PraxisSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3D3C84),
    onSecondaryContainer = Color(0xFFE1E0FF),
    
    tertiary = PraxisTertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB36A00),
    onTertiaryContainer = Color(0xFFFFDDB3),
    
    background = DarkBackground,
    onBackground = DarkOnSurface,
    
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    
    error = Color(0xFFFF453A),
    onError = Color.White,
    
    outline = Color(0xFF48484A),
    outlineVariant = Color(0xFF3A3A3C)
)

private val LightColorScheme = lightColorScheme(
    primary = PraxisPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4E3FF),
    onPrimaryContainer = Color(0xFF001C3A),
    
    secondary = PraxisSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE1E0FF),
    onSecondaryContainer = Color(0xFF1C1B5B),
    
    tertiary = PraxisTertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDDB3),
    onTertiaryContainer = Color(0xFF2E1500),
    
    background = LightBackground,
    onBackground = LightOnSurface,
    
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    
    error = Color(0xFFFF3B30),
    onError = Color.White,
    
    outline = Color(0xFFC6C6C8),
    outlineVariant = Color(0xFFE5E5EA)
)

@Composable
fun PraxisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic color for consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PraxisTypography,
        shapes = PraxisShapes,
        content = content
    )
}
