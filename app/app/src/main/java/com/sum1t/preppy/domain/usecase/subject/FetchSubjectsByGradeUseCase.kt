package com.sum1t.preppy.domain.usecase.subject

import com.sum1t.preppy.common.utils.NetworkResult
import com.sum1t.preppy.domain.model.Grade
import com.sum1t.preppy.domain.model.GradeSubjectsDTO
import com.sum1t.preppy.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface FetchSubjectsByGradeUseCase {
    fun invoke(input: Input): Flow<NetworkResult<List<GradeSubjectsDTO>>>

    data class Input(
        val gradeIds: List<Long>
    )
}