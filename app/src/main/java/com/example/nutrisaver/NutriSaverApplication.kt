package com.example.nutrisaver

//import com.example.nutrisaver.Build

import android.app.Application
import com.example.nutrisaver.data.repositories.AuthRepo
import com.example.nutrisaver.data.repositories.AuthRepoImpl
import com.example.nutrisaver.data.repositories.CommonRepo
import com.example.nutrisaver.data.repositories.CommonRepoImpl
import com.example.nutrisaver.data.sources.remote.Webservice
import com.example.nutrisaver.data.sources.remote.auth.AuthDataSourceImpl
import com.example.nutrisaver.data.sources.remote.common.CommonDataSourceImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class NutriSaverApplication : Application() {

    //define repo
    lateinit var commonRepo: CommonRepo
    lateinit var authRepo: AuthRepo

    override fun onCreate() {
        super.onCreate()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder().addConverterFactory(
            MoshiConverterFactory.create(moshi)
        ).baseUrl("http://10.0.2.2:3000/").build()
        val retrofitService = retrofit.create(Webservice::class.java)

        // isi repo
        commonRepo = CommonRepoImpl(
            CommonDataSourceImpl(retrofitService)
        )
        authRepo = AuthRepoImpl(
            AuthDataSourceImpl(retrofitService)
        )
    }
}