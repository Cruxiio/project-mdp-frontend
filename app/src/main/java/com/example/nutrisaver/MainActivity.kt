package com.example.nutrisaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nutrisaver.ui.theme.NutriSaverTheme
import com.example.nutrisaver.ui.theme.OpenSans

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriSaverTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(onNavigateToRegister = {
                            navController.navigate("register")
                        })
                    }
                    composable("register") {
                        RegisterScreen(onNavigateToLogin = {
                            navController.navigate("login")
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    val circleSize = LocalConfiguration.current.screenWidthDp.dp

    Box(modifier = Modifier
        .fillMaxSize(),
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Username", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = username,
                        onValueChange = { username = it },

                        )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Password", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = password,
                        onValueChange = { password = it },
                        label = {  },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
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
                        // LOGIKA LOGIN DISINI
                    },
                    contentPadding = PaddingValues(), // removes default padding to match Box
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent // make Button background transparent
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp) // or whatever height you want
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(greenGradient, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Login",
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

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Don't Have an Account?",
                        fontSize = 20.sp,
                        fontFamily = OpenSans
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            onNavigateToRegister()
                        }
                    ) {
                        Text("Create an Account",
                            fontSize = 20.sp,
                            style = TextStyle(
                                textDecoration = TextDecoration.Underline,
                                fontFamily = OpenSans,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.green_dark)
                            ))
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(onNavigateToLogin: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val yellow_lemon = colorResource(id = R.color.yellow_lemon)
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))
    val greenDark = colorResource(id = R.color.green_dark)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text("Create Account",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = green
                )
            )
            Spacer(modifier = Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(greenGradient)
                    .padding(all = 15.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Account Information",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    HintBasicTextField(
                        value = username,
                        onValueChange = { username = it },
                        hint = "Username",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    HintBasicTextField(
                        value = email,
                        onValueChange = { email = it },
                        hint = "Email",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    HintBasicTextField(
                        value = password,
                        onValueChange = { password = it },
                        hint = "Password",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    HintBasicTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        hint = "Confirm Password",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        onClick = {
                            // LOGIKA REGISTER DISINI
                        },
                        contentPadding = PaddingValues(),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = yellow_lemon
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Register",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontFamily = OpenSans,
                                fontWeight = FontWeight.Bold,
                                color = greenDark
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
            HorizontalDivider(
                thickness = 2.dp
            )
            Spacer(modifier = Modifier.height(30.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Already Have an Account?",
                    fontSize = 20.sp,
                    fontFamily = OpenSans
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = {
                        onNavigateToLogin()
                    }
                ) {
                    Text("Sign In",
                        fontSize = 20.sp,
                        style = TextStyle(
                            textDecoration = TextDecoration.Underline,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.green_dark)
                        ))
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun HintBasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 16.sp
            ),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = hint,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
                innerTextField()
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    NutriSaverTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(onNavigateToRegister = {
                    navController.navigate("register")
                })
            }
            composable("register") {
                RegisterScreen(onNavigateToLogin = {
                    navController.navigate("login")
                })
            }
        }
    }
}