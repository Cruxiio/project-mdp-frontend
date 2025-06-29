package com.example.nutrisaver.data.repositories
import android.util.Log
import com.example.nutrisaver.data.model.HealthArticle
import com.example.nutrisaver.data.model.json.CreateHealthArticleRequestJson
import com.example.nutrisaver.data.model.json.UpdateHealthArticleRequestJson
import com.example.nutrisaver.data.sources.remote.HealthArticleDataSource

interface HealthArticleRepo {
    suspend fun getHealthArticles(
        targetGoal: String?,
        targetDietType: String?,
        title: String?
    ): List<HealthArticle>

    suspend fun createArticle(
        token: String,
        title: String,
        content: String,
        targetGoal: String,
        targetDietType: String,
        createdBy: String
    ): HealthArticle

    suspend fun updateArticle(
        token: String,
        articleId: Int,
        title: String?,
        content: String?,
        targetGoal: String?,
        targetDietType: String?,
        createdBy: String?
    ): HealthArticle

    suspend fun deleteArticle(
        token: String,
        articleId: Int
    )
}

/**
 * Implementasi HealthArticleRepo.
 * (Untuk sekarang hanya menggunakan remote, caching bisa ditambahkan nanti)
 */
class HealthArticleRepoImpl(
    private val remoteDataSource: HealthArticleDataSource
) : HealthArticleRepo {

    private val TAG = "DataFlow-HealthArticleRepo"

    /**
     * [GET] Mengambil daftar artikel.
     */
    override suspend fun getHealthArticles(
        targetGoal: String?,
        targetDietType: String?,
        title: String?
    ): List<HealthArticle> {
        Log.d(TAG, "getHealthArticles: Mengambil artikel dari remote data source.")
        try {
            return remoteDataSource.getHealthArticles(targetGoal, targetDietType, title)
        } catch (e: Exception) {
            Log.e(TAG, "getHealthArticles: Gagal mengambil artikel.", e)
            throw e // Lemparkan error ke ViewModel untuk ditangani
        }
    }

    /**
     * [CREATE] Membuat artikel baru.
     */
    override suspend fun createArticle(
        token: String,
        title: String,
        content: String,
        targetGoal: String,
        targetDietType: String,
        createdBy: String
    ): HealthArticle {
        Log.d(TAG, "createArticle: Membuat artikel baru dengan judul: $title")
        try {
            // Membuat objek request untuk dikirim ke data source
            val request = CreateHealthArticleRequestJson(
                title = title,
                content = content,
                targetGoal = targetGoal,
                targetDietType = targetDietType,
                createdBy = createdBy
            )
            return remoteDataSource.createHealthArticle(token, request)
        } catch (e: Exception) {
            Log.e(TAG, "createArticle: Gagal membuat artikel.", e)
            throw e
        }
    }

    /**
     * [UPDATE] Memperbarui artikel yang ada.
     */
    override suspend fun updateArticle(
        token: String,
        articleId: Int,
        title: String?,
        content: String?,
        targetGoal: String?,
        targetDietType: String?,
        createdBy: String?
    ): HealthArticle {
        Log.d(TAG, "updateArticle: Memperbarui artikel dengan ID: $articleId")
        try {
            // Membuat objek request untuk dikirim ke data source
            val request = UpdateHealthArticleRequestJson(
                title = title,
                content = content,
                targetGoal = targetGoal,
                targetDietType = targetDietType,
                createdBy = createdBy
            )
            return remoteDataSource.updateHealthArticle(token, articleId, request)
        } catch (e: Exception) {
            Log.e(TAG, "updateArticle: Gagal memperbarui artikel.", e)
            throw e
        }
    }

    /**
     * [DELETE] Menghapus artikel.
     */
    override suspend fun deleteArticle(token: String, articleId: Int) {
        Log.d(TAG, "deleteArticle: Menghapus artikel dengan ID: $articleId")
        try {
            remoteDataSource.deleteHealthArticle(token, articleId)
        } catch (e: Exception) {
            Log.e(TAG, "deleteArticle: Gagal menghapus artikel.", e)
            throw e
        }
    }
}