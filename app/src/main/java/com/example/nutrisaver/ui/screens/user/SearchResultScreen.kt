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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.nutrisaver.data.model.RecipePlain
import com.example.nutrisaver.ui.theme.OpenSans
import com.example.nutrisaver.viewmodel.RecipeStatusState
import com.example.nutrisaver.viewmodel.RecipeViewModel
import kotlinx.coroutines.delay

@Composable
fun SearchResultScreen(navController: NavController, keyword:String, recipeViewModel: RecipeViewModel) {
    Scaffold { innerPadding ->
        SearchResultContent(modifier = Modifier.padding(innerPadding), navController, keyword, recipeViewModel)
    }
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
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
            text = "Search Results",
            fontSize = 20.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchResultContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    keyword: String,
    recipeViewModel: RecipeViewModel
) {
    var searchQuery by remember { mutableStateOf(keyword) }
    val itemResults by recipeViewModel.listRecipeState.observeAsState(emptyList())
//        listOf("Apple", "Banana", "Cherry", "Date", "Elderberry", "Fig") // todo: ganti ke hasil search

    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))

    val filterTypeOptions = listOf("DESC", "ASC")
    var filterType by remember { mutableStateOf(filterTypeOptions[0]) }
    var filterTypeExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery, filterType) {
        // Tunggu selama 500 milidetik (0.5 detik).
        delay(500L)
        recipeViewModel.getAllRecipe(searchQuery, "popularity", 0, 6)
    }

    // RecipeState logic
    val recipeState = recipeViewModel.recipeStatusState.observeAsState(RecipeStatusState.Idle)
    val context = LocalContext.current

    LaunchedEffect(recipeState.value) {
        when (recipeState.value) {
            is RecipeStatusState.Success -> {
//                Toast.makeText(context, "Berhasil load!", Toast.LENGTH_SHORT).show()
                Log.d("isi recipe", "${itemResults}")
            }
            is RecipeStatusState.Error -> {
                Toast.makeText(context, (recipeState.value as RecipeStatusState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Box(modifier = modifier.fillMaxSize().background(backgroundGradient)) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TopBar(onBackClick = { navController.popBackStack() })
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorResource(R.color.form_input), RoundedCornerShape(10.dp)), // Corrected background
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
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(R.color.green),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            singleLine = true
                        )
                    }
                    Box(modifier = Modifier.weight(0.5f)) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { filterTypeExpanded = !filterTypeExpanded }
                                .border(
                                    width = 0.5.dp,
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.form_input)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = filterType,
                                    fontSize = 14.sp,
                                    fontFamily = OpenSans,
                                    color = Color.Black
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = Color.Gray
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = filterTypeExpanded,
                            onDismissRequest = { filterTypeExpanded = false },
                            modifier = Modifier.background(colorResource(R.color.form_input))
                        ) {
                            filterTypeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option,
                                            fontFamily = OpenSans,
                                            color = if (option == filterType) colorResource(R.color.green) else Color.Black,
                                            fontWeight = if (option == filterType) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        filterType = option
                                        filterTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 2.dp)
                Text(
                    "# Results for ${searchQuery}", // todo: ganti ke jumlah hasil search
                    fontSize = 18.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    when (recipeState.value){
                        is RecipeStatusState.Success -> {
                            Log.d("searchResultScreen", "isi all recipes sblm tampil: ${itemResults}")
                            items(itemResults) { item ->
                                RecipeGridItem(navController,item)
                            }
                        }
                        else -> {}
                    }

                }
            }
        }
    }
}

@Composable
private fun RecipeGridItem(
    navController: NavController,
    recipe: RecipePlain,
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
                recipe.title, // todo: ganti ke nama recipe aslinya
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}