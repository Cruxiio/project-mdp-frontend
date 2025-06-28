package com.example.nutrisaver.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrisaver.data.model.HealthArticle
import com.example.nutrisaver.data.repositories.HealthArticleRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Sealed class untuk merepresentasikan state dari daftar artikel di UI.
 */
sealed class HealthArticleListState {
    object Loading : HealthArticleListState()
    data class Success(val data: List<HealthArticle>) : HealthArticleListState()
    data class Error(val message: String) : HealthArticleListState()
}

class HealthArticleViewModel(
    private val healthArticleRepo: HealthArticleRepo
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _articlesState = MutableLiveData<HealthArticleListState>()
    val articlesState: LiveData<HealthArticleListState> = _articlesState

    init {
        // Muat semua artikel saat ViewModel pertama kali dibuat
        loadHealthArticles()
    }

    /**
     * Memuat artikel kesehatan dengan filter.
     * Mengubah nilai "All" dari UI menjadi null untuk API.
     */
    fun loadHealthArticles(
        targetGoal: String? = null,
        targetDietType: String? = null,
        title: String? = null
    ) {
        _articlesState.value = HealthArticleListState.Loading
        viewModelScope.launch {
            try {
                val token = auth.currentUser?.getIdToken(true)?.await()?.token
                if (token == null) {
                    _articlesState.postValue(HealthArticleListState.Error("Sesi tidak valid. Silakan login kembali."))
                    return@launch
                }

                // Konversi nilai filter dari UI ke nilai untuk API
                val apiGoal = if (targetGoal == "All") null else targetGoal
                val apiDiet = if (targetDietType == "All") null else targetDietType
                val apiTitle = if (title.isNullOrBlank()) null else title

                val articles = healthArticleRepo.getHealthArticles(token, apiGoal, apiDiet, apiTitle)
                _articlesState.postValue(HealthArticleListState.Success(articles))
            } catch (e: Exception) {
                _articlesState.postValue(HealthArticleListState.Error(e.message ?: "Gagal memuat artikel."))
            }
        }
    }
}