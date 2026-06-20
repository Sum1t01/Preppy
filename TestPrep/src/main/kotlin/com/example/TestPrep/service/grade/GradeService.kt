package com.example.TestPrep.service.grade

import com.example.TestPrep.model.grade.Grade
import com.example.TestPrep.repository.grade.GradeRepository
import org.springframework.stereotype.Service

@Service
class GradeService(
    private val gradeRepository: GradeRepository
) {
    fun getAllGrades(): List<Grade>{
        return gradeRepository.findAll()
    }
}