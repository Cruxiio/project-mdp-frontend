package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.common.RecipeJson

data class Recipe(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val calories: Float,
    val protein: Float,
    val fat: Float,
    val carbs: Float
) {
    companion object {
        fun fromJson(json: RecipeJson): Recipe? {
            val nutrients = json.nutrition?.nutrients
            if (json.id == null || json.title == null || nutrients == null) return null

            return Recipe(
                id = json.id,
                title = json.title,
                imageUrl = json.image ?: "",
                calories = nutrients.find { it.name == "Calories" }?.amount ?: 0f,
                protein = nutrients.find { it.name == "Protein" }?.amount ?: 0f,
                fat = nutrients.find { it.name == "Fat" }?.amount ?: 0f,
                carbs = nutrients.find { it.name == "Carbohydrates" }?.amount ?: 0f
            )
        }
    }
}