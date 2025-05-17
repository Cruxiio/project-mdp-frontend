package com.example.nutrisaver

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nutrisaver.ui.screens.HomeScreen
import com.example.nutrisaver.ui.screens.LoginScreen
import com.example.nutrisaver.ui.screens.RegisterScreen

@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    NavHost(navController = navController, startDestination = "Login") {
        composable("Login") {
            LoginScreen(modifier, navController, authViewModel)
        }
        composable("Register") {
            RegisterScreen(modifier, navController, authViewModel)
        }
        composable("Home") {
            HomeScreen(modifier, navController, authViewModel)
        }
    }
}
