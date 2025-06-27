package com.example.nutrisaver.data.sources.remote.common

import android.util.Log
import com.example.nutrisaver.data.model.WeightLog
import com.example.nutrisaver.data.sources.remote.Webservice
import java.time.format.DateTimeFormatter

interface WeightLogDataSource {
    suspend fun getWeightHistory(token: String): List<WeightLog>
    suspend fun logWeight(token: String, weight: Float, unit: String, date: java.time.LocalDate)
}

// Implementasi
class WeightLogDataSourceImpl(
    private val webservice: Webservice
) : WeightLogDataSource {

    /**
     * Mengambil seluruh riwayat berat badan dari server.
     */
    override suspend fun getWeightHistory(token: String): List<WeightLog> {
        val formattedToken = "Bearer $token"
        try {
            Log.d("WeightLogRemoteDS", "Fetching weight history from remote...")
            val response = webservice.getWeightHistory(formattedToken)
            // Mapping dari List<DTO> ke List<Domain Model>
            return response.mapNotNull { it.toWeight() }
        } catch (e: Exception) {
            Log.e("WeightLogRemoteDS", "Error fetching weight history: ${e.message}", e)
            throw e
        }
    }

    /**
     * Mengirim data berat badan baru ke server.
     */
    override suspend fun logWeight(token: String, weight: Float, unit: String, date: java.time.LocalDate) {
        val formattedToken = "Bearer $token"
        try {
            // Ubah LocalDate menjadi String format YYYY-MM-DD
            val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val requestBody = WeightLogJson(0, weight, unit, dateString)

            Log.d("WeightLogRemoteDS", "Logging new weight to remote: $requestBody")
            webservice.logWeight(formattedToken, requestBody)
        } catch (e: Exception) {
            Log.e("WeightLogRemoteDS", "Error logging new weight: ${e.message}", e)
            throw e
        }
    }
}