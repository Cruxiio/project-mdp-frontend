package com.example.nutrisaver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.nutrisaver.viewmodel.AdminUsersViewModel
import com.example.nutrisaver.viewmodel.AuthViewModel
import com.example.nutrisaver.viewmodel.FoodStockViewModel
import com.example.nutrisaver.viewmodel.HealthArticleViewModel
import com.example.nutrisaver.viewmodel.LogMealViewModel
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
            val adminRepository = application.adminRepo
            val recipeRepository = application.recipeRepo
            val weightLogRepository = application.weightLogRepo
            val healthArticleRepository = application.healthArticleRepo

            when {
                isAssignableFrom(AuthViewModel::class.java) ->
                    AuthViewModel(commonRepository,authRepository,application)
                isAssignableFrom(UserViewModel::class.java) ->
                    UserViewModel(commonRepository,authRepository,consumRepository,  weightLogRepository, foodStockRepository, application)
                isAssignableFrom(FoodStockViewModel::class.java) ->
                    FoodStockViewModel(ingredientRepository, foodStockRepository)
                isAssignableFrom(AdminUsersViewModel::class.java) ->
                    AdminUsersViewModel(adminRepository, application)
                isAssignableFrom(LogMealViewModel::class.java) ->
                    LogMealViewModel(recipeRepository, consumRepository, application)
                isAssignableFrom(HealthArticleViewModel::class.java) ->
                    HealthArticleViewModel(healthArticleRepository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}