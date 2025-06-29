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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.data.model.UserFeedback
import com.example.nutrisaver.ui.navbar.AdminBottomNavBar
import com.example.nutrisaver.ui.theme.OpenSans
import com.example.nutrisaver.viewmodel.AdminFeedbackViewModel
import com.example.nutrisaver.viewmodel.AdminFeedbackState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun AdminFeedbackScreen(
    navController: NavController,
    adminFeedbackViewModel: AdminFeedbackViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        adminFeedbackViewModel.init()
    }

    Scaffold(
        bottomBar = {
            AdminBottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        AdminFeedbackContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            adminFeedbackViewModel = adminFeedbackViewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminFeedbackContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    adminFeedbackViewModel: AdminFeedbackViewModel
) {
    val background = colorResource(id = R.color.bg2_1)
    val background2 = colorResource(id = R.color.bg2_2)
    val backgroundGradient = Brush.verticalGradient(listOf(background, background2))
    val green = colorResource(id = R.color.green)

    val feedbackList by adminFeedbackViewModel.feedbacks.observeAsState(emptyList())
    val adminFeedbackState by adminFeedbackViewModel.feedbackState.observeAsState(AdminFeedbackState.Idle) // Corrected: Use AdminFeedbackState directly

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var showFilterDropdown by remember { mutableStateOf(false) }
    var showResponseBottomSheet by remember { mutableStateOf(false) }
    var selectedFeedback by remember { mutableStateOf<UserFeedback?>(null) } // Corrected: Use UserFeedback

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val filteredFeedback = remember(searchQuery, selectedFilter, feedbackList) {
        val filtered = if (searchQuery.isBlank()) {
            feedbackList
        } else {
            feedbackList.filter { feedback ->
                (feedback.userName?.contains(searchQuery, ignoreCase = true) ?: false) ||
                        (feedback.userEmail?.contains(searchQuery, ignoreCase = true) ?: false) ||
                        feedback.message.contains(searchQuery, ignoreCase = true)
            }
        }

        when (selectedFilter) {
            "Pending" -> filtered.filter { it.status.lowercase(Locale.ROOT) == "pending" }
            "Done" -> filtered.filter { it.status.lowercase(Locale.ROOT) == "done" } // Corrected: Match "Done" filter with "done" status
            else -> filtered
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        when (adminFeedbackState) {
            AdminFeedbackState.Loading -> { // Corrected: Use AdminFeedbackState directly
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = colorResource(id = R.color.green))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading feedback...",
                        fontFamily = OpenSans,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
            is AdminFeedbackState.Error -> { // Corrected: Use AdminFeedbackState directly
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Error",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (adminFeedbackState as AdminFeedbackState.Error).message, // Corrected: Use AdminFeedbackState directly
                        fontSize = 16.sp,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Medium,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { adminFeedbackViewModel.getAllFeedback() }) { // Corrected: Call ViewModel refresh
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Refresh")
                    }
                }
            }
            AdminFeedbackState.Idle, AdminFeedbackState.Success -> { // Corrected: Use AdminFeedbackState directly
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Feedback Management",
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Search and Filter Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Search TextField
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(25.dp)),
                            placeholder = {
                                Text(
                                    text = "Search feedback...",
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
                                focusedBorderColor = green,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            singleLine = true
                        )

                        // Filter Dropdown
                        Box {
                            Card(
                                modifier = Modifier
                                    .clickable { showFilterDropdown = !showFilterDropdown },
                                shape = RoundedCornerShape(25.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedFilter,
                                        fontSize = 14.sp,
                                        fontFamily = OpenSans,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Filter",
                                        tint = Color.Gray
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showFilterDropdown,
                                onDismissRequest = { showFilterDropdown = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                listOf("All", "Pending", "Done").forEach { filter -> // Corrected: "Done" instead of "Completed"
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = filter,
                                                fontFamily = OpenSans,
                                                color = if (filter == selectedFilter) green else Color.Black,
                                                fontWeight = if (filter == selectedFilter) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            selectedFilter = filter
                                            showFilterDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Feedback Statistics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Total",
                            count = feedbackList.size,
                            color = colorResource(R.color.feedback_reviewed),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Pending",
                            count = feedbackList.count { it.status.lowercase(Locale.ROOT) == "pending" },
                            color = colorResource(R.color.feedback_pending),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Done", // Changed from "Completed" to "Done" for consistency
                            count = feedbackList.count { it.status.lowercase(Locale.ROOT) == "done" },
                            color = colorResource(R.color.feedback_done),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Feedback List
                    if (filteredFeedback.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "No Feedback",
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No feedback found",
                                    fontSize = 16.sp,
                                    fontFamily = OpenSans,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredFeedback) { feedback ->
                                AdminFeedbackCard(
                                    feedback = feedback,
                                    onRespondClick = {
                                        selectedFeedback = feedback
                                        showResponseBottomSheet = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Response Bottom Sheet
        if (showResponseBottomSheet && selectedFeedback != null) {
            ModalBottomSheet(
                onDismissRequest = { showResponseBottomSheet = false },
                sheetState = bottomSheetState,
                containerColor = colorResource(id = R.color.bg2_1),
                contentColor = Color.Black,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                Color.Gray.copy(alpha = 0.5f),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            ) {
                ResponseBottomSheet(
                    feedback = selectedFeedback!!,
                    onSubmit = { responseText, newStatusString -> // Corrected: Added newStatusString
                        if (selectedFeedback != null) {
                            adminFeedbackViewModel.respondToFeedback(
                                feedbackId = selectedFeedback!!.id,
                                adminResponse = responseText,
                            )
                        }
                        scope.launch {
                            bottomSheetState.hide()
                            showResponseBottomSheet = false
                            selectedFeedback = null
                        }
                    },
                    onCancel = {
                        scope.launch {
                            bottomSheetState.hide()
                            showResponseBottomSheet = false
                            selectedFeedback = null
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 24.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 12.sp,
                fontFamily = OpenSans,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResponseBottomSheet(
    feedback: UserFeedback, // Corrected: Use UserFeedback
    onSubmit: (responseText: String, newStatus: String) -> Unit, // Corrected: Added newStatus
    onCancel: () -> Unit
) {
    var responseText by remember { mutableStateOf(feedback.adminResponse ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .imePadding()
    ) {
        // Title
        Text(
            text = "Respond to Feedback",
            fontSize = 20.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // User info
        Text(
            text = "From: ${feedback.userName} (${feedback.userEmail})",
            fontSize = 14.sp,
            fontFamily = OpenSans,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Original message
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "User Message:",
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = feedback.message,
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    color = Color.Black,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Response input
        Text(
            text = "Your Response:",
            fontSize = 14.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = responseText,
            onValueChange = { responseText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = {
                Text(
                    text = "Type your response here...",
                    fontFamily = OpenSans,
                    color = Color.Gray
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(id = R.color.green),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = colorResource(id = R.color.green)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray.copy(alpha = 0.2f),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = {
                    if (responseText.isNotBlank()) {
                        onSubmit(responseText.trim(), "done")
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = responseText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.green),
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                    disabledContentColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Submit Response",
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun AdminFeedbackCard(
    feedback: UserFeedback,
    onRespondClick: () -> Unit
) {
    val itemGradient = Brush.verticalGradient(
        listOf(
            colorResource(R.color.item_1),
            colorResource(R.color.item_2)
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(itemGradient)
                .padding(16.dp)
        ) {
            Column {
                // Header with user info and status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        feedback.userName?.let {
                            Text(
                                text = it,
                                fontSize = 16.sp,
                                fontFamily = OpenSans,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                        feedback.userEmail?.let {
                            Text(
                                text = it,
                                fontSize = 12.sp,
                                fontFamily = OpenSans,
                                color = Color.DarkGray
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        AdminStatusChip(status = feedback.status)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatDate(feedback.createdAt),
                            fontSize = 12.sp,
                            fontFamily = OpenSans,
                            color = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // User message
                Text(
                    text = "User Message:",
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = feedback.message,
                    fontSize = 14.sp,
                    fontFamily = OpenSans,
                    color = Color.Black,
                    lineHeight = 20.sp
                )

                if (feedback.adminResponse != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Response:",
                                    fontSize = 14.sp,
                                    fontFamily = OpenSans,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(id = R.color.green)
                                )
                                if (feedback.respondedAt != null) {
                                    Text(
                                        text = formatDate(feedback.respondedAt),
                                        fontSize = 12.sp,
                                        fontFamily = OpenSans,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = feedback.adminResponse,
                                fontSize = 14.sp,
                                fontFamily = OpenSans,
                                color = Color.Black,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                // Action button
                if(feedback.adminResponse == null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRespondClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.green)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Respond",
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

            }
        }
    }
}

@Composable
private fun AdminStatusChip(status: String) {
    val (backgroundColor, textColor, text) = when (status.lowercase(Locale.ROOT)) {
        "pending" -> Tuple3(
            colorResource(R.color.feedback_pending),
            Color.White,
            "Pending"
        )
        "reviewed" -> Tuple3(
            colorResource(R.color.feedback_reviewed),
            Color.White,
            "Reviewed"
        )
        "done" -> Tuple3(
            colorResource(R.color.feedback_done),
            Color.White,
            "Done"
        )
        else -> Tuple3(
            Color.LightGray,
            Color.Black,
            "Unknown"
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

private data class Tuple3<A, B, C>(
    val first: A,
    val second: B,
    val third: C,
)

// Helper function to format Instant to String
private fun formatDate(date: Instant?): String {
    if (date == null) {
        return "N/A"
    }
    val javaUtilDate = Date.from(date)
    val formatter = SimpleDateFormat("MMM dd,yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(javaUtilDate)
}