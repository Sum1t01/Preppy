package com.sum1t.preppy.data.usecase.userpreferences

import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import com.sum1t.preppy.domain.usecase.userpreferences.GetLevelSubjectSelectedUseCase
import kotlinx.coroutines.flow.Flow

class GetLevelSubjectSelectedUseCaseImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore
): GetLevelSubjectSelectedUseCase {
    override fun invoke(): Flow<Boolean> {
        return userPreferencesDataStore.levelSubjectSelected
    }
}