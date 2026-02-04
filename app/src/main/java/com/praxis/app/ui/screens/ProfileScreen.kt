package com.praxis.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.praxis.app.data.model.User

/**
 * Profile Tab
 * Shows the user's goal tree and progress
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
            Text(
                text = user.name,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            
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
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            Text(
                text = "Your Goal Tree",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        items(user.getPrimaryGoals()) { goal ->
            GoalTreeCard(goal)
        }
        
        item {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Stats
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                            val avgProgress = user.getPrimaryGoals().map { it.progress }.average().toInt()
                            Text("$avgProgress%", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalTreeCard(goal: com.praxis.app.data.model.GoalNode) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Domain badge
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = goal.domain.displayName(),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Goal name
            Text(
                text = goal.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Progress", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${goal.progress}%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                
                LinearProgressIndicator(
                    progress = goal.progress / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Weight indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Priority Weight", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${String.format("%.1f", goal.weight)}x", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            
            // Sub-goals
            if (goal.subGoals.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Sub-goals:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                goal.subGoals.forEach { subGoal ->
                    Row(modifier = Modifier.padding(start = 16.dp, top = 4.dp)) {
                        Text("• ", fontSize = 14.sp)
                        Text("${subGoal.name} (${subGoal.progress}%)", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
