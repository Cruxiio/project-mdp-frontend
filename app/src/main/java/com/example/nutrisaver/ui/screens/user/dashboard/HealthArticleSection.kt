package com.example.nutrisaver.ui.screens.user.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrisaver.R
import com.example.nutrisaver.data.model.HealthArticle
import com.example.nutrisaver.ui.theme.OpenSans
import java.time.format.DateTimeFormatter

@Composable
fun HealthArticleSection(
    modifier: Modifier = Modifier,
    healthArticleItems: List<HealthArticle>,
    navController: NavController
) {
    Column {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Health Articles",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = OpenSans,
                color = Color.Black
            )

            if (healthArticleItems.isNotEmpty()) {
                Button(
                    onClick = {
                        navController.navigate("userhealtharticle")
                    },
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = CircleShape,
                    modifier = Modifier.height(40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        colorResource(R.color.green),
                                        colorResource(R.color.green_teal_dark)
                                    )
                                ), shape = CircleShape
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "View All",
                            fontSize = 18.sp,
                            fontFamily = OpenSans,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        if (healthArticleItems.isEmpty()) {
            Text(
                text = "No articles available at the moment.",
                fontSize = 16.sp,
                fontFamily = OpenSans,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        } else {
            val displayItems = healthArticleItems.take(3)

            displayItems.forEachIndexed { index, item ->
                HealthArticleCard(item)
                if (index < displayItems.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
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
                com.example.nutrisaver.ui.screens.user.TagChip(
                    text = article.targetGoal.capitalize(),
                    backgroundColor = green.copy(alpha = 0.2f),
                    textColor = green
                )
                com.example.nutrisaver.ui.screens.user.TagChip(
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
private fun TagChip(
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
