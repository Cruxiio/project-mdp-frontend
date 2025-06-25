package com.example.nutrisaver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.nutrisaver.viewmodel.AuthViewModel
import com.example.nutrisaver.viewmodel.FoodStockViewModel
import com.example.nutrisaver.viewmodel.UserViewModel

val CustomViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        with(modelClass) {
            val application = checkNotNull(extras[APPLICATION_KEY]) as NutriSaverApplication
            val authRepository = application.authRepo
            val commonRepository = application.commonRepo
            val consumRepository = application.consumRepo
            val ingredientRepository = application.ingredientRepo
            val foodStockRepository = application.foodStockRepo

            when {
                isAssignableFrom(AuthViewModel::class.java) ->
                    AuthViewModel(commonRepository,authRepository,application)
                isAssignableFrom(UserViewModel::class.java) ->
                    UserViewModel(commonRepository,authRepository,consumRepository, application)
                isAssignableFrom(FoodStockViewModel::class.java) ->
                    FoodStockViewModel(ingredientRepository, foodStockRepository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}