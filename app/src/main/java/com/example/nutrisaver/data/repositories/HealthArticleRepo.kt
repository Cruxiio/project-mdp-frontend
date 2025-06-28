package com.example.nutrisaver.data.repositories
import android.util.Log
import com.example.nutrisaver.data.model.HealthArticle
import com.example.nutrisaver.data.sources.remote.HealthArticleRemoteDataSource

interface HealthArticleRepo {
    suspend fun getHealthArticles(
        token: String,
        targetGoal: String?,
        targetDietType: String?,
        title: String?
    ): List<HealthArticle>
}

/**
 * Implementasi HealthArticleRepo.
 * (Untuk sekarang hanya menggunakan remote, caching bisa ditambahkan nanti)
 */
class HealthArticleRepoImpl(
    private val remoteDataSource: HealthArticleRemoteDataSource
    // private val localDataSource: HealthArticleLocalDataSource // Bisa ditambahkan nanti
) : HealthArticleRepo {

    private val TAG = "DataFlow-HealthArticleRepo"

    override suspend fun getHealthArticles(
        token: String,
        targetGoal: String?,
        targetDietType: String?,
        title: String?
    ): List<HealthArticle> {
        Log.d(TAG, "Mengambil artikel dari remote data source.")
        try {
            // Langsung memanggil remote data source.
            // Logika caching bisa ditambahkan di sini seperti pada FoodStockRepo.
            return remoteDataSource.getHealthArticles(token, targetGoal, targetDietType, title)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengambil artikel dari remote.", e)
            // Jika ada local data source, bisa panggil dari sana sebagai fallback.
            // Untuk sekarang, lemparkan error kembali.
            throw e
        }
    }
}
