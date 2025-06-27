package com.example.nutrisaver.ui.screens.user

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.theme.OpenSans
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

// data class dummy cmn buat tampilin aja
data class DailyTargetsDummy(
    val calories: Int = 2500,
    val protein: Int = 150,
    val fat: Int = 80,
    val carbs: Int = 300
)
data class MealDataDummy(
    val mealType: String,
    val foodName: String,
    val quantity: String,
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carbs: Int
)

// --- MOCK DATA GENERATION (for demonstration) ---
// In a real app, this would be replaced with actual database/API calls
fun generateMockDailyData(date: LocalDate): Pair<Int, List<MealDataDummy>> {
    val dayOfMonth = date.dayOfMonth
    return when (dayOfMonth % 3) { // Simulate different data for different days
        0 -> Pair(
            1800,
            listOf(
                MealDataDummy("Breakfast", "Toast & Egg", "1 serving", 350, 15, 10, 30),
                MealDataDummy("Lunch", "Chicken Salad", "1 bowl", 450, 40, 20, 15),
                MealDataDummy("Dinner", "Grilled Fish & Rice", "1 plate", 600, 50, 25, 40)
            )
        )
        1 -> Pair(
            2200,
            listOf(
                MealDataDummy("Breakfast", "Oatmeal with Fruits", "1 bowl", 400, 12, 8, 70),
                MealDataDummy("Lunch", "Nasi Goreng", "1 plate", 700, 25, 30, 80),
                MealDataDummy("Dinner", "Vegetable Stir-fry", "1 bowl", 350, 10, 10, 40)
            )
        )
        else -> Pair(
            1500, // Default or lighter day
            listOf(
                MealDataDummy("Breakfast", "Yogurt & Granola", "1 cup", 250, 10, 5, 40),
                MealDataDummy("Lunch", "Soup & Bread", "1 bowl", 300, 12, 8, 45),
                MealDataDummy("Dinner", "Vegetable Stir-fry", "1 bowl", 350, 10, 10, 40)
            )
        )
    }
}

// Helper function to convert milliseconds (typically UTC epoch millis) to a LocalDate in the app's timezone (WIB)
private fun convertMillisToLocalDate(millis: Long): LocalDate {
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.of("Asia/Jakarta"))
        .toLocalDate()
}

// Helper function to convert LocalDate (in app's timezone) to milliseconds (UTC epoch millis)
private fun convertLocalDateToMillis(date: LocalDate): Long {
    return date.atStartOfDay(ZoneId.of("Asia/Jakarta"))
        .toInstant()
        .toEpochMilli()
}

// Helper function to convert milliseconds to a formatted date string for display
private fun convertMillisToDateDisplay(millis: Long): String {
    val date = Instant.ofEpochMilli(millis)
        .atZone(ZoneId.of("Asia/Jakarta"))
        .toLocalDate()
    return date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault()))
}


@Composable
fun LogHistoryScreen(navController: NavController) {
    Scaffold { innerPadding ->
        LogHistoryContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colorResource(R.color.bg))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
        Text(
            text = "Log History",
            fontSize = 20.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogHistoryContent(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))

    // UI State for DatePicker
    var showDateModal by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = convertLocalDateToMillis(LocalDate.now(ZoneId.of("Asia/Jakarta")))
    )

    // Current date being displayed, initialized to today in WIB
    var currentDisplayDate by remember {
        mutableStateOf(LocalDate.now(ZoneId.of("Asia/Jakarta")))
    }

    // This LaunchedEffect updates currentDisplayDate when a date is picked from the DatePickerDialog
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            currentDisplayDate = convertMillisToLocalDate(it)
        }
    }

    // --- Dynamic Data for the selected day ---
    var totalCalories by remember { mutableStateOf(0) }
    var targetCalories by remember { mutableStateOf(0) }
    var currentProtein by remember { mutableStateOf(0) }
    var targetProtein by remember { mutableStateOf(0) }
    var currentFat by remember { mutableStateOf(0) }
    var targetFat by remember { mutableStateOf(0) }
    var currentCarbs by remember { mutableStateOf(0) }
    var targetCarbs by remember { mutableStateOf(0) }
    var mealsForDay by remember { mutableStateOf(emptyList<MealDataDummy>()) } // todo: ganti tipe datanya dengan data class sebenarnya

    LaunchedEffect(currentDisplayDate) {
        // Generate or fetch new data based on currentDisplayDate
        val (newTotalCalories, newMeals) = generateMockDailyData(currentDisplayDate) // todo: ganti ini dengan function buat ambil datanya berdasarkan tanggal
        val dailyTargets = DailyTargetsDummy() // todo: ganti tipe datanya dengan data class sebenarnya

        totalCalories = newTotalCalories
        targetCalories = dailyTargets.calories
        currentProtein = newMeals.sumOf { it.protein }
        targetProtein = dailyTargets.protein
        currentFat = newMeals.sumOf { it.fat }
        targetFat = dailyTargets.fat
        currentCarbs = newMeals.sumOf { it.carbs }
        mealsForDay = newMeals
    }
    // --- End Dynamic Data ---

    val formattedDate = currentDisplayDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault()))
    val progress = if (targetCalories > 0) totalCalories.toFloat() / targetCalories.toFloat() else 0f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Card
            NutritionSummaryCard(
                totalCalories = totalCalories,
                targetCalories = targetCalories,
                protein = Pair(currentProtein, targetProtein),
                fat = Pair(currentFat, targetFat),
                carbs = Pair(currentCarbs, targetCarbs),
                progress = progress
            )

            // Date Navigation
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            currentDisplayDate = currentDisplayDate.minusDays(1)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFF4CAF50),
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous day",
                            tint = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .clickable(
                                onClick = {
                                    showDateModal = true
                                }
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = formattedDate, // Display the actively changing formatted date
                            fontSize = 18.sp,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    IconButton(
                        onClick = {
                            currentDisplayDate = currentDisplayDate.plusDays(1)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFF4CAF50),
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next day",
                            tint = Color.White
                        )
                    }
                }
            }

            // Meal History Grouped by Type
            MealHistorySection(meals = mealsForDay) // Pass the dynamic meal list
        }
        TopBar(
            onBackClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )
    }

    if (showDateModal) {
        DatePickerDialog(
            onDismissRequest = { showDateModal = false },
            confirmButton = {
                TextButton(onClick = {
                    showDateModal = false
                    // datePickerState.selectedDateMillis is automatically observed by LaunchedEffect
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
}

@Composable
fun NutritionSummaryCard(
    totalCalories: Int,
    targetCalories: Int,
    protein: Pair<Int, Int>,
    fat: Pair<Int, Int>,
    carbs: Pair<Int, Int>,
    progress: Float
) {
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = if (startAnimation) progress else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "progress"
    )

    val animatedProteinProgress by animateFloatAsState(
        targetValue = if (startAnimation) protein.first.toFloat() / protein.second.toFloat().coerceAtLeast(1f) else 0f,
        animationSpec = tween(durationMillis = 1200, delayMillis = 300),
        label = "protein_progress"
    )

    val animatedFatProgress by animateFloatAsState(
        targetValue = if (startAnimation) fat.first.toFloat() / fat.second.toFloat().coerceAtLeast(1f) else 0f,
        animationSpec = tween(durationMillis = 1200, delayMillis = 500),
        label = "fat_progress"
    )

    val animatedCarbsProgress by animateFloatAsState(
        targetValue = if (startAnimation) carbs.first.toFloat() / carbs.second.toFloat().coerceAtLeast(1f) else 0f,
        animationSpec = tween(durationMillis = 1200, delayMillis = 700),
        label = "carbs_progress"
    )

    LaunchedEffect(totalCalories, protein, fat, carbs) { // Rerun animation when data changes
        startAnimation = false // Reset animation
        delay(200) // Small delay before starting
        startAnimation = true
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                // Progress circle with animation
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF4CAF50),
                    strokeWidth = 12.dp,
                    trackColor =  Color.Gray.copy(alpha = 0.2f)
                )

                // Center text showing percentage
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Nutrition Info
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "Total Calories:",
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "$totalCalories / $targetCalories kcal",
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                NutritionRow(
                    label = "Carbs:",
                    current = carbs.first,
                    target = carbs.second,
                    color = colorResource(R.color.carbs),
                    animatedProgress = animatedCarbsProgress
                )
                NutritionRow(
                    label = "Protein:",
                    current = protein.first,
                    target = protein.second,
                    color = colorResource(R.color.protein),
                    animatedProgress = animatedProteinProgress
                )
                NutritionRow(
                    label = "Fat:",
                    current = fat.first,
                    target = fat.second,
                    color = colorResource(R.color.fat),
                    animatedProgress = animatedFatProgress
                )
            }
        }
    }
}

@Composable
fun NutritionRow(
    label: String,
    current: Int,
    target: Int,
    color: Color,
    animatedProgress: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.width(60.dp)
        )

        // Progress bar with animation
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.Gray.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = animatedProgress.coerceAtMost(1f))
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$current / $target g",
            fontSize = 12.sp,
            fontFamily = OpenSans,
            color = Color.Gray
        )
    }
}

@Composable
fun MealHistorySection(meals: List<MealDataDummy>) { // Accept meals as a parameter
    // Group meals by type
    val groupedMeals = meals.groupBy { it.mealType }
    val mealOrder = listOf("Breakfast", "Lunch", "Dinner")

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (meals.isEmpty()) {
            Text(
                text = "No meals logged for this day.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontFamily = OpenSans,
                color = Color.Gray
            )
        } else {
            mealOrder.forEach { mealType ->
                groupedMeals[mealType]?.let { mealsOfType ->
                    MealTypeSection(
                        mealType = mealType,
                        meals = mealsOfType
                    )
                }
            }
        }
    }
}

@Composable
fun MealTypeSection(
    mealType: String,
    meals: List<MealDataDummy>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header
        Text(
            text = mealType,
            fontSize = 20.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // Meals in this section
        meals.forEach { meal ->
            MealHistoryCard(meal = meal, showMealType = false)
        }
    }
}

@Composable
fun MealHistoryCard(
    meal: MealDataDummy,
    showMealType: Boolean = true
) {
    // Animation states for scaling effect
    var startAnimation by remember { mutableStateOf(false) }

    val animatedScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 600),
        label = "scale_animation"
    )

    val greenGradient = Brush.verticalGradient(
        colors = listOf(
            colorResource(R.color.green),
            colorResource(R.color.green_teal_dark)
        )
    )
    val carbsGradient = Brush.verticalGradient(
        colors = listOf(
            colorResource(R.color.carbs_1),
            colorResource(R.color.carbs_2)
        )
    )
    val proteinGradient = Brush.verticalGradient(
        colors = listOf(
            colorResource(R.color.protein_1),
            colorResource(R.color.protein_2)
        )
    )
    val fatGradient = Brush.verticalGradient(
        colors = listOf(
            colorResource(R.color.fat_1),
            colorResource(R.color.fat_2)
        )
    )

    LaunchedEffect(meal) { // Rerun animation when meal data changes
        startAnimation = false
        delay(200)
        startAnimation = true
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(animatedScale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Only show meal title if not grouped by sections
            if (showMealType) {
                Text(
                    text = meal.mealType,
                    fontSize = 18.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Food item header - matching the image design
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = meal.foodName,
                    fontSize = 20.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Quantity: ${meal.quantity}",
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }

            // Nutrition cards in a single row - matching the image design
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Calories - Green
                NutritionCard(
                    label = "Calories",
                    value = meal.calories.toString(),
                    unit = "kcal",
                    backgroundColor = greenGradient,
                    modifier = Modifier.weight(1f)
                )

                // Carbs - Teal/Cyan
                NutritionCard(
                    label = "Carbs",
                    value = meal.carbs.toString(),
                    unit = "gram",
                    backgroundColor = carbsGradient,
                    modifier = Modifier.weight(1f)
                )

                // Protein - Orange
                NutritionCard(
                    label = "Protein",
                    value = meal.protein.toString(),
                    unit = "gram",
                    backgroundColor = proteinGradient,
                    modifier = Modifier.weight(1f)
                )

                // Fat - Red
                NutritionCard(
                    label = "Fat",
                    value = meal.fat.toString(),
                    unit = "gram",
                    backgroundColor = fatGradient,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun NutritionCard(
    label: String,
    value: String,
    unit: String,
    backgroundColor: Brush,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Label
            Text(
                text = label,
                fontSize = 14.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            // Value
            Text(
                text = value,
                fontSize = 24.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            // Unit
            Text(
                text = unit,
                fontSize = 14.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}