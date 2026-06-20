package com.sum1t.preppy.presentation.screens.levelSelection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sum1t.preppy.domain.model.Grade
import com.sum1t.preppy.presentation.composables.AnimatedGradeItem
import com.sum1t.preppy.ui.components.AnimatedThemedButton
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectionScreen(
    onContinue: (List<Long>) -> Unit,
    viewModel: LevelSelectionViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val isButtonEnabled = state.selected.size in 1..3

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { viewModel.onEvent(LevelSelectionEvent.Refresh) }
    )

    val maxSelectionReached = state.selected.size >= 3

    LaunchedEffect(Unit) {
        viewModel.onEvent(LevelSelectionEvent.Refresh)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Levels") },
                actions = {
                    ThemeToggleButton(
                        isDarkMode = state.isDarkMode,
                        onToggle = {
                            viewModel.onEvent(LevelSelectionEvent.ToggleTheme)
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {

            if (state.isLoading) {
                AnimatedThemedButtonShimmer()
            } else {
                AnimatedThemedButton(
                    text = "Continue",
//                    enabled = isButtonEnabled,
                    enabled = true,
                    enableHaptics = state.isHapticsEnabled
                ) {
                    viewModel.onEvent(LevelSelectionEvent.Continue(onContinue))
//                    onContinue(state.selected.toList())
                }
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .pullRefresh(
                    state = pullRefreshState,
                    enabled = !state.isLoading
                )
        ) {

            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.TopCenter)
                    ) {
                        repeat(6) {
                            GradeShimmerItem()
                        }
                    }
                }

                state.error != null || state.grades?.isEmpty() == true -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.error ?: "")

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = { viewModel.onEvent(LevelSelectionEvent.Refresh) }) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    state.grades?.let {
                        AnimatedGradeList(
                            grades = it,
                            selected = state.selected,
                            maxSelectionReached = maxSelectionReached,
                            onToggle = { id ->
                                viewModel.onEvent(LevelSelectionEvent.ToggleGrade(id))
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = state.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}


@Composable
fun ThemeToggleButton(
    isDarkMode: Boolean,
    onToggle: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isDarkMode) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "rotation"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isDarkMode) {
            MaterialTheme.colorScheme.secondary
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        },
        animationSpec = tween(400),
        label = "icon_color"
    )

    IconButton(onClick = onToggle) {
        Crossfade(
            targetState = isDarkMode,
            animationSpec = tween(300),
            label = "theme_crossfade"
        ) { dark ->
            Icon(
                tint = iconColor,
                imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                contentDescription = "Toggle Theme",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
fun AnimatedGradeList(
    grades: List<Grade>,
    selected: Set<Long>,
    maxSelectionReached: Boolean,
    onToggle: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        itemsIndexed(
            items = grades,
            key = { _, item -> item.id }
        ) { index, grade ->

            var visible by remember { mutableStateOf(false) }

            val isSelected = selected.contains(grade.id)
            val isEnabled = isSelected || !maxSelectionReached

            // 🔥 smoother stagger (matches Subject screen feel)
            LaunchedEffect(Unit) {
                delay(index * 80L)
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(
                    animationSpec = tween(300)
                ) +
                        slideInVertically(
                            initialOffsetY = { it / 3 } // less aggressive
                        ) +
                        scaleIn(
                            initialScale = 0.92f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
            ) {

                // 🔥 Add glow/elevation wrapper like Subject cards
                val elevation by animateDpAsState(
                    targetValue = if (isSelected) 10.dp else 2.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                )

                val glowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp) // tighter than subject cards
//                        .shadow(
//                            elevation = elevation,
//                            shape = RoundedCornerShape(16.dp),
////                            ambientColor = glowColor,
////                            spotColor = glowColor
//                        )
                ) {

                    AnimatedGradeItem(
                        grade = grade,
                        enabled = isEnabled,
                        isSelected = isSelected,
                        onClick = { onToggle(grade.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun Modifier.shimmerEffect(): Modifier {
    val colorScheme = MaterialTheme.colorScheme

    val transition = rememberInfiniteTransition(label = "shimmer")

    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200, // smoother than 1500
                easing = LinearEasing
            )
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        colorScheme.surfaceVariant.copy(alpha = 0.3f),
        colorScheme.surfaceVariant.copy(alpha = 0.7f),
        colorScheme.surfaceVariant.copy(alpha = 0.3f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim, translateAnim),
        end = Offset(translateAnim + 400f, translateAnim + 400f)
    )

    return this.background(brush)
}


@Composable
fun GradeShimmerItem(
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val glowColor = colorScheme.primary.copy(alpha = 0.12f)

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = glowColor,
                spotColor = glowColor
            )
            .background(
                color = colorScheme.surfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = colorScheme.outline.copy(alpha = 0.12f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.95f)
                .height(20.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmerEffect()
        )
    }
}

@Composable
fun AnimatedThemedButtonShimmer(
    modifier: Modifier = Modifier.navigationBarsPadding(),
) {
    val colorScheme = MaterialTheme.colorScheme
    val glowColor = colorScheme.primary.copy(alpha = 0.12f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(52.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = glowColor,
                spotColor = glowColor
            )
            .background(
                color = colorScheme.primary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(28.dp)
            )
            .border(
                width = 1.dp,
                color = colorScheme.primary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(28.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.35f)
                .height(14.dp)
                .clip(RoundedCornerShape(6.dp))
                .shimmerEffect()
        )
    }
}