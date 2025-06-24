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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutrisaver.R
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import com.example.nutrisaver.ui.theme.OpenSans
import java.time.format.DateTimeFormatter

// Data class for food stock items
data class FoodStockItemDummy(
    val id: Int,
    val name: String,
    val imageUrl: String? = null, // For future use with actual images
    val quantity: Float,
    val unit: String, // kg, pcs, ml, etc.
    val expiryDate: LocalDate,
    val startRemindDate: LocalDate // 7 days before expiry
) {
    fun getDaysUntilExpiry(): Long {
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate)
    }

    fun getExpiryStatus(): ExpiryStatus {
        val daysUntilExpiry = getDaysUntilExpiry()
        return when {
            daysUntilExpiry < 0 -> ExpiryStatus.EXPIRED
            daysUntilExpiry <= 3 -> ExpiryStatus.CRITICAL
            daysUntilExpiry <= 7 -> ExpiryStatus.WARNING
            else -> ExpiryStatus.SAFE
        }
    }
}

enum class ExpiryStatus {
    EXPIRED, CRITICAL, WARNING, SAFE
}

// Generate dummy food stock data
fun generateDummyFoodStock(): List<FoodStockItemDummy> {
    val today = LocalDate.now()
    return listOf(
        FoodStockItemDummy(
            id = 1,
            name = "Chicken Meat",
            quantity = 2.5f,
            unit = "kg",
            expiryDate = today.plusDays(2),
            startRemindDate = today.minusDays(5)
        ),
        FoodStockItemDummy(
            id = 2,
            name = "Fresh Milk",
            quantity = 1.0f,
            unit = "L",
            expiryDate = today.plusDays(1),
            startRemindDate = today.minusDays(6)
        ),
        FoodStockItemDummy(
            id = 3,
            name = "Bread",
            quantity = 2.0f,
            unit = "pcs",
            expiryDate = today.plusDays(3),
            startRemindDate = today.minusDays(4)
        ),
        FoodStockItemDummy(
            id = 4,
            name = "Yogurt",
            quantity = 6.0f,
            unit = "pcs",
            expiryDate = today.plusDays(5),
            startRemindDate = today.minusDays(2)
        ),
        FoodStockItemDummy(
            id = 5,
            name = "Ground Beef",
            quantity = 1.2f,
            unit = "kg",
            expiryDate = today.plusDays(4),
            startRemindDate = today.minusDays(3)
        ),
        FoodStockItemDummy(
            id = 6,
            name = "Eggs",
            quantity = 12.0f,
            unit = "pcs",
            expiryDate = today.plusDays(7),
            startRemindDate = today
        )
    ).filter { it.getDaysUntilExpiry() <= 7 } // Only show items expiring within 7 days
        .sortedBy { it.expiryDate } // Sort by expiry date (most urgent first)
}

@Composable
fun FoodStockExpirationSection(
    modifier: Modifier = Modifier,
    onViewAllClick: () -> Unit = {}
) {
    val foodStockItems = remember { generateDummyFoodStock() }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.form_input)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 10.dp,
                    bottom = 20.dp
                )
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Food Stock Expiring Soon",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OpenSans,
                    color = Color.Black
                )

                if (foodStockItems.isNotEmpty()) {
                    TextButton(
                        onClick = onViewAllClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "View All",
                            fontSize = 14.sp,
                            fontFamily = OpenSans,
                            color = colorResource(R.color.green),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            if (foodStockItems.isEmpty()) {
                Text(
                    text = "All your food stock is fresh",
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            } else {
                // Show food items (limit to 3 for dashboard)
                val displayItems = foodStockItems.take(3)

                displayItems.forEachIndexed { index, item ->
                    FoodStockItemCard(
                        item = item,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (index < displayItems.size - 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // Show count if there are more items
                if (foodStockItems.size > 3) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(R.color.green).copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = "+${foodStockItems.size - 3} more items expiring soon",
                            fontSize = 14.sp,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Medium,
                            color = colorResource(R.color.green),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FoodStockItemCard(
    item: FoodStockItemDummy,
    modifier: Modifier = Modifier
) {
    val expiryStatus = item.getExpiryStatus()
    val daysUntilExpiry = item.getDaysUntilExpiry()

    // Optimized colors based on expiry status
    val (backgroundColor, statusBackgroundColor, statusText) = when (expiryStatus) {
        ExpiryStatus.EXPIRED -> Triple(
            Color(0xFFFFCDD2), // Light red background
            Color(0xFFD32F2F), // Dark red for status
            "Expired"
        )
        ExpiryStatus.CRITICAL -> Triple(
            Color(0xFFFFC6B5), // Coral/salmon background (like in image)
            Color(0xFFD32F2F), // Red for critical status
            if (daysUntilExpiry == 0L) "Expires today" else "Expires in $daysUntilExpiry day${if (daysUntilExpiry != 1L) "s" else ""}"
        )
        ExpiryStatus.WARNING -> Triple(
            Color(0xFFFFF3E0), // Light orange background
            Color(0xFFFF8F00), // Orange for warning
            "Expires in $daysUntilExpiry day${if (daysUntilExpiry != 1L) "s" else ""}"
        )
        ExpiryStatus.SAFE -> Triple(
            Color(0xFFE8F5E8), // Light green background
            Color(0xFF4CAF50), // Green for safe
            "Expires in $daysUntilExpiry day${if (daysUntilExpiry != 1L) "s" else ""}"
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side: Image and text content
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.default_food_image),
                    contentDescription = "Food image for ${item.name}",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Text Content
                Column {
                    Text(
                        text = item.name,
                        fontSize = 18.sp,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Stock: ${item.quantity} ${item.unit}",
                        fontSize = 14.sp,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }

            // Right side: Status Badge
            Card(
                shape = RoundedCornerShape(20.dp), // Pill shape
                colors = CardDefaults.cardColors(containerColor = statusBackgroundColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}