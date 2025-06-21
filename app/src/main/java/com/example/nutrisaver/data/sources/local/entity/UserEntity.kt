package com.example.nutrisaver.data.sources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutrisaver.data.model.Allergen
import com.example.nutrisaver.data.model.User
import java.time.Instant

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    var uuid: String,
    var id: Int?,
    var username: String,
    var email: String,
    var name: String,
    var gender: String,
    var dateOfBirth: String,
    var weight: Int,
    var height: Int,
    var goalOption: String,
    var dietTypeOption: String,
    var targetWeight: Int,
    var protein: Float,
    var carbs: Float,
    var fat: Float,
    var profilePicture: String?,
    var createdAt: Long,
    var updatedAt: Long,
    var deletedAt: Long?
) {
    companion object {
        // Mengubah Model Domain (User) menjadi Entity (UserEntity)
        fun fromUser(user: User): UserEntity = UserEntity(
            uuid = user.uuid,
            id = user.id,
            username = user.username,
            email = user.email,
            name = user.name,
            gender = user.gender,
            dateOfBirth = user.dateOfBirth,
            weight = user.weight,
            height = user.height,
            goalOption = user.goalOption,
            dietTypeOption = user.dietTypeOption,
            targetWeight = user.targetWeight,
            protein = user.protein,
            carbs = user.carbs,
            fat = user.fat,
            profilePicture = user.profilePicture,
            createdAt = user.createdAt.toEpochMilli(),
            updatedAt = user.updatedAt.toEpochMilli(),
            deletedAt = user.deletedAt?.toEpochMilli()
        )
    }
}

// Fungsi ekstensi untuk mengubah Entity (UserEntity) menjadi Model Domain (User)
// Didefinisikan di luar kelas karena membutuhkan List<Alergen> sebagai parameter tambahan
fun UserEntity.toUser(allergens: List<Allergen>): User = User(
    uuid = this.uuid,
    id = this.id,
    username = this.username,
    email = this.email,
    name = this.name,
    gender = this.gender,
    dateOfBirth = this.dateOfBirth,
    weight = this.weight,
    height = this.height,
    goalOption = this.goalOption,
    dietTypeOption = this.dietTypeOption,
    targetWeight = this.targetWeight,
    protein = this.protein,
    carbs = this.carbs,
    fat = this.fat,
    profilePicture = this.profilePicture,
    allergen = allergens,
    createdAt = Instant.ofEpochMilli(this.createdAt),
    updatedAt = Instant.ofEpochMilli(this.updatedAt),
    deletedAt = this.deletedAt?.let { Instant.ofEpochMilli(it) }
)