package com.example.TestPrep.model.question

import com.example.TestPrep.model.option.Option
import com.example.TestPrep.model.topic.Topic
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "questions")
data class Question(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    val questionText: String,

    @Column(name = "explanation", columnDefinition = "TEXT")
    val explanation: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type")
    val questionType: QuestionType = QuestionType.SINGLE, // SINGLE/MULTIPLE/INTEGER

    @Column(name = "difficulty_level")
    val difficultyLevel: String? = null,   // e.g., "Easy", "Medium", "Hard"

    @Enumerated(EnumType.STRING)
    @Column(name = "question_tier")
    val questionTier: QuestionTier = QuestionTier.FREE,        // free or premium

    val marks: Double = 4.0,

    @Column(name = "negative_marks")
    val negativeMarks: Double = 1.0,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "topic_id")
    val topic: Topic ? = null, // make this not nullable later

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    var options: MutableList<Option> = mutableListOf()
) {
    constructor() : this(
        questionText = ""
    )
}

enum class QuestionType {
    SINGLE,     // single correct answer
    MULTIPLE,   // multiple correct answers
    INTEGER     // numeric answer
}

enum class QuestionTier {
    FREE,
    PREMIUM,
    ULTRA_PREMIUM
}