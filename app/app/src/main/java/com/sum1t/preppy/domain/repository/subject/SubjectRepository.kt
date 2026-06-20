package com.sum1t.preppy.domain.repository.subject

import com.sum1t.preppy.common.utils.ApiResponse
import com.sum1t.preppy.domain.model.GradeSubjectsDTO
import com.sum1t.preppy.domain.model.Subject

interface SubjectRepository {
    suspend fun fetchSubjects(gradeIds: List<Long>): Result<ApiResponse<List<GradeSubjectsDTO>>>
}
