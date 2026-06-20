package com.sum1t.preppy.data.usecase.userpreferences

import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import com.sum1t.preppy.domain.usecase.userpreferences.SetHapticsEnabledUseCase

class SetHapticsEnabledUseCaseImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : SetHapticsEnabledUseCase {
    override suspend fun invoke(enabled: Boolean) {
        userPreferencesDataStore.setHapticsEnabled(enabled)
    }
}