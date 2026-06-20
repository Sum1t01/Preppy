package com.sum1t.preppy.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Grade (
    val name: String,
    val description: String? = null,
    val id: Long = 0
)


@Serializable
data class Subject (
    val id: Long = 0,
    val name: String
)

@Serializable
data class GradeSubjectsDTO(
    val gradeId: Long,
    val gradeName: String,
    val subjects: List<Subject>
)