package com.example.nutrisaver.data.sources.remote.auth

import java.util.Date

data class User (
    var id: Int,
    var uuid: String,
    var role: String,
    var username: String,
    var email: String,
    var password: String,
    var name : String, var gender : String, var dateOfBirth:String,
    var weight:Int, var height:Int, var goalOption:String, var dietTypeOption : String, var targetWeight:Int,
    var protein: Float, var carbs: Float, var fat: Float,
    var profilePicture: String?, // Bisa null
    var alergen: List<String>,
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var deletedAt: Date? = null
){
    companion object{
        fun fromUserJson(t:UserJson) =
            User(t.id, t.uuid, t.role, t.username, t.email, t.password,
                t.name, t.gender, t.dateOfBirth, t.weight, t.height, t.goalOption, t.dietTypeOption, t.targetWeight,
                t.protein, t.carbs, t.fat, t.profilePicture ,t.alergen,
                Date(t.createdAt), Date(t.updatedAt),
                if(t.deletedAt == null){
                    null
                } else {
                    Date(t.createdAt)
                })
    }

    fun toUserJson() = UserJson(
        id, uuid, role, username, email, password, name, gender, dateOfBirth,
        weight, height, goalOption, dietTypeOption, targetWeight,
        protein, carbs, fat, profilePicture, alergen,
        createdAt.time, updatedAt.time, deletedAt?.time
    )
}