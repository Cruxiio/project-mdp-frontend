package com.example.nutrisaver.data.sources.remote.common

import android.util.Log
import com.example.nutrisaver.data.model.FoodStock
import com.example.nutrisaver.data.sources.remote.Webservice
import java.time.format.DateTimeFormatter

interface FoodStockDataSource {
    /**
     * Menambahkan item stok makanan baru ke backend.
     * @param token Firebase ID Token untuk otentikasi.
     * @param newFoodStock Objek FoodStock yang ingin ditambahkan.
     * @return Objek FoodStock yang telah dibuat di server.
     */
    suspend fun addFoodStock(token: String, newFoodStock: FoodStock): FoodStock

    /**
     * Mengambil semua stok makanan milik pengguna dari server.
     * @param token Firebase ID Token untuk otentikasi.
     * @return List dari FoodStock.
     */
    suspend fun getAllFoodStock(token: String): List<FoodStock>
}

/**
 * Implementasi dari FoodStockRemoteDataSource yang menggunakan Retrofit.
 */
class FoodStockDataSourceImpl(
    private val webservice: Webservice
) : FoodStockDataSource {

    override suspend fun addFoodStock(token: String, newFoodStock: FoodStock): FoodStock {
        try {
            val formattedToken = "Bearer $token"
            val requestJson = NewFoodStockRequestJson(
                ingredientId = newFoodStock.ingredientId,
                quantity = newFoodStock.quantity,
                unit = newFoodStock.unit,
                expiredDate = newFoodStock.expiredDate?.format(DateTimeFormatter.ISO_LOCAL_DATE),
                startRemindDate = newFoodStock.startRemindDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)
            )

            val responseWrapper = webservice.addFoodStock(formattedToken, requestJson)

            // =================================================================
            // PERBAIKAN: Panggil fungsi 'fromJson' yang sudah benar namanya
            // =================================================================
            return FoodStock.fromStockJson(responseWrapper.foodStock)
                ?: throw Exception("Invalid data received from server after adding food stock.")

        } catch (e: Exception) {
            Log.e("FoodStockRemoteDS", "Failed to add food stock", e)
            throw e
        }
    }

    override suspend fun getAllFoodStock(token: String): List<FoodStock> {
        try {
            val formattedToken = "Bearer $token"
            // Panggil endpoint get all food stock (Anda perlu menambahkannya di Webservice.kt)
            val response = webservice.getAllFoodStock(formattedToken)

            // Mapping dari List<FoodStockJson> ke List<FoodStock>
            return response.mapNotNull { FoodStock.fromStockJson(it) }
        } catch (e: Exception) {
            Log.e("FoodStockRemoteDS", "Failed to get all food stock", e)
            throw e
        }
    }
}