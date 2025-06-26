package com.example.nutrisaver.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.nutrisaver.ui.navbar.AdminBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// dummy data class buat tampilan
// todo: hapus nanti ganti sm data class healtharticle sebenarnya
data class HealthArticleDummy(
    val id: Int = 0,
    val title: String,
    val content: String,
    val targetGoal: String, // 'gain', 'diet', 'maintain'
    val targetDietType: String, // 'vegan', 'ketogenic', 'low carbs', 'strict calories', 'free', 'custom'
    val createdBy: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

// Generate dummy data pakai array
// todo: hapus nanti
fun generateDummyHealthArticles(): List<HealthArticleDummy> {
    return listOf(
        HealthArticleDummy(
            id = 1,
            title = "10 Tips for Effective Weight Loss",
            content = "Losing weight can be challenging, but with the right approach, it's achievable. Here are 10 proven tips to help you on your weight loss journey...",
            targetGoal = "diet",
            targetDietType = "low carbs",
            createdBy = "Dr. Smith",
            createdAt = LocalDateTime.now().minusDays(5)
        ),
        HealthArticleDummy(
            id = 2,
            title = "Building Muscle: A Complete Guide",
            content = "Gaining muscle mass requires a combination of proper nutrition, consistent training, and adequate rest. This comprehensive guide will walk you through...",
            targetGoal = "gain",
            targetDietType = "free",
            createdBy = "Fitness Expert",
            createdAt = LocalDateTime.now().minusDays(3)
        ),
        HealthArticleDummy(
            id = 3,
            title = "Ketogenic Diet: Benefits and Risks",
            content = "The ketogenic diet has gained popularity for its potential weight loss benefits. However, it's important to understand both the advantages and potential risks...",
            targetGoal = "diet",
            targetDietType = "ketogenic",
            createdBy = "Nutritionist",
            createdAt = LocalDateTime.now().minusDays(1)
        ),
        HealthArticleDummy(
            id = 4,
            title = "Maintaining Your Ideal Weight",
            content = "Once you've reached your target weight, the challenge becomes maintaining it. Here are strategies to help you maintain your ideal weight long-term...",
            targetGoal = "maintain",
            targetDietType = "free",
            createdBy = "Health Coach",
            createdAt = LocalDateTime.now().minusHours(12)
        ),
        HealthArticleDummy(
            id = 5,
            title = "Plant-Based Nutrition for Athletes",
            content = "Vegan diets can provide all the nutrients needed for athletic performance. This article explores how to optimize plant-based nutrition for sports...",
            targetGoal = "gain",
            targetDietType = "vegan",
            createdBy = "Sports Nutritionist",
            createdAt = LocalDateTime.now().minusHours(6)
        )
    )
}

@Composable
fun AdminHealthArticleScreen(navController: NavController) {
    var showAddEditBottomSheet by remember { mutableStateOf(false) }
    var editingArticle by remember { mutableStateOf<HealthArticleDummy?>(null) } // todo: ganti ke tipe data aslinya

    Scaffold(
        bottomBar = {
            AdminBottomNavBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingArticle = null
                    showAddEditBottomSheet = true
                },
                containerColor = colorResource(R.color.green),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Article"
                )
            }
        }
    ) { innerPadding ->
        AdminHealthArticleContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            onEditArticle = { article ->
                editingArticle = article
                showAddEditBottomSheet = true
            }
        )

        if (showAddEditBottomSheet) {
            AddEditArticleBottomSheet(
                article = editingArticle,
                onDismiss = { showAddEditBottomSheet = false },
                onSave = { article ->
                    // TODO: Implement save / edit logic
                    showAddEditBottomSheet = false
                }
            )
        }
    }
}

@Composable
fun AdminHealthArticleContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    onEditArticle: (HealthArticleDummy) -> Unit // todo: ganti ke tipe data aslinya
) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))

    // State for search and filters
    var searchQuery by remember { mutableStateOf("") }
    var selectedGoalFilter by remember { mutableStateOf("All") }
    var selectedDietFilter by remember { mutableStateOf("All") }
    var showGoalDropdown by remember { mutableStateOf(false) }
    var showDietDropdown by remember { mutableStateOf(false) }

    val allArticles = remember { generateDummyHealthArticles() }

    // Filter articles based on search and filters
    val filteredArticles = remember(searchQuery, selectedGoalFilter, selectedDietFilter) {
        allArticles.filter { article ->
            val matchesSearch = searchQuery.isBlank() ||
                    article.title.contains(searchQuery, ignoreCase = true) ||
                    article.content.contains(searchQuery, ignoreCase = true) ||
                    article.createdBy.contains(searchQuery, ignoreCase = true)

            val matchesGoal = selectedGoalFilter == "All" || article.targetGoal == selectedGoalFilter
            val matchesDiet = selectedDietFilter == "All" || article.targetDietType == selectedDietFilter

            matchesSearch && matchesGoal && matchesDiet
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
                .padding(24.dp)
        ) {
            // Title
            Text(
                "Health Articles",
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Search TextField
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

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

            // Articles List
            if (filteredArticles.isEmpty()) {
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
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                filteredArticles.forEach { article ->
                    HealthArticleCard(
                        article = article,
                        onEditClick = { onEditArticle(article) },
                        onDeleteClick = {
                            // TODO: Implement delete logic
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Add bottom padding for FAB
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun FilterDropdown(
    selectedValue: String,
    options: List<String>,
    label: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onValueChange: (String) -> Unit
) {
    val green = colorResource(R.color.green)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpandedChange(!expanded) },
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label: $selectedValue",
                fontSize = 14.sp,
                fontFamily = OpenSans,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = Color.Gray
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onExpandedChange(false) },
        modifier = Modifier.background(Color.White)
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = option,
                        fontFamily = OpenSans,
                        color = if (option == selectedValue) green else Color.Black,
                        fontWeight = if (option == selectedValue) FontWeight.Bold else FontWeight.Normal
                    )
                },
                onClick = {
                    onValueChange(option)
                    onExpandedChange(false)
                }
            )
        }
    }
}

@Composable
private fun HealthArticleCard(
    article: HealthArticleDummy, // todo: ganti ke tipe data aslinya
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
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
                }

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Color.Blue.copy(alpha = 0.2f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.Blue,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Color.Red.copy(alpha = 0.2f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Article",
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${article.title}\"? This action cannot be undone.",
                    fontFamily = OpenSans
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White, fontFamily = OpenSans)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", fontFamily = OpenSans)
                }
            }
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditArticleBottomSheet(
    article: HealthArticleDummy?, // todo: ganti ke tipe data aslinya
    onDismiss: () -> Unit,
    onSave: (HealthArticleDummy) -> Unit // todo: ganti ke tipe data aslinya
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var title by remember { mutableStateOf(article?.title ?: "") }
    var content by remember { mutableStateOf(article?.content ?: "") }
    var targetGoal by remember { mutableStateOf(article?.targetGoal ?: "diet") }
    var targetDietType by remember { mutableStateOf(article?.targetDietType ?: "free") }
    var createdBy by remember { mutableStateOf(article?.createdBy ?: "") }

    var showGoalDropdown by remember { mutableStateOf(false) }
    var showDietDropdown by remember { mutableStateOf(false) }

    val isEditing = article != null
    val green = colorResource(R.color.green)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Title
            Text(
                text = if (isEditing) "Edit Article" else "Add New Article",
                fontSize = 20.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Title field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title", fontFamily = OpenSans) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = green,
                    focusedLabelColor = green
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content field
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content", fontFamily = OpenSans) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = green,
                    focusedLabelColor = green
                ),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Target Goal dropdown
            Box {
                OutlinedTextField(
                    value = targetGoal,
                    onValueChange = { },
                    label = { Text("Target Goal", fontFamily = OpenSans) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showGoalDropdown = true },
                    enabled = false,
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color.Gray,
                        disabledTextColor = Color.Black
                    )
                )

                DropdownMenu(
                    expanded = showGoalDropdown,
                    onDismissRequest = { showGoalDropdown = false }
                ) {
                    listOf("gain", "diet", "maintain").forEach { goal ->
                        DropdownMenuItem(
                            text = { Text(goal.capitalize(), fontFamily = OpenSans) },
                            onClick = {
                                targetGoal = goal
                                showGoalDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target Diet Type dropdown
            Box {
                OutlinedTextField(
                    value = targetDietType,
                    onValueChange = { },
                    label = { Text("Diet Type", fontFamily = OpenSans) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDietDropdown = true },
                    enabled = false,
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color.Gray,
                        disabledTextColor = Color.Black
                    )
                )

                DropdownMenu(
                    expanded = showDietDropdown,
                    onDismissRequest = { showDietDropdown = false }
                ) {
                    listOf("vegan", "ketogenic", "low carbs", "strict calories", "free", "custom").forEach { diet ->
                        DropdownMenuItem(
                            text = { Text(diet.capitalize(), fontFamily = OpenSans) },
                            onClick = {
                                targetDietType = diet
                                showDietDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Created By field
            OutlinedTextField(
                value = createdBy,
                onValueChange = { createdBy = it },
                label = { Text("Created By", fontFamily = OpenSans) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = green,
                    focusedLabelColor = green
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", fontFamily = OpenSans, color = Color.Gray)
                }

                Button(
                    onClick = {
                        // todo: logika save / update health article
                        // contoh ambil data dari field
//                        val newArticle = HealthArticle(
//                            id = article?.id ?: 0,
//                            title = title,
//                            content = content,
//                            targetGoal = targetGoal,
//                            targetDietType = targetDietType,
//                            createdBy = createdBy,
//                            createdAt = article?.createdAt ?: LocalDateTime.now()
//                        )
//                        onSave(newArticle)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = green),
                    enabled = title.isNotBlank() && content.isNotBlank() && createdBy.isNotBlank()
                ) {
                    Text(
                        if (isEditing) "Update" else "Save",
                        fontFamily = OpenSans,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}