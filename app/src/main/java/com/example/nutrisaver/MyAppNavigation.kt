package com.example.nutrisaver

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MyAppNavigation (modifier : Modifier  = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

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