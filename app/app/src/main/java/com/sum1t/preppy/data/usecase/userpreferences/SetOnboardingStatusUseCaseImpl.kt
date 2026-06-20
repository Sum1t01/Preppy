package com.sum1t.preppy.data.usecase.userpreferences

import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import com.sum1t.preppy.domain.usecase.userpreferences.SetOnboardingStatusUseCase

class SetOnboardingStatusUseCaseImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore
): SetOnboardingStatusUseCase {
    override suspend fun invoke(completed: Boolean) {
        userPreferencesDataStore.setOnboardingStatus(completed)
    }
}