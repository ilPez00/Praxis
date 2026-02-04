package com.praxis.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.praxis.app.data.model.Domain
import com.praxis.app.data.model.GoalNode
import com.praxis.app.ui.theme.getColor
import java.util.UUID

/**
 * Goal Selection Screen - Apple-style redesign
 * Requires exactly 3 goals to be selected
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalSelectionScreen(
    goalTemplates: Map<Domain, List<String>>,
    onComplete: (List<GoalNode>) -> Unit
) {
    var selectedGoals by remember { mutableStateOf(listOf<GoalNode>()) }
    var currentStep by remember { mutableStateOf(0) }
    var selectedDomain by remember { mutableStateOf<Domain?>(null) }
    var pendingGoalName by remember { mutableStateOf("") }
    var pendingGoalDetails by remember { mutableStateOf("") }
    var showGoalsSummary by remember { mutableStateOf(true) }

    // Calculate if we can proceed
    val canComplete = selectedGoals.size == 3
    val slotsRemaining = 3 - selectedGoals.size

    Box(
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Compact Header
            Text(
                text = "Your Goals",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )

            // Progress indicator with requirement message
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (index < selectedGoals.size)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                    Text(
                        text = "${selectedGoals.size}/3",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Requirement message
                AnimatedVisibility(
                    visible = slotsRemaining > 0,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = if (slotsRemaining == 3) 
                            "Select 3 primary goals to begin"
                        else 
                            "Select $slotsRemaining more goal${if (slotsRemaining > 1) "s" else ""}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                
                // Completion message
                AnimatedVisibility(
                    visible = canComplete,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Ready to start your journey!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Collapsible selected goals chips
            AnimatedVisibility(
                visible = selectedGoals.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Selected",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(
                            onClick = { showGoalsSummary = !showGoalsSummary },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = if (showGoalsSummary) "Hide" else "Show",
                                modifier = Modifier.scale(if (showGoalsSummary) 1f else -1f),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = showGoalsSummary,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedGoals.forEach { goal ->
                                GoalChip(
                                    goal = goal,
                                    onRemove = {
                                        selectedGoals = selectedGoals.filter { it.id != goal.id }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Main content with smooth transitions
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { if (targetState > initialState) it else -it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn() togetherWith
                            slideOutHorizontally(
                                targetOffsetX = { if (targetState > initialState) -it else it },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ) + fadeOut()
                },
                label = "step_transition"
            ) { step ->
                when (step) {
                    0 -> DomainSelectionStep(
                        canSelectMore = selectedGoals.size < 3,
                        onDomainSelected = { domain ->
                            selectedDomain = domain
                            currentStep = 1
                            pendingGoalName = ""
                            pendingGoalDetails = ""
                        }
                    )
                    1 -> GoalSelectionStep(
                        domain = selectedDomain!!,
                        goalTemplates = goalTemplates[selectedDomain] ?: emptyList(),
                        selectedGoals = selectedGoals,
                        pendingGoalName = pendingGoalName,
                        pendingGoalDetails = pendingGoalDetails,
                        onBack = {
                            currentStep = 0
                            pendingGoalName = ""
                            pendingGoalDetails = ""
                        },
                        onGoalSelected = { goalName ->
                            pendingGoalName = goalName
                            pendingGoalDetails = ""
                        },
                        onDetailsChanged = { pendingGoalDetails = it },
                        onAddGoal = {
                            if (selectedGoals.size < 3) {
                                val newGoal = GoalNode(
                                    id = UUID.randomUUID().toString(),
                                    domain = selectedDomain!!,
                                    name = pendingGoalName,
                                    weight = 1.0,
                                    progress = 0,
                                    details = pendingGoalDetails.trim()
                                )
                                selectedGoals = selectedGoals + newGoal
                                pendingGoalName = ""
                                pendingGoalDetails = ""
                                currentStep = 0
                            }
                        },
                        canAddMore = selectedGoals.size < 3
                    )
                }
            }
        }

        // Floating Complete Button (only when 3 goals selected)
        AnimatedVisibility(
            visible = canComplete,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp)
        ) {
            Button(
                onClick = { onComplete(selectedGoals) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Text(
                    "Continue",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun GoalChip(goal: GoalNode, onRemove: () -> Unit) {
    val color = goal.domain.getColor()
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Column {
                    Text(
                        text = goal.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = goal.domain.displayName(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DomainSelectionStep(
    canSelectMore: Boolean,
    onDomainSelected: (Domain) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Choose an area",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (!canSelectMore) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "You've selected 3 goals! Remove one to add another.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = Domain.values().toList(),
                key = { it.name }
            ) { domain ->
                DomainCard(
                    domain = domain,
                    enabled = canSelectMore,
                    onClick = { if (canSelectMore) onDomainSelected(domain) }
                )
            }
        }
    }
}

@Composable
fun GoalSelectionStep(
    domain: Domain,
    goalTemplates: List<String>,
    selectedGoals: List<GoalNode>,
    pendingGoalName: String,
    pendingGoalDetails: String,
    onBack: () -> Unit,
    onGoalSelected: (String) -> Unit,
    onDetailsChanged: (String) -> Unit,
    onAddGoal: () -> Unit,
    canAddMore: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onBack,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("← Back", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = domain.displayName(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Select a goal",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Goal list or details input
        if (pendingGoalName.isBlank()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = goalTemplates,
                    key = { it }
                ) { goalName ->
                    GoalCard(
                        goalName = goalName,
                        domain = domain,
                        isSelected = selectedGoals.any { it.name == goalName && it.domain == domain },
                        enabled = canAddMore,
                        onClick = { if (canAddMore) onGoalSelected(goalName) }
                    )
                }
            }
        } else {
            // Details input with smooth animation
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
            ) {
                Text(
                    text = "Tell us more",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Help us understand your goal better (optional)",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = pendingGoalDetails,
                    onValueChange = onDetailsChanged,
                    label = { Text("Goal details") },
                    placeholder = { Text("E.g., Run a marathon under 4h by December 2026...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = domain.getColor(),
                        focusedLabelColor = domain.getColor()
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onGoalSelected("") },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Skip")
                    }
                    Button(
                        onClick = onAddGoal,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Add Goal")
                    }
                }
            }
        }
    }
}

@Composable
fun DomainCard(
    domain: Domain,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val color = domain.getColor()
    
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = if (enabled) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color.copy(alpha = if (enabled) 0.15f else 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = if (enabled) 1f else 0.3f))
                    )
                }
                Text(
                    text = domain.displayName(),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) 
                        MaterialTheme.colorScheme.onSurface 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.scale(scaleX = -1f, scaleY = 1f),
                tint = if (enabled)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun GoalCard(
    goalName: String,
    domain: Domain,
    isSelected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val color = domain.getColor()
    
    Surface(
        onClick = onClick,
        enabled = enabled && !isSelected,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected)
            color.copy(alpha = 0.12f)
        else
            MaterialTheme.colorScheme.surface,
        tonalElevation = if (isSelected) 0.dp else 2.dp,
        border = if (isSelected)
            BorderStroke(2.dp, color)
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goalName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = domain.displayName(),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            
            AnimatedVisibility(
                visible = isSelected,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Selected",
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
