package com.sum1t.preppy.data.usecase.userpreferences

import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import com.sum1t.preppy.domain.usecase.userpreferences.GetOnboardingStatusUseCase
import kotlinx.coroutines.flow.Flow

class GetOnboardingStatusUseCaseImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : GetOnboardingStatusUseCase {
    override fun invoke(): Flow<Boolean> {
        return userPreferencesDataStore.onBoardingCompleted
    }
}