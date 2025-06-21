package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.auth.UserJson
import java.time.Instant

data class User (
    var id: Int?, // bisa null atau diisi -1
    var uuid: String,
    var username: String,
    var email: String,
    var name : String, var gender : String, var dateOfBirth:String,
    var weight:Int, var height:Int, var goalOption:String, var dietTypeOption : String, var targetWeight:Int,
    var protein: Float, var carbs: Float, var fat: Float,
    var profilePicture: String?, // Bisa null
    var allergen: List<Allergen>,
    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
    var deletedAt: Instant? = null// Bisa null
){
    companion object{
        fun fromUserJson(t: UserJson) =  User(t.id, t.uuid, t.username, t.email,
            t.name, t.gender, t.dateOfBirth, t.weight, t.height, t.goalOption, t.dietTypeOption, t.targetWeight,
            t.protein, t.carbs, t.fat, t.profilePicture,
            t.allergen?.map { Allergen.fromAllergenJson(it) } ?: emptyList(),
            Instant.parse(t.createdAt), Instant.parse(t.updatedAt), t.deletedAt?.let { Instant.parse(it) })
    }

    fun toUserJson() = UserJson(
        id, uuid, username, email, name, gender, dateOfBirth,
        weight, height, goalOption, dietTypeOption, targetWeight,
        protein, carbs, fat, profilePicture, allergen.map { it.toAllergenJson() },
        createdAt.toString(), updatedAt.toString(), deletedAt?.toString()
    )
}