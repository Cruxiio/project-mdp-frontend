package com.example.nutrisaver.ui.screens.user.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutrisaver.R
import com.example.nutrisaver.data.model.WeightLog
import com.example.nutrisaver.ui.screens.user.WeightEntryDummy
import com.example.nutrisaver.ui.theme.OpenSans
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

@SuppressLint("DefaultLocale")
@Composable
fun WeightReportSection(
    modifier: Modifier = Modifier,
    weightData: List<WeightLog>, // todo: ganti ke object aslinya setelah backend jadi
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit,
    onLogWeightClick: () -> Unit,
) {
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    var expanded by remember { mutableStateOf(false) }
    val periodOptions = listOf("Weekly", "Monthly", "Yearly")

    val weightChange = if (weightData.size >= 2) {
        val latestWeight = weightData.last().weight
        val previousWeight = weightData.first().weight
        latestWeight - previousWeight
    } else 0f

    // summary textnya dibawah graph
    val changeText = when {
        weightChange > 0 -> {
            val startDate = weightData.firstOrNull()?.date?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) ?: "start of period"
            "Your weight increases by ${String.format("%.1f", weightChange)}kg from $startDate."
        }
        weightChange < 0 -> {
            val startDate = weightData.firstOrNull()?.date?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) ?: "start of period"
            "Your weight decreases by ${String.format("%.1f", abs(weightChange))}kg from $startDate."
        }
        else -> "Your weight remains stable for the selected period."
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.form_input)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Weight Report",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OpenSans,
                    color = Color.Black
                )

                Box {
                    Card(
                        modifier = Modifier
                            .clickable { expanded = !expanded }
                            .padding(0.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = selectedPeriod,
                                fontSize = 14.sp,
                                fontFamily = OpenSans,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Dropdown Menu
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(
                                Color.White,
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        periodOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option,
                                        fontFamily = OpenSans,
                                        fontSize = 14.sp,
                                        fontWeight = if (option == selectedPeriod) FontWeight.Bold else FontWeight.Normal,
                                        color = if (option == selectedPeriod) green else Color.Black
                                    )
                                },
                                onClick = {
                                    onPeriodChange(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Weight Chart with better alignment
            WeightLineChart(
                weightData = weightData,
                selectedPeriod = selectedPeriod,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        weightChange > 0 -> colorResource(R.color.yellow_1).copy(alpha = 0.3f)
                        weightChange < 0 -> colorResource(R.color.green).copy(alpha = 0.3f)
                        else -> Color.Gray.copy(alpha = 0.2f)
                    }
                )
            ) {
                Text(
                    text = "Summary:",
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp
                    )
                )
                Text(
                    text = changeText,
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        bottom = 16.dp,
                        end = 16.dp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Log Weight Button
            Button(
                onClick = onLogWeightClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(greenGradient, RoundedCornerShape(25.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Log Weight",
                        fontSize = 18.sp,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun WeightLineChart(
    weightData: List<WeightLog>, // todo: ganti ke object aslinya setelah backend jadi
    selectedPeriod: String,
    modifier: Modifier = Modifier
) {
    val chartColor = colorResource(id = R.color.water_1)

    Canvas(modifier = modifier) {
        if (weightData.isEmpty()) {
            drawContext.canvas.nativeCanvas.drawText(
                "No weight data available for this period.",
                size.width / 2,
                size.height / 2,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 14.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
            return@Canvas
        }

        val padding = 40.dp.toPx()
        val chartWidth = size.width - (padding * 2)
        val chartHeight = size.height - (padding * 2)

        val minWeight = weightData.minOf { it.weight } - 2f
        val maxWeight = weightData.maxOf { it.weight } + 2f
        val weightRange = maxWeight - minWeight

        // Draw grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = padding + (chartHeight * i / gridLines)
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(padding, y),
                end = Offset(size.width - padding, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Calculate points
        val points = weightData.mapIndexed { index, entry ->
            val x = padding + (chartWidth * index / (weightData.size - 1).coerceAtLeast(1))
            val y = padding + chartHeight - ((entry.weight - minWeight) / weightRange * chartHeight)
            Offset(x, y)
        }

        // Draw line chart
        if (points.size > 1) {
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = chartColor,
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Draw points
        points.forEach { point ->
            drawCircle(
                color = chartColor.copy(alpha = 0.3f),
                radius = 8.dp.toPx(),
                center = point
            )
            drawCircle(
                color = chartColor,
                radius = 6.dp.toPx(),
                center = point
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = point
            )
        }

        // Draw Y-axis labels (weights)
        for (i in 0..gridLines) {
            val weight = minWeight + (weightRange * i / gridLines)
            val y = padding + (chartHeight * (gridLines - i) / gridLines)

            drawContext.canvas.nativeCanvas.drawText(
                "${String.format("%.0f", weight)}kg",
                padding - 15.dp.toPx(),
                y + 5.dp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 11.sp.toPx()
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
            )
        }

        // Draw X-axis labels (dates)
        val xAxisLabelFormatter = when (selectedPeriod) {
            "Weekly" -> DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault())
            "Monthly" -> DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault())
            "Yearly" -> DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault())
            else -> DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault())
        }

        val maxLabels = 5
        val labelInterval = if (weightData.size > maxLabels) weightData.size / maxLabels else 1

        weightData.forEachIndexed { index, entry ->
            if (index % labelInterval == 0 || index == weightData.size - 1) {
                val formattedDate = entry.date.format(xAxisLabelFormatter)

                val x = padding + (chartWidth * index / (weightData.size - 1).coerceAtLeast(1))
                val yOffset = if (index % 2 != 0 && weightData.size > maxLabels * 1.5) 15.dp.toPx() else 0f
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 10.sp.toPx()
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                drawContext.canvas.nativeCanvas.drawText(
                    formattedDate,
                    x,
                    size.height - 10.dp.toPx() + yOffset,
                    textPaint
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightLogBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (Float, String, LocalDate) -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("kg") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        WeightLogBottomSheetContent(
            weight = weight,
            onWeightChange = { weight = it },
            unit = unit,
            onUnitChange = { unit = it },
            selectedDate = selectedDate,
            onDateChange = { selectedDate = it },
            showDatePicker = showDatePicker,
            onShowDatePicker = { showDatePicker = it },
            onConfirm = {
                val weightValue = weight.toFloatOrNull()
                if (weightValue != null && weightValue > 0) {
                    onConfirm(weightValue, unit, selectedDate)
                }
            },
            onDismiss = onDismiss
        )
    }
}

@SuppressLint("DefaultLocale", "WeekBasedYear")
@Composable
fun WeightLogBottomSheetContent(
    weight: String,
    onWeightChange: (String) -> Unit,
    unit: String,
    onUnitChange: (String) -> Unit,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    showDatePicker: Boolean,
    onShowDatePicker: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM YYYY", Locale.getDefault())
    val isValidWeight = weight.toFloatOrNull()?.let { it > 0 } ?: false

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Log Your Weight",
            fontSize = 20.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Current Weight",
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = weight,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            onWeightChange(newValue)
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Enter weight",
                            fontFamily = OpenSans,
                            color = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = green,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Medium
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("kg", "lbs").forEach { unitOption ->
                        Button(
                            onClick = { onUnitChange(unitOption) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (unit == unitOption) green else Color.Gray.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = unitOption,
                                color = if (unit == unitOption) Color.White else Color.Gray,
                                fontSize = 14.sp,
                                fontFamily = OpenSans,
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Date",
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShowDatePicker(true) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedDate.format(dateFormatter),
                        fontSize = 16.sp,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.calendar_icon),
                        contentDescription = "Select Date",
                        tint = green,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (unit == "lbs" && weight.isNotEmpty() && isValidWeight) {
            val weightInKg = weight.toFloat() * 0.453592f
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = green.copy(alpha = 0.1f))
            ) {
                Text(
                    text = "≈ ${String.format("%.1f", weightInKg)} kg",
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Medium,
                    color = green,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onConfirm,
            enabled = isValidWeight,
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
                        brush = if (isValidWeight) greenGradient else SolidColor(Color.Gray.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Log Weight",
                    fontSize = 18.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = if (isValidWeight) Color.White else Color.Gray
                )
            }
        }

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

    if (showDatePicker) {
        DatePickerDialog(
            selectedDate = selectedDate,
            onDateSelected = { date ->
                onDateChange(date)
                onShowDatePicker(false)
            },
            onDismiss = { onShowDatePicker(false) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = java.time.Instant.ofEpochMilli(millis)
                        val date = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        onDateSelected(date)
                    }
                }
            ) {
                Text(
                    "OK",
                    fontFamily = OpenSans,
                    color = colorResource(R.color.green)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    fontFamily = OpenSans,
                    color = Color.Gray
                )
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = colorResource(R.color.green),
                todayDateBorderColor = colorResource(R.color.green)
            )
        )
    }
}
