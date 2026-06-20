package com.example.TestPrep.repository.subject

import com.example.TestPrep.model.grade.Grade
import com.example.TestPrep.model.subject.Subject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SubjectRepository: JpaRepository<Subject, Long> {
    fun findByName(name: String): Subject?
    fun findByGrade(grade: Grade): List<Subject>
    fun findByNameAndGrade(name: String, grade: Grade): Subject?

    @Query("SELECT s FROM Subject s WHERE s.grade.id IN :gradeIds")
    fun findByGradeIds(@Param("gradeIds") gradeIds: List<Long>): List<Subject>

}