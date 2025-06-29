package com.example.nutrisaver.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.screens.admin.FilterDropdown
import com.example.nutrisaver.ui.theme.OpenSans
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.style.TextAlign
import com.example.nutrisaver.data.model.HealthArticle
import com.example.nutrisaver.viewmodel.HealthArticleListState
import com.example.nutrisaver.viewmodel.HealthArticleViewModel


@Composable
fun UserHealthArticleScreen(
    navController: NavController,
    healthArticleViewModel: HealthArticleViewModel
) {
    Scaffold(
        topBar = {
            TopBar(onBackClick = { navController.popBackStack() })
        }
    ) { innerPadding ->
        UserHealthArticleContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            healthArticleViewModel = healthArticleViewModel
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
            text = "Health Article",
            fontSize = 20.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}


@Composable
private fun UserHealthArticleContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    healthArticleViewModel: HealthArticleViewModel
) {
    val articlesState by healthArticleViewModel.articlesState.observeAsState()

    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))

    // State for search and filters
    var searchQuery by remember { mutableStateOf("") }
    var selectedGoalFilter by remember { mutableStateOf("All") }
    var selectedDietFilter by remember { mutableStateOf("All") }
    var showGoalDropdown by remember { mutableStateOf(false) }
    var showDietDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery, selectedGoalFilter, selectedDietFilter) {
        healthArticleViewModel.loadHealthArticles(
            title = searchQuery,
            targetGoal = selectedGoalFilter,
            targetDietType = selectedDietFilter
        )
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
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(25.dp)),
                placeholder = {
                    Text(
                        text = "Search articles...",
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
            // Filter Dropdowns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Goal Filter
                Box(modifier = Modifier.weight(1f)) {
                    FilterDropdown(
                        selectedValue = selectedGoalFilter,
                        options = listOf("All", "gain", "diet", "maintain"),
                        label = "Goal",
                        expanded = showGoalDropdown,
                        onExpandedChange = { showGoalDropdown = it },
                        onValueChange = { selectedGoalFilter = it }
                    )
                }

                // Diet Type Filter
                Box(modifier = Modifier.weight(1f)) {
                    FilterDropdown(
                        selectedValue = selectedDietFilter,
                        options = listOf("All", "vegan", "ketogenic", "low carbs", "strict calories", "free", "custom"),
                        label = "Diet Type",
                        expanded = showDietDropdown,
                        onExpandedChange = { showDietDropdown = it },
                        onValueChange = { selectedDietFilter = it }
                    )
                }
            }

            when (val state = articlesState) {
                is HealthArticleListState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HealthArticleListState.Success -> {
                    val articles = state.data
                    if (articles.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                        ) {
                            Text(
                                text = "No articles found matching your criteria",
                                fontSize = 16.sp,
                                fontFamily = OpenSans,
                                color = Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // 5. Loop melalui data dari state dan perbaiki parameter
                        articles.forEach { articleItem ->
                            HealthArticleCard(
                                article = articleItem, // <-- Kirim objek dari loop
                            )
                        }
                    }
                }
                is HealthArticleListState.Error -> {
                    Text(text = state.message, color = Color.Red, modifier = Modifier.padding(16.dp))
                }
                null -> {
                    // State awal, bisa tampilkan loading juga
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

            }
        }
    }
}

@Composable
private fun HealthArticleCard(
    article: HealthArticle,
) {
    val green = colorResource(R.color.green)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = article.title,
                fontSize = 18.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TagChip(
                    text = article.targetGoal.capitalize(),
                    backgroundColor = green.copy(alpha = 0.2f),
                    textColor = green
                )
                TagChip(
                    text = article.targetDietType.capitalize(),
                    backgroundColor = Color.Blue.copy(alpha = 0.2f),
                    textColor = Color.Blue
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = article.content,
                fontSize = 14.sp,
                fontFamily = OpenSans,
                color = Color.Gray,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "By: ${article.createdBy}",
                    fontSize = 12.sp,
                    fontFamily = OpenSans,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = article.createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    fontSize = 12.sp,
                    fontFamily = OpenSans,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun TagChip(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
