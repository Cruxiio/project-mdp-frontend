package com.example.nutrisaver.data.sources.remote.common

import com.google.gson.annotations.SerializedName

data class HealthArticleResponseJson(
    @SerializedName("total")
    val total: Int?, // Buat nullable untuk keamanan
    @SerializedName("articles")
    val articles: List<HealthArticleJson>?
)

/**
 * Merepresentasikan SATU objek artikel mentah dari JSON.
 */
data class HealthArticleJson(
    @SerializedName("id")
    val id: Int?, // Wajib ada untuk identifikasi

    @SerializedName("title")
    val title: String?,

    @SerializedName("content")
    val content: String?,

    @SerializedName("target_goal")
    val targetGoal: String?,

    @SerializedName("target_diet_type")
    val targetDietType: String?,

    @SerializedName("createdBy")
    val createdBy: String?,

    @SerializedName("createdAt")
    val createdAt: String? // Terima sebagai String mentah dari API
)