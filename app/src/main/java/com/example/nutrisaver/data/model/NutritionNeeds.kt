package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.common.NutritionNeedsJson

data class NutritionNeeds(
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val water: Int
) {
    fun toNutritionNeedsJson(): NutritionNeedsJson {
        return NutritionNeedsJson(
            calories = this.calories,
            protein = this.protein,
            carbs = this.carbs,
            fat = this.fat,
            water = this.water
        )
    }

    companion object {
        /**
         * Mapper dari JSON model ke Domain model.
         * @param json Objek NutritionNeedsJson yang diterima dari API.
         * @return Objek NutritionNeeds jika semua properti pada json tidak null, jika tidak, kembalikan null.
         */
        fun fromNutritionNeedsJson(json: NutritionNeedsJson): NutritionNeeds? {
            // Pastikan semua field ada sebelum membuat objek domain.
            return if (json.calories != null && json.protein != null && json.carbs != null && json.fat != null && json.water != null) {
                NutritionNeeds(
                    calories = json.calories,
                    protein = json.protein,
                    carbs = json.carbs,
                    fat = json.fat,
                    water = json.water
                )
            } else {
                null
            }
        }
    }
}