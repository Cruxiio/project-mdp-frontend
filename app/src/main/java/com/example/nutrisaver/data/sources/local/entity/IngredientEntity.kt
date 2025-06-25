package com.example.nutrisaver.data.sources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutrisaver.data.model.Ingredient
import com.example.nutrisaver.data.sources.remote.common.IngredientJson

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey val id: Int,
    val ingredient_id: Int,
    val name: String,
    val imageUrl: String?
) {
    fun toIngredients() = Ingredient(id, ingredient_id,  name, imageUrl)
    companion object {
        fun fromIngredients(domain: Ingredient) =
            IngredientEntity(domain.id, domain.ingredient_id,
                domain.name, domain.imageUrl)
    }
}