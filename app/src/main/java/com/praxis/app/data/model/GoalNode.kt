package com.praxis.app.data.model

/**
 * Represents a single node in the user's goal tree.
 * Each node has:
 * - id: Unique identifier
 * - domain: Which life area (e.g., "Fitness", "Career")
 * - name: The specific goal (e.g., "Strength Training", "Learn Kotlin")
 * - weight: User's current priority (higher = more important)
 * - progress: 0-100% completion
 * - subGoals: Children nodes in the tree
 * - emoji: Visual representation for the goal category
 * - level: Depth in the hierarchy (0=macrogroup, 1=intermediate, 2=specific, 3=detail)
 */
data class GoalNode(
    val id: String,
    val domain: Domain,
    val name: String,
    val details: String = "",
    val emoji: String = "",
    val level: Int = 0,
    var weight: Double = 1.0,
    var progress: Int = 0, // 0-100
    val subGoals: MutableList<GoalNode> = mutableListOf()
) {
    /**
     * Calculates the average progress of this goal including all sub-goals
     */
    fun calculateTotalProgress(): Int {
        if (subGoals.isEmpty()) return progress
        
        val subProgress = subGoals.map { it.calculateTotalProgress() }.average()
        return ((progress + subProgress) / 2).toInt()
    }
    
    /**
     * Updates weight based on feedback grade
     * Implements the autopoietic recalibration from the whitepaper
     */
    fun updateWeightFromGrade(grade: FeedbackGrade) {
        weight = when (grade) {
            FeedbackGrade.DISTRACTED -> weight * 1.2 // Harder than expected
            FeedbackGrade.TOTAL_NOOB -> weight * 1.5 // Much harder
            FeedbackGrade.MEDIOCRE -> weight * 1.1  // Slightly harder
            FeedbackGrade.TRIED_BUT_FAILED -> weight * 0.95 // About right
            FeedbackGrade.SUCCEEDED -> weight * 0.8 // Easier than expected
        }
        // Keep weight in reasonable bounds
        weight = weight.coerceIn(0.5, 2.0)
    }
}

/**
 * The main life domains from the whitepaper
 */
enum class Domain {
    CAREER,
    INVESTING,
    FITNESS,
    ACADEMICS,
    MENTAL_HEALTH,
    PHILOSOPHY,
    CULTURE_HOBBIES,
    INTIMACY_ROMANCE,
    FRIENDSHIP_SOCIAL;
    
    fun displayName(): String = when (this) {
        CAREER -> "Career"
        INVESTING -> "Investing / Financial Growth"
        FITNESS -> "Fitness"
        ACADEMICS -> "Academics"
        MENTAL_HEALTH -> "Mental Health"
        PHILOSOPHY -> "Philosophical Development"
        CULTURE_HOBBIES -> "Culture / Hobbies / Creative Pursuits"
        INTIMACY_ROMANCE -> "Intimacy / Romantic Exploration"
        FRIENDSHIP_SOCIAL -> "Friendship / Social Engagement"
    }
    
    fun emoji(): String = when (this) {
        CAREER -> "💼"
        INVESTING -> "💰"
        FITNESS -> "💪"
        ACADEMICS -> "📚"
        MENTAL_HEALTH -> "🧠"
        PHILOSOPHY -> "🤔"
        CULTURE_HOBBIES -> "🎨"
        INTIMACY_ROMANCE -> "❤️"
        FRIENDSHIP_SOCIAL -> "👥"
    }
}

/**
 * Goal hierarchy structure with 4 levels
 * Level 0 (Macrogroup): Domain-level categories
 * Level 1 (Intermediate): Subcategories within domains
 * Level 2 (Specific): Concrete goal types
 * Level 3 (Detail): User's custom details
 */
data class GoalCategory(
    val emoji: String,
    val name: String,
    val subcategories: List<GoalSubcategory>
)

data class GoalSubcategory(
    val emoji: String,
    val name: String,
    val specificGoals: List<SpecificGoal>
)

data class SpecificGoal(
    val emoji: String,
    val name: String
)

/**
 * Feedback grades after collaboration (from whitepaper section 3.5)
 */
enum class FeedbackGrade {
    DISTRACTED,        // Increases weight
    TOTAL_NOOB,        // Strongly increases weight
    MEDIOCRE,          // Slight increase
    TRIED_BUT_FAILED,  // Neutral/slight decrease
    SUCCEEDED;         // Decreases weight
    
    fun displayName(): String = when (this) {
        DISTRACTED -> "Distracted"
        TOTAL_NOOB -> "Total Noob"
        MEDIOCRE -> "Mediocre"
        TRIED_BUT_FAILED -> "Tried but Failed"
        SUCCEEDED -> "Succeeded"
    }
}
