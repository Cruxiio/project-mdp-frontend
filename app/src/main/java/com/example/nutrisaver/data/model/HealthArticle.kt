package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.common.HealthArticleJson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Model "domain" yang bersih untuk HealthArticle, digunakan di UI dan ViewModel.
 */
data class HealthArticle(
    val id: Int,
    val title: String,
    val content: String,
    val targetGoal: String,
    val targetDietType: String,
    val createdBy: String,
    val createdAt: LocalDateTime
) {
    companion object {
        /**
         * Mengubah objek HealthArticleJson mentah menjadi model HealthArticle yang bersih.
         * Mengembalikan null jika data wajib (id, title) tidak ada.
         */
        fun fromJson(json: HealthArticleJson?): HealthArticle? {
            if (json?.id == null || json.title.isNullOrBlank()) {
                return null // Data wajib tidak ada, artikel tidak valid
            }

            // Parsing tanggal dengan fallback jika format salah
            val createdAtDateTime = try {
                // Sesuaikan pola ini dengan format tanggal yang dikirim server
                // Contoh format: "2023-10-27T12:30:00.000Z"
                LocalDateTime.parse(json.createdAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            } catch (e: DateTimeParseException) {
                // Jika parsing gagal, gunakan waktu sekarang sebagai fallback
                LocalDateTime.now()
            }

            return HealthArticle(
                id = json.id,
                title = json.title,
                content = json.content ?: "No content available", // Fallback jika null
                targetGoal = json.targetGoal ?: "general",
                targetDietType = json.targetDietType ?: "free",
                createdBy = json.createdBy ?: "Unknown Author",
                createdAt = createdAtDateTime
            )
        }
    }
}