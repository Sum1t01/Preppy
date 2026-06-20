package com.sum1t.preppy.domain.usecase.userpreferences

interface SetThemePaletteUseCase {
    suspend fun invoke(palette: String)
}