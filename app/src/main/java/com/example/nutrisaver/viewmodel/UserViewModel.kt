package com.example.nutrisaver.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutrisaver.data.model.Allergen
import com.example.nutrisaver.data.model.DailyConsumption
import com.example.nutrisaver.data.model.FoodStock
import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.model.WeightLog
import com.example.nutrisaver.data.repositories.AuthRepo
import com.example.nutrisaver.data.repositories.CommonRepo
import com.example.nutrisaver.data.repositories.ConsumptionRepo
import com.example.nutrisaver.data.repositories.FoodStockRepo
import com.example.nutrisaver.data.repositories.WeightLogRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException

sealed class UserState {
    object Idle : UserState()
    object Loading : UserState()
    object Success : UserState()
    data class Error(val message: String) : UserState()
}

class UserViewModel(
    private val commonRepo: CommonRepo,
    private val authRepo: AuthRepo,
    private val consumptionRepo: ConsumptionRepo,
    private val weightLogRepo: WeightLogRepo,
    private val foodStockRepo: FoodStockRepo,
    application: Application
) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> = _userProfile

    private val _allergenState = MutableLiveData<List<Allergen>>()
    val allergenState: LiveData<List<Allergen>> = _allergenState

    private val _userState = MutableLiveData<UserState>()
    val userState: LiveData<UserState> = _userState
    val todaysConsumption: LiveData<DailyConsumption?> =
        consumptionRepo.getTodaysConsumptionWithDetails().asLiveData()

    val weightHistory: LiveData<List<WeightLog>> = weightLogRepo.weightHistory.asLiveData()

    private val _expiringFoodStock = MutableLiveData<List<FoodStock>>()
    val expiringFoodStock: LiveData<List<FoodStock>> = _expiringFoodStock

    init {
        refreshDashboardData()
    }
    /**
     * Fetches the user profile from the repository.
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
                // DIUBAH: Hanya ambil token mentah
                val token = firebaseUser.getIdToken(true).await().token
                val uid = firebaseUser.uid

                if (token != null) {
                    // Panggil repo HANYA dengan token mentah
                    val profile = authRepo.getUserProfile(token, uid)
                    _userProfile.value = profile
                    _userState.value = UserState.Success
                } else {
                    throw Exception("Gagal mendapatkan token autentikasi.")
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("UserViewModel", "Gagal mengambil profil user", e)
                if (e is retrofit2.HttpException && e.code() == 401) {
                    _userState.value = UserState.Error("Autentikasi gagal. Silakan coba login kembali.")
                } else {
                    _userState.value = UserState.Error(e.message ?: "Gagal memuat profil.")
                }
            }
        }
    }

    /**
     * Updates profile picture, name, username, email remotely and locally.
     * @param user The User object containing the updated profile fields.
     */
    fun updateUserProfile(user: User) {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            _userState.value = UserState.Error("Sesi berakhir. Silakan login kembali.")
            return
        }

        _userState.value = UserState.Loading
        viewModelScope.launch {
            try {
                // DIUBAH: Hanya ambil token mentah
                val token = firebaseUser.getIdToken(true).await().token
                if (token != null) {
                    // Panggil repo HANYA dengan token mentah
                    val updatedUser = authRepo.updateUserProfile(token, user)
                    _userProfile.value = updatedUser
                    _userState.value = UserState.Success
                } else {
                    throw Exception("Gagal mendapatkan token autentikasi.")
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("UserViewModel", "Gagal mengupdate profil user (nama, username, email, gambar)", e)
                _userState.value = UserState.Error(e.message ?: "Gagal mengupdate profil.")
            }
        }
    }

    /**
     * Updates comprehensive user information (gender, dob, weight, height, goals, diet, allergens)
     * This function is for the "Edit Information" screen.
     * @param user The User object with updated information.
     */
    fun updateUserInformation(user: User) {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            _userState.value = UserState.Error("Sesi berakhir. Silakan login kembali.")
            return
        }

        _userState.value = UserState.Loading
        viewModelScope.launch {
            try {
                // DIUBAH: Hanya ambil token mentah
                val token = firebaseUser.getIdToken(true).await().token
                if (token != null) {
                    // Panggil repo HANYA dengan token mentah
                    val updatedUser = authRepo.updateUserInformation(token, user)
                    _userProfile.value = updatedUser
                    _userState.value = UserState.Success
                } else {
                    throw Exception("Gagal mendapatkan token autentikasi.")
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("UserViewModel", "Gagal mengupdate informasi user", e)
                _userState.value = UserState.Error(e.message ?: "Gagal mengupdate informasi.")
            }
        }
    }

    /**
     * Fetches all available allergens from the backend.
     */
    fun fetchAllergens() {
        _userState.value = UserState.Loading
        viewModelScope.launch {
            try {
                // Tidak perlu token untuk endpoint ini
                val allergens = commonRepo.getAllergen("")
                _allergenState.value = allergens
                _userState.value = UserState.Success
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("UserViewModel", "Gagal mengambil daftar alergen", e)
                _userState.value = UserState.Error(e.message ?: "Gagal memuat alergen.")
            }
        }
    }

    fun refreshDashboardData() {
        val firebaseUser = auth.currentUser ?: return

        _userState.value = UserState.Loading
        viewModelScope.launch {
            try {
                // DIUBAH: Hanya ambil token mentah
                val token = firebaseUser.getIdToken(true).await().token ?: throw Exception("Token tidak valid")

                // Panggil semua fungsi repo HANYA dengan token mentah
                authRepo.getUserProfile(token, firebaseUser.uid).let { _userProfile.value = it }
                consumptionRepo.refreshTodaysConsumption(token)
                weightLogRepo.refreshWeightHistory(token)
                foodStockRepo.getExpiringSoonStock(token).let { _expiringFoodStock.value = it }

                _userState.value = UserState.Success
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("UserViewModel", "Gagal me-refresh data dashboard", e)
                _userState.value = UserState.Error(e.message ?: "Gagal memuat data baru.")
            }
        }
    }

    fun updateWaterIntake(amount: Int) {
        if (amount == 0) return
        val firebaseUser = auth.currentUser ?: return

        viewModelScope.launch {
            try {
                // DIUBAH: Hanya ambil token mentah
                val token = firebaseUser.getIdToken(true).await().token ?: throw Exception("Token tidak valid")

                // Panggil repo HANYA dengan token mentah
                // Fungsi updateWaterIntake di repo akan memanggil refreshTodaysConsumption secara otomatis
                consumptionRepo.updateWaterIntake(token, amount)

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("UserViewModel", "Gagal mengupdate asupan air", e)
                _userState.value = UserState.Error(e.message ?: "Gagal menyimpan data air.")
            }
        }
    }

    fun logWeight(weight: Float, unit: String, date: LocalDate) {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                // DIUBAH: Hanya ambil token mentah
                val token = auth.currentUser?.getIdToken(true)?.await()?.token ?: throw Exception("Token tidak valid")

                // Panggil repo HANYA dengan token mentah
                weightLogRepo.logWeight(token, weight, unit, date)
                _userState.value = UserState.Success
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("UserViewModel", "Failed to log weight", e)
                _userState.value = UserState.Error(e.message ?: "Gagal mencatat berat badan.")
            }
        }
    }
}
