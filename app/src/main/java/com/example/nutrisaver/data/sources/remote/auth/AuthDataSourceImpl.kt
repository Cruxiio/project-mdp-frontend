package com.example.nutrisaver.data.sources.remote.auth

import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.sources.remote.Webservice

class AuthDataSourceImpl(
    private val webservice: Webservice
): AuthDataSource {
    override suspend fun register(user: User): User {
        val newUserJson = webservice.register(user.toUserJson())
        val mappedUser = User.fromUserJson(newUserJson)
        return mappedUser ?: throw IllegalStateException("Gagal mem-parsing data user dari server setelah registrasi.")
    }

    override suspend fun getUserDetail(uuid: String): User {
        val user: User? = User.fromUserJson(webservice.getUser(uuid)) ?: null

        if (user == null) {
            throw Exception("User not found")
        }

        return user
    }
}