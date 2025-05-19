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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        FoodStockContent(modifier = Modifier.padding(innerPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodStockContent(modifier: Modifier = Modifier) {
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
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // First Dropdown: Takes remaining space
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
                                focusedContainerColor = colorResource(R.color.bg),
                                unfocusedContainerColor = colorResource(R.color.bg)
                            )
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

                // Spacer between dropdowns
                Spacer(modifier = Modifier.width(12.dp))

                // Second Dropdown: Fixed width in a Box
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
                                focusedContainerColor = colorResource(R.color.bg),
                                unfocusedContainerColor = colorResource(R.color.bg)
                            )
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
    }
}

@Composable
fun FoodStockItem() {
    val item1 = colorResource(id = R.color.item_1)
    val item2 = colorResource(id = R.color.item_2)
    val itemGradient = Brush.verticalGradient(listOf(item1, item2))
    Box(
        modifier = Modifier
            .fillMaxWidth()
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
                onClick = { /* TODO: logika delete stock */ }
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