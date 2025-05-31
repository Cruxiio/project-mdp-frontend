package com.example.nutrisaver.data.sources.remote.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class AlergenJson(
    @Json(name = "id") var id: Int?, // ini waktu jadi input lempar di null atau diisi -1
    @Json(name = "name") var name: String,
) {
}

// ini class buat nampung response dari backend
@JsonClass(generateAdapter = true)
data class AlergenGetAllResponse(
    @Json(name = "alergen") var alergen: List<AlergenJson>,
){
}