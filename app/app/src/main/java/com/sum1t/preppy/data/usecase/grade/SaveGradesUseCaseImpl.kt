package com.sum1t.preppy.data.usecase.grade

import com.sum1t.preppy.domain.model.Grade
import com.sum1t.preppy.domain.repository.grade.GradesRepository
import com.sum1t.preppy.domain.usecase.grade.SaveGradesUseCase

class SaveGradesUseCaseImpl(
    private val gradesRepository: GradesRepository
): SaveGradesUseCase {
    override suspend fun invoke(grades: List<Grade>) {
        gradesRepository.saveGrades(grades)
    }
}