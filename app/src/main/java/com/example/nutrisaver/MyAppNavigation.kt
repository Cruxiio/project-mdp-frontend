package com.example.nutrisaver

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.nutrisaver.ui.screens.admin.AllUserScreen
import com.example.nutrisaver.ui.screens.admin.ApplicationScreen
import com.example.nutrisaver.ui.screens.admin.HealthArticleScreen
import com.example.nutrisaver.ui.screens.auth.LoginScreen
import com.example.nutrisaver.ui.screens.auth.RegisterDetailScreen
import com.example.nutrisaver.ui.screens.auth.RegisterScreen
import com.example.nutrisaver.ui.screens.user.AddFoodStockScreen
import com.example.nutrisaver.ui.screens.user.CreateRecipeScreen
import com.example.nutrisaver.ui.screens.user.DashboardScreen
import com.example.nutrisaver.ui.screens.user.EditInformationScreen
import com.example.nutrisaver.ui.screens.user.EditProfileScreen
import com.example.nutrisaver.ui.screens.user.FoodStockScreen
import com.example.nutrisaver.ui.screens.user.LogHistoryScreen
import com.example.nutrisaver.ui.screens.user.LogMealScreen
import com.example.nutrisaver.ui.screens.user.NotificationScreen
import com.example.nutrisaver.ui.screens.user.ProfileScreen
import com.example.nutrisaver.ui.screens.user.RecipeDetailScreen
import com.example.nutrisaver.ui.screens.user.RecipeScreen
import com.example.nutrisaver.ui.screens.user.SearchResultScreen

@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // buat nested navigation
    NavHost(navController = navController, startDestination = "admin") {

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
            composable("logmeal/{mealType}") {backStackEntry ->
                val mealType = backStackEntry.arguments?.getString("mealType") ?: ""
                LogMealScreen(navController = navController, mealType = mealType)
            }
            composable("loghistory") {
                LogHistoryScreen(navController = navController)
            }
            composable("foodstock") {
                FoodStockScreen(navController = navController)
            }
            composable("addfoodstock") {
                AddFoodStockScreen(navController = navController)
            }
            composable("recipe") {
                RecipeScreen(navController = navController)
            }
            composable("searchresults") {
                SearchResultScreen(navController = navController)
            }
            composable("recipedetail") {
                RecipeDetailScreen(navController = navController)
            }
            composable("createrecipe") {
                CreateRecipeScreen(navController = navController)
            }
            composable("profile") {
                ProfileScreen(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
            composable("editinformation") {
                EditInformationScreen(navController = navController)
            }
            composable("editprofile") {
                EditProfileScreen(navController = navController)
            }
            composable("notification") {
                NotificationScreen(navController = navController)
            }
        }

        navigation(startDestination = "alluser", route = "admin") {
            composable("alluser") {
                AllUserScreen(navController = navController)
            }
            composable("application") {
                ApplicationScreen(navController = navController)
            }
            composable("healtharticle") {
                HealthArticleScreen(navController = navController)
            }
        }
    }
}
