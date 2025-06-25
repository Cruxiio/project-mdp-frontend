package com.example.nutrisaver.data.sources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutrisaver.data.model.FoodStock
import java.time.LocalDate

@Entity(tableName = "food_stock")
data class FoodStockEntity(
    @PrimaryKey val id: Int, // ID dari server
    val userId: Int,
    val ingredientId: Int,
    val name: String,
    val imageUrl: String?,
    val quantity: Float,
    val unit: String,
    val expiredDate: String?, // Simpan sebagai String YYYY-MM-DD
    val startRemindDate: String?
) {
    fun toStock(): FoodStock {
        return FoodStock(
            id = this.id,
            userId = this.userId,
            ingredientId = this.ingredientId,
            name = this.name,
            imageUrl = this.imageUrl,
            quantity = this.quantity,
            unit = this.unit,
            expiredDate = this.expiredDate?.let { LocalDate.parse(it) },
            startRemindDate = this.startRemindDate?.let { LocalDate.parse(it) }
        )
    }

    companion object {
        fun fromStock(domain: FoodStock): FoodStockEntity {
            return FoodStockEntity(
                id = domain.id ?: 0, // Jika id null, Room akan auto-generate jika PK autoGenerate=true
                userId = domain.userId,
                ingredientId = domain.ingredientId,
                name = domain.name,
                imageUrl = domain.imageUrl,
                quantity = domain.quantity,
                unit = domain.unit,
                expiredDate = domain.expiredDate?.toString(),
                startRemindDate = domain.startRemindDate?.toString()
            )
        }
    }
}