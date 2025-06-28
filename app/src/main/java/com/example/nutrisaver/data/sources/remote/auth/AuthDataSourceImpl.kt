package com.example.nutrisaver.data.sources.remote.auth

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.sources.remote.Webservice
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AuthDataSourceImpl(
    private val webservice: Webservice,
    private val context: Context
): AuthDataSource {
    override suspend fun register(user: User): User {
        val newUserJson = webservice.register(user.toUserJson())
        val mappedUser = User.fromUserJson(newUserJson)
        return mappedUser ?: throw IllegalStateException("Gagal mem-parsing data user dari server setelah registrasi.")
    }

    override suspend fun getUserDetail(uuid: String): User {
        val user: User? = User.fromUserJson(webservice.getUser(uuid)) ?: null

        if (user == null) {
            throw Exception("User not found")
        }

        return user
    }

    override suspend fun getUserProfile(idToken: String, userId: String): User {
        // Panggil webservice dengan kedua parameter
        val userJson = webservice.getUserProfile(idToken, userId)
        return User.fromUserJson(userJson)
            ?: throw Exception("Gagal mem-parsing data profil user dari server.")
    }

    override suspend fun updateUserProfile(
        bearerToken: String,
        data: Map<String, RequestBody>,
        profilePicture: MultipartBody.Part?
    ): UserJson {
        return webservice.updateUserProfile(bearerToken, data, profilePicture)
    }

    // Helper function to convert Uri to MultipartBody.Part
    private fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(fileUri)
            val file = File(context.cacheDir, "temp_upload_file") // Create a temp file
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            if (file.exists() && file.length() > 0) {
                val mediaType = contentResolver.getType(fileUri)?.toMediaTypeOrNull() ?: "image/*".toMediaTypeOrNull()
                val requestFile = file.asRequestBody(mediaType)
                MultipartBody.Part.createFormData(partName, file.name, requestFile)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthDataSourceImpl", "Error preparing file part: ${e.message}")
            null
        }
    }

    // Helper function to convert String to RequestBody
    private fun createPartFromString(descriptionString: String): RequestBody {
        return descriptionString.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    override suspend fun updateUserInformation(idToken: String, user: User): User {
        val userJsonToUpdate = user.toUserJson()
        val formattedToken = "Bearer $idToken"
        val updatedUserJson = webservice.updateUserInformation(formattedToken, userJsonToUpdate)
        return User.fromUserJson(updatedUserJson)
            ?: throw Exception("Gagal mem-parsing data user yang diperbarui dari server.")
    }
}