package com.praxis.app.ui.theme
import androidx.compose.ui.graphics.Color
import com.praxis.app.data.model.Domain
fun Domain.getColor(): Color = when (this) {
    Domain.CAREER       -> Color(0xFF4CAF50)  // Green
    Domain.FITNESS      -> Color(0xFFE57373)  // Red-ish
    Domain.MENTAL_HEALTH -> Color(0xFF64B5F6) // Blue
    Domain.RELATIONSHIPS -> Color(0xFFAB47BC) // Purple
    Domain.FINANCE      -> Color(0xFFFFB300)  // Amber
    Domain.SPIRITUALITY -> Color(0xFF26A69A)  // Teal
    Domain.EDUCATION    -> Color(0xFFEC407A)  // Pink
    // Add others as needed
    else                -> Color(0xFF78909C)  // Blue Grey fallback
}