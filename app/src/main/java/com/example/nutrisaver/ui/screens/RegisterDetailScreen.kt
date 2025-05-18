package com.example.nutrisaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.theme.OpenSans
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterDetailScreen(
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    val genderOptions = listOf("Male", "Female")
    val (gender, onOptionSelected) = remember { mutableStateOf(genderOptions[0]) }
    var dateofbirth by remember { mutableStateOf<Long?>(null) }
    var showDateModal by remember { mutableStateOf(false) }
    var weight by remember { mutableStateOf(0) }
    var height by remember { mutableStateOf(0) }

    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val item1 = colorResource(id = R.color.item_1)
    val item2 = colorResource(id = R.color.item_2)
    val itemGradient = Brush.horizontalGradient(listOf(item1, item2))

    Box(modifier = Modifier
        .fillMaxSize()
        .background(backgroundGradient),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "We Need Your Information",
                fontSize = 23.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = green,
                modifier = Modifier.padding(top = 40.dp)
            )
            Text(
                "Please enter your physical measurements and your health goal to help personalize your nutrition plan.",
                fontSize = 14.sp,
                fontFamily = OpenSans
            )
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                thickness = 2.dp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Name",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = name,
                onValueChange = { name = it },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = colorResource(R.color.black),
                    unfocusedTextColor =  colorResource(R.color.black), focusedContainerColor = colorResource(
                        R.color.bg),
                    unfocusedContainerColor = colorResource(R.color.bg)
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Gender: ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .selectableGroup(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    genderOptions.forEach { text ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .selectable(
                                    selected = (text == gender),
                                    onClick = { onOptionSelected(text) },
                                    role = Role.RadioButton
                                )
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = (text == gender),
                                onClick = null
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Date of Birth",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = dateofbirth?.let { convertMillisToDate(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDateModal = true },
                label = { Text("DOB") },
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = colorResource(R.color.bg),
                    unfocusedContainerColor = colorResource(R.color.bg)
                )
            )
        }
    }

    if (showDateModal) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dateofbirth
        )

        DatePickerDialog(
            onDismissRequest = { showDateModal = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateofbirth = datePickerState.selectedDateMillis
                        showDateModal = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateModal = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

}


fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}
