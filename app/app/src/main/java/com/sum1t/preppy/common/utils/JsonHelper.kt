package com.sum1t.preppy.common.utils

import kotlinx.serialization.json.Json

object JsonHelper {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = false
        coerceInputValues = true
    }
}