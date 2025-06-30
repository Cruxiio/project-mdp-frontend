package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.common.CustomIngredientJson
import com.example.nutrisaver.data.sources.remote.common.RecipeDetailJson

data class CustomIngredient(
    val original: String,
    val originalName: String,
    val name: String,
) {
    companion object {
        fun fromJson(json: CustomIngredientJson): CustomIngredient {
            return CustomIngredient(
                original = json.original,
                originalName = json.originalName,
                name = json.name
            )
        }
    }

    fun toCustomIngredientJson(): CustomIngredientJson {
        return CustomIngredientJson(
            original = this.original,
            originalName = this.originalName,
            name = this.name
        )
    }
}

data class RecipeDetail(
    val recipeId: Int,
    val title: String,
    val image: String,
    val timeToCook: Int,
    val servings: Int,
    val sourceUrl: String,
    val calories: Double,
    val fat: Double,
    val protein: Double,
    val carbs: Double,
    val tags: List<String>,
    val ingredients: List<CustomIngredient>,
//    val instruction: List<String>,
    val instruction: String
) {
    companion object{
        fun fromJson(json: RecipeDetailJson): RecipeDetail {
            return RecipeDetail(
                recipeId = json.recipeId,
                title = json.title,
                image = json.image,
                timeToCook =  json.timeToCook,
                servings = json.servings,
                sourceUrl = json.sourceUrl,
                calories = json.calories,
                fat = json.fat,
                protein = json.protein,
                carbs = json.carbs,
                tags = json.tags,
                ingredients = json.ingredients.map { CustomIngredient.fromJson(it) },
                instruction = json.instruction,
            )
        }

    }

    fun toRecipeDetailJson(): RecipeDetailJson {
        return RecipeDetailJson(
            recipeId = this.recipeId,
            title = this.title,
            image = this.image,
            timeToCook = this.timeToCook,
            servings = this.servings,
            sourceUrl = this.sourceUrl,
            calories = this.calories,
            fat = this.fat,
            protein = this.protein,
            carbs = this.carbs,
            tags = this.tags,
            ingredients = this.ingredients.map { it.toCustomIngredientJson() },
            instruction = this.instruction,
        )
    }
}