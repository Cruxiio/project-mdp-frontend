package com.example.nutrisaver.ui.screens.user

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nutrisaver.R
import com.example.nutrisaver.data.model.RecipeFavorite
import com.example.nutrisaver.ui.theme.OpenSans
import com.example.nutrisaver.viewmodel.RecipeStatusState
import com.example.nutrisaver.viewmodel.RecipeViewModel
import kotlinx.coroutines.delay

// dummy data class buat nampilin recipe
// todo: ganti ke object aslinya nanti setelah selesai
private data class UserRecipeDummy(
    val id: Int,
    val recipeId: Int,
    val userId: Int,
    val title: String,
    val description: String,
    val calories: Float,
    val protein: Float,
    val fat: Float,
    val carbs: Float,
    val vegan: Boolean,
    val sourceUrl: String?,
    val image: String?
)

// Generate dummy favorite recipes
private fun generateDummyFavoriteRecipes(): List<UserRecipeDummy> {
    return listOf(
        UserRecipeDummy(
            id = 1,
            recipeId = 101,
            userId = 1,
            title = "Grilled Chicken Salad",
            description = "A healthy and delicious grilled chicken salad with mixed greens, cherry tomatoes, and avocado. Perfect for a light lunch or dinner.",
            calories = 320.5f,
            protein = 35.2f,
            fat = 12.8f,
            carbs = 15.3f,
            vegan = false,
            sourceUrl = "https://example.com/grilled-chicken-salad",
            image = null
        ),
        UserRecipeDummy(
            id = 2,
            recipeId = 102,
            userId = 1,
            title = "Vegan Buddha Bowl",
            description = "Nutritious vegan bowl with quinoa, roasted vegetables, chickpeas, and tahini dressing. Packed with plant-based protein and fiber.",
            calories = 450.0f,
            protein = 18.5f,
            fat = 16.2f,
            carbs = 62.8f,
            vegan = true,
            sourceUrl = "https://example.com/vegan-buddha-bowl",
            image = null
        ),
        UserRecipeDummy(
            id = 3,
            recipeId = 103,
            userId = 1,
            title = "Salmon with Sweet Potato",
            description = "Baked salmon fillet served with roasted sweet potato and steamed broccoli. Rich in omega-3 fatty acids and vitamins.",
            calories = 520.3f,
            protein = 42.1f,
            fat = 22.5f,
            carbs = 35.7f,
            vegan = false,
            sourceUrl = "https://example.com/salmon-sweet-potato",
            image = null
        ),
        UserRecipeDummy(
            id = 4,
            recipeId = 104,
            userId = 1,
            title = "Quinoa Stuffed Bell Peppers",
            description = "Colorful bell peppers stuffed with quinoa, black beans, corn, and spices. A complete vegetarian meal that's both filling and nutritious.",
            calories = 380.7f,
            protein = 16.8f,
            fat = 8.9f,
            carbs = 68.4f,
            vegan = true,
            sourceUrl = "https://example.com/quinoa-stuffed-peppers",
            image = null
        ),
        UserRecipeDummy(
            id = 5,
            recipeId = 105,
            userId = 1,
            title = "Greek Yogurt Parfait",
            description = "Layered Greek yogurt with fresh berries, granola, and honey. A perfect breakfast or healthy snack option.",
            calories = 280.2f,
            protein = 20.3f,
            fat = 8.1f,
            carbs = 35.6f,
            vegan = false,
            sourceUrl = "https://example.com/greek-yogurt-parfait",
            image = null
        ),
        UserRecipeDummy(
            id = 6,
            recipeId = 106,
            userId = 1,
            title = "Lentil Curry",
            description = "Hearty and flavorful red lentil curry with coconut milk, tomatoes, and aromatic spices. Served with brown rice.",
            calories = 420.8f,
            protein = 22.7f,
            fat = 14.3f,
            carbs = 58.9f,
            vegan = true,
            sourceUrl = "https://example.com/lentil-curry",
            image = null
        )
    )
}

@Composable
fun FavoriteRecipeScreen(navController: NavController,recipeViewModel: RecipeViewModel) {
    Scaffold(
        topBar = {
            TopBar(onBackClick = { navController.popBackStack() })
        }
    ) { innerPadding ->
        FavoriteRecipeContent(
            modifier = Modifier.padding(innerPadding),
            recipeViewModel = recipeViewModel,
            navController = navController
        )
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colorResource(R.color.bg))
            .padding(
                top = 32.dp,
                bottom = 12.dp,
                start = 16.dp,
                end = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
        Text(
            text = "Favorite Recipes",
            fontSize = 20.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun FavoriteRecipeContent(
    modifier: Modifier = Modifier,
    recipeViewModel: RecipeViewModel,
    navController: NavController
) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))

    var searchQuery by remember { mutableStateOf("") }
    var favoriteRecipes by remember { mutableStateOf(generateDummyFavoriteRecipes()) } // todo: ganti ke data yang diambil melalui viewmodel

    // data recipe dari backend
    val listRecipeFavState by recipeViewModel.listRecipeFavoriteState.observeAsState(emptyList())

    // RecipeState logic
    val recipeState = recipeViewModel.recipeStatusState.observeAsState(RecipeStatusState.Idle)
    val context = LocalContext.current

    LaunchedEffect(searchQuery) {
        // debounce 500ms
        delay(500L)
        recipeViewModel.getFavoriteRecipes(searchQuery)
    }

    LaunchedEffect(recipeState.value) {
        when (recipeState.value) {
            is RecipeStatusState.Success -> {
//                Toast.makeText(context, "Berhasil load!", Toast.LENGTH_SHORT).show()
                Log.d("favoriteRecipeScreen", "isi data recipe ${listRecipeFavState}")
            }
            is RecipeStatusState.Error -> {
                Toast.makeText(context, (recipeState.value as RecipeStatusState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    // Filter recipes based on search query
//    val filteredRecipes = remember(searchQuery, favoriteRecipes) {
//        if (searchQuery.isBlank()) {
//            favoriteRecipes
//        } else {
//            favoriteRecipes.filter { recipe ->
//                recipe.title.contains(searchQuery, ignoreCase = true) ||
//                        recipe.description.contains(searchQuery, ignoreCase = true)
//            }
//        }
//    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(25.dp))
                    .background(colorResource(R.color.form_input)),
                placeholder = {
                    Text(
                        text = "Search favorite recipes...",
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
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.green),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recipe Count
            when(recipeState.value){
                is RecipeStatusState.Success -> {
                    listRecipeFavState?.let {
                        recipeFav ->
                        Text(
                            text = "${recipeFav.size} favorite recipe${if (recipeFav.size != 1) "s" else ""}",
                            fontSize = 16.sp,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                else -> {Text(
                    text = "0 favorite recipe",
                    fontSize = 16.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold
                )}
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Recipes List
            listRecipeFavState?.let {
                recipeFav ->
                if (recipeFav.isEmpty()) {
                    EmptyFavoritesState(
                        isSearching = searchQuery.isNotBlank()
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(recipeFav) { recipe ->
                            FavoriteRecipeCard(
                                recipe = recipe,
                                onRecipeClick = {
                                    // Navigate to recipe detail
                                    navController.navigate("recipedetail/${recipe.recipeId}")
                                },
                                onRemoveFromFavorites = { recipeId ->
                                    // Remove from favorites
                                    recipeViewModel.deleteRecipeFav(recipeId)
                                }
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun FavoriteRecipeCard(
    recipe: RecipeFavorite, // todo: ganti ke tipe data aslinya nanti setelah selesai
    onRecipeClick: () -> Unit,
    onRemoveFromFavorites: (recipeId: Int) -> Unit
) {
    var showRemoveDialog by remember { mutableStateOf(false) }
    val green = colorResource(R.color.green)
    var deletedRecipeID by remember { mutableStateOf(-1) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRecipeClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Recipe Image
                AsyncImage(
                    model = recipe.image,
                    contentDescription = "Recipe Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.default_food_image),
                    error = painterResource(id = R.drawable.default_food_image),
                    fallback = painterResource(id = R.drawable.default_food_image)
                )

                // Vegan Badge
                // Todo sementara badge disable dulu karena dar backend nda ada data
//                if (recipe.vegan) {
//                    Box(
//                        modifier = Modifier
//                            .padding(12.dp)
//                            .background(
//                                Color.Green.copy(alpha = 0.9f),
//                                RoundedCornerShape(12.dp)
//                            )
//                            .padding(horizontal = 8.dp, vertical = 4.dp)
//                            .align(Alignment.TopStart)
//                    ) {
//                        Text(
//                            text = "VEGAN",
//                            fontSize = 14.sp,
//                            fontFamily = OpenSans,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.White
//                        )
//                    }
//                }

                // Favorite Heart Button
                IconButton(
                    onClick = { showRemoveDialog = true; deletedRecipeID = recipe.recipeId },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .size(40.dp)
                        .background(
                            Color.White.copy(alpha = 0.9f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remove from favorites",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title
                Text(
                    text = recipe.title,
                    fontSize = 18.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                // Todo ini juga sementara disable karena nda ada data description dari BE
//                Text(
//                    text = recipe.description,
//                    fontSize = 14.sp,
//                    fontFamily = OpenSans,
//                    color = Color.Gray,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis
//                )
//
//                Spacer(modifier = Modifier.height(12.dp))

                // Nutrition Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    NutritionChip(
                        label = "Cal",
                        value = "${recipe.calories.toInt()}",
                        color = colorResource(R.color.green)
                    )
                    NutritionChip(
                        label = "Carbs",
                        value = "${recipe.carbs.toInt()}g",
                        color = colorResource(R.color.carbs)
                    )
                    NutritionChip(
                        label = "Protein",
                        value = "${recipe.protein.toInt()}g",
                        color = colorResource(R.color.protein)
                    )
                    NutritionChip(
                        label = "Fat",
                        value = "${recipe.fat.toInt()}g",
                        color = colorResource(R.color.fat)
                    )
                }
            }
        }
    }

    // Remove from favorites confirmation dialog
    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false; deletedRecipeID = -1 },
            title = {
                Text(
                    text = "Remove from Favorites",
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove \"${recipe.title}\" from your favorites?",
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Normal
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRemoveFromFavorites(deletedRecipeID)
                        showRemoveDialog = false
                        deletedRecipeID = -1
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.delete_confirm))
                ) {
                    Text("Remove", color = Color.White, fontFamily = OpenSans, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) {
                    Text("Cancel", fontFamily = OpenSans)
                }
            }
        )
    }
}

@Composable
private fun NutritionChip(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Text(
            text = label,
            fontSize = 14.sp,
            fontFamily = OpenSans,
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun EmptyFavoritesState(
    isSearching: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isSearching) "🔍" else "💔",
            fontSize = 64.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isSearching) "No recipes found" else "No favorite recipes yet",
            fontSize = 20.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isSearching)
                "Try adjusting your search terms"
            else
                "Start exploring recipes and add them to your favorites!",
            fontSize = 14.sp,
            fontFamily = OpenSans,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}