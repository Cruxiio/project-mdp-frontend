package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.common.AllergenJson

data class Allergen (
    val id: Int?, // ini waktu jadi input lempar di null atau diisi -1
    val name: String,
) {
    companion object{
        fun fromAllergenJson(a: AllergenJson): Allergen? {
            if (a.id == null) { // Add name null check if needed
                return null // Cannot map to Allergen without an ID (or name)
            }
            return Allergen(a.id, a.name)
        }
    }
    fun toAllergenJson() = AllergenJson(id,name)
}