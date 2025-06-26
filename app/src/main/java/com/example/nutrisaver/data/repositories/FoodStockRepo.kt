package com.example.nutrisaver.data.repositories

import android.util.Log
import com.example.nutrisaver.data.model.FoodStock
import com.example.nutrisaver.data.sources.local.FoodStockLocalDataSource
import com.example.nutrisaver.data.sources.remote.Webservice
import com.example.nutrisaver.data.sources.remote.common.FoodStockDataSource
import com.example.nutrisaver.data.sources.remote.common.NewFoodStockRequestJson
import kotlinx.coroutines.flow.first


interface FoodStockRepo {
    suspend fun getFoodStock(token: String): List<FoodStock>
    suspend fun addFoodStock(token: String, foodStock: FoodStock)
    suspend fun deleteFoodStock(token: String, id: Int)
    suspend fun updateFoodStockQuantity(token: String, id: Int, quantity: Float): FoodStock
}

class FoodStockRepoImpl(
    private val foodRemoteDataSource: FoodStockDataSource,
    private val foodLocalDataSource: FoodStockLocalDataSource
) : FoodStockRepo {

    override suspend fun getFoodStock(token: String): List<FoodStock> {
        try {
            // Selalu coba ambil data terbaru dari remote
            Log.d("FoodStockRepo", "Fetching fresh data from remote...")
            val remoteStock = foodRemoteDataSource.getAllFoodStock(token)

            // Hapus data lama dan masukkan data baru untuk sinkronisasi
            foodLocalDataSource.clearAll()
            foodLocalDataSource.insertAll(remoteStock)

            return remoteStock
        } catch (e: Exception) {
            // Jika gagal (misal: tidak ada internet), tampilkan data dari lokal sebagai fallback
            Log.e("FoodStockRepo", "Failed to fetch from remote. Returning local data.", e)
            return foodLocalDataSource.getAllFoodStock().first()
        }
    }

    override suspend fun addFoodStock(token: String, foodStock: FoodStock) {
        try {
            val newStockFromRemote = foodRemoteDataSource.addFoodStock(token, foodStock)
            foodLocalDataSource.insertOrUpdate(newStockFromRemote)
            Log.d("FoodStockRepo", "Successfully added and cached food stock: ${newStockFromRemote.name}")
        } catch (e: Exception) {
            Log.e("FoodStockRepo", "Failed to add food stock in repo", e)
            throw e
        }
    }

    override suspend fun deleteFoodStock(token: String, id: Int) {
        try {
            // Hapus dari remote dulu
            foodRemoteDataSource.deleteFoodStock(token, id)
            // Jika remote berhasil, hapus dari cache lokal
            foodLocalDataSource.deleteById(id)
            Log.d("FoodStockRepo", "Successfully deleted food stock with ID: $id")
        } catch (e: Exception) {
            Log.e("FoodStockRepo", "Failed to delete food stock in repo", e)
            throw e
        }
    }

    override suspend fun updateFoodStockQuantity(token: String, id: Int, quantity: Float): FoodStock {
        val updatedStock = foodRemoteDataSource.updateFoodStockQuantity(token, id, quantity)
        // Update cache lokal dengan data baru dari server
        foodLocalDataSource.insertOrUpdate(updatedStock)
        return updatedStock
    }
}