package com.example.nutrisaver.ui.screens.user.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.theme.OpenSans

@Composable
fun MealLogSection(
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

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(10.dp))

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