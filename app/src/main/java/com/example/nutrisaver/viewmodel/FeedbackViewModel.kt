package com.example.nutrisaver.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutrisaver.data.model.UserFeedback
import com.example.nutrisaver.data.repositories.FeedbackRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

sealed class FeedbackState {
    object Idle : FeedbackState()
    object Loading : FeedbackState()
    object Success : FeedbackState()
    data class Error(val message: String) : FeedbackState()
}


class FeedbackViewModel(
    private val feedbackRepo: FeedbackRepo,
    application: Application
) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _usersFeedbacks = MutableLiveData<List<UserFeedback>>()
    val usersFeedbacks: LiveData<List<UserFeedback>> = _usersFeedbacks

    private val _feedbackState = MutableLiveData<FeedbackState>()
    val feedbackState: LiveData<FeedbackState> = _feedbackState

    fun fetchUsersFeedbacks() {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            _feedbackState.value = FeedbackState.Error("Sesi berakhir. Silakan login kembali.")
            return
        }
        _feedbackState.value = FeedbackState.Loading
        viewModelScope.launch {
            try {
                val token = firebaseUser.getIdToken(true).await().token
                if (token != null) {
                    val feedbacks = feedbackRepo.getUsersFeedback(token)
                    _usersFeedbacks.value = feedbacks
                    _feedbackState.value = FeedbackState.Success
                } else {
                    throw Exception("Gagal mendapatkan token autentikasi.")
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("FeedbackViewModel", "Gagal mengambil profil user", e)
                if (e is retrofit2.HttpException && e.code() == 401) {
                    _feedbackState.value = FeedbackState.Error("Autentikasi gagal. Silakan coba login kembali.")
                } else {
                    _feedbackState.value = FeedbackState.Error(e.message ?: "Gagal memuat feedback.")
                }
            }
        }
    }

    fun addFeedback(feedback: UserFeedback) {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            _feedbackState.value = FeedbackState.Error("Sesi berakhir. Silakan login kembali.")
            return
        }
        _feedbackState.value = FeedbackState.Loading
        viewModelScope.launch {
            try {
                // DIUBAH: Hanya ambil token mentah
                val token = firebaseUser.getIdToken(true).await().token
                if (token != null) {
                    // Panggil repo HANYA dengan token mentah
                    val feedbackBaru = feedbackRepo.addFeedback(token, feedback)
                    _feedbackState.value = FeedbackState.Success
                    Log.d("FeedbackViewModel", "Feedback berhasil ditambahkan: $feedbackBaru")
                    fetchUsersFeedbacks() // Refresh data setelah feedback ditambahkan
                } else {
                    throw Exception("Gagal mendapatkan token autentikasi.")
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("FeedbackViewModel", "Gagal menambahkan feedback", e)
                _feedbackState.value = FeedbackState.Error(e.message ?: "Gagal mengupdate feedback.")
            }
        }
    }
}