package com.example.nutrisaver.ui.screens.auth.authDTO

data class RegisterDetailInp(
    val name : String, val gender : String, val dateOfBirth:String,
    val weight:Int, val height:Int, val goalOption:String, val targetWeight:Int,
    val protein: Int, val carbs: Int, val fat: Int,
    val alergen: List<String>,
) {
    fun checkKosong(): String {
        if (name.isEmpty() || gender.isEmpty() || dateOfBirth.isEmpty() || weight == 0
            || height == 0 || goalOption.isEmpty() || targetWeight == 0 || protein == 0
            || carbs == 0 || fat == 0 || alergen.isEmpty()) {
            return "Semua input harus diisi!"
        }
        return ""
    }
}