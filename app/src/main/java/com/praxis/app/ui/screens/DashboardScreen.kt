package com.praxis.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.praxis.app.data.model.*
import com.praxis.app.ui.theme.getColor
import com.praxis.app.ui.viewmodel.MainTab

/**
 * Dashboard screen — main hub after login.
 * Ported from praxis_webapp DashboardPage.tsx.
 *
 * Sections:
 *   1. Welcome banner (name, streak, stats, premium badge)
 *   2. Quick-action chips (navigate to tabs)
 *   3. AI Coaching card (premium-gated)
 *   4. Core Objectives (root goals with progress)
 *   5. Top Alignments (top 3 matches)
 *   6. Community Achievements feed (upvote + comment dialog)
 */
@Composable
fun DashboardScreen(
    user: User?,
    matches: List<Match>,
    achievements: List<Achievement>,
    onNavigate: (MainTab) -> Unit,
    onUpvoteAchievement: (String) -> Unit,
    onNavigateToUpgrade: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToIdentityVerification: () -> Unit
) {
    var selectedAchievement by remember { mutableStateOf<Achievement?>(null) }
    var aiPrompt by remember { mutableStateOf("") }
    var aiResponse by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Screen header with info button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                InfoButton(
                    title = "Your Dashboard",
                    description = "Your mission control. See your active streak 🔥, Praxis Points, and shortcuts to every section. The AI Coach (Premium) gives personalised advice. Below you'll find your top goals, best matches, and the global achievement feed."
                )
            }
        }

        // 2. Welcome banner
        item {
            WelcomeBanner(
                user = user,
                onNavigateToUpgrade = onNavigateToUpgrade,
                onNavigateToIdentityVerification = onNavigateToIdentityVerification
            )
        }

        // 2. Quick actions
        item {
            QuickActionsRow(onNavigate = onNavigate)
        }

        // 3. AI Coaching
        item {
            AiCoachingCard(
                isPremium = user?.isPremium ?: false,
                prompt = aiPrompt,
                onPromptChange = { aiPrompt = it },
                response = aiResponse,
                onRequest = {
                    val goalHint = user?.goalTree?.firstOrNull()?.name ?: "your goals"
                    aiResponse = "Based on your progress in $goalHint, " +
                        "focus on consistent daily action rather than bursts. " +
                        "Your momentum is your biggest asset — protect it."
                    aiPrompt = ""
                },
                onNavigateToUpgrade = onNavigateToUpgrade
            )
        }

        // 4. Core objectives
        if (user != null && user.goalTree.isNotEmpty()) {
            item {
                Text(
                    text = "Core Objectives",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            items(user.goalTree.take(3)) { goal ->
                CoreObjectiveCard(goal = goal)
            }
        }

        // 5. Top alignments
        if (matches.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Top Alignments",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { onNavigate(MainTab.MATCHES) }) {
                        Text("See all")
                    }
                }
            }
            items(matches.take(3)) { match ->
                TopAlignmentCard(
                    match = match,
                    onMessage = { onNavigate(MainTab.MATCHES) }
                )
            }
        }

        // 6. Community achievements
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Community Achievements",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToAnalytics) {
                    Text("Analytics")
                }
            }
        }
        items(achievements.take(5)) { achievement ->
            AchievementCard(
                achievement = achievement,
                onUpvote = { onUpvoteAchievement(achievement.id) },
                onClick = { selectedAchievement = achievement }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }

    selectedAchievement?.let { achievement ->
        AchievementDetailDialog(
            achievement = achievement,
            onDismiss = { selectedAchievement = null },
            onUpvote = {
                onUpvoteAchievement(achievement.id)
                selectedAchievement = null
            }
        )
    }
}

// ─── Welcome banner ───────────────────────────────────────────────────────────

@Composable
private fun WelcomeBanner(
    user: User?,
    onNavigateToUpgrade: () -> Unit,
    onNavigateToIdentityVerification: () -> Unit
) {
    val avgProgress = remember(user?.goalTree) {
        if (user == null || user.goalTree.isEmpty()) 0
        else user.goalTree.map { it.progress }.average().toInt()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (user != null) "Welcome back, ${user.name}!" else "Welcome to Praxis!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if ((user?.currentStreak ?: 0) > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text("🔥", fontSize = 16.sp)
                            Text(
                                text = " ${user!!.currentStreak} day streak",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (user?.isPremium == true) {
                        Surface(
                            color = Color(0xFFFFB300),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "⭐ Premium",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        AssistChip(
                            onClick = onNavigateToUpgrade,
                            label = { Text("✨ Go Premium", fontSize = 11.sp) }
                        )
                    }
                    if (user?.isVerified == false) {
                        AssistChip(
                            onClick = onNavigateToIdentityVerification,
                            label = { Text("🪪 Verify ID", fontSize = 11.sp) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatBadge(value = "${user?.goalTree?.size ?: 0}", label = "Goals")
                StatBadge(value = "$avgProgress%", label = "Progress")
                StatBadge(value = "${user?.praxisPoints ?: 0}", label = "Points")
            }
        }
    }
}

@Composable
private fun StatBadge(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

// ─── Quick actions ────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsRow(onNavigate: (MainTab) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            "🎯 Goals" to MainTab.GOALS,
            "🤝 Matches" to MainTab.MATCHES,
            "💬 Groups" to MainTab.GROUPS,
            "👤 Profile" to MainTab.PROFILE
        ).forEach { (label, tab) ->
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate(tab) },
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ─── AI Coaching ──────────────────────────────────────────────────────────────

@Composable
private fun AiCoachingCard(
    isPremium: Boolean,
    prompt: String,
    onPromptChange: (String) -> Unit,
    response: String?,
    onRequest: () -> Unit,
    onNavigateToUpgrade: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text("🤖", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Performance Coach",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (!isPremium) {
                    Surface(
                        color = Color(0xFFFFB300),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Premium",
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (!isPremium) {
                Text(
                    text = "Upgrade to Premium for personalised AI coaching based on your goal tree.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onNavigateToUpgrade, modifier = Modifier.fillMaxWidth()) {
                    Text("Upgrade to Premium")
                }
            } else {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = onPromptChange,
                    placeholder = { Text("Ask your AI coach anything…") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRequest,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = prompt.isNotBlank()
                ) {
                    Text("Ask Coach")
                }
                if (response != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = response,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ─── Core objective card ──────────────────────────────────────────────────────

@Composable
private fun CoreObjectiveCard(goal: GoalNode) {
    val color = goal.domain.getColor()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = color
                    )
                    Text(
                        text = goal.domain.displayName(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${goal.progress}%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { goal.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
        }
    }
}

// ─── Top alignment card ───────────────────────────────────────────────────────

@Composable
private fun TopAlignmentCard(match: Match, onMessage: () -> Unit) {
    val scorePercent = (match.compatibilityScore * 100).toInt()
    val scoreColor = when {
        scorePercent >= 80 -> Color(0xFF34C759)
        scorePercent >= 60 -> Color(0xFFFF9500)
        else -> MaterialTheme.colorScheme.outline
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(scoreColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = match.userName.firstOrNull()?.toString() ?: "?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = match.userName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = match.sharedGoals.take(2).joinToString(" · ") { it.name },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$scorePercent%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor
                )
                OutlinedButton(
                    onClick = onMessage,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("Message", fontSize = 12.sp)
                }
            }
        }
    }
}

// ─── Achievement card (also used from other screens) ─────────────────────────

@Composable
fun AchievementCard(
    achievement: Achievement,
    onUpvote: () -> Unit,
    onClick: () -> Unit
) {
    val domainColor = achievement.domain.getColor()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(domainColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = achievement.userName.firstOrNull()?.toString() ?: "?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = domainColor
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = achievement.userName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Surface(
                        color = domainColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = achievement.domain.displayName(),
                            fontSize = 10.sp,
                            color = domainColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text("🏆", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = achievement.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            if (achievement.description.isNotBlank()) {
                Text(
                    text = achievement.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TextButton(onClick = onUpvote, contentPadding = PaddingValues(0.dp)) {
                        Text("👍 ${achievement.totalUpvotes}", fontSize = 13.sp)
                    }
                    Text(
                        text = "💬 ${achievement.comments.size}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Text(
                    text = "Tap to expand",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ─── Achievement detail dialog ────────────────────────────────────────────────

@Composable
private fun AchievementDetailDialog(
    achievement: Achievement,
    onDismiss: () -> Unit,
    onUpvote: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "🏆 ${achievement.title}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "by ${achievement.userName} · ${achievement.domain.displayName()}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
                if (achievement.description.isNotBlank()) {
                    Text(
                        text = achievement.description,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onUpvote) { Text("👍 ${achievement.totalUpvotes}") }
                    OutlinedButton(onClick = onDismiss) { Text("Close") }
                }

                if (achievement.comments.isNotEmpty()) {
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        text = "Comments (${achievement.comments.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    achievement.comments.forEach { comment ->
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(
                                text = comment.userName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = comment.content,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
