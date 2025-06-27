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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutrisaver.R
import com.example.nutrisaver.data.model.DailyConsumptionDetail
import com.example.nutrisaver.ui.theme.OpenSans

@Composable
fun MealLogSection(
    type: String,
    calories: Float,
    protein: Float,
    fat: Float,
    carbs: Float,
    details: List<DailyConsumptionDetail>, // <-- BARU: Terima daftar detail
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    NutritionItem(label = "Calories", value = calories.toInt().toString(), unit = "kcal")
                    NutritionItem(label = "Protein", value = String.format("%.1f", protein), unit = "g")
                    NutritionItem(label = "Fat", value = String.format("%.1f", fat), unit = "g")
                    NutritionItem(label = "Carbs", value = String.format("%.1f", carbs), unit = "g")
                }

                if (details.isNotEmpty()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 1.dp,
                        color = Color.Black.copy(alpha = 0.1f)
                    )

                    // Gunakan Column untuk menampilkan daftar item
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        details.forEach { detailItem ->
                            MealDetailItemRow(
                                foodName = detailItem.foodName,
                                calories = detailItem.calories.toInt()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealDetailItemRow(
    foodName: String,
    calories: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = foodName,
            fontSize = 14.sp,
            fontFamily = OpenSans,
            color = Color.Black.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f) // Agar bisa menangani teks panjang
        )
        Text(
            text = "$calories kcal",
            fontSize = 14.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black.copy(alpha = 0.9f)
        )
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