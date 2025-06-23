package com.example.nutrisaver.data.sources.remote.auth

import com.example.nutrisaver.data.model.User

interface AuthDataSource {
    suspend fun register(user: User): User
    suspend fun getUserDetail(uuid: String): User
    suspend fun getUserProfile(idToken: String, userId: String): User
}