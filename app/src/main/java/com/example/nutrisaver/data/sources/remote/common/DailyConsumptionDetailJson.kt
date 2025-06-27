package com.example.nutrisaver.data.sources.remote.common

import com.example.nutrisaver.data.model.DailyConsumptionDetail
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DailyConsumptionDetailJson(
    @Json(name = "daily_consumption_id") val id: Int? = null,
    @Json(name = "time") val mealType: String, // 'time' dari server, 'mealType' di app
    @Json(name = "food_name") val foodName: String, // 'food_name' dari server
    @Json(name = "quantity") val quantity: Float,
    @Json(name = "unit") val unit: String,
    @Json(name = "calories") val calories: Float,
    @Json(name = "carbs") val carbs: Float,
    @Json(name = "protein") val protein: Float,
    @Json(name = "fat") val fat: Float
){
    fun toDetail(): DailyConsumptionDetail {
        return DailyConsumptionDetail(
            id = this.id,
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