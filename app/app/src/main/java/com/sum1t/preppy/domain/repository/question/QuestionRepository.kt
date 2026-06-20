package com.sum1t.preppy.domain.repository.question

import com.sum1t.preppy.common.utils.ApiResponse
import com.sum1t.preppy.domain.usecase.question.GetAllQuestionsRequestBody
import com.sum1t.preppy.domain.usecase.question.QuestionResponse
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    suspend fun fetchQuestionsByTopicIds(request: GetAllQuestionsRequestBody): Result<ApiResponse<List<QuestionResponse>>>

    suspend fun fetchTotalQuestionCount(): Flow<Long>

    suspend fun fetchAttemptedQuestionCount(): Flow<Long>

    suspend fun saveQuestions(questions: List<QuestionResponse>): Boolean

    suspend fun getQuestionsForQuiz(): List<QuestionResponse>
}


