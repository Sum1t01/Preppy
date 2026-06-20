package com.sum1t.preppy.presentation.screens.levelSelection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sum1t.preppy.common.utils.NetworkResult
import com.sum1t.preppy.domain.model.Grade
import com.sum1t.preppy.domain.usecase.grade.FetchGradesUseCase
import com.sum1t.preppy.domain.usecase.grade.SaveGradesUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetDarkModeUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetHapticsEnabledUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetDarkModeUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LevelSelectionUiState(
    val grades: List<Grade>? = null,
    val selected: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDarkMode: Boolean = false,
    val isHapticsEnabled: Boolean = true,
)

sealed class LevelSelectionEvent {
    object FetchGrades : LevelSelectionEvent()
    object ToggleTheme : LevelSelectionEvent()

    object Refresh : LevelSelectionEvent()
    data class ToggleGrade(val id: Long) : LevelSelectionEvent()

    data class Continue(val navigateToSubjectSelection: (List<Long>) -> Unit) :
        LevelSelectionEvent()
}

class LevelSelectionViewModel(
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase,
    private val fetchGradesUseCase: FetchGradesUseCase,
    private val saveGradesUseCase: SaveGradesUseCase,
    private val getHapticsEnabledUseCase: GetHapticsEnabledUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LevelSelectionUiState())
    val uiState: StateFlow<LevelSelectionUiState> = _uiState.asStateFlow()

    init {
        observePreferences()
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

    fun onEvent(event: LevelSelectionEvent) {
        when (event) {
            is LevelSelectionEvent.Refresh -> {
                fetchGrades()
            }

            is LevelSelectionEvent.ToggleTheme -> toggleTheme()
            is LevelSelectionEvent.ToggleGrade -> onGradeToggle(event.id)
            is LevelSelectionEvent.Continue -> onContinue(event.navigateToSubjectSelection)
            else -> {}
        }
    }

    private fun onContinue(navigateToSubjectSelection: (List<Long>) -> Unit) {
        viewModelScope.launch {
            saveGradesUseCase.invoke(
                _uiState.value.grades?.filter {
                    it.id in _uiState.value.selected
                } ?: emptyList()
            )
            navigateToSubjectSelection(_uiState.value.selected.toList())
        }
    }

    private fun toggleTheme() {
        viewModelScope.launch {
            val newValue = !_uiState.value.isDarkMode
            setDarkModeUseCase.invoke(newValue)
        }
    }

    private fun fetchGrades() {
        viewModelScope.launch {
            fetchGradesUseCase.invoke().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.update {
                            it.copy(isLoading = true, error = null)
                        }
                    }

                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                grades = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                    }

                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message ?: "Something went wrong"
                            )
                        }
                    }
                }
            }
        }
    }

    fun onGradeToggle(id: Long) {
        _uiState.update { state ->
            val updated = state.selected.toMutableSet()

            if (updated.contains(id)) {
                updated.remove(id)
            } else if (updated.size < 3) {
                updated.add(id)
            }

            state.copy(selected = updated)
        }
    }
}