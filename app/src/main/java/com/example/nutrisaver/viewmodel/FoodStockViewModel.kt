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

sealed class FoodStockListState {
    object Loading : FoodStockListState()
    data class Success(val data: List<FoodStock>) : FoodStockListState()
    data class Error(val message: String) : FoodStockListState()
}

sealed class UpdateFoodStockState {
    object Idle : UpdateFoodStockState()
    object Loading : UpdateFoodStockState()
    object Success : UpdateFoodStockState()
    data class Error(val message: String) : UpdateFoodStockState()
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

    private val _foodStocks = MutableLiveData<FoodStockListState>()
    val foodStocks: LiveData<FoodStockListState> = _foodStocks

    private val _deleteState = MutableLiveData<Boolean>()
    val deleteState: LiveData<Boolean> = _deleteState

    private val _updateState = MutableLiveData<UpdateFoodStockState>(UpdateFoodStockState.Idle)
    val updateState: LiveData<UpdateFoodStockState> = _updateState

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

    fun loadFoodStock() {
        _foodStocks.value = FoodStockListState.Loading // Set state ke loading
        viewModelScope.launch {
            try {
                val token = auth.currentUser?.getIdToken(true)?.await()?.token
                if (token == null) {
                    _foodStocks.postValue(FoodStockListState.Error("Sesi tidak valid. Silakan login kembali."))
                    return@launch
                }

                // Panggil repository untuk mendapatkan data
                val stocks = foodStockRepo.getFoodStock(token)
                _foodStocks.postValue(FoodStockListState.Success(stocks))

            } catch (e: Exception) {
                _foodStocks.postValue(FoodStockListState.Error(e.message ?: "Gagal memuat data stok."))
            }
        }
    }

    fun deleteFoodStock(id: Int) {
        viewModelScope.launch {
            try {
                val token = auth.currentUser?.getIdToken(true)?.await()?.token
                if (token == null) {
                    _error.postValue("Sesi tidak valid.")
                    return@launch
                }

                // Panggil repository untuk menghapus
                foodStockRepo.deleteFoodStock(token, id)

                // PENTING: Refresh daftar setelah berhasil menghapus.
                // Cara paling mudah adalah memuat ulang semua data.
                loadFoodStock()

            } catch (e: Exception) {
                _error.postValue("Gagal menghapus stok: ${e.message}")
            }
        }
    }

    fun updateFoodStockQuantity(id: Int, quantity: Float) {
        _updateState.value = UpdateFoodStockState.Loading
        viewModelScope.launch {
            try {
                val token = auth.currentUser?.getIdToken(true)?.await()?.token
                if (token == null) throw Exception("Sesi tidak valid.")

                foodStockRepo.updateFoodStockQuantity(token, id, quantity)
                _updateState.postValue(UpdateFoodStockState.Success)
            } catch (e: Exception) {
                _updateState.postValue(UpdateFoodStockState.Error(e.message ?: "Gagal update stok."))
            }
        }
    }

    // Fungsi untuk mereset state update
    fun onUpdateFinished() {
        _updateState.value = UpdateFoodStockState.Idle
    }

    // Fungsi untuk mereset state setelah navigasi atau menampilkan pesan
    fun onAddFinished() {
        _addState.value = AddFoodStockState.Idle
    }
}