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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans

@Composable
fun LogMealScreen(navController: NavController, mealType: String) {
    Scaffold(
        bottomBar = {
            UserBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        LogMealContent(modifier = Modifier.padding(innerPadding), navController, mealType)
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colorResource(R.color.bg))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Button",
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Log Meal",
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogMealContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    mealType: String
) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    var selectedFood by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf("Choose Recipe") }
    var foodName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("0") }
    var selectedUnit by remember { mutableStateOf("grams") }
    var calories by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }

    val icon = when (mealType.lowercase()) {
        "breakfast" -> R.drawable.breakfast_icon
        "lunch" -> R.drawable.lunch_icon
        "dinner" -> R.drawable.dinner_icon
        else -> R.drawable.breakfast_icon
    }

    val gradient = when (mealType.lowercase()) {
        "breakfast" -> Brush.verticalGradient(
            colors = listOf(
                colorResource(R.color.blue_1),
                colorResource(R.color.blue_2)
            )
        )
        "lunch" -> Brush.verticalGradient(
            colors = listOf(
                colorResource(R.color.yellow_1),
                colorResource(R.color.yellow_2)
            )
        )
        "dinner" -> Brush.verticalGradient(
            colors = listOf(
                colorResource(R.color.pink_1),
                colorResource(R.color.pink_2)
            )
        )
        else -> Brush.verticalGradient(
            colors = listOf(
                colorResource(R.color.pink_1),
                colorResource(R.color.pink_2)
            )
        )
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 80.dp, bottom = 20.dp, end = 24.dp, start = 24.dp)
        ) {
            // Meal Type Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient)
                        .padding(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Icon aligned to start
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = "$mealType icon",
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.CenterStart),
                            tint = Color.White
                        )

                        // Text centered horizontally
                        Text(
                            text = mealType.lowercase().replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase() else it.toString()
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = OpenSans,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp)) // Spacer after meal type header

            // Tab Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        colorResource(R.color.green),
                        RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp)),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Choose Recipe Tab
                Button(
                    onClick = { selectedTab = "Choose Recipe" },
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = if (selectedTab == "Choose Recipe") greenGradient else SolidColor(Color.White),
                                shape = RoundedCornerShape(0.dp)
                            )
                            .clip(RoundedCornerShape(0.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Choose Recipe",
                            fontSize = 16.sp,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == "Choose Recipe") Color.White else Color.Black
                        )
                    }
                }

                // Custom Tab
                Button(
                    onClick = { selectedTab = "Custom" },
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = if (selectedTab == "Custom") greenGradient else SolidColor(Color.White),
                                shape = RoundedCornerShape(0.dp)
                            )
                            .clip(RoundedCornerShape(0.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Custom",
                            fontSize = 16.sp,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == "Custom") Color.White else Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Content based on selected tab
            when (selectedTab) {
                "Choose Recipe" -> {
                    ChooseRecipeForm(
                        selectedFood = selectedFood,
                        onFoodSelected = { selectedFood = it },
                        quantity = quantity,
                        onQuantityChange = { quantity = it },
                        selectedUnit = selectedUnit,
                        onUnitChange = { selectedUnit = it },
                        calories = calories,
                        onCaloriesChange = { calories = it },
                        carbs = carbs,
                        onCarbsChange = { carbs = it },
                        protein = protein,
                        onProteinChange = { protein = it },
                        fat = fat,
                        onFatChange = { fat = it },
                        sheetState = sheetState
                    )
                }
                "Custom" -> {
                    CustomForm(
                        foodName = foodName,
                        onFoodNameChange = { foodName = it },
                        quantity = quantity,
                        onQuantityChange = { quantity = it },
                        selectedUnit = selectedUnit,
                        onUnitChange = { selectedUnit = it },
                        calories = calories,
                        onCaloriesChange = { calories = it },
                        carbs = carbs,
                        onCarbsChange = { carbs = it },
                        protein = protein,
                        onProteinChange = { protein = it },
                        fat = fat,
                        onFatChange = { fat = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Log Button
            Button(
                onClick = {
                    // todo: tambahkan logika logging makanan
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(green, greenTealDark)
                            ),
                            RoundedCornerShape(25.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Log",
                        fontSize = 18.sp,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
        TopBar(
            onBackClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseRecipeForm(
    selectedFood: String?,
    onFoodSelected: (String) -> Unit,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    selectedUnit: String,
    onUnitChange: (String) -> Unit,
    calories: String,
    onCaloriesChange: (String) -> Unit,
    carbs: String,
    onCarbsChange: (String) -> Unit,
    protein: String,
    onProteinChange: (String) -> Unit,
    fat: String,
    onFatChange: (String) -> Unit,
    sheetState: androidx.compose.material3.SheetState
) {
    var query by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    val foodOptions = listOf(
        "Apple",
        "Banana",
        "Cherry",
        "Durian",
        "Eggplant",
        "Broccoli",
        "Chicken Breast",
        "Rice",
        "Salmon",
        "Avocado"
    )
    val filteredOptions = foodOptions.filter {
        it.contains(query, ignoreCase = true)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Food Selection
        RecipeSelector(
            selectedFood = selectedFood,
            onClick = { showBottomSheet = true }
        )

        // Quantity and Unit
        QuantityUnitField(
            quantity = quantity,
            onQuantityChange = onQuantityChange,
            selectedUnit = selectedUnit,
            onUnitChange = onUnitChange
        )

        // Nutrition Fields (Numeric Only)
        FormField(label = "Calories", value = calories, placeholder = "Enter Food Calories", onValueChange = onCaloriesChange, isNumeric = true)
        FormField(label = "Carbs", value = carbs, placeholder = "Enter Food Carbs", onValueChange = onCarbsChange, isNumeric = true)
        FormField(label = "Protein", value = protein, placeholder = "Enter Food Protein", onValueChange = onProteinChange, isNumeric = true)
        FormField(label = "Fat", value = fat, placeholder = "Enter Food Fat", onValueChange = onFatChange, isNumeric = true)
    }

    if (showBottomSheet) {
        RecipeSearchBottomSheet(
            query = query,
            onQueryChange = { query = it },
            filteredOptions = filteredOptions,
            selectedFood = selectedFood,
            onSelectFood = {
                onFoodSelected(it)
                showBottomSheet = false
                query = ""
            },
            onDismiss = { showBottomSheet = false },
            sheetState = sheetState
        )
    }
}

@Composable
private fun RecipeSelector(selectedFood: String?, onClick: () -> Unit) {
    Column {
        Text(
            text = "Food Name",
            fontSize = 16.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .background(colorResource(R.color.form_input), RoundedCornerShape(8.dp))
                .border(0.4.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(vertical = 14.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedFood ?: "Select an ingredient",
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
}

@Composable
fun CustomForm(
    foodName: String,
    onFoodNameChange: (String) -> Unit,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    selectedUnit: String,
    onUnitChange: (String) -> Unit,
    calories: String,
    onCaloriesChange: (String) -> Unit,
    carbs: String,
    onCarbsChange: (String) -> Unit,
    protein: String,
    onProteinChange: (String) -> Unit,
    fat: String,
    onFatChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Food Name (accepts any text)
        FormField(
            label = "Food Name",
            value = foodName,
            placeholder = "Enter Food Name",
            onValueChange = onFoodNameChange
        )

        // Quantity and Unit
        QuantityUnitField(
            quantity = quantity,
            onQuantityChange = onQuantityChange,
            selectedUnit = selectedUnit,
            onUnitChange = onUnitChange
        )

        // Nutrition Fields (Numeric Only)
        FormField(label = "Calories", value = calories, placeholder = "Enter Food Calories", onValueChange = onCaloriesChange, isNumeric = true)
        FormField(label = "Carbs", value = carbs, placeholder = "Enter Food Carbs", onValueChange = onCarbsChange, isNumeric = true)
        FormField(label = "Protein", value = protein, placeholder = "Enter Food Protein", onValueChange = onProteinChange, isNumeric = true)
        FormField(label = "Fat", value = fat, placeholder = "Enter Food Fat", onValueChange = onFatChange, isNumeric = true)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeSearchBottomSheet(
    query: String,
    onQueryChange: (String) -> Unit,
    filteredOptions: List<String>,
    selectedFood: String?,
    onSelectFood: (String) -> Unit,
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
                placeholder = {
                    Text(
                        "Search food...",
                        fontSize = 16.sp,
                        fontFamily = OpenSans,
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.4.dp, Color.DarkGray, RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colorResource(R.color.form_input),
                    unfocusedContainerColor = colorResource(R.color.form_input),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
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
                    fontFamily = OpenSans,
                    fontSize = 16.sp,
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
}

@Composable
fun FormField(
    label: String,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    placeholder: String = "",
    isNumeric: Boolean = false // New parameter to control numeric input
) {
    Column {
        Text(
            text = label,
            fontSize = 16.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (isNumeric) {
                    if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                        onValueChange(newValue)
                    }
                } else {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(0.4.dp, Color.Gray, RoundedCornerShape(8.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colorResource(R.color.form_input),
                unfocusedContainerColor = colorResource(R.color.form_input),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    color = Color.Gray
                )
            },
            keyboardOptions = if (isNumeric) {
                KeyboardOptions(keyboardType = KeyboardType.Number)
            } else {
                KeyboardOptions.Default
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuantityUnitField(
    quantity: String,
    onQuantityChange: (String) -> Unit,
    selectedUnit: String,
    onUnitChange: (String) -> Unit
) {
    var unitExpanded by remember { mutableStateOf(false) }
    val unitOptions = listOf("grams", "ml", "pieces", "servings")

    Column {
        Text(
            text = "Quantity and Unit",
            fontSize = 16.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val currentQuantity = quantity.toIntOrNull() ?: 0
                    if (currentQuantity > 0) {
                        onQuantityChange((currentQuantity - 1).toString())
                    }
                },
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        colorResource(R.color.item_1),
                        shape = RoundedCornerShape(10.dp)
                    ),
                enabled = (quantity.toIntOrNull() ?: 0) > 0
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_remove),
                    contentDescription = "Minus",
                    tint = Color.Black
                )
            }

            Text(
                text = quantity,
                fontSize = 18.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = {
                    val currentQuantity = quantity.toIntOrNull() ?: 0
                    onQuantityChange((currentQuantity + 1).toString())
                },
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        colorResource(R.color.item_1),
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Plus",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

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
                        .border(0.4.dp, Color.Gray, RoundedCornerShape(10.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = colorResource(R.color.form_input),
                        unfocusedContainerColor = colorResource(R.color.form_input),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
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
                                onUnitChange(selectionOption)
                                unitExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}