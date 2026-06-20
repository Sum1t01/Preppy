package com.sum1t.preppy.data

import com.sum1t.preppy.common.utils.ApiResponse
import com.sum1t.preppy.common.utils.endpoints.GradesEndPoint
import com.sum1t.preppy.common.utils.endpoints.QuestionsEndPoint
import com.sum1t.preppy.common.utils.endpoints.SubjectsEndPoint
import com.sum1t.preppy.common.utils.getRequest
import com.sum1t.preppy.common.utils.postRequest
import com.sum1t.preppy.domain.model.Grade
import com.sum1t.preppy.domain.model.GradeSubjectsDTO
import com.sum1t.preppy.domain.model.Subject
import com.sum1t.preppy.domain.model.request.GetSubjectsRequestBody
import com.sum1t.preppy.domain.usecase.question.GetAllQuestionsRequestBody
import com.sum1t.preppy.domain.usecase.question.QuestionResponse
import io.ktor.client.HttpClient

class ApiService(
    private val client: HttpClient
) {
    suspend fun getAllGrades(): Result<ApiResponse<List<Grade>>> {
        return try {
            val responseResult = client.getRequest<ApiResponse<List<Grade>>?>(
                url = GradesEndPoint.AllGrades.route
            )
            responseResult?.getOrNull()?.let {
                Result.success(it)
            } ?: run {
                Result.failure(Exception("No data found"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getAllSubjectsForGrades(getSubjectsRequestBody: GetSubjectsRequestBody): Result<ApiResponse<List<GradeSubjectsDTO>>> {
        return try {
            val responseResult =
                client.postRequest<GetSubjectsRequestBody, ApiResponse<List<GradeSubjectsDTO>>?>(
                    url = SubjectsEndPoint.AllSubjects.route,
                    body = getSubjectsRequestBody
                )
            responseResult?.getOrNull()?.let {
                Result.success(it)
            } ?: run {
                Result.failure(Exception("No data found"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }


    suspend fun getAllQuestionsByTopicIds(request: GetAllQuestionsRequestBody): Result<ApiResponse<List<QuestionResponse>>> {
        return try {
            val responseResult =
                client.postRequest<GetAllQuestionsRequestBody, ApiResponse<List<QuestionResponse>>?>(
                    url = QuestionsEndPoint.AllQuestionsByTopicIds.route,
                    body = request
                )
            responseResult?.getOrNull()?.let {
                Result.success(it)
            } ?: run {
                Result.failure(Exception("No data found"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

}