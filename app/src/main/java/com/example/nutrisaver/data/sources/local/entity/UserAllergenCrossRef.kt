package com.example.nutrisaver.data.sources.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "user_allergen_cross_ref",
    primaryKeys = ["uuid", "allergenId"],
    indices = [Index(value = ["allergenId"])]
)
data class UserAllergenCrossRef(
    // Anotasi @PrimaryKey dihapus dari sini
    val uuid: String,
    val allergenId: Int
)
