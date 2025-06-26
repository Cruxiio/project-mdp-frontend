package com.example.nutrisaver.data.sources.remote.common

import android.util.Log
import com.example.nutrisaver.data.model.FoodStock
import com.example.nutrisaver.data.sources.remote.Webservice
import java.time.LocalDate
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
    suspend fun deleteFoodStock(token: String, id: Int)
    suspend fun updateFoodStockQuantity(token: String, id: Int, quantity: Float): FoodStock
}

/**
 * Implementasi dari FoodStockRemoteDataSource yang menggunakan Retrofit.
 */
class FoodStockDataSourceImpl(
    private val webservice: Webservice
) : FoodStockDataSource {

    override suspend fun addFoodStock(token: String, foodStock: FoodStock): FoodStock {
        // 1. Buat request body dari domain model
        val requestBody = NewFoodStockRequestJson(
            ingredientId = foodStock.ingredientId,
            quantity = foodStock.quantity,
            unit = foodStock.unit,
            expiredDate = foodStock.expiredDate?.toString(),
            startRemindDate = foodStock.startRemindDate?.toString()
        )

        // 2. Panggil webservice dengan format "Bearer <spasi> token"
        // PASTIKAN BENTUKNYA SEPERTI INI
        val response = webservice.addFoodStock("Bearer $token", requestBody)

        // 3. Cek respons
        if (response.isSuccessful && response.body() != null) {
            val responseBody = response.body()!!
            val receivedItemJson = responseBody.foodStock

            // Proses Mapping...
            return FoodStock(
                id = receivedItemJson.id,
                userId = foodStock.userId,
                ingredientId = foodStock.ingredientId,
                name = receivedItemJson.name,
                imageUrl = receivedItemJson.imageUrl,
                quantity = receivedItemJson.quantity.toFloatOrNull() ?: 0f,
                unit = receivedItemJson.unit,
                expiredDate = receivedItemJson.expiredDate?.let { LocalDate.parse(it) },
                startRemindDate = receivedItemJson.startRemindDate?.let { LocalDate.parse(it) }
            )
        } else {
            // Jika jaringan gagal atau server error (seperti 401)
            throw Exception("Gagal menambah stok. Kode: ${response.code()} - ${response.message()}")
        }
    }

    override suspend fun getAllFoodStock(token: String): List<FoodStock> {
        try {
            val formattedToken = "Bearer $token"

            // Panggil endpoint yang sekarang langsung mengembalikan List
            val jsonList = webservice.getAllFoodStock(formattedToken) // <-- Ini sudah langsung List<FoodStockJson>

            // Langsung mapping seperti biasa
            return jsonList.mapNotNull { FoodStock.fromStockJson(it) }

        } catch (e: Exception) {
            Log.e("FoodStockRemoteDS", "Failed to get all food stock", e)
            throw e
        }
    }

    override suspend fun deleteFoodStock(token: String, id: Int) {
        try {
            val formattedToken = "Bearer $token"
            val response = webservice.deleteFoodStock(formattedToken, id)
            if (!response.isSuccessful) {
                // Jika server merespons dengan error (misal: 404 Not Found)
                throw Exception("Failed to delete food stock on server. Code: ${response.code()}")
            }
            // Jika berhasil, tidak perlu melakukan apa-apa lagi di sini.
        } catch (e: Exception) {
            Log.e("FoodStockRemoteDS", "Failed to delete food stock", e)
            throw e
        }
    }

    override suspend fun updateFoodStockQuantity(token: String, id: Int, quantity: Float): FoodStock {
        try {
            val formattedToken = "Bearer $token"
            val requestJson = UpdateFoodStockRequestJson(quantity = quantity)
            val responseWrapper = webservice.updateFoodStock(formattedToken, id, requestJson)
            return FoodStock.fromStockJson(responseWrapper.foodStock)
                ?: throw Exception("Invalid data received after updating.")
        } catch (e: Exception) {
            Log.e("FoodStockRemoteDS", "Failed to update food stock", e)
            throw e
        }
    }
}