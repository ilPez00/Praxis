package com.praxis.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.praxis.app.data.model.Domain
import com.praxis.app.data.model.User
import com.praxis.app.ui.theme.getColor

/**
 * Advanced analytics screen — premium-gated.
 * Ported from praxis_webapp AnalyticsPage.tsx.
 *
 * Sections (premium only):
 *   - Achievement rate summary card
 *   - Domain performance bars
 *   - Collaboration grade distribution
 *   - Community comparison
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    user: User?,
    isPremium: Boolean,
    onBack: () -> Unit,
    onNavigateToUpgrade: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (!isPremium) {
            PremiumGate(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                onNavigateToUpgrade = onNavigateToUpgrade
            )
        } else {
            AnalyticsContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                user = user
            )
        }
    }
}

@Composable
private fun PremiumGate(modifier: Modifier, onNavigateToUpgrade: () -> Unit) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Advanced Analytics", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Unlock deep insights into your goal progress, domain performance, " +
                    "and community benchmarks. Available on Premium.",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )
            Button(
                onClick = onNavigateToUpgrade,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Upgrade to Premium") }
        }
    }
}

@Composable
private fun AnalyticsContent(modifier: Modifier, user: User?) {
    val goals = user?.goalTree ?: emptyList()
    val completed = goals.count { it.progress == 100 }
    val achievementRate = if (goals.isEmpty()) 0 else (completed * 100 / goals.size)
    val avgProgress = if (goals.isEmpty()) 0 else goals.map { it.progress }.average().toInt()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Your Performance", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }

        // Summary card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AnalyticsStat("$achievementRate%", "Achievement\nRate")
                    AnalyticsStat("${goals.size}", "Total\nGoals")
                    AnalyticsStat("$completed", "Completed")
                }
            }
        }

        // Domain performance
        item {
            Text(text = "Domain Performance", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }

        val domainPerf = goals
            .groupBy { it.domain }
            .mapValues { (_, g) -> g.map { it.progress }.average().toInt() }

        if (domainPerf.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Add goals to see domain performance",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        domainPerf.forEach { (domain, pct) ->
                            DomainPerformanceRow(domain = domain, progress = pct)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        // Feedback trends
        item {
            Text(text = "Collaboration Grades", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
        item { FeedbackTrendsCard() }

        // Community comparison
        item { CommunityComparisonCard(userAvg = avgProgress) }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun AnalyticsStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 28.sp,
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

@Composable
private fun DomainPerformanceRow(domain: Domain, progress: Int) {
    val color = domain.getColor()
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${domain.emoji()} ${domain.displayName()}",
            fontSize = 12.sp,
            modifier = Modifier.width(140.dp),
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$progress%", fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.width(36.dp))
    }
}

@Composable
private fun FeedbackTrendsCard() {
    // Mock grade distribution (would be real data from backend in production)
    val grades = listOf(
        "Succeeded" to 45,
        "Tried" to 20,
        "Mediocre" to 15,
        "Distracted" to 12,
        "Total Noob" to 8
    )
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Based on your collaboration history",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            grades.forEach { (label, count) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = label, fontSize = 13.sp, modifier = Modifier.width(100.dp))
                    LinearProgressIndicator(
                        progress = { count / 100f },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "$count%", fontSize = 12.sp, modifier = Modifier.width(36.dp))
                }
            }
        }
    }
}

@Composable
private fun CommunityComparisonCard(userAvg: Int) {
    val globalAvg = 42  // Mock global average — will be a real endpoint in production
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Community Comparison",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$userAvg%",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("You", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$globalAvg%",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text("Global avg", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (userAvg >= globalAvg)
                    "You're above the community average! 🎉"
                else
                    "Keep pushing — the community average is $globalAvg%",
                fontSize = 13.sp,
                color = if (userAvg >= globalAvg) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
