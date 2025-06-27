package com.example.nutrisaver.data.repositories

import com.example.nutrisaver.data.model.Allergen
import com.example.nutrisaver.data.sources.local.CommonLocalDataSource
import com.example.nutrisaver.data.sources.remote.common.CommonDataSource

class CommonRepoImpl(
    private val commonDataSource: CommonDataSource,
    private val commonLocalDataSource: CommonLocalDataSource
): CommonRepo {
    override suspend fun getAllergen(keyword: String): List<Allergen> {
        // 1. Coba cari di database lokal dulu
        val localAllergens = commonLocalDataSource.getAllergen(keyword)
        if (localAllergens.isNotEmpty()) {
            // Jika ketemu, langsung kembalikan
            return localAllergens
        }

        // 2. Jika di lokal tidak ada/kosong, ambil dari remote API
        val remoteAllergens = commonDataSource.getAllergen(keyword)

        // 3. Simpan hasil dari remote ke database lokal
        // Ini akan memperkaya cache lokal kita untuk pencarian di masa depan
        commonLocalDataSource.saveAllergens(remoteAllergens)

        return remoteAllergens
    }
}
