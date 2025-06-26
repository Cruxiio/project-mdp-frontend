package com.example.nutrisaver.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nutrisaver.data.sources.local.entity.AllergenEntity

@Dao
interface AllergenDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllergens(allergens: List<AllergenEntity>)

    @Query("SELECT * FROM allergen WHERE name LIKE '%' || :keyword || '%'")
    suspend fun getAllergensByKeyword(keyword: String): List<AllergenEntity>
}