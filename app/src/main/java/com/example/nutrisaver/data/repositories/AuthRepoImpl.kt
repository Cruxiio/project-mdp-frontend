package com.example.nutrisaver.data.repositories

import android.util.Log
import com.example.nutrisaver.data.sources.remote.auth.AuthDataSource
import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.sources.local.auth.AuthLocalDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import android.content.Context
import java.io.FileOutputStream
import java.io.InputStream
import java.io.IOException // Import IOException

class AuthRepoImpl(
    private val authDataSource: AuthDataSource,
    private val authLocalDataSource: AuthLocalDataSource,
    private val context: Context
): AuthRepo {
    // This is the MutableStateFlow for the user profile cache
    private val _userProfileCache = MutableStateFlow<User?>(null)
    val userProfileCache: StateFlow<User?> = _userProfileCache.asStateFlow()

    override suspend fun register(user: User): User {
        val newUser: User = authDataSource.register(user)
        authLocalDataSource.saveUser(newUser)
        return newUser
    }

    override suspend fun getUserProfileForRegister(idToken: String, userId: String): User? {
        // Untuk pengecekan saat registrasi, kita tidak perlu cache.
        // Kita selalu ingin data terbaru dari server untuk memutuskan alur.
        // Jadi, kita langsung panggil fungsi spesifik dari data source.
        Log.d("AuthRepo", "Calling dataSource.getUserProfileForRegister for UID: $userId")
        return authDataSource.getUserProfileForRegister(idToken, userId)
    }

    override suspend fun getUserDetail(uuid: String): User {
        val localUser = authLocalDataSource.getUser(uuid)
        if (localUser != null) {
            // Jika ada, langsung kembalikan. Cepat & hemat kuota!
            return localUser
        }

        // 2. Jika di lokal tidak ada, baru ambil dari remote API
        val remoteUser = authDataSource.getUserDetail(uuid)

        // 3. Simpan hasil dari remote ke lokal untuk pemanggilan berikutnya
        authLocalDataSource.saveUser(remoteUser)

        return remoteUser
    }

    override suspend fun getUserProfile(idToken: String, userId: String): User {
        Log.d("AuthRepo", "Attempting to fetch user $userId from local cache...")
        val localUser = authLocalDataSource.getUser(userId)

        if (localUser != null) {
            // 2. JIKA ADA: Langsung kembalikan data dari lokal. Selesai! Cepat & bisa offline.
            Log.d("AuthRepo", "User found in local cache. Returning local data.")
            return localUser
        } else {
            // 3. JIKA TIDAK ADA: Lanjutkan untuk mengambil dari remote API
            Log.d("AuthRepo", "User not in cache. Fetching from remote API...")
            val remoteUser = authDataSource.getUserProfile(idToken, userId)

            // 4. SIMPAN hasil dari remote ke database lokal untuk pemanggilan berikutnya
            Log.d("AuthRepo", "Saving remote user to local cache.")
            authLocalDataSource.saveUser(remoteUser)

            // 5. Kembalikan data baru dari remote
            return remoteUser
        }
    }

    override suspend fun updateUserProfile(bearerToken: String, user: User): User {
        val parts = mutableMapOf<String, RequestBody>()

        // Add text fields as RequestBody
        parts["name"] = user.name.toRequestBody("text/plain".toMediaTypeOrNull())
        parts["username"] = user.username.toRequestBody("text/plain".toMediaTypeOrNull())
        parts["email"] = user.email.toRequestBody("text/plain".toMediaTypeOrNull())

        var profilePicturePart: MultipartBody.Part? = null
        var tempFile: File? = null // Declare tempFile here to ensure it's accessible for deletion at the very end

        user.profilePicture?.let { uriString ->
            try {
                val uri = Uri.parse(uriString)
                val contentResolver = context.contentResolver

                Log.d("AuthRepoImpl", "Attempting to open InputStream for URI: $uri")
                // Open the input stream from the URI
                val inputStream: InputStream? = contentResolver.openInputStream(uri)

                if (inputStream != null) {
                    // Create a unique temporary file name to avoid conflicts
                    val uniqueFileName = "temp_upload_file_${System.currentTimeMillis()}"
                    tempFile = File(context.cacheDir, uniqueFileName)

                    Log.d("AuthRepoImpl", "Attempting to write to temp file: ${tempFile!!.absolutePath}")

                    // Write the input stream to the temporary file
                    FileOutputStream(tempFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    Log.d("AuthRepoImpl", "Finished writing to temp file.")


                    // Check if the temporary file exists and has content
                    if (tempFile!!.exists() && tempFile!!.length() > 0) {
                        val mediaType = contentResolver.getType(uri)?.toMediaTypeOrNull() ?: "image/*".toMediaTypeOrNull()
                        val requestFile = tempFile!!.asRequestBody(mediaType)
                        profilePicturePart = MultipartBody.Part.createFormData("profile_picture", tempFile!!.name, requestFile)
                        Log.d("AuthRepoImpl", "MultipartBody.Part created successfully. File size: ${tempFile!!.length()}")
                    } else {
                        Log.e("AuthRepoImpl", "Temporary file is empty or does not exist after copying. URI: $uriString")
                        profilePicturePart = null // If the file is empty, set profilePicturePart to null
                    }
                } else {
                    Log.e("AuthRepoImpl", "Input stream from URI was NULL for: $uriString")
                    profilePicturePart = null // No input stream, so no file to upload
                }

            } catch (e: SecurityException) {
                Log.e("AuthRepoImpl", "SecurityException: No permission to read URI: $uriString", e)
                profilePicturePart = null
                // Consider throwing a more specific exception or informing the user
            } catch (e: IOException) {
                Log.e("AuthRepoImpl", "IOException during file copy for URI: $uriString. Message: ${e.message}", e)
                profilePicturePart = null
            } catch (e: Exception) {
                Log.e("AuthRepoImpl", "Generic error creating profile picture part for URI: $uriString. Message: ${e.message}", e)
                profilePicturePart = null
            }
        }

        try {
            // Perform the API call
            val userJson = authDataSource.updateUserProfile(bearerToken, parts, profilePicturePart)
            val updatedUser = User.fromUserJson(userJson) ?: throw Exception("Failed to parse user profile from response")

            // Update local Room cache and StateFlow cache
            authLocalDataSource.saveUser(updatedUser) // Update Room DB
            _userProfileCache.value = updatedUser // Update StateFlow cache
            return updatedUser
        } catch (e: Exception) {
            Log.e("AuthRepoImpl", "API call failed for updateUserProfile: ${e.message}", e)
            throw e // Re-throw the exception to be caught by the ViewModel
        } finally {
            // Ensure the temporary file is deleted after the network request is complete (or failed)
            tempFile?.delete()
            Log.d("AuthRepoImpl", "Temporary file deleted: ${tempFile?.absolutePath}")
        }
    }

    override suspend fun updateUserInformation(idToken: String, user: User): User {
        val updatedUser = authDataSource.updateUserInformation(idToken, user)
        authLocalDataSource.saveUser(updatedUser)
        _userProfileCache.value = updatedUser
        return updatedUser
    }
}