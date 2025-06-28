package com.example.nutrisaver.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrisaver.data.model.DailyConsumption
import com.example.nutrisaver.data.model.DailyConsumptionDetail
import com.example.nutrisaver.data.model.Recipe
import com.example.nutrisaver.data.repositories.ConsumptionRepo
import com.example.nutrisaver.data.repositories.RecipeRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

sealed class SearchState {
    object Idle : SearchState() // State Awal
    object Loading : SearchState()
    data class Success(val recipes: List<Recipe>) : SearchState()
    data class Error(val message: String) : SearchState()
}

// State untuk proses Logging Makanan
sealed class LogMealState {
    object Idle : LogMealState() // State Awal
    object Loading : LogMealState()
    object Success : LogMealState()
    data class Error(val message: String) : LogMealState()
}

class LogMealViewModel(
    private val recipeRepo: RecipeRepo,
    private val consumptionRepo: ConsumptionRepo,
    application: Application // Diperlukan jika butuh context, jika tidak bisa pakai ViewModel biasa
) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _searchState = MutableLiveData<SearchState>(SearchState.Idle)
    val searchState: LiveData<SearchState> = _searchState

    private val _logState = MutableLiveData<LogMealState>(LogMealState.Idle)
    val logState: LiveData<LogMealState> = _logState

    private var searchJob: Job? = null

    private val _historySelectedDate = MutableStateFlow(LocalDate.now())
    val historySelectedDate: StateFlow<LocalDate> = _historySelectedDate.asStateFlow()

    private val _historyConsumptionData = MutableStateFlow<DailyConsumption?>(null)
    val historyConsumptionData: StateFlow<DailyConsumption?> = _historyConsumptionData.asStateFlow()

    private val _historyIsLoading = MutableStateFlow(false)
    val historyIsLoading: StateFlow<Boolean> = _historyIsLoading.asStateFlow()

    // Blok init untuk langsung memuat data saat ViewModel dibuat
    // dan otomatis memuat ulang saat tanggal berubah
    init {
        _historySelectedDate.onEach { date ->
            fetchHistoryForDate(date)
        }.launchIn(viewModelScope)
    }

    fun changeHistoryDate(newDate: LocalDate) {
        _historySelectedDate.value = newDate
    }

    /**
     * Mengambil data konsumsi dari server untuk tanggal yang spesifik.
     */
    private fun fetchHistoryForDate(date: LocalDate) {
        viewModelScope.launch {
            _historyIsLoading.value = true
            try {
                val token = auth.currentUser?.getIdToken(true)?.await()?.token
                if (token != null) {
                    val data = consumptionRepo.getConsumptionByDate(token, date)
                    _historyConsumptionData.value = data
                } else {
                    throw Exception("Sesi tidak valid.")
                }
            } catch (e: Exception) {
                Log.e("LogHistory", "Gagal mengambil data riwayat: ", e)
                _historyConsumptionData.value = null // Set data jadi null jika ada error
            } finally {
                _historyIsLoading.value = false
            }
        }
    }


    fun searchRecipes(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchState.Success(emptyList()) // Kosongkan list jika query kosong
            return
        }

        searchJob?.cancel()
        _searchState.value = SearchState.Loading
        searchJob = viewModelScope.launch {
            // Debounce: tunggu 500ms setelah user berhenti mengetik sebelum panggil API
            delay(500)
            try {
                val token = auth.currentUser?.getIdToken(false)?.await()?.token ?: throw Exception("Sesi tidak valid.")
                val recipes = recipeRepo.searchRecipes(token, query)
                _searchState.value = SearchState.Success(recipes)
            } catch (e: Exception) {
                Log.e("LogMealViewModel", "Recipe search failed", e)
                _searchState.value = SearchState.Error(e.message ?: "Gagal mencari resep.")
            }
        }
    }

    fun logMeal(mealDetail: DailyConsumptionDetail) {
        _logState.value = LogMealState.Loading
        viewModelScope.launch {
            try {
                val token = auth.currentUser?.getIdToken(false)?.await()?.token ?: throw Exception("Sesi tidak valid.")

                consumptionRepo.logMeal(token, mealDetail)
                _logState.value = LogMealState.Success
            } catch (e: Exception) {
                Log.e("LogMealViewModel", "Failed to log meal", e)
                _logState.value = LogMealState.Error(e.message ?: "Gagal mencatat makanan.")
            }
        }
    }

    fun onLogFinished() {
        _logState.value = LogMealState.Idle
    }
}