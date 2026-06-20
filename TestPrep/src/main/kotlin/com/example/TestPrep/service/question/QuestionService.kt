package com.example.TestPrep.service.question

import com.example.TestPrep.DTO.grade.GradeDTO
import com.example.TestPrep.DTO.grade.toGrade
import com.example.TestPrep.DTO.question.QuestionDTO
import com.example.TestPrep.DTO.question.toQuestion
import com.example.TestPrep.DTO.request.QuestionFilterRequest
import com.example.TestPrep.DTO.request.QuestionResponse
import com.example.TestPrep.DTO.request.toResponse
import com.example.TestPrep.DTO.subject.SubjectDTO
import com.example.TestPrep.DTO.subject.toSubject
import com.example.TestPrep.DTO.topic.TopicDTO
import com.example.TestPrep.DTO.topic.toTopic
import com.example.TestPrep.model.grade.Grade
import com.example.TestPrep.model.question.Question
import com.example.TestPrep.model.subject.Subject
import com.example.TestPrep.model.topic.Topic
import com.example.TestPrep.repository.grade.GradeRepository
import com.example.TestPrep.repository.question.QuestionRepository
import com.example.TestPrep.repository.subject.SubjectRepository
import com.example.TestPrep.repository.topic.TopicRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val gradeRepository: GradeRepository,
    private val subjectRepository: SubjectRepository,
    private val topicRepository: TopicRepository
) {
    fun getAllQuestions(): List<Question> {
        return questionRepository.findAll()
    }

    @Transactional
    fun addBulkQuestions(dtos: List<QuestionDTO>): List<Long> {
        val questions = dtos.map { dto ->
            val grade = getOrCreateGrade(dto.grade)
            val subject = getOrCreateSubject(dto.subject, grade)
            val topic = getOrCreateTopic(dto.topic, subject)
            dto.toQuestion(topic)
        }
        val savedItems = questionRepository.saveAll(questions)
        return savedItems.map { it.id }
    }

    private fun getOrCreateGrade(dto: GradeDTO): Grade {
        return dto.id?.let {
            gradeRepository.findById(it).orElse(null)
        } ?: gradeRepository.findByName(dto.name) ?: gradeRepository.save(dto.toGrade())
    }

    private fun getOrCreateSubject(dto: SubjectDTO, grade: Grade): Subject {
        return dto.id?.let {
            subjectRepository.findById(it).orElse(null)
        } ?: subjectRepository.findByNameAndGrade(dto.name, grade) ?: subjectRepository.save(dto.toSubject(grade))
    }

    private fun getOrCreateTopic(dto: TopicDTO, subject: Subject): Topic {
        return dto.id?.let {
            topicRepository.findById(it).orElse(null)
        } ?: topicRepository.findByName(dto.name)
        ?: topicRepository.save(
            dto.toTopic(subject)
        )
    }

    fun filterQuestions(request: QuestionFilterRequest): List<QuestionResponse> {

        if (request.topicIds.isEmpty()) return emptyList()

        val questions = questionRepository.findByTopicIdsWithRelations(request.topicIds)

        return questions.map { it.toResponse() }
    }

}
