package com.example.TestPrep.service.subject

import com.example.TestPrep.DTO.GradeSubjectsDTO
import com.example.TestPrep.DTO.subject.SubjectDTO
import com.example.TestPrep.model.subject.Subject
import com.example.TestPrep.repository.grade.GradeRepository
import com.example.TestPrep.repository.subject.SubjectRepository
import org.springframework.stereotype.Service

@Service
class SubjectService(
    private val subjectRepository: SubjectRepository,
    private val gradeRepository: GradeRepository
) {
    fun findSubjectsByGrade(grade: Long): List<Subject> {
        val gradeEntity = gradeRepository.findById(grade).orElseThrow()
        return subjectRepository.findByGrade(gradeEntity)
    }

    fun findSubjectsByGrades(grades: List<Long>): List<GradeSubjectsDTO> {
        val subjects = subjectRepository.findByGradeIds(grades)
        return subjects
            .groupBy { it.grade }
            .map { (grade, subjectsList) ->
                GradeSubjectsDTO(
                    gradeId = grade?.id ?: 0,
                    gradeName = grade?.name ?: "",
                    subjects = subjectsList.map {
                        SubjectDTO(it.id, it.name)
                    }
                )
            }
    }
}