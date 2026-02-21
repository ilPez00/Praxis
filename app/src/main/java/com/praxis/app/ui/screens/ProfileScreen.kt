package com.praxis.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.praxis.app.data.model.Domain
import com.praxis.app.data.model.GoalNode
import com.praxis.app.data.model.User
import com.praxis.app.ui.theme.getColor

/**
 * Profile Tab
 * Shows the user's goal tree and progress with colored, expandable visualization
 */
@Composable
fun ProfileTab(user: User) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                InfoButton(
                    title = "Your Profile",
                    description = "Your public Praxis profile. Partners see your name, bio, and goal tree when you match. Keep your goals up-to-date to attract the most relevant accountability partners."
                )
            }

            Text(
                text = "${user.age} years old",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (user.bio.isNotBlank()) {
                Text(
                    text = user.bio,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = "Your Goal Tree",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(user.getPrimaryGoals()) { goal ->
            GoalTreeNode(goal, depth = 0)
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))

            // Stats Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Stats",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Active Goals", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${user.getPrimaryGoals().size}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Avg Progress", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val primaryGoals = user.getPrimaryGoals()
                            val avgProgress = if (primaryGoals.isEmpty()) 0 else primaryGoals.map { it.progress }.average().toInt()
                            Text("$avgProgress%", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalTreeNode(
    node: GoalNode,
    depth: Int = 0,
    modifier: Modifier = Modifier
) {
    val domainColor = node.domain.getColor()
    val indent = (depth * 24).dp
    var expanded by remember { mutableStateOf(true) }  // auto-expand roots, or remember per-node if persistent needed

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = indent, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Connector line (vertical + horizontal stub)
            if (depth > 0) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(48.dp)
                        .background(domainColor.copy(alpha = 0.3f))
                )
                Box(
                    modifier = Modifier
                        .size(width = 16.dp, height = 2.dp)
                        .background(domainColor.copy(alpha = 0.3f))
                )
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = domainColor.copy(alpha = 0.08f)
                ),
                border = BorderStroke(1.dp, domainColor.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = if (depth == 0) 4.dp else 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Expand/collapse icon + name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { if (node.subGoals.isNotEmpty()) expanded = !expanded }
                    ) {
                        Text(
                            text = node.name,
                            fontSize = (18.sp.value - (depth * 1)).sp,
                            fontWeight = if (depth == 0) FontWeight.Bold else FontWeight.Medium,
                            color = domainColor,
                            modifier = Modifier.weight(1f)
                        )

                        if (node.subGoals.isNotEmpty()) {
                            Icon(
                                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (expanded) "Collapse" else "Expand",
                                tint = domainColor
                            )
                        }
                    }

                    // Details
                    if (node.details.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = node.details,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Progress", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${node.progress}%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    LinearProgressIndicator(
                        progress = { node.progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        color = domainColor,
                        trackColor = domainColor.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Weight
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Priority", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${String.format("%.1f", node.weight)}x", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }

                    // Domain badge (small, top-right-ish)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = domainColor.copy(alpha = 0.7f),
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = node.domain.displayName(),
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Recurse children (sub-goals)
        AnimatedVisibility(
            visible = expanded && node.subGoals.isNotEmpty(),
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                node.subGoals.forEach { subGoal ->
                    GoalTreeNode(subGoal, depth + 1)
                }
            }
        }
    }
}
