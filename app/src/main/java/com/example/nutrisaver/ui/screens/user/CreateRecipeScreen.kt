package com.example.nutrisaver.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans

@Composable
fun CreateRecipeScreen(navController: NavController) {
    Scaffold { innerPadding ->
        CreateRecipeContent(modifier = Modifier.padding(innerPadding), navController)
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Button"
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Create Recipe",
            fontSize = 20.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CreateRecipeContent(modifier: Modifier = Modifier,
                        navController: NavController) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))

    Box(
        modifier = modifier.background(backgroundGradient).fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TopBar(onBackClick = { navController.popBackStack() })
        }
    }
}