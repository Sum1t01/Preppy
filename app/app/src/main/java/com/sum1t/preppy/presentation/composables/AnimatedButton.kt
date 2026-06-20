package com.sum1t.preppy.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight

enum class ThemedButtonType { PRIMARY, SECONDARY, TERTIARY }

@Composable
fun AnimatedThemedButton(
    text: String,
    modifier: Modifier = Modifier
        .navigationBarsPadding(),
    type: ThemedButtonType = ThemedButtonType.TERTIARY,
    enabled: Boolean = true,
    enableHaptics: Boolean = false,
    onClick: () -> Unit
) {

    val haptic = LocalHapticFeedback.current



    // Base colors
    val baseBackgroundColor: Color
    val baseContentColor: Color
    val pressedAlpha = 0.9f

    when (type) {
        ThemedButtonType.PRIMARY -> {
            baseBackgroundColor = MaterialTheme.colorScheme.primary
            baseContentColor = MaterialTheme.colorScheme.onPrimary
        }

        ThemedButtonType.SECONDARY -> {
            baseBackgroundColor = MaterialTheme.colorScheme.secondary
            baseContentColor = MaterialTheme.colorScheme.onSecondary
        }

        ThemedButtonType.TERTIARY -> {
            baseBackgroundColor = MaterialTheme.colorScheme.tertiary
            baseContentColor = MaterialTheme.colorScheme.onTertiary
        }
    }

    // Adjust intensity for disabled
    val backgroundColor =
        if (enabled) baseBackgroundColor else baseBackgroundColor.copy(alpha = 0.4f)
    val contentColor = if (enabled) baseContentColor else baseContentColor.copy(alpha = 0.6f)

    // Interaction source for press state
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth scale animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    LaunchedEffect(isPressed) {
        if (isPressed && enabled && enableHaptics) {
            haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) { if (enabled) onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(52.dp)
                .background(
                    color = if (isPressed && enabled) backgroundColor.copy(alpha = pressedAlpha) else backgroundColor,
                    shape = RoundedCornerShape(28.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}