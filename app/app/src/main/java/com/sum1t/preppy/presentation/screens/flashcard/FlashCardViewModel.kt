package com.sum1t.preppy.presentation.screens.flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sum1t.preppy.domain.usecase.question.GetQuestionsForQuizUseCase
import com.sum1t.preppy.domain.usecase.question.QuestionResponse
import com.sum1t.preppy.domain.usecase.userpreferences.GetHapticsEnabledUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FlashCardsState(
    val question: QuestionResponse,
    val isRevealed: Boolean = false,
)

data class FlashCardUiState(
    val isLoading: Boolean = true,
    val questions: List<QuestionResponse> = emptyList(),
    val currentIndex: Int = 0,
    val showExplanation: Boolean = false,
    val isFinished: Boolean = false,
    val flashcardsState: Map<Int, FlashCardsState> = emptyMap()
) {
    val currentFlashcardsState: FlashCardsState? get() = flashcardsState[currentIndex]

    val progress: Float get() = if (questions.isEmpty()) 0f else (currentIndex + 1f) / questions.size

    val isLastQuestion: Boolean get() = currentIndex == questions.size - 1

}

sealed class FlashCardUiEvents {
    data object Load : FlashCardUiEvents()
    data object Reveal : FlashCardUiEvents()
    data object Next : FlashCardUiEvents()
    data object Previous : FlashCardUiEvents()
    data object ToggleExplanation : FlashCardUiEvents()
}

class FlashCardViewModel(
    private val getQuestions: GetQuestionsForQuizUseCase,
    private val isHapticsEnabledUseCase: GetHapticsEnabledUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashCardUiState())
    val uiState = _uiState.asStateFlow()

    // Separate from quiz state — never reset by load(), hot from first emission
    val isHapticsEnabled = isHapticsEnabledUseCase.invoke()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)


    init {
        onEvent(FlashCardUiEvents.Load)
    }

    fun onEvent(event: FlashCardUiEvents) {
        when (event) {
            FlashCardUiEvents.Load -> loadQuizzes()
            else -> {

            }
        }
    }

    private fun loadQuizzes() {
        viewModelScope.launch {
            _uiState.update { FlashCardUiState(isLoading = true) }
            val questions = getQuestions.invoke()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    questions = questions,
                    flashcardsState = questions.mapIndexed { index, question ->
                        index to FlashCardsState(question)
                    }.toMap()
                )
            }
        }
    }

}