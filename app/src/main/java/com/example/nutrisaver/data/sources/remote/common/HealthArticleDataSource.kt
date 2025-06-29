package com.example.nutrisaver.data.sources.remote

import android.util.Log
import com.example.nutrisaver.data.model.HealthArticle
import com.example.nutrisaver.data.model.json.CreateHealthArticleRequestJson
import com.example.nutrisaver.data.model.json.UpdateHealthArticleRequestJson
import retrofit2.HttpException

/**
 * Interface untuk sumber data artikel dari remote (API).
 * Mendefinisikan semua operasi yang bisa dilakukan ke API terkait artikel.
 */
interface HealthArticleDataSource {
    suspend fun getHealthArticles(
        targetGoal: String?,
        targetDietType: String?,
        title: String?
    ): List<HealthArticle>

    suspend fun createHealthArticle(
        token: String,
        request: CreateHealthArticleRequestJson
    ): HealthArticle

    suspend fun updateHealthArticle(
        token: String,
        articleId: Int,
        request: UpdateHealthArticleRequestJson
    ): HealthArticle

    suspend fun deleteHealthArticle(
        token: String,
        articleId: Int
    )
}

/**
 * Implementasi dari HealthArticleRemoteDataSource menggunakan Retrofit (Webservice).
 */
class HealthArticleDataSourceImpl(
    private val webservice: Webservice // <-- Ganti nama menjadi webservice
) : HealthArticleDataSource {

    override suspend fun getHealthArticles(
        targetGoal: String?,
        targetDietType: String?,
        title: String?
    ): List<HealthArticle> {
        // Panggil webservice tanpa token
        val response = webservice.getHealthArticles(
            targetGoal = targetGoal,
            targetDietType = targetDietType,
            title = title
        )
        Log.d("ParsingDebug", "HealthArticleResponseJson yang di-parsing: $response")
        // Transformasi dari List<HealthArticleJson> ke List<HealthArticle> (domain model)
        return response.articles?.mapNotNull { json ->
            HealthArticle.fromJson(json)
        } ?: emptyList()
    }

    /**
     * [CREATE] Mengirim artikel baru ke API.
     */
    override suspend fun createHealthArticle(
        token: String,
        request: CreateHealthArticleRequestJson
    ): HealthArticle {
        val response = webservice.createHealthArticle("Bearer $token", request)

        if (response.isSuccessful) {
            val createdArticleJson = response.body()
            // Konversi dari JSON ke model domain, lempar error jika konversi gagal
            return HealthArticle.fromJson(createdArticleJson)
                ?: throw IllegalStateException("Data artikel yang diterima dari server tidak valid.")
        } else {
            // Lemparkan error dengan pesan dari server jika ada
            throw HttpException(response)
        }
    }

    /**
     * [UPDATE] Mengirim pembaruan artikel ke API.
     */
    // Di HealthArticleDataSourceImpl
    override suspend fun updateHealthArticle(
        token: String,
        articleId: Int,
        request: UpdateHealthArticleRequestJson
    ): HealthArticle {
        val response = webservice.updateHealthArticle("Bearer $token", articleId, request)
        if (response.isSuccessful) {
            // Ambil objek artikel dari dalam wrapper
            val updatedArticleJson = response.body()
            Log.d("UpdateDebug", "JSON diterima dari server: $updatedArticleJson")
            return HealthArticle.fromJson(updatedArticleJson) // <-- Langkah A
                ?: throw IllegalStateException("Data artikel yang diterima setelah update tidak valid.") // <-- Langkah B
        } else {
            throw HttpException(response)
        }
    }

    /**
     * [DELETE] Menghapus artikel dari API.
     */
    override suspend fun deleteHealthArticle(token: String, articleId: Int) {
        val response = webservice.deleteHealthArticle("Bearer $token", articleId)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
    }
}