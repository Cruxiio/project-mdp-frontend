package com.example.nutrisaver.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nutrisaver.data.sources.local.entity.DailyConsumptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsumptionDao {
    // Menggunakan Flow agar UI bisa update otomatis saat data di DB berubah
    @Query("SELECT * FROM daily_consumption WHERE date = :date LIMIT 1")
    fun getTodaysConsumption(date: String): Flow<DailyConsumptionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(consumption: DailyConsumptionEntity)
}