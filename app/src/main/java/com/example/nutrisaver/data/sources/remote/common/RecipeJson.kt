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

// ini raw json dari recipe favorite
@JsonClass(generateAdapter = true)
data class RecipeFavoriteJson(
    @Json(name = "id") val id: Int,
    @Json(name = "recipe_id") val recipeId: Int,
    @Json(name = "title") val title: String,
    @Json(name = "image") val image: String,
    @Json(name = "calories") val calories: Double,
    @Json(name = "protein") val protein: Double,
    @Json(name = "fat") val fat: Double,
    @Json(name = "carbs") val carbs: Double,
)

@JsonClass(generateAdapter = true)
data class RecipeFavoriteGetAllResponse(
    @Json(name = "recipes") var recipes: List<RecipeFavoriteJson>?,
){
}

@JsonClass(generateAdapter = true)
data class isRecipeFavoriteExistJson(
    @Json(name = "exist") var exist: Boolean,
){
}

@JsonClass(generateAdapter = true)
data class RequestBodyRecipeFavorite(
    @Json(name = "recipe_id") val recipeId: Int,
    @Json(name = "title") val title: String,
    @Json(name = "image") val image: String,
    @Json(name = "calories") val calories: Double,
    @Json(name = "protein") val protein: Double,
    @Json(name = "fat") val fat: Double,
    @Json(name = "carbs") val carbs: Double,
)

