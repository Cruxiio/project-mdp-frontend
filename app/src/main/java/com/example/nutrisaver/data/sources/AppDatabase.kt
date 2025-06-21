package com.example.nutrisaver.data.sources

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nutrisaver.data.sources.local.dao.AllergenDao
import com.example.nutrisaver.data.sources.local.entity.UserEntity
import com.example.nutrisaver.data.sources.local.dao.UserDao
import com.example.nutrisaver.data.sources.local.entity.AllergenEntity
import com.example.nutrisaver.data.sources.local.entity.UserAllergenCrossRef

@Database(
    entities = [UserEntity::class, AllergenEntity::class, UserAllergenCrossRef::class],
    version = 2, // Mulai dari 1. Naikkan jika ada perubahan skema
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun allergenDao(): AllergenDao

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