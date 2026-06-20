package com.example.TestPrep.controller.grade

import com.example.TestPrep.DTO.response.GradeResponseDTO
import com.example.TestPrep.DTO.response.toGradeResponseDTO
import com.example.TestPrep.model.response.ApiResponse
import com.example.TestPrep.model.response.ApiResponseBuilder
import com.example.TestPrep.service.grade.GradeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/grades")
class GradeController(
    private val gradeService: GradeService
) {

    @GetMapping("/all")
    fun findAllGrades(): ResponseEntity<ApiResponse<List<GradeResponseDTO>>> {
        val response = gradeService.getAllGrades().toGradeResponseDTO()
        return ApiResponseBuilder.success(
            data = response
        )
    }

}