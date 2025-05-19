package com.example.nutrisaver

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.nutrisaver.ui.screens.auth.LoginScreen
import com.example.nutrisaver.ui.screens.auth.RegisterDetailScreen
import com.example.nutrisaver.ui.screens.auth.RegisterScreen
import com.example.nutrisaver.ui.screens.user.DashboardScreen
import com.example.nutrisaver.ui.screens.user.ProfileScreen

@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // buat nested navigation
    NavHost(navController = navController, startDestination = "auth") {

        // navigation antara login dan register
        navigation(startDestination = "login", route = "auth") {
            composable("login") {
                LoginScreen(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
            composable("register") {
                RegisterScreen(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
            composable("register-detail") {
                RegisterDetailScreen(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }

        // navigation pada halaman user.
        navigation(startDestination = "dashboard", route = "user") {
            composable("dashboard") {
                DashboardScreen(navController = navController)
            }
            composable("foodstock") {

            }
            composable("recipe") {

            }
            composable("profile") {
                ProfileScreen(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }

    }
}
