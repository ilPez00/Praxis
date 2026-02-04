package com.praxis.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.praxis.app.data.model.Domain
import com.praxis.app.data.model.GoalNode
import java.utiimport androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush // if using gradients
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background // for .background()
import com.praxis.app.data.model.Domain // for getColor() extension
import com.praxis.app.ui.theme.getColor // if you put getColor in theme/Color.kt
/**
 * Goal Selection Screen
 * Users select up to 3 primary goals (free tier)
 * Implements the "guided interest tree" + detailed description
 */
@Composable
fun GoalSelectionScreen(
    goalTemplates: Map<Domain, List<String>>,
    onComplete: (List<GoalNode>) -> Unit
) {
    var selectedGoals by remember { mutableStateOf(listOf<GoalNode>()) }
    var currentStep by remember { mutableStateOf(0) } // 0 = select domain, 1 = select goal + details
    var selectedDomain by remember { mutableStateOf<Domain?>(null) }
    var pendingGoalName by remember { mutableStateOf("") }      // temp while editing details
    var pendingGoalDetails by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Build Your Goal Tree",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Select up to 3 primary goals (${selectedGoals.size}/3)",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Show selected goals summary
        if (selectedGoals.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Your Goals:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    selectedGoals.forEach { goal ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${goal.domain.displayName()}: ${goal.name}")
                            IconButton(onClick = {
                                selectedGoals = selectedGoals.filter { it.id != goal.id }
                            }) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Remove")
                            }
                        }
                    }
                }
            }
        }

        // Main content: domain / goal selection OR details input
        if (currentStep == 0) {
            // Step 1: Select Domain
            Text(
                text = "What area of life do you want to improve?",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(Domain.values().toList()) { domain ->
                    DomainCard(
                        domain = domain,
                        onClick = {
                            selectedDomain = domain
                            currentStep = 1
                            pendingGoalName = ""     // reset
                            pendingGoalDetails = ""
                        }
                    )
                }
            }
        } else {
            // Step 2: Select goal + enter details
            selectedDomain?.let { domain ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        currentStep = 0
                        pendingGoalName = ""
                        pendingGoalDetails = ""
                    }) {
                        Text("← Back")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Select a ${domain.displayName()} goal:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                LazyColumn {
                    items(goalTemplates[domain] ?: emptyList()) { goalName ->
                        GoalCard(
                            goalName = goalName,
                            domain = domain,
                            isSelected = selectedGoals.any { it.name == goalName && it.domain == domain },
                            onClick = {
                                // Instead of adding immediately → go to details mode
                                pendingGoalName = goalName
                                pendingGoalDetails = ""
                                // We stay on step 1, but show TextField below list
                            }
                        )
                    }
                }

                // Details input (shown only when a goal is pending)
                if (pendingGoalName.isNotBlank() && selectedGoals.size < 3) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Describe your goal in detail",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pendingGoalDetails,
                        onValueChange = { pendingGoalDetails = it },
                        label = { Text("Details (optional but recommended)") },
                        placeholder = { Text("E.g., Run a marathon under 4h by end of 2026, train 4× week...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        minLines = 4,
                        maxLines = 6
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val newGoal = GoalNode(
                                id = UUID.randomUUID().toString(),
                                domain = domain,
                                name = pendingGoalName,
                                weight = 1.0,
                                progress = 0,
                                details = pendingGoalDetails.trim()
                            )
                            selectedGoals = selectedGoals + newGoal
                            pendingGoalName = ""
                            pendingGoalDetails = ""
                            currentStep = 0  // back to domain selection
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Add Goal")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Tree preview + complete button
        if (selectedGoals.isNotEmpty()) {
            Text(
                text = "Your Goal Tree Preview",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    selectedGoals.forEach { goal ->
                        GoalTreeNode(goal, depth = 0)
                    }
                }
            }

            Button(
                onClick = { onComplete(selectedGoals) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Start Matching", fontSize = 16.sp)
            }
        }
    }
}

// Recursive tree node (simple indented + colored)
@Composable
fun GoalTreeNode(
    node: GoalNode,
    depth: Int = 0
) {
    val color = node.domain.getColor()
    val indent = (depth * 20).dp

    Column {
        Row(
            modifier = Modifier
                .padding(start = indent, top = 6.dp, bottom = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Simple connector line simulation
            if (depth > 0) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .padding(end = 8.dp)
                        .background(color.copy(alpha = 0.4f))
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, color),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = node.name,
                        fontWeight = FontWeight.Medium,
                        color = color
                    )
                    if (node.details.isNotBlank()) {
                        Text(
                            text = node.details,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Recurse children (if you later allow sub-goals)
        node.children.forEach { child ->
            GoalTreeNode(child, depth + 1)
        }
    }
}

// Your existing DomainCard & GoalCard (unchanged, just included for completeness)
@Composable
fun DomainCard(domain: Domain, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = domain.displayName(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text("→", fontSize = 20.sp)
        }
    }
}

@Composable
fun GoalCard(
    goalName: String,
    domain: Domain,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick, enabled = !isSelected),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
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
                    text = goalName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = domain.displayName(),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                contentDescription = if (isSelected) "Selected" else "Not selected",
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
