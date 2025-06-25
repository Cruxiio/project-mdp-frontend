package com.example.nutrisaver.data.sources.remote.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// 1. Untuk request body saat MENAMBAH stok baru
@JsonClass(generateAdapter = true)
data class NewFoodStockRequestJson(
    @Json(name = "ingredient_id") val ingredientId: Int,
    @Json(name = "quantity") val quantity: Float,
    @Json(name = "unit") val unit: String,
    @Json(name = "expired_date") val expiredDate: String?,
    @Json(name = "start_remind_date") val startRemindDate: String?
)

// 2. Untuk objek 'foodStock' di dalam respons JSON
@JsonClass(generateAdapter = true)
data class FoodStockJson(
    @Json(name = "id") val id: Int?,
    @Json(name = "user_id") val userId: Int?,
    @Json(name = "ingredient_id") val ingredientId: Int?,
    @Json(name = "name") val name: String?,
    @Json(name = "image") val image: String?,
    @Json(name = "quantity") val quantity: Float?,
    @Json(name = "unit") val unit: String?,
    @Json(name = "expired_date") val expiredDate: String?,
    @Json(name = "start_remind_date") val startRemindDate: String?
)

// 3. Untuk menampung seluruh respons dari API addFoodStock
@JsonClass(generateAdapter = true)
data class AddFoodStockResponseJson(
    @Json(name = "message") val message: String?,
    @Json(name = "foodStock") val foodStock: FoodStockJson?
)