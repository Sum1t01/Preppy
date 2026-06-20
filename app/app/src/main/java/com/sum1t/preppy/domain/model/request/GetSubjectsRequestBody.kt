package com.sum1t.preppy.domain.model.request

import kotlinx.serialization.Serializable

@Serializable
data class GetSubjectsRequestBody(
    val gradeIds: List<Long>
)