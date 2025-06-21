package com.example.nutrisaver.data.sources.remote

import com.example.nutrisaver.data.sources.remote.auth.UserJson
import com.example.nutrisaver.data.sources.remote.common.AllergenGetAllResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Webservice {
    // common api (api buat data yang digunakan pilihan input kyk selectbox)
    // api ini bisa diakses tanpa auth token
    @GET("api/common/alergen")
    suspend fun getAllergen(@Query("keyword") keyword:String): AllergenGetAllResponse

    //===========================================================================
    // auth api
    @POST("api/auth/register")
    suspend fun register(@Body body:UserJson):UserJson

    @GET("api/auth/user")
    suspend fun getUser(@Query("uuid") uuid:String): UserJson
}