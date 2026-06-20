package com.sum1t.preppy.data.usecase.userpreferences

import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import com.sum1t.preppy.domain.usecase.userpreferences.SetLevelSubjectSelectedUseCase

class SetLevelSubjectSelectedUseCaseImpl(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : SetLevelSubjectSelectedUseCase {
    override suspend fun invoke(selected: Boolean) {
        userPreferencesDataStore.setLevelSubjectSelected(selected)
    }
}