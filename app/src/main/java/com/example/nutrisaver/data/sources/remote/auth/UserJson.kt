package com.example.nutrisaver.data.sources.remote.auth

import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.sources.remote.common.AllergenJson
import com.example.nutrisaver.data.sources.remote.common.NutritionNeedsJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class UserJson(
    @Json(name = "id") val id: Int?,
    @Json(name = "uuid") val uuid: String?,
    @Json(name = "role") val role : String?,
    @Json(name = "name") val name: String?,
    @Json(name = "username") val username: String?,
    @Json(name = "email") val email: String?,
    @Json(name = "date_of_birth") val date_of_birth: String?,
    @Json(name = "gender") val gender: String?,
    @Json(name = "weight") val weight: Int?,
    @Json(name = "height") val height: Int?,
    @Json(name = "profile_picture") val profile_picture: String?,
    @Json(name = "goal") val goal: String?,
    @Json(name = "target_weight") val target_weight: Int?,
    @Json(name = "diet_type") val diet_type: String?,
    @Json(name = "protein_ratio") val protein_ratio: Float?,
    @Json(name = "carbs_ratio") val carbs_ratio: Float?,
    @Json(name = "fat_ratio") val fat_ratio: Float?,
    @Json(name = "allergen") val allergen: List<AllergenJson>?,
    @Json(name = "nutrition_needs") val nutrition_needs: NutritionNeedsJson?,
    @Json(name = "firebase_token") val firebase_token: String?,
    @Json(name = "createdAt") val createdAt: String?,
    @Json(name = "updatedAt") val updatedAt: String?,
    @Json(name = "deletedAt") val deletedAt: String?
){
}