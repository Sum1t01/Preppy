package com.sum1t.preppy.presentation.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sum1t.preppy.domain.usecase.question.GetQuestionsForQuizUseCase
import com.sum1t.preppy.domain.usecase.question.QuestionResponse
import com.sum1t.preppy.domain.usecase.question.QuestionType
import com.sum1t.preppy.domain.usecase.userpreferences.GetHapticsEnabledUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizUiState(
    val isLoading: Boolean = true,
    val questions: List<QuestionResponse> = emptyList(),
    val currentIndex: Int = 0,
    val score: Double = 0.0,
    val showExplanation: Boolean = false,
    val isFinished: Boolean = false,
    val answers: Map<Int, QuestionState> = emptyMap()
) {
    val currentQuestionState: QuestionState? get() = answers[currentIndex]
    val progress: Float get() = if (questions.isEmpty()) 0f else (currentIndex + 1f) / questions.size
    val isLastQuestion: Boolean get() = currentIndex == questions.size - 1
    val correctCount: Int get() = answers.values.count { it.isCorrect == true }
}

data class QuestionState(
    val question: QuestionResponse,
    val selectedOptions: Set<Long> = emptySet(),
    val integerAnswer: String = "",
    val isSubmitted: Boolean = false,
    val isCorrect: Boolean? = null
)

sealed class QuizUiEvent {
    data object Load : QuizUiEvent()
    data class Select(val optionId: Long) : QuizUiEvent()
    data class SetInteger(val value: String) : QuizUiEvent()
    data object Submit : QuizUiEvent()
    data object Next : QuizUiEvent()
    data object Previous : QuizUiEvent()
    data object ToggleExplanation : QuizUiEvent()
}

class QuizViewModel(
    private val getQuestions: GetQuestionsForQuizUseCase,
    private val isHapticsEnabledUseCase: GetHapticsEnabledUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState = _uiState.asStateFlow()

    // Separate from quiz state — never reset by load(), hot from first emission
    val isHapticsEnabled = isHapticsEnabledUseCase.invoke()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        onEvent(QuizUiEvent.Load)
    }

    fun onEvent(event: QuizUiEvent) {
        when (event) {
            QuizUiEvent.Load -> load()
            is QuizUiEvent.Select -> selectOption(event.optionId)
            is QuizUiEvent.SetInteger -> setIntegerAnswer(event.value)
            QuizUiEvent.Submit -> submitAnswer()
            QuizUiEvent.Next -> goNext()
            QuizUiEvent.Previous -> goPrevious()
            QuizUiEvent.ToggleExplanation -> _uiState.update { it.copy(showExplanation = !it.showExplanation) }
        }
    }

    private fun goNext() {
        _uiState.update {
            if (it.currentIndex < it.questions.size - 1)
                it.copy(currentIndex = it.currentIndex + 1, showExplanation = false)
            else
                it.copy(isFinished = true)
        }
    }

    private fun goPrevious() {
        _uiState.update {
            if (it.currentIndex > 0) it.copy(
                currentIndex = it.currentIndex - 1,
                showExplanation = false
            )
            else it
        }
    }

    private fun selectOption(optionId: Long) {
        val state = _uiState.value
        val qState = state.answers[state.currentIndex] ?: return
        if (qState.isSubmitted) return

        val updated = when (qState.question.questionType) {
            QuestionType.SINGLE -> qState.copy(selectedOptions = setOf(optionId))
            QuestionType.MULTIPLE -> {
                val newSet = qState.selectedOptions.toMutableSet()
                if (!newSet.add(optionId)) newSet.remove(optionId)
                qState.copy(selectedOptions = newSet)
            }

            QuestionType.INTEGER -> qState
        }

        _uiState.update { it.copy(answers = it.answers + (it.currentIndex to updated)) }
    }

    private fun setIntegerAnswer(value: String) {
        val state = _uiState.value
        val qState = state.answers[state.currentIndex] ?: return
        if (qState.isSubmitted) return
        _uiState.update {
            it.copy(answers = it.answers + (it.currentIndex to qState.copy(integerAnswer = value)))
        }
    }

    private fun submitAnswer() {
        val state = _uiState.value
        val qState = state.answers[state.currentIndex] ?: return
        if (qState.isSubmitted) return

        val isCorrect = when (qState.question.questionType) {
            QuestionType.INTEGER -> {
                val correct = qState.question.options.firstOrNull { it.isCorrect }?.text?.trim()
                qState.integerAnswer.trim() == correct
            }

            else -> {
                val correctIds =
                    qState.question.options.filter { it.isCorrect }.map { it.id }.toSet()
                qState.selectedOptions == correctIds
            }
        }

        val delta = if (isCorrect) qState.question.marks else -qState.question.negativeMarks
        _uiState.update {
            it.copy(
                score = it.score + delta,
                answers = it.answers + (it.currentIndex to qState.copy(
                    isSubmitted = true,
                    isCorrect = isCorrect
                ))
            )
        }
    }

    private fun load() = viewModelScope.launch {
        _uiState.update { QuizUiState(isLoading = true) }
        val questions = getQuestions.invoke()
        _uiState.update {
            it.copy(
                isLoading = false,
                questions = questions,
                answers = questions.mapIndexed { index, question ->
                    index to QuestionState(question)
                }.toMap()
            )
        }
    }
}
