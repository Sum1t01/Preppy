package com.sum1t.preppy.domain.usecase.userpreferences

interface SetOnboardingStatusUseCase {
    suspend fun invoke(completed: Boolean)
}