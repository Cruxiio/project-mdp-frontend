package com.example.nutrisaver.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.nutrisaver.data.sources.local.entity.AllergenEntity
import com.example.nutrisaver.data.sources.local.entity.UserAllergenCrossRef
import com.example.nutrisaver.data.sources.local.entity.UserEntity

@Dao
interface UserDao {

    // === Metode untuk mengambil data (Fetch) ===

    @Query("SELECT * FROM user WHERE uuid = :uuid LIMIT 1")
    suspend fun getUserByUuid(uuid: String): UserEntity?

    @Query("SELECT allergenId FROM user_allergen_cross_ref WHERE uuid = :uuid")
    suspend fun getAllergenIdsForUser(uuid: String): List<Int>

    @Query("SELECT * FROM allergen WHERE allergenId IN (:ids)")
    suspend fun getAllergensByIds(ids: List<Int>): List<AllergenEntity>


    // === Metode untuk menyimpan data (Save) ===
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllergens(allergens: List<AllergenEntity>)

    @Query("DELETE FROM user_allergen_cross_ref WHERE uuid = :uuid")
    suspend fun deleteCrossRefsForUser(uuid: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(crossRefs: List<UserAllergenCrossRef>)
}