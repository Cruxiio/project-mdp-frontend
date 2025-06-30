package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.common.RecipeFavoriteJson
import com.example.nutrisaver.data.sources.remote.common.RequestBodyRecipeFavorite
import com.example.nutrisaver.data.sources.remote.common.isRecipeFavoriteExistJson

data class isRecipeFavoriteExist(
    val exist: Boolean
){
    companion object{
        fun fromJson(json: isRecipeFavoriteExistJson): isRecipeFavoriteExist{
            return isRecipeFavoriteExist(
                exist = json.exist
            )
        }
    }
}

data class RecipeFavorite(
    val id: Int,
    val recipeId: Int,
    val title: String,
    val image: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double,
) {
    companion object{
        fun fromJson(json: RecipeFavoriteJson): RecipeFavorite {
            return RecipeFavorite(
                id = json.id,
                recipeId = json.recipeId,
                title = json.title,
                image = json.image,
                calories = json.calories,
                protein = json.protein,
                fat = json.fat,
                carbs = json.carbs,
            )
        }
    }

    fun toCreateRequest(): RequestBodyRecipeFavorite{
        return RequestBodyRecipeFavorite(
            recipeId = this.recipeId,
            title = this.title,
            image = this.image,
            calories = this.calories,
            protein = this.protein,
            carbs = this.carbs,
            fat = this.fat,
        )
    }
}