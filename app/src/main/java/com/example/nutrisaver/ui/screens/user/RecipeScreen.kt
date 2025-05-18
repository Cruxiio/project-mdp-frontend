package com.example.nutrisaver.ui.screens.user

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.nutrisaver.ui.navbar.UserBottomNavBar

@Composable
fun RecipeScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            UserBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        RecipeContent(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun RecipeContent(modifier: Modifier = Modifier) {

}
