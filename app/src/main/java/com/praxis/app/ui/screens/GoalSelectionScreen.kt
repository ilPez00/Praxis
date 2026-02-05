package com.praxis.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.praxis.app.data.model.Domain
import com.praxis.app.data.model.GoalCategory
import com.praxis.app.data.model.GoalNode
import com.praxis.app.data.model.GoalSubcategory
import com.praxis.app.data.model.SpecificGoal
import com.praxis.app.ui.theme.getColor
import java.util.UUID

/**
 * Goal Selection Screen
 * Users select up to 3 primary goals (free tier)
 * Implements 4-level hierarchy: Macrogroup → Intermediate → Specific → Custom Details
 */
@Composable
fun GoalSelectionScreen(
    goalTemplates: Map<Domain, List<GoalCategory>>,
    onComplete: (List<GoalNode>) -> Unit
) {
    var selectedGoals by remember { mutableStateOf(listOf<GoalNode>()) }
    var currentStep by remember { mutableStateOf(0) } 
    // 0 = select domain, 1 = select category, 2 = select subcategory, 3 = select specific goal, 4 = details
    var selectedDomain by remember { mutableStateOf<Domain?>(null) }
    var selectedCategory by remember { mutableStateOf<GoalCategory?>(null) }
    var selectedSubcategory by remember { mutableStateOf<GoalSubcategory?>(null) }
    var selectedSpecificGoal by remember { mutableStateOf<SpecificGoal?>(null) }
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

        // Main content: 4-level navigation
        when (currentStep) {
            0 -> {
                // Step 1: Select Domain (Macrogroup)
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
            }
            
            1 -> {
                // Step 2: Select Category (Intermediate Subgroup)
                selectedDomain?.let { domain ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = {
                            currentStep = 0
                            selectedCategory = null
                        }) {
                            Text("← Back")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${domain.emoji()} Select ${domain.displayName()} Category",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    LazyColumn {
                        items(goalTemplates[domain] ?: emptyList()) { category ->
                            CategoryCard(
                                category = category,
                                onClick = {
                                    selectedCategory = category
                                    currentStep = 2
                                }
                            )
                        }
                    }
                }
            }
            
            2 -> {
                // Step 3: Select Subcategory (Specific Subgroup)
                selectedCategory?.let { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = {
                            currentStep = 1
                            selectedSubcategory = null
                        }) {
                            Text("← Back")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${category.emoji} ${category.name}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    LazyColumn {
                        items(category.subcategories) { subcategory ->
                            SubcategoryCard(
                                subcategory = subcategory,
                                onClick = {
                                    selectedSubcategory = subcategory
                                    currentStep = 3
                                }
                            )
                        }
                    }
                }
            }
            
            3 -> {
                // Step 4: Select Specific Goal
                selectedSubcategory?.let { subcategory ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = {
                            currentStep = 2
                            selectedSpecificGoal = null
                        }) {
                            Text("← Back")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${subcategory.emoji} ${subcategory.name}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    LazyColumn {
                        items(subcategory.specificGoals) { specificGoal ->
                            SpecificGoalCard(
                                specificGoal = specificGoal,
                                isSelected = selectedGoals.any { it.name == specificGoal.name },
                                onClick = {
                                    selectedSpecificGoal = specificGoal
                                    currentStep = 4
                                }
                            )
                        }
                    }
                }
            }
            
            4 -> {
                // Step 5: Enter Custom Details
                selectedSpecificGoal?.let { specificGoal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = {
                            currentStep = 3
                            pendingGoalDetails = ""
                        }) {
                            Text("← Back")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${specificGoal.emoji} ${specificGoal.name}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Text(
                        text = "Describe your goal in detail",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
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
                            selectedDomain?.let { domain ->
                                val newGoal = GoalNode(
                                    id = UUID.randomUUID().toString(),
                                    domain = domain,
                                    name = specificGoal.name,
                                    emoji = specificGoal.emoji,
                                    level = 0,
                                    weight = 1.0,
                                    progress = 0,
                                    details = pendingGoalDetails.trim()
                                )
                                selectedGoals = selectedGoals + newGoal
                                // Reset to start
                                currentStep = 0
                                selectedDomain = null
                                selectedCategory = null
                                selectedSubcategory = null
                                selectedSpecificGoal = null
                                pendingGoalDetails = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedGoals.size < 3
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

// Enhanced tree node with proper tree structure and connecting lines
@Composable
fun GoalTreeNode(
    node: GoalNode,
    depth: Int = 0,
    isLast: Boolean = true,
    parentPath: List<Boolean> = emptyList()
) {
    val color = node.domain.getColor()
    val indent = (depth * 32).dp

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = indent, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Draw tree connector lines
            if (depth > 0) {
                Box(modifier = Modifier.width(32.dp).height(40.dp)) {
                    // Vertical line from parent (if not last child)
                    parentPath.forEachIndexed { index, wasLast ->
                        if (!wasLast && index < depth - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .fillMaxHeight()
                                    .offset(x = ((index + 1) * 32).dp)
                                    .background(color.copy(alpha = 0.3f))
                            )
                        }
                    }
                    
                    // Horizontal line to node
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(2.dp)
                            .offset(y = 20.dp)
                            .background(color.copy(alpha = 0.5f))
                    )
                    
                    // Vertical line for this node (if not last)
                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .fillMaxHeight()
                                .background(color.copy(alpha = 0.3f))
                        )
                    }
                    
                    // Partial vertical line to connect to horizontal
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(21.dp)
                            .background(color.copy(alpha = 0.5f))
                    )
                }
            }

            // Node content card
            Card(
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
                border = BorderStroke(2.dp, color),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Emoji
                    if (node.emoji.isNotEmpty()) {
                        Text(
                            text = node.emoji,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = node.name,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
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
        }

        // Recurse children with updated path tracking
        node.subGoals.forEachIndexed { index, child ->
            GoalTreeNode(
                child,
                depth + 1,
                isLast = index == node.subGoals.size - 1,
                parentPath = parentPath + listOf(isLast)
            )
        }
    }
}

// Cards for each level of the hierarchy
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = domain.emoji(),
                    fontSize = 32.sp,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Text(
                    text = domain.displayName(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text("→", fontSize = 20.sp)
        }
    }
}

@Composable
fun CategoryCard(category: GoalCategory, onClick: () -> Unit) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = category.emoji,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = category.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text("→", fontSize = 20.sp)
        }
    }
}

@Composable
fun SubcategoryCard(subcategory: GoalSubcategory, onClick: () -> Unit) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = subcategory.emoji,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = subcategory.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text("→", fontSize = 20.sp)
        }
    }
}

@Composable
fun SpecificGoalCard(
    specificGoal: SpecificGoal,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = specificGoal.emoji,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = specificGoal.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
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
