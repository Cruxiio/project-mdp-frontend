package com.example.nutrisaver.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.nutrisaver.AuthState
import com.example.nutrisaver.AuthViewModel
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.theme.OpenSans


@Composable
fun LoginScreen(modifier: Modifier = Modifier,
                navController: NavHostController,
                authViewModel: AuthViewModel,
                innerPadding: PaddingValues = PaddingValues()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> {
                navController.navigate("main") {
                    popUpTo("auth") { inclusive = true } // ini biar gk bisa balik ke login screen
                    launchSingleTop = true
                }
            }
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }
    val circleSize = LocalConfiguration.current.screenWidthDp.dp

    Box(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(R.color.bg)),
    ){

        Box(
            modifier = Modifier
                .size(circleSize)
                .offset(
                    y = -(circleSize / 1.4f)
                )
                .background(greenGradient)
                .zIndex(0f)
        )
        Box(
            modifier = Modifier
                .size(circleSize)
                .offset(
                    y = -(circleSize / 4)
                )
                .clip(CircleShape)
                .background(greenGradient)
                .zIndex(0f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Image(
                painter = painterResource(R.drawable.logo_white),
                modifier = Modifier
                    .size(90.dp)
                    .padding(bottom = 10.dp),
                contentDescription = "App icon"
            )
            Text(
                modifier = Modifier
                    .padding(bottom = 5.dp),
                text = "Welcome Back!",
                fontSize = 38.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = OpenSans
            )
            Text(
                text = "Login to NutriSaver",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = OpenSans
            )

            Spacer(modifier = Modifier.height(120.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Email", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    value = email,
                    onValueChange = { email = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.black),
                        unfocusedTextColor = colorResource(R.color.black),
                        focusedContainerColor = colorResource(R.color.form_input),
                        unfocusedContainerColor = colorResource(R.color.form_input)
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Password", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    value = password,
                    onValueChange = { password = it },
                    label = {  },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.black),
                        unfocusedTextColor = colorResource(R.color.black),
                        focusedContainerColor = colorResource(R.color.form_input),
                        unfocusedContainerColor = colorResource(R.color.form_input)
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically,) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it }
                    )
                    Text(
                        "Remember Me",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = OpenSans
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        authViewModel.login(email,password)
                    }, enabled = authState.value != AuthState.Loading,
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(greenGradient, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign In",
                            fontSize = 24.sp,
                            fontFamily = OpenSans,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
                HorizontalDivider(
                    thickness = 2.dp
                )
                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't Have an Account? ",
                        fontSize = 18.sp,
                        fontFamily = OpenSans,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    TextButton(
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            navController.navigate("register")
                        }
                    ) {
                        Text(
                            text = "Sign up",
                            fontSize = 18.sp,
                            style = TextStyle(
                                fontFamily = OpenSans,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.green_dark)
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}