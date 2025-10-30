package com.example.sprint_2_kotlin.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sprint_2_kotlin.model.data.RatingItem
import androidx.compose.ui.graphics.Color

@Composable
fun RatingItemCard(rating: RatingItem) {
    val percentage = (rating.assigned_reliability_score * 100).toInt()
    val color = when {
        percentage >= 75 -> Color(0xFF4CAF50)
        percentage >= 50 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Reliability: $percentage%",
                    color = color,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = rating.rating_date,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = rating.comment_text.ifEmpty { "No comment provided." },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
