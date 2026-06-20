package com.sum1t.preppy.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sum1t.preppy.presentation.screens.levelSelection.ThemeToggleButton
import com.sum1t.preppy.presentation.screens.levelSelection.shimmerEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToProfileSettings: () -> Unit,
    onNavigateToFlashCards: () -> Unit,
    onNavigateToSubjects: () -> Unit = {},
    onNavigateToQuiz: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(HomeEvent.LoadHome)
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                onProfileClick = onNavigateToProfileSettings
            )
        }
    ) { padding ->

        when {
            state.isLoading -> {
                HomeShimmerLoading(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                )
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.error ?: "Something went wrong")

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.onEvent(HomeEvent.LoadHome)
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            else -> {
                HomeContent(
                    state = state,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    onNavigateToSubjects = onNavigateToSubjects,
                    onNavigateToQuiz = onNavigateToQuiz,
                    onNavigateToProfile = onNavigateToProfile,
                    onEvent = viewModel::onEvent,
                    onNavigateToFlashCards = onNavigateToFlashCards
                )
            }
        }
    }
}


@Composable
fun HomeContent(
    state: HomeUiState,
    modifier: Modifier = Modifier,
    onNavigateToSubjects: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToFlashCards: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onEvent: (HomeEvent) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Greeting Section
        item {
            AnimatedGreeting(state.userName)
        }

        item {
            ProgressRingCard(
                progress = state.progress,
                solved = state.solvedQuestions,
                total = state.totalQuestions,
                onClick = { onNavigateToSubjects() }
            )
        }

        item {
            FeatureColumn(
                onQuizClick = onNavigateToSubjects,
                onFlashcardClick = onNavigateToFlashCards,
                onNavigateToQuiz = onNavigateToQuiz
            )
        }


    }
}

@Composable
fun HomeShimmerLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(6) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .shimmerEffect()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(16.dp)
                    )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onProfileClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = ""
    )

    TopAppBar(
        title = {}, // no text
        navigationIcon = {}, // keep empty for balance
        actions = {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(42.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .pressClickEffect(onClick = onProfileClick),
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}


@Composable
fun AnimatedGreeting(name: String?) {
    var visible by remember { mutableStateOf(false) }
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    val greeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Hello"
    }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600)) + slideInVertically { it / 2 }
    ) {
        Column {
            Text(
                text = greeting,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Ready to learn today?",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun FeatureColumn(
    onQuizClick: () -> Unit,
    onFlashcardClick: () -> Unit,
    onNavigateToQuiz: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

        DuolingoCard(
            title = "Quiz",
            subtitle = "Challenge yourself",
            emoji = "🧠",
            color = MaterialTheme.colorScheme.primary,
            onClick = onNavigateToQuiz
        )

        DuolingoCard(
            title = "Flashcards",
            subtitle = "Quick revision",
            emoji = "⚡",
            color = MaterialTheme.colorScheme.secondary,
            onClick = onFlashcardClick
        )
    }
}

@Composable
fun DuolingoCard(
    title: String,
    subtitle: String,
    emoji: String,
    color: Color,
    isHapticsEnabled: Boolean = true,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    val elevation = remember { Animatable(14f) }
    val glow = remember { Animatable(0f) }

    val scope = rememberCoroutineScope()

    fun animatePress() {
        scope.launch {
            // PRESS DOWN
            launch {
                scale.animateTo(0.92f, tween(80))
            }
            launch {
                elevation.animateTo(4f, tween(80))
            }
            launch {
                rotation.animateTo(-1.5f, tween(80))
            }
            launch {
                glow.animateTo(0.2f, tween(80))
            }

            // Hold briefly (important for feel)
            delay(90)

            // RELEASE (BOUNCE BACK)
            launch {
                scale.animateTo(
                    1f,
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }
            launch {
                elevation.animateTo(14f, spring())
            }
            launch {
                rotation.animateTo(0f, spring())
            }
            launch {
                glow.animateTo(0f, tween(150))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        Card(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    rotationZ = rotation.value
                }
                .shadow(elevation.value.dp, RoundedCornerShape(24.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
//                    indication = rememberRipple()
                ) {
                    if (isHapticsEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                    }

                    animatePress()
                    onClick()
                },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = color)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                AnimatedEmoji(emoji)
            }
        }

        // ✨ Glow Overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.White.copy(alpha = glow.value))
        )
    }
}

@Composable
fun AnimatedEmoji(emoji: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Text(
        text = emoji,
        fontSize = 42.sp,
        modifier = Modifier.graphicsLayer {
            translationY = floatY
        }
    )
}

@Composable
fun Modifier.pressClickEffect(
    onClick: () -> Unit
): Modifier {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val scale = remember { Animatable(1f) }
    val glow = remember { Animatable(0f) }

    fun animatePress() {
        scope.launch {
            // PRESS
            launch { scale.animateTo(0.88f, tween(70)) }
            launch { glow.animateTo(0.15f, tween(70)) }

            delay(80)

            // RELEASE
            launch {
                scale.animateTo(
                    1f,
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch { glow.animateTo(0f, tween(120)) }
        }
    }

    return this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
//            indication = rememberRipple(bounded = false, radius = 24.dp)
        ) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            animatePress()

            scope.launch {
                delay(100) // smoother navigation feel
                onClick()
            }
        }
}


@Composable
fun ProgressRingCard(
    progress: Float,
    solved: Long,
    total: Long,
    onClick: () -> Unit
) {
    val clampedProgress = progress.coerceIn(0f, 1f)

    val animatedProgress = remember { Animatable(0f) }
    val previousProgress = remember { mutableStateOf(0f) }
    val glowAlpha = remember { Animatable(0f) }

    LaunchedEffect(clampedProgress) {
        if (clampedProgress > previousProgress.value) {
            glowAlpha.snapTo(0.35f)
            glowAlpha.animateTo(0f, tween(600))
        }

        previousProgress.value = clampedProgress

        animatedProgress.animateTo(
            clampedProgress,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // LEFT SIDE (Progress + text)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                // CENTERED PROGRESS BAR WRAPPER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Gray.copy(alpha = 0.2f))
                    )

                    // Progress fill
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress.value)
                            .clip(RoundedCornerShape(50))
                            .background(
                                MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.9f + glowAlpha.value
                                )
                            )
                    )

                    // ticks aligned properly
                    MilestoneTicks()
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$solved / $total",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "${(clampedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(10.dp))

            AnimatedEmoji("📚")
        }
    }
}

@Composable
private fun MilestoneTicks() {
    val milestones = listOf(0.25f, 0.5f, 0.75f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp) // match bar height exactly
    ) {
        milestones.forEach { point ->

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(point),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(Color.White.copy(alpha = 0.5f))
                )
            }
        }
    }
}




//data class QuizUiState(
//
//)
//
//sealed class QuizUiEvent {
//    data object Load : QuizUiEvent()
//    data class Select(val optionId: Long) : QuizUiEvent()
//    data object Submit : QuizUiEvent()
//    data object Next : QuizUiEvent()
//    data object Previous: QuizUiEvent()
//}
//data class QuestionState(
//
//)
//
//class QuizViewModel(
//    private val getQuestions: GetQuestionsForQuizUseCase
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(QuizUiState(isLoading = true))
//    val uiState = _uiState.asStateFlow()
//
//    private var answers = mutableMapOf<Int, QuestionState>()
//
//    init {
//        onEvent(QuizUiEvent.Load)
//    }
//
//    fun onEvent(event: QuizUiEvent) {
//        when (event) {
//
//        }
//    }
//
//    private fun load() = viewModelScope.launch {
//        val q = getQuestions.invoke()
//
//        answers = q.mapIndexed { index, question ->
//            index to QuestionState(question)
//        }.toMap().toMutableMap()
//
//        _uiState.update {
//            it.copy(
//                isLoading = false,
//                questions = q
//            )
//        }
//    }
//
//
//}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun QuizScreen(
//    onBack: () -> Unit,
//    viewModel: QuizViewModel = koinViewModel()
//) {
//    val state by viewModel.uiState.collectAsState()
//
//    val questionState = viewModel.getCurrentState()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Quiz") },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.Default.ArrowBack, null)
//                    }
//                }
//            )
//        }
//    ) { padding ->
//    }
//
//
//}
//@Serializable
//data class QuestionResponse(
//    val id: Long,
//    val questionText: String,
//    val explanation: String?,
//    val questionType: QuestionType,
//    val difficultyLevel: String?,
//    val questionTier: QuestionTier,
//    val marks: Double,
//    val negativeMarks: Double,
//    val topic: TopicResponse?,
//    val options: List<OptionResponse>
//)
//
//@Serializable
//
//data class OptionResponse(
//    val id: Long,
//    val text: String,
//    val isCorrect: Boolean
//)
//enum class QuestionType {
//    SINGLE,     // single correct answer
//    MULTIPLE,   // multiple correct answers
//    INTEGER     // numeric answer
//}
//
//Lets design the quiz screen. At the very top, first, row, there is a bar that displays the progress. No numbers just a cylindrical bar that is filled with color like duolingo. Below that, second row,  is a score counter that updates in real time. if correct answer, add marks else subtract negativeMarks. Position left. on same row at right is difficultyLevel in a ribbon like ui. Third row right has show explanation toggle. left has retry button. Fourth row has a pad ie elevated surface with theme-based background color to display questionText. Below it if toggled, explanation should be visible. Make explanation distinct from question text with color coding and font size. Fifth row, options with two row two column. Same elevated surface selectable one or more depending on QuestionType. if QuestionType is INTEGER just a textbox. Sixth row, Continue button. Make everything lively, animated and gamey like duolingo. I want production grade ui/ux. Use theme. Once all questions are answered, Just show score in the centre and retry below it.

