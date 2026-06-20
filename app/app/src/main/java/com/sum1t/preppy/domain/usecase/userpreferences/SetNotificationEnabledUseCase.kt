package com.sum1t.preppy.domain.usecase.userpreferences

interface SetNotificationEnabledUseCase {
    suspend fun invoke(enabled: Boolean)
}