package com.example.nutrisaver.data.sources.remote.common

import com.example.nutrisaver.data.model.Allergen
import com.example.nutrisaver.data.sources.remote.Webservice

class CommonDataSourceImpl(
    private val webservice: Webservice
): CommonDataSource {
    override suspend fun getAllergen(keyword: String): List<Allergen> {
        // ambil data dari Backend
        val alergenData = webservice.getAllergen(keyword)
        // konvert ke list class alergen yang bisa dipakai di kotlin
        return alergenData.alergen.map { Allergen.fromAllergenJson(it) }
    }
}