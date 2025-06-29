package com.example.nutrisaver.data.repositories

import android.content.ContentValues.TAG
import android.util.Log
import com.example.nutrisaver.data.model.UserFeedback
import com.example.nutrisaver.data.sources.remote.common.UserFeedbackDataSource

interface FeedbackRepo {
    suspend fun getUsersFeedback(token: String): List<UserFeedback>
    suspend fun addFeedback(token: String, feedback: UserFeedback): UserFeedback
}

class FeedbackRepoImpl(
    private val feedbackDataSource: UserFeedbackDataSource
): FeedbackRepo {
    override suspend fun getUsersFeedback(token: String): List<UserFeedback> {
        try {
            return feedbackDataSource.getUsersFeedback(token)
        }
        catch (e: Exception) {
            Log.e(TAG, "getUsersFeedback: GAGAL mengambil dari remote.", e)
            throw e
        }
    }

    override suspend fun addFeedback(token: String, feedback: UserFeedback): UserFeedback {
        try {
            return feedbackDataSource.addFeedback(token, feedback)
        }
        catch (e: Exception) {
            Log.e(TAG, "addFeedback: GAGAL menambah feedback.", e)
            throw e
        }
    }

}