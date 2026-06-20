package com.sum1t.preppy.data.usecase.question

import com.sum1t.data.sqldelight.AppDatabaseQueries
import com.sum1t.preppy.common.utils.NetworkResult
import com.sum1t.preppy.data.ApiService
import com.sum1t.preppy.domain.repository.question.QuestionRepository
import com.sum1t.preppy.domain.usecase.question.FetchQuestionsByTopicIdsUseCase
import com.sum1t.preppy.domain.usecase.question.GetAllQuestionsRequestBody
import com.sum1t.preppy.domain.usecase.question.QuestionResponse
import com.sum1t.preppy.domain.usecase.question.toDbModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchQuestionsByTopicIdsUseCaseImpl(
    private val questionRepository: QuestionRepository
) : FetchQuestionsByTopicIdsUseCase {
    override fun invoke(request: GetAllQuestionsRequestBody): Flow<NetworkResult<Boolean>> {
        return flow {
            emit(NetworkResult.Loading())
            val response = questionRepository.fetchQuestionsByTopicIds(request)
            if (response.isSuccess) {
                val payload = response.getOrNull()
                if (payload?.isSuccess() == true) {
                    payload?.data?.let {
                        // Save to DB first
                        val saved = questionRepository.saveQuestions(it)

                        if (saved) {
                            emit(NetworkResult.Success(true))
                        } else {
                            emit(NetworkResult.Error("Failed to save questions locally"))
                        }
                    } ?: run {
                        emit(NetworkResult.Error("No data found!"))
                    }

                } else {
                    emit(NetworkResult.Error(payload?.error))
                }

            } else {
                emit(NetworkResult.Error(response.exceptionOrNull()?.message))
            }
        }
    }
}