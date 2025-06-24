package com.example.nutrisaver.ui.screens.user.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.theme.OpenSans
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CalorieProgressSection(
    modifier: Modifier = Modifier,
    currentCalories: Int = 1721, // default value
    targetCalories: Int = 2213,
    protein: Pair<Int, Int> = Pair(78, 90),
    fats: Pair<Int, Int> = Pair(45, 70),
    carbs: Pair<Int, Int> = Pair(95, 110)
) {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("id", "ID"))
    val formattedDate = currentDate.format(formatter)

    val progress = currentCalories.toFloat() / targetCalories.toFloat()

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
                    val circleSize = minOf(containerWidth * 1.6f, 180.dp)
                    val circleHeight = circleSize * 0.8f

                    Box(
                        modifier = Modifier.size(circleSize, circleHeight),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Canvas(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val strokeWidth = (circleSize.value * 0.1f).dp.toPx()
                            val radius = (circleSize.value * 0.42f).dp.toPx()
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
                                    radius = (circleSize.value * 0.03f).dp.toPx(),
                                    center = Offset(dotX, dotY)
                                )
                            }
                        }

                        // Center content positioned under the arc - Made bigger
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = (-15).dp)
                        ) {
                            Text(
                                text = "🔥",
                                fontSize = (circleSize.value * 0.18f).sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "$currentCalories Kcal",
                                fontSize = (circleSize.value * 0.12f).sp,
                                fontFamily = OpenSans,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "of $targetCalories Kcal",
                                fontSize = (circleSize.value * 0.08f).sp,
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