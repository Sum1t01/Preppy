package com.sum1t.preppy.domain.usecase.userpreferences

import kotlinx.coroutines.flow.Flow

interface GetNotificationEnabledUseCase {
    fun invoke(): Flow<Boolean>
}