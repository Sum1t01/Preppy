package com.example.TestPrep.DTO.request

import com.example.TestPrep.model.grade.Grade
import com.example.TestPrep.model.option.Option
import com.example.TestPrep.model.question.Question
import com.example.TestPrep.model.question.QuestionTier
import com.example.TestPrep.model.question.QuestionType
import com.example.TestPrep.model.subject.Subject
import com.example.TestPrep.model.topic.Topic
import com.fasterxml.jackson.annotation.JsonProperty

data class QuestionFilterRequest(
    val topicIds: List<Long> = emptyList()
)

data class QuestionResponse(
    val id: Long,
    val questionText: String,
    val explanation: String?,
    val questionType: QuestionType,
    val difficultyLevel: String?,
    val questionTier: QuestionTier,
    val marks: Double,
    val negativeMarks: Double,
    val topic: TopicResponse?,
    val options: List<OptionResponse>
)

data class OptionResponse(
    val id: Long,
    val text: String,
    @get:JsonProperty("isCorrect")
    val isCorrect: Boolean
)

data class TopicResponse(
    val id: Long,
    val name: String,
    val subject: SubjectResponse?
)

data class SubjectResponse(
    val id: Long,
    val name: String,
    val grade: GradeResponse?
)

data class GradeResponse(
    val id: Long,
    val name: String
)


fun Question.toResponse(): QuestionResponse {
    return QuestionResponse(
        id = id,
        questionText = questionText,
        explanation = explanation,
        questionType = questionType,
        difficultyLevel = difficultyLevel,
        questionTier = questionTier,
        marks = marks,
        negativeMarks = negativeMarks,
        topic = topic?.toResponse(),
        options = options.map { it.toResponse() }
    )
}

fun Option.toResponse(): OptionResponse {
    return OptionResponse(
        id = id,
        text = optionText,
        isCorrect = isCorrect
    )
}

fun Topic.toResponse(): TopicResponse {
    return TopicResponse(
        id = id,
        name = name,
        subject = subject?.toResponse()
    )
}

fun Subject.toResponse(): SubjectResponse {
    return SubjectResponse(
        id = id,
        name = name,
        grade = grade?.toResponse()
    )
}

fun Grade.toResponse(): GradeResponse {
    return GradeResponse(
        id = id,
        name = name
    )
}