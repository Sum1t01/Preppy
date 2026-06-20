package com.sum1t.preppy.data.db

import app.cash.sqldelight.ColumnAdapter
import com.sum1t.preppy.domain.usecase.question.QuestionTier
import com.sum1t.preppy.domain.usecase.question.QuestionType

val booleanAdapter = object : ColumnAdapter<Boolean, Long> {
    override fun decode(databaseValue: Long): Boolean = databaseValue == 1L
    override fun encode(value: Boolean): Long = if (value) 1L else 0L
}

val questionTypeAdapter = object : ColumnAdapter<QuestionType, String> {
    override fun decode(databaseValue: String): QuestionType =
        QuestionType.valueOf(databaseValue)

    override fun encode(value: QuestionType): String = value.name
}

val questionTierAdapter = object : ColumnAdapter<QuestionTier, String> {
    override fun decode(databaseValue: String): QuestionTier =
        QuestionTier.valueOf(databaseValue)

    override fun encode(value: QuestionTier): String = value.name
}