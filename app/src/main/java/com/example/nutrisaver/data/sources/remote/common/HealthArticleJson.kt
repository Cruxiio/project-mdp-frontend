package com.example.nutrisaver.data.sources.remote.common

import com.google.gson.annotations.SerializedName

/**
 * [GET Response] - Membungkus seluruh respons dari GET /api/health-articles.
 */
data class HealthArticleResponseJson(

    @SerializedName("articles") // <-- DIPERBAIKI
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

    @SerializedName("created_by")
    val createdBy: String?,

    @SerializedName("createdAt")
    val createdAt: String?
)

/**
 * [CREATE Request] - Untuk body permintaan saat membuat artikel.
 */
data class CreateHealthArticleRequestJson(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("target_goal")
    val targetGoal: String,
    @SerializedName("target_diet_type")
    val targetDietType: String,
    @SerializedName("created_by")
    val createdBy: String
)

data class UpdateHealthArticleRequestJson(
    @SerializedName("title")
    val title: String?,

    @SerializedName("content")
    val content: String?,

    @SerializedName("target_goal")
    val targetGoal: String?,

    @SerializedName("target_diet_type")
    val targetDietType: String?,

    @SerializedName("created_by")
    val createdBy: String?
)

data class UpdateHealthArticleResponseJson(
    @SerializedName("article")
    val article: HealthArticleJson?
)
