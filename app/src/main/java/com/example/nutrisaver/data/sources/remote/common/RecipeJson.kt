package com.example.nutrisaver.data.sources.remote.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ini buat data recipe + nutrisi
@JsonClass(generateAdapter = true)
data class RecipeJson(
    @Json(name = "id") val id: Int?,
    @Json(name = "title") val title: String?,
    @Json(name = "image") val image: String?,
    @Json(name = "nutrition") val nutrition: RecipeNutritionJson?
)

// ini raw data recipe
@JsonClass(generateAdapter = true)
data class RecipePlainJson(
    @Json(name = "recipe_id") val recipeId: Int,
    @Json(name = "title") val title: String,
    @Json(name = "image") val image: String,
)

@JsonClass(generateAdapter = true)
data class RecipePlainGetAllResponse(
    @Json(name = "recipes") var recipes: List<RecipePlainJson>?,
){
}

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