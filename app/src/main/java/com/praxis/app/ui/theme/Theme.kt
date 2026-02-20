package com.praxis.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ─── Brand palette — mirrors praxis_webapp MUI theme ──────────────────────────

// Primary: Amber-gold (#F59E0B)
private val Amber500    = Color(0xFFF59E0B)
private val AmberDark   = Color(0xFF92400E)
private val AmberLight  = Color(0xFFFDE68A)
private val AmberOnPrim = Color(0xFF0A0B14)

// Secondary: Electric violet (#8B5CF6)
private val Violet400       = Color(0xFF8B5CF6)
private val VioletDark      = Color(0xFF4C1D95)
private val VioletLight     = Color(0xFFEDE9FE)

// Tertiary: Emerald (#10B981) — used for success / streak
private val Emerald500      = Color(0xFF10B981)
private val EmeraldDark     = Color(0xFF065F46)
private val EmeraldLight    = Color(0xFFD1FAE5)

// Backgrounds — deep dark, matching webapp #0A0B14 / #111827 / #1F2937
private val BgDeep          = Color(0xFF0A0B14)
private val BgSurface       = Color(0xFF111827)
private val BgSurfaceVar    = Color(0xFF1F2937)

// Text
private val TextPrimary     = Color(0xFFF9FAFB)
private val TextSecondary   = Color(0xFF9CA3AF)

// Borders
private val OutlineColor    = Color(0xFF374151)
private val OutlineVarColor = Color(0xFF1F2937)

// Error
private val ErrorColor      = Color(0xFFEF4444)

// ─── Single dark color scheme (Praxis is dark-only, like the webapp) ──────────

private val PraxisColorScheme = darkColorScheme(
    primary              = Amber500,
    onPrimary            = AmberOnPrim,
    primaryContainer     = AmberDark,
    onPrimaryContainer   = AmberLight,

    secondary            = Violet400,
    onSecondary          = Color.White,
    secondaryContainer   = VioletDark,
    onSecondaryContainer = VioletLight,

    tertiary             = Emerald500,
    onTertiary           = Color.White,
    tertiaryContainer    = EmeraldDark,
    onTertiaryContainer  = EmeraldLight,

    background           = BgDeep,
    onBackground         = TextPrimary,

    surface              = BgSurface,
    onSurface            = TextPrimary,
    surfaceVariant       = BgSurfaceVar,
    onSurfaceVariant     = TextSecondary,

    error                = ErrorColor,
    onError              = Color.White,

    outline              = OutlineColor,
    outlineVariant       = OutlineVarColor,

    inverseSurface       = TextPrimary,
    inverseOnSurface     = BgDeep,
    inversePrimary       = AmberDark,
)

@Composable
fun PraxisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PraxisColorScheme,
        typography  = PraxisTypography,
        shapes      = PraxisShapes,
        content     = content
    )
}
