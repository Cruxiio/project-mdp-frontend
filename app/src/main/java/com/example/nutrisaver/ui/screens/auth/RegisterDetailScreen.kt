package com.example.nutrisaver.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.nutrisaver.AuthState
import com.example.nutrisaver.AuthViewModel
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.screens.auth.authDTO.RegisterDetailInp
import com.example.nutrisaver.ui.theme.OpenSans
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegisterDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    innerPadding: PaddingValues = PaddingValues(0.dp)
) {
    var name by remember { mutableStateOf("") }
    val genderOptions = listOf("Male", "Female")
    val (gender, onOptionSelected) = remember { mutableStateOf(genderOptions[0]) }

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

    // nanti diganti jadi allergen yang ada di database
    val allAllergen = listOf("Peanuts", "Shellfish", "Dairy", "Eggs", "Wheat", "Soy")
    var allergyExpanded by remember { mutableStateOf(false) }
    var selectedAllergy by remember { mutableStateOf(allAllergen[0]) }
    var userAllergies by remember { mutableStateOf(listOf<String>()) }
    // Local callback function that updates userAllergies state
    val onAllergyListChanged: (List<String>) -> Unit = { newList ->
        userAllergies = newList
    }

    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val item1 = colorResource(id = R.color.item_1)
    val item2 = colorResource(id = R.color.item_2)
    val itemGradient = Brush.verticalGradient(listOf(item1, item2))

    // pindah halaman
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> {
                navController.navigate("user") {
                    popUpTo("auth") { inclusive = true } // ini biar gk bisa balik ke login screen
                    launchSingleTop = true
                }
            }
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
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
                "Please fill out the following form to help personalize your nutrition plan.",
                fontSize = 16.sp,
                fontFamily = OpenSans
            )
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 2.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Text("Measurements", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                "Basic body measurements, gender and age help us calculate your recommended daily intake.",
                fontSize = 14.sp,
                fontFamily = OpenSans,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text("Name", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colorResource(R.color.black),
                    unfocusedTextColor = colorResource(R.color.black),
                    focusedContainerColor = colorResource(R.color.bg),
                    unfocusedContainerColor = colorResource(R.color.bg)
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Gender:", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                Row(modifier = Modifier.selectableGroup()) {
                    genderOptions.forEach { text ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .selectable(
                                    selected = (text == gender),
                                    onClick = { onOptionSelected(text) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 8.dp)
                        ) {
                            RadioButton(
                                selected = (text == gender),
                                onClick = null // Handled by parent Row
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

            Text("Date of Birth", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = dateOfBirth,
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
                    focusedTextColor = colorResource(R.color.black),
                    unfocusedTextColor = colorResource(R.color.black),
                    focusedContainerColor = colorResource(R.color.bg),
                    unfocusedContainerColor = colorResource(R.color.bg)
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

            Text("Weight", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                        focusedContainerColor = colorResource(R.color.bg),
                        unfocusedContainerColor = colorResource(R.color.bg)
                    )
                )
                Text("kg", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("Height", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                        focusedContainerColor = colorResource(R.color.bg),
                        unfocusedContainerColor = colorResource(R.color.bg)
                    )
                )
                Text("cm", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 2.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Text("Goal", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                "Choose your goal so we can tailor your nutrition plan accordingly.",
                fontSize = 14.sp,
                fontFamily = OpenSans,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text("Select Goal", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                        focusedContainerColor = colorResource(R.color.bg),
                        unfocusedContainerColor = colorResource(R.color.bg)
                    )
                )

                ExposedDropdownMenu(
                    expanded = goalExpanded,
                    onDismissRequest = { goalExpanded = false }
                ) {
                    goalOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                goal = selectionOption
                                goalExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            Text("Target Weight", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                        focusedContainerColor = colorResource(R.color.bg),
                        unfocusedContainerColor = colorResource(R.color.bg)
                    )
                )
                Text("kg", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 2.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Text("Macronutient Ratio", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                "Set your desired percentage split between protein, carbs, and fats. Total must equal 100%.",
                fontSize = 14.sp,
                fontFamily = OpenSans,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(10.dp))
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
                Text("Total must equal 100%", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 2.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Text("Allegies", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                "Select any allergies you have from the dropdown below. You can add multiple allergies.",
                fontSize = 14.sp,
                fontFamily = OpenSans,
                color = Color.DarkGray
            )
            Spacer(Modifier.height(10.dp))
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
                            focusedContainerColor = colorResource(R.color.bg),
                            unfocusedContainerColor = colorResource(R.color.bg)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = allergyExpanded,
                        onDismissRequest = { allergyExpanded = false }
                    ) {
                        allAllergen.forEach { allergy ->
                            DropdownMenuItem(
                                text = { Text(allergy) },
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
                                .padding(1.dp) // Thin padding to simulate chip border
                        ) {
                            AssistChip(
                                onClick = { /* gk perlu diisi */ },
                                label = { Text(
                                    allergy,
                                    color = Color.White,
                                    fontFamily = OpenSans,
                                    fontWeight = FontWeight.Bold
                                ) },
                                trailingIcon = {
                                    Icon(
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

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(thickness = 2.dp)
            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    // REGISTRASI USER KE DATABASE
                    authViewModel.signup(RegisterDetailInp(name,gender,dateOfBirth,weight,height,goal,targetWeight,proteinVal,carbsVal,fatVal,userAllergies))
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
                        text = "Submit",
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

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Composable
fun RatioInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.all { char -> char.isDigit() } && it.length <= 3) {
                onValueChange(it)
            }
        },
        label = { Text(label) },
        trailingIcon = { Text("%") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colorResource(R.color.black),
            unfocusedTextColor = colorResource(R.color.black),
            focusedContainerColor = colorResource(R.color.bg),
            unfocusedContainerColor = colorResource(R.color.bg)
        )
    )
}
