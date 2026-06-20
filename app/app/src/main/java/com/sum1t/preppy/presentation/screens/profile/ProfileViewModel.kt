package com.sum1t.preppy.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sum1t.preppy.domain.usecase.userpreferences.GetDarkModeUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetHapticsEnabledUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetNotificationEnabledUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetDarkModeUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetHapticsEnabledUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetNotificationEnabledUseCase
import com.sum1t.preppy.presentation.screens.setting.SettingsUiState
import com.sum1t.preppy.presentation.ui.theme.ThemePalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val avatarUrl: String? = null,

    val streak: Int = 0,

    val isDarkMode: Boolean = false,
    val isHapticsEnabled: Boolean = true,
    val isNotificationsEnabled: Boolean = true,

    val isLoading: Boolean = false
)

data class SettingsSection(
    val title: String,
    val items: List<SettingsItem>
)

data class SettingsItem(
    val title: String,
    val subtitle: String? = null,
    val action: SettingsAction
)

sealed class SettingsAction {
    data object ToggleTheme : SettingsAction()
    data object Notifications : SettingsAction()
    data object Logout : SettingsAction()
}

data class Preferences(
    val darkMode: Boolean,
    val haptics: Boolean,
    val notifications: Boolean
)
class ProfileViewModel(
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase,
    private val getHapticsEnabledUseCase: GetHapticsEnabledUseCase,
    private val setHapticsEnabledUseCase: SetHapticsEnabledUseCase,
    private val getNotificationEnabledUseCase: GetNotificationEnabledUseCase,
    private val setNotificationEnabledUseCase: SetNotificationEnabledUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUser()
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            combine(
                getDarkModeUseCase.invoke(),
                getHapticsEnabledUseCase.invoke(),
                getNotificationEnabledUseCase.invoke()

            ) { dark, haptics, notifications ->
                Preferences(dark, haptics, notifications)
            }.collect { perf ->

                _uiState.update { current ->
                    current.copy(
                        isHapticsEnabled = perf.haptics,
                        isDarkMode = perf.darkMode,
                        isNotificationsEnabled = perf.notifications
                    )
                }
            }
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    name = "John Doe",
                    username = "john_doe_99",
                    email = "john@example.com",
                    streak = 12
                )
            }
        }
    }

    fun onToggleTheme() {
        viewModelScope.launch {
            val current = _uiState.value.isDarkMode
            setDarkModeUseCase.invoke(!current)
        }
    }


    fun onToggleHaptics() {
        viewModelScope.launch {
            val current = _uiState.value.isHapticsEnabled
            setHapticsEnabledUseCase.invoke(!current)
        }
    }

    fun onToggleNotifications() {
        viewModelScope.launch {
            val current = _uiState.value.isNotificationsEnabled
            setNotificationEnabledUseCase.invoke(!current)

        }
    }
}