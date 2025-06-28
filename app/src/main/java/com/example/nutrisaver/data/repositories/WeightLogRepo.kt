package com.example.nutrisaver.data.repositories

import android.util.Log
import com.example.nutrisaver.data.model.WeightLog
import com.example.nutrisaver.data.sources.local.WeightLogLocalDataSource
import com.example.nutrisaver.data.sources.local.dao.WeightLogDao
import com.example.nutrisaver.data.sources.local.entity.toEntity
import com.example.nutrisaver.data.sources.remote.Webservice
import com.example.nutrisaver.data.sources.remote.common.WeightLogDataSource
import com.example.nutrisaver.data.sources.remote.common.WeightLogJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.format.DateTimeFormatter

interface WeightLogRepo {
    val weightHistory: Flow<List<WeightLog>>
    suspend fun refreshWeightHistory(token: String)
    suspend fun logWeight(token: String, weight: Float, unit: String, date: java.time.LocalDate)
}

class WeightLogRepoImpl(
    private val weightRemoteDataSource: WeightLogDataSource, // <-- Menggunakan Remote DS
    private val weightLocalDataSource: WeightLogLocalDataSource
) : WeightLogRepo {

    override val weightHistory: Flow<List<WeightLog>> = weightLocalDataSource.getWeightHistory()

    override suspend fun refreshWeightHistory(token: String) {
        // 1. Ambil dari remote
        val remoteHistory = weightRemoteDataSource.getWeightHistory(token)
        // 2. Simpan ke lokal
        weightLocalDataSource.saveWeightHistory(remoteHistory)
    }

    override suspend fun logWeight(token: String, weight: Float, unit: String, date: java.time.LocalDate) {
        // 1. Kirim ke remote
        weightRemoteDataSource.logWeight(token, weight, unit, date)
        // 2. Refresh data dari awal untuk sinkronisasi
        refreshWeightHistory(token)
    }
}