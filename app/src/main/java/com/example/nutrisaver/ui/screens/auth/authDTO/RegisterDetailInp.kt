package com.example.nutrisaver.ui.screens.auth.authDTO

import com.example.nutrisaver.data.model.Allergen

data class RegisterDetailInp(
    val name : String, val gender : String, val dateOfBirth:String,
    val weight:Int, val height:Int, val goalOption:String, val dietTypeOption : String, val targetWeight:Int,
    val protein: Float, val carbs: Float, val fat: Float,
    val allergen: List<Allergen>,
) {
    fun checkKosong(): String {
        if (name.isEmpty() || gender.isEmpty() || dateOfBirth.isEmpty() || weight == 0
            || height == 0 || goalOption.isEmpty() || targetWeight == 0 || protein.toDouble() == 0.0
            || carbs.toDouble() == 0.0 || fat.toDouble() == 0.0 || dietTypeOption.isEmpty()) {
            return "Semua input harus diisi!"
        }
        return ""
    }
}