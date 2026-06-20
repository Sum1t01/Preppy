package com.sum1t.preppy.data.repository.grade

import com.sum1t.data.sqldelight.AppDatabaseQueries
import com.sum1t.preppy.common.utils.ApiResponse
import com.sum1t.preppy.data.ApiService
import com.sum1t.preppy.domain.model.Grade
import com.sum1t.preppy.domain.repository.grade.GradesRepository

class GradesRepositoryImpl(
    private val apiService: ApiService,
    private val appDatabaseQueries: AppDatabaseQueries,
) : GradesRepository {
    override suspend fun fetchGrades(): Result<ApiResponse<List<Grade>>> {
        return apiService.getAllGrades()
    }

    override suspend fun saveGrades(grades: List<Grade>) {
        appDatabaseQueries.transaction {
            appDatabaseQueries.deleteAllGrades()
            grades.forEach { grade ->
                appDatabaseQueries.insertGrade(
                    grade_id = grade.id,
                    name = grade.name,
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis(),
                    description = "",
                )
            }
        }

    }


}