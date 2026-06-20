package com.sum1t.preppy.domain.usecase.question

import com.sum1t.preppy.common.utils.NetworkResult
import kotlinx.coroutines.flow.Flow

interface GetAttemptedQuestionsCountUseCase {
    suspend fun invoke(): Flow<Long>

}