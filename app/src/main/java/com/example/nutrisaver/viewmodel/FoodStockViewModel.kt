package com.example.nutrisaver.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrisaver.data.model.FoodStock
import com.example.nutrisaver.data.model.Ingredient
import com.example.nutrisaver.data.repositories.FoodStockRepo
import com.example.nutrisaver.data.repositories.IngredientRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

sealed class AddFoodStockState {
    object Idle : AddFoodStockState() // State awal
    object Loading : AddFoodStockState()
    object Success : AddFoodStockState()
    data class Error(val message: String) : AddFoodStockState()
}

class FoodStockViewModel(
    private val ingredientRepo: IngredientRepo,
    private val foodStockRepo: FoodStockRepo
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _allIngredients = MutableLiveData<List<Ingredient>>()
    val allIngredients: LiveData<List<Ingredient>> = _allIngredients

    private val _addState = MutableLiveData<AddFoodStockState>(AddFoodStockState.Idle)
    val addState: LiveData<AddFoodStockState> = _addState

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadAllIngredients() {
        viewModelScope.launch {
            try {
                // Ambil token dari user yang sedang login
                val firebaseUser = auth.currentUser
                val token = firebaseUser?.getIdToken(true)?.await()?.token

                if (token == null) {
                    _error.postValue("Sesi tidak valid. Silakan login kembali.")
                    return@launch
                }

                // Panggil repository dengan token
                val ingredients = ingredientRepo.getIngredients(token)
                _allIngredients.postValue(ingredients)

            } catch (e: Exception) {
                _error.postValue("Gagal memuat bahan makanan: ${e.message}")
            }
        }
    }

    fun addFoodStock(
        selectedIngredient: Ingredient,
        quantity: Float,
        unit: String,
        expiredDate: LocalDate?,
        startRemindDate: LocalDate? // Bisa null, backend akan menghitungnya
    ) {
        _addState.value = AddFoodStockState.Loading // Set state menjadi loading
        viewModelScope.launch {
            try {
                val token = auth.currentUser?.getIdToken(true)?.await()?.token
                val userId = auth.currentUser?.uid
                if (token == null || userId == null) {
                    throw Exception("Sesi tidak valid. Silakan login kembali.")
                }

                // Buat objek FoodStock dari input UI
                val newFoodStock = FoodStock(
                    id = null,
                    userId = 0, // Backend akan menggunakan ID dari token, ini bisa diabaikan
                    ingredientId = selectedIngredient.id,
                    name = selectedIngredient.name,
                    imageUrl = selectedIngredient.imageUrl,
                    quantity = quantity,
                    unit = unit,
                    expiredDate = expiredDate,
                    startRemindDate = startRemindDate
                )

                // Panggil repository untuk mengirim data ke backend
                foodStockRepo.addFoodStock(token, newFoodStock)

                _addState.value = AddFoodStockState.Success

            } catch (e: Exception) {
                _addState.value = AddFoodStockState.Error(e.message ?: "Gagal menambahkan stok makanan.")
            }
        }
    }

    // Fungsi untuk mereset state setelah navigasi atau menampilkan pesan
    fun onAddFinished() {
        _addState.value = AddFoodStockState.Idle
    }
}