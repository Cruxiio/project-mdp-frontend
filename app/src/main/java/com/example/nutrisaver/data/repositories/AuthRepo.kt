package com.example.nutrisaver.data.repositories

import com.example.nutrisaver.data.sources.remote.auth.User

interface AuthRepo {
    suspend fun register(user: User): User
    suspend fun getUserDetail(uuid:String):User
}