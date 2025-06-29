package com.example.nutrisaver

//import com.example.nutrisaver.Build

import android.app.Application
import com.example.nutrisaver.data.repositories.AdminRepo
import com.example.nutrisaver.data.repositories.AdminRepoImpl
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.example.nutrisaver.data.repositories.AuthRepo
import com.example.nutrisaver.data.repositories.AuthRepoImpl
import com.example.nutrisaver.data.repositories.CommonRepo
import com.example.nutrisaver.data.repositories.CommonRepoImpl
import com.example.nutrisaver.data.repositories.ConsumptionRepo
import com.example.nutrisaver.data.repositories.ConsumptionRepoImpl
import com.example.nutrisaver.data.repositories.FoodStockRepo
import com.example.nutrisaver.data.repositories.FoodStockRepoImpl
import com.example.nutrisaver.data.repositories.HealthArticleRepo
import com.example.nutrisaver.data.repositories.HealthArticleRepoImpl
import com.example.nutrisaver.data.repositories.IngredientRepo
import com.example.nutrisaver.data.repositories.IngredientRepoImpl
import com.example.nutrisaver.data.repositories.RecipeRepo
import com.example.nutrisaver.data.repositories.RecipeRepoImpl
import com.example.nutrisaver.data.repositories.WeightLogRepo
import com.example.nutrisaver.data.repositories.WeightLogRepoImpl
import com.example.nutrisaver.data.sources.AppDatabase
import com.example.nutrisaver.data.sources.local.CommonLocalDataSourceImpl
import com.example.nutrisaver.data.sources.local.ConsumptionLocalDataSourceImpl
import com.example.nutrisaver.data.sources.local.FoodStockLocalDataSourceImpl
import com.example.nutrisaver.data.sources.local.IngredientLocalDataSourceImpl
import com.example.nutrisaver.data.sources.local.WeightLogLocalDataSourceImpl
import com.example.nutrisaver.data.sources.local.auth.AuthLocalDataSourceImpl
import com.example.nutrisaver.data.sources.remote.HealthArticleDataSource
import com.example.nutrisaver.data.sources.remote.HealthArticleDataSourceImpl
import com.example.nutrisaver.data.sources.remote.Webservice
import com.example.nutrisaver.data.sources.remote.admin.AdminDataSource
import com.example.nutrisaver.data.sources.remote.admin.AdminDataSourceImpl
import com.example.nutrisaver.data.sources.remote.auth.AuthDataSourceImpl
import com.example.nutrisaver.data.sources.remote.common.CommonDataSourceImpl
import com.example.nutrisaver.data.sources.remote.common.ConsumptionDataSourceImpl
import com.example.nutrisaver.data.sources.remote.common.FoodStockDataSourceImpl
import com.example.nutrisaver.data.sources.remote.common.IngredientDataSourceImpl
import com.example.nutrisaver.data.sources.remote.common.RecipeDataSourceImpl
import com.example.nutrisaver.data.sources.remote.common.WeightLogDataSourceImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class NutriSaverApplication : Application() {

    // Definisikan semua dependensi yang akan kita buat
    // Ini adalah bentuk sederhana dari "Service Locator" atau "Manual Dependency Injection"
    lateinit var commonRepo: CommonRepo
    lateinit var authRepo: AuthRepo
    lateinit var consumRepo : ConsumptionRepo
    lateinit var ingredientRepo : IngredientRepo
    lateinit var foodStockRepo : FoodStockRepo
    lateinit var adminRepo: AdminRepo
    lateinit var recipeRepo : RecipeRepo
    lateinit var weightLogRepo: WeightLogRepo
    lateinit var healthArticleRepo: HealthArticleRepo

    override fun onCreate() {
        super.onCreate()

        // Inisialisasi Database Lokal TERLEBIH DAHULU ===
        val database = AppDatabase.getDatabase(this)

        // Buat instance untuk semua Local Data Source ===
        // Mereka membutuhkan DAO dari database
        val authLocalDataSource = AuthLocalDataSourceImpl(database.userDao())
        val commonLocalDataSource = CommonLocalDataSourceImpl(database.allergenDao())
        val consumptionLocalDataSource = ConsumptionLocalDataSourceImpl(database.consumptionDao())
        val ingredientLocalDataSource = IngredientLocalDataSourceImpl(database.ingredientDao())
        val foodStockLocalDataSource = FoodStockLocalDataSourceImpl(database.foodStockDao())
        val weightLogLocalDataSource = WeightLogLocalDataSourceImpl(database.weightLogDao())
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // BODY akan menampilkan semua detail response
        }

        // 2. Buat OkHttpClient dan tambahkan interceptor
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        //Inisialisasi Dependensi Remote (Retrofit, Moshi) ===
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("http://10.0.2.2:3000/")
            .client(okHttpClient)// Pastikan IP ini benar untuk emulator Anda
            .build()

        val retrofitService = retrofit.create(Webservice::class.java)

        //Buat instance untuk semua Remote Data Source ===
        val authRemoteDataSource = AuthDataSourceImpl(retrofitService, this)
        val commonRemoteDataSource = CommonDataSourceImpl(retrofitService)
        val consumptionRemoteDataSource = ConsumptionDataSourceImpl(retrofitService)
        val ingredientRemoteDataSource = IngredientDataSourceImpl(retrofitService)
        val foodStockRemoteDataSource = FoodStockDataSourceImpl(retrofitService)
        val adminDataSource = AdminDataSourceImpl(retrofitService)
        val recipeRemoteDataSource = RecipeDataSourceImpl(retrofitService)
        val weightLogRemoteDataSource = WeightLogDataSourceImpl(retrofitService)
        val healthArticleRemoteDataSource = HealthArticleDataSourceImpl(retrofitService)

        // Inisialisasi Repositories dengan SEMUA dependensinya ===
        // Sekarang kita memberikan dependensi remote DAN lokal
        commonRepo = CommonRepoImpl(
            commonDataSource = commonRemoteDataSource,
            commonLocalDataSource = commonLocalDataSource
        )
        authRepo = AuthRepoImpl(
            authDataSource = authRemoteDataSource,
            authLocalDataSource = authLocalDataSource,
            context = this
        )
        consumRepo = ConsumptionRepoImpl(
            consumpRemoteDataSource = consumptionRemoteDataSource,
            consumpLocalDataSource = consumptionLocalDataSource
        )
        ingredientRepo = IngredientRepoImpl(
            ingredientLocalDataSource = ingredientLocalDataSource,
            ingredientRemoteDataSource = ingredientRemoteDataSource
        )
        foodStockRepo = FoodStockRepoImpl(
            foodRemoteDataSource = foodStockRemoteDataSource,
            foodLocalDataSource = foodStockLocalDataSource
        )
        healthArticleRepo = HealthArticleRepoImpl(
            remoteDataSource = healthArticleRemoteDataSource
        )
        adminRepo = AdminRepoImpl(
            adminDataSource = adminDataSource
        )
        recipeRepo = RecipeRepoImpl(
            recipeRemoteDataSource = recipeRemoteDataSource
        )
        weightLogRepo = WeightLogRepoImpl(
            weightRemoteDataSource = weightLogRemoteDataSource,
            weightLocalDataSource = weightLogLocalDataSource
        )

//        Log.w("DATABASE_DEBUG", "!!! MENGHAPUS SEMUA TABEL DI DATABASE LOKAL SAAT STARTUP !!!")
//        CoroutineScope(Dispatchers.IO).launch {
//            database.clearAllTables()
//        }
    }
}