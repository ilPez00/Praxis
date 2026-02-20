package com.praxis.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.praxis.app.data.matching.MatchingEngine
import com.praxis.app.data.model.*
import com.praxis.app.data.repository.MockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

/**
 * Main ViewModel for Praxis.
 * Manages all navigation state and business logic.
 * Extended in Session 22 to support Dashboard, Groups, Analytics, Upgrade,
 * and IdentityVerification screens ported from praxis_webapp.
 */
class PraxisViewModel : ViewModel() {

    private val repository = MockRepository()
    private val matchingEngine = MatchingEngine()

    // ─── Navigation state ─────────────────────────────────────────────────────

    private val _uiState = MutableStateFlow<PraxisUiState>(PraxisUiState.Onboarding)
    val uiState: StateFlow<PraxisUiState> = _uiState.asStateFlow()

    // ─── Data state ───────────────────────────────────────────────────────────

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    private val _bets = MutableStateFlow<List<Bet>>(emptyList())
    val bets: StateFlow<List<Bet>> = _bets.asStateFlow()

    // Goal templates are static; no need for StateFlow
    val goalTemplates = repository.getGoalTemplates()

    init {
        _achievements.value = repository.getAchievements()
        _groups.value = repository.getGroups()
    }

    // ─── Onboarding & goal setup ──────────────────────────────────────────────

    fun createUser(name: String, age: Int, bio: String) {
        val user = repository.createUser(name, age, bio)
        _currentUser.value = user
        _uiState.value = PraxisUiState.GoalSelection
    }

    fun completeGoalSelection(goals: List<GoalNode>) {
        repository.updateUserGoals(goals.toMutableList())
        _currentUser.value = repository.getCurrentUser()
        _uiState.value = PraxisUiState.Main()
    }

    // ─── Matching ─────────────────────────────────────────────────────────────

    fun findMatches() {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val candidates = repository.getAllUsers()
            val newMatches = matchingEngine.findMatches(user, candidates, limit = 10)
            repository.clearMatches()
            newMatches.forEach { repository.addMatch(it) }
            _matches.value = newMatches
        }
    }

    fun getMatch(matchId: String): Match? = _matches.value.find { it.userId == matchId }

    // ─── Collaboration & grading ──────────────────────────────────────────────

    fun gradeCollaboration(collaborationId: String, grade: FeedbackGrade) {
        repository.completeCollaboration(collaborationId, grade)
        _currentUser.value = repository.getCurrentUser()
    }

    // ─── Goal progress ────────────────────────────────────────────────────────

    fun updateGoalProgress(goalId: String, newProgress: Int) {
        val user = _currentUser.value ?: return
        fun updateNode(nodes: MutableList<GoalNode>): Boolean {
            for (node in nodes) {
                if (node.id == goalId) {
                    node.progress = newProgress.coerceIn(0, 100)
                    return true
                }
                if (updateNode(node.subGoals)) return true
            }
            return false
        }
        updateNode(user.goalTree)
        // Trigger recomposition by emitting a new reference
        _currentUser.value = user.copy(goalTree = user.goalTree.toMutableList())
    }

    // ─── Achievements ─────────────────────────────────────────────────────────

    fun upvoteAchievement(achievementId: String) {
        repository.upvoteAchievement(achievementId)
        _achievements.value = repository.getAchievements()
    }

    // ─── Groups ───────────────────────────────────────────────────────────────

    fun joinGroup(groupId: String) {
        repository.joinGroup(groupId)
        _groups.value = repository.getGroups()
    }

    fun createGroup(name: String, description: String, domain: Domain) {
        repository.createGroup(name, description, domain)
        _groups.value = repository.getGroups()
    }

    // ─── Betting ──────────────────────────────────────────────────────────────

    fun placeBet(goalNodeId: String, goalName: String, stake: Int, deadline: Date) {
        val user = _currentUser.value ?: return
        if (user.praxisPoints < stake) return
        val bet = Bet(
            userId = user.id,
            goalNodeId = goalNodeId,
            goalName = goalName,
            stake = stake,
            deadline = deadline
        )
        _bets.value = _bets.value + bet
        _currentUser.value = user.copy(praxisPoints = user.praxisPoints - stake)
    }

    fun cancelBet(betId: String) {
        val user = _currentUser.value ?: return
        val bet = _bets.value.find { it.id == betId } ?: return
        _bets.value = _bets.value.filter { it.id != betId }
        _currentUser.value = user.copy(praxisPoints = user.praxisPoints + bet.stake)
    }

    // ─── Premium ──────────────────────────────────────────────────────────────

    /** Called after a successful upgrade flow (stub — no real Stripe on Android yet). */
    fun activatePremium() {
        val user = _currentUser.value ?: return
        _currentUser.value = user.copy(isPremium = true)
        // Return to Dashboard tab
        _uiState.value = PraxisUiState.Main(MainTab.DASHBOARD)
    }

    /** Called after successful identity verification. */
    fun markIdentityVerified() {
        val user = _currentUser.value ?: return
        _currentUser.value = user.copy(isVerified = true)
        _uiState.value = PraxisUiState.Main(MainTab.PROFILE)
    }

    // ─── Navigation helpers ───────────────────────────────────────────────────

    fun navigateTo(tab: MainTab) {
        _uiState.value = PraxisUiState.Main(tab)
    }

    fun navigateToMain() {
        _uiState.value = PraxisUiState.Main()
    }

    fun openChat(matchId: String) {
        _uiState.value = PraxisUiState.Chat(matchId)
    }

    fun openGroupChat(groupId: String, groupName: String, domain: Domain) {
        _uiState.value = PraxisUiState.GroupChat(groupId, groupName, domain)
    }

    fun navigateToAnalytics() {
        _uiState.value = PraxisUiState.Analytics
    }

    fun navigateToUpgrade() {
        _uiState.value = PraxisUiState.Upgrade
    }

    fun navigateToIdentityVerification() {
        _uiState.value = PraxisUiState.IdentityVerification
    }
}

// ─── UI State ─────────────────────────────────────────────────────────────────

/**
 * Sealed class representing every possible navigation destination.
 */
sealed class PraxisUiState {
    object Onboarding : PraxisUiState()
    object GoalSelection : PraxisUiState()

    /** Main scaffold with bottom navigation. [tab] selects the active tab. */
    data class Main(val tab: MainTab = MainTab.DASHBOARD) : PraxisUiState()

    data class Chat(val matchId: String) : PraxisUiState()
    data class GroupChat(val groupId: String, val groupName: String, val domain: Domain) : PraxisUiState()
    object Analytics : PraxisUiState()
    object Upgrade : PraxisUiState()
    object IdentityVerification : PraxisUiState()
}

/**
 * Bottom-navigation tabs shown inside the Main scaffold.
 */
enum class MainTab(val label: String, val emoji: String) {
    DASHBOARD("Dashboard", "🏠"),
    GOALS("Goals", "🎯"),
    MATCHES("Matches", "🤝"),
    GROUPS("Groups", "💬"),
    PROFILE("Profile", "👤")
}
