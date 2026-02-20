package com.praxis.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.praxis.app.data.model.GoalNode
import com.praxis.app.ui.components.GoalTreeVisualization
import com.praxis.app.ui.components.CompactGoalTree
import com.praxis.app.ui.theme.getColor

/**
 * Example Home Screen with Goal Tree Visualization
 * 
 * Shows:
 * - Large interactive tree at top
 * - Goal progress cards
 * - Quick actions
 */
@Composable
fun HomeScreenWithTree(
    goals: List<GoalNode>,
    onGoalClick: (GoalNode) -> Unit = {},
    onAddProgress: (GoalNode) -> Unit = {}
) {
    var selectedView by remember { mutableStateOf(TreeView.FULL) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Journey",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    InfoButton(
                        title = "Your Goal Tree",
                        description = "Visualise every goal you're tracking. Tap '+' to log progress on any goal. Goals are colour-coded by life domain. The tree expands as you add sub-goals."
                    )
                }
            }
            
            // Tree Visualization Card
            item {
                TreeVisualizationCard(
                    goals = goals,
                    viewMode = selectedView,
                    onViewModeChange = { selectedView = it }
                )
            }
            
            // Overall Progress Summary
            item {
                OverallProgressCard(goals = goals)
            }
            
            // Individual Goal Cards
            item {
                Text(
                    text = "Your Goals",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }
            
            items(goals.size) { index ->
                GoalProgressCard(
                    goal = goals[index],
                    onClick = { onGoalClick(goals[index]) },
                    onAddProgress = { onAddProgress(goals[index]) }
                )
            }
        }
    }
}

enum class TreeView {
    FULL, COMPACT
}

@Composable
fun TreeVisualizationCard(
    goals: List<GoalNode>,
    viewMode: TreeView,
    onViewModeChange: (TreeView) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Goal Tree",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                // View toggle
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = viewMode == TreeView.FULL,
                        onClick = { onViewModeChange(TreeView.FULL) },
                        label = { Text("Full", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = viewMode == TreeView.COMPACT,
                        onClick = { onViewModeChange(TreeView.COMPACT) },
                        label = { Text("Compact", fontSize = 12.sp) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tree visualization with animation
            AnimatedContent(
                targetState = viewMode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                },
                label = "tree_view_transition"
            ) { view ->
                when (view) {
                    TreeView.FULL -> {
                        GoalTreeVisualization(
                            goals = goals,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                    }
                    TreeView.COMPACT -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CompactGoalTree(
                                goals = goals,
                                size = 150f
                            )
                        }
                    }
                }
            }
            
            // Legend
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Progress Guide:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LegendItem("Small dot", "Starting")
                    LegendItem("Far branch", "Progressing")
                    LegendItem("Large dot", "Advanced")
                }
            }
        }
    }
}

@Composable
fun LegendItem(label: String, description: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = description,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun OverallProgressCard(goals: List<GoalNode>) {
    val averageProgress = remember(goals) {
        if (goals.isEmpty()) 0
        else goals.map { it.progress }.average().toInt()
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Overall Progress",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${goals.size} active goals",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { averageProgress / 100f },
                    modifier = Modifier.size(60.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 6.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = "$averageProgress%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun GoalProgressCard(
    goal: GoalNode,
    onClick: () -> Unit,
    onAddProgress: () -> Unit
) {
    val color = goal.domain.getColor()
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = color
                    )
                    Text(
                        text = goal.domain.displayName(),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                IconButton(
                    onClick = onAddProgress,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add progress",
                        tint = color
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${goal.progress}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = color
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { goal.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = color,
                    trackColor = color.copy(alpha = 0.2f)
                )
            }
            
            // Details if present
            if (goal.details.isNotBlank()) {
                Text(
                    text = goal.details,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp),
                    maxLines = 2
                )
            }
        }
    }
}
