package com.example.nutrisaver.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.data.model.HealthArticle
import com.example.nutrisaver.ui.navbar.AdminBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans
import com.example.nutrisaver.viewmodel.CrudState
import com.example.nutrisaver.viewmodel.HealthArticleListState
import com.example.nutrisaver.viewmodel.HealthArticleViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@Composable
fun AdminHealthArticleScreen(navController: NavController, healthArticleViewModel: HealthArticleViewModel) {
    var showAddEditBottomSheet by remember { mutableStateOf(false) }
    var editingArticle by remember { mutableStateOf<HealthArticle?>(null) }
    val context = LocalContext.current
    val crudState by healthArticleViewModel.crudState.observeAsState()

    // Efek untuk menampilkan Toast berdasarkan hasil operasi CUD
    LaunchedEffect(crudState) {
        when (val state = crudState) {
            is CrudState.Success -> {
                Toast.makeText(context, "Operation successful!", Toast.LENGTH_SHORT).show()
                healthArticleViewModel.onCrudOperationFinished() // Reset state setelah ditampilkan
            }
            is CrudState.Error -> {
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                healthArticleViewModel.onCrudOperationFinished()
            }
            else -> { /* Tidak melakukan apa-apa untuk Idle atau Loading */ }
        }
    }

    Scaffold(
        bottomBar = { AdminBottomNavBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingArticle = null // Mode Add
                    showAddEditBottomSheet = true
                },
                containerColor = colorResource(R.color.green),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Article")
            }
        }
    ) { innerPadding ->
        AdminHealthArticleContent(
            modifier = Modifier.padding(innerPadding),
            healthArticleViewModel = healthArticleViewModel,
            onEditArticle = { article ->
                editingArticle = article // Mode Edit
                showAddEditBottomSheet = true
            },
            onDeleteArticle = { articleId ->
                healthArticleViewModel.deleteArticle(articleId)
            }
        )

        if (showAddEditBottomSheet) {
            AddEditArticleBottomSheet(
                articleToEdit = editingArticle,
                onDismiss = { showAddEditBottomSheet = false },
                onSave = { title, content, goal, diet, createdBy ->
                    if (editingArticle == null) {
                        // CREATE
                        healthArticleViewModel.createArticle(title, content, goal, diet, createdBy)
                    } else {
                        // UPDATE
                        healthArticleViewModel.updateArticle(editingArticle!!.id, title, content, goal, diet)
                    }
                    showAddEditBottomSheet = false // Tutup bottom sheet
                }
            )
        }
    }
}

@Composable
fun AdminHealthArticleContent(
    modifier: Modifier = Modifier,
    healthArticleViewModel: HealthArticleViewModel,
    onEditArticle: (HealthArticle) -> Unit,
    onDeleteArticle: (Int) -> Unit
) {
    val articlesState by healthArticleViewModel.articlesState.observeAsState()
    val backgroundGradient = Brush.verticalGradient(listOf(colorResource(id = R.color.bg2_1), colorResource(id = R.color.bg2_2)))

    var searchQuery by remember { mutableStateOf("") }
    var selectedGoalFilter by remember { mutableStateOf("All") }
    var selectedDietFilter by remember { mutableStateOf("All") }
    var showGoalDropdown by remember { mutableStateOf(false) }
    var showDietDropdown by remember { mutableStateOf(false) }

    // Memuat artikel berdasarkan filter
    LaunchedEffect(searchQuery, selectedGoalFilter, selectedDietFilter) {
        healthArticleViewModel.loadHealthArticles(searchQuery, selectedGoalFilter, selectedDietFilter)
    }

    Box(modifier = modifier.fillMaxSize().background(backgroundGradient)) {
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Text("Health Articles", fontFamily = OpenSans, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(20.dp))
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

            // Menampilkan daftar artikel dari ViewModel
            when (val state = articlesState) {
                is HealthArticleListState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is HealthArticleListState.Success -> {
                    if (state.data.isEmpty()) {
                        Card { Text("No articles found.", modifier = Modifier.padding(20.dp), textAlign = TextAlign.Center) }
                    } else {
                        state.data.forEach { article ->
                            HealthArticleCard(
                                article = article,
                                onEditClick = { onEditArticle(article) },
                                onDeleteClick = { onDeleteArticle(article.id) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
                is HealthArticleListState.Error -> {
                    Text(text = state.message, color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                null -> {}
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// FilterDropdown tidak perlu diubah

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

    // Box ini membuat seluruh area Card bisa diklik
    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandedChange(!expanded) }, // Toggle state 'expanded' saat diklik
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

        // DropdownMenu yang muncul/hilang berdasarkan state 'expanded'
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }, // Tutup menu jika pengguna mengklik di luar
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.replaceFirstChar { it.titlecase() }, // Tampilkan dengan huruf kapital
                            fontFamily = OpenSans,
                            color = if (option == selectedValue) green else Color.Black,
                            fontWeight = if (option == selectedValue) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onValueChange(option)      // Update state pilihan
                        onExpandedChange(false)    // Tutup menu setelah memilih
                    }
                )
            }
        }
    }
}

@Composable
private fun HealthArticleCard(
    article: HealthArticle, // Menerima model data asli
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
                // Kolom untuk Judul dan Tag
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
                            text = article.targetGoal.replaceFirstChar { it.titlecase() },
                            backgroundColor = green.copy(alpha = 0.2f),
                            textColor = green
                        )
                        TagChip(
                            text = article.targetDietType.replaceFirstChar { it.titlecase() },
                            backgroundColor = Color.Blue.copy(alpha = 0.2f),
                            textColor = Color.Blue
                        )
                    }
                }

                // Kolom untuk Tombol Aksi (Edit & Delete)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Beri sedikit jarak
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Color.Blue.copy(alpha = 0.15f),
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
                        onClick = { showDeleteDialog = true }, // Tampilkan dialog konfirmasi
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Color.Red.copy(alpha = 0.15f),
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

            // Konten artikel (ringkasan)
            Text(
                text = article.content,
                fontSize = 14.sp,
                fontFamily = OpenSans,
                color = Color.Gray,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata (Penulis & Tanggal)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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

    // Dialog Konfirmasi Hapus
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
                        onDeleteClick() // Panggil callback delete
                        showDeleteDialog = false // Tutup dialog
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
            .background(backgroundColor, RoundedCornerShape(12.dp)) // Memberi warna latar dan sudut melengkung
            .padding(horizontal = 10.dp, vertical = 5.dp), // Memberi ruang di dalam chip
        contentAlignment = Alignment.Center // Memastikan teks berada di tengah
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Medium,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditArticleBottomSheet(
    articleToEdit: HealthArticle?, // Menerima artikel yang akan diedit (bisa null untuk mode Add)
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, goal: String, diet: String, createdBy: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // State untuk setiap field input, diinisialisasi dengan data dari articleToEdit jika ada
    var title by remember { mutableStateOf(articleToEdit?.title ?: "") }
    var content by remember { mutableStateOf(articleToEdit?.content ?: "") }
    var targetGoal by remember { mutableStateOf(articleToEdit?.targetGoal ?: "diet") }
    var targetDietType by remember { mutableStateOf(articleToEdit?.targetDietType ?: "free") }
    var createdBy by remember { mutableStateOf(articleToEdit?.createdBy ?: "Admin") } // Default "Admin"

    // State untuk mengontrol dropdown
    var showGoalDropdown by remember { mutableStateOf(false) }
    var showDietDropdown by remember { mutableStateOf(false) }

    val isEditing = articleToEdit != null
    val green = colorResource(R.color.green)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()) // Membuat konten bisa di-scroll jika tidak muat
        ) {
            // Judul BottomSheet
            Text(
                text = if (isEditing) "Edit Article" else "Add New Article",
                fontSize = 20.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Input field untuk Judul
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title", fontFamily = OpenSans) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = green,
                    focusedLabelColor = green
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input field untuk Konten
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content", fontFamily = OpenSans) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp), // Beri tinggi lebih untuk konten
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = green,
                    focusedLabelColor = green
                ),
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown untuk Target Goal
            Box {
                OutlinedTextField(
                    value = targetGoal.replaceFirstChar { it.titlecase() },
                    onValueChange = {}, // Biarkan kosong karena tidak bisa diedit langsung
                    label = { Text("Target Goal", fontFamily = OpenSans) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showGoalDropdown = true },
                    enabled = false, // Nonaktifkan agar tidak bisa diketik
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledBorderColor = Color.Gray.copy(alpha = 0.5f),
                        disabledLabelColor = Color.Gray.copy(alpha = 0.7f),
                        disabledTrailingIconColor = Color.Gray
                    )
                )
                DropdownMenu(
                    expanded = showGoalDropdown,
                    onDismissRequest = { showGoalDropdown = false }
                ) {
                    listOf("gain", "diet", "maintain").forEach { goal ->
                        DropdownMenuItem(
                            text = { Text(goal.replaceFirstChar { it.titlecase() }, fontFamily = OpenSans) },
                            onClick = {
                                targetGoal = goal
                                showGoalDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown untuk Tipe Diet
            Box {
                OutlinedTextField(
                    value = targetDietType.replaceFirstChar { it.titlecase() },
                    onValueChange = {},
                    label = { Text("Diet Type", fontFamily = OpenSans) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDietDropdown = true },
                    enabled = false,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledBorderColor = Color.Gray.copy(alpha = 0.5f),
                        disabledLabelColor = Color.Gray.copy(alpha = 0.7f),
                        disabledTrailingIconColor = Color.Gray
                    )
                )
                DropdownMenu(
                    expanded = showDietDropdown,
                    onDismissRequest = { showDietDropdown = false }
                ) {
                    listOf("vegan", "ketogenic", "low carbs", "strict calories", "free", "custom").forEach { diet ->
                        DropdownMenuItem(
                            text = { Text(diet.replaceFirstChar { it.titlecase() }, fontFamily = OpenSans) },
                            onClick = {
                                targetDietType = diet
                                showDietDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input field untuk Created By
            OutlinedTextField(
                value = createdBy,
                onValueChange = { createdBy = it },
                label = { Text("Created By", fontFamily = OpenSans) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = green,
                    focusedLabelColor = green
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Aksi (Cancel dan Save/Update)
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
                    onClick = { onSave(title, content, targetGoal, targetDietType, createdBy) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = green),
                    // Tombol aktif hanya jika field wajib terisi
                    enabled = title.isNotBlank() && content.isNotBlank() && createdBy.isNotBlank()
                ) {
                    Text(
                        if (isEditing) "Update" else "Save",
                        fontFamily = OpenSans,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp)) // Padding bawah
        }
    }
}