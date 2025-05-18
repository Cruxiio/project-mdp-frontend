package com.example.nutrisaver.ui.navbar

import androidx.compose.foundation.Image
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nutrisaver.R

@Composable
fun UserBottomNavBar(navController: NavController) {
    val items = listOf(
        UserBottomNavItem("dashboard", R.drawable.dashboard, "Dashboard"),
        UserBottomNavItem("food_stock", R.drawable.foodstock, "Food Stock"),
        UserBottomNavItem("recipes", R.drawable.recipe, "Recipes"),
        UserBottomNavItem("profile", R.drawable.profile, "Profile"),

    )
    val currentRoute = navController
        .currentBackStackEntryAsState().value?.destination?.route

    BottomNavigation {
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Image(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                }
            )
        }
    }
}

data class UserBottomNavItem(val route: String, val iconRes: Int, val label: String)