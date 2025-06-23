package com.example.nutrisaver.data.sources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutrisaver.data.model.Allergen
import com.example.nutrisaver.data.model.NutritionNeeds
import com.example.nutrisaver.data.model.User
import java.time.Instant

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var uuid: String,
    var role: String,
    var name: String,
    var username: String,
    var email: String,
    var dateOfBirth: String,
    var gender: String,
    var weight: Int,
    var height: Int,
    var profilePicture: String?,
    var goal: String,
    var targetWeight: Int,
    var dietType: String,
    var proteinRatio: Float,
    var carbsRatio: Float,
    var fatRatio: Float,
    // Data nutrisi disimpan sebagai kolom terpisah (embedded)
    var calories: Int?,
    var proteinGrams: Int?,
    var carbsGrams: Int?,
    var fatGrams: Int?,
    var waterMl: Int?,
    // Timestamps
    var createdAt: Long?,
    var updatedAt: Long?,
    var deletedAt: Long?
) {
    companion object {
        // Mapper dari User (domain) ke UserEntity (database)
        fun fromUser(user: User): UserEntity = UserEntity(
            id = user.id ?: 0,
            uuid = user.uuid,
            role = user.role,
            name = user.name,
            username = user.username,
            email = user.email,
            dateOfBirth = user.dateOfBirth,
            gender = user.gender,
            weight = user.weight,
            height = user.height,
            profilePicture = user.profilePicture,
            goal = user.goal,
            targetWeight = user.targetWeight,
            dietType = user.dietType,
            proteinRatio = user.proteinRatio,
            carbsRatio = user.carbsRatio,
            fatRatio = user.fatRatio,
            calories = user.nutritionNeeds?.calories,
            proteinGrams = user.nutritionNeeds?.protein,
            carbsGrams = user.nutritionNeeds?.carbs,
            fatGrams = user.nutritionNeeds?.fat,
            waterMl = user.nutritionNeeds?.water,
            createdAt = user.createdAt?.toEpochMilli(),
            updatedAt = user.updatedAt?.toEpochMilli(),
            deletedAt = user.deletedAt?.toEpochMilli()
        )
    }
}

// Mapper dari UserEntity (database) ke User (domain)
fun UserEntity.toUser(allergens: List<Allergen>): User {
    val nutritionData = if (calories != null && proteinGrams != null && carbsGrams != null && fatGrams != null && waterMl != null) {
        NutritionNeeds(calories!!, proteinGrams!!, carbsGrams!!, fatGrams!!, waterMl!!)
    } else {
        null
    }
    return User(
        id = this.id,
        uuid = this.uuid,
        role = this.role,
        name = this.name,
        username = this.username,
        email = this.email,
        dateOfBirth = this.dateOfBirth,
        gender = this.gender,
        weight = this.weight,
        height = this.height,
        profilePicture = this.profilePicture,
        goal = this.goal,
        targetWeight = this.targetWeight,
        dietType = this.dietType,
        proteinRatio = this.proteinRatio,
        carbsRatio = this.carbsRatio,
        fatRatio = this.fatRatio,
        allergen = allergens,
        nutritionNeeds = nutritionData,
        firebaseToken = null, // Token tidak pernah disimpan di database
        createdAt = this.createdAt?.let { Instant.ofEpochMilli(it) },
        updatedAt = this.updatedAt?.let { Instant.ofEpochMilli(it) },
        deletedAt = this.deletedAt?.let { Instant.ofEpochMilli(it) }
    )
}