package com.example.TestPrep.model.option

import com.example.TestPrep.model.question.Question
import jakarta.persistence.*

@Entity
@Table(name = "options")
data class Option(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "option_text")
    val optionText: String = "",

    @Column(name = "is_correct")
    val isCorrect: Boolean = false,

    @Column(name = "option_order")
    val optionOrder: Int = 0,

    @ManyToOne
    @JoinColumn(name = "question_id")
    val question: Question? = null
) {
    constructor() : this(
        optionText = ""
    )
}