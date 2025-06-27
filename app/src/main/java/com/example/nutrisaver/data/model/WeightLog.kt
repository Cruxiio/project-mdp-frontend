package com.example.nutrisaver.data.model

import java.time.LocalDate

data class WeightLog(
    val id: Int,
    val weight: Float,
    val unit: String,
    val date: LocalDate
)