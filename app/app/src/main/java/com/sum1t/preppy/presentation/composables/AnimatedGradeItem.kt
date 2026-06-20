package com.sum1t.preppy.presentation.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sum1t.preppy.domain.model.Grade

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.alpha

@Composable
fun AnimatedGradeItem(
    grade: Grade,
    isSelected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val colorScheme = MaterialTheme.colorScheme

    val contentAlpha = if (enabled) 1f else 0.4f

    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            pressed -> 0.95f
            isSelected && enabled -> 1.02f
            else -> 1f
        }
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
        !enabled -> colorScheme.surface.copy(alpha = 0.6f)
        isSelected -> colorScheme.secondary
        else -> colorScheme.surface
    }

    val textColor = when {
        !enabled -> colorScheme.onSurface.copy(alpha = 0.38f)
        isSelected -> colorScheme.onSecondary
        else -> colorScheme.onSurface
    }


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
            text = grade.name,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
    }
}