package com.example.nutrisaver

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nutrisaver.ui.screens.admin.AdminHealthArticleScreen
import com.example.nutrisaver.ui.screens.admin.AllUserScreen
import com.example.nutrisaver.ui.screens.admin.ApplicationScreen
//import com.example.nutrisaver.ui.screens.admin.HealthArticleScreen
import com.example.nutrisaver.ui.screens.auth.LoginScreen
import com.example.nutrisaver.ui.screens.auth.RegisterDetailScreen
import com.example.nutrisaver.ui.screens.auth.RegisterScreen
import com.example.nutrisaver.ui.screens.user.AddFoodStockScreen
import com.example.nutrisaver.ui.screens.user.CreateRecipeScreen
import com.example.nutrisaver.ui.screens.user.DashboardScreen
import com.example.nutrisaver.ui.screens.user.EditInformationScreen
import com.example.nutrisaver.ui.screens.user.EditProfileScreen
import com.example.nutrisaver.ui.screens.user.FavoriteRecipeScreen
import com.example.nutrisaver.ui.screens.user.FoodStockScreen
import com.example.nutrisaver.ui.screens.user.LogHistoryScreen
import com.example.nutrisaver.ui.screens.user.LogMealScreen
import com.example.nutrisaver.ui.screens.user.NotificationScreen
import com.example.nutrisaver.ui.screens.user.ProfileScreen
import com.example.nutrisaver.ui.screens.user.RecipeDetailScreen
import com.example.nutrisaver.ui.screens.user.RecipeScreen
import com.example.nutrisaver.ui.screens.user.SearchResultScreen
import com.example.nutrisaver.ui.screens.user.UserHealthArticleScreen
import com.example.nutrisaver.viewmodel.AdminUsersViewModel
import com.example.nutrisaver.viewmodel.AuthViewModel
import com.example.nutrisaver.viewmodel.FoodStockViewModel
import com.example.nutrisaver.viewmodel.HealthArticleViewModel
import com.example.nutrisaver.viewmodel.LogMealViewModel
import com.example.nutrisaver.viewmodel.UserViewModel

@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    foodStockViewModel: FoodStockViewModel,
    adminUsersViewModel: AdminUsersViewModel,
    logMealViewModel: LogMealViewModel
) {
    // buat nested navigation
    val healthArticleViewModel: HealthArticleViewModel = viewModel(factory = CustomViewModelFactory)
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
                DashboardScreen(navController = navController, userViewModel = userViewModel)
            }
            composable("logmeal/{mealType}") {backStackEntry ->
                val mealType = backStackEntry.arguments?.getString("mealType") ?: ""
                LogMealScreen(navController = navController, mealType = mealType, logMealViewModel = logMealViewModel)
            }
            composable("loghistory") {
                LogHistoryScreen(navController = navController, logMealViewModel = logMealViewModel)
            }
            composable("userhealtharticle") {
                UserHealthArticleScreen(
                    navController = navController,
                    healthArticleViewModel = healthArticleViewModel
                )
            }
            composable("foodstock") {
                FoodStockScreen(navController = navController, foodStockViewModel = foodStockViewModel)
            }
            composable(
                // 1. Definisikan route dengan argumen opsional 'foodStockJson'
                route = "add_food_stock_screen?foodStockJson={foodStockJson}",
                arguments = listOf(navArgument("foodStockJson") {
                    type = NavType.StringType
                    nullable = true // Tandai bahwa argumen ini boleh null (untuk mode Add)
                    defaultValue = null
                })
            ) { backStackEntry ->
                // 2. Ambil argumen dari backStackEntry
                val foodStockJson = backStackEntry.arguments?.getString("foodStockJson")

                // 3. Kirim argumen ke AddFoodStockScreen
                AddFoodStockScreen(
                    navController = navController,
                    foodStockViewModel = foodStockViewModel,
                    foodStockJson = foodStockJson?.let { Uri.decode(it) } // Decode URL-encoded string
                )
            }
            composable("recipe") {
                RecipeScreen(navController = navController)
            }
            composable("favoriterecipe") {
                FavoriteRecipeScreen(navController = navController)
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
                    modifier = modifier,
                    navController = navController,
                    authViewModel = authViewModel,
                    userViewModel = userViewModel
                )
            }
            composable("editinformation") {
                EditInformationScreen(
                    navController = navController,
                    userViewModel = userViewModel
                )
            }
            composable("editprofile") {
                EditProfileScreen(
                    navController = navController,
                    userViewModel = userViewModel
                )
            }
            composable("notification") {
                NotificationScreen(navController = navController)
            }
        }

        navigation(startDestination = "alluser", route = "admin") {
            composable("alluser") {
                AllUserScreen(
                    navController = navController,
                    adminUsersViewModel = adminUsersViewModel
                )
            }
            composable("application") {
                ApplicationScreen(navController = navController)
            }
            composable("adminhealtharticle") {
                AdminHealthArticleScreen(
                    navController = navController,
                    healthArticleViewModel = healthArticleViewModel
                )
            }
        }
    }
}
