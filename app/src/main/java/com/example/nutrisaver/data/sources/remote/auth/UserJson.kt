package com.example.nutrisaver.data.sources.remote.auth

import com.example.nutrisaver.data.sources.remote.common.AllergenJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class UserJson (
    @Json(name = "id") var id: Int?, // ini waktu jadi input lempar di null atau diisi -1
    @Json(name = "uuid") var uuid: String,
    @Json(name = "username") var username: String,
    @Json(name = "email") var email: String,
    @Json(name = "name") var name : String,
    @Json(name = "gender") var gender : String,
    @Json(name = "date_of_birth") var dateOfBirth:String,
    @Json(name = "weight") var weight:Int,
    @Json(name = "height") var height:Int,
    @Json(name = "goal") var goalOption:String,
    @Json(name = "diet_type") var dietTypeOption : String,
    @Json(name = "target_weight") var targetWeight:Int,
    @Json(name = "protein_ratio") var protein: Float,
    @Json(name = "carbs_ratio") var carbs: Float,
    @Json(name = "fat_ratio") var fat: Float,
    @Json(name = "profile_picture") var profilePicture: String?, // Bisa null
    @Json(name = "allergen") var allergen: List<AllergenJson>,
    @Json(name = "createdAt") var createdAt: String,
    @Json(name = "updatedAt") var updatedAt: String,
    @Json(name = "deletedAt") var deletedAt: String? = null
){
}