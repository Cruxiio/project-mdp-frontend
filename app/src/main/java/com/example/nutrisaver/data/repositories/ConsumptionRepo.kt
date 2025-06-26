package com.example.nutrisaver.data.repositories

import android.util.Log
import com.example.nutrisaver.data.model.DailyConsumption
import com.example.nutrisaver.data.sources.local.ConsumptionLocalDataSource
import com.example.nutrisaver.data.sources.local.dao.ConsumptionDao
import com.example.nutrisaver.data.sources.local.entity.DailyConsumptionEntity
import com.example.nutrisaver.data.sources.remote.common.ConsumptionDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface ConsumptionRepo {
    // Fungsi ini hanya mengambil data dari LOKAL (sebagai Flow)
    fun getTodaysConsumption(): Flow<DailyConsumption?>

    // Fungsi ini secara eksplisit mengambil dari REMOTE dan MENYIMPAN ke lokal
    suspend fun refreshTodaysConsumption(token: String)
}

class ConsumptionRepoImpl(
    private val consumpRemoteDataSource: ConsumptionDataSource,
    private val consumpLocalDataSource: ConsumptionLocalDataSource
) : ConsumptionRepo {
    override fun getTodaysConsumption(): Flow<DailyConsumption?> {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        return consumpLocalDataSource.getTodaysConsumption(today)
    }

    override suspend fun refreshTodaysConsumption(token: String) {
        try {
            val formattedToken = "Bearer $token"
            val remoteData = consumpRemoteDataSource.getTodaysConsumption(formattedToken)
            consumpLocalDataSource.saveTodaysConsumption(remoteData)
            Log.d("ConsumptionRepo", "Successfully refreshed and saved today's consumption.")
        } catch (e: Exception) {
            Log.e("ConsumptionRepo", "Failed to refresh today's consumption", e)
            throw e // Lemparkan lagi agar ViewModel bisa menangani errornya
        }
    }
}