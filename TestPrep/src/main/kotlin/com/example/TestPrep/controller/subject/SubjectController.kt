package com.example.TestPrep.controller.subject

import com.example.TestPrep.DTO.GradeSubjectsDTO
import com.example.TestPrep.DTO.response.SubjectResponseDTO
import com.example.TestPrep.DTO.response.toSubjectResponseDTO
import com.example.TestPrep.DTO.subject.SubjectRequestDTO
import com.example.TestPrep.model.response.ApiResponse
import com.example.TestPrep.model.response.ApiResponseBuilder
import com.example.TestPrep.repository.subject.SubjectRepository
import com.example.TestPrep.service.subject.SubjectService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/subjects")
class SubjectController(
    private val subjectService: SubjectService
) {

    @PostMapping("/all")
    fun getAllSubjectsForGradeId(
        @RequestBody
        request: SubjectRequestDTO
    ): ResponseEntity<ApiResponse<List<GradeSubjectsDTO>>> {
        val response = subjectService
            .findSubjectsByGrades(request.gradeIds)
        return ApiResponseBuilder.success(
            data = response
        )
    }

}