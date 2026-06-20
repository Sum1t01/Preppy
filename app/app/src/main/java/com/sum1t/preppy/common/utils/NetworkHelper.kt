package com.sum1t.preppy.common.utils

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

suspend inline fun <reified outpuT> HttpClient.getRequest(
    url: String,
    headers: Map<String, Any>? = null,
    query: Map<String, Any>? = null
): Result<outpuT?> {
    return try {
        val response = this.get(url) {
            headers?.forEach {
                header(it.key, it.value)
            }
            query?.forEach {
                parameter(it.key, it.value)
            }
        }
        Log.d("NetworkResult : ", "${response.body<outpuT>()}")
        Result.success(response.body<outpuT>())
    } catch (e: Exception) {
        return Result.failure(e)
    }
}

suspend inline fun <reified requesT, reified outpuT> HttpClient.postRequest(
    url: String,
    body: requesT? = null,
    query: Map<String, Any>? = null,
    headers: Map<String, Any>? = null
): Result<outpuT> {
    return try {
        val response = this.post(url) {
            // Set Content-Type for JSON serialization
            contentType(ContentType.Application.Json)

            headers?.forEach {
                header(it.key, it.value)
            }
            query?.forEach {
                parameter(it.key, it.value)
            }
            body?.let {
                setBody(it)
            }
        }
        Log.d("NetworkResult : ", "${response.body<outpuT>()}")
        Result.success(response.body<outpuT>())
    } catch (e: Exception) {
        Result.failure(e)
    }
}

suspend inline fun<reified requesT, reified outpuT> HttpClient.deleteRequest(
    url: String,
    body: requesT? = null,
    query: Map<String, Any>? = null,
    headers: Map<String, Any>? = null
): Result<outpuT> {
    return try {
        val response = this.delete(url) {
            // Set Content-Type for JSON serialization
            contentType(ContentType.Application.Json)

            headers?.forEach {
                header(it.key, it.value)
            }
            query?.forEach {
                parameter(it.key, it.value)
            }
            body?.let {
                setBody(it)
            }
        }
        Log.d("NetworkResult : ", "${response.body<outpuT>()}")
        Result.success(response.body<outpuT>())
    } catch (e: Exception) {
        Result.failure(e)
    }
}