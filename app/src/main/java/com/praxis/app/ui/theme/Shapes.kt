package com.praxis.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Apple-style shapes with consistent rounded corners
val PraxisShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),   // Small chips, icons
    small = RoundedCornerShape(12.dp),        // Buttons, small cards
    medium = RoundedCornerShape(16.dp),       // Cards, dialogs
    large = RoundedCornerShape(20.dp),        // Large cards, bottom sheets
    extraLarge = RoundedCornerShape(28.dp)    // Full-screen dialogs
)
