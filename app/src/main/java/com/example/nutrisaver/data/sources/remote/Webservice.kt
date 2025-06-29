package com.example.nutrisaver.data.sources.remote

import com.example.nutrisaver.data.model.json.CreateHealthArticleRequestJson
import com.example.nutrisaver.data.model.json.HealthArticleJson
import com.example.nutrisaver.data.model.json.HealthArticleResponseJson
import com.example.nutrisaver.data.model.json.UpdateHealthArticleRequestJson
import com.example.nutrisaver.data.sources.remote.auth.UserJson
import com.example.nutrisaver.data.sources.remote.common.AddFoodStockResponseJson
import com.example.nutrisaver.data.sources.remote.common.AllergenGetAllResponse
import com.example.nutrisaver.data.sources.remote.common.DailyConsumptionDetailJson
import com.example.nutrisaver.data.sources.remote.common.DailyConsumptionJson
import com.example.nutrisaver.data.sources.remote.common.FoodStockJson
import com.example.nutrisaver.data.sources.remote.common.IngredientGetAllResponse
import com.example.nutrisaver.data.sources.remote.common.IngredientJson
import com.example.nutrisaver.data.sources.remote.common.NewFoodStockRequestJson
import com.example.nutrisaver.data.sources.remote.common.RecipeJson
import com.example.nutrisaver.data.sources.remote.common.RecipeSearchRequestJson
import com.example.nutrisaver.data.sources.remote.common.UpdateFoodStockRequestJson
import com.example.nutrisaver.data.sources.remote.common.UpdateFoodStockResponseJson
import com.example.nutrisaver.data.sources.remote.common.WaterUpdateRequestJson
import com.example.nutrisaver.data.sources.remote.common.WeightLogJson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
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

    @Multipart
    @PATCH("api/user/profile")
    suspend fun updateUserProfile(
        @Header("Authorization") bearerToken: String,
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>, // For name, username, email
        @Part profilePicture: MultipartBody.Part? // For the image file
    ): UserJson

    @PATCH("api/user/profile/information")
    suspend fun updateUserInformation(
        @Header("Authorization") token: String,
        @Body user: UserJson
    ): UserJson

    @GET("api/consumption/today") // Sesuaikan jika path berbeda
    suspend fun getTodaysConsumption(@Header("Authorization") token: String): DailyConsumptionJson

    @GET("api/ingredient/all")
    suspend fun getAllIngredients(@Header("Authorization") token: String): IngredientGetAllResponse

    @POST("api/foodstock/new")
    suspend fun addFoodStock(
        @Header("Authorization") token: String,
        @Body foodStock: NewFoodStockRequestJson
    ): Response<AddFoodStockResponseJson>

    @GET("api/foodstock/stock") // Sesuaikan path jika nama endpoint Anda berbeda
    suspend fun getAllFoodStock(@Header("Authorization") token: String): List<FoodStockJson> // <-- DIUBAH

    @DELETE("api/foodstock/delete/{id}")
    suspend fun deleteFoodStock(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit> // Gunakan Response<Unit> karena kita hanya butuh status sukses/gagal

    @PUT("api/foodstock/update/{id}")
    suspend fun updateFoodStock(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: UpdateFoodStockRequestJson // <-- Gunakan request yang baru
    ): UpdateFoodStockResponseJson

    // admin api
    @GET("api/admin/users")
    suspend fun getUsers(): List<UserJson>

    @DELETE("api/admin/users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: String)

    @POST("api/recipe/search")
    suspend fun searchRecipes(
        @Header("Authorization") token: String,
        @Body request: RecipeSearchRequestJson
    ): List<RecipeJson>

    @POST("api/consumption/log")
    suspend fun logMeal(
        @Header("Authorization") token: String,
        @Body mealDetail: DailyConsumptionDetailJson // Terima DTO yang baru
    ) // Endpoint ini mungkin tidak mengembalikan body, jadi bisa Unit

    @POST("api/consumption/water")
    suspend fun updateWaterIntake(
        @Header("Authorization") token: String,
        @Body waterUpdate: WaterUpdateRequestJson
    ): Response<ResponseBody>

    @GET("api/weight/history")
    suspend fun getWeightHistory(@Header("Authorization") token: String): List<WeightLogJson>

    @POST("api/weight/log")
    suspend fun logWeight(
        @Header("Authorization") token: String,
        @Body weightLog: WeightLogJson
    ): Response<ResponseBody> // Atau sesuaikan dengan respons backend

    @GET("api/foodstock/expiring/soon") // <-- PERBAIKI PATH URL-nya agar cocok dengan backend
    suspend fun getExpiringSoonStock(@Header("Authorization") token: String): List<FoodStockJson> // <-- UBAH return type-nya

    @GET("api/consumption/by/date")
    suspend fun getConsumptionByDate(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): Response<DailyConsumptionJson>

    @POST("api/common/health-articles")
    suspend fun createHealthArticle(
        @Header("Authorization") token: String,
        @Body request: CreateHealthArticleRequestJson
    ): Response<HealthArticleJson>

    @GET("api/common/health-articles")
    suspend fun getHealthArticles(
        @Query("target_goal") targetGoal: String? = null,
        @Query("target_diet_type") targetDietType: String? = null,
        @Query("title") title: String? = null
    ): HealthArticleResponseJson

    @PATCH("api/common/health-articles/{article_id}")
    suspend fun updateHealthArticle(
        @Header("Authorization") token: String,
        @Path("article_id") articleId: Int,
        @Body request: UpdateHealthArticleRequestJson
    ): Response<HealthArticleJson>

    @DELETE("api/common/health-articles/{article_id}")
    suspend fun deleteHealthArticle(
        @Header("Authorization") token: String,
        @Path("article_id") articleId: Int
    ): Response<Unit>
}