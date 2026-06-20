package com.example.TestPrep.DTO.question

import com.example.TestPrep.DTO.grade.GradeDTO
import com.example.TestPrep.DTO.option.OptionDTO
import com.example.TestPrep.DTO.subject.SubjectDTO
import com.example.TestPrep.DTO.topic.TopicDTO
import com.example.TestPrep.model.option.Option
import com.example.TestPrep.model.question.Question
import com.example.TestPrep.model.question.QuestionTier
import com.example.TestPrep.model.question.QuestionType
import com.example.TestPrep.model.topic.Topic

data class QuestionDTO(
    val grade: GradeDTO,
    val subject: SubjectDTO,
    val topic: TopicDTO,
    val questionText: String,
    val explanation: String? = null,
    val difficultyLevel: String? = null,      // e.g., "Easy", "Medium", "Hard"
    val questionType: QuestionType = QuestionType.SINGLE,
    val questionTier: QuestionTier = QuestionTier.FREE,
    val marks: Double = 4.0,
    val negativeMarks: Double = 1.0,
    val options: List<OptionDTO> = emptyList()
)


fun QuestionDTO.toQuestion(topic: Topic): Question {

    val question = Question(
        questionText = this.questionText,
        explanation = this.explanation,
        difficultyLevel = this.difficultyLevel,
        questionType = this.questionType,
        questionTier = this.questionTier,
        marks = this.marks,
        negativeMarks = this.negativeMarks,
        topic = topic,
        options = mutableListOf()
    )

    val options = this.options.map {
        Option(
            optionText = it.optionText,
            isCorrect = it.isCorrect,
            optionOrder = it.optionOrder,
            question = question
        )
    }

    question.options.addAll(options)

    return question
}