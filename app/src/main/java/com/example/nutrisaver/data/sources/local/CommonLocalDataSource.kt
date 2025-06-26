package com.example.nutrisaver.data.sources.local

import com.example.nutrisaver.data.model.Allergen
import com.example.nutrisaver.data.sources.local.dao.AllergenDao
import com.example.nutrisaver.data.sources.local.entity.AllergenEntity

interface CommonLocalDataSource {
    suspend fun getAllergen(keyword: String): List<Allergen>
    suspend fun saveAllergens(alergens: List<Allergen>)
}

class CommonLocalDataSourceImpl(
    private val allergenDao: AllergenDao
) : CommonLocalDataSource {

    override suspend fun getAllergen(keyword: String): List<Allergen> {
        return allergenDao.getAllergensByKeyword(keyword).map { it.toAllergen() }
    }

    override suspend fun saveAllergens(allergens: List<Allergen>) {
        val allergenEntities = allergens.map { AllergenEntity.fromAllergen(it) }
        allergenDao.insertAllergens(allergenEntities)
    }
}