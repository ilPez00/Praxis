package com.praxis.app.data.model

import java.util.UUID

/**
 * Represents a Praxis user with their complete goal tree
 */
data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val age: Int,
    val bio: String = "",
    val isVerified: Boolean = false,
    val isPremium: Boolean = false,
    val currentStreak: Int = 0,    // Days of consecutive activity
    val praxisPoints: Int = 100,   // Points for accountability betting
    val goalTree: MutableList<GoalNode> = mutableListOf()
) {
    /**
     * Gets all primary goals (top-level nodes in the tree)
     * Free tier: max 3 goals
     * Premium: unlimited
     */
    fun getPrimaryGoals(): List<GoalNode> = goalTree

    /**
     * Adds a new primary goal to the tree.
     * Free tier is capped at 3; Premium has no limit.
     */
    fun addPrimaryGoal(goal: GoalNode) {
        if (isPremium || goalTree.size < 3) {
            goalTree.add(goal)
        }
    }
    
    /**
     * Gets all goals across all domains (flattened tree)
     */
    fun getAllGoals(): List<GoalNode> {
        val allGoals = mutableListOf<GoalNode>()
        fun traverse(node: GoalNode) {
            allGoals.add(node)
            node.subGoals.forEach { traverse(it) }
        }
        goalTree.forEach { traverse(it) }
        return allGoals
    }
    
    /**
     * Finds goals by domain
     */
    fun getGoalsByDomain(domain: Domain): List<GoalNode> {
        return getAllGoals().filter { it.domain == domain }
    }
}

/**
 * Match between two users based on the compatibility algorithm
 */
data class Match(
    val userId: String,
    val userName: String,
    val compatibilityScore: Double, // The SAB score from whitepaper
    val sharedGoals: List<GoalNode>,
    val matchedAt: Long = System.currentTimeMillis()
)

/**
 * A collaboration session between matched users
 */
data class Collaboration(
    val id: String = UUID.randomUUID().toString(),
    val matchId: String,
    val goalId: String,
    val startedAt: Long = System.currentTimeMillis(),
    var completedAt: Long? = null,
    var myGrade: FeedbackGrade? = null,
    var theirGrade: FeedbackGrade? = null
) {
    fun isCompleted(): Boolean = completedAt != null
    fun isMutuallyGraded(): Boolean = myGrade != null && theirGrade != null
}
