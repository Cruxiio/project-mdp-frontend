package com.example.nutrisaver.ui.screens.user

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.nutrisaver.ui.navbar.UserBottomNavBar

@Composable
fun FoodStockScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            UserBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        FoodStockContent(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun FoodStockContent(modifier: Modifier = Modifier) {

}

@Composable
fun FoodStockItem() {

}