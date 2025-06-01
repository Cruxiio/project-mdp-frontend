package com.example.nutrisaver.data.sources.remote.common

interface CommonDataSource {
    suspend fun getAlergen(keyword:String): List<Alergen>
}