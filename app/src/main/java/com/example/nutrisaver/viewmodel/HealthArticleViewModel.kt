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

// Sealed class untuk operasi Create, Update, Delete (CRUD)
sealed class CrudState {
    object Idle : CrudState() // State awal, tidak ada operasi
    object Loading : CrudState()
    object Success : CrudState()
    data class Error(val message: String) : CrudState()
}

class HealthArticleViewModel(
    private val healthArticleRepo: HealthArticleRepo
) : ViewModel() {

    // LiveData untuk daftar artikel (operasi GET)
    private val _articlesState = MutableLiveData<HealthArticleListState>()
    val articlesState: LiveData<HealthArticleListState> = _articlesState

    // LiveData untuk operasi CUD (Create, Update, Delete)
    // Dibuat nullable agar bisa di-reset ke state awal (Idle)
    private val _crudState = MutableLiveData<CrudState?>(null)
    val crudState: LiveData<CrudState?> = _crudState

    init {
        // Muat semua artikel saat ViewModel pertama kali dibuat
        loadHealthArticles()
    }

    /**
     * [GET] Memuat artikel kesehatan dengan filter.
     */
    fun loadHealthArticles(
        targetGoal: String? = null,
        targetDietType: String? = null,
        title: String? = null
    ) {
        _articlesState.value = HealthArticleListState.Loading
        viewModelScope.launch {
            try {
                // Konversi nilai filter dari UI ("All" atau string kosong) menjadi null untuk API
                val apiGoal = if (targetGoal == "All") null else targetGoal
                val apiDiet = if (targetDietType == "All") null else targetDietType
                val apiTitle = if (title.isNullOrBlank()) null else title

                // Panggil repository tanpa token
                val articles = healthArticleRepo.getHealthArticles(apiGoal, apiDiet, apiTitle)
                _articlesState.postValue(HealthArticleListState.Success(articles))

            } catch (e: Exception) {
                _articlesState.postValue(HealthArticleListState.Error(e.message ?: "Gagal memuat artikel."))
            }
        }
    }

    /**
     * [CREATE] Membuat artikel baru. Fungsi ini memerlukan token.
     */
    fun createArticle(title: String, content: String, targetGoal: String, targetDietType: String, createdBy: String) {
        _crudState.value = CrudState.Loading
        viewModelScope.launch {
            try {
                val token = FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
                if (token == null) throw Exception("Sesi tidak valid. Silakan login kembali.")

                healthArticleRepo.createArticle(token, title, content, targetGoal, targetDietType, createdBy)
                _crudState.postValue(CrudState.Success)
                // Refresh daftar artikel setelah berhasil membuat yang baru
                loadHealthArticles()
            } catch (e: Exception) {
                _crudState.postValue(CrudState.Error(e.message ?: "Gagal membuat artikel."))
            }
        }
    }

    /**
     * [UPDATE] Memperbarui artikel yang ada. Fungsi ini memerlukan token.
     */
    fun updateArticle(
        articleId: Int,
        title: String?,
        content: String?,
        targetGoal: String?,
        targetDietType: String?,
        createdBy: String?
    ) {
        _crudState.value = CrudState.Loading
        viewModelScope.launch {
            try {
                val token = FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
                if (token == null) throw Exception("Sesi tidak valid. Silakan login kembali.")

                healthArticleRepo.updateArticle(token, articleId, title, content, targetGoal, targetDietType, createdBy)
                _crudState.postValue(CrudState.Success)
                // Refresh daftar artikel setelah berhasil update
                loadHealthArticles()
            } catch (e: Exception) {
                _crudState.postValue(CrudState.Error(e.message ?: "Gagal memperbarui artikel."))
            }
        }
    }

    /**
     * [DELETE] Menghapus artikel. Fungsi ini memerlukan token.
     */
    fun deleteArticle(articleId: Int) {
        _crudState.value = CrudState.Loading
        viewModelScope.launch {
            try {
                val token = FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
                if (token == null) throw Exception("Sesi tidak valid. Silakan login kembali.")

                healthArticleRepo.deleteArticle(token, articleId)
                _crudState.postValue(CrudState.Success)
                // Refresh daftar artikel setelah berhasil delete
                loadHealthArticles()
            } catch (e: Exception) {
                _crudState.postValue(CrudState.Error(e.message ?: "Gagal menghapus artikel."))
            }
        }
    }

    /**
     * Mereset state CUD setelah operasi selesai (misal: setelah Toast ditampilkan).
     */
    fun onCrudOperationFinished() {
        _crudState.value = null
    }
}