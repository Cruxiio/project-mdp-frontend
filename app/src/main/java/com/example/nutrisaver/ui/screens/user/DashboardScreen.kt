package com.example.nutrisaver.ui.screens.user

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.cos
import kotlin.math.sin
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    val waterIntakeProgress = currentIntake.value.toFloat() / targetIntake.toFloat()

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
                        // Todo: see history
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

            Text(
                "Water Intake",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = OpenSans,
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
                    Text(
                        "${currentIntake.value} / $targetIntake ml (${(waterIntakeProgress * 100).toInt()}%)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        fontFamily = OpenSans,
                        color = colorResource(R.color.water_3)
                    )
                    LinearProgressIndicator(
                        progress = waterIntakeProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(CircleShape),
                        color = colorResource(R.color.water_1),
                        trackColor = Color.Gray.copy(alpha = 0.2f)
                    )
                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 240ml Button
                        WaterIntakeButton(
                            text = "240 ml",
                            icon = painterResource(R.drawable.glass_icon),
                            iconSize = 20.dp, // Standard size
                            modifier = Modifier.weight(1f),
                            onClick = {
                                currentIntake.value = minOf(currentIntake.value + 240, targetIntake)
                            }
                        )

                        // 500ml Button
                        WaterIntakeButton(
                            text = "500 ml",
                            icon = painterResource(R.drawable.bottle_icon),
                            iconSize = 20.dp, // Standard size
                            modifier = Modifier.weight(1f),
                            onClick = {
                                currentIntake.value = minOf(currentIntake.value + 500, targetIntake)
                            }
                        )
                    }
                    // Custom Button
                    WaterIntakeButton(
                        text = "Custom",
                        icon = rememberVectorPainter(Icons.Default.Add),
                        iconSize = 28.dp,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            // Handle custom amount input
                        }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), thickness = 2.dp)

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Weight Report",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OpenSans,
                )
                Spacer(modifier = Modifier.weight(1f))
            }

        }
    }
}

@Composable
fun WaterIntakeButton(
    text: String,
    icon: Painter,
    iconSize: Dp = 20.dp,
    modifier: Modifier = Modifier,
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

@Composable
fun MealLogCard(
    type: String,
    calories: MutableState<Int>?,
    protein: Float = 0f,
    fat: Float = 0f,
    carbs: Float = 0f,
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
    currentCalories: Int = 1721, // default value
    targetCalories: Int = 2213,
    protein: Pair<Int, Int> = Pair(78, 90),
    fats: Pair<Int, Int> = Pair(45, 70),
    carbs: Pair<Int, Int> = Pair(95, 110),
    modifier: Modifier = Modifier
) {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault())
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

    // Macronutrients with Animation
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