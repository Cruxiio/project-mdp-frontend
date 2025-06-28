package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.auth.UserJson
import java.time.Instant

data class User(
    var id: Int?,
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
    var allergen: List<Allergen>,
    var nutritionNeeds: NutritionNeeds?, // Objek domain untuk nutrisi, bisa null
    var firebaseToken: String?,
    var createdAt: Instant?,
    var updatedAt: Instant?,
    var deletedAt: Instant? = null
) {
    val absoluteProfilePictureUrl: String?
        get() = if (!profilePicture.isNullOrBlank()) {
            // IMPORTANT: Match this base URL to your server's address
            // This is the same URL you used in your Retrofit base URL, but WITHOUT the /api/
            // because /uploads is typically served directly from the root.
            "http://10.0.2.2:3000" + profilePicture
        } else {
            null
        }

    companion object {
        // Mapper dari UserJson (dari API) ke User (untuk aplikasi)
        fun fromUserJson(json: UserJson): User? {
            // Validasi field wajib ada agar tidak crash
            if (json.uuid == null || json.name == null || json.username == null || json.email == null) {
                return null
            }
            return User(
                id = json.id,
                uuid = json.uuid,
                role = "user",
                name = json.name,
                username = json.username,
                email = json.email,
                dateOfBirth = json.date_of_birth ?: "",
                gender = json.gender ?: "",
                weight = json.weight ?: 0,
                height = json.height ?: 0,
                profilePicture = json.profile_picture,
                goal = json.goal ?: "",
                targetWeight = json.target_weight ?: 0,
                dietType = json.diet_type ?: "",
                proteinRatio = json.protein_ratio ?: 0f,
                carbsRatio = json.carbs_ratio ?: 0f,
                fatRatio = json.fat_ratio ?: 0f,
                allergen = json.allergen?.mapNotNull { Allergen.fromAllergenJson(it) } ?: emptyList(),
                nutritionNeeds = json.nutrition_needs?.let { NutritionNeeds.fromNutritionNeedsJson(it) },
                firebaseToken = json.firebase_token,
                createdAt = json.createdAt?.let { Instant.parse(it) },
                updatedAt = json.updatedAt?.let { Instant.parse(it) },
                deletedAt = json.deletedAt?.let { Instant.parse(it) }
            )
        }
    }

    fun toUserJson() = UserJson(
        id = this.id,
        uuid = this.uuid,
        role = this.role,
        name = this.name,
        username = this.username,
        email = this.email,
        date_of_birth = this.dateOfBirth,
        gender = this.gender,
        weight = this.weight,
        height = this.height,
        profile_picture = this.profilePicture,
        goal = this.goal,
        target_weight = this.targetWeight,
        diet_type = this.dietType,
        protein_ratio = this.proteinRatio,
        carbs_ratio = this.carbsRatio,
        fat_ratio = this.fatRatio,
        allergen = this.allergen.map { it.toAllergenJson() },
        nutrition_needs = this.nutritionNeeds?.toNutritionNeedsJson(),
        firebase_token = this.firebaseToken,
        createdAt = this.createdAt?.toString(),
        updatedAt = this.updatedAt?.toString(),
        deletedAt = this.deletedAt?.toString()
    )
}