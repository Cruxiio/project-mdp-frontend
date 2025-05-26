package com.example.nutrisaver.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nutrisaver.R

@Composable
fun SearchResultScreen(navController: NavController) {
    Scaffold { innerPadding ->
        SearchResultContent(modifier = Modifier.padding(innerPadding), navController)
    }
}



@Composable
private fun SearchResultContent(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))

    Box(modifier = modifier.fillMaxSize().background(backgroundGradient)) {
        Column(
            modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp)
        ) {

        }
    }
}