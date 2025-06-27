package com.example.nutrisaver.data.sources.remote.common

import com.example.nutrisaver.data.model.Allergen

interface CommonDataSource {
    suspend fun getAllergen(keyword:String): List<Allergen>
}