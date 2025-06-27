package com.example.nutrisaver.data.sources.local

import com.example.nutrisaver.data.model.DailyConsumption
import com.example.nutrisaver.data.model.DailyConsumptionDetail
import com.example.nutrisaver.data.sources.local.dao.ConsumptionDao
import com.example.nutrisaver.data.sources.local.entity.DailyConsumptionDetailEntity
import com.example.nutrisaver.data.sources.local.entity.DailyConsumptionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface ConsumptionLocalDataSource {
    fun getTodaysConsumptionFlow(date: String): Flow<DailyConsumption?>
    suspend fun getTodaysConsumption(date: String): DailyConsumption? // <-- BARU
    suspend fun saveTodaysConsumption(consumption: DailyConsumption, date: String)
    fun getDetailsForDateFlow(date: String): Flow<List<DailyConsumptionDetail>>
    suspend fun saveConsumptionAndDetails(consumption: DailyConsumption)
    suspend fun addMealDetail(date: String, detail: DailyConsumptionDetail)
    suspend fun getDetailsForDate(date: String): List<DailyConsumptionDetail> // <-- BARU
}

class ConsumptionLocalDataSourceImpl(
    private val consumptionDao: ConsumptionDao
) : ConsumptionLocalDataSource {
    override fun getTodaysConsumptionFlow(date: String): Flow<DailyConsumption?> {
        return consumptionDao.getTodaysConsumptionFlow(date).map { it?.toDaily() }
    }

    override suspend fun getTodaysConsumption(date: String): DailyConsumption? { // <-- BARU
        return consumptionDao.getTodaysConsumption(date)?.toDaily()
    }

    override fun getDetailsForDateFlow(date: String): Flow<List<DailyConsumptionDetail>> {
        return consumptionDao.getDetailsForDateFlow(date).map { list -> list.map { it.toDailyDetail() } }
    }

    // <-- DIUBAH: Hapus saveTodaysConsumption, ganti dengan yg baru
    override suspend fun saveConsumptionAndDetails(consumption: DailyConsumption) {
        val date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        // Lewatkan 'date' yang baru ini ke fungsi konversi Entity
        val consumptionEntity = DailyConsumptionEntity.fromDaily(consumption, date)
        val detailEntities = consumption.details.map { DailyConsumptionDetailEntity.fromDailyDetail(it, date) }

        consumptionDao.clearAndInsertTodaysData(date, consumptionEntity, detailEntities)
    }

    override suspend fun saveTodaysConsumption(consumption: DailyConsumption, date: String) {
        val entity = DailyConsumptionEntity.fromDaily(consumption, date)
        consumptionDao.insertOrUpdateConsumption(entity)
    }

    override suspend fun addMealDetail(date: String, detail: DailyConsumptionDetail) {
        val entity = DailyConsumptionDetailEntity.fromDailyDetail(detail, date)
        consumptionDao.insertDetail(entity)
    }

    override suspend fun getDetailsForDate(date: String): List<DailyConsumptionDetail> { // <-- BARU
        return consumptionDao.getDetailsForDate(date).map { it.toDailyDetail() }
    }

}