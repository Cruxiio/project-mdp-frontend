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
                .padding(horizontal = 32.dp, vertical = 16.dp)
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(colorResource(R.color.form_input), shape = CircleShape)
                    .border(2.dp, colorResource(R.color.gray_border), shape = CircleShape)
            ) {
                Row {
                    Text(
                        "Your Current Goal",
                        fontSize = 18.sp,
                        fontFamily = OpenSans,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(greenGradient, shape = CircleShape)
                    ) {
                        Text(
                            "Lose Weight", // todo: ubah ini jadi goalnya user
                            fontSize = 18.sp,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            CalorieProgressBar(
                // todo: insert parameternya disini nanti
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), thickness = 2.dp)

            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Your Current Goal",
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

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MealLogCard(
                    type = "Breakfast",
                    calories = breakfastCalories,
                    gradient = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(R.color.blue_1),
                            colorResource(R.color.blue_2)
                        )
                    ),
                    onLogClick = { onLogClick("breakfast") },
                    modifier = Modifier.weight(1f)
                )
                MealLogCard(
                    type = "Lunch",
                    calories = lunchCalories,
                    gradient = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(R.color.yellow_1),
                            colorResource(R.color.yellow_2)
                        )
                    ),
                    onLogClick = { onLogClick("lunch") },
                    modifier = Modifier.weight(1f)
                )
                MealLogCard(
                    type = "Dinner",
                    calories = dinnerCalories,
                    gradient = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(R.color.pink_1),
                            colorResource(R.color.pink_2)
                        )
                    ),
                    onLogClick = { onLogClick("dinner") },
                    modifier = Modifier.weight(1f)
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.form_input), shape = RoundedCornerShape(20.dp))
                    .border(2.dp, colorResource(R.color.gray_border), shape = RoundedCornerShape(20.dp))
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
        }
    }
}

fun onLogClick(s: String) {

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
            .height(48.dp)
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
    gradient: Brush,
    onLogClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
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
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // Top content
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = type,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = OpenSans
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Total kcal",
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.7f),
                        fontFamily = OpenSans
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = calories?.value?.let { "$it kcal" } ?: "-",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = OpenSans
                    )
                }

                // Log button at bottom
                Button(
                    onClick = onLogClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(18.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Log",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = OpenSans
                    )
                }
            }
        }
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
    var animationPlayed by remember { mutableStateOf(false) }
    val progress = currentCalories.toFloat() / targetCalories.toFloat()

    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) progress else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular Progress Bar - responsive sizing
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val containerWidth = maxWidth // 85% of available width
            val containerHeight = containerWidth * 0.6f // Height is 60% of width for semi-circle

            Box(
                modifier = Modifier.size(containerWidth, containerHeight),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawCircularProgressBar(
                        progress = animatedProgress,
                        size = size
                    )
                }

                // Center content - scales with container size
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.offset(y = containerHeight * 0.1f) // 10% of container height
                ) {
                    Text(
                        text = "🔥",
                        fontSize = (containerWidth.value * 0.12f).sp // Font size scales with width
                    )
                    Spacer(modifier = Modifier.height(containerHeight * 0.05f))
                    Text(
                        text = "$currentCalories Kcal",
                        fontSize = (containerWidth.value * 0.09f).sp, // Responsive font size
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = OpenSans
                    )
                    Text(
                        text = "of $targetCalories kcal",
                        fontSize = (containerWidth.value * 0.045f).sp, // Responsive font size
                        color = Color.Gray,
                        fontFamily = OpenSans
                    )
                }
            }
        }

        // Macronutrients Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MacronutrientCard(
                title = "Protein",
                current = protein.first,
                target = protein.second,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            MacronutrientCard(
                title = "Fats",
                current = fats.first,
                target = fats.second,
                color = Color(0xFFFF7043),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            MacronutrientCard(
                title = "Carbs",
                current = carbs.first,
                target = carbs.second,
                color = Color(0xFFFFC107),
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
fun MacronutrientCard(
    title: String,
    current: Int,
    target: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            fontFamily = OpenSans
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Progress indicator line
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
                    .fillMaxWidth(fraction = (current.toFloat() / target.toFloat()).coerceAtMost(1f))
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$current/${target}g",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            fontFamily = OpenSans
        )
    }
}


fun DrawScope.drawCircularProgressBar(
    progress: Float,
    size: Size
) {
    val strokeWidth = 26.dp.toPx()
    val radius = (size.minDimension - strokeWidth) / 2
    val center = Offset(size.width / 2, size.height / 2)

    // Background arc
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

    // Progress arc
    val sweepAngle = 180f * progress
    drawArc(
        color = Color(0xFF39a23d),
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
    if (progress > 0) {
        val angle = Math.toRadians((180 + sweepAngle).toDouble())
        val dotX = center.x + radius * cos(angle).toFloat()
        val dotY = center.y + radius * sin(angle).toFloat()

        drawCircle(
            color = Color.White,
            radius = 7.dp.toPx(),
            center = Offset(dotX, dotY)
        )
    }
}