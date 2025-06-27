package com.example.nutrisaver.data.sources.remote.common

import android.util.Log
import com.example.nutrisaver.data.model.DailyConsumption
import com.example.nutrisaver.data.sources.remote.Webservice
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface ConsumptionDataSource {
    suspend fun getTodaysConsumption(token: String): DailyConsumption
    suspend fun logMeal(token: String, mealDetailJson: DailyConsumptionDetailJson)
    suspend fun updateWaterIntake(token: String, amount: Int)
    suspend fun getConsumptionByDate(token: String, date: LocalDate): DailyConsumption?
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

    override suspend fun updateWaterIntake(token: String, amount: Int) {
        val formattedToken = "Bearer $token"
        val requestBody = WaterUpdateRequestJson(water_ml = amount)
        webservice.updateWaterIntake(formattedToken, requestBody)
    }


    override suspend fun getConsumptionByDate(token: String, date: LocalDate): DailyConsumption? {
        try {
            val formattedToken = "Bearer $token"
            // Ubah LocalDate menjadi String dengan format yang dibutuhkan backend
            val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE) // "YYYY-MM-DD"

            // Panggil webservice
            val response = webservice.getConsumptionByDate(formattedToken, dateString)

            // Cek apakah panggilan berhasil
            if (response.isSuccessful) {
                // response.body() bisa null, jadi kita gunakan safe call (?.) dan let
                // untuk mem-parsing hanya jika body tidak null.
                return response.body()?.let { json ->
                    DailyConsumption.fromDailyConsumptionJson(json)
                }
            } else {
                // Jika server mengembalikan error (4xx, 5xx)
                throw Exception("Gagal mengambil riwayat. Kode: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("FoodStockRemoteDS", "Gagal mengambil riwayat konsumsi untuk tanggal $date", e)
            throw e
        }
    }
}