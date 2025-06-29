package com.example.nutrisaver.data.model

import com.example.nutrisaver.data.sources.remote.common.UserFeedbackJson
import com.google.gson.annotations.SerializedName
import java.time.Instant

data class UserFeedback(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    val message: String,
    val status: String, // PENDING, REVIEWED, DONE
    val adminResponse: String? = null,
    val respondedBy: Int? = null,
    val createdAt: Instant,
    val respondedAt: Instant? = null
) {
    companion object {
        fun fromUserFeedbackJson(json: UserFeedbackJson): UserFeedback? {
            // Basic validation for essential fields to prevent crashes or illogical states
            if (json.id == null || json.userId == null || json.message == null || json.status == null || json.createdAt == null) {
                // You might want to log this error or throw a more specific exception
                return null
            }
            return UserFeedback(
                id = json.id,
                userId = json.userId,
                message = json.message,
                status = json.status,
                adminResponse = json.adminResponse,
                respondedBy = json.respondedBy,
                createdAt = json.createdAt.let { Instant.parse(it) },
                respondedAt = json.respondedAt?.let { Instant.parse(it) },
            )
        }
    }

    fun toUserFeedbackJson() = UserFeedbackJson(
        id = this.id,
        userId = this.userId,
        message = this.message,
        status = this.status,
        adminResponse = this.adminResponse,
        respondedBy = this.respondedBy,
        createdAt = this.createdAt.toString(),
        respondedAt = this.respondedAt?.toString()
    )
}
