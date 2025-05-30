package com.example.nutrisaver.data.sources.remote.auth

import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class UserJson (
    var id: Int,
    var uuid: String,
    var role: String,
    var username: String,
    var email: String,
    var password: String,
    var name : String,
    val gender : String,
    val dateOfBirth:String,
    val weight:Int,
    val height:Int,
    val goalOption:String,
    val dietTypeOption : String,
    val targetWeight:Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val profilePicture: String?, // Bisa null
    val alergen: List<String>,
    val createdAt: Long = Date().time,
    var updatedAt: Long = Date().time,
    var deletedAt: Long? = null
){
}