package com.example.nutrisaver.data.sources.remote.admin

import com.example.nutrisaver.data.model.User

interface AdminDataSource {
    suspend fun getUsers(): List<User>
    suspend fun deleteUser(id: Int)
}