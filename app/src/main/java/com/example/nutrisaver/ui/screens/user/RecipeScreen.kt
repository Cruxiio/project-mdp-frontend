package com.example.nutrisaver.ui.screens.user

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nutrisaver.R
import com.example.nutrisaver.data.model.RecipePlain
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans
import com.example.nutrisaver.viewmodel.AuthState
import com.example.nutrisaver.viewmodel.RecipeStatusState
import com.example.nutrisaver.viewmodel.RecipeViewModel

@Composable
fun RecipeScreen(navController: NavController, recipeViewModel: RecipeViewModel) {
    Scaffold(
        bottomBar = {
            UserBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        RecipeContent(modifier = Modifier.padding(innerPadding), navController,recipeViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    recipeViewModel: RecipeViewModel
) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val greenGradient1 = Brush.horizontalGradient(
        listOf(
            colorResource(R.color.green),
            colorResource(R.color.green_teal_dark)
        )
    )

    // Sample data - todo: replace with actual data
//    val allRecipes = remember {
//        (1..100).map { index ->
//            RecipeDummy(
//                id = index,
//                name = "Recipe $index"
//            )
//        }
//    }

    // data recipe dari backend
    val allRecipes by recipeViewModel.listRecipeState.observeAsState(emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf(1) }
    var showPageSelector by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("popularity") }

    // RecipeState logic
    val recipeState = recipeViewModel.recipeStatusState.observeAsState(RecipeStatusState.Idle)
    val context = LocalContext.current
    val totalPages = 10
    val itemsPerPage = 6
    var currentPageItems: List<RecipePlain> = listOf()

    // buat update tampilan recipe sesuai jenis filter dan page
    LaunchedEffect(selectedFilter, currentPage) {
        recipeViewModel.getAllRecipe("", selectedFilter, currentPage-1, itemsPerPage)
    }


    LaunchedEffect(recipeState.value) {
        when (recipeState.value) {
            is RecipeStatusState.Success -> {
//                Toast.makeText(context, "Berhasil load!", Toast.LENGTH_SHORT).show()
                Log.d("isi recipe", "${allRecipes}")
            }
            is RecipeStatusState.Error -> {
                Toast.makeText(context, (recipeState.value as RecipeStatusState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    top = 32.dp,
                    start = 32.dp,
                    end = 32.dp,
                    bottom = 16.dp
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Enhanced Title
                Text(
                    text = "Recipes",
                    fontSize = 28.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    letterSpacing = (-0.5).sp
                )

                // Sleek Favorite Button
                Surface(
                    onClick = { navController.navigate("favoriterecipe") },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite Recipes",
                            tint = colorResource(R.color.green),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "My Favorites",
                            fontSize = 14.sp,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(R.color.green)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Search Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .weight(3f)
                        .background(colorResource(R.color.form_input), CircleShape),
                    placeholder = {
                        Text(
                            text = "Search",
                            fontFamily = OpenSans,
                            color = Color.Gray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(R.color.green),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                    ),
                    singleLine = true
                )
                Button(
                    onClick = {
                        navController.navigate("searchresults/${searchQuery}")
                    },
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(greenGradient1, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Search",
                            fontSize = 16.sp,
                            fontFamily = OpenSans,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                "Filter Recommended Recipes By:",
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        colorResource(R.color.green),
                        RoundedCornerShape(8.dp)
                    )
                    .clip(RoundedCornerShape(8.dp)),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                FilterButton(
                    modifier = Modifier.weight(1f),
                    text = "Popularity",
                    isSelected = selectedFilter == "popularity",
                    onClick = { selectedFilter = "popularity"; currentPage = 1 }
                )
                FilterButton(
                    modifier = Modifier.weight(1f),
                    text = "Foodstock",
                    isSelected = selectedFilter == "foodstock",
                    onClick = { selectedFilter = "foodstock"; currentPage = 1 }
                )
                FilterButton(
                    modifier = Modifier.weight(1f),
                    text = "Nutrition",
                    isSelected = selectedFilter == "nutrition",
                    onClick = { selectedFilter = "nutrition"; currentPage = 1 }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (recipeState.value) {
                is RecipeStatusState.Success -> {
                    Log.d("isi all recipes sblm tampil", "${allRecipes}")
                    RecipeGrid(
                        recipes = allRecipes,
                        navController = navController
                    )
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(12.dp))

            PaginationControls(
                currentPage = currentPage,
                totalPages = totalPages, // todo ganti sesuai total pages yang ada
                onPreviousClick = {
                    if (currentPage > 1) currentPage--
                },
                onNextClick = {
                    if (currentPage < totalPages) currentPage++
                },
                onPageClick = {
                    showPageSelector = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showPageSelector) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showPageSelector = false },
            sheetState = bottomSheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            PageSelectorBottomSheet(
                currentPage = currentPage,
                totalPages = totalPages,
                onPageSelected = { page ->
                    currentPage = page
                },
                onDone = {
                    showPageSelector = false
                }
            )
        }
    }
}

@Composable
fun RecipeGrid(
    recipes: List<RecipePlain>, // todo: ganti ke tipe data aslinya
    navController: NavController
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // row pertama
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            recipes.getOrNull(0)?.let { recipe ->
                RecipeGridItem(
                    recipe = recipe,
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            } ?: Spacer(modifier = Modifier.weight(1f))

            recipes.getOrNull(1)?.let { recipe ->
                RecipeGridItem(
                    recipe = recipe,
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            } ?: Spacer(modifier = Modifier.weight(1f))
        }
        // row kedua
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            recipes.getOrNull(2)?.let { recipe ->
                RecipeGridItem(
                    recipe = recipe,
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            } ?: Spacer(modifier = Modifier.weight(1f))

            recipes.getOrNull(3)?.let { recipe ->
                RecipeGridItem(
                    recipe = recipe,
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            } ?: Spacer(modifier = Modifier.weight(1f))
        }
        // row ketiga
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            recipes.getOrNull(4)?.let { recipe ->
                RecipeGridItem(
                    recipe = recipe,
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            } ?: Spacer(modifier = Modifier.weight(1f))

            recipes.getOrNull(5)?.let { recipe ->
                RecipeGridItem(
                    recipe = recipe,
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            } ?: Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun FilterButton(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))
    Button(
        onClick = { onClick() },
        modifier = modifier
            .height(45.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (isSelected) greenGradient else SolidColor(Color.White),
                    shape = RoundedCornerShape(0.dp)
                )
                .clip(RoundedCornerShape(0.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onPageClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (currentPage > 1) colorResource(R.color.green)
                    else Color.Gray.copy(alpha = 0.3f)
                )
                .clickable(enabled = currentPage > 1) { onPreviousClick() }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Previous",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(
                    1.dp,
                    Color.Gray.copy(alpha = 0.3f),
                    RoundedCornerShape(8.dp)
                )
                .clickable { onPageClick() }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = currentPage.toString(),
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select page",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (currentPage < totalPages) colorResource(R.color.green)
                    else Color.Gray.copy(alpha = 0.3f)
                )
                .clickable(enabled = currentPage < totalPages) { onNextClick() }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Next",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun PageSelectorBottomSheet(
    currentPage: Int,
    totalPages: Int,
    onPageSelected: (Int) -> Unit,
    onDone: () -> Unit
) {
    var selectedPage by remember { mutableStateOf(currentPage) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select Page",
                fontSize = 20.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Button(
                onClick = {
                    onPageSelected(selectedPage)
                    onDone()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.green)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Done",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(totalPages) { index ->
                val pageNumber = index + 1
                PageNumberItem(
                    pageNumber = pageNumber,
                    isSelected = pageNumber == selectedPage,
                    onClick = { selectedPage = pageNumber }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PageNumberItem(
    pageNumber: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = pageNumber.toString(),
            fontSize = 24.sp,
            fontFamily = OpenSans,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) colorResource(R.color.green) else Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun RecipeGridItem(
    recipe: RecipePlain, // todo: ganti ke tipe data aslinya
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val item1 = colorResource(id = R.color.item2_1)
    val item2 = colorResource(id = R.color.item2_2)
    val itemGradient = Brush.verticalGradient(listOf(item1, item2))

    Box(
        modifier = modifier
            .background(itemGradient, shape = RoundedCornerShape(10.dp))
            .padding(15.dp)
            .clickable {
                navController.navigate("recipedetail/${recipe.recipeId}")
            }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = recipe.image, // Di sinilah Anda meletakkan URL String
                contentDescription = "Recipe image", // Deskripsi untuk aksesibilitas
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop, // Atur bagaimana gambar di-scale
                // Tampilkan gambar lokal ini JIKA link-nya TIDAK BISA DIAKSES (misal: error 404, tidak ada internet)
                error = painterResource(id = R.drawable.default_food_image),
                fallback = painterResource(id = R.drawable.default_food_image) // Tampilkan gambar lokal ini JIKA link-nya NULL
            )
            Text(
                recipe.title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold
            )
        }
    }
}