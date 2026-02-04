package com.praxis.app.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.praxis.app.data.model.GoalNode

@Composable
fun GoalLabel(goal: GoalNode, modifier: Modifier = Modifier) {
    Text(text = goal.name, modifier = modifier)
}
