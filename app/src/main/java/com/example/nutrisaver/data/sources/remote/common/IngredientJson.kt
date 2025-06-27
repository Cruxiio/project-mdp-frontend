package com.example.nutrisaver.data.sources.remote.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IngredientJson(
    @Json(name = "id") val id: Int?,
    @Json(name = "ingredient_id") val ingredientApiId: Int?, // ID dari API eksternal jika ada
    @Json(name = "name") val name: String?,
    @Json(name = "image") val image: String?
)

@JsonClass(generateAdapter = true)
data class IngredientGetAllResponse(
    // Properti ini harus cocok dengan key "ingredients" dari JSON backend Anda
    @Json(name = "ingredients") val ingredients: List<IngredientJson>?
)