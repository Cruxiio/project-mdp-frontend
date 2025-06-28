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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.nutrisaver.R
import com.example.nutrisaver.ui.theme.OpenSans
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data class representing user feedback based on your database model
data class UserFeedbackData(
    val id: Int,
    val userId: Int,
    val message: String,
    val status: FeedbackStatus,
    val adminResponse: String? = null,
    val respondedBy: Int? = null,
    val createdAt: Date,
    val respondedAt: Date? = null
)

enum class FeedbackStatus {
    PENDING,
    REVIEWED,
    DONE
}

// Generate dummy feedback data for demonstration
fun generateDummyFeedback(): List<UserFeedbackData> {
    return listOf(
        UserFeedbackData(
            id = 1,
            userId = 1,
            message = "The app is great but I would love to see more recipe suggestions based on my dietary preferences.",
            status = FeedbackStatus.DONE,
            adminResponse = "Thank you for your feedback! We're working on implementing personalized recipe recommendations in the next update.",
            respondedBy = 1,
            createdAt = Date(System.currentTimeMillis() - 86400000 * 3), // 3 days ago
            respondedAt = Date(System.currentTimeMillis() - 86400000 * 1) // 1 day ago
        ),
        UserFeedbackData(
            id = 2,
            userId = 1,
            message = "Sometimes the barcode scanner doesn't work properly in low light conditions.",
            status = FeedbackStatus.REVIEWED,
            adminResponse = "We've noted this issue and our development team is working on improving the scanner's performance in various lighting conditions.",
            respondedBy = 2,
            createdAt = Date(System.currentTimeMillis() - 86400000 * 5), // 5 days ago
            respondedAt = Date(System.currentTimeMillis() - 86400000 * 2) // 2 days ago
        ),
        UserFeedbackData(
            id = 3,
            userId = 1,
            message = "Could you add a feature to track water intake? It would be really helpful for my daily nutrition goals.",
            status = FeedbackStatus.PENDING,
            createdAt = Date(System.currentTimeMillis() - 86400000 * 1), // 1 day ago
        ),
        UserFeedbackData(
            id = 4,
            userId = 1,
            message = "Love the new UI update! Much more intuitive than before.",
            status = FeedbackStatus.DONE,
            adminResponse = "Thank you so much for the positive feedback! We're glad you're enjoying the new interface.",
            respondedBy = 1,
            createdAt = Date(System.currentTimeMillis() - 86400000 * 7), // 7 days ago
            respondedAt = Date(System.currentTimeMillis() - 86400000 * 6) // 6 days ago
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

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
                        // Handle feedback submission here
                        // TODO: Add API call to submit feedback
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
    navController: NavController
) {
    val backgroundGradient = Brush.verticalGradient(
        listOf(
            colorResource(id = R.color.bg2_1),
            colorResource(id = R.color.bg2_2)
        )
    )

    val feedbackList = remember { generateDummyFeedback() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient),
    ) {
        if (feedbackList.isEmpty()) {
            // Empty state
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
                    text = "No feedback yet",
                    fontSize = 18.sp,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Share your thoughts and suggestions with us!",
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
                        text = "Your feedback helps us improve!",
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

@Composable
private fun FeedbackCard(feedback: UserFeedbackData) {
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
                    text = "Your Message:",
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
                                    text = "Admin Response:",
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
private fun StatusChip(status: FeedbackStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        FeedbackStatus.PENDING -> Tuple3(
            colorResource(R.color.feedback_pending),
            Color.White,
            "Pending"
        )
        FeedbackStatus.REVIEWED -> Tuple3(
            colorResource(R.color.feedback_reviewed),
            Color.White,
            "Reviewed"
        )
        FeedbackStatus.DONE -> Tuple3(
            colorResource(R.color.feedback_done),
            Color.White,
            "Completed"
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

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}