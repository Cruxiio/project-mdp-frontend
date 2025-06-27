package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.common.DailyConsumptionDetailJson

data class DailyConsumptionDetail(
    val id : Int?,
    val mealType: String,
    val foodName: String,
    val quantity: Float,
    val unit: String,
    val calories: Float,
    val carbs: Float,
    val protein: Float,
    val fat: Float
) {
    // Fungsi helper untuk mengubah model domain menjadi model JSON (DTO)
    // yang akan dikirim ke API
    fun toRequestJson(): DailyConsumptionDetailJson {
        return DailyConsumptionDetailJson(
            mealType = this.mealType,
            foodName = this.foodName,
            quantity = this.quantity,
            unit = this.unit,
            calories = this.calories,
            carbs = this.carbs,
            protein = this.protein,
            fat = this.fat
        )
    }
}