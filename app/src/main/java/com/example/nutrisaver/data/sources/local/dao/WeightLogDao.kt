package com.example.nutrisaver.data.sources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.nutrisaver.data.sources.local.entity.WeightLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightLogDao {
    @Query("SELECT * FROM user_weight ORDER BY date ASC")
    fun getWeightHistory(): Flow<List<WeightLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(weights: List<WeightLogEntity>)

    @Query("DELETE FROM user_weight")
    suspend fun clearAll()

    @Transaction
    suspend fun clearAndInsertAll(weights: List<WeightLogEntity>) {
        clearAll()
        insertAll(weights)
    }
}