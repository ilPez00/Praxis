package com.praxis.app.data.model

import java.util.Date
import java.util.UUID

/**
 * An achievement unlocked when a user completes a goal node.
 * Shown in the community feed with social voting and comments.
 * Ported from praxis_webapp Achievement.ts / DashboardPage.tsx
 */
data class Achievement(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String,
    val goalNodeId: String,
    val title: String,
    val description: String = "",
    val domain: Domain,
    val createdAt: Date = Date(),
    val totalUpvotes: Int = 0,
    val totalDownvotes: Int = 0,
    val comments: List<AchievementComment> = emptyList()
)

/**
 * A comment left on a community achievement.
 */
data class AchievementComment(
    val id: String = UUID.randomUUID().toString(),
    val achievementId: String,
    val userId: String,
    val userName: String,
    val content: String,
    val createdAt: Date = Date()
)
