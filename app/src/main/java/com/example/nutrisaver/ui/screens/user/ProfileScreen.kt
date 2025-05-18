package com.example.nutrisaver.ui.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.nutrisaver.AuthState
import com.example.nutrisaver.AuthViewModel
import com.example.nutrisaver.ui.navbar.UserBottomNavBar

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    innerPadding: PaddingValues = PaddingValues()
) {
    Scaffold(
        bottomBar = {
            UserBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        ProfileContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            authViewModel = authViewModel
        )
    }
}

@Composable
fun ProfileContent(modifier: Modifier = Modifier,
                   navController: NavHostController,
                   authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("Login")
            else -> Unit
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Tes masuk home", fontSize = 32.sp)
        TextButton(onClick = {
            authViewModel.signout()
        }){
            Text( "Sign Out")
        }
    }
}
