package com.praxis.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.praxis.app.data.model.*
import com.praxis.app.ui.screens.*
import com.praxis.app.ui.theme.PraxisTheme
import com.praxis.app.ui.viewmodel.MainTab
import com.praxis.app.ui.viewmodel.PraxisUiState
import com.praxis.app.ui.viewmodel.PraxisViewModel

/**
 * MainActivity — single-activity entry point.
 * All navigation is handled by PraxisUiState sealed class in PraxisViewModel.
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

@Composable
fun PraxisApp() {
    val viewModel: PraxisViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val groups by viewModel.groups.collectAsState()

    // Grading dialog state (shown on top of Chat screen)
    var showGradingDialog by remember { mutableStateOf(false) }
    var currentMatchForGrading by remember { mutableStateOf<String?>(null) }

    when (val state = uiState) {

        // ── Onboarding ─────────────────────────────────────────────────────────
        is PraxisUiState.Onboarding -> {
            OnboardingScreen(
                onComplete = { name, age, bio ->
                    viewModel.createUser(name, age, bio)
                }
            )
        }

        // ── Goal selection ──────────────────────────────────────────────────────
        is PraxisUiState.GoalSelection -> {
            GoalSelectionScreen(
                goalTemplates = viewModel.goalTemplates,
                onComplete = { goals ->
                    viewModel.completeGoalSelection(goals)
                }
            )
        }

        // ── Main scaffold (bottom nav) ──────────────────────────────────────────
        is PraxisUiState.Main -> {
            MainScaffold(
                activeTab = state.tab,
                onTabSelected = { viewModel.navigateTo(it) },
                currentUser = currentUser,
                matches = matches,
                achievements = achievements,
                groups = groups,
                viewModel = viewModel
            )
        }

        // ── Direct-message chat ─────────────────────────────────────────────────
        is PraxisUiState.Chat -> {
            val match = viewModel.getMatch(state.matchId)
            match?.let {
                ChatScreen(
                    matchName = it.userName,
                    sharedGoalName = it.sharedGoals.firstOrNull()?.name ?: "shared goal",
                    onBack = { viewModel.navigateToMain() },
                    onCompleteCollaboration = {
                        currentMatchForGrading = state.matchId
                        showGradingDialog = true
                    }
                )
            }

            if (showGradingDialog && currentMatchForGrading != null) {
                val m = viewModel.getMatch(currentMatchForGrading!!)
                m?.let {
                    GradingDialog(
                        matchName = it.userName,
                        goalName = it.sharedGoals.firstOrNull()?.name ?: "goal",
                        onGradeSelected = { _ ->
                            showGradingDialog = false
                            currentMatchForGrading = null
                            viewModel.navigateToMain()
                        },
                        onDismiss = {
                            showGradingDialog = false
                            currentMatchForGrading = null
                        }
                    )
                }
            }
        }

        // ── Group chat ──────────────────────────────────────────────────────────
        is PraxisUiState.GroupChat -> {
            GroupChatRoomScreen(
                groupId = state.groupId,
                groupName = state.groupName,
                domain = state.domain,
                currentUserName = currentUser?.name ?: "You",
                onBack = { viewModel.navigateToMain() }
            )
        }

        // ── Analytics (premium-gated) ───────────────────────────────────────────
        is PraxisUiState.Analytics -> {
            AnalyticsScreen(
                user = currentUser,
                isPremium = currentUser?.isPremium ?: false,
                onBack = { viewModel.navigateToMain() },
                onNavigateToUpgrade = { viewModel.navigateToUpgrade() }
            )
        }

        // ── Upgrade / premium ───────────────────────────────────────────────────
        is PraxisUiState.Upgrade -> {
            UpgradeScreen(
                onBack = { viewModel.navigateToMain() },
                onUpgradeSuccess = { viewModel.activatePremium() }
            )
        }

        // ── Identity verification ───────────────────────────────────────────────
        is PraxisUiState.IdentityVerification -> {
            IdentityVerificationScreen(
                onBack = { viewModel.navigateToMain() },
                onVerified = { viewModel.markIdentityVerified() }
            )
        }
    }
}

// ─── Main scaffold with bottom navigation ────────────────────────────────────

@Composable
fun MainScaffold(
    activeTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    currentUser: User?,
    matches: List<Match>,
    achievements: List<Achievement>,
    groups: List<Group>,
    viewModel: PraxisViewModel
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                MainTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = activeTab == tab,
                        onClick = { onTabSelected(tab) },
                        icon = { Text(tab.emoji, style = MaterialTheme.typography.titleMedium) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (activeTab) {

                MainTab.DASHBOARD -> DashboardScreen(
                    user = currentUser,
                    matches = matches,
                    achievements = achievements,
                    onNavigate = { onTabSelected(it) },
                    onUpvoteAchievement = { viewModel.upvoteAchievement(it) },
                    onNavigateToUpgrade = { viewModel.navigateToUpgrade() },
                    onNavigateToAnalytics = { viewModel.navigateToAnalytics() },
                    onNavigateToIdentityVerification = { viewModel.navigateToIdentityVerification() }
                )

                MainTab.GOALS -> currentUser?.let { user ->
                    HomeScreenWithTree(
                        goals = user.goalTree,
                        onGoalClick = { /* future: open node detail */ },
                        onAddProgress = { goal ->
                            // Increment progress by 10 on tap (simple affordance)
                            val newPct = (goal.progress + 10).coerceAtMost(100)
                            viewModel.updateGoalProgress(goal.id, newPct)
                        }
                    )
                }

                MainTab.MATCHES -> {
                    // Trigger initial match fetch if list is empty
                    LaunchedEffect(Unit) {
                        if (matches.isEmpty()) viewModel.findMatches()
                    }
                    MatchesTab(
                        matches = matches,
                        onFindMatches = { viewModel.findMatches() },
                        onMatchClick = { match -> viewModel.openChat(match.userId) }
                    )
                }

                MainTab.GROUPS -> GroupsScreen(
                    groups = groups,
                    onJoinGroup = { viewModel.joinGroup(it) },
                    onEnterGroup = { id, name, domain -> viewModel.openGroupChat(id, name, domain) },
                    onCreateGroup = { name, desc, domain -> viewModel.createGroup(name, desc, domain) }
                )

                MainTab.PROFILE -> currentUser?.let { user ->
                    ProfileTab(user = user)
                }
            }
        }
    }
}
