package com.sum1t.preppy.data.usecase.userpreferences

import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import com.sum1t.preppy.domain.usecase.userpreferences.SetNotificationEnabledUseCase

class SetNotificationEnabledUseCaseImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore
): SetNotificationEnabledUseCase {
    override suspend fun invoke(enabled: Boolean) {
        userPreferencesDataStore.setNotificationsEnabled(enabled)
    }
}