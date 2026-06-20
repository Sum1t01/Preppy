package com.example.TestPrep.DTO.option

data class OptionDTO(
    val optionText: String,
    val isCorrect: Boolean = false,
    val optionOrder: Int = 0
)