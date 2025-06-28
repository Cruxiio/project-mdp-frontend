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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import com.example.nutrisaver.data.model.Allergen
import com.example.nutrisaver.viewmodel.UserViewModel

@Composable
fun EditInformationScreen(navController: NavController, userViewModel: UserViewModel) {
    Scaffold(
        topBar = { TopBar(onBackClick = { navController.popBackStack() }) }
    ) { innerPadding ->
        EditInformationContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            userViewModel = userViewModel
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
            .padding(
                top = 32.dp,
                bottom = 12.dp,
                start = 16.dp,
                end = 16.dp
            )
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

// helper function to convert date string to millis
private fun convertDateToMillis(dateString: String): Long? {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return try {
        formatter.parse(dateString)?.time
    } catch (e: Exception) {
        null // Return null if parsing fails
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun EditInformationContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel
) {
    val userProfile by userViewModel.userProfile.observeAsState()

    var gender by remember { mutableStateOf("Male") }
    var showDateModal by remember { mutableStateOf(false) }

    val initialDateMillis = remember(userProfile?.dateOfBirth) {
        userProfile?.dateOfBirth?.let { dateString ->
            convertDateToMillis(dateString)
        } ?: System.currentTimeMillis()
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)
    var dateOfBirth by remember { mutableStateOf(userProfile?.dateOfBirth ?: "") }


    var weight by remember { mutableStateOf(0) }
    var height by remember { mutableStateOf(0) }

    val goalOptions = listOf("Diet", "Maintain Weight", "Gain Muscle")
    var goalExpanded by remember { mutableStateOf(false) }
    var goal by remember { mutableStateOf(goalOptions[0]) }

    val dietTypeOptions = listOf("vegan", "ketogenic", "low carbs", "strict calories", "free", "custom")
    var dietTypeExpanded by remember { mutableStateOf(false) }
    var dietType by remember { mutableStateOf(dietTypeOptions[0]) }

    var targetWeight by remember { mutableStateOf(0) }

    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }

    val allAvailableAllergens by userViewModel.allergenState.observeAsState(emptyList())
    var allergyExpanded by remember { mutableStateOf(false) }
    // This selectedAllergy is for the dropdown selection itself
    var selectedAllergyFromDropdown by remember { mutableStateOf<Allergen?>(null) }
    // This holds the allergies the user HAS selected
    var userAllergies by remember { mutableStateOf(listOf<Allergen>()) }


    // Fetch user profile and all allergens when the composable enters the composition
    LaunchedEffect(Unit) {
        userViewModel.fetchUserProfile()
        userViewModel.fetchAllergens() // This populates allAvailableAllergens
    }

    // Populate fields when userProfile is available
    LaunchedEffect(userProfile) {
        userProfile?.let { user ->
            gender = user.gender.replaceFirstChar { it.uppercase(Locale.getDefault()) }
            dateOfBirth = user.dateOfBirth
            weight = user.weight
            height = user.height
            goal = user.goal.replaceFirstChar { it.uppercase(Locale.getDefault()) }
            dietType = user.dietType
            targetWeight = user.targetWeight
            protein = user.proteinRatio.toString()
            carbs = user.carbsRatio.toString()
            fat = user.fatRatio.toString()
            userAllergies = user.allergen
        }
    }

    // Initialize selectedAllergyFromDropdown based on allAvailableAllergens
    // This ensures the dropdown always shows an available option.
    // userAllergies is NOT used for the initial selection of the dropdown itself.
    LaunchedEffect(allAvailableAllergens) {
        selectedAllergyFromDropdown = allAvailableAllergens.firstOrNull()
    }

    // Update `dateOfBirth` whenever the `datePickerState`'s selection changes
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            dateOfBirth = convertMillisToDate(it)
        }
    }

    // Handle diet type changes and update the macronutrient values
    fun updateMacronutrientRecommendations(newDietType: String) {
        when (newDietType) {
            "vegan" -> {
                protein = "27.5"
                carbs = "47.5"
                fat = "25.0"
            }
            "ketogenic" -> {
                protein = "20.0"
                carbs = "10.0"
                fat = "70.0"
            }
            "low carbs" -> {
                protein = "30.0"
                carbs = "30.0"
                fat = "40.0"
            }
            "strict calories" -> {
                protein = "30.0"
                carbs = "45.0"
                fat = "25.0"
            }
            "free" -> {
                protein = "20.0"
                carbs = "50.0"
                fat = "30.0"
            }
            "custom" -> {
                // Keep current values if custom is selected, or reset if they are default
                if (protein == "0.0" && carbs == "0.0" && fat == "0.0") {
                    protein = ""
                    carbs = ""
                    fat = ""
                }
            }
        }
    }

    // Trigger the update when the selected diet type changes
    LaunchedEffect(dietType) {
        updateMacronutrientRecommendations(dietType)
    }

    val proteinVal = protein.toFloatOrNull() ?: 0f
    val carbsVal = carbs.toFloatOrNull() ?: 0f
    val fatVal = fat.toFloatOrNull() ?: 0f
    val total = proteinVal + carbsVal + fatVal

    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))
    val item1 = colorResource(id = R.color.item_1)
    val item2 = colorResource(id = R.color.item_2)
    val itemGradient = Brush.verticalGradient(listOf(item1, item2))

    Box(modifier = modifier
        .fillMaxSize()
        .background(backgroundGradient)) {
        Column( modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))
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
                val genderOptions = listOf("Male", "Female")
                Row(modifier = Modifier.selectableGroup()) {
                    genderOptions.forEach { text ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .selectable(
                                    selected = (text == gender),
                                    onClick = { gender = text },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 8.dp)
                        ) {
                            RadioButton(
                                selected = (text == gender),
                                onClick = { gender = text },
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
                value = if (dateOfBirth.isEmpty()) "Select Date" else dateOfBirth,
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
                    focusedTextColor = if (dateOfBirth.isEmpty()) Color.Gray else Color.Black,
                    unfocusedTextColor = if (dateOfBirth.isEmpty()) Color.Gray else Color.Black,
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
                    value = goal.replaceFirstChar { it.uppercase(Locale.getDefault()) },
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
                "Diet Type",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            ExposedDropdownMenuBox(
                expanded = dietTypeExpanded,
                onExpandedChange = { dietTypeExpanded = !dietTypeExpanded }
            ) {
                OutlinedTextField(
                    value = dietType.replaceFirstChar { it.uppercase(Locale.getDefault()) },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dietTypeExpanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.black),
                        unfocusedTextColor = colorResource(R.color.black),
                        focusedContainerColor = colorResource(R.color.form_input),
                        unfocusedContainerColor = colorResource(R.color.form_input)
                    )
                )

                ExposedDropdownMenu(
                    expanded = dietTypeExpanded,
                    onDismissRequest = { dietTypeExpanded = false }
                ) {
                    dietTypeOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.replaceFirstChar { it.uppercase(Locale.getDefault()) }) },
                            onClick = {
                                dietType = selectionOption
                                dietTypeExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "Macronutrient Ratio",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            val isDietTypeCustom = dietType == "custom"
            RatioInputField("Protein", protein, onValueChange = { if (isDietTypeCustom) protein = it }, enabled = isDietTypeCustom)
            RatioInputField("Carbs", carbs, onValueChange = { if (isDietTypeCustom) carbs = it }, enabled = isDietTypeCustom)
            RatioInputField("Fat", fat, onValueChange = { if (isDietTypeCustom) fat = it}, enabled = isDietTypeCustom)
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Total: ${String.format("%.1f", total)}%",
                color = if (total.toFloat() == 100f) colorResource(R.color.green) else colorResource(R.color.red),
                fontFamily = OpenSans,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            if (total.toFloat() != 100f) {
                Text(
                    "Total must equal 100%",
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Allergies",
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
                        value = selectedAllergyFromDropdown?.name ?: "Select allergy",
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
                        // Iterate through all available allergens
                        allAvailableAllergens.forEach { allergy ->
                            DropdownMenuItem(
                                text = { Text(allergy.name) },
                                onClick = {
                                    selectedAllergyFromDropdown = allergy
                                    allergyExpanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        // Add the selected allergy if it's not null and not already in the list
                        selectedAllergyFromDropdown?.let { allergyToAdd ->
                            if (allergyToAdd !in userAllergies) {
                                userAllergies = userAllergies + allergyToAdd
                                selectedAllergyFromDropdown = null // Clear selection after adding
                            }
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
                                            userAllergies = userAllergies - allergy
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
                    // Collect all the updated information
                    val updatedUser = userProfile?.copy(
                        gender = gender.lowercase(Locale.getDefault()),
                        dateOfBirth = dateOfBirth,
                        weight = weight,
                        height = height,
                        goal = goal.lowercase(Locale.getDefault()),
                        dietType = dietType,
                        targetWeight = targetWeight,
                        proteinRatio = protein.toFloatOrNull() ?: 0f,
                        carbsRatio = carbs.toFloatOrNull() ?: 0f,
                        fatRatio = fat.toFloatOrNull() ?: 0f,
                        allergen = userAllergies
                    )
                    // Call ViewModel to update user information
                    updatedUser?.let {
                        userViewModel.updateUserInformation(it)
                    }
                    navController.popBackStack() // Navigate back to profile
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
                        text = "Save Information",
                        fontSize = 20.sp,
                        fontFamily = OpenSans,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}