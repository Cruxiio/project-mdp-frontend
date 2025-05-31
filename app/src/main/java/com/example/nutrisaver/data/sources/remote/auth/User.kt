package com.example.nutrisaver.data.sources.remote.auth

import android.os.Build
import com.example.nutrisaver.data.sources.remote.common.Alergen
import java.time.Instant
import java.util.Date

data class User (
    var id: Int?, // bisa null atau diisi -1
    var uuid: String,
    var role: String,
    var username: String,
    var email: String,
    var name : String, var gender : String, var dateOfBirth:String,
    var weight:Int, var height:Int, var goalOption:String, var dietTypeOption : String, var targetWeight:Int,
    var protein: Float, var carbs: Float, var fat: Float,
    var profilePicture: String?, // Bisa null
    var allergen: List<Alergen>,
    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
    var deletedAt: Instant? = null// Bisa null
){
    companion object{
        fun fromUserJson(t:UserJson) =  User(t.id, t.uuid, t.role, t.username, t.email,
                t.name, t.gender, t.dateOfBirth, t.weight, t.height, t.goalOption, t.dietTypeOption, t.targetWeight,
                t.protein, t.carbs, t.fat, t.profilePicture , t.allergen.map { Alergen.fromAlergenJson(it) },
                Instant.parse(t.createdAt), Instant.parse(t.updatedAt), t.deletedAt?.let { Instant.parse(it) })

    }

    fun toUserJson() = UserJson(
        id, uuid, role, username, email, name, gender, dateOfBirth,
        weight, height, goalOption, dietTypeOption, targetWeight,
        protein, carbs, fat, profilePicture, allergen.map { it.toAlergenJson() },
        createdAt.toString(), updatedAt.toString(), deletedAt?.toString()
    )
}