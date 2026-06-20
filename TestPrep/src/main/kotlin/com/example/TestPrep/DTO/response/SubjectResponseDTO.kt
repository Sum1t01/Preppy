package com.example.TestPrep.DTO.response

import com.example.TestPrep.model.subject.Subject

data class SubjectResponseDTO(
    val id: Long = 0,
    val name: String,
)

fun Subject.toSubjectResponseDTO(): SubjectResponseDTO {
    val subjectResponseDTO = SubjectResponseDTO(
        id = this.id,
        name = this.name
    )
    return subjectResponseDTO
}

fun List<Subject>.toSubjectResponseDTO(): List<SubjectResponseDTO> {
    return this.map { it.toSubjectResponseDTO() }
}