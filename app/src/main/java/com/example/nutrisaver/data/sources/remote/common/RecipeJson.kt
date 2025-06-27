package com.example.nutrisaver.data.sources.remote.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecipeJson(
    @Json(name = "id") val id: Int?,
    @Json(name = "title") val title: String?,
    @Json(name = "image") val image: String?,
    @Json(name = "nutrition") val nutrition: RecipeNutritionJson?
)

@JsonClass(generateAdapter = true)
data class RecipeNutritionJson(
    @Json(name = "nutrients") val nutrients: List<NutrientJson>?
)

@JsonClass(generateAdapter = true)
data class NutrientJson(
    @Json(name = "name") val name: String?,
    @Json(name = "amount") val amount: Float?,
    @Json(name = "unit") val unit: String?
)

// Request body untuk dikirim ke backend kita
@JsonClass(generateAdapter = true)
data class RecipeSearchRequestJson(
    @Json(name = "query") val query: String
)