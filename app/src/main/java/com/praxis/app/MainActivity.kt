package com.praxis.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.praxis.app.ui.screens.*
import com.praxis.app.ui.theme.PraxisTheme
import com.praxis.app.ui.viewmodel.PraxisUiState
import com.praxis.app.ui.viewmodel.PraxisViewModel

/**
 * MainActivity - Entry point of Praxis app
 * Sets up the Compose UI and navigation
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PraxisTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PraxisApp()
                }
            }
        }
    }
}

/**
 * Main composable that handles navigation between screens
 */
@Composable
fun PraxisApp() {
    val viewModel: PraxisViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val matches by viewModel.matches.collectAsState()
    
    // Show grading dialog state
    var showGradingDialog by remember { mutableStateOf(false) }
    var currentMatchForGrading by remember { mutableStateOf<String?>(null) }
    
    when (uiState) {
        is PraxisUiState.Onboarding -> {
            OnboardingScreen(
                onComplete = { name, age, bio ->
                    viewModel.createUser(name, age, bio)
                }
            )
        }
        
        is PraxisUiState.GoalSelection -> {
            GoalSelectionScreen(
                goalTemplates = viewModel.goalTemplates,
                onComplete = { goals ->
                    viewModel.completeGoalSelection(goals)
                }
            )
        }
        
        is PraxisUiState.Home -> {
            currentUser?.let { user ->
                HomeScreen(
                    currentUser = user,
                    matches = matches,
                    onFindMatches = {
                        viewModel.findMatches()
                    },
                    onMatchClick = { match ->
                        viewModel.openChat(match.userId)
                    }
                )
            }
        }
        
        is PraxisUiState.Chat -> {
            val chatState = uiState as PraxisUiState.Chat
            val match = viewModel.getMatch(chatState.matchId)
            
            match?.let {
                ChatScreen(
                    matchName = it.userName,
                    sharedGoalName = it.sharedGoals.firstOrNull()?.name ?: "shared goal",
                    onBack = {
                        viewModel.navigateToHome()
                    },
                    onCompleteCollaboration = {
                        currentMatchForGrading = chatState.matchId
                        showGradingDialog = true
                    }
                )
            }
        }
    }
    
    // Grading dialog (shown on top of other screens)
    if (showGradingDialog && currentMatchForGrading != null) {
        val match = viewModel.getMatch(currentMatchForGrading!!)
        match?.let {
            GradingDialog(
                matchName = it.userName,
                goalName = it.sharedGoals.firstOrNull()?.name ?: "goal",
                onGradeSelected = { grade ->
                    // In a real app, would save collaboration ID
                    showGradingDialog = false
                    currentMatchForGrading = null
                    viewModel.navigateToHome()
                },
                onDismiss = {
                    showGradingDialog = false
                    currentMatchForGrading = null
                }
            )
        }
    }
}
