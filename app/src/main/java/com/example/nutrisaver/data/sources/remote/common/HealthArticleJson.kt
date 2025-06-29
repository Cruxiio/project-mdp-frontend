package com.example.nutrisaver.data.model.json // Pastikan package ini sesuai

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * [GET Response] - Membungkus seluruh respons dari GET /api/health-articles.
 * Anotasi @JsonClass(generateAdapter = true) memberitahu Moshi untuk membuat adapter
 * secara otomatis untuk kelas ini.
 */
@JsonClass(generateAdapter = true)
data class HealthArticleResponseJson(
    @Json(name = "articles") // Anotasi Moshi untuk memetakan nama field JSON
    val articles: List<HealthArticleJson>?
)

/**
 * [Single Item] - Merepresentasikan SATU objek artikel mentah dari API.
 * Digunakan baik dalam response GET maupun dalam body response CREATE/UPDATE.
 */
@JsonClass(generateAdapter = true)
data class HealthArticleJson(
    @Json(name = "id")
    val id: Int?,

    @Json(name = "title")
    val title: String?,

    @Json(name = "content")
    val content: String?,

    @Json(name = "target_goal")
    val targetGoal: String?,

    @Json(name = "target_diet_type")
    val targetDietType: String?,

    @Json(name = "created_by")
    val createdBy: String?,

    @Json(name = "createdAt")
    val createdAt: String?
)

/**
 * [CREATE Request] - Untuk body permintaan saat membuat artikel baru.
 */
@JsonClass(generateAdapter = true)
data class CreateHealthArticleRequestJson(
    @Json(name = "title")
    val title: String,

    @Json(name = "content")
    val content: String,

    @Json(name = "target_goal")
    val targetGoal: String,

    @Json(name = "target_diet_type")
    val targetDietType: String,

    @Json(name = "created_by")
    val createdBy: String
)

/**
 * [UPDATE Request] - Untuk body permintaan saat memperbarui artikel.
 * Semua field dibuat nullable.
 */
@JsonClass(generateAdapter = true)
data class UpdateHealthArticleRequestJson(
    @Json(name = "title")
    val title: String?,

    @Json(name = "content")
    val content: String?,

    @Json(name = "target_goal")
    val targetGoal: String?,

    @Json(name = "target_diet_type")
    val targetDietType: String?,

    @Json(name = "created_by")
    val createdBy: String?
)

// Catatan: UpdateHealthArticleResponseJson tidak lagi diperlukan jika backend Anda
// mengembalikan HealthArticleJson secara langsung untuk operasi update.
// Namun, tidak masalah jika tetap ada di sini.
@JsonClass(generateAdapter = true)
data class UpdateHealthArticleResponseJson(
    @Json(name = "article")
    val article: HealthArticleJson?
)