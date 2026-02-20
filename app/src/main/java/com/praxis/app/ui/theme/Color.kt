package com.praxis.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.praxis.app.data.model.Domain

// Domain colors — matches praxis_webapp theme exactly
fun Domain.getColor(): Color = when (this) {
    Domain.CAREER            -> Color(0xFFF59E0B)  // Amber
    Domain.INVESTING         -> Color(0xFF3B82F6)  // Blue
    Domain.FITNESS           -> Color(0xFFEF4444)  // Red
    Domain.ACADEMICS         -> Color(0xFF8B5CF6)  // Violet
    Domain.MENTAL_HEALTH     -> Color(0xFF10B981)  // Emerald
    Domain.PHILOSOPHY        -> Color(0xFFEC4899)  // Pink
    Domain.CULTURE_HOBBIES   -> Color(0xFFA855F7)  // Purple
    Domain.INTIMACY_ROMANCE  -> Color(0xFFF97316)  // Orange
    Domain.FRIENDSHIP_SOCIAL -> Color(0xFF06B6D4)  // Cyan
}
