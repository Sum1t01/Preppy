package com.sum1t.preppy.domain.usecase.userpreferences

import kotlinx.coroutines.flow.Flow

interface IsUserLoginEnabledUseCase {
    fun invoke(): Flow<Boolean>
}