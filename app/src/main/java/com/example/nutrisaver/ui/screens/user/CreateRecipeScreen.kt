package com.example.nutrisaver.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nutrisaver.CustomViewModelFactory
import com.example.nutrisaver.R
import com.example.nutrisaver.data.model.FoodStock
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans
import com.example.nutrisaver.viewmodel.ChosenIngredient
import com.example.nutrisaver.viewmodel.CreateRecipeViewModel


@Composable
fun CreateRecipeScreen(
    navController: NavController,
    createRecipeViewModel: CreateRecipeViewModel = viewModel(factory = CustomViewModelFactory)
) {
    Scaffold { innerPadding ->
        CreateRecipeContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            createRecipeViewModel = createRecipeViewModel
        )
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
private fun CreateRecipeContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    createRecipeViewModel: CreateRecipeViewModel
) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    // Mengambil state dari ViewModel
    val userFoodStock by createRecipeViewModel.userFoodStock.observeAsState(emptyList())
    val chosenIngredients by createRecipeViewModel.chosenIngredients.observeAsState(emptyList())

    // State lokal untuk UI
    var showInfoDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    var selectedFoodStock by remember { mutableStateOf<FoodStock?>(null) }
    var amountToUse by remember { mutableStateOf("1") } // Simpan sebagai String untuk input

    var query by remember { mutableStateOf("") }
    val filteredOptions = userFoodStock.filter {
        it.name.contains(query, ignoreCase = true)
    }

    val openConfirmDialog = remember { mutableStateOf(false) }

    // Memuat data food stock saat layar pertama kali dibuka
    LaunchedEffect(Unit) {
        createRecipeViewModel.loadUserFoodStock()
    }

    Box(
        modifier = modifier
            .background(backgroundGradient)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 70.dp, bottom = 80.dp)
        ) {
            // Region: Recipe Name Box (Tidak diubah)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.form_input))
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        val topY = 0f
                        val bottomY = size.height
                        drawLine(color = Color.Gray, start = Offset(0f, topY), end = Offset(size.width, topY), strokeWidth = strokeWidth)
                        drawLine(color = Color.Gray, start = Offset(0f, bottomY - strokeWidth), end = Offset(size.width, bottomY - strokeWidth), strokeWidth = strokeWidth)
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.height(120.dp).width(120.dp).clip(RoundedCornerShape(10.dp))) {
                        Image(painter = painterResource(id = R.drawable.default_food_image), contentDescription = "deskripsi gambar recipe", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Recipe Name", modifier = Modifier.padding(bottom = 8.dp), fontSize = 20.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text("Key Ingredient", fontSize = 16.sp, fontFamily = OpenSans, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text("Key Ingredient", fontSize = 16.sp, fontFamily = OpenSans, textAlign = TextAlign.Center)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Region: Ingredient Selection
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {
                FoodSelector(
                    selectedFood = selectedFoodStock?.name,
                    onClick = { showBottomSheet = true }
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Amount to Use:", fontSize = 18.sp, fontFamily = OpenSans, modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // DIUBAH: Tombol minus sekarang berfungsi
                        IconButton(
                            onClick = {
                                amountToUse = (amountToUse.toIntOrNull() ?: 1).minus(1).coerceAtLeast(1).toString()
                            },
                            modifier = Modifier.size(36.dp).background(colorResource(R.color.item_1), shape = RoundedCornerShape(10.dp))
                        ) {
                            Icon(painter = painterResource(id = R.drawable.ic_remove), contentDescription = "Minus")
                        }

                        // DIUBAH: Teks kuantitas sekarang dinamis
                        Text(
                            text = "$amountToUse ${selectedFoodStock?.unit ?: ""}",
                            fontSize = 18.sp,
                            fontFamily = OpenSans
                        )

                        // DIUBAH: Tombol plus sekarang berfungsi
                        IconButton(
                            onClick = {
                                val currentStock = selectedFoodStock?.quantity ?: 1f
                                amountToUse = (amountToUse.toIntOrNull() ?: 1).plus(1).coerceAtMost(currentStock.toInt()).toString()
                            },
                            modifier = Modifier.size(36.dp).background(colorResource(R.color.item_1), shape = RoundedCornerShape(10.dp))
                        ) {
                            Icon(painter = painterResource(R.drawable.ic_add), contentDescription = "Plus")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        // DIUBAH: Menghubungkan logika ke ViewModel
                        selectedFoodStock?.let { stock ->
                            amountToUse.toFloatOrNull()?.let { amount ->
                                createRecipeViewModel.addIngredientToRecipe(stock, amount)
                            }
                        }
                    },
                    enabled = selectedFoodStock != null, // Tombol hanya aktif jika ada bahan yang dipilih
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = CircleShape,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(greenGradient, shape = CircleShape), contentAlignment = Alignment.Center) {
                        Text("Add to List", fontSize = 20.sp, fontFamily = OpenSans, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 15.dp), thickness = 2.dp)
                Text("Ingredients", fontFamily = OpenSans, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))

                // DIUBAH: Menggunakan LazyColumn untuk efisiensi
                if (chosenIngredients.isEmpty()) {
                    Text("No ingredients added yet.", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        chosenIngredients.forEach { chosen ->
                            IngredientItem(
                                chosenIngredient = chosen,
                                onIncrease = { createRecipeViewModel.updateIngredientQuantity(chosen, 1f) },
                                onDecrease = { createRecipeViewModel.updateIngredientQuantity(chosen, -1f) },
                                onRemove = { createRecipeViewModel.removeIngredientFromRecipe(chosen) }
                            )
                        }
                    }
                }
            }
        }

        // Region: TopBar dan Bottom Button (Tidak diubah)
        TopBar(
            onBackClick = { navController.popBackStack() },
            onInfoClick = { showInfoDialog = true },
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
        )
        Box(modifier = Modifier.padding(20.dp).align(Alignment.BottomCenter)) {
            Button(
                onClick = { openConfirmDialog.value = true },
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(greenGradient, shape = CircleShape), contentAlignment = Alignment.Center) {
                    Text("Create this Recipe", fontSize = 20.sp, fontFamily = OpenSans, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Region: Dialogs and Bottom Sheet
    if (showBottomSheet) {
        FoodSearchBottomSheet(
            query = query,
            onQueryChange = { query = it },
            filteredOptions = filteredOptions,
            selectedFood = selectedFoodStock,
            onSelectFood = { foodStock ->
                selectedFoodStock = foodStock
                amountToUse = "1" // Reset jumlah saat item baru dipilih
                showBottomSheet = false
                query = ""
            },
            onDismiss = { showBottomSheet = false },
            sheetState = sheetState
        )
    }
    if (showInfoDialog) {
        InfoDialog(onDismissRequest = { showInfoDialog = false })
    }
    if (openConfirmDialog.value) {
        CreateConfirmDialog(
            onDismissRequest = { openConfirmDialog.value = false },
            onConfirmRequest = {
                openConfirmDialog.value = false
                // TODO: Panggil fungsi ViewModel untuk membuat resep
            },
            icon = Icons.Default.CheckCircle
        )
    }
}

// DIUBAH: Item sekarang dinamis
@Composable
private fun IngredientItem(
    chosenIngredient: ChosenIngredient,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kolom untuk Informasi Teks
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = chosenIngredient.foodStock.name,
                    fontFamily = OpenSans,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "In stock: ${chosenIngredient.foodStock.quantity.toInt()} ${chosenIngredient.foodStock.unit}",
                    fontFamily = OpenSans,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Grup untuk Kontrol Aksi
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tombol Minus
                IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_remove),
                        contentDescription = "Minus",
                        tint = colorResource(R.color.green)
                    )
                }

                // Teks Kuantitas
                Text(
                    text = chosenIngredient.amountToUse.toInt().toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OpenSans,
                    color = Color.Black
                )

                // Tombol Plus
                IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Plus",
                        tint = colorResource(R.color.green)
                    )
                }

                // Garis pemisah vertikal
                VerticalDivider(modifier = Modifier
                    .height(24.dp)
                    .padding(horizontal = 8.dp))

                // Tombol Hapus
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Remove",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

// Tambahkan VerticalDivider Composable jika belum ada
@Composable
fun VerticalDivider(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxHeight()
            .width(1.dp)
            .background(color = Color.Gray.copy(alpha = 0.3f))
    )
}

@Composable
private fun CreateConfirmDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    icon: ImageVector,
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
            Column {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colorResource(R.color.create_confirm),
                        modifier = Modifier.size(24.dp)
                    )
                    Text("Confirm Recipe Creation", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("Are you sure you want to create this recipe?\n" +
                        "The selected ingredients will be used and removed from your stock.")
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.cancel),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onConfirmRequest,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.create_confirm),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
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

// DIUBAH: FoodSelector tetap sama, hanya untuk tampilan
@Composable
private fun FoodSelector(selectedFood: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).background(colorResource(R.color.form_input), RoundedCornerShape(10.dp)).border(0.4.dp, Color.DarkGray, RoundedCornerShape(10.dp)).padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = selectedFood ?: "Select an ingredient", color = if (selectedFood == null) Color.Gray else Color.Black, fontFamily = OpenSans, fontSize = 16.sp)
        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Color.Black)
    }
}

// DIUBAH: Bottom sheet sekarang bekerja dengan objek FoodStock
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodSearchBottomSheet(
    query: String,
    onQueryChange: (String) -> Unit,
    filteredOptions: List<FoodStock>,
    selectedFood: FoodStock?,
    onSelectFood: (FoodStock) -> Unit,
    onDismiss: () -> Unit,
    sheetState: androidx.compose.material3.SheetState
) {
    ModalBottomSheet(modifier = Modifier.fillMaxHeight(), sheetState = sheetState, onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search...", fontSize = 16.sp, fontFamily = OpenSans) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(filteredOptions) { option ->
                    val isSelected = option.id == selectedFood?.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) colorResource(R.color.pastel_green2) else Color.Transparent)
                            .clickable { onSelectFood(option) }
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = option.name,
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

