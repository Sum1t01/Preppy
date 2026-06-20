package com.sum1t.preppy.presentation.screens.subjectSelection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.sum1t.preppy.domain.model.GradeSubjectsDTO
import com.sum1t.preppy.domain.model.Subject
import com.sum1t.preppy.ui.components.AnimatedThemedButton
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectSelectionScreen(
    grades: List<Long>,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    viewModel: SubjectSelectionViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val isButtonEnabled = state.selectedSubjects.isNotEmpty()

    LaunchedEffect(Unit) {
        viewModel.onEvent(SubjectSelectionEvent.Refresh(grades))
    }

    LaunchedEffect(state.navigateToHome) {
        if (state.navigateToHome) {
            onContinue()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Subjects") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        bottomBar = {
            AnimatedThemedButton(
                text = "Continue",
//                enabled = isButtonEnabled,
                enabled = true,
                enableHaptics = state.isHapticsEnabled
            ) {
                onContinue()
                viewModel.onEvent(SubjectSelectionEvent.onContinue)
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when {
                state.isLoading -> {

                    LazyColumn {
                        items(6) {
                            ShimmerGradeCard()
                        }
                    }
                }

                state.error != null -> Text(state.error!!)

                else -> {
                    LazyColumn(
                        modifier = Modifier
//                            .padding(padding)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Top
                    ) {

                        items(
                            items = state.grades,
                            key = { it.gradeId }
                        ) { grade ->

                            val expanded = state.expandedGradeId == grade.gradeId

                            AnimatedGradeCard(
                                grade = grade,
                                expanded = expanded,
                                selectedSubjects = state.selectedSubjects,
                                maxSelectionReached = false,
                                onExpandGrade = {
                                    viewModel.onEvent(
                                        SubjectSelectionEvent.OnExpandGrade(it)
                                    )
                                },
                                onSelectSubject = {
                                    viewModel.onEvent(
                                        SubjectSelectionEvent.OnSelectSubject(it)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedGradeCard(
    grade: GradeSubjectsDTO,
    expanded: Boolean,
    selectedSubjects: Set<Long>,
    maxSelectionReached: Boolean,
    onExpandGrade: (Long) -> Unit,
    onSelectSubject: (Long) -> Unit
) {

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(grade.gradeId) {
        delay(80L)
        visible = true
    }

    val elevation by animateDpAsState(
        targetValue = if (expanded) 10.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    val glowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .shadow(
                    elevation = elevation,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = glowColor,
                    spotColor = glowColor
                )
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = elevation)
            ) {

                Column {

                    // HEADER
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onExpandGrade(grade.gradeId) }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = grade.gradeName,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = if (expanded) "▲" else "▼",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // SUBJECTS
                    if (expanded) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                                )
                        ) {

                            AnimatedSubjectList(
                                subjects = grade.subjects,
                                selected = selectedSubjects,
                                maxSelectionReached = maxSelectionReached,
                                onToggle = onSelectSubject
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AnimatedSubjectList(
    subjects: List<Subject>,
    selected: Set<Long>,
    maxSelectionReached: Boolean,
    onToggle: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        subjects.forEachIndexed { index, subject ->

            var visible by remember(subject.id) { mutableStateOf(false) }

            val isSelected = selected.contains(subject.id)
            val isEnabled = isSelected || !maxSelectionReached

            LaunchedEffect(subject.id) {
                delay(index * 80L)
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(
                    animationSpec = spring()
                ) + slideInVertically(
                    initialOffsetY = { it / 2 }
                ) + scaleIn(
                    initialScale = 0.9f
                )
            ) {

                AnimatedSubjectItem(
                    subject = subject,
                    enabled = isEnabled,
                    isSelected = isSelected,
                    onClick = { onToggle(subject.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun AnimatedSubjectItem(
    subject: Subject,
    isSelected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    val contentAlpha = if (enabled) 1f else 0.4f

    val scale by animateFloatAsState(
        targetValue = if (isSelected && enabled) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> colorScheme.outline.copy(alpha = 0.12f)
            isSelected -> colorScheme.secondary
            else -> colorScheme.outline.copy(alpha = 0.35f)
        },
        animationSpec = spring()
    )

    val backgroundColor = when {
        !enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        isSelected -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .scale(scale)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .alpha(contentAlpha)
    ) {
        Text(
            text = subject.name,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
    }
}


@Composable
fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")

    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        ),
        label = "shimmer_translate"
    )

    return Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        ),
        start = Offset(translateAnim, translateAnim),
        end = Offset(translateAnim + 400f, translateAnim + 400f)
    )
}

@Composable
fun ShimmerGradeCard() {
    val shimmerBrush = rememberShimmerBrush()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {

            Column(modifier = Modifier.padding(16.dp)) {

                // Header shimmer
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(22.dp)
                        .background(shimmerBrush, RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fake subject rows shimmer
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(vertical = 6.dp)
                            .background(shimmerBrush, RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
