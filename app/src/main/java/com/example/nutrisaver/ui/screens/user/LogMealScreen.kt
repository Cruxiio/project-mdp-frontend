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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.data.model.DailyConsumptionDetail
import com.example.nutrisaver.data.model.Recipe
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans
import com.example.nutrisaver.viewmodel.LogMealState
import com.example.nutrisaver.viewmodel.LogMealViewModel
import com.example.nutrisaver.viewmodel.SearchState
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

@Composable
fun LogMealScreen(navController: NavController, mealType: String, logMealViewModel: LogMealViewModel) {
    Scaffold(
        bottomBar = {
            UserBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        LogMealContent(modifier = Modifier.padding(innerPadding), navController, mealType, logMealViewModel)
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
    mealType: String,
    logMealViewModel : LogMealViewModel
) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    val searchState by logMealViewModel.searchState.observeAsState(SearchState.Idle)
    val logState by logMealViewModel.logState.observeAsState(LogMealState.Idle)
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf("Choose Recipe") }
    var showBottomSheet by remember { mutableStateOf(false) }

    // --- STATE UNTUK MENAMPUNG DATA FORM ---
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
    var customFoodName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("100") }
    var selectedUnit by remember { mutableStateOf("grams") }
    var calories by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    // EFEK: Saat resep dipilih, isi field nutrisi secara otomatis
    LaunchedEffect(selectedRecipe) {
        selectedRecipe?.let {
            quantity = "100" // Reset kuantitas ke 100 gram
            calories = it.calories.toInt().toString()
            carbs = String.format(Locale.US, "%.1f", it.carbs)
            protein = String.format(Locale.US, "%.1f", it.protein)
            fat = String.format(Locale.US, "%.1f", it.fat)
        }
    }

    // EFEK: Menangani hasil logging (sukses/gagal)
    LaunchedEffect(logState) {
        when (val state = logState) {
            is LogMealState.Success -> {
                Toast.makeText(context, "Makanan berhasil dicatat!", Toast.LENGTH_SHORT).show()
                logMealViewModel.onLogFinished()
                navController.popBackStack()
            }
            is LogMealState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                logMealViewModel.onLogFinished()
            }
            else -> {}
        }
    }

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
                        selectedRecipe = selectedRecipe,
                        onSelectClick = { showBottomSheet = true },
                        quantity = quantity,
                        onQuantityChange = { quantity = it },
                        selectedUnit = selectedUnit,
                        onUnitChange = { selectedUnit = it },
                        calories = calories,
                        carbs = carbs,
                        protein = protein,
                        fat = fat
                    )
                }
                "Custom" -> {
                    CustomForm(
                        foodName = customFoodName,
                        onFoodNameChange = { customFoodName = it },
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
                    val finalQuantity = quantity.toFloatOrNull() ?: 0f
                    val finalCalories = calories.toFloatOrNull() ?: 0f
                    val finalFoodName = if (selectedTab == "Choose Recipe") selectedRecipe?.title else customFoodName

                    if (finalFoodName.isNullOrBlank() || finalQuantity <= 0f || finalCalories <= 0f) {
                        Toast.makeText(context, "Nama, kuantitas, dan kalori wajib diisi.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    firebaseUser?.getIdToken(false)?.addOnSuccessListener { tokenResult ->
                        tokenResult.token?.let { token ->
                            val detailToLog = DailyConsumptionDetail(
                                id = 0,
                                mealType = mealType.lowercase(),
                                foodName = finalFoodName,
                                quantity = finalQuantity,
                                unit = selectedUnit,
                                calories = finalCalories,
                                carbs = carbs.toFloatOrNull() ?: 0f,
                                protein = protein.toFloatOrNull() ?: 0f,
                                fat = fat.toFloatOrNull() ?: 0f
                            )
                            logMealViewModel.logMeal(detailToLog)
                        }
                    }
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
            if (showBottomSheet) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                RecipeSearchBottomSheet(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    searchState = searchState,
                    onSearchClick = {
                        firebaseUser?.getIdToken(false)?.addOnSuccessListener { tokenResult ->
                            tokenResult.token?.let { logMealViewModel.searchRecipes(searchQuery) }
                        }
                    },
                    onSelectFood = { recipe ->
                        selectedRecipe = recipe // Update resep yang dipilih
                        showBottomSheet = false
                        searchQuery = "" // Reset query setelah memilih
                    },
                    onDismiss = { showBottomSheet = false },
                    sheetState = sheetState
                )
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

@Composable
fun ChooseRecipeForm(
    selectedRecipe: Recipe?,
    onSelectClick: () -> Unit,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    selectedUnit: String,
    onUnitChange: (String) -> Unit,
    calories: String,
    carbs: String,
    protein: String,
    fat: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        RecipeSelector(
            selectedFood = selectedRecipe?.title,
            onClick = onSelectClick
        )
        QuantityUnitField(quantity, onQuantityChange, selectedUnit, onUnitChange)
        FormField(label = "Calories", value = calories, onValueChange = {}, isNumeric = true, enabled = false)
        FormField(label = "Carbs", value = carbs, onValueChange = {}, isNumeric = true, enabled = false)
        FormField(label = "Protein", value = protein, onValueChange = {}, isNumeric = true, enabled = false)
        FormField(label = "Fat", value = fat, onValueChange = {}, isNumeric = true, enabled = false)
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
                text = selectedFood ?: "Select a recipe",
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
    searchState: SearchState, // <-- DIUBAH: Terima SearchState dari ViewModel
    onSearchClick: () -> Unit,
    onSelectFood: (Recipe) -> Unit, // <-- DIUBAH: Callback sekarang dengan objek Recipe
    onDismiss: () -> Unit,
    sheetState: SheetState
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
            // -- MULAI PERUBAHAN DI SINI --
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // TextField untuk mencari resep
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = {
                        Text("Search Recipe...",
                            fontSize = 16.sp,
                            fontFamily = OpenSans,
                            color = Color.Gray
                        )
                    },
                    modifier = Modifier
                        .weight(1f) // Mengisi sisa ruang
                        .border(0.4.dp, Color.Gray, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorResource(R.color.form_input),
                        unfocusedContainerColor = colorResource(R.color.form_input),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )

                // Tombol Search baru
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .size(56.dp) // Menyamakan tinggi dengan OutlinedTextField
                        .background(color = colorResource(id = R.color.green), shape = RoundedCornerShape(8.dp)),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Button"
                    )
                }
            }
            // -- AKHIR PERUBAHAN --

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (searchState) {
                    is SearchState.Loading -> {
                        CircularProgressIndicator() // Tampilkan loading spinner
                    }
                    is SearchState.Error -> {
                        Text(
                            text = searchState.message,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    is SearchState.Success -> {
                        val recipes = searchState.recipes
                        if (recipes.isEmpty()) {
                            Text("No results found. Try another keyword.")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                items(recipes, key = { it.id }) { recipe ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { onSelectFood(recipe) } // Kirim objek Recipe saat diklik
                                            .padding(horizontal = 12.dp, vertical = 12.dp)
                                    ) {
                                        // TODO: Bisa ditambahkan Image(recipe.imageUrl) di sini
                                        Text(
                                            text = recipe.title,
                                            fontSize = 16.sp,
                                            fontFamily = OpenSans,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is SearchState.Idle -> {
                        Text("Type something to search for recipes.")
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
    isNumeric: Boolean = false, // New parameter to control numeric input
    enabled: Boolean = true // New parameter to control enabled state
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
            enabled = enabled, // Use the new enabled parameter
            modifier = Modifier
                .fillMaxWidth()
                .border(0.4.dp, Color.Gray, RoundedCornerShape(8.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colorResource(R.color.form_input),
                unfocusedContainerColor = colorResource(R.color.form_input),
                disabledContainerColor = colorResource(R.color.form_input).copy(alpha = 0.5f), // Lighter background for disabled
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent, // Border handled by .border modifier
                disabledTextColor = Color.DarkGray // Lighter text for disabled
            ),
            shape = RoundedCornerShape(8.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    color = Color.Gray.copy(alpha = if (enabled) 1f else 0.7f) // Adjust placeholder color for disabled
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
    val unitOptions = listOf("gram", "ml", "pcs", "serving")

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
                    painter = painterResource(id = R.drawable.ic_add),
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