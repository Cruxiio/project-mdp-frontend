package com.example.nutrisaver.data.sources.local

import android.util.Log
import com.example.nutrisaver.data.model.FoodStock
import com.example.nutrisaver.data.sources.local.dao.FoodStockDao
import com.example.nutrisaver.data.sources.local.entity.FoodStockEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface FoodStockLocalDataSource {
    fun getAllFoodStock(): Flow<List<FoodStock>>
    suspend fun insertOrUpdate(foodStock: FoodStock)
    suspend fun insertAll(foodStocks: List<FoodStock>)
    suspend fun clearAll()
    suspend fun deleteById(id: Int)
}

class FoodStockLocalDataSourceImpl(
    private val foodStockDao: FoodStockDao
) : FoodStockLocalDataSource {

    // --- PERBAIKAN DI FUNGSI INI ---
    override fun getAllFoodStock(): Flow<List<FoodStock>> {
        // Fungsi ini TIDAK suspend dan mengembalikan Flow, sesuai interface.
        // Kita menggunakan operator .map dari Flow untuk mengubah setiap list yang di-emit.
        return foodStockDao.getAllFoodStock().map { listOfEntities ->
            // Untuk setiap List<FoodStockEntity> yang datang dari database,
            // kita ubah menjadi List<FoodStock> (domain model).
            listOfEntities.map { entity ->
                entity.toStock()
            }
        }
    }

    override suspend fun insertOrUpdate(foodStock: FoodStock) {
        val entity = FoodStockEntity.fromStock(foodStock)
        foodStockDao.insert(entity)
        Log.d("FoodStockLocalDS", "Inserted/Updated single food stock: ${foodStock.name}")
    }

    override suspend fun insertAll(foodStocks: List<FoodStock>) {
        val entities = foodStocks.map { FoodStockEntity.fromStock(it) }
        foodStockDao.insertAll(entities)
        Log.d("FoodStockLocalDS", "Saved ${entities.size} food stocks to local DB.")
    }

    override suspend fun clearAll() {
        foodStockDao.clearAll()
        Log.d("FoodStockLocalDS", "Cleared all food stocks from local DB.")
    }

    override suspend fun deleteById(id: Int) {
        foodStockDao.deleteById(id)
    }
}