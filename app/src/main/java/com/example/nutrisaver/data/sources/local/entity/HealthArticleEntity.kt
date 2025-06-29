package com.example.nutrisaver.data.sources.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "health_articles")
data class HealthArticleEntity (
    @PrimaryKey
    val id: Int,
    val title: String,
    val content: String,
    @ColumnInfo(name = "target_goal")
    val targetGoal: String,
    @ColumnInfo(name = "target_diet_type")
    val targetDietType: String,
    @ColumnInfo(name = "created_by")
    val createdBy: String,
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime

)