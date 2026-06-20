package com.sum1t.preppy.common.utils

sealed class Screen(val route: String) {

    data object Splash: Screen("splash")
    data object LevelSelection: Screen("level_selection")

    data object OnBoarding: Screen("onboarding")
    data object SubjectSelection : Screen("subject_selection/{grades}") {
        fun createRoute(grades: List<Long>): String {
            return "subject_selection/${grades.joinToString(",")}"
        }
    }
    data object Home : Screen("home")
    data object Default: Screen("default")
    data object ListingDetail : Screen("listing_detail")
    data object Settings : Screen("settings")
    data object Profile: Screen("profile")

   data object Quiz: Screen("quiz")
    data object FlashCard: Screen("flashcard")

    data object Remote: Screen("remote")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}