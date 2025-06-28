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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState // Import for LiveData observation
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
import androidx.lifecycle.viewmodel.compose.viewModel // Import for viewModel()
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.nutrisaver.R
import com.example.nutrisaver.data.model.UserFeedback
import com.example.nutrisaver.data.model.UserFeedback as DomainUserFeedback // Alias to avoid name conflict
import com.example.nutrisaver.ui.theme.OpenSans
import com.example.nutrisaver.viewmodel.FeedbackState
import com.example.nutrisaver.viewmodel.FeedbackViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant // For converting Instant to Date for UserFeedbackData
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    feedbackViewModel: FeedbackViewModel = viewModel()
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    val usersFeedbacks by feedbackViewModel.usersFeedbacks.observeAsState(initial = emptyList())
    val feedbackState by feedbackViewModel.feedbackState.observeAsState(initial = FeedbackState.Idle)

    // Trigger data fetch when the composable enters the composition
    LaunchedEffect(Unit) {
        feedbackViewModel.fetchUsersFeedbacks()
    }

    Scaffold(
        topBar = { TopBar(onBackClick = { navController.popBackStack() }) },
        floatingActionButton = {
            val greenGradient = Brush.horizontalGradient(listOf(colorResource(id = R.color.green), colorResource(id = R.color.green_teal_dark)))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(brush = greenGradient, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                LargeFloatingActionButton(
                    onClick = {
                        showBottomSheet = true
                    },
                    shape = CircleShape,
                    containerColor = Color.Transparent, // keep it transparent
                    elevation = FloatingActionButtonDefaults.elevation(0.dp), // disable shadow
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(40.dp))
                }
            }
        }
    ) { innerPadding ->
        FeedbackContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            feedbackList = usersFeedbacks,
            feedbackState = feedbackState, // Pass the state
            onRefresh = { feedbackViewModel.fetchUsersFeedbacks() } // Provide refresh action
        )

        // Bottom Sheet for adding feedback
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
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
                AddFeedbackBottomSheet(
                    onSubmit = { feedbackText ->
                        // Create a UserFeedback domain object with current data
                        // Note: ID, userId, createdAt are usually set by the backend for new feedback.
                        // We provide minimal data required for submission.
                        val newFeedback = DomainUserFeedback(
                            id = 0, // Placeholder, backend will assign actual ID
                            userId = 0, // Placeholder, backend will use auth.currentUser.uuid
                            message = feedbackText,
                            status = "pending", // Default status for new feedback
                            createdAt = Instant.now() // Timestamp for local creation
                        )
                        feedbackViewModel.addFeedback(newFeedback)
                        scope.launch {
                            bottomSheetState.hide()
                            showBottomSheet = false
                        }
                    },
                    onCancel = {
                        scope.launch {
                            bottomSheetState.hide()
                            showBottomSheet = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AddFeedbackBottomSheet(
    onSubmit: (String) -> Unit,
    onCancel: () -> Unit
) {
    var feedbackText by remember { mutableStateOf("") }
    val green = colorResource(id = R.color.green)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .imePadding() // Adjusts for keyboard
    ) {
        // Title
        Text(
            text = "Share Your Feedback",
            fontSize = 20.sp,
            fontFamily = OpenSans,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Help us improve by sharing your thoughts and suggestions",
            fontSize = 14.sp,
            fontFamily = OpenSans,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Feedback input field
        OutlinedTextField(
            value = feedbackText,
            onValueChange = { feedbackText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = {
                Text(
                    text = "Tell us what you think...",
                    fontFamily = OpenSans,
                    color = Color.Gray
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = green,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = green
            ),
            shape = RoundedCornerShape(12.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cancel button
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

            // Submit button
            Button(
                onClick = {
                    if (feedbackText.isNotBlank()) {
                        onSubmit(feedbackText.trim())
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = feedbackText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                    disabledContentColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Submit",
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Add bottom padding for better spacing
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val green = colorResource(id = R.color.green)
    val greenTealDark = colorResource(id = R.color.green_teal_dark)
    val greenGradient = Brush.horizontalGradient(listOf(green, greenTealDark))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(greenGradient)
            .padding(
                top = 32.dp,
                bottom = 12.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
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
                text = "My Feedback",
                fontSize = 20.sp,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun FeedbackContent(
    modifier: Modifier,
    navController: NavController,
    feedbackList: List<UserFeedback>,
    feedbackState: FeedbackState,
    onRefresh: () -> Unit
) {
    val backgroundGradient = Brush.verticalGradient(
        listOf(
            colorResource(id = R.color.bg2_1),
            colorResource(id = R.color.bg2_2)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient),
    ) {
        when (feedbackState) {
            FeedbackState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = colorResource(id = R.color.green))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Memuat feedback...",
                        fontFamily = OpenSans,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
            is FeedbackState.Error -> {
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
                        text = feedbackState.message,
                        fontSize = 16.sp,
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.Medium,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Refresh")
                    }
                }
            }
            FeedbackState.Idle, FeedbackState.Success -> {
                if (feedbackList.isEmpty()) {
                    // Empty state when there's no feedback after loading successfully
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "No Feedback",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Belum ada feedback",
                            fontSize = 18.sp,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bagikan pikiran dan saran Anda kepada kami!",
                            fontSize = 14.sp,
                            fontFamily = OpenSans,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = "Feedback Anda membantu kami berkembang!",
                                fontSize = 16.sp,
                                fontFamily = OpenSans,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        items(feedbackList) { feedback ->
                            FeedbackCard(feedback = feedback)
                        }

                        // Add some bottom padding for the FAB
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedbackCard(feedback: UserFeedback) {
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
                // Header with status and date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusChip(status = feedback.status)
                    Text(
                        text = formatDate(feedback.createdAt),
                        fontSize = 12.sp,
                        fontFamily = OpenSans,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // User message
                Text(
                    text = "Pesan Anda:",
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

                // Admin response (if available)
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
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Respon Admin:",
                                    fontSize = 14.sp,
                                    fontFamily = OpenSans,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(id = R.color.green)
                                )
                                if (feedback.respondedAt != null) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = formatDate(feedback.respondedAt),
                                        fontSize = 12.sp,
                                        fontFamily = OpenSans,
                                        color = Color.Gray
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
            }
        }
    }
}
@Composable
private fun StatusChip(status: String) {
    val (backgroundColor, textColor, text) = when (status.lowercase(Locale.ROOT)) { // Convert to lowercase for case-insensitive comparison
        "pending" -> Tuple3(
            colorResource(R.color.feedback_pending),
            Color.White,
            "Pending"
        )
        "reviewed" -> Tuple3(
            colorResource(R.color.feedback_reviewed),
            Color.White,
            "Ditinjau"
        )
        "done" -> Tuple3(
            colorResource(R.color.feedback_done),
            Color.White,
            "Selesai"
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

// Helper data class for multiple return values
private data class Tuple3<A, B, C>(
    val first: A,
    val second: B,
    val third: C,
)

private fun formatDate(date: Instant?): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}