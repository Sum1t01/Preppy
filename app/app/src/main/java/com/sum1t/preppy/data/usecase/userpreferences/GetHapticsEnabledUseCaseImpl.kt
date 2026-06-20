package com.sum1t.preppy.data.usecase.userpreferences

import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import com.sum1t.preppy.domain.usecase.userpreferences.GetHapticsEnabledUseCase
import kotlinx.coroutines.flow.Flow

class GetHapticsEnabledUseCaseImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : GetHapticsEnabledUseCase {
    override fun invoke(): Flow<Boolean> {
        return userPreferencesDataStore.hapticsEnabled
    }
}