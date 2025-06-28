package com.example.nutrisaver.data.sources.remote

import com.example.nutrisaver.data.model.HealthArticle

/**
 * Interface untuk sumber data artikel dari remote (API).
 */
interface HealthArticleRemoteDataSource {
    suspend fun getHealthArticles(
        token: String,
        targetGoal: String?,
        targetDietType: String?,
        title: String?
    ): List<HealthArticle>
}

/**
 * Implementasi dari HealthArticleRemoteDataSource menggunakan Retrofit (ApiService).
 */
class HealthArticleRemoteDataSourceImpl(
    private val apiService: ApiService
) : HealthArticleRemoteDataSource {

    override suspend fun getHealthArticles(
        token: String,
        targetGoal: String?,
        targetDietType: String?,
        title: String?
    ): List<HealthArticle> {
        val response = apiService.getHealthArticles(
            token = "Bearer $token",
            targetGoal = targetGoal,
            targetDietType = targetDietType,
            title = title
        )

        // Transformasi dari List<HealthArticleJson> ke List<HealthArticle>
        // dan filter item yang tidak valid (null).
        return response.articles?.mapNotNull { json ->
            HealthArticle.fromJson(json)
        } ?: emptyList() // Kembalikan list kosong jika 'articles' null
    }
}