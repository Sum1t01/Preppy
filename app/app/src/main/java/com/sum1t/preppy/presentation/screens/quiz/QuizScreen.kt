package com.sum1t.preppy.presentation.screens.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sum1t.preppy.domain.usecase.question.OptionResponse
import com.sum1t.preppy.domain.usecase.question.QuestionType
import com.sum1t.preppy.ui.components.AnimatedThemedButton
import com.sum1t.preppy.ui.components.ThemedButtonType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onBack: () -> Unit,
    viewModel: QuizViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val isHapticsEnabled by viewModel.isHapticsEnabled.collectAsState()

    // Session-wide timer — resets on load, freezes on finish
    var sessionSeconds by remember { mutableStateOf(0) }
    LaunchedEffect(state.isLoading, state.isFinished) {
        when {
            state.isLoading -> sessionSeconds = 0
            state.isFinished -> Unit
            else -> while (true) { delay(1000L); sessionSeconds++ }
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
                                text = if (finished) "Results" else "Quiz",
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
        // ── improvement 4: button fixed to bottom bar ──
        bottomBar = {
            if (!state.isLoading && !state.isFinished && state.questions.isNotEmpty()) {
                val qState = state.currentQuestionState
                val isSubmitted = qState?.isSubmitted ?: false
                val hasSelection = when (qState?.question?.questionType) {
                    QuestionType.INTEGER -> qState.integerAnswer.isNotBlank()
                    else -> qState?.selectedOptions?.isNotEmpty() ?: false
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    CheckContinueButton(
                        isSubmitted = isSubmitted,
                        hasSelection = hasSelection,
                        isLastQuestion = state.isLastQuestion,
                        isHapticsEnabled = isHapticsEnabled,
                        onCheck = { viewModel.onEvent(QuizUiEvent.Submit) },
                        onContinue = { viewModel.onEvent(QuizUiEvent.Next) }
                    )
                }
            }
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
                ResultScreen(
                    score = state.score,
                    total = state.questions.size,
                    correct = state.correctCount,
                    elapsedSeconds = sessionSeconds,
                    isHapticsEnabled = isHapticsEnabled,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    onRetry = {
                        sessionSeconds = 0
                        viewModel.onEvent(QuizUiEvent.Load)
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
                val qState = state.currentQuestionState

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
                        ScoreChip(score = state.score)
                    }

                    Spacer(Modifier.height(16.dp))

                    // Row 3: Explanation left | Timer right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExplanationToggleButton(
                            showExplanation = state.showExplanation,
                            isHapticsEnabled = isHapticsEnabled,
                            onClick = { viewModel.onEvent(QuizUiEvent.ToggleExplanation) }
                        )

                        QuizTimer(totalSeconds = sessionSeconds)
                    }

                    Spacer(Modifier.height(18.dp))

                    // Rows 4–5: Animated question + options
                    AnimatedContent(
                        targetState = state.currentIndex,
                        transitionSpec = {
                            val forward = targetState > initialState
                            (slideInHorizontally(tween(280)) { if (forward) it else -it } + fadeIn(
                                tween(220)
                            )) togetherWith
                                    (slideOutHorizontally(tween(220)) { if (forward) -it else it } + fadeOut(
                                        tween(180)
                                    ))
                        },
                        label = "question_transition"
                    ) { _ ->
                        Column {
                            // Row 4: Question card (improvement 3: gradient + shimmer + larger)
                            qState?.question?.let { question ->
                                QuestionCard(
                                    questionText = question.questionText,
                                    explanation = question.explanation,
                                    showExplanation = state.showExplanation,
                                    isSubmitted = qState.isSubmitted,
                                    isCorrect = qState.isCorrect
                                )
                            }

                            Spacer(Modifier.height(20.dp))

                            // Row 5: Options
                            if (qState?.question?.questionType == QuestionType.INTEGER) {
                                IntegerAnswerInput(
                                    value = qState.integerAnswer,
                                    isSubmitted = qState.isSubmitted,
                                    isCorrect = qState.isCorrect,
                                    onChange = { viewModel.onEvent(QuizUiEvent.SetInteger(it)) }
                                )
                            } else {
                                OptionsGrid(
                                    options = qState?.question?.options ?: emptyList(),
                                    selected = qState?.selectedOptions ?: emptySet(),
                                    isSubmitted = qState?.isSubmitted ?: false,
                                    isHapticsEnabled = isHapticsEnabled,
                                    onSelect = { viewModel.onEvent(QuizUiEvent.Select(it)) }
                                )
                            }

                            // Skip button — bottom-right, only before submission
                            AnimatedVisibility(
                                visible = qState?.isSubmitted == false,
                                enter = fadeIn(tween(200)),
                                exit = fadeOut(tween(150))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { viewModel.onEvent(QuizUiEvent.Next) }) {
                                        Text(
                                            text = "Skip  →",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = 0.42f
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}


// ─── Progress Bar with shimmer ────────────────────────────────────────────────

@Composable
fun QuizProgressBar(progress: Float, modifier: Modifier = Modifier) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "progress"
    )

    val shimmerTransition = rememberInfiniteTransition(label = "progressShimmer")
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing)),
        label = "shimmerX"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Gradient fill
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animated)
                .clip(RoundedCornerShape(50))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                )
                // Sweeping shimmer on top of gradient
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.55f),
                                Color.Transparent
                            ),
                            start = Offset(size.width * (shimmerOffset - 0.15f), 0f),
                            end = Offset(size.width * (shimmerOffset + 0.15f), size.height)
                        )
                    )
                }
        )
    }
}


// ─── Score Chip with zoom + border animation on change (improvement 5) ────────

@Composable
fun ScoreChip(score: Double, modifier: Modifier = Modifier) {
    val displayScore = remember(score) {
        if (score % 1.0 == 0.0) score.toInt().toString() else "%.1f".format(score)
    }

    val scaleAnim = remember { Animatable(1f) }
    val prevScore = remember { mutableStateOf<Double?>(null) }
    var flashBorderColor by remember { mutableStateOf(Color.Transparent) }

    LaunchedEffect(score) {
        val prev = prevScore.value
        prevScore.value = score
        if (prev == null) return@LaunchedEffect          // skip on initial render
        val delta = score - prev
        if (delta == 0.0) return@LaunchedEffect

        flashBorderColor = if (delta > 0) Color(0xFF43A047) else Color(0xFFE53935)
        scaleAnim.animateTo(1.22f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
        delay(80)
        scaleAnim.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        delay(120)
        flashBorderColor = Color.Transparent
    }

    Box(
        modifier = modifier
            .scale(scaleAnim.value)
            .border(
                width = if (flashBorderColor != Color.Transparent) 2.dp else 0.dp,
                color = flashBorderColor,
                shape = RoundedCornerShape(50)
            )
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(15.dp)
            )
            AnimatedContent(
                targetState = displayScore,
                transitionSpec = {
                    slideInVertically { -it } + fadeIn(tween(200)) togetherWith
                            slideOutVertically { it } + fadeOut(tween(150))
                },
                label = "score_anim"
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = "pts",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
            )
        }
    }
}


// ─── Quiz Timer — circular live display ──────────────────────────────────────

@Composable
fun QuizTimer(
    totalSeconds: Int,
    modifier: Modifier = Modifier
) {
    val minutes = totalSeconds / 60
    val secs = totalSeconds % 60

    val timerColor by animateColorAsState(
        targetValue = when {
            totalSeconds < 60  -> Color(0xFF43A047)
            totalSeconds < 120 -> Color(0xFFFB8C00)
            else               -> Color(0xFFE53935)
        },
        animationSpec = tween(800),
        label = "timerColor"
    )

    // Icon pulses slightly on each second tick
    val iconScale = remember { Animatable(1f) }
    LaunchedEffect(totalSeconds) {
        if (totalSeconds == 0) return@LaunchedEffect
        iconScale.animateTo(1.22f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
        iconScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
    }

    Row(
        modifier = modifier
            .background(timerColor.copy(alpha = 0.10f), RoundedCornerShape(50))
            .border(1.5.dp, timerColor.copy(alpha = 0.35f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            Icons.Filled.Timer,
            contentDescription = null,
            tint = timerColor,
            modifier = Modifier
                .size(15.dp)
                .graphicsLayer { scaleX = iconScale.value; scaleY = iconScale.value }
        )

        // Minutes — slides only when the minute value flips
        AnimatedContent(
            targetState = minutes,
            transitionSpec = {
                slideInVertically { -it } + fadeIn(tween(260)) togetherWith
                    slideOutVertically { it } + fadeOut(tween(180))
            },
            label = "timer_min"
        ) { min ->
            Text(
                text = "%02d".format(min),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = timerColor
            )
        }

        Text(
            text = ":",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = timerColor.copy(alpha = 0.5f)
        )

        // Seconds — slides every tick
        AnimatedContent(
            targetState = secs,
            transitionSpec = {
                slideInVertically { -it } + fadeIn(tween(200)) togetherWith
                    slideOutVertically { it } + fadeOut(tween(130))
            },
            label = "timer_sec"
        ) { sec ->
            Text(
                text = "%02d".format(sec),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = timerColor
            )
        }
    }
}


// ─── Difficulty Badge ─────────────────────────────────────────────────────────

@Composable
fun DifficultyBadge(difficulty: String?, modifier: Modifier = Modifier) {
    if (difficulty == null) return

    val (bg, fg) = when (difficulty.lowercase()) {
        "easy" -> Color(0xFF43A047) to Color.White
        "medium" -> Color(0xFFFB8C00) to Color.White
        "hard" -> Color(0xFFE53935) to Color.White
        else -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }

    Box(
        modifier = modifier
            .background(
                color = bg,
                shape = RoundedCornerShape(
                    topStart = 6.dp,
                    bottomStart = 6.dp,
                    topEnd = 20.dp,
                    bottomEnd = 20.dp
                )
            )
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = difficulty.uppercase(),
            color = fg,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
    }
}


// ─── Explanation Toggle Button (improvement 2) ────────────────────────────────

@Composable
fun ExplanationToggleButton(
    showExplanation: Boolean,
    isHapticsEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    val containerColor by animateColorAsState(
        targetValue = if (showExplanation) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(280),
        label = "expBtnBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (showExplanation) MaterialTheme.colorScheme.onTertiary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(280),
        label = "expBtnFg"
    )
    val bulbScale by animateFloatAsState(
        targetValue = if (showExplanation) 1.25f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "bulbScale"
    )
    val bulbRotation by animateFloatAsState(
        targetValue = if (showExplanation) 15f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "bulbRotate"
    )

    Row(
        modifier = modifier
            .background(containerColor, RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (isHapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                onClick()
            }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            Icons.Filled.Lightbulb,
            contentDescription = null,
            tint = if (showExplanation) Color(0xFFFFF176) else contentColor,
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer {
                    scaleX = bulbScale
                    scaleY = bulbScale
                    rotationZ = bulbRotation
                }
        )
        AnimatedContent(
            targetState = showExplanation,
            transitionSpec = {
                (slideInVertically { -it } + fadeIn(tween(220))) togetherWith
                        (slideOutVertically { it } + fadeOut(tween(180)))
            },
            label = "expBtnText"
        ) { show ->
            Text(
                text = if (show) "Hide Explanation" else "Show Explanation",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}


// ─── Question Card with animated gradient + shimmer (improvement 3) ───────────

@Composable
fun QuestionCard(
    questionText: String,
    explanation: String?,
    showExplanation: Boolean,
    isSubmitted: Boolean,
    isCorrect: Boolean?,
    modifier: Modifier = Modifier
) {
    val correctColor = Color(0xFF43A047)

    val borderColor by animateColorAsState(
        targetValue = when {
            isSubmitted && isCorrect == true -> correctColor
            isSubmitted && isCorrect == false -> MaterialTheme.colorScheme.error
            else -> Color.Transparent
        },
        animationSpec = tween(300),
        label = "cardBorder"
    )

    val primary = MaterialTheme.colorScheme.primaryContainer
    val tertiary = MaterialTheme.colorScheme.tertiaryContainer
    val surface = MaterialTheme.colorScheme.surface

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 300.dp)
            .border(
                width = if (isSubmitted) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 300.dp)
        ) {
            // Static diagonal gradient layer
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                surface,
                                primary.copy(alpha = 0.28f),
                                tertiary.copy(alpha = 0.16f),
                                surface
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
            )

            // Content on top
            Column(modifier = Modifier.padding(24.dp)) {

                // Correct/Incorrect status strip
                AnimatedVisibility(
                    visible = isSubmitted,
                    enter = expandVertically() + fadeIn(tween(250)),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 14.dp)
                        ) {
                            val icon =
                                if (isCorrect == true) Icons.Filled.Check else Icons.Filled.Close
                            val tint =
                                if (isCorrect == true) correctColor else MaterialTheme.colorScheme.error
                            val label = if (isCorrect == true) "Correct!" else "Incorrect"

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(tint.copy(alpha = 0.14f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    tint = tint,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = tint
                            )
                        }
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )
                    }
                }

                // Question text — larger (improvement 3)
                Text(
                    text = questionText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 30.sp
                )

                // Explanation section
                AnimatedVisibility(
                    visible = showExplanation && explanation != null,
                    enter = expandVertically(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(
                        tween(250)
                    ),
                    exit = shrinkVertically() + fadeOut(tween(200))
                ) {
                    Column {
                        Spacer(Modifier.height(18.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "💡  Explanation",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = explanation ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f),
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// ─── Options Grid ─────────────────────────────────────────────────────────────

@Composable
fun OptionsGrid(
    options: List<OptionResponse>,
    selected: Set<Long>,
    isSubmitted: Boolean,
    isHapticsEnabled: Boolean,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        options.chunked(2).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { option ->
                    OptionCard(
                        option = option,
                        isSelected = option.id in selected,
                        isSubmitted = isSubmitted,
                        isHapticsEnabled = isHapticsEnabled,
                        onClick = { onSelect(option.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}


// ─── Option Card ──────────────────────────────────────────────────────────────

@Composable
fun OptionCard(
    option: OptionResponse,
    isSelected: Boolean,
    isSubmitted: Boolean,
    isHapticsEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val correctColor = Color(0xFF43A047)

    val targetBg = when {
        isSubmitted && option.isCorrect -> correctColor.copy(alpha = 0.13f)
        isSubmitted && isSelected && !option.isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.11f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val targetBorder = when {
        isSubmitted && option.isCorrect -> correctColor
        isSubmitted && isSelected && !option.isCorrect -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)
    }

    val bgColor by animateColorAsState(targetBg, tween(280), label = "optBg")
    val borderColor by animateColorAsState(targetBorder, tween(280), label = "optBorder")
    val scale by animateFloatAsState(
        targetValue = if (isSelected && !isSubmitted) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "optScale"
    )
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 6.dp else 2.dp,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "optElev"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val haptic = LocalHapticFeedback.current

    ElevatedCard(
        modifier = modifier
            .scale(scale)
            .border(
                width = if (isSelected || (isSubmitted && option.isCorrect)) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isSubmitted
            ) {
                if (isHapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                onClick()
            },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation),
        colors = CardDefaults.elevatedCardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedVisibility(
                visible = isSubmitted && (option.isCorrect || isSelected),
                enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Icon(
                    imageVector = if (option.isCorrect) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = null,
                    tint = if (option.isCorrect) correctColor else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = option.text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


// ─── Integer Answer Input ─────────────────────────────────────────────────────

@Composable
fun IntegerAnswerInput(
    value: String,
    isSubmitted: Boolean,
    isCorrect: Boolean?,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val correctColor = Color(0xFF43A047)
    OutlinedTextField(
        value = value,
        onValueChange = { if (!isSubmitted) onChange(it.filter { c -> c.isDigit() || c == '-' }) },
        modifier = modifier.fillMaxWidth(),
        label = { Text("Enter your answer") },
        singleLine = true,
        enabled = !isSubmitted,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(14.dp),
        trailingIcon = {
            if (isSubmitted) {
                Icon(
                    imageVector = if (isCorrect == true) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = null,
                    tint = if (isCorrect == true) correctColor else MaterialTheme.colorScheme.error
                )
            }
        }
    )
}


// ─── Check / Continue Button ──────────────────────────────────────────────────

@Composable
fun CheckContinueButton(
    isSubmitted: Boolean,
    hasSelection: Boolean,
    isLastQuestion: Boolean,
    isHapticsEnabled: Boolean,
    onCheck: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = isSubmitted,
        transitionSpec = {
            (scaleIn(spring(Spring.DampingRatioMediumBouncy)) + fadeIn(tween(200))) togetherWith
                    (scaleOut() + fadeOut(tween(150)))
        },
        label = "btn_transition",
        modifier = modifier
    ) { submitted ->
        val label = when {
            submitted && isLastQuestion -> "Finish Quiz  🎉"
            submitted -> "Continue  →"
            else -> "Check Answer"
        }
        AnimatedThemedButton(
            text = label,
            type = if (submitted) ThemedButtonType.PRIMARY else ThemedButtonType.SECONDARY,
            enabled = submitted || hasSelection,
            enableHaptics = isHapticsEnabled,
            onClick = if (submitted) onContinue else onCheck
        )
    }
}


// ─── Result Screen ────────────────────────────────────────────────────────────

@Composable
fun ResultScreen(
    score: Double,
    total: Int,
    correct: Int,
    elapsedSeconds: Int,
    isHapticsEnabled: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(120)
        visible = true
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow)) + fadeIn(tween(400))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                val passRate = if (total > 0) correct.toDouble() / total else 0.0
                Text(
                    text = when {
                        passRate >= 0.9 -> "🏆"
                        passRate >= 0.7 -> "🎉"
                        passRate >= 0.5 -> "👍"
                        else -> "💪"
                    },
                    fontSize = 64.sp
                )

                Text(
                    "Quiz Complete!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "$correct out of $total correct",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                )

                Spacer(Modifier.height(16.dp))

                ResultRing(
                    score = score,
                    correct = correct,
                    total = total,
                    elapsedSeconds = elapsedSeconds
                )

                Spacer(Modifier.height(28.dp))

                AnimatedThemedButton(
                    text = "Try Again",
                    type = ThemedButtonType.PRIMARY,
                    enableHaptics = isHapticsEnabled,
                    onClick = onRetry
                )
            }
        }
    }
}


// ─── Result Ring — score arc + score + time combined ─────────────────────────

@Composable
fun ResultRing(
    score: Double,
    correct: Int,
    total: Int,
    elapsedSeconds: Int,
    modifier: Modifier = Modifier
) {
    val passRate = if (total > 0) correct.toDouble() / total else 0.0
    val targetSweep = (passRate * 360f).toFloat().coerceAtLeast(0f)
    val arcColor = when {
        passRate >= 0.7 -> Color(0xFF43A047)
        passRate >= 0.4 -> Color(0xFFFB8C00)
        else            -> Color(0xFFE53935)
    }
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    val sweepAnim = remember { Animatable(0f) }
    val countAnim = remember { Animatable(0f) }
    val scoreAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(350)
        launch { sweepAnim.animateTo(targetSweep, tween(1600, easing = FastOutSlowInEasing)) }
        launch { countAnim.animateTo(elapsedSeconds.toFloat(), tween(1600, easing = FastOutSlowInEasing)) }
        launch { scoreAnim.animateTo(score.toFloat(), tween(1400, easing = FastOutSlowInEasing)) }
    }

    val displayScore = scoreAnim.value.let { v ->
        if (score % 1.0 == 0.0) v.toInt().toString() else "%.1f".format(v)
    }
    val displaySecs = countAnim.value.toInt()
    val minutes = displaySecs / 60
    val secs = displaySecs % 60

    Box(
        modifier = modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 13.dp.toPx()
            val half = strokeWidth / 2f
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(half, half)

            // Track ring
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Score-percentage arc — fills proportionally to how many correct
            if (sweepAnim.value > 0f) {
                drawArc(
                    color = arcColor,
                    startAngle = -90f,
                    sweepAngle = sweepAnim.value,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // Inner content: score on top, time on bottom, separated by a divider
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 28.dp)
        ) {
            Text(
                text = "SCORE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = arcColor.copy(alpha = 0.65f),
                letterSpacing = 1.5.sp
            )
            AnimatedContent(
                targetState = displayScore,
                transitionSpec = { fadeIn(tween(80)) togetherWith fadeOut(tween(80)) },
                label = "ring_score"
            ) { s ->
                Text(
                    text = s,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = arcColor
                )
            }
            Text(
                text = "pts",
                style = MaterialTheme.typography.labelSmall,
                color = arcColor.copy(alpha = 0.55f)
            )

            Spacer(Modifier.height(6.dp))
            HorizontalDivider(
                modifier = Modifier.width(44.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp
            )
            Spacer(Modifier.height(6.dp))

            Text(
                text = "TIME",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = primaryColor.copy(alpha = 0.55f),
                letterSpacing = 1.5.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedContent(
                    targetState = minutes,
                    transitionSpec = {
                        slideInVertically { -it } + fadeIn(tween(260)) togetherWith
                            slideOutVertically { it } + fadeOut(tween(180))
                    },
                    label = "ring_min"
                ) { min ->
                    Text(
                        text = "%02d".format(min),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryColor
                    )
                }
                Text(
                    text = ":",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor.copy(alpha = 0.5f)
                )
                AnimatedContent(
                    targetState = secs,
                    transitionSpec = {
                        slideInVertically { -it } + fadeIn(tween(200)) togetherWith
                            slideOutVertically { it } + fadeOut(tween(140))
                    },
                    label = "ring_sec"
                ) { sec ->
                    Text(
                        text = "%02d".format(sec),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryColor
                    )
                }
            }
        }
    }
}
