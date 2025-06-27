package com.example.nutrisaver.data.repositories

import com.example.nutrisaver.data.model.Recipe
import com.example.nutrisaver.data.sources.remote.common.RecipeDataSource

interface RecipeRepo {
    suspend fun searchRecipes(token: String, query: String): List<Recipe>
}

class RecipeRepoImpl(
    private val recipeRemoteDataSource: RecipeDataSource
) : RecipeRepo {
    override suspend fun searchRecipes(token: String, query: String): List<Recipe> {
        // Langsung teruskan ke remote data source
        return recipeRemoteDataSource.searchRecipes(token, query)
    }
}