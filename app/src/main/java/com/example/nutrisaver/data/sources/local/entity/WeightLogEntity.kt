package com.example.nutrisaver.data.sources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutrisaver.data.model.WeightLog
import java.time.LocalDate

@Entity(tableName = "user_weight")
data class WeightLogEntity(
    @PrimaryKey val id: Int, // id dari server
    val weight: Float,
    val unit: String,
    val date: String // Simpan sebagai YYYY-MM-DD
) {
    fun toWeight(): WeightLog = WeightLog(id, weight, unit, LocalDate.parse(date))
}

fun WeightLog.toEntity(): WeightLogEntity = WeightLogEntity(id, weight, unit, date.toString())