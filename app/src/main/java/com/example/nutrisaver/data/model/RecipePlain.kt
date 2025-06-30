package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.common.RecipePlainJson

data class RecipePlain(
    val recipeId: Int,
    val title: String,
    val image: String,
) {
    companion object {
        fun fromJson(json: RecipePlainJson): RecipePlain? {

            return RecipePlain(
                recipeId = json.recipeId,
                title = json.title,
                image = json.image,
            )
        }
    }

    fun toRecipePlainJson(): RecipePlainJson {
        return RecipePlainJson(
            recipeId = this.recipeId,
            title = this.title,
            image = this.image,
        )
    }
}