package com.sum1t.preppy.common.utils

import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class ApiResponse<T>(
//    val timestamp: Long = Timestamp.(),
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val error: String? = null
) {
    fun isSuccess() = success
}