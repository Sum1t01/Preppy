package com.example.TestPrep.controller.questions

import com.example.TestPrep.DTO.request.BulkQuestionRequest
import com.example.TestPrep.DTO.request.QuestionFilterRequest
import com.example.TestPrep.DTO.request.QuestionResponse
import com.example.TestPrep.model.question.Question
import com.example.TestPrep.model.response.ApiResponse
import com.example.TestPrep.model.response.ApiResponseBuilder
import com.example.TestPrep.service.question.QuestionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/questions")
private class QuestionController(
    private val questionService: QuestionService
) {
    @GetMapping("/all")
    fun getAllQuestions(): ResponseEntity<ApiResponse<List<Question>>> {
        val body = questionService.getAllQuestions()
        return ApiResponseBuilder.success(
            data = body,
            message = "Successfully fetched all questions",
            status = HttpStatus.OK,
        )
    }

    @PostMapping("/bulk")
    fun addQuestionsBulk(
        @RequestBody
        bulkQuestionRequest: BulkQuestionRequest
    ): ResponseEntity<ApiResponse<List<Long>>> {
        val response = questionService.addBulkQuestions(bulkQuestionRequest.questions)
        return ApiResponseBuilder.success(
            data = response
        )
    }

    @GetMapping("/test")
    fun test(): ResponseEntity<ApiResponse<String>> {
        return ApiResponseBuilder.success(
            data = "hello from server",
        )
    }

    @PostMapping("/filter")
    fun filterQuestions(
        @RequestBody request: QuestionFilterRequest
    ): ResponseEntity<ApiResponse<List<QuestionResponse>>> {

        val data = questionService.filterQuestions(request)

        return ApiResponseBuilder.success(
            data = data,
            message = "Questions fetched successfully",
            status = HttpStatus.OK
        )
    }
}