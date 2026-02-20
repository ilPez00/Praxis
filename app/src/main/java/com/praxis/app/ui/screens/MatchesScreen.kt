package com.praxis.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.praxis.app.data.model.Match

/**
 * Matches Tab
 * Shows list of compatible users based on goal alignment
 */
@Composable
fun MatchesTab(
    matches: List<Match>,
    onFindMatches: () -> Unit,
    onMatchClick: (Match) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Matches",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Row {
                InfoButton(
                    title = "Accountability Matches",
                    description = "Partners ranked by SAB alignment — a composite score of how well your goals, domain priorities, and collaboration style overlap. Tap a match to open a private chat."
                )
                IconButton(onClick = onFindMatches) {
                    Icon(Icons.Default.Search, "Find new matches")
                }
            }
        }
        
        Text(
            text = "${matches.size} people aligned with your goals",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (matches.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No matches yet",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tap the search icon to find people who share your goals",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onFindMatches) {
                    Text("Find Matches")
                }
            }
        } else {
            // List of matches
            LazyColumn {
                items(matches) { match ->
                    MatchCard(
                        match = match,
                        onClick = { onMatchClick(match) }
                    )
                }
            }
        }
    }
}

@Composable
fun MatchCard(match: Match, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Name and compatibility score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.userName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                // Compatibility badge
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "${(match.compatibilityScore * 100).toInt()}% Match",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Shared goals
            Text(
                text = "Shared Goals:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            match.sharedGoals.take(3).forEach { goal ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text("• ", fontSize = 14.sp)
                    Text(
                        text = "${goal.domain.displayName()}: ${goal.name}",
                        fontSize = 14.sp
                    )
                }
            }
            
            if (match.sharedGoals.size > 3) {
                Text(
                    text = "+ ${match.sharedGoals.size - 3} more",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
