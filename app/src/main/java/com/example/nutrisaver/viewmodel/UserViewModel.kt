package com.example.nutrisaver.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutrisaver.data.model.DailyConsumption
import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.repositories.AuthRepo
import com.example.nutrisaver.data.repositories.CommonRepo
import com.example.nutrisaver.data.repositories.ConsumptionRepo
import com.example.nutrisaver.data.repositories.ConsumptionRepoImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

sealed class UserState {
    object Idle : UserState() // State awal
    object Loading : UserState()
    object Success : UserState()
    data class Error(val message: String) : UserState()
}

class UserViewModel(
    private val commonRepo: CommonRepo,
    private val authRepo: AuthRepo,
    private val consumptionRepo: ConsumptionRepo,
    application: Application
) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // State untuk menampung data profil user
    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> = _userProfile

    // State untuk mengelola UI (Loading, Error, Success)
    private val _userState = MutableLiveData<UserState>()
    val userState: LiveData<UserState> = _userState
    val todaysConsumption: LiveData<DailyConsumption?> =
        consumptionRepo.getTodaysConsumptionWithDetails().asLiveData()

    /**
     * Mengambil data profil user dari repository.
     * Menerapkan strategi Cache-First yang ada di AuthRepoImpl.
     */
    fun fetchUserProfile() {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            _userState.value = UserState.Error("Sesi berakhir. Silakan login kembali.")
            return
        }

        _userState.value = UserState.Loading
        viewModelScope.launch {
            try {
                val firebaseUser = auth.currentUser!!
                val rawToken = firebaseUser.getIdToken(true).await().token
                val uid = firebaseUser.uid

                if (rawToken != null) {
                    // [FIX DI SINI] Tambahkan "Bearer " di depan token
                    val bearerToken = "Bearer $rawToken"

                    val profile = authRepo.getUserProfile(bearerToken, uid) // Kirim token yang sudah diformat
                    _userProfile.value = profile
                    _userState.value = UserState.Success
                } else {
                    throw Exception("Gagal mendapatkan token autentikasi.")
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("UserViewModel", "Gagal mengambil profil user", e)
                // Cek apakah errornya adalah HttpException 401
                if (e is retrofit2.HttpException && e.code() == 401) {
                    _userState.value = UserState.Error("Autentikasi gagal. Silakan coba login kembali.")
                } else {
                    _userState.value = UserState.Error(e.message ?: "Gagal memuat profil.")
                }
            }
        }
    }

    fun refreshDashboardData() {
        val firebaseUser = auth.currentUser ?: return

        _userState.value = UserState.Loading
        viewModelScope.launch {
            try {
                val token = firebaseUser.getIdToken(true).await().token ?: throw Exception("Token tidak valid")

                // Panggil fungsi refresh dari repo, tidak perlu casting lagi
                authRepo.getUserProfile(token, firebaseUser.uid).let { _userProfile.value = it }
                consumptionRepo.refreshTodaysConsumption(token)

                // Ambil juga profil user
                val profile = authRepo.getUserProfile(token, firebaseUser.uid)
                _userProfile.value = profile

                _userState.value = UserState.Success
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("UserViewModel", "Gagal me-refresh data dashboard", e)
                _userState.value = UserState.Error(e.message ?: "Gagal memuat data baru.")
            }
        }
    }

}