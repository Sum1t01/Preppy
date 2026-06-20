package com.sum1t.preppy.domain.usecase

import android.content.Context

interface SyncNotificationStateUseCase {
    suspend fun observe(context: Context)
}