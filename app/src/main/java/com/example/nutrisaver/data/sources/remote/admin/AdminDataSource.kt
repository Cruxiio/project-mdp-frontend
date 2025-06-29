package com.example.nutrisaver.data.sources.remote.admin

import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.model.UserFeedback

interface AdminDataSource {
    suspend fun getUsers(token: String): List<User> // Add token parameter
    suspend fun deleteUser(token: String, id: Int) // Add token parameter
    suspend fun getAllFeedback(token: String): List<UserFeedback>
    suspend fun respondFeedback(token: String, id: Int, adminResponse: String)
}