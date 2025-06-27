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
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.ui.navbar.AdminBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans
import com.example.nutrisaver.viewmodel.AdminUsersViewModel

@Composable
fun AllUserScreen(
    navController: NavController,
    adminUsersViewModel: AdminUsersViewModel
) {
    LaunchedEffect(Unit) {
        adminUsersViewModel.init() // ambil semua user dari database
    }

    Scaffold(
        bottomBar = {
            AdminBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        AllUserContent(
            modifier = Modifier.padding(innerPadding),
            navController,
            adminUsersViewModel
        )
    }
}

@Composable
fun AllUserContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    adminUsersViewModel: AdminUsersViewModel
) {
    val allUsers by adminUsersViewModel.allUsers.observeAsState(emptyList())

    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))

    val openDeleteConfirmDialog = remember { mutableStateOf(false) }

    // state buat user utk delete
    var userToDelete by remember { mutableStateOf<User?>(null) }

    // State for search and filters
    var searchQuery by remember { mutableStateOf("") }
    var selectedSortBy by remember { mutableStateOf("Name") }
    var selectedOrder by remember { mutableStateOf("ASC") }
    var showSortByDropdown by remember { mutableStateOf(false) }
    var showOrderDropdown by remember { mutableStateOf(false) }

    // Filter and sort users based on search and filters (updated to include email search)
    val filteredAndSortedUsers = remember(searchQuery, selectedSortBy, selectedOrder, allUsers) {
        val filtered = if (searchQuery.isBlank()) {
            allUsers
        } else {
            allUsers.filter { user ->
                // Ensure 'user' itself is not null before accessing its properties
                // If user is null, it won't match, so it's effectively filtered out
                user?.let { actualUser ->
                    actualUser.name.contains(searchQuery, ignoreCase = true) ||
                            actualUser.username.contains(searchQuery, ignoreCase = true) ||
                            actualUser.email.contains(searchQuery, ignoreCase = true)
                } ?: false // If 'user' is null, the predicate is false (don't include it)
            }
        }

        val sorted = when (selectedSortBy) {
            "Name" -> if (selectedOrder == "ASC") filtered.sortedBy { it?.name } else filtered.sortedByDescending { it?.name }
            "Username" -> if (selectedOrder == "ASC") filtered.sortedBy { it?.username } else filtered.sortedByDescending { it?.username }
            "Email" -> if (selectedOrder == "ASC") filtered.sortedBy { it?.email } else filtered.sortedByDescending { it?.email }
            else -> filtered
        }
        sorted
    }
    Log.d("UserListDebug !!!!!", "Filtered and sorted user list size: ${filteredAndSortedUsers.size}")

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
                    .background(Color.White, RoundedCornerShape(25.dp)),
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
                    focusedBorderColor = colorResource(R.color.green),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Dropdowns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sort By Dropdown (updated to include Email option)
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
                        listOf("Name", "Username", "Email").forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option,
                                        fontFamily = OpenSans,
                                        color = if (option == selectedSortBy) colorResource(R.color.green) else Color.Black,
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
                                        color = if (option == selectedOrder) colorResource(R.color.green) else Color.Black,
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
                user?.let { // Safely unwrap nullable user
                    UserCard(
                        user = it, // Pass the non-null User object
                        onDeleteClick = {
                            // 2. Set the user to be deleted and open the dialog
                            userToDelete = it
                            openDeleteConfirmDialog.value = true
                        }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (filteredAndSortedUsers.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                ) {
                    Text(
                        text = "No users found",
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
            onDismiss = {
                openDeleteConfirmDialog.value = false
                userToDelete = null
            },
            onConfirm = {
                openDeleteConfirmDialog.value = false
                // Perform the delete action
                userToDelete?.let { user ->
                    user.id?.let { id -> // user.id can be null because _allUsers is MutableLiveData<List<User?>>
                        adminUsersViewModel.deleteUser(id)
                    }
                }
                // Clear userToDelete immediately after initiating the action
                userToDelete = null
            },
            icon = Icons.Default.Warning
        )
    }
}

@Composable
fun UserCard(
    user: User,
    onDeleteClick: () -> Unit
) {
    val itemGradient = Brush.verticalGradient(listOf(
        colorResource(R.color.item_1),
        colorResource(R.color.item_2))
    )

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
                // Email display (replaced password)
                Text(
                    text = "Email: ${user.email}",
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    color = Color.Black
                )
            }

            // Action Buttons (removed password toggle button)
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
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
                    color = colorResource(R.color.form_input),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colorResource(R.color.delete_confirm),
                        modifier = Modifier.size(28.dp)
                    )
                    Text("Delete User?", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Are you sure you want to delete this user?",
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    color = Color.DarkGray
                )

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
