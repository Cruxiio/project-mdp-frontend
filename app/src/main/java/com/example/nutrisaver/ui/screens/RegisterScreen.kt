package com.example.nutrisaver.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.nutrisaver.AuthState
import com.example.nutrisaver.AuthViewModel
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.theme.OpenSans


@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    innerPadding: PaddingValues = PaddingValues()
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> navController.navigate("register-detail")
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(R.color.bg))
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(45.dp))

            Text(
                text = "Create Account",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = green,
                modifier = Modifier.padding(vertical = 60.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .background(greenGradient, shape = RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Account Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text("Username", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        singleLine = true,
                        value = username,
                        onValueChange = { username = it },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = colorResource(R.color.black),
                            unfocusedTextColor =  colorResource(R.color.black), focusedContainerColor = colorResource(
                                R.color.bg),
                            unfocusedContainerColor = colorResource(R.color.bg)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Email", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        singleLine = true,
                        value = email,
                        onValueChange = { email = it },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = colorResource(R.color.black),
                            unfocusedTextColor =  colorResource(R.color.black), focusedContainerColor = colorResource(
                                R.color.bg),
                            unfocusedContainerColor = colorResource(R.color.bg)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Password", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        singleLine = true,
                        value = password,
                        onValueChange = { password = it },
                        label = {  },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = colorResource(R.color.black),
                            unfocusedTextColor =  colorResource(R.color.black), focusedContainerColor = colorResource(
                                R.color.bg),
                            unfocusedContainerColor = colorResource(R.color.bg)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Confirm Password", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = confirmPassword,
                        singleLine = true,
                        onValueChange = { confirmPassword = it },
                        label = {  },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = colorResource(R.color.black),
                            unfocusedTextColor =  colorResource(R.color.black), focusedContainerColor = colorResource(
                                R.color.bg),
                            unfocusedContainerColor = colorResource(R.color.bg)
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))


                    Button(
                        onClick = {
                            authViewModel.signup(email,password)
                        }, enabled = authState.value != AuthState.Loading,
                        contentPadding = PaddingValues(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.pastel_green)
                        ),
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colorResource(R.color.pastel_green), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sign Up",
                                fontSize = 24.sp,
                                fontFamily = OpenSans,
                                color = colorResource(R.color.green_dark),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
            HorizontalDivider(
                thickness = 2.dp
            )
            Spacer(modifier = Modifier.height(50.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Already Have an Account? ",
                    fontSize = 18.sp,
                    fontFamily = OpenSans,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(5.dp))
                TextButton(

                    contentPadding = PaddingValues(0.dp),
                    onClick = {
                        navController.navigate("login")
                    }
                ) {
                    Text(
                        text = "Sign in",
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
