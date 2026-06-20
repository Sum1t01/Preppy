package com.example.TestPrep.repository.grade

import com.example.TestPrep.model.grade.Grade
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GradeRepository: JpaRepository<Grade, Long> {
    fun findByName(name: String): Grade?
}