package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.common.FoodStockJson
import java.time.LocalDate
import java.time.OffsetDateTime // <-- Tambahkan import ini
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
        fun fromStockJson(json: FoodStockJson?): FoodStock? {
            if (json?.id == null || json.userId == null || json.name == null) {
                return null // Return null jika data wajib tidak ada
            }

            // PERBAIKAN: Gunakan OffsetDateTime untuk parsing format tanggal dari server,
            // lalu konversi ke LocalDate.
//            val expiredDate = json.expiredDate?.let {
//                OffsetDateTime.parse(it).toLocalDate()
//            }
//            val startRemindDate = json.startRemindDate?.let {
//                OffsetDateTime.parse(it).toLocalDate()
//            }
//
            val expiredDate = json.expiredDate?.let {
                LocalDate.parse(it)
            }
            val startRemindDate = json.startRemindDate?.let {
                LocalDate.parse(it)
            }

            return FoodStock(
                id = json.id,
                userId = json.userId,
                ingredientId = json.ingredientId ?: 0,
                name = json.name,
                imageUrl = json.image,
                quantity = json.quantity ?: 0f,
                unit = json.unit ?: "",
                expiredDate = expiredDate,
                startRemindDate = startRemindDate
            )
        }
    }
}