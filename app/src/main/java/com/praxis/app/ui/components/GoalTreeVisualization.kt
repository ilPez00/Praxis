package com.praxis.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.praxis.app.data.model.GoalNode
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GoalTreeVisualization(
    goals: List<GoalNode>,
    modifier: Modifier = Modifier,
    growthProgress: Float = 1f
) {
    val density = LocalDensity.current
    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val centerOrigin = Offset(size.width / 2, size.height / 2)

        drawGoalNode(
            center = centerOrigin,
            radius = with(density) { 32.dp.toPx() },
            growthProgress = growthProgress,
            color = primaryColor
        )

        goals.forEachIndexed { index, goal ->
            val angle = (index * 60f - 120f) * (Math.PI / 180f)
            val childOffset = Offset(
                centerOrigin.x + with(density) { 180.dp.toPx() } * cos(angle.toFloat()),
                centerOrigin.y + with(density) { 140.dp.toPx() } * sin(angle.toFloat())
            )

            drawLine(
                color = primaryColor.copy(alpha = 0.6f),
                start = centerOrigin,
                end = childOffset,
                strokeWidth = with(density) { 4.dp.toPx() },
                cap = StrokeCap.Round
            )

            drawGoalNode(
                center = childOffset,
                radius = with(density) { 24.dp.toPx() },
                growthProgress = growthProgress * 0.9f,
                color = primaryColor
            )
        }
    }
}

private fun DrawScope.drawGoalNode(
    center: Offset,
    radius: Float,
    growthProgress: Float,
    color: Color
) {
    drawCircle(
        color = color,
        radius = radius * growthProgress,
        center = center
    )
}
