package com.example.nutrisaver.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutrisaver.data.sources.remote.common.HealthArticleJson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// =======================================================================
// == 1. MODEL DOMAIN (Digunakan di UI & ViewModel) ==
// =======================================================================
/**
 * Model "domain" yang bersih untuk HealthArticle.
 * Ini adalah representasi data yang digunakan di seluruh aplikasi.
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
         * Factory method untuk membuat objek HealthArticle dari objek JSON mentah.
         * Ini adalah titik masuk data dari remote.
         */
        fun fromJson(json: HealthArticleJson?): HealthArticle? {
            // Validasi data penting dari JSON
            if (json?.id == null || json.title.isNullOrBlank()) {
                return null // Artikel tidak valid jika tidak punya ID atau judul
            }

            // Parsing tanggal dengan aman, dengan fallback jika format tidak sesuai
            val createdAtDateTime = try {
                // Pola ISO_OFFSET_DATE_TIME cocok untuk format seperti "2023-11-20T15:30:00.000Z"
                LocalDateTime.parse(json.createdAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            } catch (e: DateTimeParseException) {
                // Jika parsing gagal, gunakan waktu sekarang sebagai default
                LocalDateTime.now()
            }

            // Membuat objek HealthArticle yang bersih dengan nilai fallback
            return HealthArticle(
                id = json.id,
                title = json.title,
                content = json.content ?: "No content available.",
                targetGoal = json.targetGoal ?: "general",
                targetDietType = json.targetDietType ?: "free",
                createdBy = json.createdBy ?: "Unknown Author",
                createdAt = createdAtDateTime
            )
        }
    }
}

