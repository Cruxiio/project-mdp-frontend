package com.example.nutrisaver.ui.screens.user

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.screens.user.dashboard.CalorieProgressSection
import com.example.nutrisaver.ui.screens.user.dashboard.FoodStockExpirationSection
import com.example.nutrisaver.ui.screens.user.dashboard.MealLogSection
import com.example.nutrisaver.ui.screens.user.dashboard.WaterIntakeBottomSheet
import com.example.nutrisaver.ui.screens.user.dashboard.WaterIntakeSection
import com.example.nutrisaver.ui.screens.user.dashboard.WeightEntryDummy
import com.example.nutrisaver.ui.screens.user.dashboard.WeightLogBottomSheet
import com.example.nutrisaver.ui.screens.user.dashboard.WeightReportSection
import com.example.nutrisaver.ui.screens.user.dashboard.generateSampleWeightData
import com.example.nutrisaver.ui.theme.OpenSans
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import com.example.nutrisaver.viewmodel.UserState
import com.example.nutrisaver.viewmodel.UserViewModel
import kotlin.math.cos
import kotlin.math.sin
import java.time.LocalDate

@Composable
fun DashboardScreen(navController: NavController, userViewModel: UserViewModel) {
    Scaffold(
        bottomBar = {
            UserBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        DashboardContent(modifier = Modifier.padding(innerPadding), navController, userViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(modifier: Modifier = Modifier, navController: NavController, userViewModel: UserViewModel) {
    // --- 1. AMBIL SEMUA DATA DARI VIEWMODEL ---
    val userProfile by userViewModel.userProfile.observeAsState()
    val consumption by userViewModel.todaysConsumption.observeAsState()
    val userState by userViewModel.userState.observeAsState()

    // --- 2. PICU REFRESH DATA DARI REMOTE SAAT LAYAR MUNCUL ---
    LaunchedEffect(key1 = Unit) {
        userViewModel.refreshDashboardData()
    }

    // --- 3. SIAPKAN VARIABEL UNTUK UI DENGAN NILAI DEFAULT ---
    // Variabel ini akan otomatis ter-update saat 'consumption' dan 'userProfile' berubah
    val currentCalories = consumption?.totalCalories?.toFloat() ?: 0f
    val targetCalories = consumption?.targetCalories ?: 2000f

    val currentProtein = consumption?.totalProtein?.toInt() ?: 0
    val targetProtein = consumption?.targetProtein?.toInt() ?: 100

    val currentFat = consumption?.totalFat?.toInt() ?: 0
    val targetFat = consumption?.targetFat?.toInt() ?: 70

    val currentCarbs = consumption?.totalCarbs?.toInt() ?: 0
    val targetCarbs = consumption?.targetCarbs?.toInt() ?: 250

    var currentWater by remember(consumption) { mutableStateOf(consumption?.totalWater?.toInt() ?: 0) }

    // PERBAIKAN DI SINI: Tambahkan 'f' untuk menandakan angka Float
    val targetWater = consumption?.targetWater ?: 2000f

    // Bandingkan juga dengan 0f untuk konsistensi, dan pastikan targetWater tidak 0 untuk pembagian
    val waterIntakeProgress = if (targetWater > 0f) minOf(currentWater.toFloat() / targetWater, 1f) else 0f

//    var showBottomSheet by remember { mutableStateOf(false) }
//    var isReduceMode by remember { mutableStateOf(false) }

    // --- Definisi warna dan gradient (tidak ada perubahan) ---
//    val backgroundGradient = Brush.verticalGradient(listOf(colorResource(id = R.color.bg2_1), colorResource(id = R.color.bg2_2)))
//    val greenGradient = Brush.horizontalGradient(listOf(colorResource(id = R.color.green), colorResource(id = R.color.green_teal_dark)))
    // todo: ganti ke water intake user
    val currentIntake = remember { mutableStateOf(1000) }
    val targetIntake = 2000
    // Water intake progress should still cap at 1.0 for the LinearProgressIndicator
    val waterIntakeProgress = minOf(currentIntake.value.toFloat() / targetIntake.toFloat(), 1f)
    var showWaterIntakeBottomSheet by remember { mutableStateOf(false) }
    var isReduceMode by remember { mutableStateOf(false) }

    // State for weight data filtering
    val allWeightData = remember { generateSampleWeightData() }
    var selectedPeriod by remember { mutableStateOf("Monthly") } // Default period
    var filteredWeightData by remember { mutableStateOf(emptyList<WeightEntryDummy>()) }
    var showWeightLogBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(selectedPeriod, allWeightData) {
        val today = LocalDate.now()
        filteredWeightData = when (selectedPeriod) {
            "Weekly" -> allWeightData.filter { !it.date.isBefore(today.minusWeeks(1)) }
            "Monthly" -> allWeightData.filter { !it.date.isBefore(today.minusMonths(1)) }
            "Yearly" -> allWeightData.filter { !it.date.isBefore(today.minusYears(1)) }
            else -> allWeightData // Fallback, e.g., show all if "All Time" is an option
        }.sortedBy { it.date } // Ensure filtered data remains sorted
    }

    Box(modifier = modifier
        .fillMaxSize()
        .background(backgroundGradient)
    ) {
        if (userState is UserState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    top = 24.dp,
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 16.dp
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hi,  ${userProfile?.name?.split(" ")?.first() ?: "User"}!",
                    fontSize = 20.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = {
                    navController.navigate("notification")
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.notification),
                        contentDescription = "Notification Button",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(vertical = 10.dp))

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.form_input)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Your Current Goal",
                        fontSize = 18.sp,
                        fontFamily = OpenSans,
                        color = Color.Black
                    )

                    Box(
                        modifier = Modifier
                            .background(greenGradient, shape = RoundedCornerShape(20.dp))
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = userProfile?.goal?.replace("_", " ")?.replaceFirstChar { it.titlecase(Locale.getDefault()) } ?: "Set Goal",
                            fontSize = 18.sp,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            CalorieProgressBar(
                currentCalories = currentCalories,
                targetCalories = targetCalories,
                protein = Pair(currentProtein, targetProtein),
                fats = Pair(currentFat, targetFat),
                carbs = Pair(currentCarbs, targetCarbs)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), thickness = 2.dp)

            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "Daily Meal Log",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OpenSans,
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        navController.navigate("loghistory")
                    },
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = CircleShape,
                    modifier = Modifier.height(40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        colorResource(R.color.green),
                                        colorResource(R.color.green_teal_dark)
                                    )
                                ), shape = CircleShape
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "See History",
                            fontSize = 18.sp,
                            fontFamily = OpenSans,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                MealLogSection(
                    type = "Breakfast",
                    calories = consumption?.breakfastCalories?.toFloat() ?: 0f,
                    protein = consumption?.breakfastProteinGrams ?: 0f,
                    fat = consumption?.breakfastFatGrams ?: 0f,
                    carbs = consumption?.breakfastCarbsGrams ?: 0f,
                    gradient = Brush.verticalGradient(colors = listOf(colorResource(R.color.blue_1), colorResource(R.color.blue_2))),
                    onLogClick = { navController.navigate("logmeal/breakfast") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                MealLogSection(
                    type = "Lunch",
                    calories = consumption?.lunchCalories?.toFloat() ?: 0f,
                    protein = consumption?.lunchProteinGrams ?: 0f,
                    fat = consumption?.lunchFatGrams ?: 0f,
                    carbs = consumption?.lunchCarbsGrams ?: 0f,
                    gradient = Brush.verticalGradient(colors = listOf(colorResource(R.color.yellow_1), colorResource(R.color.yellow_2))),
                    onLogClick = { navController.navigate("logmeal/lunch") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                MealLogSection(
                    type = "Dinner",
                    calories = consumption?.dinnerCalories?.toFloat() ?: 0f,
                    protein = consumption?.dinnerProteinGrams ?: 0f,
                    fat = consumption?.dinnerFatGrams ?: 0f,
                    carbs = consumption?.dinnerCarbsGrams ?: 0f,
                    gradient = Brush.verticalGradient(colors = listOf(colorResource(R.color.pink_1), colorResource(R.color.pink_2))),
                    onLogClick = { navController.navigate("logmeal/dinner") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), thickness = 2.dp)

            WaterIntakeSection(
                currentIntake = currentIntake,
                targetIntake = targetIntake,
                onAdd240ml = { currentIntake.value += 240 },
                onAdd500ml = { currentIntake.value += 500 },
                onReduceClick = {
                    isReduceMode = true
                    showWaterIntakeBottomSheet = true
                },
                onCustomClick = {
                    isReduceMode = false
                    showWaterIntakeBottomSheet = true
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.form_input)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Display current intake, target, and percentage
                    Text(
                        "${currentWater} / $targetWater ml (${(waterIntakeProgress * 100).toInt()}%)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        fontFamily = OpenSans,
                        color = colorResource(R.color.water_3)
                    )
                    LinearProgressIndicator(
                        progress = waterIntakeProgress, // This will still cap at 1.0 for visual consistency
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(CircleShape),
                        color = colorResource(R.color.water_1),
                        trackColor = Color.Gray.copy(alpha = 0.2f)
                    )
                    // Action Buttons - Add (240ml, 500ml)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 240ml Button - now adds without limit
                        WaterIntakeButton(
                            text = "240 ml",
                            icon = painterResource(R.drawable.glass_icon),
                            iconSize = 20.dp,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                currentWater = currentWater + 240
                            }
                        )

                        // 500ml Button - now adds without limit
                        WaterIntakeButton(
                            text = "500 ml",
                            icon = painterResource(R.drawable.bottle_icon),
                            iconSize = 20.dp,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                currentWater = currentWater + 500
                            }
                        )
                    }
                    // Action Buttons - Custom and Reduce
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WaterIntakeButton(
                            text = "Reduce",
                            icon = painterResource(id = R.drawable.ic_remove), // Assuming you have an ic_remove drawable
                            iconSize = 28.dp,
                            modifier = Modifier.weight(1f), // Make it take half width
                            onClick = {
                                isReduceMode = true
                                showBottomSheet = true
                            }
                        )
                        WaterIntakeButton(
                            text = "Custom",
                            icon = rememberVectorPainter(Icons.Default.Add),
                            iconSize = 28.dp,
                            modifier = Modifier.weight(1f), // Make it take half width
                            onClick = {
                                isReduceMode = false
                                showBottomSheet = true
                            }
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), thickness = 2.dp)

            WeightReportSection(
                weightData = filteredWeightData, // Pass the filtered data to the chart
                selectedPeriod = selectedPeriod,
                onPeriodChange = { newPeriod -> selectedPeriod = newPeriod },
                onLogWeightClick = {
                    showWeightLogBottomSheet = true
                },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), thickness = 2.dp)

            FoodStockExpirationSection(
                onViewAllClick = {
                    navController.navigate("foodstock") // Create this route for full food stock management
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showWaterIntakeBottomSheet) {
        WaterIntakeBottomSheet(
            isReduceMode = isReduceMode,
            onDismiss = { showWaterIntakeBottomSheet = false },
            onConfirm = { amount ->
                if (isReduceMode) {
                    currentWater = maxOf(0, currentWater - amount)
                } else {
                    currentWater= currentWater + amount
                }
                showWaterIntakeBottomSheet = false
            }
        )
    }

    if (showWeightLogBottomSheet) {
        WeightLogBottomSheet(
            onDismiss = { showWeightLogBottomSheet = false },
            onConfirm = { weight, unit, date ->
                // TODO: Save weight to database
                Log.d("WeightLogBottomSheet", "Weight: $weight, Unit: $unit, Date: $date")
                // After saving, consider refreshing the weight data if it's stored locally
                // For now, let's just dismiss
                showWeightLogBottomSheet = false
            }
        )
    }
}

@Composable
fun WaterIntakeBottomSheetContent(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    unit: String,
    onUnitChange: (String) -> Unit,
    isReduceMode: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title
        Text(
            text = if (isReduceMode) "Reduce Water Intake" else "Add Custom Amount",
            fontSize = 20.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // Quantity Selector Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Minus Button
            IconButton(
                onClick = {
                    if (quantity > 1) onQuantityChange(quantity - 1)
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                colorResource(R.color.water_1),
                                colorResource(R.color.water_2)
                            )
                        ),
                        CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_remove),
                    contentDescription = "Decrease",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Quantity Display
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .border(
                        2.dp,
                        colorResource(R.color.water_1),
                        RoundedCornerShape(12.dp)
                    )
                    .background(Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$quantity $unit",
                    fontSize = 20.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.water_1)
                )
            }

            // Plus Button
            IconButton(
                onClick = { onQuantityChange(quantity + 1) },
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                colorResource(R.color.water_1),
                                colorResource(R.color.water_2)
                            )
                        ),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Unit Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("ml", "L", "cup", "oz").forEach { unitOption ->
                Button(
                    onClick = { onUnitChange(unitOption) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (unit == unitOption)
                            colorResource(R.color.water_1) else Color.Gray.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = unitOption,
                        color = if (unit == unitOption) Color.White else Color.Gray,
                        fontSize = 14.sp,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                colorResource(R.color.water_1),
                                colorResource(R.color.water_2)
                            )
                        ),
                        RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isReduceMode) "Reduce" else "Add",
                    fontSize = 18.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Cancel Button
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Cancel",
                fontSize = 16.sp,
                fontFamily = OpenSans,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun MealLogCard(
    type: String,
    calories: Float,
    protein: Float,
    fat: Float,
    carbs: Float,
    gradient: Brush,
    onLogClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(
                            when (type.lowercase()) {
                                "breakfast" -> R.drawable.breakfast_icon
                                "lunch" -> R.drawable.lunch_icon
                                "dinner" -> R.drawable.dinner_icon
                                else -> R.drawable.breakfast_icon
                            }
                        ),
                        contentDescription = "$type icon",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                    // Meal type text
                    Text(
                        text = type,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = OpenSans,
                        modifier = Modifier.weight(1f).padding(start = 12.dp)
                    )

                    // Plus button
                    IconButton(
                        onClick = onLogClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add $type",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Nutritional information row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Calories
                    NutritionItem(
                        label = "Calories",
                        value = calories.toString(),
                        unit = "kcal",
                        modifier = Modifier.weight(1f)
                    )

                    // Protein
                    NutritionItem(
                        label = "Protein",
                        value = protein.toString(),
                        unit = "gram",
                        modifier = Modifier.weight(1f)
                    )

                    // Fat
                    NutritionItem(
                        label = "Fat",
                        value = fat.toString(),
                        unit = "gram",
                        modifier = Modifier.weight(1f)
                    )

                    // Carbs
                    NutritionItem(
                        label = "Carbs",
                        value = carbs.toString(),
                        unit = "gram",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun NutritionItem(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            fontFamily = OpenSans
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontFamily = OpenSans
        )

        Text(
            text = unit,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            fontFamily = OpenSans
        )
    }
}

@Composable
fun CalorieProgressBar(
    currentCalories: Float = 1721f, // default value
    targetCalories: Float = 2213f,
    protein: Pair<Int, Int> = Pair(78, 90),
    fats: Pair<Int, Int> = Pair(45, 70),
    carbs: Pair<Int, Int> = Pair(95, 110),
    modifier: Modifier = Modifier
) {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("id", "ID")) // Corrected pattern for full year
    val formattedDate = currentDate.format(formatter)

    // Calculate progress - this was missing!
    val progress = currentCalories.toFloat() / targetCalories.toFloat()

    // Progress Display with Animation and Responsiveness
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) progress else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.form_input)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 20.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 5.dp
                )
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Your Progress",
                    fontSize = 20.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Top)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formattedDate,
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Top)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side - Animated Percentage
                val animatedPercentage by animateFloatAsState(
                    targetValue = if (animationPlayed) (progress * 100) else 0f,
                    animationSpec = tween(durationMillis = 1000),
                    label = "percentage"
                )

                Text(
                    text = "${animatedPercentage.toInt()}%",
                    fontSize = 54.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                BoxWithConstraints(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    val containerWidth = maxWidth
                    val circleSize = minOf(containerWidth * 1.6f, 180.dp) // Made much bigger
                    val circleHeight = circleSize * 0.8f // Increased height ratio for better proportion

                    Box(
                        modifier = Modifier.size(circleSize, circleHeight),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Canvas(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val strokeWidth = (circleSize.value * 0.1f).dp.toPx() // Slightly thinner stroke for bigger circle
                            val radius = (circleSize.value * 0.42f).dp.toPx() // Bigger radius
                            val center = Offset(size.width / 2, radius + strokeWidth / 2)

                            // Background half-circle arc
                            drawArc(
                                color = Color.Gray.copy(alpha = 0.2f),
                                startAngle = 180f,
                                sweepAngle = 180f,
                                useCenter = false,
                                topLeft = Offset(
                                    center.x - radius,
                                    center.y - radius
                                ),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(
                                    width = strokeWidth,
                                    cap = StrokeCap.Round
                                )
                            )

                            // Progress half-circle arc
                            val sweepAngle = 180f * animatedProgress
                            drawArc(
                                color = Color(0xFF4CAF50),
                                startAngle = 180f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = Offset(
                                    center.x - radius,
                                    center.y - radius
                                ),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(
                                    width = strokeWidth,
                                    cap = StrokeCap.Round
                                )
                            )

                            // Progress indicator dot
                            if (animatedProgress > 0) {
                                val angle = Math.toRadians((180 + sweepAngle).toDouble())
                                val dotX = center.x + radius * cos(angle).toFloat()
                                val dotY = center.y + radius * sin(angle).toFloat()

                                drawCircle(
                                    color = Color.White,
                                    radius = (circleSize.value * 0.03f).dp.toPx(), // Responsive dot size
                                    center = Offset(dotX, dotY)
                                )
                            }
                        }

                        // Center content positioned under the arc - Made bigger
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = (-15).dp) // Adjusted offset for bigger size
                        ) {
                            Text(
                                text = "🔥",
                                fontSize = (circleSize.value * 0.18f).sp // Bigger emoji
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "$currentCalories Kcal",
                                fontSize = (circleSize.value * 0.12f).sp, // Bigger font size
                                fontFamily = OpenSans,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "of $targetCalories Kcal",
                                fontSize = (circleSize.value * 0.08f).sp, // Bigger font size
                                fontFamily = OpenSans,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MacronutrientCard(
            title = "Protein",
            current = protein.first,
            target = protein.second,
            color = Color(0xFF4CAF50),
            animationPlayed = animationPlayed,
            modifier = Modifier.weight(1f)
        )

        MacronutrientCard(
            title = "Fat",
            current = fats.first,
            target = fats.second,
            color = Color(0xFFFF5722),
            animationPlayed = animationPlayed,
            modifier = Modifier.weight(1f)
        )

        MacronutrientCard(
            title = "Carbs",
            current = carbs.first,
            target = carbs.second,
            color = Color(0xFFFFC107),
            animationPlayed = animationPlayed,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MacronutrientCard(
    title: String,
    current: Int,
    target: Int,
    color: Color,
    animationPlayed: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) (current.toFloat() / target.toFloat()) else 0f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 200),
        label = "macro_progress"
    )

    val animatedCurrent by animateFloatAsState(
        targetValue = if (animationPlayed) current.toFloat() else 0f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 200),
        label = "macro_current"
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.form_input)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = OpenSans
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Animated Progress indicator line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.Gray.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = animatedProgress.coerceAtMost(1f))
                        .clip(RoundedCornerShape(2.dp))
                        .background(color)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${animatedCurrent.toInt()}/${target}g",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontFamily = OpenSans
            )
        }
    }
}