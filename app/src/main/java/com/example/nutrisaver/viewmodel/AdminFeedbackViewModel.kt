package com.example.nutrisaver.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutrisaver.data.model.UserFeedback
import com.example.nutrisaver.data.repositories.AdminRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException
import android.util.Log

sealed class AdminFeedbackState {
    object Idle : AdminFeedbackState()
    object Loading : AdminFeedbackState()
    object Success : AdminFeedbackState()
    data class Error(val message: String) : AdminFeedbackState()
}

class AdminFeedbackViewModel(
    private val adminRepo: AdminRepo,
    application: Application
): AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _feedbacks = MutableLiveData<List<UserFeedback>>()
    val feedbacks: LiveData<List<UserFeedback>> = _feedbacks

    private val _feedbackState = MutableLiveData<AdminFeedbackState>()
    val feedbackState: LiveData<AdminFeedbackState> = _feedbackState

    fun init() {
        getAllFeedback()
    }

    fun getAllFeedback() {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            _feedbackState.value = AdminFeedbackState.Error("Session expired. Please log in again.")
            return
        }

        _feedbackState.value = AdminFeedbackState.Loading
        viewModelScope.launch {
            try {
                val token = firebaseUser.getIdToken(true).await().token
                if (token != null) {
                    val feedbackList = adminRepo.getAllFeedback(token)
                    _feedbacks.value = feedbackList
                    _feedbackState.value = AdminFeedbackState.Success
                } else {
                    throw Exception("Failed to get authentication token.")
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("AdminFeedbackViewModel", "Failed to fetch all feedback: ${e.message}", e)
                if (e is retrofit2.HttpException && e.code() == 401) {
                    _feedbackState.value = AdminFeedbackState.Error("Authentication failed. Please try logging in again.")
                } else if (e is retrofit2.HttpException && e.code() == 403) {
                    _feedbackState.value = AdminFeedbackState.Error("Forbidden: You don't have admin access.")
                }
                else {
                    _feedbackState.value = AdminFeedbackState.Error(e.message ?: "Failed to load feedback.")
                }
            }
        }
    }

    fun respondToFeedback(feedbackId: Int, adminResponse: String) {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            _feedbackState.value = AdminFeedbackState.Error("Session expired. Please log in again.")
            return
        }

        _feedbackState.value = AdminFeedbackState.Loading
        viewModelScope.launch {
            try {
                val token = firebaseUser.getIdToken(true).await().token
                if (token != null) {
                    adminRepo.respondFeedback(token, feedbackId, adminResponse)
                    _feedbackState.value = AdminFeedbackState.Success
                    Log.d("AdminFeedbackViewModel", "Feedback with ID $feedbackId responded successfully.")
                    getAllFeedback() // Refresh the list of feedbacks after responding
                } else {
                    throw Exception("Failed to get authentication token.")
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("AdminFeedbackViewModel", "Failed to respond to feedback $feedbackId: ${e.message}", e)
                if (e is retrofit2.HttpException && e.code() == 401) {
                    _feedbackState.value = AdminFeedbackState.Error("Authentication failed. Please try logging in again.")
                } else if (e is retrofit2.HttpException && e.code() == 403) {
                    _feedbackState.value = AdminFeedbackState.Error("Forbidden: You don't have admin access to respond.")
                }
                else {
                    _feedbackState.value = AdminFeedbackState.Error(e.message ?: "Failed to respond to feedback.")
                }
            }
        }
    }
}