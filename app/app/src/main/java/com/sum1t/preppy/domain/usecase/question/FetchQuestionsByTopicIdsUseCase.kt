package com.sum1t.preppy.domain.usecase.question

import com.sum1t.data.sqldelight.Options
import com.sum1t.data.sqldelight.Questions
import com.sum1t.preppy.common.utils.NetworkResult
import com.sum1t.preppy.domain.model.Grade
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import okhttp3.Request

interface FetchQuestionsByTopicIdsUseCase {
    fun invoke(request: GetAllQuestionsRequestBody): Flow<NetworkResult<Boolean>>
}


@Serializable
data class GetAllQuestionsRequestBody(
    val topicIds: List<Long>
)

@Serializable
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

@Serializable

data class OptionResponse(
    val id: Long,
    val text: String,
    val isCorrect: Boolean
)

@Serializable

data class TopicResponse(
    val id: Long,
    val name: String,
    val subject: SubjectResponse?
)

@Serializable
data class SubjectResponse(
    val id: Long,
    val name: String,
    val grade: GradeResponse?
)

@Serializable
data class GradeResponse(
    val id: Long,
    val name: String
)

enum class QuestionType {
    SINGLE,     // single correct answer
    MULTIPLE,   // multiple correct answers
    INTEGER     // numeric answer
}

fun String.toQuestionType(): QuestionType {
    return when (this) {
        "SINGLE" -> QuestionType.SINGLE
        "MULTIPLE" -> QuestionType.MULTIPLE
        "INTEGER" -> QuestionType.INTEGER
        else -> throw IllegalArgumentException("Invalid question type: $this")
    }
}

enum class QuestionTier {
    FREE,
    PREMIUM,
    ULTRA_PREMIUM
}

fun String.toQuestionTier(): QuestionTier {
    return when (this) {
        "FREE" -> QuestionTier.FREE
        "PREMIUM" -> QuestionTier.PREMIUM
        "ULTRA_PREMIUM" -> QuestionTier.ULTRA_PREMIUM
        else -> throw IllegalArgumentException("Invalid question tier: $this")

    }
}

fun QuestionResponse.toDbModel(
    createdAt: Long = System.currentTimeMillis(),
    updatedAt: Long = System.currentTimeMillis(),
    isDeleted: Long = 0L
): Questions {
    return Questions(
        id = id,
        question_text = questionText,
        explanation = explanation,
        question_type = questionType.name,          // stored as TEXT
        difficulty_level = difficultyLevel,
        question_tier = questionTier.name,           // stored as TEXT
        marks = marks,
        negative_marks = negativeMarks,
        created_at = createdAt,
        updated_at = updatedAt,
        is_deleted = isDeleted,
        topic_id = topic?.id,
        attempted = 0,
        question_id = id
    )
}

fun OptionResponse.toDbModel(
    questionId: Long,
    order: Long
): Options {
    return Options(
        id = id,
        option_text = text,
        is_correct = if (isCorrect) 1L else 0L,   // BOOLEAN -> INTEGER
        option_order = order,
        question_id = questionId
    )
}



