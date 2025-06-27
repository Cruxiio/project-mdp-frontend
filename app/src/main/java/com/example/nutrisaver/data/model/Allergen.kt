package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.common.AllergenJson

data class Allergen (
    val id: Int?, // ini waktu jadi input lempar di null atau diisi -1
    val name: String,
) {
    companion object{
        fun fromAllergenJson(a: AllergenJson) =
            Allergen(a.id, a.name)
    }
    fun toAllergenJson() = AllergenJson(id,name)
}