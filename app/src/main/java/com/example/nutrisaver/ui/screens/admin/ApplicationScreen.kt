package com.example.nutrisaver.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.navbar.AdminBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans
import com.example.nutrisaver.viewmodel.AuthState
import com.example.nutrisaver.viewmodel.AuthViewModel

@Composable
fun ApplicationScreen(
    navController: NavController,
    // 1. Tambahkan AuthViewModel sebagai parameter
    authViewModel: AuthViewModel
) {
    // Mengamati state otentikasi dari ViewModel
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    // 2. LaunchedEffect untuk menangani navigasi setelah logout
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> {
                // Jika user tidak terotentikasi (setelah logout), kembali ke halaman login
                navController.navigate("auth") {
                    // Hapus semua backstack dari navigasi admin
                    popUpTo("admin") { inclusive = true }
                    launchSingleTop = true
                }
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit // Abaikan state lain seperti Loading, Authenticated, dll.
        }
    }

    Scaffold(
        bottomBar = {
            AdminBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        // Teruskan authViewModel ke content
        ApplicationContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            authViewModel = authViewModel
        )
    }
}

@Composable
fun ApplicationContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))
    val logoutGradient = Brush.verticalGradient(listOf(colorResource(id = R.color.logout_btn_1), colorResource(id = R.color.logout_btn_2)))

    // Box ini hanya untuk background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // SEMUA elemen konten, termasuk tombol logout, sekarang ada di DALAM Column ini.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                // Padding bawah yang besar tidak diperlukan lagi
                .padding(24.dp)
        ) {
            Text(
                "Application",
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Spacer(Modifier.height(20.dp))

            // Tombol Health Article
            Button(
                onClick = {
                    navController.navigate("adminhealtharticle")
                },
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(greenGradient, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Health Article",
                        fontSize = 20.sp,
                        fontFamily = OpenSans,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Beri jarak antara tombol sebelumnya dengan tombol logout
            Spacer(Modifier.height(24.dp)) // Anda bisa atur jarak ini

            // Tombol Logout sekarang ada di DALAM Column
            Button(
                onClick = { authViewModel.signOut() },
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = CircleShape,
                modifier = Modifier // Modifier .align() sudah DIHAPUS
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(logoutGradient, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Logout",
                        fontSize = 18.sp,
                        fontFamily = OpenSans,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}