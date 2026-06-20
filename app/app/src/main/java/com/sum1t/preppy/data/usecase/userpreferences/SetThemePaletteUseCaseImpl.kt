package com.sum1t.preppy.data.usecase.userpreferences

import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import com.sum1t.preppy.domain.usecase.userpreferences.SetThemePaletteUseCase

class SetThemePaletteUseCaseImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : SetThemePaletteUseCase {
    override suspend fun invoke(palette: String) {
        userPreferencesDataStore.setThemePalette(palette)
    }

}