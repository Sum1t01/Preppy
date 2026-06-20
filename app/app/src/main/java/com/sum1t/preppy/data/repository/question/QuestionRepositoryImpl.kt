package com.sum1t.preppy.data.repository.question


import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.sum1t.data.sqldelight.AppDatabaseQueries
import com.sum1t.preppy.common.utils.ApiResponse
import com.sum1t.preppy.data.ApiService
import com.sum1t.preppy.domain.repository.question.QuestionRepository
import com.sum1t.preppy.domain.usecase.question.GetAllQuestionsRequestBody
import com.sum1t.preppy.domain.usecase.question.OptionResponse
import com.sum1t.preppy.domain.usecase.question.QuestionResponse
import com.sum1t.preppy.domain.usecase.question.toQuestionTier
import com.sum1t.preppy.domain.usecase.question.toQuestionType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuestionRepositoryImpl(
    private val apiService: ApiService,
    private val appDatabaseQueries: AppDatabaseQueries,
    private val ioDispatcher: CoroutineDispatcher
) : QuestionRepository {
    override suspend fun fetchQuestionsByTopicIds(request: GetAllQuestionsRequestBody): Result<ApiResponse<List<QuestionResponse>>> {
        return apiService.getAllQuestionsByTopicIds(request)
    }

    override suspend fun fetchTotalQuestionCount(): Flow<Long> {
        return appDatabaseQueries
            .getQuestionsCount()
            .asFlow()
            .map { it.executeAsOne() }
    }

    override suspend fun fetchAttemptedQuestionCount(): Flow<Long> {
        return appDatabaseQueries
            .getAttemptedQuestionsCount()
            .asFlow()
            .map { it.executeAsOne() }
    }

    override suspend fun saveQuestions(questions: List<QuestionResponse>): Boolean {
        return try {
            appDatabaseQueries.transaction {

                questions.forEach { question ->

                    appDatabaseQueries.insertQuestion(
                        question_text = question.questionText,
                        explanation = question.explanation,
                        question_type = question.questionType.name,
                        difficulty_level = question.difficultyLevel,
                        question_tier = question.questionTier.name,
                        marks = question.marks,
                        negative_marks = question.negativeMarks,
                        created_at = System.currentTimeMillis(),
                        updated_at = System.currentTimeMillis(),
                        topic_id = question.topic?.id,
                        is_deleted = 0L,
                        question_id = question.id
                    )

                    question.options.forEachIndexed { index, option ->
                        appDatabaseQueries.insertOption(
                            option_text = option.text,
                            is_correct = if (option.isCorrect) 1L else 0L,
                            option_order = index.toLong(),
                            question_id = question.id
                        )
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getQuestionsForQuiz(): List<QuestionResponse> {
        return appDatabaseQueries.transactionWithResult {
            val questions = appDatabaseQueries.getAllQuestions().executeAsList()
            val questionsObj = questions.map { question ->
                val options =
                    appDatabaseQueries.selectOptionsByQuestion(question.question_id).executeAsList()

                val optionsObj = options.map { option ->
                    OptionResponse(
                        id = option.id,
                        text = option.option_text,
                        isCorrect = option.is_correct != 0L
                    )
                }

                QuestionResponse(
                    id = question.id,
                    questionText = question.question_text,
                    explanation = question.explanation,
                    questionType = question.question_type.toQuestionType(),
                    difficultyLevel = question.difficulty_level,
                    questionTier = question.question_tier.toQuestionTier(),
                    marks = question.marks,
                    negativeMarks = question.negative_marks,
                    topic = null,
                    options = optionsObj
                )
            }
            questionsObj
        }

    }
}