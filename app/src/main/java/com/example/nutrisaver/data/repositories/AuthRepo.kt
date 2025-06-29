package com.example.nutrisaver.data.repositories

import com.example.nutrisaver.data.model.User

interface AuthRepo {
    suspend fun register(user: User): User
    suspend fun getUserDetail(uuid:String): User
    suspend fun getUserProfile(idToken: String, userId: String): User
    suspend fun updateUserProfile(idToken: String, user: User): User
    suspend fun updateUserInformation(idToken: String, user: User): User
    suspend fun getUserProfileForRegister(idToken: String, userId: String): User?
}