package com.sum1t.preppy.presentation.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sum1t.preppy.presentation.screens.levelSelection.ThemeToggleButton
import com.sum1t.preppy.ui.components.AnimatedThemedButton
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = koinViewModel(),
    onFinish: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {

                    // Theme toggle (icon → subtle)
                    ThemeToggleButton(
                        isDarkMode = state.isDarkMode,
                        onToggle = {
                            viewModel.onEvent(OnboardingEvent.ToggleTheme)
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Skip (text → primary action)
                    TextButton(
                        onClick = {
                            viewModel.onEvent(OnboardingEvent.CompleteOnboarding)
                            onFinish()
                        }
                    ) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->

                AnimatedOnboardingPage(
                    page = pages[page],
                    pageIndex = page,
                    currentPage = pagerState.currentPage
                )
            }

            BottomControls(
                hapticsEnabled = state.isHapticsEnabled,
                currentPage = pagerState.currentPage,
                totalPages = pages.size,
                onNext = {
                    scope.launch {
                        if (pagerState.currentPage < pages.lastIndex) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            viewModel.onEvent(OnboardingEvent.CompleteOnboarding)
                            onFinish()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun BottomControls(
    hapticsEnabled: Boolean = true,
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Dots Indicator
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(totalPages) { index ->
                val color = if (index == currentPage)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(color, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Animated button visibility
        AnimatedVisibility(
            visible = currentPage == totalPages - 1,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            AnimatedThemedButton(
                text = "Get Started 🚀",
                enabled = true,
                enableHaptics = hapticsEnabled
            ) {
                onNext()
            }
        }
    }
}

@Composable
fun AnimatedOnboardingPage(
    page: OnboardingPage,
    pageIndex: Int,
    currentPage: Int
) {
    val isSelected = pageIndex == currentPage

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.5f
    )

    val offsetY by animateDpAsState(
        targetValue = if (isSelected) 0.dp else 40.dp
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                translationY = offsetY.toPx()
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val rotation by animateFloatAsState(
            targetValue = if (isSelected) 0f else -30f
        )

        Text(
            text = page.emoji,
            fontSize = 80.sp,
            modifier = Modifier.graphicsLayer {
                rotationZ = rotation
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val emoji: String
)

val pages = listOf(
    OnboardingPage(
        title = "Learn by Playing 🎮",
        description = "Practice quizzes by grade, subject, and topic — made just for you.",
        emoji = "🧠"
    ),
    OnboardingPage(
        title = "Revise with Flashcards 🧾",
        description = "Quickly review concepts by grade, subject, and topic anytime.",
        emoji = "⚡"
    ),
    OnboardingPage(
        title = "Climb the Playboard 🗺️",
        description = "Unlock levels as you complete topics and master subjects.",
        emoji = "🚀"
    ),
    OnboardingPage(
        title = "Build Your Streak 🔥",
        description = "Stay consistent and grow your daily learning streak.",
        emoji = "🔥"
    )
)