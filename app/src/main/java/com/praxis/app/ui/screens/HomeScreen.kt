package com.praxis.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.praxis.app.data.model.Match
import com.praxis.app.data.model.User

/**
 * Main Home Screen with bottom navigation
 * Shows Matches and Profile tabs
 */
@Composable
fun HomeScreen(
    currentUser: User,
    matches: List<Match>,
    onFindMatches: () -> Unit,
    onMatchClick: (Match) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Matches") },
                    label = { Text("Matches") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> MatchesTab(
                    matches = matches,
                    onFindMatches = onFindMatches,
                    onMatchClick = onMatchClick
                )
                1 -> ProfileTab(user = currentUser)
            }
        }
    }
}
