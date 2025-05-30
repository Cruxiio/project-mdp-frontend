package com.example.nutrisaver.data.sources.remote.auth

interface AuthDataSource {
    suspend fun register(user: User): User
    suspend fun getUserDetail(uuid: String): User

}