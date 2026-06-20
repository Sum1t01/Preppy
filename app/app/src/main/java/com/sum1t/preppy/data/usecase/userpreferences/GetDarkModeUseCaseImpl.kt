package com.sum1t.preppy.data.usecase.userpreferences

import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import com.sum1t.preppy.domain.usecase.userpreferences.GetDarkModeUseCase
import kotlinx.coroutines.flow.Flow

class GetDarkModeUseCaseImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : GetDarkModeUseCase {
    override fun invoke(): Flow<Boolean> {
        return userPreferencesDataStore.darkMode
    }
}
