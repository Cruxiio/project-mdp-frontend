package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.common.IngredientJson

data class Ingredient(
    val id: Int,
    val ingredient_id: Int,
    val name: String,
    val imageUrl: String?
) {
    companion object {
        fun fromIngredientJson(json: IngredientJson): Ingredient? {
            if (json.id == null || json.name == null || json.ingredientApiId == null) return null
            return Ingredient(
                id = json.id,
                ingredient_id = json.ingredientApiId,
                name = json.name,
                imageUrl = json.image
            )
        }
    }
}