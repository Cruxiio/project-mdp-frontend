package com.example.nutrisaver.data.sources.remote.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CustomIngredientJson(
    @Json(name = "original") val original: String,
    @Json(name = "originalName") val originalName: String,
    @Json(name = "name") val name: String,
){

}

@JsonClass(generateAdapter = true)
data class RecipeDetailJson(
    @Json(name = "recipe_id") val recipeId: Int,
    @Json(name = "title") val title: String,
    @Json(name = "image") val image: String,
    @Json(name = "time_to_cook") val timeToCook: Int,
    @Json(name = "servings") val servings: Int,
    @Json(name = "source_url") val sourceUrl: String,
    @Json(name = "calories") val calories: Double,
    @Json(name = "fat") val fat: Double,
    @Json(name = "protein") val protein: Double,
    @Json(name = "carbs") val carbs: Double,
    @Json(name = "tags") val tags: List<String>,
    @Json(name = "ingredients") val ingredients: List<CustomIngredientJson>,
//    @Json(name = "instruction") val instruction: List<String>,
    @Json(name = "instruction") val instruction:String
) {
}