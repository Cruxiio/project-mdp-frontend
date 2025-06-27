package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.common.DailyConsumptionJson

data class DailyConsumption(
    val id: Int?,
    val userId: Int,
    val totalCalories: Float,
    val totalProtein: Float,
    val totalFat: Float,
    val totalCarbs: Float,
    val totalWater: Float,
    val targetCalories: Float,
    val targetProtein: Float,
    val targetFat: Float,
    val targetCarbs: Float,
    val targetWater: Float,
    val breakfastCalories: Float,
    val breakfastProteinGrams: Float,
    val breakfastCarbsGrams: Float,
    val breakfastFatGrams: Float,
    val lunchCalories: Float,
    val lunchProteinGrams: Float,
    val lunchCarbsGrams: Float,
    val lunchFatGrams: Float,
    val dinnerCalories: Float,
    val dinnerProteinGrams: Float,
    val dinnerCarbsGrams: Float,
    val dinnerFatGrams: Float,
    val date: String
) {
    companion object {
        fun fromDailyConsumptionJson(json: DailyConsumptionJson): DailyConsumption? {
            // Validasi semua field yang diperlukan
            if (json.userId == null || json.date == null) return null
            return DailyConsumption(
                id = json.id,
                userId = json.userId,
                totalCalories = json.totalCalories ?: 0f,
                totalProtein = json.totalProtein ?: 0f,
                totalFat = json.totalFat ?: 0f,
                totalCarbs = json.totalCarbs ?: 0f,
                totalWater = json.totalWater ?: 0f,
                targetCalories = json.targetCalories ?: 2000f,
                targetProtein = json.targetProtein ?: 100f,
                targetFat = json.targetFat ?: 70f,
                targetCarbs = json.targetCarbs ?: 250f,
                targetWater = json.targetWater ?: 2000f,
                breakfastCalories = json.breakfastCalories ?: 0f,
                breakfastProteinGrams = json.breakfastProtein ?: 0f,
                breakfastCarbsGrams = json.breakfastCarbs ?: 0f,
                breakfastFatGrams = json.breakfastFat ?: 0f,
                lunchCalories = json.lunchCalories ?: 0f,
                lunchProteinGrams = json.lunchProtein ?: 0f,
                lunchCarbsGrams = json.lunchCarbs ?: 0f,
                lunchFatGrams = json.lunchFat ?: 0f,
                dinnerCalories = json.dinnerCalories ?: 0f,
                dinnerProteinGrams = json.dinnerProtein ?: 0f,
                dinnerCarbsGrams = json.dinnerCarbs ?: 0f,
                dinnerFatGrams = json.dinnerFat?: 0f,
                date = json.date
            )
        }
    }
}