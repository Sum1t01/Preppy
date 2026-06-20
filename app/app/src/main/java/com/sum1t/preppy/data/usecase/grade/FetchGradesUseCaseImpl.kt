package com.sum1t.preppy.data.usecase.grade

import com.sum1t.data.sqldelight.AppDatabaseQueries
import com.sum1t.preppy.common.utils.NetworkResult
import com.sum1t.preppy.domain.model.Grade
import com.sum1t.preppy.domain.repository.grade.GradesRepository
import com.sum1t.preppy.domain.usecase.grade.FetchGradesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class FetchGradesUseCaseImpl(
    private val gradesRepository: GradesRepository,
    private val appDatabaseQueries: AppDatabaseQueries
) : FetchGradesUseCase {
    override fun invoke(): Flow<NetworkResult<List<Grade>>> {
        return flow {
            emit(NetworkResult.Loading())
//            delay(2000)
            val response = gradesRepository.fetchGrades()
            if (response.isSuccess) {
                val payload = response.getOrNull()
                if (payload?.isSuccess() == true) {
                    payload?.data?.let {
                        emit(NetworkResult.Success(it))
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