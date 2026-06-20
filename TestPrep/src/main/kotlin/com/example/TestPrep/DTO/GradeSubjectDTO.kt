package com.example.TestPrep.DTO

import com.example.TestPrep.DTO.subject.SubjectDTO

data class GradeSubjectsDTO(
    val gradeId: Long,
    val gradeName: String,
    val subjects: List<SubjectDTO>
)