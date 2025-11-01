package com.example.sprint_2_kotlin.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sprint_2_kotlin.viewmodel.RatingDistributionViewModel

/**
 * ACTUALIZACIÓN DEL AdminAnalyticsDialog EXISTENTE
 * Se agrega nueva tab para Business Question #4
 */

@Composable
fun AdminAnalyticsDialog(
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("📊 Session Data", "📈 Rating Distribution")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                AdminDialogHeader(onDismiss = onDismiss)

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                // Content
                when (selectedTab) {
                    0 -> SessionDataTab()
                    1 -> RatingDistributionTab()
                }

                // Logout Button
                AdminLogoutButton(onLogout = onLogout)
            }
        }
    }
}

@Composable
private fun AdminDialogHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primaryContainer
            )
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "🔒 Admin Analytics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Confidential Data Dashboard",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

// ============================================
// TAB 1: Session Data (EXISTENTE)
// ============================================
@Composable
private fun SessionDataTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Average Session Duration by Device",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        val sessionData = listOf(
            SessionData("📱 Android", "12.5 min", Color(0xFF4CAF50)),
            SessionData("🍎 iOS", "10.8 min", Color(0xFF2196F3)),
            SessionData("🌐 Web", "8.2 min", Color(0xFFFF9800)),
            SessionData("💻 Desktop", "6.8 min", Color(0xFF9C27B0))
        )

        items(sessionData) { data ->
            SessionCard(data)
        }
    }
}

data class SessionData(val device: String, val duration: String, val color: Color)

@Composable
private fun SessionCard(data: SessionData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = data.color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = data.device,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = data.duration,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = data.color
            )
        }
    }
}

// ============================================
// TAB 2: Rating Distribution (NUEVO - BQ #4)
// ============================================
@Composable
private fun RatingDistributionTab() {
    val viewModel: RatingDistributionViewModel = viewModel()
    val data = viewModel.distributionData

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Loading State
        if (viewModel.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            return@LazyColumn
        }

        // Error State
        if (viewModel.errorMessage != null) {
            item {
                ErrorCard(
                    message = viewModel.errorMessage!!,
                    onRetry = { viewModel.refresh() }
                )
            }
            return@LazyColumn
        }

        // Header with Statistics
        item {
            Text(
                text = "📈 Rating Distribution by Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Business Question #4: User-assigned ratings analysis",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        // Global Statistics Card
        data?.let { distributionData ->
            item {
                GlobalStatisticsCard(
                    statistics = distributionData.statistics,
                    viewModel = viewModel
                )
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "📊 Category Breakdown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Category Cards
            items(distributionData.distributions) { categoryData ->
                CategoryDistributionCard(
                    data = categoryData,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun GlobalStatisticsCard(
    statistics: com.example.sprint_2_kotlin.model.data.RatingStatistics,
    viewModel: RatingDistributionViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🌍 Global Statistics",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Total Ratings",
                    value = statistics.totalRatings.toString(),
                    icon = "📊"
                )
                StatItem(
                    label = "Avg Veracity",
                    value = viewModel.formatRating(statistics.avgVeracity),
                    icon = "⭐"
                )
                StatItem(
                    label = "Avg Bias",
                    value = viewModel.formatRating(statistics.avgBias),
                    icon = "⚖️"
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            InsightRow(
                icon = "🏆",
                label = "Most Rated",
                value = statistics.mostRatedCategory
            )
            InsightRow(
                icon = "✅",
                label = "Most Credible",
                value = statistics.mostCredibleCategory
            )
            InsightRow(
                icon = "📍",
                label = "Most Biased",
                value = statistics.mostBiasedCategory
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, icon: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun InsightRow(icon: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun CategoryDistributionCard(
    data: com.example.sprint_2_kotlin.model.data.CategoryRatingDistribution,
    viewModel: RatingDistributionViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = viewModel.getCategoryColor(data.category).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = viewModel.getCategoryColor(data.category)
                )
                Surface(
                    color = viewModel.getCategoryColor(data.category).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${data.ratingCount} ratings",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Veracity Rating
            RatingBar(
                label = "Veracity",
                value = data.avgVeracityRating,
                maxValue = 5.0,
                color = viewModel.getCategoryColor(data.category),
                emoji = viewModel.getCredibilityEmoji(data.avgVeracityRating),
                viewModel = viewModel
            )

            // Political Bias Rating
            BiasBar(
                value = data.avgPoliticalBiasRating,
                color = viewModel.getCategoryColor(data.category),
                emoji = viewModel.getBiasEmoji(data.avgPoliticalBiasRating),
                viewModel = viewModel
            )

            // Detailed Distribution (collapsible)
            var expanded by remember { mutableStateOf(false) }

            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (expanded) "Hide Details" else "Show Details",
                    style = MaterialTheme.typography.bodySmall
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }

            if (expanded) {
                DetailedDistribution(data = data)
            }
        }
    }
}

@Composable
private fun RatingBar(
    label: String,
    value: Double,
    maxValue: Double,
    color: Color,
    emoji: String,
    viewModel: RatingDistributionViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$emoji $label",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${viewModel.formatRating(value)} / ${maxValue.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        LinearProgressIndicator(
            progress = (value / maxValue).toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun BiasBar(
    value: Double,
    color: Color,
    emoji: String,
    viewModel: RatingDistributionViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$emoji Political Bias",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = viewModel.formatRating(value),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Bias scale: -100 (Left) to +100 (Right), 0 = Center
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray)
        ) {
            val normalizedPosition = ((value + 100) / 200).coerceIn(0.0, 1.0)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(normalizedPosition.toFloat())
                    .background(color)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "⬅️ Left",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = "⚖️",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Right ➡️",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun DetailedDistribution(
    data: com.example.sprint_2_kotlin.model.data.CategoryRatingDistribution
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "📊 Detailed Breakdown",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Veracity Distribution:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
        DistributionRow("⭐", data.veracity1Star)
        DistributionRow("⭐⭐", data.veracity2Star)
        DistributionRow("⭐⭐⭐", data.veracity3Star)
        DistributionRow("⭐⭐⭐⭐", data.veracity4Star)
        DistributionRow("⭐⭐⭐⭐⭐", data.veracity5Star)

        Divider(modifier = Modifier.padding(vertical = 4.dp))

        Text(
            text = "Political Bias Distribution:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
        DistributionRow("⬅️ Left", data.biasLeftCount)
        DistributionRow("⚖️ Center", data.biasCenterCount)
        DistributionRow("➡️ Right", data.biasRightCount)
    }
}

@Composable
private fun DistributionRow(label: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Error Loading Data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

@Composable
private fun AdminLogoutButton(onLogout: () -> Unit) {
    Button(
        onClick = onLogout,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE53935)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ExitToApp,
            contentDescription = "Logout",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Logout & Return to Login",
            fontWeight = FontWeight.Bold
        )
    }
}