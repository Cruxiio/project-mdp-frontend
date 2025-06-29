package com.example.nutrisaver.ui.screens.user

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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
import com.example.nutrisaver.data.model.RecipeDetail
import com.example.nutrisaver.ui.theme.OpenSans
import com.example.nutrisaver.viewmodel.RecipeStatusState
import com.example.nutrisaver.viewmodel.RecipeViewModel

// Data classes to match the JSON structure
data class RecipeDetailDummy(
    val id: Int,
    val title: String,
    val image: String,
    val readyInMinutes: Int,
    val servings: Int,
    val vegetarian: Boolean,
    val vegan: Boolean,
    val glutenFree: Boolean,
    val dairyFree: Boolean,
    val veryHealthy: Boolean,
    val cheap: Boolean,
    val veryPopular: Boolean,
    val sustainable: Boolean,
    val lowFodmap: Boolean,
    val nutrition: NutritionDummy,
    val extendedIngredients: List<IngredientDummy>,
    val instructions: String,
    val summary: String
)
data class NutritionDummy(
    val nutrients: List<NutrientDummy>,
    val caloricBreakdown: CaloricBreakdownDummy
)
data class NutrientDummy(
    val name: String,
    val amount: Double,
    val unit: String
)
data class CaloricBreakdownDummy(
    val percentProtein: Double,
    val percentFat: Double,
    val percentCarbs: Double
)
data class IngredientDummy(
    val id: Int,
    val name: String,
    val original: String,
    val amount: Double,
    val unit: String
)

@Composable
fun RecipeDetailScreen(navController: NavController, recipeId : Int, recipeViewModel: RecipeViewModel) {
    // data sample dari json yang diambil, todo: nanti ganti ke data yang diambil
//    val sampleRecipe = RecipeDetailDummy(
//        id = 945221,
//        title = "Peanut Butter Banana Oat Breakfast Cookies",
//        image = "https://img.spoonacular.com/recipes/945221-556x370.jpg",
//        readyInMinutes = 45,
//        servings = 16,
//        vegetarian = false,
//        vegan = false,
//        glutenFree = true,
//        dairyFree = true,
//        veryHealthy = false,
//        cheap = false,
//        veryPopular = true,
//        sustainable = false,
//        lowFodmap = false,
//        nutrition = NutritionDummy(
//            nutrients = listOf(
//                NutrientDummy("Calories", 103.19, "kcal"),
//                NutrientDummy("Protein", 3.67, "g"),
//                NutrientDummy("Fat", 5.38, "g"),
//                NutrientDummy("Carbohydrates", 11.25, "g")
//            ),
//            caloricBreakdown = CaloricBreakdownDummy(13.58, 44.8, 41.62)
//        ),
//        extendedIngredients = listOf(
//            IngredientDummy(9040, "bananas", "2 ripe bananas, mashed until smooth & creamy", 2.0, ""),
//            IngredientDummy(10116098, "creamy peanut butter", "1/3 cup peanut butter - creamy or chunky", 0.33, "cup"),
//            IngredientDummy(8121, "oatmeal", "1 1/2 cups quick oatmeal - uncooked", 1.5, "cups")
//        ),
//        instructions = "Preheat oven to 350 degrees. In a large bowl, mix mashed banana & peanut butter until completely combined...",
//        summary = "If you want to add more gluten free and dairy free recipes to your repertoire, this might be a recipe you should try."
//    )
    // data recipe dari backend
    val recipeData by recipeViewModel.recipeDetailState.observeAsState(null)

    LaunchedEffect(Unit) {
        recipeViewModel.getRecipeDetail(recipeId)
    }

    // RecipeState logic
    val recipeState = recipeViewModel.recipeStatusState.observeAsState(RecipeStatusState.Idle)
    val context = LocalContext.current

    LaunchedEffect(recipeState.value) {
        when (recipeState.value) {
            is RecipeStatusState.Success -> {
//                Toast.makeText(context, "Berhasil load!", Toast.LENGTH_SHORT).show()
                Log.d("recipeDetailScreen", "isi data recipe ${recipeData}")
            }
            is RecipeStatusState.Error -> {
                Toast.makeText(context, (recipeState.value as RecipeStatusState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    when(recipeState.value){
        is RecipeStatusState.Success -> {
            recipeData?.let {
                recipe ->
                Scaffold { innerPadding ->
                    RecipeDetailContent(
                        recipe = recipe,
                        recipeId = recipeId,
                        modifier = Modifier.padding(innerPadding),
                        navController = navController
                    )
                }
            }
        }
        else -> {}
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {}
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
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side - Back button and title
            Row(
                verticalAlignment = Alignment.CenterVertically
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
                    text = "Recipe Detail",
                    fontSize = 20.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Right side - Favorite button
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color.Red else Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecipeDetailContent(
    recipe: RecipeDetail, // todo: ganti ke tipe data aslinya
    recipeId: Int,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    // State for favorite toggle
    var isFavorite by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 70.dp, bottom = 80.dp)
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Recipe Title
                Text(
                    text = recipe.title,
                    fontSize = 24.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                // Basic Info Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoItem(
                        icon = painterResource(R.drawable.fire_icon),
                        label = "Calories",
                        value = "${recipe.calories?.toInt() ?: 0} kcal"
                    )
                    InfoItem(
                        icon = painterResource(R.drawable.time_icon_2),
                        label = "Time",
                        value = "${recipe.timeToCook} min"
                    )
                    InfoItem(
                        icon = painterResource(R.drawable.group_icon),
                        label = "Servings",
                        value = "${recipe.servings}"
                    )
                }

                // Nutrition Macros
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Nutrition per serving",
                            fontSize = 16.sp,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            NutritionItem(
                                label = "Protein",
                                value = "${recipe.protein?.toInt() ?: 0}g",
                                color = Color(0xFF4CAF50)
                            )
                            NutritionItem(
                                label = "Fat",
                                value = "${recipe.fat?.toInt() ?: 0}g",
                                color = Color(0xFFFF9800)
                            )
                            NutritionItem(
                                label = "Carbs",
                                value = "${recipe.carbs?.toInt() ?: 0}g",
                                color = Color(0xFFF44336)
                            )
                        }
                    }
                }

                // Dietary Tags
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recipe.tags.forEach {
                        tag -> DietaryTag(tag)
                    }
//                    if (recipe.vegetarian) DietaryTag("Vegetarian")
//                    if (recipe.vegan) DietaryTag("Vegan")
//                    if (recipe.glutenFree) DietaryTag("Gluten Free")
//                    if (recipe.dairyFree) DietaryTag("Dairy Free")
//                    if (recipe.veryHealthy) DietaryTag("Healthy")
//                    if (recipe.cheap) DietaryTag("Cheap")
//                    if (recipe.veryPopular) DietaryTag("Very Popular")
//                    if (recipe.sustainable) DietaryTag("Sustainable")
//                    if (recipe.lowFodmap) DietaryTag("Low Fodmap")
                }

                // Ingredients Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Ingredients",
                            fontSize = 18.sp,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        recipe.ingredients.take(5).forEach { ingredient ->
                            Text(
                                text = "• ${ingredient.original}",
                                fontSize = 14.sp,
                                fontFamily = OpenSans,
                                color = Color.Black,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        if (recipe.ingredients.size > 5) {
                            Text(
                                text = "... and ${recipe.ingredients.size - 5} more ingredients",
                                fontSize = 12.sp,
                                fontFamily = OpenSans,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                // Instructions Preview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Instructions",
                            fontSize = 18.sp,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = recipe.instruction.take(200) + if (recipe.instruction.length > 200) "..." else "",
                            fontSize = 14.sp,
                            fontFamily = OpenSans,
                            color = Color.Black,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        TopBar(
            onBackClick = { navController.popBackStack() },
            isFavorite = isFavorite,
            onFavoriteClick = {
                isFavorite = !isFavorite
                // TODO: Add logic to save/remove from favorites
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )

        // Create Recipe Button
        Box(
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = {
                    navController.navigate("createrecipe")
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
                        text = "Create this Recipe",
                        fontSize = 18.sp,
                        fontFamily = OpenSans,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItem(
    icon: Painter,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .padding(bottom = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = icon,
                contentDescription = label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(colorResource(R.color.green))
            )
        }
        Text(
            text = label,
            fontSize = 16.sp,
            fontFamily = OpenSans,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
private fun NutritionItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 16.sp,
            fontFamily = OpenSans,
            color = Color.Gray
        )
    }
}

@Composable
private fun DietaryTag(text: String) {
    Box(
        modifier = Modifier
            .background(
                colorResource(R.color.green),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}