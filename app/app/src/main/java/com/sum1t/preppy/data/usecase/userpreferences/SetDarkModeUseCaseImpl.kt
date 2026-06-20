package com.sum1t.preppy.data.usecase.userpreferences

import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import com.sum1t.preppy.domain.usecase.userpreferences.SetDarkModeUseCase

class SetDarkModeUseCaseImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : SetDarkModeUseCase {
    override suspend fun invoke(enabled: Boolean) {
        userPreferencesDataStore.setDarkMode(enabled)
    }
}
