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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.navbar.UserBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans
import java.time.format.TextStyle

// data class dummy buat tampilan
data class RecipeDummy(
    val id: Int,
    val name: String,
    val imageRes: Int = R.drawable.default_food_image
)

@Composable
fun RecipeScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            UserBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        RecipeContent(modifier = Modifier.padding(innerPadding), navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeContent(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))

    // Sample data - todo: replace with actual data
    val allRecipes = remember {
        (1..100).map { index ->
            RecipeDummy(
                id = index,
                name = "Recipe $index"
            )
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf(1) }
    var showPageSelector by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Foodstock") }

    val itemsPerPage = 6
    val totalPages = (allRecipes.size + itemsPerPage - 1) / itemsPerPage // todo: total pages
    val currentPageItems = allRecipes.drop((currentPage - 1) * itemsPerPage).take(itemsPerPage)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 32.dp,
                    start = 32.dp,
                    end = 32.dp,
                    bottom = 16.dp
                )
        ) {
            Text(
                "Recipes",
                fontSize = 24.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold
            )
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
                    placeholder = {
                        Text(
                            "Search",
                            fontSize = 14.sp,
                            fontFamily = OpenSans,
                            color = Color.Gray
                        ) },
                    modifier = Modifier
                        .weight(3f)
                        .border(1.dp, Color.Gray, CircleShape),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorResource(R.color.form_input),
                        unfocusedContainerColor = colorResource(R.color.form_input),
                        disabledContainerColor = colorResource(R.color.form_input).copy(alpha = 0.5f), // Lighter background for disabled
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent, // Border handled by .border modifier
                        disabledTextColor = Color.DarkGray // Lighter text for disabled
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon",
                            modifier = Modifier.size(20.dp) // Adjusted icon size
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) { // Adjusted IconButton size
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    modifier = Modifier.size(20.dp) // Adjusted icon size
                                )
                            }
                        }
                    },
                    shape = CircleShape,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp) // Reduced text size
                )
                Button(
                    onClick = {
                        navController.navigate("searchresults")
                    },
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = CircleShape,
                    modifier = Modifier.height(48.dp).weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        colorResource(R.color.green),
                                        colorResource(R.color.green_teal_dark)
                                    )
                                ), shape = CircleShape
                            ),
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

            Spacer(modifier = Modifier.height(10.dp))

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
                    isSelected = selectedFilter == "Popularity",
                    onClick = { selectedFilter = "Popularity" }
                )
                FilterButton(
                    modifier = Modifier.weight(1f),
                    text = "Foodstock",
                    isSelected = selectedFilter == "Foodstock",
                    onClick = { selectedFilter = "Foodstock" }
                )
                FilterButton(
                    modifier = Modifier.weight(1f),
                    text = "Nutrition",
                    isSelected = selectedFilter == "Nutrition",
                    onClick = { selectedFilter = "Nutrition" }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            RecipeGrid(
                recipes = currentPageItems,
                navController = navController
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaginationControls(
                currentPage = currentPage,
                totalPages = totalPages,
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
    recipes: List<RecipeDummy>, // todo: ganti ke tipe data aslinya
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
    recipe: RecipeDummy, // todo: ganti ke tipe data aslinya
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
                navController.navigate("recipedetail")
            }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = recipe.imageRes),
                contentDescription = "Recipe image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
            )
            Text(
                recipe.name,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold
            )
        }
    }
}