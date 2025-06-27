package com.example.nutrisaver.data.sources.remote.common

import android.util.Log
import com.example.nutrisaver.data.model.DailyConsumption
import com.example.nutrisaver.data.sources.remote.Webservice

interface ConsumptionDataSource {
    suspend fun getTodaysConsumption(token: String): DailyConsumption
    suspend fun logMeal(token: String, mealDetailJson: DailyConsumptionDetailJson)
}

// Implementasinya
class ConsumptionDataSourceImpl(
    private val webservice: Webservice
): ConsumptionDataSource {
    override suspend fun getTodaysConsumption(token: String): DailyConsumption {
        val formattedToken = "Bearer $token"
        val consumptionJson = webservice.getTodaysConsumption(formattedToken)
        Log.d("ConsumptionDataSource", "Received consumption JSON: $consumptionJson")
        return DailyConsumption.fromDailyConsumptionJson(consumptionJson)
            ?: throw Exception("Gagal mem-parsing data konsumsi dari server.")
    }

    override suspend fun logMeal(token: String, mealDetailJson: DailyConsumptionDetailJson) {
        webservice.logMeal("Bearer $token", mealDetailJson)
    }
}