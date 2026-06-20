package com.example.TestPrep.DTO.subject

import com.example.TestPrep.DTO.grade.GradeDTO
import com.example.TestPrep.model.grade.Grade
import com.example.TestPrep.model.subject.Subject
import jakarta.validation.constraints.NotEmpty

data class SubjectDTO(
    val id: Long? = null,    // Optional if already exists
    val name: String
)

fun SubjectDTO.toSubject(grade:Grade): Subject {
    val subject = Subject(
        id = this.id ?: 0,
        name = this.name,
        grade = grade
    )
    return subject
}

data class SubjectRequestDTO(
    @field:NotEmpty(message = "gradeIds cannot be empty")
    val gradeIds: List<Long>
)