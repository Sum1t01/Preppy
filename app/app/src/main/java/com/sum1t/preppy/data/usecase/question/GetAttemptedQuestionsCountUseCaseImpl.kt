package com.sum1t.preppy.data.usecase.question

import com.sum1t.preppy.common.utils.NetworkResult
import com.sum1t.preppy.domain.repository.question.QuestionRepository
import com.sum1t.preppy.domain.usecase.question.GetAttemptedQuestionsCountUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAttemptedQuestionsCountUseCaseImpl(
    private val questionRepository: QuestionRepository
): GetAttemptedQuestionsCountUseCase {
    override suspend fun invoke(): Flow<Long> {
        return questionRepository.fetchAttemptedQuestionCount()
    }
}