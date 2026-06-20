package com.sum1t.preppy.domain.usecase.userpreferences

import com.sum1t.preppy.presentation.ui.theme.ThemePalette
import kotlinx.coroutines.flow.Flow

interface GetThemePaletteUseCase {
    fun invoke(): Flow<ThemePalette>
}