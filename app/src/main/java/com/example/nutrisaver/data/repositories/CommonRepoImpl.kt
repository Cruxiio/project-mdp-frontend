package com.example.nutrisaver.data.repositories

import com.example.nutrisaver.data.sources.remote.common.Alergen
import com.example.nutrisaver.data.sources.remote.common.CommonDataSource

class CommonRepoImpl(
    private val commonDataSource: CommonDataSource,
): CommonRepo {
    override suspend fun getAlergen(keyword: String): List<Alergen> {
        return commonDataSource.getAlergen(keyword)
    }

}