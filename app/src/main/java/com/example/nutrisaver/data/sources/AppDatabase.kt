package com.example.nutrisaver.data.sources

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nutrisaver.data.sources.local.dao.AllergenDao
import com.example.nutrisaver.data.sources.local.dao.ConsumptionDao
import com.example.nutrisaver.data.sources.local.dao.FoodStockDao
import com.example.nutrisaver.data.sources.local.dao.IngredientDao
import com.example.nutrisaver.data.sources.local.entity.UserEntity
import com.example.nutrisaver.data.sources.local.dao.UserDao
import com.example.nutrisaver.data.sources.local.entity.AllergenEntity
import com.example.nutrisaver.data.sources.local.entity.DailyConsumptionDetailEntity
import com.example.nutrisaver.data.sources.local.entity.DailyConsumptionEntity
import com.example.nutrisaver.data.sources.local.entity.FoodStockEntity
import com.example.nutrisaver.data.sources.local.entity.IngredientEntity
import com.example.nutrisaver.data.sources.local.entity.UserAllergenCrossRef

@Database(
    entities = [UserEntity::class, AllergenEntity::class, UserAllergenCrossRef::class,
        DailyConsumptionEntity::class, IngredientEntity :: class, FoodStockEntity::class,
               DailyConsumptionDetailEntity::class],
    version = 8, // Mulai dari 1. Naikkan jika ada perubahan skema
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun allergenDao(): AllergenDao
    abstract fun consumptionDao(): ConsumptionDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun foodStockDao(): FoodStockDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, 
                    AppDatabase::class.java,
                    "nutrisaver"
                )
                    // Sebaiknya gunakan migration di produksi, tapi ini cukup untuk development
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}