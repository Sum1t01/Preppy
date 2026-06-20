package com.sum1t.preppy.presentation.screens.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sum1t.preppy.domain.repository.userpreferences.UserPreferencesDataStore
import com.sum1t.preppy.domain.usecase.userpreferences.GetDarkModeUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetHapticsEnabledUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetNotificationEnabledUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetThemePaletteUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetDarkModeUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetHapticsEnabledUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetNotificationEnabledUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetThemePaletteUseCase
import com.sum1t.preppy.presentation.ui.theme.ThemePalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class SettingsUiState(
    val selectedTheme: ThemePalette = ThemePalette.DEFAULT_GREEN,
    val isDarkMode: Boolean = false,
    val isHapticsEnabled: Boolean = true,
    val isNotificationsEnabled: Boolean = true
)

sealed class SettingsEvent {

    data class ThemeSelected(val theme: ThemePalette) : SettingsEvent()

    data object ToggleDarkMode : SettingsEvent()

    data object ToggleHaptics : SettingsEvent()

    data object ToggleNotifications : SettingsEvent()
}

data class Preferences(
    val theme: ThemePalette,
    val darkMode: Boolean,
    val haptics: Boolean,
    val notifications: Boolean
)

class SettingsViewModel(
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val setThemePaletteUseCase: SetThemePaletteUseCase,
    private val getThemePaletteUseCase: GetThemePaletteUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase,
    private val getHapticsEnabledUseCase: GetHapticsEnabledUseCase,
    private val setHapticsEnabledUseCase: SetHapticsEnabledUseCase,
    private val getNotificationEnabledUseCase: GetNotificationEnabledUseCase,
    private val setNotificationEnabledUseCase: SetNotificationEnabledUseCase,
) : ViewModel() {

    // UI state


    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        observePreferences()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {

            is SettingsEvent.ThemeSelected -> {
                onThemeSelected(event.theme)
            }

            SettingsEvent.ToggleDarkMode -> {
                onToggleDarkMode()
            }

            SettingsEvent.ToggleHaptics -> {
                onToggleHaptics()
            }

            SettingsEvent.ToggleNotifications -> {
                onToggleNotifications()
            }
        }
    }

    fun onToggleHaptics() {
        viewModelScope.launch {
            val current = _uiState.value.isHapticsEnabled
            setHapticsEnabledUseCase.invoke(!current)
        }
    }


    private fun observePreferences() {
        viewModelScope.launch {
            combine(
                getThemePaletteUseCase.invoke(),
                getDarkModeUseCase.invoke(),
                getHapticsEnabledUseCase.invoke(),
                getNotificationEnabledUseCase.invoke()
            ) { theme, dark, haptics, notification ->
                Preferences(theme, dark, haptics, notification)
            }.collect { perf ->
                _uiState.update {
                    it.copy(
                        selectedTheme = perf.theme,
                        isDarkMode = perf.darkMode,
                        isHapticsEnabled = perf.haptics,
                        isNotificationsEnabled = perf.notifications
                    )
                }
            }
        }
    }

    // 🎨 Theme
    fun onThemeSelected(theme: ThemePalette) {
        viewModelScope.launch {
            setThemePaletteUseCase.invoke(theme.name)
        }
    }

    // 🌙 Dark mode
    fun onToggleDarkMode() {
        viewModelScope.launch {
            setDarkModeUseCase.invoke(!_uiState.value.isDarkMode)
        }
    }


    // 🔔 Notifications
    fun onToggleNotifications() {
        viewModelScope.launch {
            setNotificationEnabledUseCase.invoke(!_uiState.value.isNotificationsEnabled)
        }
    }
}