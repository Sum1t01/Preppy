package com.example.TestPrep.DTO.topic

import com.example.TestPrep.DTO.subject.SubjectDTO
import com.example.TestPrep.model.grade.Grade
import com.example.TestPrep.model.subject.Subject
import com.example.TestPrep.model.topic.Topic


data class TopicDTO(
    val id: Long? = null,    // Optional if already exists
    val name: String
)

fun TopicDTO.toTopic(subject: Subject): Topic {
    val topic = Topic(
        id = this.id ?: 0,
        name = this.name,
        subject = subject
    )
    return topic
}