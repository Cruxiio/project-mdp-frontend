package com.example.nutrisaver.data.sources.remote

import com.example.nutrisaver.data.sources.remote.auth.UserJson
import com.example.nutrisaver.data.sources.remote.common.AllergenGetAllResponse
import com.example.nutrisaver.data.sources.remote.common.DailyConsumptionJson
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Webservice {
    // common api (api buat data yang digunakan pilihan input kyk selectbox)
    // api ini bisa diakses tanpa auth token
    @GET("api/common/allergen")
    suspend fun getAllergen(@Query("keyword") keyword:String): AllergenGetAllResponse

    //===========================================================================
    // auth api
    @POST("api/auth/register")
    suspend fun register(@Body body:UserJson):UserJson

    @GET("api/auth/user")
    suspend fun getUser(@Query("uuid") uuid:String): UserJson

    @GET("api/user/profile/{userId}")
    suspend fun getUserProfile(
        @Header("Authorization") token: String,
        @Path("userId") userId: String // <-- Parameter baru untuk mengisi {userId} di URL
    ): UserJson

    @GET("api/consumption/today") // Sesuaikan jika path berbeda
    suspend fun getTodaysConsumption(@Header("Authorization") token: String): DailyConsumptionJson
}