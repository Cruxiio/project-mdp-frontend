package com.example.nutrisaver.data.sources.remote.common

import com.example.nutrisaver.data.model.UserFeedback
import com.example.nutrisaver.data.sources.remote.Webservice

interface UserFeedbackDataSource {
    suspend fun getUsersFeedback(token: String): List<UserFeedback>
    suspend fun addFeedback(token: String, feedback: UserFeedback): UserFeedback
}

class UserFeedbackDataSourceImpl(
    private val webservice: Webservice
): UserFeedbackDataSource {
    override suspend fun getUsersFeedback(token: String): List<UserFeedback> {
        val formattedToken = "Bearer $token"
        val feedbackJsons = webservice.getUserFeedback(formattedToken)
        return feedbackJsons.mapNotNull { UserFeedback.fromUserFeedbackJson(it) }
    }

    override suspend fun addFeedback(token: String, feedback: UserFeedback): UserFeedback {
        val formattedToken = "Bearer $token"
        val feedbackJsonRequest = feedback.toUserFeedbackJson()
        val feedbackJsonResponse = webservice.addFeedback(formattedToken, feedbackJsonRequest)
        return UserFeedback.fromUserFeedbackJson(feedbackJsonResponse)
            ?: throw IllegalStateException("Failed to parse added feedback response from API")
    }

}