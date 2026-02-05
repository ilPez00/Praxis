package com.praxis.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.praxis.app.data.model.Domain
import com.praxis.app.data.model.GoalNode
import com.praxis.app.ui.theme.getColor // if your getColor is in theme/Color.kt
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun GoalTreeVisualization(
    goals: List<GoalNode>,
    modifier: Modifier = Modifier,
    showLabels: Boolean = true
) {
    var isVisible by remember { mutableStateOf(false) }
    val growthProgress by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    LaunchedEffect(Unit) { isVisible = true }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Only drawing commands here — NO composables
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            // ... your center dot, branches, lines, dots ...

            // NO GoalLabel calls here!
        }

        // Labels as normal Compose (outside Canvas)
        if (showLabels) {
            goals.forEachIndexed { index, goal ->
                // calculate label position (same math as before)
                val angle = -90f + (360f / goals.size * index)
                val rad = Math.toRadians(angle.toDouble())
                val distance = 100.dp.value + 40.dp.value * growthProgress // Simplified distance
                val labelX = (cos(rad) * distance).toFloat().dp
                val labelY = (sin(rad) * distance).toFloat().dp

                GoalLabel(
                    goal = goal,
                    modifier = Modifier
                        .offset(x = labelX, y = labelY)
                        .width(120.dp)
                )
            }
        }
    }
}
