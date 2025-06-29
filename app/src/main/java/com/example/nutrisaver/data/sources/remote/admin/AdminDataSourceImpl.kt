package com.example.nutrisaver.data.sources.remote.admin

import android.util.Log
import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.model.UserFeedback
import com.example.nutrisaver.data.sources.remote.Webservice
import com.example.nutrisaver.data.sources.remote.common.UserFeedbackJson
import com.google.firebase.auth.FirebaseAuth

class AdminDataSourceImpl(
    private val webservice: Webservice
): AdminDataSource {

    override suspend fun getUsers(token: String): List<User> {
        val bearerToken = "Bearer $token"
        val usersJson = webservice.getUsers(bearerToken)
        val users = usersJson.mapNotNull {
            User.fromUserJson(it)
        }
        Log.e("AdminDataSource",  users.toString())
        return users
    }

    override suspend fun deleteUser(token: String, id: Int) {
        try {
            val bearerToken = "Bearer $token"
            webservice.deleteUser(bearerToken, id.toString())
        } catch (e: Exception) {
            Log.e("AdminDataSource", "deleteUser: Failed to delete user", e)
            throw e
        }
    }

    override suspend fun getAllFeedback(token: String): List<UserFeedback> {
        try {
            val bearerToken = "Bearer $token"
            val feedbackJson = webservice.getAllFeedback(bearerToken)
            return feedbackJson.mapNotNull { UserFeedback.fromUserFeedbackJson(it) }
        } catch (e: Exception) {
            Log.e("AdminDataSource", "getAllFeedback: GAGAL mengambil dari remote.", e)
            throw e
        }
    }

    // Corrected: Added newStatus parameter
    override suspend fun respondFeedback(token: String, id: Int, adminResponse: String) {
        try {
            val bearerToken = "Bearer $token"
            val requestBodyJson = UserFeedbackJson(
                id = null,
                userId = null,
                userName = null,
                userEmail = null,
                message = null,
                status = "done",
                adminResponse = adminResponse,
                respondedBy = null,
                createdAt = null,
                respondedAt = null
            )
            webservice.respondFeedback(bearerToken, id, requestBodyJson)
        }
        catch (e: Exception) {
            Log.e("AdminDataSource", "respondFeedback: GAGAL merespons feedback.", e)
            throw e
        }
    }

}