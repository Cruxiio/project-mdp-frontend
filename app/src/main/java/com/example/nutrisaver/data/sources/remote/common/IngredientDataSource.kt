package com.example.nutrisaver.data.sources.remote.common

import android.util.Log
import com.example.nutrisaver.data.model.Ingredient
import com.example.nutrisaver.data.sources.remote.Webservice

interface IngredientDataSource {
    suspend fun getIngredients(token: String): List<Ingredient>
}

class IngredientDataSourceImpl(
    private val webservice: Webservice
) : IngredientDataSource {
    override suspend fun getIngredients(token: String): List<Ingredient> {
        try {
            val formattedToken = "Bearer $token"
            // 1. Panggil webservice, hasilnya sekarang adalah objek wrapper
            val responseWrapper = webservice.getAllIngredients(formattedToken)

            // 2. "Buka bungkusnya" untuk mendapatkan list.
            //    Gunakan elvis operator (?:) untuk keamanan jika 'ingredients' null.
            val ingredientJsonList = responseWrapper.ingredients ?: emptyList()

            Log.d("IngredientDataSource", "Successfully unwrapped ${ingredientJsonList.size} ingredients.")

            // 3. Sisa mapping tetap sama
            return ingredientJsonList.mapNotNull { Ingredient.fromIngredientJson(it) }
        } catch (e: Exception) {
            Log.e("IngredientDataSource", "Failed to fetch ingredients from remote", e)
            throw e
        }
    }
}