package com.example.nutrisaver.ui.screens.user

import android.widget.Toast
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.data.model.Ingredient
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans
import com.example.nutrisaver.viewmodel.AddFoodStockState
import com.example.nutrisaver.viewmodel.FoodStockViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Helper function to convert milliseconds to a formatted date string for display
private fun convertMillisToDateDisplay(millis: Long): String {
    val date = Instant.ofEpochMilli(millis)
        .atZone(ZoneId.of("Asia/Jakarta")) // Explicitly use Jakarta time zone for WIB
        .toLocalDate()
    return date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault()))
}

// Helper function to convert LocalDate to milliseconds for DatePicker initial state
private fun convertLocalDateToMillis(date: LocalDate): Long {
    return date.atStartOfDay(ZoneId.of("Asia/Jakarta"))
        .toInstant()
        .toEpochMilli()
}


@Composable
fun AddFoodStockScreen(navController: NavController, foodStockViewModel: FoodStockViewModel) {
    Scaffold(
        bottomBar = {
            UserBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        AddFoodStockContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            foodStockViewModel = foodStockViewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddFoodStockContent(modifier: Modifier = Modifier, navController: NavController, foodStockViewModel: FoodStockViewModel) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    val allIngredients by foodStockViewModel.allIngredients.observeAsState(emptyList())

    // --- PICU PENGAMBILAN DATA ---
    LaunchedEffect(key1 = Unit) {
        foodStockViewModel.loadAllIngredients()
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val foodOptions = listOf("Apple", "Banana", "Cherry", "Durian", "Eggplant", "Milk", "Bread", "Rice", "Chicken")
    var selectedFood by remember { mutableStateOf<Ingredient?>(null) }

    val addState by foodStockViewModel.addState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(addState) {
        when (val state = addState) {
            is AddFoodStockState.Success -> {
                Toast.makeText(context, "Stok makanan berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                foodStockViewModel.onAddFinished() // Reset state
                navController.popBackStack()
            }
            is AddFoodStockState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                foodStockViewModel.onAddFinished() // Reset state
            }
            else -> {} // Loading atau null
        }
    }


    var query by remember { mutableStateOf("") }
    // Filter list dinamis dari ViewModel
    val filteredOptions = allIngredients.filter {
        it.name.contains(query, ignoreCase = true)
    }

    var quantityInput by remember { mutableStateOf("0") }
    val unitOptions = listOf("gr", "pcs", "ml")
    var unitExpanded by remember { mutableStateOf(false) }
    var selectedUnit by remember { mutableStateOf(unitOptions[0]) }

    var showDateModal by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = convertLocalDateToMillis(LocalDate.now(ZoneId.of("Asia/Jakarta")))
    )

    // Derived state for display
    val expiredDateDisplay = datePickerState.selectedDateMillis?.let { convertMillisToDateDisplay(it) } ?: ""

    var reminderEnabled by remember { mutableStateOf(false) }
    val reminderOptions = listOf("1 Month", "1 Week", "3 Days", "1 Day")
    var selectedReminderOption by remember { mutableStateOf<String?>(null) }


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        TopBar(onBackClick = { navController.popBackStack() })
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Makes this column fill available space, pushing the button to bottom
                .padding(horizontal = 24.dp, vertical = 16.dp) // Adjusted padding
        ) {
            Text(
                "Food Name",
                modifier = Modifier.padding(bottom = 5.dp),
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold
            )

            FoodSelector(
                selectedFood = selectedFood?.name, // Tampilkan nama dari objek yang dipilih
                onClick = { showBottomSheet = true }
            )

            if (showBottomSheet) {
                FoodSearchBottomSheet(
                    query = query,
                    onQueryChange = { query = it },
                    filteredOptions = filteredOptions, // Gunakan list yang sudah difilter
                    selectedFood = selectedFood,      // Berikan objek Ingredient yang dipilih
                    onSelectFood = { ingredient ->    // Terima objek Ingredient
                        selectedFood = ingredient
                        showBottomSheet = false
                        query = ""
                    },
                    onDismiss = { showBottomSheet = false },
                    sheetState = sheetState
                )
            }

            Spacer(modifier = Modifier.height(16.dp)) // Increased spacing

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
                    value = quantityInput,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } || input.isEmpty()) {
                            quantityInput = input
                        }
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(2.5f)
                        .border(0.4.dp, Color.DarkGray, RoundedCornerShape(10.dp)),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.black),
                        unfocusedTextColor = colorResource(R.color.black),
                        focusedContainerColor = colorResource(R.color.form_input),
                        unfocusedContainerColor = colorResource(R.color.form_input),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = !unitExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedUnit,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .border(0.4.dp, Color.DarkGray, RoundedCornerShape(10.dp)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colorResource(R.color.black),
                            unfocusedTextColor = colorResource(R.color.black),
                            focusedContainerColor = colorResource(R.color.form_input),
                            unfocusedContainerColor = colorResource(R.color.form_input),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false }
                    ) {
                        unitOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    selectedUnit = selectionOption
                                    unitExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Expired Date",
                modifier = Modifier.padding(bottom = 5.dp),
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = if (expiredDateDisplay.isEmpty()) "Select Date" else expiredDateDisplay,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDateModal = true }
                    .border(0.4.dp, Color.DarkGray, RoundedCornerShape(10.dp)),
                trailingIcon = {
                    IconButton(onClick = { showDateModal = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (expiredDateDisplay.isEmpty()) Color.Gray else Color.Black,
                    unfocusedTextColor = if (expiredDateDisplay.isEmpty()) Color.Gray else Color.Black,
                    focusedContainerColor = colorResource(R.color.form_input),
                    unfocusedContainerColor = colorResource(R.color.form_input),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(10.dp)
            )

            if (showDateModal) {
                DatePickerDialog(
                    onDismissRequest = { showDateModal = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showDateModal = false
                            // datePickerState.selectedDateMillis is automatically captured
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDateModal = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    onCheckedChange = { reminderEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorResource(R.color.green),
                        checkedTrackColor = colorResource(R.color.green).copy(alpha = 0.5f),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (reminderEnabled) {
                Text(
                    "Remind Me:",
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Column {
                    reminderOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedReminderOption = if (selectedReminderOption == option) null else option
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedReminderOption == option,
                                onClick = {
                                    selectedReminderOption = option
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colorResource(R.color.green),
                                    unselectedColor = Color.Gray
                                )
                            )
                            Text(
                                text = option,
                                fontSize = 16.sp,
                                fontFamily = OpenSans,
                                color = Color.Black,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f)) // Pushes the button to the bottom
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    val finalQuantity = quantityInput.toFloatOrNull() ?: 0f
                    val finalSelectedFood = selectedFood

                    // Validasi input
                    if (finalSelectedFood == null) {
                        Toast.makeText(context, "Pilih nama bahan makanan terlebih dahulu.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (finalQuantity <= 0f) {
                        Toast.makeText(context, "Kuantitas harus lebih dari 0.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val finalExpiredDate = datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }

                    val startRemindDate = datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }

                    // Panggil fungsi di ViewModel
                    foodStockViewModel.addFoodStock(
                        selectedIngredient = finalSelectedFood,
                        quantity = finalQuantity,
                        unit = selectedUnit,
                        expiredDate = finalExpiredDate,
                        startRemindDate = startRemindDate
                    )
                    navController.popBackStack()
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
                                listOf(green, greenTealDark)
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
    filteredOptions: List<Ingredient>, // Terima List<Ingredient>
    selectedFood: Ingredient?,         // Terima Ingredient?
    onSelectFood: (Ingredient) -> Unit, // Callback dengan Ingredient
    onDismiss: () -> Unit,
    sheetState: androidx.compose.material3.SheetState
) {
    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(0.7f),
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Gray.copy(alpha = 0.4f))
                )
            }
        }
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search...",
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.4.dp, Color.DarkGray, RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colorResource(R.color.form_input),
                    unfocusedContainerColor = colorResource(R.color.form_input),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredOptions.isEmpty()) {
                Text(
                    text = "No results found.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    color = Color.Gray,
                    fontWeight = FontWeight.Light
                )
            } else {
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
                                text = option.name,
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
}