package com.example.TestPrep.model.response

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

data class ApiResponse<T>(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val error: String? = null
)


object ApiResponseBuilder {

    fun <T> success(
        data: T?,
        message: String = "Success",
        status: HttpStatus = HttpStatus.OK
    ): ResponseEntity<ApiResponse<T>> {
        return ResponseEntity.status(status).body(
            ApiResponse(
                status = status.value(),
                success = true,
                message = message,
                data = data
            )
        )
    }

    fun <T> error(
        message: String,
        status: HttpStatus = HttpStatus.BAD_REQUEST,
        error: String? = null
    ): ResponseEntity<ApiResponse<T>> {
        return ResponseEntity.status(status).body(
            ApiResponse(
                status = status.value(),
                success = false,
                message = message,
                error = error
            )
        )
    }
}