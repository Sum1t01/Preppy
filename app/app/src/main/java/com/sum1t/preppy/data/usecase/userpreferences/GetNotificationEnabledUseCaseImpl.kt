package com.sum1t.preppy.data.usecase.userpreferences

import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import com.sum1t.preppy.domain.usecase.userpreferences.GetNotificationEnabledUseCase
import kotlinx.coroutines.flow.Flow

class GetNotificationEnabledUseCaseImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : GetNotificationEnabledUseCase {
    override fun invoke(): Flow<Boolean> {
        return userPreferencesDataStore.notificationsEnabled
    }
}