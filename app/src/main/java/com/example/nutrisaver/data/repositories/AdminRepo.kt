package com.example.nutrisaver.data.repositories

import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.model.UserFeedback
import com.example.nutrisaver.data.sources.remote.admin.AdminDataSource
import android.util.Log

interface AdminRepo {
    suspend fun getUsers(token: String): List<User>
    suspend fun deleteUser(token: String, id: Int)
    suspend fun getAllFeedback(token: String): List<UserFeedback>
    suspend fun respondFeedback(token: String, id: Int, adminResponse: String)
}

class AdminRepoImpl(
    private val adminDataSource: AdminDataSource
): AdminRepo {
    override suspend fun getUsers(token: String): List<User> {
        try {
            val users = adminDataSource.getUsers(token)
            return users
        } catch (e: Exception) {
            Log.e("AdminRepo", "getUsers: Failed to get users from data source", e)
            throw e
        }
    }

    override suspend fun deleteUser(token: String, id: Int) {
        try {
            adminDataSource.deleteUser(token, id)
        } catch (e: Exception) {
            Log.e("AdminRepo", "deleteUser: Failed to delete user via data source", e)
            throw e
        }
    }

    override suspend fun getAllFeedback(token: String): List<UserFeedback> {
        try {
            return adminDataSource.getAllFeedback(token)
        } catch (e: Exception) {
            Log.e("AdminRepo", "getAllFeedback: Failed to get all feedback from data source", e)
            throw e
        }
    }

    override suspend fun respondFeedback(token: String, id: Int, adminResponse: String) {
        try {
            adminDataSource.respondFeedback(token, id, adminResponse)
        } catch (e: Exception) {
            Log.e("AdminRepo", "respondFeedback: Failed to respond to feedback via data source", e)
            throw e
        }
    }
}