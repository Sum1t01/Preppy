package com.sum1t.preppy.domain.usecase.userpreferences

interface SetHapticsEnabledUseCase {
    suspend  fun invoke(enabled: Boolean)
}