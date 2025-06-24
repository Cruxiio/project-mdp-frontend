package com.example.nutrisaver.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.screens.auth.RatioInputField
import com.example.nutrisaver.ui.theme.OpenSans
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditInformationScreen(navController: NavController) {
    Scaffold { innerPadding ->
        EditInformationContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }

}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(greenGradient)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back Button",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Edit Your Information",
                fontSize = 20.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// helper function to convert millis to date
private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun EditInformationContent(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    // todo: ganti semua value ini dari value user saat ini
    val genderOptions = listOf("Male", "Female")
    val (gender, onGenderOptionSelected) = remember { mutableStateOf(genderOptions[0]) }

    var showDateModal by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val dateOfBirth = datePickerState.selectedDateMillis?.let { convertMillisToDate(it) } ?: ""

    var weight by remember { mutableStateOf(0) }
    var height by remember { mutableStateOf(0) }

    val goalOptions = listOf("Diet", "Maintain Weight", "Gain Muscle")
    var goalExpanded by remember { mutableStateOf(false) }
    var goal by remember { mutableStateOf(goalOptions[0]) }

    var targetWeight by remember { mutableStateOf(0) }

    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    val proteinVal = protein.toIntOrNull() ?: 0
    val carbsVal = carbs.toIntOrNull() ?: 0
    val fatVal = fat.toIntOrNull() ?: 0
    val total = proteinVal + carbsVal + fatVal

    // Todo: nanti diganti jadi allergen yang ada di database
    val allAllergen = listOf("Peanuts", "Shellfish", "Dairy", "Eggs", "Wheat", "Soy")
    var allergyExpanded by remember { mutableStateOf(false) }
    var selectedAllergy by remember { mutableStateOf(allAllergen[0]) }
    var userAllergies by remember { mutableStateOf(listOf<String>()) }
    // Local callback function that updates userAllergies state
    val onAllergyListChanged: (List<String>) -> Unit = { newList ->
        userAllergies = newList
    }

    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))
    val item1 = colorResource(id = R.color.item_1)
    val item2 = colorResource(id = R.color.item_2)
    val itemGradient = Brush.verticalGradient(listOf(item1, item2))

    Column(modifier = modifier
        .fillMaxSize()
        .background(backgroundGradient)) {
        TopBar(onBackClick = { navController.popBackStack() })
        Spacer(modifier = Modifier.height(16.dp))

        Column( modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Gender:",
                    modifier = Modifier.padding(bottom = 4.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(modifier = Modifier.selectableGroup()) {
                    genderOptions.forEach { text ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .selectable(
                                    selected = (text == gender),
                                    onClick = { onGenderOptionSelected(text) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 8.dp)
                        ) {
                            RadioButton(
                                selected = (text == gender),
                                onClick = { onGenderOptionSelected(text) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colorResource(R.color.green),
                                )
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
                modifier = Modifier.padding(bottom = 4.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = if (dateOfBirth == "") "Select Date" else dateOfBirth,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDateModal = true },
                trailingIcon = {
                    IconButton(onClick = { showDateModal = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (dateOfBirth == "") Color.Gray else Color.Black,
                    unfocusedTextColor = if (dateOfBirth == "") Color.Gray else Color.Black,
                    focusedContainerColor = colorResource(R.color.form_input),
                    unfocusedContainerColor = colorResource(R.color.form_input)
                )
            )

            if (showDateModal) {
                DatePickerDialog(
                    onDismissRequest = { showDateModal = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showDateModal = false
                        }) {
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

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Weight",
                modifier = Modifier.padding(bottom = 4.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = weight.toString(),
                    onValueChange = { input ->
                        weight = input.filter { it.isDigit() }.toIntOrNull() ?: 0
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.black),
                        unfocusedTextColor = colorResource(R.color.black),
                        focusedContainerColor = colorResource(R.color.form_input),
                        unfocusedContainerColor = colorResource(R.color.form_input)
                    )
                )
                Text("kg", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Height",
                modifier = Modifier.padding(bottom = 4.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = height.toString(),
                    onValueChange = { input ->
                        height = input.filter { it.isDigit() }.toIntOrNull() ?: 0
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.black),
                        unfocusedTextColor = colorResource(R.color.black),
                        focusedContainerColor = colorResource(R.color.form_input),
                        unfocusedContainerColor = colorResource(R.color.form_input)
                    )
                )
                Text("cm", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Select Goal",
                modifier = Modifier.padding(bottom = 4.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            ExposedDropdownMenuBox(
                expanded = goalExpanded,
                onExpandedChange = { goalExpanded = !goalExpanded }
            ) {
                OutlinedTextField(
                    value = goal,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.black),
                        unfocusedTextColor = colorResource(R.color.black),
                        focusedContainerColor = colorResource(R.color.form_input),
                        unfocusedContainerColor = colorResource(R.color.form_input)
                    )
                )

                ExposedDropdownMenu(
                    expanded = goalExpanded,
                    onDismissRequest = { goalExpanded = false }
                ) {
                    goalOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { androidx.compose.material.Text(selectionOption) },
                            onClick = {
                                goal = selectionOption
                                goalExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Target Weight",
                modifier = Modifier.padding(bottom = 4.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = targetWeight.toString(),
                    onValueChange = { input ->
                        targetWeight = input.filter { it.isDigit() }.toIntOrNull() ?: 0
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.black),
                        unfocusedTextColor = colorResource(R.color.black),
                        focusedContainerColor = colorResource(R.color.form_input),
                        unfocusedContainerColor = colorResource(R.color.form_input)
                    )
                )
                Text("kg", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Macronutient Ratio",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            RatioInputField("Protein", protein, onValueChange = { protein = it })
            RatioInputField("Carbs", carbs, onValueChange = { carbs = it })
            RatioInputField("Fat", fat, onValueChange = { fat = it })
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Total: $total%",
                color = if (total == 100) colorResource(R.color.green) else colorResource(R.color.red),
                fontFamily = OpenSans,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            if (total != 100) {
                Text(
                    "Total must equal 100%",
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Allegies",
                modifier = Modifier.padding(bottom = 5.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ExposedDropdownMenuBox(
                    expanded = allergyExpanded,
                    onExpandedChange = { allergyExpanded = !allergyExpanded }
                ) {
                    OutlinedTextField(
                        value = if (selectedAllergy.isNotEmpty()) selectedAllergy else "Select allergy",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = allergyExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colorResource(R.color.black),
                            unfocusedTextColor = colorResource(R.color.black),
                            focusedContainerColor = colorResource(R.color.form_input),
                            unfocusedContainerColor = colorResource(R.color.form_input)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = allergyExpanded,
                        onDismissRequest = { allergyExpanded = false }
                    ) {
                        allAllergen.forEach { allergy ->
                            DropdownMenuItem(
                                text = { androidx.compose.material.Text(allergy) },
                                onClick = {
                                    selectedAllergy = allergy
                                    allergyExpanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (selectedAllergy.isNotEmpty() && selectedAllergy !in userAllergies) {
                            val updatedList = userAllergies + selectedAllergy
                            onAllergyListChanged(updatedList)
                            selectedAllergy = ""
                        }
                    },
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .height(50.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(greenGradient, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Add",
                            fontFamily = OpenSans,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (userAllergies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    userAllergies.forEach { allergy ->
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = itemGradient,
                                    shape = CircleShape,
                                )
                                .padding(1.dp)
                        ) {
                            AssistChip(
                                onClick = { /* gk perlu diisi */ },
                                label = { androidx.compose.material.Text(
                                    allergy,
                                    color = Color.White,
                                    fontFamily = OpenSans,
                                    fontWeight = FontWeight.Bold
                                ) },
                                trailingIcon = {
                                    androidx.compose.material.Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        modifier = Modifier.clickable {
                                            val updatedList = userAllergies - allergy
                                            onAllergyListChanged(updatedList)
                                        },
                                        tint = Color.White
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color.Transparent,
                                    labelColor = Color.White,
                                    leadingIconContentColor = Color.White,
                                    trailingIconContentColor = Color.White
                                ),
                                border = null
                            )
                        }

                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(thickness = 2.dp)
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    // TODO: Simpan editan user ke database
                },
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(greenGradient, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Edit Information",
                        fontSize = 20.sp,
                        fontFamily = OpenSans,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}