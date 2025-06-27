package com.example.nutrisaver.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.nutrisaver.data.sources.local.entity.DailyConsumptionDetailEntity
import com.example.nutrisaver.data.sources.local.entity.DailyConsumptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsumptionDao {
    // --- Fungsi untuk Observasi UI (Menggunakan Flow) ---
    @Query("SELECT * FROM daily_consumption WHERE date = :date LIMIT 1")
    fun getTodaysConsumptionFlow(date: String): Flow<DailyConsumptionEntity?>

    @Query("SELECT * FROM daily_consumption_detail WHERE consumptionDate = :date")
    fun getDetailsForDateFlow(date: String): Flow<List<DailyConsumptionDetailEntity>>

    // --- Fungsi untuk Kalkulasi & Operasi (Menggunakan suspend) ---
    @Query("SELECT * FROM daily_consumption WHERE date = :date LIMIT 1")
    suspend fun getTodaysConsumption(date: String): DailyConsumptionEntity? // <-- BARU

    @Query("SELECT * FROM daily_consumption_detail WHERE consumptionDate = :date")
    suspend fun getDetailsForDate(date: String): List<DailyConsumptionDetailEntity> // <-- BARU

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConsumption(consumption: DailyConsumptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetail(detail: DailyConsumptionDetailEntity)

    @Query("DELETE FROM daily_consumption_detail WHERE consumptionDate = :date")
    suspend fun clearDetailsForDate(date: String)

    @Transaction
    suspend fun clearAndInsertTodaysData(date: String, consumption: DailyConsumptionEntity, details: List<DailyConsumptionDetailEntity>) {
        // Hapus data lama untuk hari ini
        clearDetailsForDate(date)
        deleteConsumptionByDate(date)

        // Masukkan data baru yang dari server
        insertOrUpdateConsumption(consumption)
        insertAllDetails(details)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDetails(details: List<DailyConsumptionDetailEntity>)

    // ... fungsi insertDetail dan clearDetailsForDate tetap ada ...
    @Query("DELETE FROM daily_consumption WHERE date = :date") // <-- BARU
    suspend fun deleteConsumptionByDate(date: String)
}