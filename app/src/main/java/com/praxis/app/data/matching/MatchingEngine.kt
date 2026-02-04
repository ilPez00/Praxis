package com.praxis.app.data.matching

import com.praxis.app.data.model.GoalNode
import com.praxis.app.data.model.Match
import com.praxis.app.data.model.User
import kotlin.math.abs

/**
 * Implements the matching algorithm from the Praxis whitepaper (Section 3.3)
 * 
 * SAB = Σ Σ δ(gi, gj) · sim(i, j) · Wi · Wj / (Σ Wi · Σ Wj)
 *       i j
 * 
 * Where:
 * - δ = 1 if domains match, 0 otherwise
 * - sim(i, j) = similarity of progress (0-1)
 * - Wi, Wj = goal weights
 */
class MatchingEngine {
    
    /**
     * Computes compatibility score between two users
     * Returns a Match object with the score and shared goals
     */
    fun computeMatch(userA: User, userB: User): Match? {
        val treeA = userA.getPrimaryGoals()
        val treeB = userB.getPrimaryGoals()
        
        if (treeA.isEmpty() || treeB.isEmpty()) return null
        
        var totalScore = 0.0
        val sharedGoals = mutableListOf<GoalNode>()
        
        // Iterate through all goals in both trees
        for (nodeA in userA.getAllGoals()) {
            for (nodeB in userB.getAllGoals()) {
                // δ(gi, gj) - domain match
                if (nodeA.domain == nodeB.domain) {
                    // sim(i, j) - progress similarity
                    val similarity = computeSimilarity(nodeA, nodeB)
                    
                    // Add to score: δ · sim · Wi · Wj
                    totalScore += similarity * nodeA.weight * nodeB.weight
                    
                    // Track shared goals
                    if (similarity > 0.3) { // Threshold for "shared"
                        sharedGoals.add(nodeA)
                    }
                }
            }
        }
        
        // Normalize by total weight products
        val sumWeightsA = userA.getAllGoals().sumOf { it.weight }
        val sumWeightsB = userB.getAllGoals().sumOf { it.weight }
        
        if (sumWeightsA == 0.0 || sumWeightsB == 0.0) return null
        
        val normalizedScore = totalScore / (sumWeightsA * sumWeightsB)
        
        // Only return matches above threshold
        return if (normalizedScore > 0.2 && sharedGoals.isNotEmpty()) {
            Match(
                userId = userB.id,
                userName = userB.name,
                compatibilityScore = normalizedScore,
                sharedGoals = sharedGoals.distinctBy { it.id }
            )
        } else null
    }
    
    /**
     * Computes similarity between two goal nodes
     * Based on progress alignment (how close they are in their journey)
     * Returns value between 0 and 1
     */
    private fun computeSimilarity(nodeA: GoalNode, nodeB: GoalNode): Double {
        // Progress similarity: closer progress = higher similarity
        val progressDiff = abs(nodeA.progress - nodeB.progress)
        val progressSimilarity = 1.0 - (progressDiff / 100.0)
        
        // Weight similarity: similar priorities = higher similarity
        val weightDiff = abs(nodeA.weight - nodeB.weight)
        val weightSimilarity = 1.0 - (weightDiff.coerceAtMost(1.0))
        
        // Combined similarity (weighted average)
        return (progressSimilarity * 0.7) + (weightSimilarity * 0.3)
    }
    
    /**
     * Finds best matches for a user from a pool of candidates
     * Returns top N matches sorted by compatibility score
     */
    fun findMatches(user: User, candidates: List<User>, limit: Int = 10): List<Match> {
        return candidates
            .mapNotNull { candidate -> computeMatch(user, candidate) }
            .sortedByDescending { it.compatibilityScore }
            .take(limit)
    }
}
