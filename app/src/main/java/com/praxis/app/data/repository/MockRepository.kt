package com.praxis.app.data.repository

import com.praxis.app.data.model.*
import java.util.UUID

/**
 * Mock data repository for MVP
 * In production, this will connect to a real backend API
 * For now, it stores everything in memory
 */
class MockRepository {
    
    // Current logged-in user
    private var currentUser: User? = null
    
    // Mock database of other users
    private val allUsers = mutableListOf<User>()
    
    // User's matches
    private val matches = mutableListOf<Match>()
    
    // Active collaborations
    private val collaborations = mutableListOf<Collaboration>()
    
    init {
        // Create some mock users for testing
        createMockUsers()
    }
    
    /**
     * Creates mock users with various goals for testing
     */
    private fun createMockUsers() {
        // Mock User 1: Fitness + Career focus
        allUsers.add(User(
            name = "Alex",
            age = 28,
            bio = "Software engineer looking to get stronger and advance my career",
            isVerified = true,
            goalTree = mutableListOf(
                GoalNode(
                    id = UUID.randomUUID().toString(),
                    domain = Domain.FITNESS,
                    name = "Strength Training",
                    weight = 1.2,
                    progress = 60,
                    subGoals = mutableListOf(
                        GoalNode(UUID.randomUUID().toString(), Domain.FITNESS, "Squat 100kg", progress = 70),
                        GoalNode(UUID.randomUUID().toString(), Domain.FITNESS, "Bench 80kg", progress = 50)
                    )
                ),
                GoalNode(
                    id = UUID.randomUUID().toString(),
                    domain = Domain.CAREER,
                    name = "Senior Promotion",
                    weight = 1.0,
                    progress = 40
                )
            )
        ))
        
        // Mock User 2: Fitness + Mental Health
        allUsers.add(User(
            name = "Sam",
            age = 25,
            bio = "Yoga enthusiast and meditation practitioner",
            isVerified = true,
            goalTree = mutableListOf(
                GoalNode(
                    id = UUID.randomUUID().toString(),
                    domain = Domain.FITNESS,
                    name = "Cardio Training",
                    weight = 1.3,
                    progress = 55
                ),
                GoalNode(
                    id = UUID.randomUUID().toString(),
                    domain = Domain.MENTAL_HEALTH,
                    name = "Daily Meditation",
                    weight = 1.1,
                    progress = 80
                )
            )
        ))
        
        // Mock User 3: Career + Philosophy
        allUsers.add(User(
            name = "Jordan",
            age = 30,
            bio = "Startup founder exploring Stoicism",
            isVerified = false,
            goalTree = mutableListOf(
                GoalNode(
                    id = UUID.randomUUID().toString(),
                    domain = Domain.CAREER,
                    name = "Launch Startup",
                    weight = 1.5,
                    progress = 30
                ),
                GoalNode(
                    id = UUID.randomUUID().toString(),
                    domain = Domain.PHILOSOPHY,
                    name = "Study Stoicism",
                    weight = 0.9,
                    progress = 50
                )
            )
        ))
    }
    
    // User Management
    fun getCurrentUser(): User? = currentUser
    
    fun setCurrentUser(user: User) {
        currentUser = user
    }
    
    fun createUser(name: String, age: Int, bio: String): User {
        val user = User(name = name, age = age, bio = bio)
        currentUser = user
        return user
    }
    
    fun updateUserGoals(goals: MutableList<GoalNode>) {
        currentUser?.goalTree?.clear()
        currentUser?.goalTree?.addAll(goals)
    }
    
    // Matching
    fun getAllUsers(): List<User> = allUsers
    
    fun getMatches(): List<Match> = matches
    
    fun addMatch(match: Match) {
        matches.add(match)
    }
    
    fun clearMatches() {
        matches.clear()
    }
    
    // Collaborations
    fun getCollaborations(): List<Collaboration> = collaborations
    
    fun createCollaboration(matchId: String, goalId: String): Collaboration {
        val collab = Collaboration(matchId = matchId, goalId = goalId)
        collaborations.add(collab)
        return collab
    }
    
    fun completeCollaboration(collabId: String, myGrade: FeedbackGrade) {
        collaborations.find { it.id == collabId }?.let {
            it.completedAt = System.currentTimeMillis()
            it.myGrade = myGrade
            
            // Update goal weights based on feedback
            currentUser?.getAllGoals()?.find { goal -> goal.id == it.goalId }?.let { goal ->
                goal.updateWeightFromGrade(myGrade)
            }
        }
    }
    
    // Predefined goal templates for onboarding
    fun getGoalTemplates(): Map<Domain, List<String>> = mapOf(
        Domain.FITNESS to listOf("Strength Training", "Cardio", "Yoga", "Weight Loss", "Muscle Gain"),
        Domain.CAREER to listOf("Promotion", "Skill Learning", "Networking", "Start Business", "Career Change"),
        Domain.MENTAL_HEALTH to listOf("Meditation", "Therapy", "Stress Management", "Better Sleep"),
        Domain.ACADEMICS to listOf("Learn New Language", "Get Degree", "Online Course", "Read More"),
        Domain.PHILOSOPHY to listOf("Study Stoicism", "Existentialism", "Ethics", "Mindfulness"),
        Domain.INVESTING to listOf("Save Money", "Invest in Stocks", "Financial Independence", "Budget Better"),
        Domain.CULTURE_HOBBIES to listOf("Learn Instrument", "Photography", "Painting", "Writing"),
        Domain.INTIMACY_ROMANCE to listOf("Find Partner", "Improve Communication", "Date More"),
        Domain.FRIENDSHIP_SOCIAL to listOf("Make New Friends", "Reconnect", "Join Community", "Be More Social")
    )
}
