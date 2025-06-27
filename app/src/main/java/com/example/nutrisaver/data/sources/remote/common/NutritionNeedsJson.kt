package com.example.nutrisaver.data.sources.remote.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NutritionNeedsJson(
    @Json(name = "calories") val calories: Int?,
    @Json(name = "protein") val protein: Int?,
    @Json(name = "carbs") val carbs: Int?,
    @Json(name = "fat") val fat: Int?,
    @Json(name = "water") val water: Int?
)