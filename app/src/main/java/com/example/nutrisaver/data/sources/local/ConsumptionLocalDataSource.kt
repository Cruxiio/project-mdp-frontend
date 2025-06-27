package com.example.nutrisaver.data.sources.local

import com.example.nutrisaver.data.model.DailyConsumption
import com.example.nutrisaver.data.sources.local.dao.ConsumptionDao
import com.example.nutrisaver.data.sources.local.entity.DailyConsumptionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ConsumptionLocalDataSource {
    fun getTodaysConsumption(date: String): Flow<DailyConsumption?>
    suspend fun saveTodaysConsumption(consumption: DailyConsumption)
}

class ConsumptionLocalDataSourceImpl(
    private val consumptionDao: ConsumptionDao
) : ConsumptionLocalDataSource {
    override fun getTodaysConsumption(date: String): Flow<DailyConsumption?> {
        // Mengubah Flow<Entity?> menjadi Flow<Domain?>
        return consumptionDao.getTodaysConsumption(date).map { entity ->
            entity?.toDaily()
        }
    }

    override suspend fun saveTodaysConsumption(consumption: DailyConsumption) {
        val entity = DailyConsumptionEntity.fromDaily(consumption)
        consumptionDao.insertOrUpdate(entity)
    }
}