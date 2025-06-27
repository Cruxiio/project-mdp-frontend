package com.example.nutrisaver.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrisaver.data.model.DailyConsumptionDetail
import com.example.nutrisaver.data.model.Recipe
import com.example.nutrisaver.data.repositories.ConsumptionRepo
import com.example.nutrisaver.data.repositories.RecipeRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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