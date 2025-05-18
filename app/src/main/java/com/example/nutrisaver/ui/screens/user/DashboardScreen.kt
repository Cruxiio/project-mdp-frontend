package com.example.nutrisaver.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.navbar.UserBottomNavBar

@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            UserBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        DashboardContent(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun DashboardContent(modifier: Modifier = Modifier) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    Box(modifier = modifier
        .fillMaxSize()
        .background(backgroundGradient)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Hi, User!"
                )
                IconButton(onClick = { /* your action here */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.notification),
                        contentDescription = "My Image Button"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 2.dp)
            Spacer(modifier = Modifier.height(10.dp))

            
        }
    }
}