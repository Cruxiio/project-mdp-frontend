package com.example.nutrisaver.data.sources.remote.auth

import com.example.nutrisaver.data.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AuthDataSource {
    suspend fun register(user: User): User
    suspend fun getUserDetail(uuid: String): User
    suspend fun getUserProfile(idToken: String, userId: String): User
    suspend fun updateUserProfile(
        bearerToken: String,
        data: Map<String, RequestBody>,
        profilePicture: MultipartBody.Part?
    ): UserJson
    suspend fun updateUserInformation(idToken: String, user: User): User
    suspend fun getUserProfileForRegister(idToken: String, userId: String): User?
}