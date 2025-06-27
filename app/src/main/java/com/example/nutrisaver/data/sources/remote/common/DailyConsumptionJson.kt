package com.example.nutrisaver.data.sources.remote.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DailyConsumptionJson(
    @Json(name = "id") val id: Int?,
    @Json(name = "user_id") val userId: Int?,
    @Json(name = "total_calories") val totalCalories: Float?,
    @Json(name = "total_protein") val totalProtein: Float?,
    @Json(name = "total_fat") val totalFat: Float?,
    @Json(name = "total_carbs") val totalCarbs: Float?,
    @Json(name = "total_water_ml") val totalWater: Float?,
    @Json(name = "target_calories") val targetCalories: Float?,
    @Json(name = "target_protein_grams") val targetProtein: Float?,
    @Json(name = "target_fat_grams") val targetFat: Float?,
    @Json(name = "target_carbs_grams") val targetCarbs: Float?,
    @Json(name = "target_water_ml") val targetWater: Float?,
    @Json(name = "breakfast_calories") val breakfastCalories: Float?,
    @Json(name = "breakfast_protein") val breakfastProtein: Float?,
    @Json(name = "breakfast_carbs") val breakfastCarbs: Float?,
    @Json(name = "breakfast_fat") val breakfastFat: Float?,
    @Json(name = "lunch_calories") val lunchCalories: Float?,
    @Json(name = "lunch_protein") val lunchProtein: Float?,
    @Json(name = "lunch_carbs") val lunchCarbs: Float?,
    @Json(name = "lunch_fat") val lunchFat: Float?,
    @Json(name = "dinner_calories") val dinnerCalories: Float?,
    @Json(name = "dinner_protein") val dinnerProtein: Float?,
    @Json(name = "dinner_carbs") val dinnerCarbs: Float?,
    @Json(name = "dinner_fat") val dinnerFat: Float?,
    @Json(name = "createdAt") val createdAt: String?,
    @Json(name = "details") val details: List<DailyConsumptionDetailJson>?
)