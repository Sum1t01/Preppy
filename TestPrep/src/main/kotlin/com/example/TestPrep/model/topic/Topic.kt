package com.example.TestPrep.model.topic

import com.example.TestPrep.model.subject.Subject
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "topics")
data class Topic(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "name", nullable = false)
    val name: String,  // e.g., "Mechanics"

    @ManyToOne
    @JoinColumn(name = "subject_id")
    val subject: Subject? = null,

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
){
    constructor(): this(
        name = ""
    )
}