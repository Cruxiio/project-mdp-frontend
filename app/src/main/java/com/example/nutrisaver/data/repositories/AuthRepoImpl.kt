package com.example.nutrisaver.data.repositories

import android.util.Log
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

    override suspend fun getUserProfile(idToken: String, userId: String): User {
        Log.d("AuthRepo", "Attempting to fetch user $userId from local cache...")
        val localUser = authLocalDataSource.getUser(userId)

        if (localUser != null) {
            // 2. JIKA ADA: Langsung kembalikan data dari lokal. Selesai! Cepat & bisa offline.
            Log.d("AuthRepo", "User found in local cache. Returning local data.")
            return localUser
        } else {
            // 3. JIKA TIDAK ADA: Lanjutkan untuk mengambil dari remote API
            Log.d("AuthRepo", "User not in cache. Fetching from remote API...")
            val remoteUser = authDataSource.getUserProfile(idToken, userId)

            // 4. SIMPAN hasil dari remote ke database lokal untuk pemanggilan berikutnya
            Log.d("AuthRepo", "Saving remote user to local cache.")
            authLocalDataSource.saveUser(remoteUser)

            // 5. Kembalikan data baru dari remote
            return remoteUser
        }
    }

}