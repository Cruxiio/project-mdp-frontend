package com.example.nutrisaver.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nutrisaver.data.sources.local.entity.FoodStockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodStockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foodStocks: List<FoodStockEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(foodStock: FoodStockEntity)

    // Mengambil semua stok makanan, diurutkan berdasarkan tanggal kedaluwarsa terdekat
    @Query("SELECT * FROM food_stock ORDER BY expiredDate ASC")
    fun getAllFoodStock(): Flow<List<FoodStockEntity>>

    @Query("DELETE FROM food_stock")
    suspend fun clearAll()

    @Query("DELETE FROM food_stock WHERE id = :id")
    suspend fun deleteById(id: Int)
}