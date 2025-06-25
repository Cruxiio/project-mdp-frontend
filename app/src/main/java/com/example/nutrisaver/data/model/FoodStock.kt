package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.common.FoodStockJson
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FoodStock(
    val id: Int?,
    val userId: Int,
    val ingredientId: Int,
    val name: String,
    val imageUrl: String?,
    val quantity: Float,
    val unit: String,
    val expiredDate: LocalDate?,
    val startRemindDate: LocalDate?
){
    companion object {
        // PERBAIKAN: Buat fungsi mapper 'fromJson'
        // Ini akan menggantikan 'fromStock' yang error.
        fun fromStockJson(json: FoodStockJson?): FoodStock? {
            if (json?.id == null || json.userId == null || json.name == null) {
                return null // Return null jika data wajib tidak ada
            }
            return FoodStock(
                id = json.id,
                userId = json.userId,
                ingredientId = json.ingredientId ?: 0,
                name = json.name,
                imageUrl = json.image,
                quantity = json.quantity ?: 0f,
                unit = json.unit ?: "",
                expiredDate = json.expiredDate?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) },
                startRemindDate = json.startRemindDate?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
            )
        }
    }
}


