package com.example.nutrisaver.ui.screens.user.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.theme.OpenSans

@Composable
fun WaterIntakeSection(
    modifier: Modifier = Modifier,
    currentIntake: Int,
    targetIntake: Int,
    //currentIntake: MutableState<Int>, // progress user saat ini
    //targetIntake: Int, // target water user atau maksimumnya
    onAdd240ml: () -> Unit,
    onAdd500ml: () -> Unit,
    onReduceClick: () -> Unit,
    onCustomClick: () -> Unit
) {
    val waterIntakeProgress = if (targetIntake > 0) {
        minOf(currentIntake.toFloat() / targetIntake.toFloat(), 1f)
    } else {
        0f
    }

    Card(
        modifier = modifier.fillMaxWidth(),
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
                text = "$currentIntake / $targetIntake ml (${(waterIntakeProgress * 100).toInt()}%)",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                fontFamily = OpenSans,
                color = colorResource(R.color.water_3)
            )
            LinearProgressIndicator(
                progress = {
                    waterIntakeProgress
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
                    onClick = onAdd240ml
                )

                // 500ml Button - now adds without limit
                WaterIntakeButton(
                    text = "500 ml",
                    icon = painterResource(R.drawable.bottle_icon),
                    iconSize = 20.dp,
                    modifier = Modifier.weight(1f),
                    onClick = onAdd500ml
                )
            }
            // Action Buttons - Custom and Reduce
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WaterIntakeButton(
                    text = "Reduce",
                    icon = painterResource(id = R.drawable.ic_remove),
                    iconSize = 28.dp,
                    modifier = Modifier.weight(1f),
                    onClick = onReduceClick
                )
                WaterIntakeButton(
                    text = "Custom",
                    icon = rememberVectorPainter(Icons.Default.Add),
                    iconSize = 28.dp,
                    modifier = Modifier.weight(1f),
                    onClick = onCustomClick
                )
            }
        }
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