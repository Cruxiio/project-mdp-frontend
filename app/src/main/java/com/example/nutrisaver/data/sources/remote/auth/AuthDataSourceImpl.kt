package com.example.nutrisaver.data.sources.remote.auth

import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.sources.remote.Webservice

class AuthDataSourceImpl(
    private val webservice: Webservice
): AuthDataSource {
    override suspend fun register(user: User): User {
        val newUser = webservice.register(user.toUserJson())
        return User.fromUserJson(newUser)
    }

    override suspend fun getUserDetail(uuid: String): User {
        val user: User? = User.fromUserJson(webservice.getUser(uuid)) ?: null

        if (user == null) {
            throw Exception("User not found")
        }

        return user
    }
}