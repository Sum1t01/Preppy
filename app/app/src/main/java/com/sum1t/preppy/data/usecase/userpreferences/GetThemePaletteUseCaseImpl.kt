package com.sum1t.preppy.data.usecase.userpreferences

import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import com.sum1t.preppy.domain.usecase.userpreferences.GetThemePaletteUseCase
import com.sum1t.preppy.presentation.ui.theme.ThemePalette
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetThemePaletteUseCaseImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : GetThemePaletteUseCase {

    override fun invoke(): Flow<ThemePalette> {
        return userPreferencesDataStore.selectedThemePalette.map { value ->
                when (value) {

                    "DEFAULT_GREEN" -> ThemePalette.DEFAULT_GREEN
                    "OCEAN_BLUE" -> ThemePalette.OCEAN_BLUE
                    "SUNSET_ORANGE" -> ThemePalette.SUNSET_ORANGE
                    "MONOCHROME" -> ThemePalette.MONOCHROME
                    "FOREST" -> ThemePalette.FOREST
                    "NEON_PURPLE" -> ThemePalette.NEON_PURPLE
                    "COFFEE" -> ThemePalette.COFFEE
                    "MIDNIGHT" -> ThemePalette.MIDNIGHT
                    else -> ThemePalette.DEFAULT_GREEN
//                    else -> ThemePalette.MIDNIGHT
                }
            }
    }
}