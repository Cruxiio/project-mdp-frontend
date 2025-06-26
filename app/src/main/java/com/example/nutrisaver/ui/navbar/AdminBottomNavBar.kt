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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
fun AdminBottomNavBar(navController: NavController) {
    val items = listOf(
        AdminBottomNavItem("alluser", R.drawable.profile, "Users", "admin"), // Add the graph route here
        AdminBottomNavItem("application", R.drawable.dashboard, "Application", "admin") // Add the graph route here
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
            val isSelected = currentRoute == item.route

            val textColor = if (isSelected) colorResource(R.color.green_dark) else Color.Black
            val iconTint = if (isSelected) colorResource(R.color.green_dark) else Color.Black

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the *admin graph*
                                // This ensures you don't pop destinations from other top-level graphs (like 'user')
                                popUpTo(item.graphRoute) { // Use the nested graph's route
                                    saveState = true
                                    inclusive = true // Pop the graph route itself if needed
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.label,
                    modifier = Modifier.size(36.dp),
                    colorFilter = ColorFilter.tint(iconTint)
                )
                Text(
                    text = item.label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OpenSans,
                    maxLines = 1,
                    color = textColor
                )
            }
        }
    }
}

// Update data class to include graphRoute
data class AdminBottomNavItem(
    val route: String,
    val iconRes: Int,
    val label: String,
    val graphRoute: String // New field to specify the nested graph's route
)