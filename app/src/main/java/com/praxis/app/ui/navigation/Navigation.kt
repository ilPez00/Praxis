package com.praxis.app.ui.navigation

/**
 * Navigation routes for the app
 * Each screen has a unique route string
 */
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object GoalSelection : Screen("goal_selection")
    object Home : Screen("home")
    object Matches : Screen("matches")
    object Chat : Screen("chat/{matchId}") {
        fun createRoute(matchId: String) = "chat/$matchId"
    }
    object Profile : Screen("profile")
}
