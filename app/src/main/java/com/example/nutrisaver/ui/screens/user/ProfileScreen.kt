package com.example.nutrisaver.ui.screens.user

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.nutrisaver.viewmodel.AuthState
import com.example.nutrisaver.viewmodel.AuthViewModel
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans
import com.example.nutrisaver.viewmodel.UserViewModel
import java.util.Locale

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {
    Scaffold(
        bottomBar = {
            UserBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        ProfileContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            authViewModel = authViewModel,
            userViewModel = userViewModel
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {

    val userProfile by userViewModel.userProfile.observeAsState()
    val authState by authViewModel.authState.observeAsState()
    val userState by userViewModel.userState.observeAsState()

    // --- 2. PICU PENGAMBILAN DATA ---
    // LaunchedEffect akan memanggil fetchUserProfile() HANYA SEKALI saat layar pertama kali muncul.
    LaunchedEffect(key1 = Unit) {
        userViewModel.fetchUserProfile()
    }

    // Navigasi ke halaman login jika user logout
    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) {
            navController.navigate("Login") {
                // Hapus semua backstack sampai ke awal agar tidak bisa kembali ke halaman profil
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // --- Definisi warna dan gradient (tidak ada perubahan) ---
    val backgroundGradient = Brush.verticalGradient(listOf(colorResource(id = R.color.bg2_1), colorResource(id = R.color.bg2_2)))
    val greenGradient = Brush.horizontalGradient(listOf(colorResource(id = R.color.green), colorResource(id = R.color.green_teal_dark)))
    val logoutGradient = Brush.verticalGradient(listOf(colorResource(id = R.color.logout_btn_1), colorResource(id = R.color.logout_btn_2)))
    val itemGradient = Brush.verticalGradient(listOf(colorResource(id = R.color.item_1), colorResource(id = R.color.item_2)))

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Tampilkan loading indicator jika data sedang diambil
        if (authState is AuthState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // --- 3. TAMPILKAN DATA (MENGGANTIKAN TODO) ---
        // Hanya tampilkan konten jika userProfile tidak null (data sudah diterima)
        userProfile?.let { user ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- BAGIAN HEADER PROFIL ---
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape)
                                .border(BorderStroke(2.dp, Color.White), CircleShape)
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            // Logika untuk menampilkan gambar profil
                            val painter = when {
                                selectedImageUri != null -> rememberAsyncImagePainter(selectedImageUri)
                                !user.profilePicture.isNullOrBlank() -> rememberAsyncImagePainter(user.profilePicture)
                                else -> null
                            }

                            if (painter != null) {
                                Image(
                                    painter = painter,
                                    contentDescription = "Profile Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Default Profile Picture",
                                    tint = Color.White,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                        // Tombol Edit Profile Picture
                        IconButton(
                            onClick = { navController.navigate("editprofile") },
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.BottomEnd)
                                .background(greenGradient, CircleShape)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit Profile", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(user.name, fontFamily = OpenSans, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("@${user.username}", fontFamily = OpenSans, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- BAGIAN DETAIL INFORMASI ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.background(greenGradient)) {
                        // ... (Tombol Edit Informasi) ...
                    }
                    // Tampilkan setiap baris informasi dari objek 'user'
                    InfoRow(label = "Gender:", value = user.gender.replaceFirstChar { it.uppercase(Locale.getDefault()) })
                    HorizontalDivider(thickness = 1.dp)
                    InfoRow(label = "Date of Birth:", value = user.dateOfBirth)
                    HorizontalDivider(thickness = 1.dp)
                    InfoRow(label = "Weight:", value = "${user.weight} kg")
                    HorizontalDivider(thickness = 1.dp)
                    InfoRow(label = "Height:", value = "${user.height} cm")
                    HorizontalDivider(thickness = 1.dp)
                    InfoRow(label = "Goal:", value = user.goal.replaceFirstChar { it.uppercase(
                        Locale.getDefault()) })
                    HorizontalDivider(thickness = 1.dp)
                    InfoRow(label = "Target Weight:", value = "${user.targetWeight} kg")
                    HorizontalDivider(thickness = 1.dp)

                    // Bagian Diet Ratio
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 15.dp)) {
                        Text("Diet Ratio", fontSize = 18.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        DietRatioBar(
                            carbRatio = user.carbsRatio,
                            proteinRatio = user.proteinRatio,
                            fatRatio = user.fatRatio
                        )
                    }
                    HorizontalDivider(thickness = 1.dp)

                    // Bagian Alergi
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 15.dp)) {
                        Text("Allergies", fontSize = 18.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold)
                        if (user.allergen.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                user.allergen.forEach { allergy ->
                                    Box(modifier = Modifier.background(brush = itemGradient, shape = RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                                        Text(allergy.name, color = Color.White, fontFamily = OpenSans, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else {
                            Text("No Allergies", fontSize = 16.sp, fontFamily = OpenSans)
                        }
                    }
                }
                HorizontalDivider(thickness = 1.dp)
                Spacer(modifier = Modifier.height(20.dp))
                // Tombol Logout
                Button(
                    onClick = { authViewModel.signOut() },
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = CircleShape,
                    modifier = Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 24.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(logoutGradient, shape = CircleShape), contentAlignment = Alignment.Center) {
                        Text("Logout", fontSize = 18.sp, fontFamily = OpenSans, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// Composable terpisah untuk baris informasi agar lebih rapi
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontFamily = OpenSans, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(value, fontFamily = OpenSans, fontSize = 18.sp)
    }
}


// Composable DietRatioBar (sedikit modifikasi untuk keamanan)
@Composable
fun DietRatioBar(carbRatio: Float, proteinRatio: Float, fatRatio: Float, modifier: Modifier = Modifier) {
    // Pastikan total tidak nol untuk menghindari pembagian dengan nol
    val total = (carbRatio + proteinRatio + fatRatio).takeIf { it > 0f } ?: 1f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, colorResource(R.color.black), RoundedCornerShape(12.dp))
    ) {
        Box(
            modifier = Modifier
                .weight(carbRatio / total)
                .fillMaxHeight()
                .background(colorResource(R.color.carbs_light)) // Light Blue for Carbs
        )
        Box(
            modifier = Modifier
                .weight(proteinRatio / total)
                .fillMaxHeight()
                .background(colorResource(R.color.protein_light)) // Light Green for Protein
        )
        Box(
            modifier = Modifier
                .weight(fatRatio / total)
                .fillMaxHeight()
                .background(colorResource(R.color.fat_light)) // Light Orange for Fats
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        Text("Carbs: ${String.format("%.1f", carbRatio)}%", fontSize = 14.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold)
        Text("Protein: ${String.format("%.1f", proteinRatio)}%", fontSize = 14.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold)
        Text("Fat: ${String.format("%.1f", fatRatio)}%", fontSize = 14.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold)
    }
}