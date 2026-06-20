package com.example.TestPrep.model.grade

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "grades")
data class Grade(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "name", unique = true, nullable = false)
    val name: String,   // e.g., "Class 6"

    @Column(name = "description")
    val description: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    constructor() : this(
        name = ""
    )
}