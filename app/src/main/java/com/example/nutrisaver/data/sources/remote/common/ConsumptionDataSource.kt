package com.example.nutrisaver.data.sources.remote.common

import com.example.nutrisaver.data.model.DailyConsumption
import com.example.nutrisaver.data.sources.remote.Webservice

interface ConsumptionDataSource {
    suspend fun getTodaysConsumption(token: String): DailyConsumption
}

// Implementasinya
class ConsumptionDataSourceImpl(
    private val webservice: Webservice
): ConsumptionDataSource {
    override suspend fun getTodaysConsumption(token: String): DailyConsumption {
        val consumptionJson = webservice.getTodaysConsumption(token)
        return DailyConsumption.fromDailyConsumptionJson(consumptionJson)
            ?: throw Exception("Gagal mem-parsing data konsumsi dari server.")
    }
}