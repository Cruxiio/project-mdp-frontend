package com.example.nutrisaver.ui.screens.admin

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.navbar.AdminBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans

// Dummy user data class
// todo: hapus nanti
data class UserDataDummy(
    val id: Int,
    val name: String,
    val username: String,
    val age: Int,
    val password: String
)

// Generate dummy users
// todo: hapus nanti
fun generateDummyUsers(): List<UserDataDummy> {
    return listOf(
        UserDataDummy(1, "John Doe", "john_doe", 25, "pass123"),
        UserDataDummy(2, "Jane Smith", "jane_smith", 30, "securepwd"),
        UserDataDummy(3, "Mike Johnson", "mike_j", 22, "mikeyrocks"),
        UserDataDummy(4, "Sarah Wilson", "sarah_w", 28, "sarahs_pass"),
        UserDataDummy(5, "David Brown", "david_brown", 35, "dbrown_pwd"),
        UserDataDummy(6, "Lisa Davis", "lisa_d", 27, "lisa_secret"),
        UserDataDummy(7, "Tom Anderson", "tom_a", 31, "anderson_1"),
        UserDataDummy(8, "Emily Taylor", "emily_t", 24, "emily_code"),
        UserDataDummy(9, "Chris Martin", "chris_m", 29, "coldplay_fan"),
        UserDataDummy(10, "Anna Garcia", "anna_g", 26, "garcia_anna")
    )
}

@Composable
fun AllUserScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            AdminBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        AllUserContent(modifier = Modifier.padding(innerPadding), navController)
    }
}

@Composable
fun AllUserContent(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    val openDeleteConfirmDialog = remember { mutableStateOf(false) }

    // State for search and filters
    var searchQuery by remember { mutableStateOf("") }
    var selectedSortBy by remember { mutableStateOf("Name") }
    var selectedOrder by remember { mutableStateOf("ASC") }
    var showSortByDropdown by remember { mutableStateOf(false) }
    var showOrderDropdown by remember { mutableStateOf(false) }

    val allUsers = remember { generateDummyUsers() }

    // Filter and sort users based on search and filters
    val filteredAndSortedUsers = remember(searchQuery, selectedSortBy, selectedOrder, allUsers) { // Add allUsers to the keys
        val filtered = if (searchQuery.isBlank()) {
            allUsers
        } else {
            allUsers.filter { user ->
                user.name.contains(searchQuery, ignoreCase = true) ||
                        user.username.contains(searchQuery, ignoreCase = true)
            }
        }

        val sorted = when (selectedSortBy) {
            "Name" -> if (selectedOrder == "ASC") filtered.sortedBy { it.name } else filtered.sortedByDescending { it.name }
            "Age" -> if (selectedOrder == "ASC") filtered.sortedBy { it.age } else filtered.sortedByDescending { it.age }
            "Username" -> if (selectedOrder == "ASC") filtered.sortedBy { it.username } else filtered.sortedByDescending { it.username }
            else -> filtered
        }
        sorted
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Title
            Text(
                "All Users",
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Search TextField
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(25.dp)), // Corrected background
                placeholder = {
                    Text(
                        text = "Search",
                        fontFamily = OpenSans,
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = green,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Dropdowns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sort By Dropdown
                Box(modifier = Modifier.weight(1f)) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSortByDropdown = !showSortByDropdown },
                        shape = RoundedCornerShape(25.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Filter: \"$selectedSortBy\"",
                                fontSize = 14.sp,
                                fontFamily = OpenSans,
                                color = Color.Black
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.Gray
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showSortByDropdown,
                        onDismissRequest = { showSortByDropdown = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        listOf("Name", "Age", "Username").forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option,
                                        fontFamily = OpenSans,
                                        color = if (option == selectedSortBy) green else Color.Black,
                                        fontWeight = if (option == selectedSortBy) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedSortBy = option
                                    showSortByDropdown = false
                                }
                            )
                        }
                    }
                }

                // Order Dropdown
                Box(modifier = Modifier.weight(0.7f)) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showOrderDropdown = !showOrderDropdown },
                        shape = RoundedCornerShape(25.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedOrder,
                                fontSize = 14.sp,
                                fontFamily = OpenSans,
                                color = Color.Black
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.Gray
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showOrderDropdown,
                        onDismissRequest = { showOrderDropdown = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        listOf("ASC", "DESC").forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option,
                                        fontFamily = OpenSans,
                                        color = if (option == selectedOrder) green else Color.Black,
                                        fontWeight = if (option == selectedOrder) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedOrder = option
                                    showOrderDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // User List
            filteredAndSortedUsers.forEach { user ->
                UserCard(
                    user = user,
                    onViewClick = {
                        // todo: Handle view user details
                        // navController.navigate("user_details/${user.id}")
                    },
                    onDeleteClick = {
                        // todo: Handle delete user
                        openDeleteConfirmDialog.value = true
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (filteredAndSortedUsers.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                ) {
                    Text(
                        text = "No users found matching your search criteria",
                        fontSize = 16.sp,
                        fontFamily = OpenSans,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }

    if (openDeleteConfirmDialog.value) {
        DeleteConfirmationDialog(
            onDismiss = { openDeleteConfirmDialog.value = false },
            onConfirm = {
                openDeleteConfirmDialog.value = false
                Log.d("user deleted", "user deleted")
                // todo: tambahkan logika delete item
            },
            icon = Icons.Default.Warning
        )
    }
}

@Composable
fun UserCard(
    user: UserDataDummy, // todo: ganti ke object user setelah backend jadi
    onViewClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val itemGradient = Brush.verticalGradient(listOf(
        colorResource(R.color.item_1),
        colorResource(R.color.item_2))
    )

    // State to toggle password visibility
    var showPassword by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(itemGradient),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Name: ${user.name}",
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Username: ${user.username}",
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Password display based on toggle state
                Text(
                    text = "Password: ${if (showPassword) user.password else "********"}",
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    color = Color.Black
                )
            }

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                // Toggle Password Visibility Button
                IconButton(
                    onClick = { showPassword = !showPassword }, // Toggle the state
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (showPassword) Color.Blue.copy(alpha = 0.8f) else Color.Yellow.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = if (showPassword) Icons.Outlined.Lock else Icons.Default.Lock,
                        contentDescription = if (showPassword) "Hide Password" else "Show Password",
                        tint = if (showPassword) Color.White else Color.Black, // White tint for blue background, black for yellow
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Delete Button
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color.Red.copy(alpha = 0.8f),
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete User",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    icon: ImageVector,
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(R.color.bg2_1),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colorResource(R.color.delete_confirm),
                        modifier = Modifier.size(24.dp)
                    )
                    Text("Delete User?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Are you sure you want to delete this user?")

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.cancel),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.delete_confirm),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}