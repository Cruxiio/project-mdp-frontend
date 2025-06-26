package com.example.nutrisaver.data.sources.remote.common

import android.util.Log
import com.example.nutrisaver.data.model.Allergen
import com.example.nutrisaver.data.sources.remote.Webservice

class CommonDataSourceImpl(
    private val webservice: Webservice
): CommonDataSource {
    override suspend fun getAllergen(keyword: String): List<Allergen> {
        Log.d("DATASOURCE_DEBUG", "1. Memanggil webservice.getAlergen...")
        try {
            // Panggil webservice seperti biasa
            val alergenDataResponse = webservice.getAllergen(keyword)

            // Log #2: Tampilkan hasil parsing mentah dari Moshi. INI LOG PALING PENTING.
            Log.d("DATASOURCE_DEBUG", "2. Hasil parsing Moshi: $alergenDataResponse")

            // Log #3: Cek list di dalam objek response
            val listOfAlergenJson = alergenDataResponse.allergen
            Log.d("DATASOURCE_DEBUG", "3. List AlergenJson diekstrak. Apakah null? ${listOfAlergenJson == null}")

            if (listOfAlergenJson != null) {
                Log.d("DATASOURCE_DEBUG", "4. Ukuran list AlergenJson: ${listOfAlergenJson.size}")
            }

            // Lakukan mapping
            val finalResult = (listOfAlergenJson ?: emptyList()).mapNotNull {
                Allergen.fromAllergenJson(it)
            }
            Log.d("DATASOURCE_DEBUG", "5. Hasil akhir setelah mapNotNull. Ukuran: ${finalResult.size}")

            return finalResult
        } catch (e: Exception) {
            Log.e("DATASOURCE_DEBUG", "TERJADI ERROR DI DALAM DATASOURCE", e)
            return emptyList() // Kembalikan list kosong jika ada error
        }
    }
}