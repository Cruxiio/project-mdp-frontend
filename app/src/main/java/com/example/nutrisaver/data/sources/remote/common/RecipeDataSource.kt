package com.example.nutrisaver.data.sources.remote.common

import android.util.Log
import com.example.nutrisaver.data.model.Recipe
import com.example.nutrisaver.data.model.RecipeDetail
import com.example.nutrisaver.data.model.RecipePlain
import com.example.nutrisaver.data.sources.remote.Webservice

interface RecipeDataSource {
    suspend fun searchRecipes(token: String, query: String): List<Recipe>
    suspend fun getAllRecipes(token: String, keyword: String, type: String, page: Int, perpage: Int): List<RecipePlain>
    suspend fun getRecipeDetail(token: String, recipeId: Int): RecipeDetail
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

    override suspend fun getAllRecipes(
        token: String,
        keyword: String,
        type: String,
        page: Int,
        perpage: Int
    ): List<RecipePlain> {
        try {
            val formattedToken = "Bearer $token"
            val responseJson = webservice.getRecipes(formattedToken, keyword, type, page, perpage)
            val listRecipePlainJson = responseJson.recipes ?: emptyList()

            return listRecipePlainJson.mapNotNull { RecipePlain.fromJson(it) }
        }
        catch (e: Exception) {
            Log.e("RecipeRemoteDS", "Failed to get all recipes", e)
            throw e
        }
    }

    override suspend fun getRecipeDetail(token: String, recipeId: Int): RecipeDetail {
        try {
            val formattedToken = "Bearer $token"
            val responseJson = webservice.getRecipesDetail(formattedToken, recipeId)

            return RecipeDetail.fromJson(responseJson)
        }
        catch (e: Exception) {
            Log.e("RecipeRemoteDS", "Failed to get recipes detail", e)
            throw e
        }
    }
}