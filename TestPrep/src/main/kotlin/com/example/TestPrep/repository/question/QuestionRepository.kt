package com.example.TestPrep.repository.question

import com.example.TestPrep.model.question.Question
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface QuestionRepository : JpaRepository<Question, Long> {
    @Query(
        """
    SELECT DISTINCT q FROM Question q
    LEFT JOIN FETCH q.options
    LEFT JOIN FETCH q.topic t
    LEFT JOIN FETCH t.subject s
    LEFT JOIN FETCH s.grade
    WHERE t.id IN :topicIds
"""
    )
    fun findByTopicIdsWithRelations(
        @Param("topicIds") topicIds: List<Long>
    ): List<Question>
}