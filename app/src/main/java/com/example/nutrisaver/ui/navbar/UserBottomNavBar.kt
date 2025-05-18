package com.example.nutrisaver.ui.navbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.theme.OpenSans

@Composable
fun UserBottomNavBar(navController: NavController) {
    val items = listOf(
        UserBottomNavItem("dashboard", R.drawable.dashboard, "Dashboard"),
        UserBottomNavItem("foodstock", R.drawable.foodstock, "Food Stock"),
        UserBottomNavItem("recipe", R.drawable.recipe, "Recipes"),
        UserBottomNavItem("profile", R.drawable.profile, "Profile"),

    )
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.bg))
            .height(88.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.label,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = item.label,
                    fontSize = 16.sp, // Now you can go bigger
                    fontWeight = FontWeight.Bold,
                    fontFamily = OpenSans,
                    maxLines = 1
                )
            }
        }
    }
}

data class UserBottomNavItem(val route: String, val iconRes: Int, val label: String)