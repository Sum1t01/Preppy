package com.sum1t.preppy.domain.repository.userpreferences

import kotlinx.coroutines.flow.Flow

interface UserPreferencesDataStore {
    val darkMode: Flow<Boolean>
    val userName: Flow<String>

    val levelSubjectSelected: Flow<Boolean>

    val selectedThemePalette: Flow<String>

    val onBoardingCompleted: Flow<Boolean>

    val notificationsEnabled: Flow<Boolean>
    val hapticsEnabled: Flow<Boolean>
    val isUserLoginEnabled: Flow<Boolean>

    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setUserName(name: String)
    suspend fun setOnboardingStatus(completed: Boolean)

    suspend fun setLevelSubjectSelected(selected: Boolean)

    suspend fun setThemePalette(palette: String)

    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setHapticsEnabled(enabled: Boolean)

}
