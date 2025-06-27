    package com.example.nutrisaver.data.sources.local.entity

    import androidx.room.Entity
    import androidx.room.PrimaryKey
    import com.example.nutrisaver.data.model.DailyConsumptionDetail

    @Entity (tableName = "daily_consumption_detail")
    data class DailyConsumptionDetailEntity (
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val serverId: Int?,
        val consumptionDate: String, // Foreign key yang menunjuk ke DailyConsumptionEntity.date
        val mealType: String,
        val foodName: String,
        val quantity: Float,
        val unit: String,
        val calories: Float,
        val carbs: Float,
        val protein: Float,
        val fat: Float
    ) {
        fun toDailyDetail(): DailyConsumptionDetail {
            return DailyConsumptionDetail(id = this.serverId, mealType, foodName, quantity, unit, calories, carbs, protein, fat)
        }

        companion object {
            fun fromDailyDetail(domain: DailyConsumptionDetail, date: String): DailyConsumptionDetailEntity {
                return DailyConsumptionDetailEntity(
                    serverId = domain.id,
                    consumptionDate = date,
                    mealType = domain.mealType,
                    foodName = domain.foodName,
                    quantity = domain.quantity,
                    unit = domain.unit,
                    calories = domain.calories,
                    carbs = domain.carbs,
                    protein = domain.protein,
                    fat = domain.fat
                )
            }
        }
    }