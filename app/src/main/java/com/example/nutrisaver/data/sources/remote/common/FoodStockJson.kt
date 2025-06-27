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

@JsonClass(generateAdapter = true)
data class FoodStockResponseItemJson(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "image") val imageUrl: String?,
    @Json(name = "quantity") val quantity: String, // <<-- KUNCI: Didefinisikan sebagai String
    @Json(name = "unit") val unit: String,
    @Json(name = "expired_date") val expiredDate: String?, // <<-- KUNCI: Didefinisikan sebagai String
    @Json(name = "start_remind_date") val startRemindDate: String?
)

// Class ini mewakili struktur JSON lengkap dari respons endpoint "new"
@JsonClass(generateAdapter = true)
data class AddFoodStockResponseJson(
    @Json(name = "message") val message: String,
    @Json(name = "foodStock") val foodStock: FoodStockResponseItemJson
)

@JsonClass(generateAdapter = true)
data class UpdateFoodStockRequestJson(
    @Json(name = "quantity") val quantity: Float
)

// Responsnya tetap sama seperti Add, jadi kita bisa pakai lagi
@JsonClass(generateAdapter = true)
data class UpdateFoodStockResponseJson(
    @Json(name = "message") val message: String?,
    @Json(name = "foodStock") val foodStock: FoodStockJson?
)
