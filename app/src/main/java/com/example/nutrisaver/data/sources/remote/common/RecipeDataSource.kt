package com.example.nutrisaver.data.sources.remote.common

import android.util.Log
import com.example.nutrisaver.data.model.Recipe
import com.example.nutrisaver.data.sources.remote.Webservice

interface RecipeDataSource {
    suspend fun searchRecipes(token: String, query: String): List<Recipe>
}

class RecipeDataSourceImpl(
    private val webservice: Webservice
) : RecipeDataSource {
    override suspend fun searchRecipes(token: String, query: String): List<Recipe> {
        try {
            val formattedToken = "Bearer $token"
            val request = RecipeSearchRequestJson(query = query)
            val recipeJsonList = webservice.searchRecipes(formattedToken, request)

            // Mapping dari JSON ke Domain Model
            return recipeJsonList.mapNotNull { Recipe.fromJson(it) }
        } catch (e: Exception) {
            Log.e("RecipeRemoteDS", "Failed to search recipes", e)
            throw e
        }
    }
}