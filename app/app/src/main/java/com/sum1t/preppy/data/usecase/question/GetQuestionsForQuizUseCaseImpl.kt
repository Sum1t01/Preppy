package com.sum1t.preppy.data.usecase.question

import com.sum1t.preppy.domain.repository.question.QuestionRepository
import com.sum1t.preppy.domain.usecase.question.GetQuestionsForQuizUseCase
import com.sum1t.preppy.domain.usecase.question.QuestionResponse

class GetQuestionsForQuizUseCaseImpl(
    private val questionRepository: QuestionRepository
) : GetQuestionsForQuizUseCase {
    override suspend fun invoke(): List<QuestionResponse> {
        return questionRepository.getQuestionsForQuiz()
    }
}