package com.sum1t.preppy.domain.usecase.userpreferences

interface SetLevelSubjectSelectedUseCase {
    suspend fun invoke(selected: Boolean)
}