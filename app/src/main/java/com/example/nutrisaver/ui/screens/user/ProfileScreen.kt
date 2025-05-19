package com.example.nutrisaver.ui.screens.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.nutrisaver.AuthState
import com.example.nutrisaver.AuthViewModel
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans

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
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))

    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    val logoutBtn1 = colorResource(id = R.color.logout_btn_1)
    val logoutBtn2 = colorResource(id = R.color.logout_btn_2)
    val logoutGradient = Brush.verticalGradient(listOf(logoutBtn1, logoutBtn2))

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("Login")
            else -> Unit
        }
    }

    Box(modifier = modifier
        .fillMaxSize()
        .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
//            Box(
//                modifier = Modifier.fillMaxWidth().background(greenGradient)
//            ) {
//                Column(
//                    modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        "Profile",
//                        fontFamily = OpenSans,
//                        fontSize = 24.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.White
//                    )
//                }
//            }

            Column(
                modifier = modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.blank_profile),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .border(
                                BorderStroke(1.dp, Color.Black),
                                CircleShape
                            )
                    )
                    IconButton(
                        onClick = { /* TODO: ke halaman edit profile */ },
                        modifier = Modifier
                            .size(25.dp)
                            .align(Alignment.BottomEnd)
                            .background(greenGradient, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    "Nama User", // TODO: Ganti ke nama user
                    fontFamily = OpenSans,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "username", // TODO: Ganti ke username user
                    fontFamily = OpenSans,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.background(greenGradient)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Your Information",
                                modifier = Modifier.weight(3f),
                                fontFamily = OpenSans,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Button(
                                onClick = {
                                    authViewModel.signout()
                                },
                                contentPadding = PaddingValues(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(R.color.pastel_green)
                                ),
                                shape = CircleShape,
                                modifier = Modifier
                                    .height(40.dp)
                                    .weight(1f)
                            ) {
                                Text(
                                    text = "Edit",
                                    fontSize = 20.sp,
                                    fontFamily = OpenSans,
                                    color = colorResource(R.color.green_dark),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Gender: ",
                            fontFamily = OpenSans,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Male", // TODO: Ganti ke gender user
                            fontFamily = OpenSans,
                            fontSize = 18.sp
                        )
                    }
                    HorizontalDivider(thickness = 1.dp)
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Date of Birth: ",
                            fontFamily = OpenSans,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "11/11/2005", // TODO: Ganti ke dob user
                            fontFamily = OpenSans,
                            fontSize = 18.sp
                        )
                    }
                    HorizontalDivider(thickness = 1.dp)
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Weight: ",
                            fontFamily = OpenSans,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "xx kg", // TODO: Ganti ke weight user
                            fontFamily = OpenSans,
                            fontSize = 18.sp
                        )
                    }
                    HorizontalDivider(thickness = 1.dp)
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Height: ",
                            fontFamily = OpenSans,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "xx cm", // TODO: Ganti ke height user
                            fontFamily = OpenSans,
                            fontSize = 18.sp
                        )
                    }
                    HorizontalDivider(thickness = 1.dp)
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Goal: ",
                            fontFamily = OpenSans,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "--", // TODO: Ganti ke goal user
                            fontFamily = OpenSans,
                            fontSize = 18.sp
                        )
                    }
                    HorizontalDivider(thickness = 1.dp)
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Height: ",
                            fontFamily = OpenSans,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "xx cm", // TODO: Ganti ke gender user
                            fontFamily = OpenSans,
                            fontSize = 18.sp
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp)
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        authViewModel.signout()
                    },
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .height(50.dp)
                        .padding(horizontal = 32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(logoutGradient, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Logout",
                            fontSize = 20.sp,
                            fontFamily = OpenSans,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
