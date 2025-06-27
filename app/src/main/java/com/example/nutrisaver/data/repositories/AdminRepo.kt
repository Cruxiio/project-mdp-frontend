package com.example.nutrisaver.data.repositories

import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.sources.remote.admin.AdminDataSource

interface AdminRepo {
    suspend fun getUsers(): List<User>
    suspend fun deleteUser(id: Int)
}

class AdminRepoImpl(
    private val adminDataSource: AdminDataSource
): AdminRepo {
    override suspend fun getUsers(): List<User> {
        val users = adminDataSource.getUsers()
        return users
    }

    override suspend fun deleteUser(id: Int) {
        adminDataSource.deleteUser(id)
    }
}