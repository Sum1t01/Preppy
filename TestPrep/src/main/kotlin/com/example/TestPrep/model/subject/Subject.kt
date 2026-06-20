package com.example.TestPrep.model.subject

import com.example.TestPrep.model.grade.Grade
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "subjects")
data class Subject(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "name", nullable = false)
    val name: String,  // e.g., "Physics"

    @ManyToOne
    @JoinColumn(name = "grade_id")
    val grade: Grade? = null,

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    constructor(): this (
        name = ""
    )
}