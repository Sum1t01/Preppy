package com.example.TestPrep.DTO.grade

import com.example.TestPrep.model.grade.Grade

data class GradeDTO(
    val id: Long? = null,    // If exists, can pass ID
    val name: String          // Otherwise backend will create
)

fun GradeDTO.toGrade(): Grade {
    val grade = Grade(
        id = this.id ?: 0,
        name = this.name
    )
    return grade
}