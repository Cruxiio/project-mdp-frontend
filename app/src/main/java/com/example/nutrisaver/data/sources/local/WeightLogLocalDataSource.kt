package com.example.nutrisaver.data.sources.local

import com.example.nutrisaver.data.model.WeightLog
import com.example.nutrisaver.data.sources.local.dao.WeightLogDao
import com.example.nutrisaver.data.sources.local.entity.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface WeightLogLocalDataSource {
    fun getWeightHistory(): Flow<List<WeightLog>>
    suspend fun saveWeightHistory(weights: List<WeightLog>)
}

// Implementasi
class WeightLogLocalDataSourceImpl(
    private val weightLogDao: WeightLogDao
) : WeightLogLocalDataSource {

    /**
     * Menyediakan Flow dari riwayat berat badan yang sudah di-map ke domain model.
     * UI akan mengobservasi data dari sini.
     */
    override fun getWeightHistory(): Flow<List<WeightLog>> {
        // Ambil Flow<List<Entity>> dari DAO
        val weightEntitiesFlow = weightLogDao.getWeightHistory()
        // Map menjadi Flow<List<Domain Model>>
        return weightEntitiesFlow.map { entityList ->
            entityList.map { it.toWeight() }
        }
    }

    /**
     * Menyimpan data riwayat berat badan dari server ke database lokal.
     * Menggunakan transaksi untuk memastikan data lama dihapus sebelum data baru dimasukkan.
     */
    override suspend fun saveWeightHistory(weights: List<WeightLog>) {
        // Map dari List<Domain Model> menjadi List<Entity>
        val weightEntities = weights.map { it.toEntity() }
        // Panggil fungsi DAO transaksional
        weightLogDao.clearAndInsertAll(weightEntities)
    }
}