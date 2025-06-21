package com.example.nutrisaver

//import com.example.nutrisaver.Build

import android.app.Application
import com.example.nutrisaver.data.repositories.AuthRepo
import com.example.nutrisaver.data.repositories.AuthRepoImpl
import com.example.nutrisaver.data.repositories.CommonRepo
import com.example.nutrisaver.data.repositories.CommonRepoImpl
import com.example.nutrisaver.data.sources.AppDatabase
import com.example.nutrisaver.data.sources.local.CommonLocalDataSourceImpl
import com.example.nutrisaver.data.sources.local.auth.AuthLocalDataSourceImpl
import com.example.nutrisaver.data.sources.remote.Webservice
import com.example.nutrisaver.data.sources.remote.auth.AuthDataSourceImpl
import com.example.nutrisaver.data.sources.remote.common.CommonDataSourceImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class NutriSaverApplication : Application() {

    // Definisikan semua dependensi yang akan kita buat
    // Ini adalah bentuk sederhana dari "Service Locator" atau "Manual Dependency Injection"
    lateinit var commonRepo: CommonRepo
    lateinit var authRepo: AuthRepo

    override fun onCreate() {
        super.onCreate()

        // Inisialisasi Database Lokal TERLEBIH DAHULU ===
        val database = AppDatabase.getDatabase(this)

        // Buat instance untuk semua Local Data Source ===
        // Mereka membutuhkan DAO dari database
        val authLocalDataSource = AuthLocalDataSourceImpl(database.userDao())
        val commonLocalDataSource = CommonLocalDataSourceImpl(database.allergenDao())

        //Inisialisasi Dependensi Remote (Retrofit, Moshi) ===
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("http://10.0.2.2:3000/") // Pastikan IP ini benar untuk emulator Anda
            .build()
        val retrofitService = retrofit.create(Webservice::class.java)

        //Buat instance untuk semua Remote Data Source ===
        val authRemoteDataSource = AuthDataSourceImpl(retrofitService)
        val commonRemoteDataSource = CommonDataSourceImpl(retrofitService)

        // Inisialisasi Repositories dengan SEMUA dependensinya ===
        // Sekarang kita memberikan dependensi remote DAN lokal
        commonRepo = CommonRepoImpl(
            commonDataSource = commonRemoteDataSource,
            commonLocalDataSource = commonLocalDataSource
        )
        authRepo = AuthRepoImpl(
            authDataSource = authRemoteDataSource,
            authLocalDataSource = authLocalDataSource
        )
    }
}