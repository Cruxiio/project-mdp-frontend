package com.example.nutrisaver.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutrisaver.data.model.RecipeDetail
import com.example.nutrisaver.data.model.RecipeFavorite
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

    private val _listRecipeFavoriteState = MutableLiveData<List<RecipeFavorite>>(null)
    val listRecipeFavoriteState: LiveData<List<RecipeFavorite>> = _listRecipeFavoriteState

    private val _recipeFavoriteState = MutableLiveData<Boolean>(false)
    val recipeFavoriteState: LiveData<Boolean> = _recipeFavoriteState


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

    fun getFavoriteRecipes(keyword: String) {
        _recipeStatusState.value = RecipeStatusState.Loading
        viewModelScope.launch {
            try {
                val token = auth.currentUser?.getIdToken(false)?.await()?.token ?: throw Exception("Sesi tidak valid.")
                _listRecipeFavoriteState.value = recipeRepo.getFavoriteRecipes(token, keyword) // ambil data recipe
                _recipeStatusState.value = RecipeStatusState.Success
            }
            catch (e: Exception) {
                Log.e("RecipeViewModel", "Fetch Favorite Recipe Failed", e)
                _recipeStatusState.value = RecipeStatusState.Error(e.message ?: "Gagal mendapatkan recipe favorit")
            }
        }
    }

    fun checkUserFavoriteRecipeExist(recipeId: Int){
        _recipeStatusState.value = RecipeStatusState.Loading
        viewModelScope.launch {
            try {
                val token = auth.currentUser?.getIdToken(false)?.await()?.token ?: throw Exception("Sesi tidak valid.")
                _recipeFavoriteState.value = recipeRepo.isUserFavoriteRecipeExist(token, recipeId) // ambil data recipe
                _recipeStatusState.value = RecipeStatusState.Success
            }
            catch (e: Exception) {
                Log.e("RecipeViewModel", "Check Favorite Recipe Exist Failed", e)
                _recipeStatusState.value = RecipeStatusState.Error(e.message ?: "Gagal check recipe favorit")
            }
        }
    }

    fun createOrDeleteFavoriteRecipes(recipeId: Int, title: String, image: String, calories: Double, protein: Double, fat: Double, carbs: Double){
        _recipeStatusState.value = RecipeStatusState.Loading
        viewModelScope.launch {
            try {
                val token = auth.currentUser?.getIdToken(false)?.await()?.token ?: throw Exception("Sesi tidak valid.")

                if (_recipeFavoriteState.value == true){
                    // jika sudah ada di data favorite recipes, maka hapus
                    recipeRepo.deleteFavoriteRecipes(token, recipeId) //hapus data fav recipe
                }
                else{
                    // jika belum add ke db
                    val newFavRecipe = RecipeFavorite(
                        id = -1, // ini nda penting soale sebelum dikirm ke BE idnya nda diambil
                        recipeId = recipeId,
                        title = title,
                        image = image,
                        calories = calories,
                        protein = protein,
                        fat = fat,
                        carbs = carbs
                    )
                    recipeRepo.addFavoriteRecipes(token, newFavRecipe)
                }
                // toggle nilai fav state
                _recipeFavoriteState.value = !_recipeFavoriteState.value
                _recipeStatusState.value = RecipeStatusState.Success
            }
            catch (e: Exception) {
                Log.e("RecipeViewModel", "Create or Delete Favorite Recipe Failed", e)
                _recipeStatusState.value = RecipeStatusState.Error(e.message ?: "Gagal create atau delete recipe favorit")
            }
        }
    }

    fun deleteRecipeFav(recipeId: Int) {
        _recipeStatusState.value = RecipeStatusState.Loading
        viewModelScope.launch {
            try{
                val token = auth.currentUser?.getIdToken(false)?.await()?.token ?: throw Exception("Sesi tidak valid.")
                recipeRepo.deleteFavoriteRecipes(token, recipeId) //hapus data fav recipe
                // refresh data favorite recipes
                _listRecipeFavoriteState.value = recipeRepo.getFavoriteRecipes(token, "")
                _recipeStatusState.value = RecipeStatusState.Success
            }
            catch (e: Exception) {
                Log.e("RecipeViewModel", "Create or Delete Favorite Recipe Failed", e)
                _recipeStatusState.value = RecipeStatusState.Error(e.message ?: "Gagal create atau delete recipe favorit")
            }
        }
    }

}