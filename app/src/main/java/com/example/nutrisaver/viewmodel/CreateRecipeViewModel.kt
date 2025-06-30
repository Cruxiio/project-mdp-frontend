// Buat file baru: viewmodel/CreateRecipeViewModel.kt

package com.example.nutrisaver.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrisaver.data.model.FoodStock
import com.example.nutrisaver.data.model.RecipeDetail
import com.example.nutrisaver.data.repositories.FoodStockRepo
import com.example.nutrisaver.data.repositories.RecipeRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Class untuk merepresentasikan bahan yang sudah dipilih beserta jumlah yang akan digunakan
data class ChosenIngredient(
    val foodStock: FoodStock,
    var amountToUse: Float
)

class CreateRecipeViewModel(
    private val foodStockRepo: FoodStockRepo,
    private val recipeRepo: RecipeRepo
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // State untuk semua food stock yang dimiliki user (untuk pilihan)
    private val _userFoodStock = MutableLiveData<List<FoodStock>>()
    val userFoodStock: LiveData<List<FoodStock>> = _userFoodStock

    // State untuk daftar bahan yang telah dipilih untuk resep
    private val _chosenIngredients = MutableLiveData<List<ChosenIngredient>>(emptyList())
    val chosenIngredients: LiveData<List<ChosenIngredient>> = _chosenIngredients

    private val _recipeDetailState = MutableLiveData<RecipeDetail>(null)
    val recipeDetailState: LiveData<RecipeDetail> = _recipeDetailState

    // State untuk error
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Memuat semua food stock milik user dari repository
    fun loadUserFoodStock() {
        viewModelScope.launch {
            try {
                val token = auth.currentUser?.getIdToken(true)?.await()?.token
                if (token == null) {
                    _error.postValue("Sesi tidak valid.")
                    return@launch
                }
                _userFoodStock.postValue(foodStockRepo.getFoodStock(token))
            } catch (e: Exception) {
                _error.postValue("Gagal memuat stok makanan: ${e.message}")
            }
        }
    }

    fun addIngredientToRecipe(foodStock: FoodStock, amount: Float) {
        val currentList = _chosenIngredients.value ?: emptyList()
        val existingIngredient = currentList.find { it.foodStock.id == foodStock.id }

        val newList = if (existingIngredient != null) {
            // JIKA SUDAH ADA: Buat list baru dengan item yang sudah diupdate
            currentList.map {
                if (it.foodStock.id == existingIngredient.foodStock.id) {
                    val newAmount = (it.amountToUse + amount).coerceIn(0f, foodStock.quantity)
                    it.copy(amountToUse = newAmount)
                } else {
                    it
                }
            }
        } else {
            // JIKA BELUM ADA: Buat list baru dengan tambahan item baru
            val newAmount = amount.coerceIn(0f, foodStock.quantity)
            currentList + ChosenIngredient(foodStock = foodStock, amountToUse = newAmount)
        }
        // Set LiveData dengan instance list yang baru
        _chosenIngredients.value = newList
    }

    // Mengupdate kuantitas bahan yang sudah ada di daftar
    fun updateIngredientQuantity(chosenIngredient: ChosenIngredient, delta: Float) {
        val currentList = _chosenIngredients.value ?: emptyList()

        // Buat list BARU dengan memetakan (map) setiap item dari list lama.
        val newList = currentList.map { item ->
            // Jika item ini adalah yang ingin kita ubah...
            if (item.foodStock.id == chosenIngredient.foodStock.id) {
                // ...hitung jumlah barunya
                val newAmount = (item.amountToUse + delta).coerceIn(0f, item.foodStock.quantity)
                // ...lalu kembalikan SALINAN BARU dari item tersebut dengan jumlah yang sudah diupdate.
                item.copy(amountToUse = newAmount)
            } else {
                // Jika bukan item yang dituju, kembalikan item tersebut apa adanya.
                item
            }
        }

        // Set nilai LiveData dengan INSTANCE LIST YANG BARU untuk memicu update UI.
        _chosenIngredients.value = newList
    }

    // Menghapus bahan dari daftar resep
    fun removeIngredientFromRecipe(chosenIngredient: ChosenIngredient) {
        val currentList = _chosenIngredients.value?.toMutableList() ?: return
        currentList.removeAll { it.foodStock.id == chosenIngredient.foodStock.id }
        _chosenIngredients.value = currentList
    }

    fun getRecipeDetail(recipeId: Int){
        viewModelScope.launch {
            try {
//              // ambil token user
                val token = auth.currentUser?.getIdToken(false)?.await()?.token ?: throw Exception("Sesi tidak valid.")
                _recipeDetailState.value = recipeRepo.getRecipeDetail(token,recipeId) // ambil data recipe detail

            }
            catch (e: Exception) {
                Log.e("RecipeViewModel", "Fetch Recipe Detail Failed", e)
//                _recipeStatusState.value = RecipeStatusState.Error(e.message ?: "Gagal mendapatkan recipe detail")
            }
        }
    }

    fun reduceIngredientQty() {
        viewModelScope.launch {
            try {
//              // ambil token user
                val token = auth.currentUser?.getIdToken(false)?.await()?.token ?: throw Exception("Sesi tidak valid.")
                if (_chosenIngredients.value.size > 0){
                    var idx = 0;
                    _chosenIngredients.value.forEach {
                        idx++;
                        Log.d("createRecipeDetail", "$idx. ${it.foodStock}")
                        foodStockRepo.updateFoodStockQuantity(token, it.foodStock.id ?: -1, it.amountToUse)
                    }
                }
                // reset
                _chosenIngredients.value = emptyList()

            }
            catch (e: Exception) {
                Log.e("RecipeViewModel", "Update ingredient Qty Failed", e)
//                _recipeStatusState.value = RecipeStatusState.Error(e.message ?: "Gagal mendapatkan recipe detail")
            }
        }
    }
}