package com.example.nutrisaver.ui.screens.user

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.theme.OpenSans
import coil.compose.rememberAsyncImagePainter
import com.example.nutrisaver.viewmodel.UserViewModel

@Composable
fun EditProfileScreen(navController: NavController, userViewModel: UserViewModel) {
    Scaffold(
        topBar = { TopBar(onBackClick = { navController.popBackStack() }) }
    ) { innerPadding ->
        EditProfileContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            userViewModel = userViewModel
        )
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(greenGradient)
            .padding(
                top = 32.dp,
                bottom = 12.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back Button",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Edit Profile",
                fontSize = 20.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun EditProfileContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel // Pass UserViewModel here
) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    val userProfile by userViewModel.userProfile.observeAsState()

    var name by remember { mutableStateOf("") } // Add name field
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // This will hold the URI of the image selected from the gallery.
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    // This will hold the *current* profile picture URI from the userProfile.
    var currentProfilePictureUrl by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // When an image is selected, update selectedImageUri
        selectedImageUri = uri
    }

    // Fetch user profile when the composable enters the composition
    LaunchedEffect(Unit) {
        userViewModel.fetchUserProfile()
    }

    // Populate fields when userProfile is available
    LaunchedEffect(userProfile) {
        userProfile?.let { user ->
            name = user.name
            username = user.username
            email = user.email
            currentProfilePictureUrl = user.absoluteProfilePictureUrl // This is the value passed to rememberAsyncImagePainter below
            Log.d("ProfileImageDebug", "Profile Picture URL from User model: ${user.absoluteProfilePictureUrl}")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color.Gray, shape = CircleShape)
                        .border(10.dp, colorResource(R.color.bg), CircleShape)
                        .clickable {
                            imagePickerLauncher.launch("image/*") // Launch picker on click
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val painter = when {
                        selectedImageUri != null -> {
                            Log.d("ProfileImageDebug", "Using selectedImageUri: $selectedImageUri")
                            rememberAsyncImagePainter(selectedImageUri)
                        }
                        !currentProfilePictureUrl.isNullOrBlank() -> {
                            Log.d("ProfileImageDebug", "Using currentProfilePictureUrl: $currentProfilePictureUrl")
                            rememberAsyncImagePainter(currentProfilePictureUrl)
                        }
                        else -> {
                            Log.d("ProfileImageDebug", "Using default Person icon.")
                            null
                        }
                    }

                    if (painter != null) {
                        Image(
                            painter = painter,
                            contentDescription = "Profile Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(shape = CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default Profile Icon",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = CircleShape,
                ) {
                    Box(
                        modifier = Modifier
                            .background(greenGradient, shape = CircleShape)
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Icon",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Change Profile Picture",
                                fontSize = 16.sp,
                                fontFamily = OpenSans,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Name", // New field for user's full name
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    value = name,
                    onValueChange = { name = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.black),
                        unfocusedTextColor = colorResource(R.color.black),
                        focusedContainerColor = colorResource(R.color.form_input),
                        unfocusedContainerColor = colorResource(R.color.form_input)
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Username",
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    value = username,
                    onValueChange = { username = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.black),
                        unfocusedTextColor = colorResource(R.color.black),
                        focusedContainerColor = colorResource(R.color.form_input),
                        unfocusedContainerColor = colorResource(R.color.form_input)
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Email",
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    value = email,
                    onValueChange = { email = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.black),
                        unfocusedTextColor = colorResource(R.color.black),
                        focusedContainerColor = colorResource(R.color.form_input),
                        unfocusedContainerColor = colorResource(R.color.form_input)
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Column(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 24.dp)
        ) {
            Button(
                onClick = {
                    val currentUser = userProfile // Get the current user data
                    if (currentUser != null) {
                        val updatedUser = currentUser.copy(
                            name = name,
                            username = username,
                            email = email,
                            profilePicture = selectedImageUri?.toString() ?: currentProfilePictureUrl
                        )
                        userViewModel.updateUserProfile(updatedUser) // Use the new function
                    }
                    navController.popBackStack() // Navigate back to profile
                },
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
                        text = "Save Changes",
                        fontSize = 20.sp,
                        fontFamily = OpenSans,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}