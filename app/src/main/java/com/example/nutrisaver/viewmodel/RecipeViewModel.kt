package com.example.nutrisaver.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutrisaver.data.model.RecipeDetail
import com.example.nutrisaver.data.model.RecipePlain
import com.example.nutrisaver.data.repositories.CommonRepo
import com.example.nutrisaver.data.repositories.FoodStockRepo
import com.example.nutrisaver.data.repositories.RecipeRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// State untuk proses Logging Makanan
sealed class RecipeStatusState {
    object Idle : RecipeStatusState() // State Awal
    object Loading : RecipeStatusState()
    object Success : RecipeStatusState()
    data class Error(val message: String) : RecipeStatusState()
}

class RecipeViewModel(
    private val commonRepo: CommonRepo,
    private val recipeRepo: RecipeRepo,
    private val foodStockRepo: FoodStockRepo,
    application: Application
): AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _recipeStatusState = MutableLiveData<RecipeStatusState>(RecipeStatusState.Idle)
    val recipeStatusState: LiveData<RecipeStatusState> = _recipeStatusState

    private val _listRecipeState = MutableLiveData<List<RecipePlain>>(null)
    val listRecipeState: LiveData<List<RecipePlain>> = _listRecipeState

    private val _recipeDetailState = MutableLiveData<RecipeDetail>(null)
    val recipeDetailState: LiveData<RecipeDetail> = _recipeDetailState


    fun getAllRecipe(keyword: String, type: String, page: Int, perpage:Int) {
//        set status ke loading
        _recipeStatusState.value = RecipeStatusState.Loading
        viewModelScope.launch {
            try {
//              // ambil token user
                val token = auth.currentUser?.getIdToken(false)?.await()?.token ?: throw Exception("Sesi tidak valid.")
                _listRecipeState.value = recipeRepo.getAllRecipes(token, keyword, type, page, perpage) // ambil data recipe
                _recipeStatusState.value = RecipeStatusState.Success
            }
            catch (e: Exception) {
                Log.e("RecipeViewModel", "Recipe search failed", e)
                _recipeStatusState.value = RecipeStatusState.Error(e.message ?: "Gagal mencari resep.")
            }
        }
    }

    fun getRecipeDetail(recipeId: Int){
        _recipeStatusState.value = RecipeStatusState.Loading
        viewModelScope.launch {
            try {
//              // ambil token user
                val token = auth.currentUser?.getIdToken(false)?.await()?.token ?: throw Exception("Sesi tidak valid.")
                _recipeDetailState.value = recipeRepo.getRecipeDetail(token,recipeId) // ambil data recipe detail
                if (_recipeDetailState.value == null){
                    _recipeStatusState.value = RecipeStatusState.Error("Id tidak ditemukan")
                }
                else{
                    _recipeStatusState.value = RecipeStatusState.Success
                }

            }
            catch (e: Exception) {
                Log.e("RecipeViewModel", "Fetch Recipe Detail Failed", e)
                _recipeStatusState.value = RecipeStatusState.Error(e.message ?: "Gagal mendapatkan recipe detail")
            }
        }
    }
}