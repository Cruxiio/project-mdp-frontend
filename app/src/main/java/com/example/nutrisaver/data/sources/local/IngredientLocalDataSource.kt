package com.example.nutrisaver.data.sources.local

import android.util.Log
import com.example.nutrisaver.data.model.Ingredient
import com.example.nutrisaver.data.sources.local.dao.IngredientDao
import com.example.nutrisaver.data.sources.local.entity.IngredientEntity

interface IngredientLocalDataSource {
    /**
     * Mengambil semua bahan makanan yang tersimpan di database lokal.
     * @return List dari model domain [Ingredient].
     */
    suspend fun getIngredients(): List<Ingredient>

    /**
     * Menyimpan daftar bahan makanan ke database lokal.
     * Data lama akan ditimpa jika ada ID yang sama.
     * @param ingredients List dari model domain [Ingredient] yang akan disimpan.
     */
    suspend fun saveIngredients(ingredients: List<Ingredient>)
}

/**
 * Implementasi dari [IngredientLocalDataSource] yang menggunakan Room DAO.
 */
class IngredientLocalDataSourceImpl(
    private val ingredientDao: IngredientDao
) : IngredientLocalDataSource {

    override suspend fun getIngredients(): List<Ingredient> {
        // Ambil semua entity dari DAO, lalu map ke model domain
        val ingredients = ingredientDao.getAllIngredients().map { entity ->
            entity.toIngredients()
        }
        Log.d("IngredientLocalDS", "Fetched ${ingredients.size} ingredients from local DB.")
        return ingredients
    }

    override suspend fun saveIngredients(ingredients: List<Ingredient>) {
        // Ubah list model domain menjadi list entity sebelum disimpan
        val ingredientEntities = ingredients.map { domainModel ->
            IngredientEntity.fromIngredients(domainModel)
        }
        ingredientDao.insertAll(ingredientEntities)
        Log.d("IngredientLocalDS", "Saved ${ingredientEntities.size} ingredients to local DB.")
    }
}