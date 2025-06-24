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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import java.time.LocalDate

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

            CalorieProgressSection(
                // todo: insert parameternya disini nanti
                currentCalories = 1721,
                targetCalories = 2213,
                protein = Pair(78, 90),
                fats = Pair(45, 70),
                carbs = Pair(95, 110),
                modifier = Modifier.fillMaxWidth()
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
                MealLogSection(
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
                MealLogSection(
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
                // After saving, consider refreshing the weight data if it's stored locally
                // For now, let's just dismiss
                showWeightLogBottomSheet = false
            }
        )
    }
}