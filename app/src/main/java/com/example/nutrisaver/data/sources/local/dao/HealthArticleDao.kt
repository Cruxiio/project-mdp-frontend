package com.example.nutrisaver.data.sources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nutrisaver.data.sources.local.entity.HealthArticleEntity

@Dao
interface HealthArticleDao {
    @Query("SELECT * FROM health_articles ORDER BY created_at DESC")
    suspend fun getAllArticles(): List<HealthArticleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<HealthArticleEntity>)

    @Query("DELETE FROM health_articles")
    suspend fun clearAll()
}