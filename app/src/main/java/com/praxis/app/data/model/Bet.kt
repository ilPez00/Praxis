package com.praxis.app.data.model

import java.util.Date
import java.util.UUID

/**
 * A bet placed on completing a goal node by a deadline.
 * Implements the accountability betting system from whitepaper §3.5.
 * Stake is denominated in Praxis Points (loss-aversion mechanic).
 * Ported from praxis_webapp bettingController.ts / GoalTreePage.tsx
 */
data class Bet(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val goalNodeId: String,
    val goalName: String,
    val stake: Int,       // Praxis Points at risk
    val deadline: Date,
    val status: BetStatus = BetStatus.ACTIVE
)

enum class BetStatus {
    ACTIVE,
    WON,
    LOST,
    CANCELLED;

    fun displayName(): String = when (this) {
        ACTIVE -> "Active"
        WON -> "Won ✅"
        LOST -> "Lost ❌"
        CANCELLED -> "Cancelled"
    }
}
