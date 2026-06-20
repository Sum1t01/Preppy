package com.sum1t.preppy.domain.usecase.question

interface GetQuestionsForQuizUseCase {
    suspend fun invoke(): List<QuestionResponse>
}

