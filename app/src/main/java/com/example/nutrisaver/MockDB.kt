package com.example.nutrisaver

import android.os.Build
import java.time.format.DateTimeFormatter

class MockDB {
    companion object{
        var API_PREFIX = "api"

        // ini buat format date dari format 05/28/2024 ke format 2024-05-28
        fun dateFormater(date: String): String {
            val dates = date.split("/")

            return "${dates[2]}-${dates[0]}-${dates[1]}"
        }
    }
}