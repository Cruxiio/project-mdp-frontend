package com.example.nutrisaver.data.repositories

import com.example.nutrisaver.data.model.Recipe
import com.example.nutrisaver.data.model.RecipeDetail
import com.example.nutrisaver.data.model.RecipePlain
import com.example.nutrisaver.data.sources.remote.common.RecipeDataSource

interface RecipeRepo {
    suspend fun searchRecipes(token: String, query: String): List<Recipe>
    suspend fun getAllRecipes(token: String, keyword: String, type: String, page: Int, perpage: Int): List<RecipePlain>
    suspend fun getRecipeDetail(token: String, recipeId: Int): RecipeDetail
}

class RecipeRepoImpl(
    private val recipeRemoteDataSource: RecipeDataSource
) : RecipeRepo {
    override suspend fun searchRecipes(token: String, query: String): List<Recipe> {
        // Langsung teruskan ke remote data source
        return recipeRemoteDataSource.searchRecipes(token, query)
    }

    override suspend fun getAllRecipes(
        token: String,
        keyword: String,
        type: String,
        page: Int,
        perpage: Int
    ): List<RecipePlain> {
        return recipeRemoteDataSource.getAllRecipes(token, keyword, type, page, perpage)
    }

    override suspend fun getRecipeDetail(token: String, recipeId: Int): RecipeDetail {
        return recipeRemoteDataSource.getRecipeDetail(token,recipeId)
    }
}