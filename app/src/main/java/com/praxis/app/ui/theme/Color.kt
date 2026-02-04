package com.praxis.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.praxis.app.data.model.Domain

fun Domain.getColor(): Color = when (this) {
    Domain.CAREER -> Color(0xFF4CAF50)  // Green
    Domain.INVESTING -> Color(0xFF26A69A)  // Teal
    Domain.FITNESS -> Color(0xFFE57373)  // Red-ish
    Domain.ACADEMICS -> Color(0xFFEC407A)  // Pink
    Domain.MENTAL_HEALTH -> Color(0xFF64B5F6) // Blue
    Domain.PHILOSOPHY -> Color(0xFF78909C)  // Blue Grey
    Domain.CULTURE_HOBBIES -> Color(0xFF9CCC65) // Light Green
    Domain.INTIMACY_ROMANCE -> Color(0xFFFFA726) // Orange
    Domain.FRIENDSHIP_SOCIAL -> Color(0xFFAB47BC) // Purple
}