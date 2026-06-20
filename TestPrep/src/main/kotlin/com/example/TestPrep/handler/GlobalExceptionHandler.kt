package com.example.TestPrep.handler

import com.example.TestPrep.model.response.ApiResponse
import com.example.TestPrep.model.response.ApiResponseBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(exception: IllegalArgumentException): ResponseEntity<ApiResponse<String>> {
        return ApiResponseBuilder.error(
            message = "Validation failed",
            error = exception.message ?: "Invalid input",
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(exception: java.lang.Exception): ResponseEntity<ApiResponse<String>> {
        return ApiResponseBuilder.error(
            message = "Something went wrong!",
            error = exception.message ?: "Something went wrong!",
            status = HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}