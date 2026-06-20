package com.sum1t.preppy.domain.usecase.grade

import com.sum1t.preppy.common.baseusecases.SuspendingOutputUseCase
import com.sum1t.preppy.common.utils.NetworkResult
import com.sum1t.preppy.domain.model.Grade
import kotlinx.coroutines.flow.Flow

interface FetchGradesUseCase {
    fun invoke(): Flow<NetworkResult<List<Grade>>>
}