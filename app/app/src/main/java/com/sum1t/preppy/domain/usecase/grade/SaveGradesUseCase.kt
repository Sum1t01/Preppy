package com.sum1t.preppy.domain.usecase.grade

import com.sum1t.preppy.domain.model.Grade

interface SaveGradesUseCase {
    suspend operator fun invoke(grades: List<Grade>)
}