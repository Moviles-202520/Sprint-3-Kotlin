package com.example.sprint_2_kotlin.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Logo",
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF1A1A1A)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Punto Neutro",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { /* TODO: Notifications */ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color(0xFF1A1A1A)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .offset(x = 24.dp, y = 8.dp)
                                .background(Color.Red, CircleShape)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Guide to Identify Fake News",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Learn to detect misinformation and manipulated content",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(24.dp))
            }

            item {
                GuideTipCard(
                    icon = Icons.Default.RemoveRedEye,
                    title = "Examine the source",
                    level = "Basic",
                    description = "Verify if the website is known and reliable. Check the 'About' section to learn more about the organization."
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                GuideTipCard(
                    icon = Icons.Default.Search,
                    title = "Search multiple sources",
                    level = "Basic",
                    description = "Real news is usually reported by multiple reliable media outlets. If it only appears on one site, be suspicious."
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                GuideTipCard(
                    icon = Icons.Default.Person,
                    title = "Verify the author",
                    level = "Intermediate",
                    description = "Look up information about the journalist or author. Professionals usually have verifiable online presence.",
                    levelColor = Color(0xFFFFA726)
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                GuideTipCard(
                    icon = Icons.Default.Warning,
                    title = "Beware of sensationalist headlines",
                    level = "Basic",
                    description = "Overly emotional titles or those with ALL CAPS are usually indicators of questionable content."
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                GuideTipCard(
                    icon = Icons.Default.CheckCircle,
                    title = "Check the dates",
                    level = "Basic",
                    description = "Make sure the information is current. Sometimes old news is recycled as if it were recent."
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                GuideTipCard(
                    icon = Icons.Default.Shield,
                    title = "Use verification tools",
                    level = "Advanced",
                    description = "Use fact-checking sites like Snopes, PolitiFact, or Punto Neutro's integrated tools.",
                    levelColor = Color(0xFF1976D2)
                )
                Spacer(Modifier.height(24.dp))
            }

            item {
                WarningSigns()
                Spacer(Modifier.height(24.dp))
            }

            item {
                VerificationProcess()
                Spacer(Modifier.height(24.dp))
            }

            item {
                RecommendedTools()
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun GuideTipCard(
    icon: ImageVector,
    title: String,
    level: String,
    description: String,
    levelColor: Color = Color(0xFF757575)
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF5F5F5),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = Color(0xFF1A1A1A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = levelColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = level,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = levelColor
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF666666),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun WarningSigns() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Warning Signs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }

            Spacer(Modifier.height(16.dp))

            WarningItem("Strange URLs or many spelling errors")
            Spacer(Modifier.height(8.dp))
            WarningItem("Images that don't match the text")
            Spacer(Modifier.height(8.dp))
            WarningItem("Missing date or clearly identified author")
        }
    }
}

@Composable
fun WarningItem(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .offset(y = 7.dp)
                .background(Color(0xFFE53935), CircleShape)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF333333),
            lineHeight = 20.sp
        )
    }
}

@Composable
fun VerificationProcess() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Process",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Verification Process",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }

            Spacer(Modifier.height(16.dp))

            ProcessStep(
                number = "1",
                title = "First reading",
                description = "Read the title and first paragraph. Does it seem credible to you?"
            )
            Spacer(Modifier.height(12.dp))

            ProcessStep(
                number = "2",
                title = "Verify the source",
                description = "Research who published the information and their reputation."
            )
            Spacer(Modifier.height(12.dp))

            ProcessStep(
                number = "3",
                title = "Look for corroboration",
                description = "Search for the same news in other reliable media outlets."
            )
            Spacer(Modifier.height(12.dp))

            ProcessStep(
                number = "4",
                title = "Analyze the images",
                description = "Use reverse image search to verify their origin."
            )
            Spacer(Modifier.height(12.dp))

            ProcessStep(
                number = "5",
                title = "Consult experts",
                description = "Look for opinions from specialists on the topic covered."
            )
        }
    }
}

@Composable
fun ProcessStep(number: String, title: String, description: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color(0xFF666666),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun RecommendedTools() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = "Tools",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Recommended Tools",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }

            Spacer(Modifier.height(16.dp))

            ToolCard(
                title = "Punto Neutro AI",
                description = "Our integrated automatic verification system.",
                icon = Icons.Default.SmartToy
            )

            Spacer(Modifier.height(12.dp))

            ToolCard(
                title = "Reverse Search",
                description = "Verify the origin and authenticity of images.",
                icon = Icons.Default.ImageSearch
            )
        }
    }
}

@Composable
fun ToolCard(title: String, description: String, icon: ImageVector) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF8F8F8),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF1A1A1A),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}