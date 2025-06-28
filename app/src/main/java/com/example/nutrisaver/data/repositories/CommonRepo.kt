package com.example.nutrisaver.data.repositories

import com.example.nutrisaver.data.model.Allergen

interface CommonRepo {
    suspend fun getAllergen(keyword:String): List<Allergen>
    suspend fun getEveryAllegen(keyword: String): List<Allergen>
}