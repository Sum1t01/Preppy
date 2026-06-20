package com.sum1t.preppy.presentation.screens.subjectSelection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sum1t.preppy.common.utils.NetworkResult
import com.sum1t.preppy.domain.model.GradeSubjectsDTO
import com.sum1t.preppy.domain.usecase.question.FetchQuestionsByTopicIdsUseCase
import com.sum1t.preppy.domain.usecase.question.GetAllQuestionsRequestBody
import com.sum1t.preppy.domain.usecase.subject.FetchSubjectsByGradeUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.GetHapticsEnabledUseCase
import com.sum1t.preppy.domain.usecase.userpreferences.SetLevelSubjectSelectedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SubjectSelectionUiState(
    val expandedGradeId: Long? = null,
    val selectedSubjects: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val grades: List<GradeSubjectsDTO> = emptyList(),
    val isHapticsEnabled: Boolean = true,
    val navigateToHome: Boolean = false
)

sealed class SubjectSelectionEvent {
    data class Refresh(val grades: List<Long>) : SubjectSelectionEvent()
    data class OnExpandGrade(val gradeId: Long) : SubjectSelectionEvent()
    data class OnSelectSubject(val subjectId: Long) : SubjectSelectionEvent()

    data object onContinue : SubjectSelectionEvent()
}

class SubjectSelectionViewModel(
    private val fetchSubjectsByGradeUseCase: FetchSubjectsByGradeUseCase,
    private val setLevelSubjectSelectedUseCase: SetLevelSubjectSelectedUseCase,
    private val fetchQuestionsByTopicIdsUseCase: FetchQuestionsByTopicIdsUseCase,
    private val getHapticsEnabledUseCase: GetHapticsEnabledUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectSelectionUiState())
    val uiState: StateFlow<SubjectSelectionUiState> = _uiState.asStateFlow()

    init {
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            combine(
                getHapticsEnabledUseCase.invoke(),
            ) { haptics ->
                haptics
            }.collect { (haptics) ->
                _uiState.update { current ->
                    current.copy(
                        isHapticsEnabled = haptics
                    )
                }
            }
        }
    }

    fun onEvent(event: SubjectSelectionEvent) {
        when (event) {

            is SubjectSelectionEvent.Refresh -> {
                loadSubjects(event.grades)
            }

            is SubjectSelectionEvent.OnExpandGrade -> {
                onToggleGrade(event.gradeId)
            }

            is SubjectSelectionEvent.OnSelectSubject -> {
                _uiState.update { state ->
                    val updated = state.selectedSubjects.toMutableSet()

                    if (updated.contains(event.subjectId)) {
                        updated.remove(event.subjectId)
                    } else {
                        updated.add(event.subjectId)
                    }

                    state.copy(selectedSubjects = updated)
                }
            }

            is SubjectSelectionEvent.onContinue -> {
                setUpQuestionsOffline()
            }
        }
    }


    fun setUpQuestionsOffline() {

        viewModelScope.launch {

            val selectedSubjects = _uiState.value.selectedSubjects.toList()

            if (selectedSubjects.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No subjects selected"
                    )
                }
                return@launch
            }

            fetchQuestionsByTopicIdsUseCase.invoke(
                GetAllQuestionsRequestBody(
                    topicIds = selectedSubjects
                )
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                    }

                    is NetworkResult.Success -> {
                        setLevelSubjectSelectedUseCase.invoke(
                            selected = true
                        )
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = null,
                                navigateToHome = true
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


    fun onToggleGrade(gradeId: Long) {
        _uiState.update { state ->
            state.copy(
                expandedGradeId = if (state.expandedGradeId == gradeId) {
                    null // collapse if same
                } else {
                    gradeId // open new one
                }
            )
        }
    }

    private fun loadSubjects(grades: List<Long>) {
        viewModelScope.launch {
            val input = FetchSubjectsByGradeUseCase.Input(grades)

            fetchSubjectsByGradeUseCase.invoke(input).collect { result ->
                when (result) {

                    is NetworkResult.Loading -> {
                        _uiState.update {
                            it.copy(isLoading = true, error = null)
                        }
                    }

                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                grades = result.data ?: emptyList(),
                                expandedGradeId = result.data?.get(0)?.gradeId ?: null,
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
}