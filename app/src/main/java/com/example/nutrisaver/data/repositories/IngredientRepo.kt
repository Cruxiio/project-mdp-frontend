package com.example.nutrisaver.data.repositories

import com.example.nutrisaver.data.model.Ingredient
import com.example.nutrisaver.data.sources.local.IngredientLocalDataSource
import com.example.nutrisaver.data.sources.local.IngredientLocalDataSourceImpl
import com.example.nutrisaver.data.sources.local.dao.IngredientDao
import com.example.nutrisaver.data.sources.local.entity.IngredientEntity
import com.example.nutrisaver.data.sources.remote.Webservice
import com.example.nutrisaver.data.sources.remote.common.IngredientDataSource

interface IngredientRepo {
    suspend fun getIngredients(token: String): List<Ingredient>
}

// Implementasi dengan strategi Cache-First
class IngredientRepoImpl(
    private val ingredientRemoteDataSource : IngredientDataSource,
    private val ingredientLocalDataSource: IngredientLocalDataSource
) : IngredientRepo {
    override suspend fun getIngredients(token: String): List<Ingredient> {
        val localIngredients = ingredientLocalDataSource.getIngredients()
        if (localIngredients.isNotEmpty()) {
            return localIngredients
        }

        // Teruskan token saat memanggil remote source
        val remoteIngredients = ingredientRemoteDataSource.getIngredients(token)

        if (remoteIngredients.isNotEmpty()) {
            ingredientLocalDataSource.saveIngredients(remoteIngredients)
        }
        return remoteIngredients
    }
}