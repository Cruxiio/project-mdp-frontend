package com.example.nutrisaver.data.sources.remote.common

import com.example.nutrisaver.data.model.WeightLog
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@JsonClass(generateAdapter = true)
data class WeightLogJson(
    @Json(name = "id") val id: Int?,
    @Json(name = "weight") val weight: Float?,
    @Json(name = "unit") val unit: String?,
    @Json(name = "date") val date: String? // YYYY-MM-DDTHH:mm:ss.sssZ
) {
    fun toWeight(): WeightLog? {
        if (id == null || weight == null || unit == null || date == null) return null
        try {
            // DIUBAH: Gunakan format yang benar untuk mem-parsing tanggal.
            // Coba parsing bagian tanggal saja jika ada komponen waktu.
            val dateStringOnly = date.substring(0, 10) // Ambil "YYYY-MM-DD"
            val localDate = LocalDate.parse(dateStringOnly, DateTimeFormatter.ISO_LOCAL_DATE)
            return WeightLog(id, weight, unit, localDate)
        } catch (e: Exception) {
            // Jika parsing gagal, log error dan kembalikan null agar aplikasi tidak crash
            android.util.Log.e("WeightLogJson", "Failed to parse date: $date", e)
            return null
        }
    }
}