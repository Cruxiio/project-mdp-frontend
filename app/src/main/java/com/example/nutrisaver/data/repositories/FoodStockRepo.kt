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
}

class FoodStockRepoImpl(
    private val foodRemoteDataSource: FoodStockDataSource,
    private val foodLocalDataSource: FoodStockLocalDataSource
) : FoodStockRepo {

    override suspend fun getFoodStock(token: String): List<FoodStock> {
        // --- PERBAIKAN DI BAGIAN INI ---

        // 1. Ambil data pertama dari Flow<List<FoodStock>> yang disediakan oleh LocalDataSource.
        //    Tidak perlu mapping lagi di sini, karena sudah dilakukan di dalam LocalDataSource.
        val localStock = foodLocalDataSource.getAllFoodStock().first()

        // 2. Logika cache diperbaiki menjadi isNotEmpty()
        if (localStock.isNotEmpty()) {
            Log.d("FoodStockRepo", "Returning ${localStock.size} items from local cache.")
            return localStock
        }

        // Jika cache kosong, lanjutkan ke remote
        Log.d("FoodStockRepo", "Local cache is empty. Fetching from remote.")
        val remoteStock = foodRemoteDataSource.getAllFoodStock(token)
        if (remoteStock.isNotEmpty()) {
            foodLocalDataSource.insertAll(remoteStock)
        }
        return remoteStock
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
}