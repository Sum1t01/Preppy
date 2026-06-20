package com.sum1t.preppy.domain.repository.grade

import com.sum1t.preppy.common.utils.ApiResponse
import com.sum1t.preppy.domain.model.Grade

interface GradesRepository {
    suspend fun fetchGrades(): Result<ApiResponse<List<Grade>>>

    suspend fun saveGrades(grades: List<Grade>)
}




