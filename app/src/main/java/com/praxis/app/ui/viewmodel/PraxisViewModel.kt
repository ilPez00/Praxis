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

/**
 * Main ViewModel for Praxis
 * Manages all app state and business logic
 */
class PraxisViewModel : ViewModel() {
    
    private val repository = MockRepository()
    private val matchingEngine = MatchingEngine()
    
    // UI State
    private val _uiState = MutableStateFlow<PraxisUiState>(PraxisUiState.Onboarding)
    val uiState: StateFlow<PraxisUiState> = _uiState.asStateFlow()
    
    // Current user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    // Matches
    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()
    
    // Goal templates for selection
    val goalTemplates = repository.getGoalTemplates()
    
    /**
     * Creates a new user after onboarding
     */
    fun createUser(name: String, age: Int, bio: String) {
        val user = repository.createUser(name, age, bio)
        _currentUser.value = user
        _uiState.value = PraxisUiState.GoalSelection
    }
    
    /**
     * Completes goal selection and moves to main app
     */
    fun completeGoalSelection(goals: List<GoalNode>) {
        repository.updateUserGoals(goals.toMutableList())
        _currentUser.value = repository.getCurrentUser()
        _uiState.value = PraxisUiState.Home
    }
    
    /**
     * Finds matches based on current user's goals
     * Implements the matching algorithm from the whitepaper
     */
    fun findMatches() {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val candidates = repository.getAllUsers()
            
            // Run matching algorithm
            val newMatches = matchingEngine.findMatches(user, candidates, limit = 10)
            
            // Update repository and state
            repository.clearMatches()
            newMatches.forEach { repository.addMatch(it) }
            _matches.value = newMatches
        }
    }
    
    /**
     * Grades a collaboration and updates goal weights
     */
    fun gradeCollaboration(collaborationId: String, grade: FeedbackGrade) {
        repository.completeCollaboration(collaborationId, grade)
        _currentUser.value = repository.getCurrentUser()
    }
    
    /**
     * Navigates to chat screen
     */
    fun openChat(matchId: String) {
        _uiState.value = PraxisUiState.Chat(matchId)
    }
    
    /**
     * Returns to home screen
     */
    fun navigateToHome() {
        _uiState.value = PraxisUiState.Home
    }
    
    /**
     * Gets a specific match by ID
     */
    fun getMatch(matchId: String): Match? {
        return _matches.value.find { it.userId == matchId }
    }
}

/**
 * UI State sealed class
 * Represents different screens in the app
 */
sealed class PraxisUiState {
    object Onboarding : PraxisUiState()
    object GoalSelection : PraxisUiState()
    object Home : PraxisUiState()
    data class Chat(val matchId: String) : PraxisUiState()
}
