package com.example.nutrisaver.data.sources.remote.common

import com.example.nutrisaver.data.sources.remote.Webservice

class CommonDataSourceImpl(
    private val webservice: Webservice
): CommonDataSource {
    override suspend fun getAlergen(keyword: String): List<Alergen> {
        // ambil data dari Backend
        val alergenData = webservice.getAlergen(keyword)
        // konvert ke list class alergen yang bisa dipakai di kotlin
        return alergenData.alergen.map { Alergen.fromAlergenJson(it) }
    }
}