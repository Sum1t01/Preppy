package com.sum1t.preppy.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sum1t.preppy.domain.model.Grade
import com.sum1t.preppy.domain.usecase.userpreferences.GetDarkModeUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetHapticsEnabledUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetOnboardingStatusUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetDarkModeUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetOnboardingStatusUseCase
import com.sum1t.preppy.presentation.screens.levelSelection.LevelSelectionEvent
import com.sum1t.preppy.presentation.screens.levelSelection.LevelSelectionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class OnboardingUiState(
    val isDarkMode: Boolean = false,
    val isHapticsEnabled: Boolean = true
)

sealed class OnboardingEvent {
    object CompleteOnboarding : OnboardingEvent()
    object ToggleTheme : OnboardingEvent()

}

class OnboardingViewModel(
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase,
    private val setOnboardingStatusUseCase: SetOnboardingStatusUseCase,
    private val getHapticsEnabledUseCase: GetHapticsEnabledUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        observePreferences()
    }

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.ToggleTheme -> toggleTheme()
            is OnboardingEvent.CompleteOnboarding -> {
                completeOnboarding()
            }
        }
    }

    private fun toggleTheme() {
        viewModelScope.launch {
            val newValue = !_uiState.value.isDarkMode
            setDarkModeUseCase.invoke(newValue)
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            combine(
                getHapticsEnabledUseCase.invoke(),
                getDarkModeUseCase.invoke()
            ) { haptics, dark ->
                haptics to dark
            }.collect { (haptics, dark) ->

                _uiState.update { current ->
                    current.copy(
                        isHapticsEnabled = haptics,
                        isDarkMode = dark
                    )
                }
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            setOnboardingStatusUseCase.invoke(true)
        }
    }
}