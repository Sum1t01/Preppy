package com.sum1t.preppy.presentation.screens.flashcard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sum1t.preppy.presentation.screens.quiz.DifficultyBadge
import com.sum1t.preppy.presentation.screens.quiz.ExplanationToggleButton
import com.sum1t.preppy.presentation.screens.quiz.QuizProgressBar
import com.sum1t.preppy.presentation.screens.quiz.QuizTimer
import com.sum1t.preppy.presentation.screens.quiz.QuizUiEvent
import com.sum1t.preppy.presentation.screens.quiz.ResultScreen
import com.sum1t.preppy.presentation.screens.quiz.ScoreChip
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashCardScreen(
    onBack: () -> Unit,
    viewModel: FlashCardViewModel = koinViewModel()
) {
    val isHapticsEnabled by viewModel.isHapticsEnabled.collectAsState()

    // Session-wide timer — resets on load, freezes on finish
    var sessionSeconds by remember { mutableStateOf(0) }

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isLoading, state.isFinished) {
        when {
            state.isLoading -> sessionSeconds = 0
            state.isFinished -> Unit
            else -> while (true) {
                delay(1000L); sessionSeconds++
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedContent(
                        targetState = state.isFinished,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "topbar_title"
                    ) { finished ->
                        if (!finished && state.questions.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${state.currentIndex + 1}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = " / ${state.questions.size}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
                                )
                            }
                        } else {
                            Text(
                                text = if (finished) "Results" else "Flashcard",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },

        bottomBar = {

        },
        containerColor = MaterialTheme.colorScheme.background

    ) { padding ->

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                }
            }

            state.isFinished -> {
                FlashCardResultScreen(
                    elapsedSeconds = sessionSeconds,
                    isHapticsEnabled = isHapticsEnabled,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    onRetry = {
                        sessionSeconds = 0
                        viewModel.onEvent(FlashCardUiEvents.Load)
                    }
                )
            }

            state.questions.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No questions available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }

            else -> {
                val qState = state.currentFlashcardsState

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(8.dp))

                    // Row 1: Progress bar (with shimmer)
                    QuizProgressBar(progress = state.progress)

                    Spacer(Modifier.height(20.dp))

                    // Row 2: Difficulty left | Score right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DifficultyBadge(difficulty = qState?.question?.difficultyLevel)
                        QuizTimer(totalSeconds = sessionSeconds)
                    }

                    Spacer(Modifier.height(20.dp))

                    qState?.question?.let {
                        FlashCard(
                            question = it.questionText,
                            answer = it.options?.firstOrNull { it.isCorrect }?.text ?: ""
                        )
                    }
                }
            }
        }


    }

}

@Composable
fun FlashCardResultScreen(
    elapsedSeconds: Int,
    isHapticsEnabled: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {

}


@Composable
fun FlashCard(
    question: String,
    answer: String,
    modifier: Modifier = Modifier
) {
    var flipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "card_rotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .graphicsLayer {
                rotationY = rotation

                // Improves 3D effect
                cameraDistance = 12f * density
            }
            .clickable {
                flipped = !flipped
            },
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            if (rotation <= 90f) {
                // Front side (Question)
                Text(
                    text = question,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            } else {
                // Back side (Answer)
                Box(
                    modifier = Modifier.graphicsLayer {
                        // Prevent mirrored text
                        rotationY = 180f
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        }
    }
}