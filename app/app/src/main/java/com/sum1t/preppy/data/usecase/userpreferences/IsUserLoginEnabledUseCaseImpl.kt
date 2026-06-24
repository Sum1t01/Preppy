package com.sum1t.preppy.data.usecase.userpreferences

import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import com.sum1t.preppy.domain.usecase.userpreferences.IsUserLoginEnabledUseCase
import kotlinx.coroutines.flow.Flow

class IsUserLoginEnabledUseCaseImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : IsUserLoginEnabledUseCase {

    override fun invoke(): Flow<Boolean> {
        return userPreferencesDataStore.isUserLoginEnabled
    }
}