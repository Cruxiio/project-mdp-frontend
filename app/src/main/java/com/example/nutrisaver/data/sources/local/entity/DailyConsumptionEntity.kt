package com.example.nutrisaver.data.sources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutrisaver.data.model.DailyConsumption

@Entity(tableName = "daily_consumption")
data class DailyConsumptionEntity(
    // 'date' adalah satu-satunya Primary Key, tidak ada lagi id dari server
    @PrimaryKey
    val date: String,

    val userId: Int,
    val totalCalories: Float,
    val totalProtein: Float,
    val totalFat: Float,
    val totalCarbs: Float,
    val totalWater: Float,
    val targetCalories: Float,
    val targetProtein: Float,
    val targetFat: Float,
    val targetCarbs: Float,
    val targetWater: Float,
    val breakfastCalories: Float,
    val breakfastProteinGrams: Float,
    val breakfastCarbsGrams: Float,
    val breakfastFatGrams: Float,
    val lunchCalories: Float,
    val lunchProteinGrams: Float,
    val lunchCarbsGrams: Float,
    val lunchFatGrams: Float,
    val dinnerCalories: Float,
    val dinnerProteinGrams: Float,
    val dinnerCarbsGrams: Float,
    val dinnerFatGrams: Float
) {
    // <-- DIUBAH: Fungsi konversi ke model domain
    fun toDaily(): DailyConsumption {
        return DailyConsumption(
            id = null, // Saat mengambil dari DB lokal, id akan selalu null.
            userId = this.userId,
            date = this.date,
            // ... semua field lainnya
            totalCalories = this.totalCalories,
            totalProtein = this.totalProtein,
            totalFat = this.totalFat,
            totalCarbs = this.totalCarbs,
            totalWater = this.totalWater,
            targetCalories = this.targetCalories,
            targetProtein = this.targetProtein,
            targetFat = this.targetFat,
            targetCarbs = this.targetCarbs,
            targetWater = this.targetWater,
            breakfastCalories = this.breakfastCalories,
            breakfastProteinGrams = this.breakfastProteinGrams,
            breakfastCarbsGrams = this.breakfastCarbsGrams,
            breakfastFatGrams = this.breakfastFatGrams,
            lunchCalories = this.lunchCalories,
            lunchProteinGrams = this.lunchProteinGrams,
            lunchCarbsGrams = this.lunchCarbsGrams,
            lunchFatGrams = this.lunchFatGrams,
            dinnerCalories = this.dinnerCalories,
            dinnerProteinGrams = this.dinnerProteinGrams,
            dinnerCarbsGrams = this.dinnerCarbsGrams,
            dinnerFatGrams = this.dinnerFatGrams
        )
    }

    companion object {
        // <-- DIUBAH: Fungsi konversi dari model domain
        fun fromDaily(daily: DailyConsumption): DailyConsumptionEntity {
            return DailyConsumptionEntity(
                date = daily.date, // Gunakan date sebagai Primary Key
                userId = daily.userId,
                // 'daily.id' dari server diabaikan dan tidak disimpan sama sekali
                // ... semua field lainnya
                totalCalories = daily.totalCalories,
                totalProtein = daily.totalProtein,
                totalFat = daily.totalFat,
                totalCarbs = daily.totalCarbs,
                totalWater = daily.totalWater,
                targetCalories = daily.targetCalories,
                targetProtein = daily.targetProtein,
                targetFat = daily.targetFat,
                targetCarbs = daily.targetCarbs,
                targetWater = daily.targetWater,
                breakfastCalories = daily.breakfastCalories,
                breakfastProteinGrams = daily.breakfastProteinGrams,
                breakfastCarbsGrams = daily.breakfastCarbsGrams,
                breakfastFatGrams = daily.breakfastFatGrams,
                lunchCalories = daily.lunchCalories,
                lunchProteinGrams = daily.lunchProteinGrams,
                lunchCarbsGrams = daily.lunchCarbsGrams,
                lunchFatGrams = daily.lunchFatGrams,
                dinnerCalories = daily.dinnerCalories,
                dinnerProteinGrams = daily.dinnerProteinGrams,
                dinnerCarbsGrams = daily.dinnerCarbsGrams,
                dinnerFatGrams = daily.dinnerFatGrams
            )
        }
    }
}