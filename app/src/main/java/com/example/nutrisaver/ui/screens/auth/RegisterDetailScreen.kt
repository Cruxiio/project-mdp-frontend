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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import com.example.nutrisaver.data.sources.remote.common.Alergen
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
    val genderOptions = listOf("male", "female")
    val (gender, onGenderOptionSelected) = remember { mutableStateOf(genderOptions[0]) }

    var showDateModal by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val dateOfBirth = datePickerState.selectedDateMillis?.let { convertMillisToDate(it) } ?: ""

    var weight by remember { mutableStateOf(0) }
    var height by remember { mutableStateOf(0) }

    val goalOptions = listOf("diet", "maintain", "gain")
    var goalExpanded by remember { mutableStateOf(false) }
    var goal by remember { mutableStateOf(goalOptions[0]) }

    val dietTypeOptions = listOf("vegan", "ketogenic", "low carbs", "strict calories", "free", "custom")
    var dietTypeExpanded by remember { mutableStateOf(false) }
    var dietType by remember { mutableStateOf(dietTypeOptions[0]) }

    var targetWeight by remember { mutableStateOf(0) }

    // Macronutrient values as Floats
    var protein by remember { mutableStateOf<String>("") }  // Default as empty string
    var carbs by remember { mutableStateOf<String>("") }    // Default as empty string
    var fat by remember { mutableStateOf<String>("") }        // Default as empty string

    // Handle diet type changes and update the macronutrient values
    fun updateMacronutrientRecommendations(dietType: String) {
        when (dietType) {
            "vegan" -> {
                protein = "27.5"
                carbs = "47.5"
                fat = "25"
            }
            "ketogenic" -> {
                protein = "20"
                carbs = "10"
                fat = "70"
            }
            "low carbs" -> {
                protein = "30"
                carbs = "30"
                fat = "40"
            }
            "strict calories" -> {
                protein = "30"
                carbs = "45"
                fat = "25"
            }
            "free" -> {
                protein = "20"
                carbs = "50"
                fat = "30"
            }
            "custom" -> {
                // Reset the values when "Custom" is selected
                protein = ""
                carbs = ""
                fat = ""
            }
        }
    }

    // Trigger the update when the selected diet type changes
    LaunchedEffect(dietType) {
        updateMacronutrientRecommendations(dietType)
    }

    // Calculate the total macronutrient value
    val proteinVal = protein.toFloatOrNull() ?: 0f
    val carbsVal = carbs.toFloatOrNull() ?: 0f
    val fatVal = fat.toFloatOrNull() ?: 0f
    val total = proteinVal + carbsVal + fatVal

    // Handle allergies and other UI components
    val alergenOption by authViewModel.alergenState.observeAsState(emptyList())
//    val allAllergen = listOf("Peanuts", "Tree Nuts", "Fish", "Shellfish", "Dairy", "Eggs", "Wheat", "Soy", "Gluten")
    var allergyExpanded by remember { mutableStateOf(false) }
//    var selectedAllergy by remember { mutableStateOf(allAllergen[0]) }
//    var userAllergies by remember { mutableStateOf(listOf<String>()) }
    var selectedAllergy by remember { mutableStateOf<Alergen?>(null) }
    var userAllergies by remember { mutableStateOf<List<Alergen>>(listOf<Alergen>()) }

    // Local callback function that updates userAllergies state
    val onAllergyListChanged: (List<Alergen>) -> Unit = { newList ->
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

    // Page navigation logic
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                navController.navigate("user") {
                    popUpTo("auth") { inclusive = true }
                    launchSingleTop = true
                }
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).verticalScroll(rememberScrollState())) {
            Text("We Need Your Information", fontSize = 23.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold, color = green, modifier = Modifier.padding(top = 40.dp))
            Text("Please fill out the following form to help personalize your nutrition plan.", fontSize = 16.sp, fontFamily = OpenSans)

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 2.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Personal Information Section
            Text("Measurements", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Basic body measurements, gender and age help us calculate your recommended daily intake.", fontSize = 14.sp, fontFamily = OpenSans, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(10.dp))
            Text("Name", modifier = Modifier.padding(bottom = 4.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colorResource(R.color.black),
                    unfocusedTextColor = colorResource(R.color.black),
                    focusedContainerColor = colorResource(R.color.form_input),
                    unfocusedContainerColor = colorResource(R.color.form_input)
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Gender:", modifier = Modifier.padding(bottom = 4.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold)

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

            // Date Picker and Other Fields
            Spacer(modifier = Modifier.height(10.dp))

            Text("Date of Birth", modifier = Modifier.padding(bottom = 4.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = if (dateOfBirth == "") "Select Date" else dateOfBirth,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().clickable { showDateModal = true },
                trailingIcon = {
                    IconButton(onClick = { showDateModal = true }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select date")
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
                DatePickerDialog(onDismissRequest = { showDateModal = false }, confirmButton = { TextButton(onClick = { showDateModal = false }) { Text("OK") } }, dismissButton = { TextButton(onClick = { showDateModal = false }) { Text("Cancel") } }) {
                    DatePicker(state = datePickerState)
                }
            }

            // Weight and Height Fields
            Spacer(modifier = Modifier.height(10.dp))

            Text("Weight", modifier = Modifier.padding(bottom = 4.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = weight.toString(),
                    onValueChange = { input -> weight = input.filter { it.isDigit() }.toIntOrNull() ?: 0 },
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
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
            Text("Height", modifier = Modifier.padding(bottom = 4.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold)
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

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 2.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Text("Goal & Diet Type", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                "Choose your goal and diet type so we can tailor your nutrition plan accordingly.",
                fontSize = 14.sp,
                fontFamily = OpenSans,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text("Select Goal", modifier = Modifier.padding(bottom = 4.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold)
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

            Text("Diet Type", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            ExposedDropdownMenuBox(
                expanded = dietTypeExpanded,
                onExpandedChange = { dietTypeExpanded = !dietTypeExpanded }
            ) {
                OutlinedTextField(
                    value = dietType,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dietTypeExpanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.black),
                        unfocusedTextColor = colorResource(R.color.black),
                        focusedContainerColor = colorResource(R.color.bg),
                        unfocusedContainerColor = colorResource(R.color.bg)
                    )
                )

                ExposedDropdownMenu(
                    expanded = dietTypeExpanded,
                    onDismissRequest = { dietTypeExpanded = false }
                ) {
                    dietTypeOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                dietType = selectionOption
                                dietTypeExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("Target Weight", modifier = Modifier.padding(bottom = 4.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(thickness = 2.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Text("Macronutrient Ratio", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                "Set your desired percentage split between protein, carbs, and fats. Total must equal 100%.",
                fontSize = 14.sp,
                fontFamily = OpenSans,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(10.dp))
            val isDietTypeCustom = dietType == "custom"

            RatioInputField("Protein", protein.toString(), onValueChange = { if (isDietTypeCustom) protein = it }, enabled = isDietTypeCustom)
            RatioInputField("Carbs", carbs.toString(), onValueChange = { if (isDietTypeCustom) carbs = it }, enabled = isDietTypeCustom)
            RatioInputField("Fat", fat.toString(), onValueChange = { if (isDietTypeCustom) fat = it}, enabled = isDietTypeCustom)

            Spacer(Modifier.height(10.dp))
            Text(
                text = "Total: $total%",
                color = if (total.toFloat() == 100f) colorResource(R.color.green) else colorResource(R.color.red),
                fontFamily = OpenSans,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            if (total.toFloat() != 100f) {
                Text("Total must equal 100%", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

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
                        value = selectedAllergy?.name ?: "Select allergy",
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
                        alergenOption.forEach { allergy ->
                            DropdownMenuItem(
                                text = { Text(allergy.name) },
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
                        if (selectedAllergy != null && selectedAllergy !in userAllergies) {
                            val updatedList: List<Alergen> = userAllergies + selectedAllergy!!
                            onAllergyListChanged(updatedList)
                            selectedAllergy = null
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
                                    allergy.name,
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
                    authViewModel.signup(RegisterDetailInp(name, gender, dateOfBirth, weight, height, goal, dietType, targetWeight, protein.toFloat(), carbs.toFloat(), fat.toFloat(), userAllergies))
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

            Spacer(Modifier.height(30.dp))
        }
    }
}

private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Composable
fun RatioInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        trailingIcon = { Text("%") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colorResource(R.color.black),
            unfocusedTextColor = colorResource(R.color.black),
            focusedContainerColor = colorResource(R.color.form_input),
            unfocusedContainerColor = colorResource(R.color.form_input)
        )
    )
}
