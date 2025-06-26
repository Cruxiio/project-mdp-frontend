package com.example.nutrisaver.ui.screens.user

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nutrisaver.R
import com.example.nutrisaver.data.model.FoodStock
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans
import com.example.nutrisaver.viewmodel.FoodStockListState
import com.example.nutrisaver.viewmodel.FoodStockViewModel
import com.google.gson.Gson
import java.time.format.DateTimeFormatter

@Composable
fun FoodStockScreen(navController: NavController, foodStockViewModel: FoodStockViewModel) {
    Scaffold(
        bottomBar = {
            UserBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        FoodStockContent(modifier = Modifier.padding(innerPadding), navController, foodStockViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodStockContent(modifier: Modifier = Modifier, navController: NavController, foodStockViewModel: FoodStockViewModel) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Perintah ini akan selalu dijalankan setiap kali layar kembali aktif
                foodStockViewModel.loadFoodStock()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 2. Amati perubahan state dari ViewModel
    val foodStockState by foodStockViewModel.foodStocks.observeAsState()

    var searchQuery by remember { mutableStateOf("") }
    val filterOptions = listOf("Stock Name", "Quantity", "Expired Date")
    var selectedFilter by remember { mutableStateOf(filterOptions[0]) }
    val filterTypeOptions = listOf("ASC", "DESC")
    var selectedFilterType by remember { mutableStateOf(filterTypeOptions[1]) } // Default Descending

    // State untuk filter yang SUDAH DITERAPKAN
    var appliedSearchQuery by remember { mutableStateOf("") }
    var appliedFilter by remember { mutableStateOf(filterOptions[0]) }
    var appliedFilterType by remember { mutableStateOf(filterTypeOptions[1]) }

    var filterExpanded by remember { mutableStateOf(false) }
    var filterTypeExpanded by remember { mutableStateOf(false) }

    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(
                "Your Food Stock",
                fontSize = 28.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.form_input), RoundedCornerShape(10.dp)), // Corrected background
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
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = green,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dropdown untuk Filter Berdasarkan (Stock Name, etc.)
                Box(modifier = Modifier.weight(1f)) {
                    @OptIn(ExperimentalMaterial3Api::class)
                    ExposedDropdownMenuBox(
                        expanded = filterExpanded,
                        onExpandedChange = { filterExpanded = !filterExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedFilter,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = green,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedContainerColor = colorResource(R.color.form_input),
                                unfocusedContainerColor = colorResource(R.color.form_input)
                            ),
                            shape = RoundedCornerShape(10.dp),
                        )
                        ExposedDropdownMenu(
                            expanded = filterExpanded,
                            onDismissRequest = { filterExpanded = false }
                        ) {
                            filterOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedFilter = option
                                        filterExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Dropdown untuk Tipe Filter (ASC/DESC)
                Box(modifier = Modifier.width(120.dp)) {
                    @OptIn(ExperimentalMaterial3Api::class)
                    ExposedDropdownMenuBox(
                        expanded = filterTypeExpanded,
                        onExpandedChange = { filterTypeExpanded = !filterTypeExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedFilterType,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterTypeExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = green,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedContainerColor = colorResource(R.color.form_input),
                                unfocusedContainerColor = colorResource(R.color.form_input)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = filterTypeExpanded,
                            onDismissRequest = { filterTypeExpanded = false }
                        ) {
                            filterTypeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option,
                                            fontFamily = OpenSans,
                                            color = if (option == selectedFilterType) green else Color.Black,
                                            fontWeight = if (option == selectedFilterType) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        selectedFilterType = option
                                        filterTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
            HorizontalDivider(thickness = 2.dp)
            Spacer(modifier = Modifier.height(15.dp))

            when (val state = foodStockState) {
                is FoodStockListState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is FoodStockListState.Success -> {
                    val allStocks = state.data

                    // Terapkan filter dan sort di sini
                    val displayedItems = remember(allStocks, searchQuery, selectedFilter, selectedFilterType) {
                        val filtered = if (searchQuery.isBlank()) {
                            allStocks
                        } else {
                            allStocks.filter {
                                it.name.contains(searchQuery, ignoreCase = true)
                            }
                        }

                        val isAscending = selectedFilterType == "ASC"

                        when (selectedFilter) {
                            "Stock Name" -> if (isAscending) filtered.sortedBy { it.name } else filtered.sortedByDescending { it.name }
                            "Quantity" -> if (isAscending) filtered.sortedBy { it.quantity } else filtered.sortedByDescending { it.quantity }
                            "Expired Date" -> if (isAscending) filtered.sortedBy { it.expiredDate } else filtered.sortedByDescending { it.expiredDate }
                            else -> filtered
                        }
                    }

                    if (displayedItems.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                        ) {
                            Text(
                                text = if (searchQuery.isBlank()) "You have no food stock yet." else "No food stock found matching your search.",
                                fontSize = 16.sp,
                                fontFamily = OpenSans,
                                color = Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn {
                            items(displayedItems, key = { it.id!! }) { item ->
                                FoodStockItem(
                                    foodStock = item,
                                    onDeleteClicked = {
                                        foodStockViewModel.deleteFoodStock(item.id!!)
                                        println("Delete requested for item ID: ${item.id}")
                                    },
                                    onEditClicked = {
                                        // 1. Ubah objek 'item' menjadi string JSON
                                        val foodStockJson = Gson().toJson(item)
                                        // 2. Encode string JSON agar aman untuk URL
                                        val encodedJson = Uri.encode(foodStockJson)
                                        // 3. Panggil navigate dengan route baru dan argumennya
                                        navController.navigate("add_food_stock_screen?foodStockJson=$encodedJson")
                                    }
                                )
                            }
                        }
                    }
                }
                is FoodStockListState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                    ) {
                        Text(
                            text = state.message,
                            color = Color.Red.copy(alpha = 0.8f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                null -> {
                    // State awal, bisa tampilkan loading juga
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp) // position on screen
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp) // or 96.dp for LargeFloatingActionButton
                    .background(brush = greenGradient, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                LargeFloatingActionButton(
                    onClick = {
                        navController.navigate("add_food_stock_screen")
                    },
                    shape = CircleShape,
                    containerColor = Color.Transparent, // keep it transparent
                    elevation = FloatingActionButtonDefaults.elevation(0.dp), // disable shadow
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(40.dp))
                }
            }
        }

    }
}

@Composable
fun FoodStockItem(foodStock: FoodStock, onDeleteClicked: () -> Unit,  onEditClicked: () -> Unit) {
    val item1 = colorResource(id = R.color.item_1)
    val item2 = colorResource(id = R.color.item_2)
    val itemGradient = Brush.verticalGradient(listOf(item1, item2))

    var openAlertDialog by remember { mutableStateOf(false) }

    if (openAlertDialog) {
        DeleteConfirmationDialog(
            onDismiss = { openAlertDialog = false },
            onConfirm = {
                onDeleteClicked()
                openAlertDialog = false
            },
            icon = Icons.Default.Warning
        )
    }

    Box(
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 5.dp, shape = RoundedCornerShape(10.dp), clip = false)
                .background(itemGradient, shape = RoundedCornerShape(10.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = foodStock.imageUrl,
                    contentDescription = foodStock.name,
                    placeholder = painterResource(id = R.drawable.default_food_image),
                    error = painterResource(id = R.drawable.default_food_image),
                    modifier = Modifier
                        .width(70.dp)
                        .height(70.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    contentScale = ContentScale.Crop,
                )
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        foodStock.name,
                        fontFamily = OpenSans,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Stock: ${foodStock.quantity} ${foodStock.unit}",
                        fontFamily = OpenSans,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "Expired: ${foodStock.expiredDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) ?: "N/A"}",
                        fontFamily = OpenSans,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(colorResource(R.color.yellow))
                            .clickable { onEditClicked() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.edit),
                            contentDescription = "Edit Button",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(colorResource(R.color.red_light))
                            .clickable { openAlertDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "Delete Button",
                            modifier = Modifier.size(18.dp)
                        )
                    }
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
                    Text("Delete Stock?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Are you sure you want to delete this item?")

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
