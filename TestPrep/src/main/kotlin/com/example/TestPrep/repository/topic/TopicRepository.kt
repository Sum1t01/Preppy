package com.example.TestPrep.repository.topic

import com.example.TestPrep.model.topic.Topic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TopicRepository: JpaRepository<Topic, Long> {
    fun findByName(name: String): Topic?
}