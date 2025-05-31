package com.example.nutrisaver.data.repositories

import com.example.nutrisaver.data.sources.remote.common.Alergen

interface CommonRepo {
    suspend fun getAlergen(keyword:String): List<Alergen>
}