package com.sum1t.preppy.data.usecase.question

import com.sum1t.preppy.common.utils.NetworkResult
import com.sum1t.preppy.domain.repository.question.QuestionRepository
import com.sum1t.preppy.domain.usecase.question.GetTotalQuestionsCountUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTotalQuestionsCountUseCaseImpl(
    private val questionRepository: QuestionRepository
) : GetTotalQuestionsCountUseCase {
    override suspend fun invoke(): Flow<Long> {
        return questionRepository.fetchTotalQuestionCount()
    }
}