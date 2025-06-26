package com.example.nutrisaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.nutrisaver.ui.theme.NutriSaverTheme
import com.example.nutrisaver.viewmodel.AdminUsersViewModel
import com.example.nutrisaver.viewmodel.AuthViewModel
import com.example.nutrisaver.viewmodel.FoodStockViewModel
import com.example.nutrisaver.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriSaverTheme {
                val navController = rememberNavController()

                // Buat instance CustomViewModelFactory
                val factory = CustomViewModelFactory
                // define view models
                val authViewModel: AuthViewModel = viewModel(factory=factory)
                val userViewModel: UserViewModel = viewModel(factory=factory)
                val foodStockViewModel : FoodStockViewModel = viewModel(factory=factory)
                val adminUsersViewModel: AdminUsersViewModel = viewModel(factory=factory)

                MyAppNavigation(
                    navController = navController,
                    authViewModel = authViewModel,
                    userViewModel = userViewModel,
                    adminUsersViewModel = adminUsersViewModel,
                    foodStockViewModel = foodStockViewModel,
                )
            }
        }
    }
}
