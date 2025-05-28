package com.example.nutrisaver.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans

@Composable
fun CreateRecipeScreen(navController: NavController) {
    Scaffold { innerPadding ->
        CreateRecipeContent(modifier = Modifier.padding(innerPadding), navController)
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onInfoClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Button"
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Create Recipe",
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onInfoClick) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Info Button",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateRecipeContent(modifier: Modifier = Modifier,
                        navController: NavController) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))

    var showInfoDialog by remember { mutableStateOf(false) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val foodOptions = listOf("Apple", "Banana", "Cherry", "Durian", "Eggplant") // todo: isi dengan list food stock user di database
    var selectedFood by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    val filteredOptions = foodOptions.filter {
        it.contains(query, ignoreCase = true)
    }

    Box(
        modifier = modifier.background(backgroundGradient).fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TopBar(
                onBackClick = { navController.popBackStack() },
                onInfoClick = { showInfoDialog = true }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.form_input))
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        val topY = 0f
                        val bottomY = size.height

                        drawLine(
                            color = Color.Gray,
                            start = Offset(0f, topY),
                            end = Offset(size.width, topY),
                            strokeWidth = strokeWidth
                        )
                        drawLine(
                            color = Color.Gray,
                            start = Offset(0f, bottomY - strokeWidth),
                            end = Offset(size.width, bottomY - strokeWidth),
                            strokeWidth = strokeWidth
                        )
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .height(120.dp)
                            .width(120.dp)
                            .clip(RoundedCornerShape(10.dp)),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.default_food_image),
                            contentDescription = "deskripsi gambar recipe",
                            modifier = Modifier.fillMaxSize(), // fills entire box
                            contentScale = ContentScale.Crop // or ContentScale.Cover depending on effect
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            "Recipe Name", // todo: ganti ke recipe name
                            modifier = Modifier.padding(bottom = 8.dp),
                            fontSize = 20.sp,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            "Key Ingredient",
                            fontSize = 16.sp,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            "Key Ingredient", // todo: ganti ke concat
                            fontSize = 16.sp,
                            fontFamily = OpenSans,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {
                FoodSelector(
                    selectedFood = selectedFood,
                    onClick = { showBottomSheet = true }
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Amount to Use:"
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { /* TODO: Decrease action */ },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.RemoveCircleOutline,
                            contentDescription = "Minus",
                            tint = Color.Black
                        )
                    }
                    Text(
                        "100g"
                    )
                }
                if (showBottomSheet) {
                    FoodSearchBottomSheet(
                        query = query,
                        onQueryChange = { query = it },
                        filteredOptions = filteredOptions,
                        selectedFood = selectedFood,
                        onSelectFood = {
                            selectedFood = it
                            showBottomSheet = false
                            query = ""
                        },
                        onDismiss = { showBottomSheet = false },
                        sheetState = sheetState
                    )
                }
            }
        }
    }

    if (showInfoDialog) {
        InfoDialog(onDismissRequest = { showInfoDialog = false })
    }
}

@Composable
private fun InfoDialog(
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(R.color.bg2_1),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "How to Create a Recipe",
                    fontSize = 20.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "To create a recipe, select ingredients from your current food stock.\n" +
                            "\n" +
                            "At the top of the screen, you’ll see the recipe name and its required main ingredients.\n" +
                            "\n" +
                            "Add the ingredients you want to use to the list, then press Confirm to finalize the recipe.\n" +
                            "\n" +
                            " Your selected ingredients will be deducted from your stock.",
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun FoodSelector(selectedFood: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(colorResource(R.color.form_input), RoundedCornerShape(10.dp))
            .border(0.4.dp, Color.DarkGray, RoundedCornerShape(10.dp))
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = selectedFood ?: "Select an ingredient",
            color = if (selectedFood == null) Color.Gray else Color.Black,
            fontFamily = OpenSans,
            fontSize = 16.sp
        )
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Dropdown",
            tint = Color.Black
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodSearchBottomSheet(
    query: String,
    onQueryChange: (String) -> Unit,
    filteredOptions: List<String>,
    selectedFood: String?,
    onSelectFood: (String) -> Unit,
    onDismiss: () -> Unit,
    sheetState: androidx.compose.material3.SheetState
) {
    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Column(Modifier.padding(16.dp)) {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search...",
                    fontSize = 16.sp,
                    fontFamily = OpenSans,) },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(filteredOptions) { option ->
                    val isSelected = option == selectedFood
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) colorResource(R.color.pastel_green2)
                                else Color.Transparent
                            )
                            .clickable { onSelectFood(option) }
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            fontFamily = OpenSans,
                            color = if (isSelected) colorResource(R.color.green_dark) else Color.Black,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

        }
    }
}