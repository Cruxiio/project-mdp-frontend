package com.example.nutrisaver.data.sources.remote

import com.example.nutrisaver.data.sources.remote.auth.UserJson
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Webservice {
    @POST("api/auth/register")
    suspend fun register(@Body body:UserJson):UserJson

    @GET("api/auth/user")
    suspend fun getUser(@Query("uuid") uuid:String): UserJson
}