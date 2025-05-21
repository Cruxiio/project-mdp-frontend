package com.example.nutrisaver.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import com.example.nutrisaver.ui.screens.convertMillisToDate
import com.example.nutrisaver.ui.theme.OpenSans

@Composable
fun AddFoodStockScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            UserBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        AddFoodStockContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddFoodStockContent(modifier: Modifier = Modifier, navController: NavController) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val foodOptions = listOf("Apple", "Banana", "Cherry", "Durian", "Eggplant")
    var selectedFood by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    val filteredOptions = foodOptions.filter {
        it.contains(query, ignoreCase = true)
    }
    var quantity by remember { mutableStateOf(0) }
    val unitOptions = listOf("g", "pcs", "ml")
    var unitExpanded by remember { mutableStateOf(false) }
    var unit by remember { mutableStateOf(unitOptions[0]) }

    var showDateModal by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val expiredDate = datePickerState.selectedDateMillis?.let { convertMillisToDate(it) } ?: ""

    var reminderEnabled by remember { mutableStateOf(false) }
    val options = listOf("1 Month", "1 Week", "3 Days", "1 Day")
    var selectedReminder by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        TopBar(onBackClick = { navController.popBackStack() })
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(
                "Food Name",
                modifier = Modifier.padding(bottom = 5.dp),
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold
            )

            FoodSelector(
                selectedFood = selectedFood,
                onClick = { showBottomSheet = true }
            )

            if (showBottomSheet) {
                FoodSearchBottomSheet(
                    query = query,
                    onQueryChange = { query = it },
                    filteredOptions = filteredOptions,
                    selectedFood = selectedFood,
                    onSelectFood = {
                        selectedFood = it
                        showBottomSheet = false
                        query = ""
                    },
                    onDismiss = { showBottomSheet = false },
                    sheetState = sheetState
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Quantity (per unit)",
                modifier = Modifier.padding(bottom = 5.dp),
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = quantity.toString(),
                    onValueChange = { input ->
                        quantity = input.filter { it.isDigit() }.toIntOrNull() ?: 0
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(2.5f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.black),
                        unfocusedTextColor = colorResource(R.color.black),
                        focusedContainerColor = colorResource(R.color.form_input),
                        unfocusedContainerColor = colorResource(R.color.form_input)
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = !unitExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colorResource(R.color.black),
                            unfocusedTextColor = colorResource(R.color.black),
                            focusedContainerColor = colorResource(R.color.form_input),
                            unfocusedContainerColor = colorResource(R.color.form_input)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false }
                    ) {
                        unitOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { androidx.compose.material.Text(selectionOption) },
                                onClick = {
                                    unit = selectionOption
                                    unitExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Expired Date",
                modifier = Modifier.padding(bottom = 5.dp),
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = if (expiredDate == "") "Select Date" else expiredDate,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDateModal = true },
                trailingIcon = {
                    IconButton(onClick = { showDateModal = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (expiredDate == "") Color.Gray else Color.Black,
                    unfocusedTextColor = if (expiredDate == "") Color.Gray else Color.Black,
                    focusedContainerColor = colorResource(R.color.form_input),
                    unfocusedContainerColor = colorResource(R.color.form_input)
                ),
                shape = RoundedCornerShape(10.dp)
            )

            if (showDateModal) {
                DatePickerDialog(
                    onDismissRequest = { showDateModal = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showDateModal = false
                        }) {
                            androidx.compose.material.Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDateModal = false }) {
                            androidx.compose.material.Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enable Reminder",
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f))
                Switch(
                    checked = reminderEnabled,
                    onCheckedChange = {reminderEnabled =! reminderEnabled},
                    colors = SwitchDefaults.colors(checkedThumbColor = colorResource(R.color.green))
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            if (reminderEnabled) {
                Text("Remind Me:", fontWeight = FontWeight.Bold)
                Column {
                    options.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedReminder =
                                        if (selectedReminder == option) null else option
                                }
                        ) {
                            RadioButton(
                                selected = selectedReminder == option,
                                onClick = { if (selectedReminder == option) null else option },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colorResource(R.color.green),
                                )
                            )
                            Text(option, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    // Todo: Add food stock
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
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    colorResource(R.color.green),
                                    colorResource(R.color.green_teal_dark)
                                )
                            ), shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Add Food Stock",
                        fontSize = 18.sp,
                        fontFamily = OpenSans,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
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
            .padding(vertical = 12.dp, horizontal = 16.dp)
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
                text = "Add Food Stock",
                fontSize = 20.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@Composable
private fun FoodSelector(selectedFood: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(colorResource(R.color.form_input), RoundedCornerShape(10.dp))
            .border(0.4.dp, Color.DarkGray, RoundedCornerShape(10.dp))
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = selectedFood ?: "Select a food name",
            color = if (selectedFood == null) Color.Gray else Color.Black,
            fontFamily = OpenSans,
            fontSize = 16.sp
        )
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Dropdown",
            tint = Color.Black
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodSearchBottomSheet(
    query: String,
    onQueryChange: (String) -> Unit,
    filteredOptions: List<String>,
    selectedFood: String?,
    onSelectFood: (String) -> Unit,
    onDismiss: () -> Unit,
    sheetState: androidx.compose.material3.SheetState
) {
    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Column(Modifier.padding(16.dp)) {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search...",
                    fontSize = 16.sp,
                    fontFamily = OpenSans,) },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(filteredOptions) { option ->
                    val isSelected = option == selectedFood
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) colorResource(R.color.pastel_green2)
                                else Color.Transparent
                            )
                            .clickable { onSelectFood(option) }
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            fontFamily = OpenSans,
                            color = if (isSelected) colorResource(R.color.green_dark) else Color.Black,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

        }
    }
}

