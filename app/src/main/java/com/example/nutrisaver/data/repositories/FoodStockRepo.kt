package com.example.nutrisaver.data.repositories

import android.util.Log
import com.example.nutrisaver.data.model.FoodStock
import com.example.nutrisaver.data.sources.local.FoodStockLocalDataSource
import com.example.nutrisaver.data.sources.remote.common.FoodStockDataSource
import kotlinx.coroutines.flow.first

// ... (interface FoodStockRepo tetap sama) ...
interface FoodStockRepo {
    suspend fun getFoodStock(token: String): List<FoodStock>
    suspend fun addFoodStock(token: String, foodStock: FoodStock)
    suspend fun deleteFoodStock(token: String, id: Int)
    suspend fun updateFoodStockQuantity(token: String, id: Int, quantity: Float): FoodStock
    suspend fun getExpiringSoonStock(token: String): List<FoodStock>
}


class FoodStockRepoImpl(
    private val foodRemoteDataSource: FoodStockDataSource,
    private val foodLocalDataSource: FoodStockLocalDataSource
) : FoodStockRepo {

    private val TAG = "DataFlow-Repository" // Tag untuk Logcat

    override suspend fun getFoodStock(token: String): List<FoodStock> {
        Log.d(TAG, "getFoodStock: Proses dimulai di Repository.")
        try {
            Log.d(TAG, "getFoodStock: Mencoba mengambil data dari REMOTE...")
            val remoteStock = foodRemoteDataSource.getAllFoodStock(token)
            Log.d(TAG, "getFoodStock: BERHASIL mengambil dari REMOTE. Jumlah item: ${remoteStock.size}")

            Log.d(TAG, "getFoodStock: Membersihkan cache lokal (clearAll)...")
            foodLocalDataSource.clearAll()

            Log.d(TAG, "getFoodStock: Memasukkan data baru ke cache lokal (insertAll)...")
            foodLocalDataSource.insertAll(remoteStock)

            return remoteStock
        } catch (e: Exception) {
            Log.e(TAG, "getFoodStock: GAGAL mengambil dari REMOTE. Mengambil dari cache LOKAL sebagai fallback.", e)
            val localData = foodLocalDataSource.getAllFoodStock().first()
            Log.d(TAG, "getFoodStock: Mengembalikan data dari LOKAL. Jumlah item: ${localData.size}")
            return localData
        }
    }

    override suspend fun addFoodStock(token: String, foodStock: FoodStock) {
        Log.d(TAG, "addFoodStock: Menambah stok di Repository.")
        try {
            val newStockFromRemote = foodRemoteDataSource.addFoodStock(token, foodStock)
            foodLocalDataSource.insertOrUpdate(newStockFromRemote)
            Log.d(TAG, "addFoodStock: BERHASIL menambah di remote dan mengupdate cache lokal untuk: ${newStockFromRemote.name}")
        } catch (e: Exception) {
            Log.e(TAG, "addFoodStock: GAGAL menambah stok di repo.", e)
            throw e
        }
    }

    override suspend fun deleteFoodStock(token: String, id: Int) {
        Log.d(TAG, "deleteFoodStock: Menghapus stok di Repository untuk ID: $id.")
        try {
            foodRemoteDataSource.deleteFoodStock(token, id)
            foodLocalDataSource.deleteById(id)
            Log.d(TAG, "deleteFoodStock: BERHASIL menghapus dari remote dan lokal.")
        } catch (e: Exception) {
            Log.e(TAG, "deleteFoodStock: GAGAL menghapus stok di repo.", e)
            throw e
        }
    }

    override suspend fun updateFoodStockQuantity(token: String, id: Int, quantity: Float): FoodStock {
        Log.d(TAG, "updateFoodStockQuantity: Mengupdate stok di Repository untuk ID: $id.")
        val updatedStock = foodRemoteDataSource.updateFoodStockQuantity(token, id, quantity)
        foodLocalDataSource.insertOrUpdate(updatedStock)
        Log.d(TAG, "updateFoodStockQuantity: BERHASIL mengupdate di remote dan lokal.")
        return updatedStock
    }

    override suspend fun getExpiringSoonStock(token: String): List<FoodStock> {
        // Fungsi ini langsung meneruskan panggilan ke remote data source
        return foodRemoteDataSource.getExpiringSoonStock(token)
    }

}