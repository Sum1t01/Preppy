package com.sum1t.preppy.domain.usecase.userpreferences

interface SetDarkModeUseCase {
    suspend fun invoke(enabled: Boolean)
}
