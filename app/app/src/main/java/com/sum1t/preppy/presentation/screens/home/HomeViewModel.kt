package com.sum1t.preppy.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sum1t.preppy.domain.usecase.question.GetAttemptedQuestionsCountUseCase
import com.sum1t.preppy.domain.usecase.question.GetTotalQuestionsCountUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetDarkModeUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetDarkModeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false,
    val userName: String? = null,

    val totalQuestions: Long = 0L,
    val solvedQuestions: Long = 0L,

    val error: String? = null
) {
    val progress: Float
        get() = if (totalQuestions == 0L) 0f else solvedQuestions.toFloat() / totalQuestions
}

data class QuizUiModel(
    val id: Long,
    val title: String,
    val description: String,
    val emoji: String = "🧠"
)

data class ContinueLearningUiModel(
    val id: Long,
    val title: String,
    val subtitle: String,
    val progress: Float, // 0f to 1f
    val emoji: String = "📚"
)

sealed class HomeEvent {
    data object LoadHome : HomeEvent()
    data object ToggleTheme : HomeEvent()
}

class HomeViewModel(
    private val getTotalQuestionsCountUseCase: GetTotalQuestionsCountUseCase,
    private val getAttemptedQuestionsCountUseCase: GetAttemptedQuestionsCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        startObserving()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {

            HomeEvent.LoadHome -> {}

            HomeEvent.ToggleTheme -> toggleTheme()
        }
    }

    fun startObserving() {
        viewModelScope.launch {
            combine(
                getAttemptedQuestionsCountUseCase.invoke(),
                getTotalQuestionsCountUseCase.invoke()
            ) { solved, total ->
                Pair(solved, total)
            }.collect { (solved, total) ->
                _uiState.update {
                    it.copy(
                        solvedQuestions = solved,
                        totalQuestions = total
                    )
                }
            }
        }
    }

    private fun toggleTheme() {
        val current = _uiState.value.isDarkMode
        _uiState.update { it.copy(isDarkMode = !current) }
    }
}
