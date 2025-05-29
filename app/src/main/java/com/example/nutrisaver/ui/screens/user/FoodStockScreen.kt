package com.example.nutrisaver.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans

@Composable
fun FoodStockScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            UserBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        FoodStockContent(modifier = Modifier.padding(innerPadding), navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodStockContent(modifier: Modifier = Modifier, navController: NavController) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))

    var searchQuery by remember { mutableStateOf("") }
    val allItems = listOf("tes") // nanti diisi dengan data dari database
    val filteredItems = allItems.filter {
        it.contains(searchQuery, ignoreCase = true)
    }

    val filterOptions = listOf("Stock Name", "Quantity", "Expired Date")
    var filter by remember { mutableStateOf(filterOptions[0]) }
    var filterExpanded by remember { mutableStateOf(false) }

    val filterTypeOptions = listOf("DESC", "ASC")
    var filterType by remember { mutableStateOf(filterTypeOptions[0]) }
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
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(
                "Your Food Stock",
                fontSize = 24.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colorResource(R.color.black),
                    unfocusedTextColor = colorResource(R.color.black),
                    focusedContainerColor = colorResource(R.color.form_input),
                    unfocusedContainerColor = colorResource(R.color.form_input)
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ExposedDropdownMenuBox(
                        expanded = filterExpanded,
                        onExpandedChange = { filterExpanded = !filterExpanded }
                    ) {
                        OutlinedTextField(
                            value = filter,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = colorResource(R.color.black),
                                unfocusedTextColor = colorResource(R.color.black),
                                focusedContainerColor = colorResource(R.color.form_input),
                                unfocusedContainerColor = colorResource(R.color.form_input)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = filterExpanded,
                            onDismissRequest = { filterExpanded = false }
                        ) {
                            filterOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        filter = option
                                        filterExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Box(modifier = Modifier.width(120.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = filterTypeExpanded,
                        onExpandedChange = { filterTypeExpanded = !filterTypeExpanded }
                    ) {
                        OutlinedTextField(
                            value = filterType,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterTypeExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = colorResource(R.color.black),
                                unfocusedTextColor = colorResource(R.color.black),
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
                                    text = { Text(option) },
                                    onClick = {
                                        filterType = option
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

            LazyColumn {
                items(filteredItems) { item ->
                    FoodStockItem()
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
                        navController.navigate("addfoodstock")
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
fun FoodStockItem() {
    val item1 = colorResource(id = R.color.item_1)
    val item2 = colorResource(id = R.color.item_2)
    val itemGradient = Brush.verticalGradient(listOf(item1, item2))

    val openAlertDialog = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(bottom = 8.dp) //
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(20.dp),
                    clip = false
                )
                .background(itemGradient, shape = RoundedCornerShape(20.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Nama Stock", // TODO: nama stock
                        fontFamily = OpenSans,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Stock: ", // TODO: quantity dan satuan stock
                        fontFamily = OpenSans,
                        fontSize = 14.sp
                    )
                    Text(
                        "Expired Date: ", // TODO: expired date
                        fontFamily = OpenSans,
                        fontSize = 14.sp
                    )
                }
                IconButton(
                    modifier = Modifier.background(
                        colorResource(R.color.yellow),
                        shape = RoundedCornerShape(10.dp)
                    ),
                    onClick = { /* TODO: logika edit stock */ }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit Button",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(
                    modifier = Modifier.background(
                        colorResource(R.color.red_light),
                        shape = RoundedCornerShape(10.dp)
                    ),
                    onClick = {
                        openAlertDialog.value = true // pakai dialog buat konfirmasi
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = "Delete Button",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    if (openAlertDialog.value) {
        DeleteConfirmationDialog(
            onDismiss = { openAlertDialog.value = false },
            onConfirm = {
                openAlertDialog.value = false
                println("Confirmation registered") // todo: tambahkan logika delete item
            },
            icon = Icons.Default.Warning
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
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
