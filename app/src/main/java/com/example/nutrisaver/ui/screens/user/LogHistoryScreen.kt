package com.example.nutrisaver.ui.screens.user

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.data.model.DailyConsumptionDetail
import com.example.nutrisaver.ui.theme.OpenSans
import com.example.nutrisaver.viewmodel.LogMealViewModel
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

// --- SEMUA DATA DUMMY (data class dan function generateMock) DIHAPUS ---

// Helper function untuk konversi tanggal
private fun convertMillisToLocalDate(millis: Long): LocalDate {
    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
}

private fun convertLocalDateToMillis(date: LocalDate): Long {
    return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}


@Composable
fun LogHistoryScreen(
    navController: NavController,
    // Gunakan ViewModel yang sama sesuai permintaan Anda
    logMealViewModel: LogMealViewModel = viewModel()
) {
    Scaffold { innerPadding ->
        LogHistoryContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            viewModel = logMealViewModel
        )
    }
}

@Composable
private fun TopBar(modifier: Modifier = Modifier, onBackClick: () -> Unit = {}) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colorResource(R.color.bg))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }
        Text(text = "Log History", fontSize = 20.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(start = 8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogHistoryContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: LogMealViewModel
) {
    val backgroundGradient = Brush.verticalGradient(listOf(colorResource(id = R.color.bg2_1), colorResource(id = R.color.bg2_2)))

    // --- AMBIL STATE DARI VIEWMODEL ---
    val isLoading by viewModel.historyIsLoading.collectAsState()
    val selectedDate by viewModel.historySelectedDate.collectAsState()
    val consumptionData by viewModel.historyConsumptionData.collectAsState()

    // --- State untuk DatePicker ---
    var showDateModal by remember { mutableStateOf(false) }
    // DIUBAH: State ini sekarang benar-benar hanya untuk DatePicker itu sendiri
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = convertLocalDateToMillis(selectedDate))

    // --- Siapkan Variabel untuk UI dari State ViewModel ---
    val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault()))

    // Gunakan data dari consumptionData, atau nilai default jika null
    val totalCalories = consumptionData?.totalCalories?.toInt() ?: 0
    val targetCalories = consumptionData?.targetCalories?.toInt() ?: 2000
    val mealsForDay = consumptionData?.details ?: emptyList()

    val currentProtein = consumptionData?.totalProtein?.toInt() ?: 0
    val targetProtein = consumptionData?.targetProtein?.toInt() ?: 100

    val currentFat = consumptionData?.totalFat?.toInt() ?: 0
    val targetFat = consumptionData?.targetFat?.toInt() ?: 70

    val currentCarbs = consumptionData?.totalCarbs?.toInt() ?: 0
    val targetCarbs = consumptionData?.targetCarbs?.toInt() ?: 250

    // DIUBAH: Coerce (paksa) nilai progress agar berada di antara 0f dan 1f (0% - 100%)
    val progress = if (targetCalories > 0) (totalCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f) else 0f

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
            NutritionSummaryCard(
                totalCalories = totalCalories,
                targetCalories = targetCalories,
                protein = Pair(currentProtein, targetProtein),
                fat = Pair(currentFat, targetFat),
                carbs = Pair(currentCarbs, targetCarbs),
                progress = progress
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.changeHistoryDate(selectedDate.minusDays(1)) },
                        modifier = Modifier.size(40.dp).background(Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                    ) { Icon(Icons.Default.KeyboardArrowLeft, "Previous day", tint = Color.White) }

                    Box(
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp).clickable { showDateModal = true },
                        contentAlignment = Alignment.Center,
                    ) { Text(formattedDate, fontSize = 18.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold, color = Color.Black) }

                    IconButton(
                        onClick = { viewModel.changeHistoryDate(selectedDate.plusDays(1)) },
                        modifier = Modifier.size(40.dp).background(Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                    ) { Icon(Icons.Default.KeyboardArrowRight, "Next day", tint = Color.White) }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 32.dp))
            } else {
                MealHistorySection(meals = mealsForDay)
            }
        }
        TopBar(
            onBackClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
        )
    }

    if (showDateModal) {
        DatePickerDialog(
            onDismissRequest = { showDateModal = false },
            confirmButton = {
                TextButton(onClick = {
                    showDateModal = false
                    // Ambil tanggal yang dipilih dari state picker dan panggil ViewModel
                    datePickerState.selectedDateMillis?.let {
                        viewModel.changeHistoryDate(convertMillisToLocalDate(it))
                    }
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDateModal = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }
}

// ... (Composable NutritionSummaryCard dan NutritionRow tidak perlu diubah) ...
@Composable
fun NutritionSummaryCard(totalCalories: Int, targetCalories: Int, protein: Pair<Int, Int>, fat: Pair<Int, Int>, carbs: Pair<Int, Int>, progress: Float) {
    var startAnimation by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(targetValue = if (startAnimation) progress else 0f, animationSpec = tween(durationMillis = 1500), label = "")
    val animatedProteinProgress by animateFloatAsState(targetValue = if (startAnimation) protein.first.toFloat() / protein.second.toFloat().coerceAtLeast(1f) else 0f, animationSpec = tween(durationMillis = 1200, delayMillis = 300), label = "")
    val animatedFatProgress by animateFloatAsState(targetValue = if (startAnimation) fat.first.toFloat() / fat.second.toFloat().coerceAtLeast(1f) else 0f, animationSpec = tween(durationMillis = 1200, delayMillis = 500), label = "")
    val animatedCarbsProgress by animateFloatAsState(targetValue = if (startAnimation) carbs.first.toFloat() / carbs.second.toFloat().coerceAtLeast(1f) else 0f, animationSpec = tween(durationMillis = 1200, delayMillis = 700), label = "")

    LaunchedEffect(totalCalories) {
        startAnimation = false
        delay(100)
        startAnimation = true
    }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(progress = { animatedProgress }, modifier = Modifier.fillMaxSize(), color = Color(0xFF4CAF50), strokeWidth = 12.dp, trackColor = Color.Gray.copy(alpha = 0.2f))
                Text(text = "${(animatedProgress * 100).toInt()}%", fontSize = 16.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Total Calories:", fontSize = 16.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "$totalCalories / $targetCalories kcal", fontSize = 14.sp, fontFamily = OpenSans, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                NutritionRow(label = "Carbs:", current = carbs.first, target = carbs.second, color = colorResource(R.color.carbs), animatedProgress = animatedCarbsProgress)
                NutritionRow(label = "Protein:", current = protein.first, target = protein.second, color = colorResource(R.color.protein), animatedProgress = animatedProteinProgress)
                NutritionRow(label = "Fat:", current = fat.first, target = fat.second, color = colorResource(R.color.fat), animatedProgress = animatedFatProgress)
            }
        }
    }
}


@Composable
fun NutritionRow(label: String, current: Int, target: Int, color: Color, animatedProgress: Float) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontSize = 14.sp, fontFamily = OpenSans, fontWeight = FontWeight.Medium, color = Color.Black, modifier = Modifier.width(60.dp))
        Box(modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)).background(Color.Gray.copy(alpha = 0.2f))) {
            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(fraction = animatedProgress.coerceAtMost(1f)).clip(RoundedCornerShape(3.dp)).background(color))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$current / $target g", fontSize = 12.sp, fontFamily = OpenSans, color = Color.Gray)
    }
}


@Composable
fun MealHistorySection(meals: List<DailyConsumptionDetail>) {
    val groupedMeals = meals.groupBy { it.mealType.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() } }
    val mealOrder = listOf("Breakfast", "Lunch", "Dinner")

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        if (meals.isEmpty()) {
            Text(text = "No meals logged for this day.", modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), textAlign = TextAlign.Center, fontSize = 16.sp, fontFamily = OpenSans, color = Color.Gray)
        } else {
            mealOrder.forEach { mealType ->
                groupedMeals[mealType]?.let { mealsOfType ->
                    MealTypeSection(mealType = mealType, meals = mealsOfType)
                }
            }
        }
    }
}


@Composable
fun MealTypeSection(mealType: String, meals: List<DailyConsumptionDetail>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = mealType, fontSize = 20.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 4.dp))
        meals.forEach { meal ->
            MealHistoryCard(meal = meal, showMealType = false)
        }
    }
}


@Composable
fun MealHistoryCard(meal: DailyConsumptionDetail, showMealType: Boolean = true) {
    var startAnimation by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(targetValue = if (startAnimation) 1f else 0.8f, animationSpec = tween(durationMillis = 600), label = "")
    val greenGradient = Brush.verticalGradient(colors = listOf(colorResource(R.color.green), colorResource(R.color.green_teal_dark)))
    val carbsGradient = Brush.verticalGradient(colors = listOf(colorResource(R.color.carbs_1), colorResource(R.color.carbs_2)))
    val proteinGradient = Brush.verticalGradient(colors = listOf(colorResource(R.color.protein_1), colorResource(R.color.protein_2)))
    val fatGradient = Brush.verticalGradient(colors = listOf(colorResource(R.color.fat_1), colorResource(R.color.fat_2)))

    LaunchedEffect(meal) {
        startAnimation = false
        delay(200)
        startAnimation = true
    }

    Card(modifier = Modifier.fillMaxWidth().scale(animatedScale), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (showMealType) {
                Text(text = meal.mealType, fontSize = 18.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = meal.foodName, fontSize = 20.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f, fill=false))
                Text(text = "Qty: ${meal.quantity.toInt()} ${meal.unit}", fontSize = 16.sp, fontFamily = OpenSans, fontWeight = FontWeight.Medium, color = Color.Gray)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NutritionCard(label = "Calories", value = meal.calories.toInt().toString(), unit = "kcal", backgroundColor = greenGradient, modifier = Modifier.weight(1f))
                NutritionCard(label = "Carbs", value = meal.carbs.toInt().toString(), unit = "gram", backgroundColor = carbsGradient, modifier = Modifier.weight(1f))
                NutritionCard(label = "Protein", value = meal.protein.toInt().toString(), unit = "gram", backgroundColor = proteinGradient, modifier = Modifier.weight(1f))
                NutritionCard(label = "Fat", value = meal.fat.toInt().toString(), unit = "gram", backgroundColor = fatGradient, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun NutritionCard(label: String, value: String, unit: String, backgroundColor: Brush, modifier: Modifier = Modifier) {
    Box(modifier = modifier.height(80.dp).clip(RoundedCornerShape(12.dp)).background(backgroundColor), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = label, fontSize = 14.sp, fontFamily = OpenSans, fontWeight = FontWeight.Medium, color = Color.White, textAlign = TextAlign.Center)
            Text(text = value, fontSize = 24.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
            Text(text = unit, fontSize = 14.sp, fontFamily = OpenSans, fontWeight = FontWeight.Medium, color = Color.White, textAlign = TextAlign.Center)
        }
    }
}
