package com.sum1t.preppy.data.usecase.subject

import com.sum1t.preppy.common.utils.NetworkResult
import com.sum1t.preppy.domain.model.GradeSubjectsDTO
import com.sum1t.preppy.domain.model.Subject
import com.sum1t.preppy.domain.repository.subject.SubjectRepository
import com.sum1t.preppy.domain.usecase.subject.FetchSubjectsByGradeUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchSubjectsByGradeUseCaseImpl(
    private val subjectRepository: SubjectRepository
) : FetchSubjectsByGradeUseCase {
    override fun invoke(input: FetchSubjectsByGradeUseCase.Input): Flow<NetworkResult<List<GradeSubjectsDTO>>> {
        return flow {
            emit(NetworkResult.Loading())
//            delay(2000)
            if (input.gradeIds.isEmpty()) {
                emit(NetworkResult.Error("No grades selected"))
                return@flow
            }
            val response = subjectRepository.fetchSubjects(input.gradeIds)
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