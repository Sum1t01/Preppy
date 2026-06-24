package com.sum1t.preppy.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.sum1t.preppy.datastore.UserPreferences
import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
    fileName = "user_prefs.pb",
    serializer = UserPreferencesSerializer
)

class UserPreferencesDataStoreImpl(
    private val context: Context
) : UserPreferencesDataStore {

    override val darkMode: Flow<Boolean>
        get() = context.userPreferencesStore.data.map { it.darkMode }

    override val userName: Flow<String>
        get() = context.userPreferencesStore.data.map { it.userName }
    override val onBoardingCompleted: Flow<Boolean>
        get() = context.userPreferencesStore.data.map { it.onboardingCompleted }

    override val levelSubjectSelected: Flow<Boolean>
        get() = context.userPreferencesStore.data.map { it.levelSubjectSelected }

    override val selectedThemePalette: Flow<String>
        get() = context.userPreferencesStore.data.map { it.selectedThemePalette }

    override val hapticsEnabled: Flow<Boolean>
        get() = context.userPreferencesStore.data.map { it.hapticsEnabled }

    override val notificationsEnabled: Flow<Boolean>
        get() = context.userPreferencesStore.data.map { it.notificationsEnabled }

    override val isUserLoginEnabled: Flow<Boolean>
        get() = context.userPreferencesStore.data.map { it.isUserLoginEnabled }


    override suspend fun setDarkMode(enabled: Boolean) {
        context.userPreferencesStore.updateData { prefs ->
            prefs.toBuilder().setDarkMode(enabled).build()
        }
    }

    override suspend fun setUserName(name: String) {
        context.userPreferencesStore.updateData { prefs ->
            prefs.toBuilder().setUserName(name).build()
        }
    }

    override suspend fun setOnboardingStatus(completed: Boolean) {
        context.userPreferencesStore.updateData { prefs ->
            prefs.toBuilder().setOnboardingCompleted(completed).build()
        }
    }

    override suspend fun setLevelSubjectSelected(selected: Boolean) {
        context.userPreferencesStore.updateData { prefs ->
            prefs.toBuilder().setLevelSubjectSelected(selected).build()

        }
    }

    override suspend fun setThemePalette(palette: String) {
        context.userPreferencesStore.updateData { prefs ->
            prefs.toBuilder().setSelectedThemePalette(palette).build()

        }
    }

    override suspend fun setHapticsEnabled(enabled: Boolean) {
        context.userPreferencesStore.updateData { prefs ->
            prefs.toBuilder().setHapticsEnabled(enabled).build()
        }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.userPreferencesStore.updateData { prefs ->
            prefs.toBuilder().setNotificationsEnabled(enabled).build()
        }
    }
}
