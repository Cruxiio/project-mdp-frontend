package com.example.nutrisaver.data.sources.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutrisaver.data.model.Allergen

@Entity(tableName = "allergen")
data class AllergenEntity(
    @PrimaryKey
    val allergenId: Int,
    val name: String
) {
    // Mengubah Entity menjadi Model Domain
    fun toAllergen(): Allergen = Allergen(id = allergenId, name = name)

    companion object {
        // Mengubah Model Domain menjadi Entity
        fun fromAllergen(allergen: Allergen): AllergenEntity {
            requireNotNull(allergen.id) { "Allergen ID cannot be null when saving to database" }
            return AllergenEntity(
                allergenId = allergen.id,
                name = allergen.name
            )
        }
    }
}