package com.example.nutrisaver.ui.screens.user

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.theme.OpenSans

@Composable
fun SearchResultScreen(navController: NavController) {
    Scaffold { innerPadding ->
        SearchResultContent(modifier = Modifier.padding(innerPadding), navController)
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
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf("") }
    val itemResults = listOf("Apple", "Banana", "Cherry", "Date", "Elderberry", "Fig") // todo: ganti ke hasil search

    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))

    val filterTypeOptions = listOf("DESC", "ASC")
    var filterType by remember { mutableStateOf(filterTypeOptions[0]) }
    var filterTypeExpanded by remember { mutableStateOf(false) }

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
                            placeholder = { Text("Search") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = colorResource(R.color.black),
                                unfocusedTextColor = colorResource(R.color.black),
                                focusedContainerColor = colorResource(R.color.form_input),
                                unfocusedContainerColor = colorResource(R.color.form_input)
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search icon"
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear search"
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                    Box(modifier = Modifier.width(110.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = filterTypeExpanded,
                            onExpandedChange = { filterTypeExpanded = !filterTypeExpanded }
                        ) {
                            OutlinedTextField(
                                value = filterType,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterTypeExpanded)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = colorResource(R.color.black),
                                    unfocusedTextColor = colorResource(R.color.black),
                                    focusedContainerColor = colorResource(R.color.form_input),
                                    unfocusedContainerColor = colorResource(R.color.form_input)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = filterTypeExpanded,
                                onDismissRequest = { filterTypeExpanded = false }
                            ) {
                                filterTypeOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            filterType = option
                                            filterTypeExpanded = false
                                        }
                                    )
                                }
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
                    items(itemResults) { item ->
                        RecipeGridItem(navController)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeGridItem(
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
                painter = painterResource(R.drawable.default_food_image), // todo: ganti ke link gambar aslinya
                contentDescription = "Recipe image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
            )
            Text(
                "Nama Recipe", // todo: ganti ke nama recipe aslinya
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold
            )
        }
    }
}