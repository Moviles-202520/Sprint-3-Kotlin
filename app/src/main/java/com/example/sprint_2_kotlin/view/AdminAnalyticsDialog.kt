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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// Datos de ejemplo para las sesiones
data class DeviceSessionData(
    val deviceType: String,
    val icon: String,
    val avgDuration: Double, // en minutos
    val totalSessions: Int,
    val percentage: Float,
    val color: Color
)

fun getSessionData(): List<DeviceSessionData> {
    return listOf(
        DeviceSessionData(
            deviceType = "Android Devices",
            icon = "📱",
            avgDuration = 12.5,
            totalSessions = 2847,
            percentage = 0.85f,
            color = Color(0xFF4CAF50)
        ),
        DeviceSessionData(
            deviceType = "Web Platform",
            icon = "🌐",
            avgDuration = 8.2,
            totalSessions = 1203,
            percentage = 0.65f,
            color = Color(0xFF2196F3)
        ),
        DeviceSessionData(
            deviceType = "Desktop",
            icon = "💻",
            avgDuration = 6.8,
            totalSessions = 456,
            percentage = 0.50f,
            color = Color(0xFF9C27B0)
        ),
        DeviceSessionData(
            deviceType = "iOS Devices",
            icon = "🍎",
            avgDuration = 10.8,
            totalSessions = 1534,
            percentage = 0.75f,
            color = Color(0xFFFF9800)
        )
    )
}

@Composable
fun AdminAnalyticsDialog(
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1A1A1A)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Admin Analytics",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title
                    item {
                        Column {
                            Text(
                                text = "Average Session Duration",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "by Device Type & Platform",
                                fontSize = 14.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }

                    // Session Cards
                    items(getSessionData()) { data ->
                        SessionDataCard(data)
                    }

                    // Summary Stats
                    item {
                        Spacer(Modifier.height(8.dp))
                        SummaryStats()
                    }
                }

                // Bottom Actions
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF5F5F5),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Logout & Return to Login",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Close Panel")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SessionDataCard(data: DeviceSessionData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = data.icon,
                        fontSize = 24.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = data.deviceType,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = "${data.totalSessions} sessions",
                            fontSize = 12.sp,
                            color = Color(0xFF888888)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = data.color.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${data.avgDuration} min",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = data.color
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Progress Bar
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(data.percentage)
                            .fillMaxHeight()
                            .background(data.color, RoundedCornerShape(4.dp))
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${(data.percentage * 100).toInt()}% of max duration (15 min)",
                    fontSize = 11.sp,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}

@Composable
fun SummaryStats() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Insights,
                    contentDescription = "Insights",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Key Insights",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }

            Spacer(Modifier.height(12.dp))

            SummaryItem("📱 Most Active Platform:", "Android (2,847 sessions)")
            Spacer(Modifier.height(6.dp))
            SummaryItem("⏱️ Overall Avg Duration:", "10.2 minutes")
            Spacer(Modifier.height(6.dp))
            SummaryItem("🔥 Peak Usage Time:", "2:00 PM - 4:00 PM")
            Spacer(Modifier.height(6.dp))
            SummaryItem("📊 Total Sessions (Today):", "6,040 sessions")
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF1976D2),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = Color(0xFF1976D2),
            fontWeight = FontWeight.Bold
        )
    }
}

// Password Dialog
@Composable
fun AdminPasswordDialog(
    onDismiss: () -> Unit,
    onPasswordCorrect: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val correctPassword = "admin123"

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock",
                tint = Color(0xFF1A1A1A),
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Admin Access Required",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text("Enter admin password to view analytics:")
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        showError = false
                    },
                    label = { Text("Password") },
                    singleLine = true,
                    isError = showError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (showError) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Incorrect password. Try 'admin123'",
                        color = Color(0xFFE53935),
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (password == correctPassword) {
                        onPasswordCorrect()
                    } else {
                        showError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            ) {
                Text("Access")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}