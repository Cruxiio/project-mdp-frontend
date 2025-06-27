package com.example.nutrisaver.data.repositories

import android.util.Log
import com.example.nutrisaver.data.model.DailyConsumption
import com.example.nutrisaver.data.model.DailyConsumptionDetail
import com.example.nutrisaver.data.sources.local.ConsumptionLocalDataSource
import com.example.nutrisaver.data.sources.local.dao.ConsumptionDao
import com.example.nutrisaver.data.sources.local.entity.DailyConsumptionEntity
import com.example.nutrisaver.data.sources.remote.common.ConsumptionDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface ConsumptionRepo {
    fun getTodaysConsumptionWithDetails(): Flow<DailyConsumption?>
    suspend fun refreshTodaysConsumption(token: String)
    suspend fun logMeal(token: String, mealDetail: DailyConsumptionDetail)
}

class ConsumptionRepoImpl(
    private val consumpRemoteDataSource: ConsumptionDataSource,
    private val consumpLocalDataSource: ConsumptionLocalDataSource
) : ConsumptionRepo {
    override fun getTodaysConsumptionWithDetails(): Flow<DailyConsumption?> {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        val summaryFlow = consumpLocalDataSource.getTodaysConsumptionFlow(today)
        val detailsFlow = consumpLocalDataSource.getDetailsForDateFlow(today)

        // Gabungkan kedua Flow. Jika ada perubahan di ringkasan atau detail,
        // data baru akan otomatis di-emit ke UI.
        return combine(summaryFlow, detailsFlow) { summary, details ->
            summary?.copy(details = details)
        }
    }

    override suspend fun refreshTodaysConsumption(token: String) {
        try {
            // Panggil remote, yang sekarang mengembalikan JSON dengan 'details'
            val remoteData = consumpRemoteDataSource.getTodaysConsumption(token)

            // Panggil fungsi baru di local data source untuk menyimpan semuanya
            remoteData?.let {
                consumpLocalDataSource.saveConsumptionAndDetails(it)
                Log.d("ConsumptionRepo", "Successfully refreshed and saved consumption AND details.")
            }
        } catch (e: Exception) {
            Log.e("ConsumptionRepo", "Failed to refresh today's consumption", e)
            throw e
        }
    }

    override suspend fun logMeal(token: String, mealDetail: DailyConsumptionDetail) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val TAG = "LogMealDebug" // Tag khusus untuk mempermudah filter di Logcat

        Log.d(TAG, "--- STARTING logMeal ---")
        Log.d(TAG, "Meal to log: $mealDetail")

        // Kirim ke server di background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "[Remote] Attempting to send meal to server...")
                consumpRemoteDataSource.logMeal(token, mealDetail.toRequestJson())
                Log.d(TAG, "[Remote] SUCCESS: Meal sent to server.")
            } catch (e: Exception) {
                // PENTING: Cek Logcat untuk error ini! Ini bisa jadi penyebabnya.
                Log.e(TAG, "[Remote] FAILED to send meal to server.", e)
            }
        }

        // --- LOGIKA UTAMA: UPDATE LOKAL SECARA LANGSUNG ---
        Log.d(TAG, "[Local] Starting local database update...")

        try {
            // 1. Simpan detail makanan baru ke database lokal
            Log.d(TAG, "[Local] 1. Adding new meal detail to local DB...")
            consumpLocalDataSource.addMealDetail(today, mealDetail)
            Log.d(TAG, "[Local] 1. SUCCESS: New meal detail added.")

            // 2. Ambil SEMUA detail makanan untuk hari ini dari lokal
            Log.d(TAG, "[Local] 2. Fetching all today's details from local DB...")
            val allTodaysDetails = consumpLocalDataSource.getDetailsForDate(today)
            Log.d(TAG, "[Local] 2. SUCCESS: Found ${allTodaysDetails.size} details for today.")

            // 3. Hitung ulang semua total
            Log.d(TAG, "[Local] 3. Recalculating all totals...")
            // ... (kode kalkulasi Anda yang sudah lengkap dari jawaban sebelumnya tetap di sini) ...
            var totalCalories = 0f; var totalProtein = 0f; var totalCarbs = 0f; var totalFat = 0f
            var breakfastCalories = 0f; var breakfastProtein = 0f; var breakfastCarbs = 0f; var breakfastFat = 0f
            var lunchCalories = 0f; var lunchProtein = 0f; var lunchCarbs = 0f; var lunchFat = 0f
            var dinnerCalories = 0f; var dinnerProtein = 0f; var dinnerCarbs = 0f; var dinnerFat = 0f
            allTodaysDetails.forEach { detail ->
                totalCalories += detail.calories; totalProtein += detail.protein; totalCarbs += detail.carbs; totalFat += detail.fat
                when (detail.mealType.lowercase()) {
                    "breakfast" -> { breakfastCalories += detail.calories; breakfastProtein += detail.protein; breakfastCarbs += detail.carbs; breakfastFat += detail.fat }
                    "lunch" -> { lunchCalories += detail.calories; lunchProtein += detail.protein; lunchCarbs += detail.carbs; lunchFat += detail.fat }
                    "dinner" -> { dinnerCalories += detail.calories; dinnerProtein += detail.protein; dinnerCarbs += detail.carbs; dinnerFat += detail.fat }
                }
            }
            Log.d(TAG, "[Local] 3. SUCCESS: New totals calculated. Total Calories: $totalCalories, Breakfast Calories: $breakfastCalories")

            // 4. Ambil data ringkasan saat ini
            Log.d(TAG, "[Local] 4. Fetching current consumption summary...")
            val currentConsumptionSummary = consumpLocalDataSource.getTodaysConsumption(today)
            if (currentConsumptionSummary == null) {
                Log.d(TAG, "[Local] 4. No existing summary found. Will create a new one.")
            } else {
                Log.d(TAG, "[Local] 4. Found existing summary.")
            }

            // Buat objek default jika tidak ada
            val summaryToUpdate = currentConsumptionSummary ?: DailyConsumption(id = null, userId = 0, totalCalories = 0f, totalProtein = 0f, totalCarbs = 0f, totalFat = 0f,
                targetCalories = 0f, targetProtein = 0f, targetCarbs = 0f, targetFat = 0f,
                breakfastCalories = 0f, breakfastProteinGrams = 0f, breakfastCarbsGrams = 0f, breakfastFatGrams = 0f,
                lunchCalories = 0f, lunchProteinGrams = 0f, lunchCarbsGrams = 0f, lunchFatGrams = 0f, dinnerCalories = 0f,
                dinnerProteinGrams = 0f, dinnerCarbsGrams = 0f, dinnerFatGrams = 0f, createdAt = today, targetWater = 0f, totalWater = 0f)

            // 5. Buat objek ringkasan baru dengan nilai yang sudah di-update
            Log.d(TAG, "[Local] 5. Creating updated summary object...")
            val updatedSummary = summaryToUpdate.copy(
                totalCalories = totalCalories, totalProtein = totalProtein, totalCarbs = totalCarbs, totalFat = totalFat,
                breakfastCalories = breakfastCalories, breakfastProteinGrams = breakfastProtein, breakfastCarbsGrams = breakfastCarbs, breakfastFatGrams = breakfastFat,
                lunchCalories = lunchCalories, lunchProteinGrams = lunchProtein, lunchCarbsGrams = lunchCarbs, lunchFatGrams = lunchFat,
                dinnerCalories = dinnerCalories, dinnerProteinGrams = dinnerProtein, dinnerCarbsGrams = dinnerCarbs, dinnerFatGrams = dinnerFat
            )
            Log.d(TAG, "[Local] 5. SUCCESS: Updated summary object created.")


            // 6. Simpan ringkasan yang sudah ter-update ke database lokal
            Log.d(TAG, "[Local] 6. Saving updated summary to local DB...")
            consumpLocalDataSource.saveTodaysConsumption(updatedSummary, today)
            Log.d(TAG, "[Local] 6. SUCCESS: Updated summary saved.")
            Log.d(TAG, "--- FINISHED logMeal ---")

        } catch (e: Exception) {
            Log.e(TAG, "[Local] FAILED: An error occurred during local DB update.", e)
        }
    }

}