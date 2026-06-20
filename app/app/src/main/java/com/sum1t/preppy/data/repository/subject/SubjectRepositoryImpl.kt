package com.sum1t.preppy.data.repository.subject

import com.sum1t.preppy.common.utils.ApiResponse
import com.sum1t.preppy.data.ApiService
import com.sum1t.preppy.domain.model.GradeSubjectsDTO
import com.sum1t.preppy.domain.model.Subject
import com.sum1t.preppy.domain.model.request.GetSubjectsRequestBody
import com.sum1t.preppy.domain.repository.subject.SubjectRepository

class SubjectRepositoryImpl(
    private val apiService: ApiService
): SubjectRepository {
    override suspend fun fetchSubjects(gradeIds: List<Long>): Result<ApiResponse<List<GradeSubjectsDTO>>> {
        val input = GetSubjectsRequestBody(gradeIds)
        return apiService.getAllSubjectsForGrades(input)
    }
}