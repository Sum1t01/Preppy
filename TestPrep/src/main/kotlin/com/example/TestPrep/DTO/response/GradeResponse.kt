package com.example.TestPrep.DTO.response

import com.example.TestPrep.model.grade.Grade
import org.springframework.context.annotation.Description

data class GradeResponseDTO(
    val name: String,
    val description: String? = null,
    val id: Long = 0
)

fun Grade.toGradeResponseDTO(): GradeResponseDTO {
    val gradeResponseDTO = GradeResponseDTO(
        name = this.name,
        description = this.description,
        id = this.id
    )
    return gradeResponseDTO
}

fun List<Grade>.toGradeResponseDTO(): List<GradeResponseDTO> {
    return this.map { it.toGradeResponseDTO() }
}