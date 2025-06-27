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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.nutrisaver.data.model.FoodStock
import com.example.nutrisaver.ui.screens.user.ExpiryStatus
import com.example.nutrisaver.ui.screens.user.FoodStockItemDummy
import com.example.nutrisaver.ui.screens.user.generateDummyFoodStock


enum class ExpiryStatus {
    EXPIRED, CRITICAL, WARNING, SAFE
}

// Fungsi helper untuk model FoodStock
fun FoodStock.getDaysUntilExpiry(): Long {
    return ChronoUnit.DAYS.between(LocalDate.now(), this.expiredDate)
}

fun FoodStock.getExpiryStatus(): ExpiryStatus {
    val daysUntilExpiry = getDaysUntilExpiry()
    return when {
        daysUntilExpiry < 0 -> ExpiryStatus.EXPIRED
        daysUntilExpiry <= 3 -> ExpiryStatus.CRITICAL
        daysUntilExpiry <= 10 -> ExpiryStatus.WARNING // Sesuai logika: 4-10 hari
        else -> ExpiryStatus.SAFE
    }
}

@Composable
fun FoodStockExpirationSection(
    modifier: Modifier = Modifier,
    foodStockItems: List<FoodStock>, // todo: ganti dengan object FoodStockItem dari backend
    onViewAllClick: () -> Unit = {}
) {
    var showBottomSheet by remember { mutableStateOf(false) }

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showBottomSheet = true },
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

    if (showBottomSheet) {
        FoodStockBottomSheet(
            onDismiss = { showBottomSheet = false },
            foodStockItems = foodStockItems
        )
    }
}

@Composable
fun FoodStockItemCard(
    item: FoodStock, // todo: ganti dengan object FoodStockItem dari backend
    modifier: Modifier = Modifier
) {
    val expiryStatus = item.getExpiryStatus()
    val daysUntilExpiry = item.getDaysUntilExpiry()

    // Optimized colors based on expiry status
    val (backgroundColor, statusTextColor, statusText) = when (expiryStatus) {
        ExpiryStatus.EXPIRED -> Triple(
            Color(0xFFFFA9AD), // warna background
            Color(0xFFD32F2F), // warna tag status
            "Expired"
        )
        ExpiryStatus.CRITICAL -> Triple(
            Color(0xFFFFCAB9), // warna background
            Color(0xFFD32F2F), // warna tag
            if (daysUntilExpiry == 0L) "Expires today" else "Expires in $daysUntilExpiry day${if (daysUntilExpiry != 1L) "s" else ""}"
        )
        ExpiryStatus.WARNING -> Triple(
            Color(0xFFFFF3E0), // warna background
            Color(0xFFFF8F00), // warna tag
            "Expires in $daysUntilExpiry day${if (daysUntilExpiry != 1L) "s" else ""}"
        )
        ExpiryStatus.SAFE -> Triple(
            Color(0xFFE8F5E8), // warna background
            Color(0xFF4CAF50), // warna tag
            "Expires in $daysUntilExpiry day${if (daysUntilExpiry != 1L) "s" else ""}"
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.default_food_image),
                contentDescription = "Food image for ${item.name}",
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
            Column {
                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Stock: ${item.quantity} ${item.unit}",
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = statusText,
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = statusTextColor,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodStockBottomSheet(
    onDismiss: () -> Unit,
    foodStockItems: List<FoodStock>
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    ) {
        FoodStockBottomSheetContent(
            foodStockItems = foodStockItems,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun FoodStockBottomSheetContent(
    foodStockItems: List<FoodStock>, // todo: ganti dengan object FoodStockItem dari backend, hapus default valuenya
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Food Stock Expiring Soon",
                fontSize = 20.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            TextButton(
                onClick = onDismiss,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Close",
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    color = colorResource(R.color.green),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Summary info
        if (foodStockItems.isNotEmpty()) {
            val expiredCount = foodStockItems.count { it.getExpiryStatus() == ExpiryStatus.EXPIRED }
            val criticalCount = foodStockItems.count { it.getExpiryStatus() == ExpiryStatus.CRITICAL }
            val warningCount = foodStockItems.count { it.getExpiryStatus() == ExpiryStatus.WARNING }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorResource(R.color.form_input))
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (expiredCount > 0) {
                        SummaryItem(
                            count = expiredCount,
                            label = "Expired",
                            color = Color(0xFFD32F2F)
                        )
                    }
                    if (criticalCount > 0) {
                        SummaryItem(
                            count = criticalCount,
                            label = "Critical",
                            color = Color(0xFFD32F2F)
                        )
                    }
                    if (warningCount > 0) {
                        SummaryItem(
                            count = warningCount,
                            label = "Warning",
                            color = Color(0xFFFF8F00)
                        )
                    }
                }
            }
        }

        // Scrollable list of all items
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(foodStockItems) { item ->
                FoodStockItemCard(
                    item = item,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Add some bottom padding for the last item
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun SummaryItem(
    count: Int,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            fontSize = 24.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
    }
}