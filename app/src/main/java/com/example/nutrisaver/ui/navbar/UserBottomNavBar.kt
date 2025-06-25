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
fun UserBottomNavBar(navController: NavController) {
    val items = listOf(
        UserBottomNavItem("dashboard", R.drawable.dashboard, "Dashboard"),
        UserBottomNavItem("foodstock", R.drawable.foodstock, "Food Stock"),
        UserBottomNavItem("recipe", R.drawable.recipe, "Recipes"),
        UserBottomNavItem("profile", R.drawable.profile, "Profile"),
    )
    // Get the current route from the back stack entry
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
            // Determine if the current item is selected
            val isSelected = currentRoute == item.route

            val textColor = if (isSelected) colorResource(R.color.green_dark) else Color.Black // Assuming you have a green for selected and another for unselected text
            val iconTint = if (isSelected) colorResource(R.color.green_dark) else Color.Black // Assuming you have an icon tint for unselected

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        // Avoid re-navigating if already on the selected tab
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to avoid building up a large backstack
                                // when re-selecting the same item.
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true // Save the state of the popped-up destination and its child destinations
                                }
                                // Avoid multiple copies of the same destination when re-selecting the same item
                                launchSingleTop = true
                                // Restore state when re-selecting a previously selected item
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

data class UserBottomNavItem(val route: String, val iconRes: Int, val label: String)