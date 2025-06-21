package com.example.nutrisaver.data.repositories

import com.example.nutrisaver.data.sources.remote.auth.AuthDataSource
import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.sources.local.auth.AuthLocalDataSource

class AuthRepoImpl(
    private val authDataSource: AuthDataSource,
    private val authLocalDataSource: AuthLocalDataSource
): AuthRepo {
    override suspend fun register(user: User): User {
        val newUser: User = authDataSource.register(user)
        authLocalDataSource.saveUser(newUser)
        return newUser
    }

    override suspend fun getUserDetail(uuid: String): User {
        val localUser = authLocalDataSource.getUser(uuid)
        if (localUser != null) {
            // Jika ada, langsung kembalikan. Cepat & hemat kuota!
            return localUser
        }

        // 2. Jika di lokal tidak ada, baru ambil dari remote API
        val remoteUser = authDataSource.getUserDetail(uuid)

        // 3. Simpan hasil dari remote ke lokal untuk pemanggilan berikutnya
        authLocalDataSource.saveUser(remoteUser)

        return remoteUser
    }

}