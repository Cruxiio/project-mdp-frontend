package com.example.nutrisaver.ui.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

@Composable
private fun RecipeContent(modifier: Modifier = Modifier,
                  navController: NavController) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")
    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(
                "Recipes",
                fontSize = 24.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search") },
                    modifier = Modifier.weight(3f),
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
                    shape = CircleShape
                )
                Button(
                    onClick = {
                        // Todo: navigate ke search results screen dan bawa parameter search
                        navController.navigate("searchresults")
                    },
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = CircleShape,
                    modifier = Modifier.height(50.dp).weight(1f)
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
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(vertical = 20.dp))
            Text(
                "Recommended By Your Food Stock",
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items) { item ->
                    RecipeItem()
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Recommended By Your Nutrient Intake",
                fontSize = 16.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items) { item ->
                    RecipeItem()
                }
            }
        }
    }
}

@Composable
private fun RecipeItem() {
    val item1 = colorResource(id = R.color.item2_1)
    val item2 = colorResource(id = R.color.item2_2)
    val itemGradient = Brush.verticalGradient(listOf(item1, item2))
    Box(
        modifier = Modifier
            .width(180.dp)
            .height(220.dp)
            .background(itemGradient, shape = RoundedCornerShape(10.dp))
            .padding(15.dp)
    ) {
       Column(
           modifier = Modifier.fillMaxWidth()
       ) {
           Image(
               painter = painterResource(id = R.drawable.default_food_image), // todo: ganti ke gambar recipe
               contentDescription = "deskripsi gambar recipe",
               modifier = Modifier.fillMaxWidth().width(150.dp).height(100.dp).padding(bottom = 4.dp),
               contentScale = ContentScale.Crop,
           )
           Text(
               "Nama Recipe",
               modifier = Modifier.fillMaxWidth(),
               textAlign = TextAlign.Center,
               fontSize = 16.sp,
               fontFamily = OpenSans,
               fontWeight = FontWeight.Bold
           )
       }
    }
}