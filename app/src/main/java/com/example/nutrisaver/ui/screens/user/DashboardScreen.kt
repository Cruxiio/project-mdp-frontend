package com.example.nutrisaver.ui.screens.user

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

// Updated data class for weight entries to use LocalDate
data class WeightEntryDummy(
    val weight: Float,
    val date: LocalDate // Changed to LocalDate for easier filtering
)

// Sample data - replace with actual database queries
fun generateSampleWeightData(): List<WeightEntryDummy> {
    val today = LocalDate.now()
    return listOf(
        WeightEntryDummy(68.5f, today.minusWeeks(8)),
        WeightEntryDummy(69.2f, today.minusWeeks(7)),
        WeightEntryDummy(67.8f, today.minusWeeks(6)),
        WeightEntryDummy(70.1f, today.minusWeeks(5)),
        WeightEntryDummy(71.3f, today.minusWeeks(4)),
        WeightEntryDummy(70.5f, today.minusWeeks(3)),
        WeightEntryDummy(69.8f, today.minusWeeks(2)),
        WeightEntryDummy(68.9f, today.minusWeeks(1)),
        WeightEntryDummy(68.7f, today) // Today's weight
    ).sortedBy { it.date } // Ensure data is sorted by date
}

@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            UserBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        DashboardContent(modifier = Modifier.padding(innerPadding), navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(modifier: Modifier = Modifier, navController: NavController) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    // todo: ganti ke total calorie user berdasarkan masing-masing tipe
    val breakfastCalories = remember { mutableStateOf(0) }
    val lunchCalories = remember { mutableStateOf(0) }
    val dinnerCalories = remember { mutableStateOf(0) }

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
            "Weekly" -> allWeightData.filter { it.date.isAfter(today.minusWeeks(1)) || it.date.isEqual(today.minusWeeks(1)) }
            "Monthly" -> allWeightData.filter { it.date.isAfter(today.minusMonths(1)) || it.date.isEqual(today.minusMonths(1)) }
            "Yearly" -> allWeightData.filter { it.date.isAfter(today.minusYears(1)) || it.date.isEqual(today.minusYears(1)) }
            else -> allWeightData // Fallback, e.g., show all if "All Time" is an option
        }.sortedBy { it.date } // Ensure filtered data remains sorted
    }


    Box(modifier = modifier
        .fillMaxSize()
        .background(backgroundGradient)
    ) {
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
                    text = "Hi, User!",
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
                            "Lose Weight", // todo: ganti ke goal user
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
                // todo: insert parameternya disini nanti
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), thickness = 2.dp)

            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
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
                    modifier = Modifier
                        .height(40.dp)
                        .wrapContentWidth()
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
                MealLogCard(
                    type = "Breakfast",
                    calories = breakfastCalories,
                    protein = 50.1f, // todo: ganti ke total protein utk breakfast
                    fat = 22.7f,     // todo: ganti ke total fat utk breakfast
                    carbs = 38.4f,   // todo: ganti ke total carbs utk breakfast
                    gradient = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(R.color.blue_1),
                            colorResource(R.color.blue_2)
                        )
                    ),
                    onLogClick = { navController.navigate("logmeal/breakfast") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                MealLogCard(
                    type = "Lunch",
                    calories = lunchCalories,
                    protein = 50.1f, // todo: ganti ke total protein utk lunch
                    fat = 22.7f,     // todo: ganti ke total fat utk lunch
                    carbs = 38.4f,   // todo: ganti ke total carbs utk lunch
                    gradient = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(R.color.yellow_1),
                            colorResource(R.color.yellow_2)
                        )
                    ),
                    onLogClick = { navController.navigate("logmeal/lunch") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                MealLogCard(
                    type = "Dinner",
                    calories = dinnerCalories,
                    protein = 50.1f, // todo: ganti ke total protein utk dinner
                    fat = 22.7f,     // todo: ganti ke total fat utk dinner
                    carbs = 38.4f,   // todo: ganti ke total carbs utk dinner
                    gradient = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(R.color.pink_1),
                            colorResource(R.color.pink_2)
                        )
                    ),
                    onLogClick = { navController.navigate("logmeal/dinner") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), thickness = 2.dp)

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
                    Text(
                        "Water Intake",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = OpenSans,
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Display current intake, target, and percentage
                    Text(
                        "${currentIntake.value} / $targetIntake ml (${(waterIntakeProgress * 100).toInt()}%)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        fontFamily = OpenSans,
                        color = colorResource(R.color.water_3)
                    )
                    LinearProgressIndicator(
                        progress = {
                            waterIntakeProgress // This will still cap at 1.0 for visual consistency
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(CircleShape),
                        color = colorResource(R.color.water_1),
                        trackColor = Color.Gray.copy(alpha = 0.2f),
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
                                currentIntake.value += 240
                            }
                        )

                        // 500ml Button - now adds without limit
                        WaterIntakeButton(
                            text = "500 ml",
                            icon = painterResource(R.drawable.bottle_icon),
                            iconSize = 20.dp,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                currentIntake.value += 500
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
                                showWaterIntakeBottomSheet = true
                            }
                        )
                        WaterIntakeButton(
                            text = "Custom",
                            icon = rememberVectorPainter(Icons.Default.Add),
                            iconSize = 28.dp,
                            modifier = Modifier.weight(1f), // Make it take half width
                            onClick = {
                                isReduceMode = false
                                showWaterIntakeBottomSheet = true
                            }
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), thickness = 2.dp)

            WeightReportCard(
                weightData = filteredWeightData, // Pass the filtered data to the chart
                selectedPeriod = selectedPeriod,
                onPeriodChange = { newPeriod -> selectedPeriod = newPeriod },
                onLogWeightClick = {
                    showWeightLogBottomSheet = true
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
                    currentIntake.value = maxOf(0, currentIntake.value - amount)
                } else {
                    currentIntake.value += amount
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
                showWeightLogBottomSheet = false
            }
        )
    }
}

@Composable
fun WaterIntakeButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: Painter,
    iconSize: Dp = 20.dp,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = CircleShape,
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            colorResource(R.color.water_1),
                            colorResource(R.color.water_2)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Text(
                    text = text,
                    fontSize = 18.sp,
                    fontFamily = OpenSans,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterIntakeBottomSheet(
    isReduceMode: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    var unit by remember { mutableStateOf("ml") }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        WaterIntakeBottomSheetContent(
            quantity = quantity,
            onQuantityChange = { quantity = it },
            unit = unit,
            onUnitChange = { unit = it },
            isReduceMode = isReduceMode,
            onConfirm = {
                val amountInMl = when (unit) {
                    "L" -> quantity * 1000
                    "cup" -> quantity * 240
                    "oz" -> quantity * 30
                    else -> quantity // ml
                }
                onConfirm(amountInMl)
            },
            onDismiss = onDismiss
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
    modifier: Modifier = Modifier,
    type: String,
    calories: MutableState<Int>?,
    protein: Float = 0f,
    fat: Float = 0f,
    carbs: Float = 0f,
    gradient: Brush,
    onLogClick: () -> Unit,
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
                        value = calories?.value?.toString() ?: "0",
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
    modifier: Modifier = Modifier,
    currentCalories: Int = 1721, // default value
    targetCalories: Int = 2213,
    protein: Pair<Int, Int> = Pair(78, 90),
    fats: Pair<Int, Int> = Pair(45, 70),
    carbs: Pair<Int, Int> = Pair(95, 110)
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
            title = "Carbs",
            current = carbs.first,
            target = carbs.second,
            color = colorResource(R.color.carbs),
            animationPlayed = animationPlayed,
            modifier = Modifier.weight(1f)
        )

        MacronutrientCard(
            title = "Protein",
            current = protein.first,
            target = protein.second,
            color = colorResource(R.color.protein),
            animationPlayed = animationPlayed,
            modifier = Modifier.weight(1f)
        )

        MacronutrientCard(
            title = "Fat",
            current = fats.first,
            target = fats.second,
            color = colorResource(R.color.fat),
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

            LinearProgressIndicator(
                progress = { animatedProgress.coerceAtMost(1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = Color.Gray.copy(alpha = 0.2f),
            )

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

@SuppressLint("DefaultLocale")
@Composable
fun WeightReportCard(
    modifier: Modifier = Modifier,
    weightData: List<WeightEntryDummy> = generateSampleWeightData(), // todo: ganti ke tipe data aslinya, hapus default valuenya jg
    selectedPeriod: String = "Monthly",
    onPeriodChange: (String) -> Unit = {},
    onLogWeightClick: () -> Unit = {},
) {
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    var expanded by remember { mutableStateOf(false) }
    val periodOptions = listOf("Weekly", "Monthly", "Yearly")

    val weightChange = if (weightData.size >= 2) {
        val latestWeight = weightData.last().weight
        val previousWeight = weightData.first().weight
        latestWeight - previousWeight
    } else 0f

    val changeText = when {
        weightChange > 0 -> {
            val startDate = weightData.firstOrNull()?.date?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) ?: "start of period"
            "Your weight increases by ${String.format("%.1f", weightChange)}kg from $startDate."
        }
        weightChange < 0 -> {
            val startDate = weightData.firstOrNull()?.date?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) ?: "start of period"
            "Your weight decreases by ${String.format("%.1f", kotlin.math.abs(weightChange))}kg from $startDate."
        }
        else -> "Your weight remains stable for the selected period."
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.form_input)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Weight Report",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OpenSans,
                    color = Color.Black
                )

                Box {
                    Card(
                        modifier = Modifier
                            .clickable { expanded = !expanded }
                            .padding(0.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = selectedPeriod,
                                fontSize = 14.sp,
                                fontFamily = OpenSans,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Dropdown Menu
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(
                                Color.White,
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        periodOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option,
                                        fontFamily = OpenSans,
                                        fontSize = 14.sp,
                                        fontWeight = if (option == selectedPeriod) FontWeight.Bold else FontWeight.Normal,
                                        color = if (option == selectedPeriod) green else Color.Black
                                    )
                                },
                                onClick = {
                                    onPeriodChange(option) // Use the passed lambda
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Weight Chart with better alignment
            WeightLineChart(
                weightData = weightData, // Pass the filtered data from DashboardContent
                selectedPeriod = selectedPeriod, // Pass selectedPeriod for X-axis formatting
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        weightChange > 0 -> colorResource(R.color.yellow_1).copy(alpha = 0.3f)
                        weightChange < 0 -> colorResource(R.color.green).copy(alpha = 0.3f)
                        else -> Color.Gray.copy(alpha = 0.2f)
                    }
                )
            ) {
                Text(
                    text = "Summary:",
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp
                    )
                )
                Text(
                    text = changeText,
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        bottom = 16.dp,
                        end = 16.dp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Log Weight Button
            Button(
                onClick = onLogWeightClick,
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
                        .background(greenGradient, RoundedCornerShape(25.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Log Weight",
                        fontSize = 18.sp,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun WeightLineChart(
    weightData: List<WeightEntryDummy>, // todo: ganti tipe datanya
    selectedPeriod: String, // filternya berdasarkna
    modifier: Modifier = Modifier
) {
    val chartColor = colorResource(id = R.color.water_1)

    Canvas(modifier = modifier) {
        if (weightData.isEmpty()) {
            drawContext.canvas.nativeCanvas.drawText(
                "No weight data available for this period.",
                size.width / 2,
                size.height / 2,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 14.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
            return@Canvas
        }

        val padding = 40.dp.toPx()
        val chartWidth = size.width - (padding * 2)
        val chartHeight = size.height - (padding * 2)

        // Find min and max weights for scaling
        val minWeight = weightData.minOf { it.weight } - 2f
        val maxWeight = weightData.maxOf { it.weight } + 2f
        val weightRange = maxWeight - minWeight

        // Draw grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = padding + (chartHeight * i / gridLines)
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(padding, y),
                end = Offset(size.width - padding, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Calculate points
        val points = weightData.mapIndexed { index, entry ->
            val x = padding + (chartWidth * index / (weightData.size - 1).coerceAtLeast(1))
            val y = padding + chartHeight - ((entry.weight - minWeight) / weightRange * chartHeight)
            Offset(x, y)
        }

        // Draw line chart
        if (points.size > 1) {
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = chartColor,
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Draw points
        points.forEach { point ->
            // Outer circle (shadow effect)
            drawCircle(
                color = chartColor.copy(alpha = 0.3f),
                radius = 8.dp.toPx(),
                center = point
            )
            // Main circle
            drawCircle(
                color = chartColor,
                radius = 6.dp.toPx(),
                center = point
            )
            // Inner white circle
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = point
            )
        }

        // Draw Y-axis labels (weights) with better positioning
        for (i in 0..gridLines) {
            val weight = minWeight + (weightRange * i / gridLines)
            val y = padding + (chartHeight * (gridLines - i) / gridLines)

            drawContext.canvas.nativeCanvas.drawText(
                "${String.format("%.0f", weight)}kg",
                padding - 15.dp.toPx(), // Position labels to the left of the chart area
                y + 5.dp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 11.sp.toPx()
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
            )
        }

        // Draw X-axis labels (dates)
        val xAxisLabelFormatter = when (selectedPeriod) {
            "Weekly" -> DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault()) // e.g., Jun 17
            "Monthly" -> DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault()) // e.g., Jun 01, Jun 15
            "Yearly" -> DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault()) // e.g., Jan 2024, Apr 2024
            else -> DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault()) // Default
        }

        val maxLabels = 5 // Max number of labels to avoid crowding
        val labelInterval = if (weightData.size > maxLabels) weightData.size / maxLabels else 1

        weightData.forEachIndexed { index, entry ->
            if (index % labelInterval == 0 || index == weightData.size - 1) { // Always show first and last, and spaced labels
                val formattedDate = entry.date.format(xAxisLabelFormatter)

                val x = padding + (chartWidth * index / (weightData.size - 1).coerceAtLeast(1))
                val yOffset = if (index % 2 != 0 && weightData.size > maxLabels * 1.5) 15.dp.toPx() else 0f // Offset every other label if many
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 10.sp.toPx()
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                drawContext.canvas.nativeCanvas.drawText(
                    formattedDate,
                    x,
                    size.height - 10.dp.toPx() + yOffset,
                    textPaint
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightLogBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (Float, String, LocalDate) -> Unit // weight, unit, date
) {
    var weight by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("kg") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        WeightLogBottomSheetContent(
            weight = weight,
            onWeightChange = { weight = it },
            unit = unit,
            onUnitChange = { unit = it },
            selectedDate = selectedDate,
            onDateChange = { selectedDate = it },
            showDatePicker = showDatePicker,
            onShowDatePicker = { showDatePicker = it },
            onConfirm = {
                val weightValue = weight.toFloatOrNull()
                if (weightValue != null && weightValue > 0) {
                    onConfirm(weightValue, unit, selectedDate)
                }
            },
            onDismiss = onDismiss
        )
    }
}

@Composable
fun WeightLogBottomSheetContent(
    weight: String,
    onWeightChange: (String) -> Unit,
    unit: String,
    onUnitChange: (String) -> Unit,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    showDatePicker: Boolean,
    onShowDatePicker: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
    val isValidWeight = weight.toFloatOrNull()?.let { it > 0 } ?: false

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Title
        Text(
            text = "Log Your Weight",
            fontSize = 20.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // Weight Input Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Current Weight",
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Weight Input Field
                OutlinedTextField(
                    value = weight,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { newValue ->
                        // Only allow numbers and decimal point
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            onWeightChange(newValue)
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Enter weight",
                            fontFamily = OpenSans,
                            color = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = green,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Medium
                    )
                )

                // Unit Selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("kg", "lbs").forEach { unitOption ->
                        Button(
                            onClick = { onUnitChange(unitOption) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (unit == unitOption) green else Color.Gray.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = unitOption,
                                color = if (unit == unitOption) Color.White else Color.Gray,
                                fontSize = 14.sp,
                                fontFamily = OpenSans,
                            )
                        }
                    }
                }
            }
        }

        // Date Selection Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Date",
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShowDatePicker(true) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedDate.format(dateFormatter),
                        fontSize = 16.sp,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.calendar_icon), // Add calendar icon to your drawables
                        contentDescription = "Select Date",
                        tint = green,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Weight Conversion Info (if using lbs)
        if (unit == "lbs" && weight.isNotEmpty() && isValidWeight) {
            val weightInKg = weight.toFloat() * 0.453592f
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = green.copy(alpha = 0.1f))
            ) {
                Text(
                    text = "≈ ${String.format("%.1f", weightInKg)} kg",
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Medium,
                    color = green,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Button
        Button(
            onClick = onConfirm,
            enabled = isValidWeight,
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
                        // Use the 'brush' parameter for the background modifier
                        brush = if (isValidWeight) greenGradient else SolidColor(Color.Gray.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Log Weight",
                    fontSize = 18.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = if (isValidWeight) Color.White else Color.Gray
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

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            selectedDate = selectedDate,
            onDateSelected = { date ->
                onDateChange(date)
                onShowDatePicker(false)
            },
            onDismiss = { onShowDatePicker(false) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = java.time.Instant.ofEpochMilli(millis)
                        val date = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        onDateSelected(date)
                    }
                }
            ) {
                Text(
                    "OK",
                    fontFamily = OpenSans,
                    color = colorResource(R.color.green)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    fontFamily = OpenSans,
                    color = Color.Gray
                )
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = colorResource(R.color.green),
                todayDateBorderColor = colorResource(R.color.green)
            )
        )
    }
}