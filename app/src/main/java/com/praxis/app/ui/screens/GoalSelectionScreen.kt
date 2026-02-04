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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.praxis.app.data.model.Domain
import com.praxis.app.data.model.GoalNode
import java.util.UUID

/**
 * Goal Selection Screen
 * Users select up to 3 primary goals (free tier)
 * Implements the "guided interest tree" from whitepaper section 3.1
 */
@Composable
fun GoalSelectionScreen(
    goalTemplates: Map<Domain, List<String>>,
    onComplete: (List<GoalNode>) -> Unit
) {
    var selectedGoals by remember { mutableStateOf(listOf<GoalNode>()) }
    var currentStep by remember { mutableStateOf(0) } // 0 = select domain, 1 = select specific goal
    var selectedDomain by remember { mutableStateOf<Domain?>(null) }
    
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
        
        // Show selected goals
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
                                Icon(Icons.Default.CheckCircle, "Remove")
                            }
                        }
                    }
                }
            }
        }
        
        // Domain or Goal selection
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
                        }
                    )
                }
            }
        } else {
            // Step 2: Select Specific Goal
            selectedDomain?.let { domain ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { currentStep = 0 }) {
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
                            isSelected = selectedGoals.any { it.name == goalName },
                            onClick = {
                                if (selectedGoals.size < 3) {
                                    val newGoal = GoalNode(
                                        id = UUID.randomUUID().toString(),
                                        domain = domain,
                                        name = goalName,
                                        weight = 1.0,
                                        progress = 0
                                    )
                                    selectedGoals = selectedGoals + newGoal
                                    currentStep = 0
                                }
                            }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Complete button
        if (selectedGoals.isNotEmpty()) {
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
fun GoalCard(goalName: String, domain: Domain, isSelected: Boolean, onClick: () -> Unit) {
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
