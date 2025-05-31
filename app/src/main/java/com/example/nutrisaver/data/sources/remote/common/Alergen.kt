package com.example.nutrisaver.data.sources.remote.common

data class Alergen (
    val id: Int?, // ini waktu jadi input lempar di null atau diisi -1
    val name: String,
) {
    companion object{
        fun fromAlergenJson(a: AlergenJson) =
            Alergen(a.id, a.name)
    }
    fun toAlergenJson() = AlergenJson(id,name)
}