package com.example.nutrisaver.data.repositories

import com.example.nutrisaver.data.sources.remote.auth.AuthDataSource
import com.example.nutrisaver.data.sources.remote.auth.User

class AuthRepoImpl(
    private val authDataSource: AuthDataSource,
): AuthRepo {
    override suspend fun register(user: User): User {
        val newUser: User = authDataSource.register(user)
        return newUser
    }

    override suspend fun getUserDetail(uuid: String): User {
        val user:User = authDataSource.getUserDetail(uuid)
        return user
    }

}