package com.example.nutrisaver.data.sources.remote.common

import com.squareup.moshi.Json

data class UserFeedbackJson(
    @Json(name = "id") val id: Int?,
    @Json(name = "user_id") val userId: Int?,
    @Json(name = "message") val message: String?,
    @Json(name = "status") val status: String?, // PENDING, REVIEWED, DONE\
    @Json(name = "admin_response") val adminResponse: String?,
    @Json(name = "responded_by") val respondedBy: Int?,
    @Json(name = "createdAt") val createdAt: String?,
    @Json(name = "responded_at") val respondedAt: String?
)