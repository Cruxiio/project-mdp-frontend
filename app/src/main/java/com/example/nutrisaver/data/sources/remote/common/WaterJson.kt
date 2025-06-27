package com.example.nutrisaver.data.sources.remote.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WaterUpdateRequestJson(
    @Json(name = "water_ml") val water_ml: Int
)