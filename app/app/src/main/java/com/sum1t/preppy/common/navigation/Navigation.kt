package com.sum1t.preppy.common.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sum1t.preppy.common.utils.Screen
import com.sum1t.preppy.domain.usecase.userpreferences.GetDarkModeUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetLevelSubjectSelectedUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetOnboardingStatusUseCase
import com.sum1t.preppy.ir.RemoteScreen
import com.sum1t.preppy.presentation.screens.flashcard.FlashCardScreen
import com.sum1t.preppy.presentation.screens.home.HomeScreen
import com.sum1t.preppy.presentation.screens.levelSelection.LevelSelectionScreen
import com.sum1t.preppy.presentation.screens.onboarding.OnboardingScreen
import com.sum1t.preppy.presentation.screens.profile.ProfileScreen
import com.sum1t.preppy.presentation.screens.quiz.QuizScreen
import com.sum1t.preppy.presentation.screens.setting.SettingsScreen
import com.sum1t.preppy.presentation.screens.subjectSelection.SubjectSelectionScreen
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue


@Composable
fun Navigation() {
    val navController = rememberNavController()
    val getDarkModeUseCase: GetOnboardingStatusUseCase by inject(GetOnboardingStatusUseCase::class.java)
    val isOnboardingCompleted by getDarkModeUseCase.invoke().collectAsState(
        initial = null
    )
    val getLevelSubjectSelectedUseCase: GetLevelSubjectSelectedUseCase by inject(
        GetLevelSubjectSelectedUseCase::class.java
    )
    val levelSubjectSelected by getLevelSubjectSelectedUseCase.invoke().collectAsState(
        initial = false
    )


    NavHost(
        navController = navController,
        startDestination = when (isOnboardingCompleted) {
            null -> {
                Screen.Default.route
            }

            true -> {
                when (levelSubjectSelected) {
                    true -> Screen.Home.route
                    false -> Screen.LevelSelection.route
                }
            }

            false -> {
                Screen.OnBoarding.route
            }
        },
        enterTransition = { smoothEnter() },
        exitTransition = { smoothExit() },
        popEnterTransition = { smoothPopEnter() },
        popExitTransition = { smoothPopExit() }
    ) {

        composable(Screen.OnBoarding.route) {
            OnboardingScreen {
                navController.navigate(Screen.LevelSelection.route) {
                    popUpTo("onboarding") { inclusive = true }
                }
            }
        }

        composable(
            route = Screen.LevelSelection.route
        ) {
            LevelSelectionScreen(
                onContinue = { selected ->
                    navController.navigate(
                        Screen.SubjectSelection.createRoute(selected)
                    )
                }
            )
        }

        composable(
            route = Screen.SubjectSelection.route,
            arguments = listOf(
                navArgument("grades") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val gradesArg = backStackEntry.arguments?.getString("grades") ?: ""
            val grades = gradesArg.split(",").mapNotNull { it.toLongOrNull() }

            SubjectSelectionScreen(
                grades = grades,
                onBack = {
                    navController.popBackStack()
                },
                onContinue = {
                    navController.navigate(Screen.Home.route)
                }
            )
        }



        composable(
            route = Screen.Settings.route
        ) {
            SettingsScreen {
                navController.popBackStack()
            }
        }

        composable(
            route = Screen.Default.route
        ) {
            // Show splash / loading
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }


        composable(
            route = Screen.Home.route
        ) {
            HomeScreen(
                onNavigateToProfileSettings = {
                    navController.navigate(Screen.Profile.route)

                },
                onNavigateToFlashCards = {
                    navController.navigate(Screen.FlashCard.route)
                },
                onNavigateToQuiz = {
                    navController.navigate(Screen.Quiz.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onRemoteScreen = {
                    navController.navigate(Screen.Remote.route)
                }
            )
        }

        composable(Screen.Quiz.route) {
            QuizScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }


        composable(Screen.FlashCard.route) {
            FlashCardScreen()
        }

        composable(Screen.Remote.route) {
            RemoteScreen()
        }
    }


}


private const val DURATION = 280
private val easing = FastOutSlowInEasing

private fun smoothEnter() =
    fadeIn(
        animationSpec = tween(DURATION, easing = easing)
    ) + slideInHorizontally(
        initialOffsetX = { it / 6 }, // 🔥 very small movement
        animationSpec = tween(DURATION, easing = easing)
    )

private fun smoothExit() =
    fadeOut(
        animationSpec = tween(DURATION - 50, easing = easing)
    ) + slideOutHorizontally(
        targetOffsetX = { -it / 8 }, // 🔥 subtle push
        animationSpec = tween(DURATION, easing = easing)
    )

private fun smoothPopEnter() =
    fadeIn(
        animationSpec = tween(DURATION, easing = easing)
    ) + slideInHorizontally(
        initialOffsetX = { -it / 8 },
        animationSpec = tween(DURATION, easing = easing)
    )

private fun smoothPopExit() =
    fadeOut(
        animationSpec = tween(DURATION - 50, easing = easing)
    ) + slideOutHorizontally(
        targetOffsetX = { it / 6 },
        animationSpec = tween(DURATION, easing = easing)
    )


