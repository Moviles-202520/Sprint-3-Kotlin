package com.example.sprint_2_kotlin.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.sprint_2_kotlin.viewmodel.NewsItemDetailViewModel
import okhttp3.internal.userAgent
import utils.NetworkMonitor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsItemDetailScreen(
    userProfileId: Int,
    newsItemId: Int,  // 👈 Receive ID instead of full object
    onBackClick: () -> Unit = {},  // 👈 Add back navigation callback
    viewModel: NewsItemDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val networkMonitor = remember { NetworkMonitor(context) }
    // Load the news item using the ID
    LaunchedEffect(newsItemId) {
        viewModel.loadNewsItemById(newsItemId)
    }
    // Launches the listener of the internet connection
    LaunchedEffect(Unit) {
        viewModel.startNetworkObserver(networkMonitor)
    }

    val currentItem by viewModel.newsItem.collectAsState()
    val ratings by viewModel.ratings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to News Feed"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        currentItem?.let { item ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Image
                    Image(
                        painter = rememberAsyncImagePainter(item.image_url),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category and reliability
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Category ID: ${item.category_id}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        ReliabilityIndicator(item.average_reliability_score)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Meta info
                    Text(
                        text = "By ${item.author_type} at ${item.author_institution}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Published ${item.days_since} days ago • ${item.total_ratings} total ratings",
                        style = MaterialTheme.typography.labelSmall
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Long description
                    Text(
                        text = item.long_description.ifEmpty { item.short_description },
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (item.original_source_url.isNotEmpty()) {
                        Text(
                            text = "Original source: ${item.original_source_url}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Box() {
                        CommentSection(userProfileId = userProfileId, newsItemId = newsItemId)


                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Ratings & Comments",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))



                }

                // Ratings list
                items(ratings) { rating ->
                    RatingItemCard(rating)
                    Spacer(modifier = Modifier.height(10.dp))
                }




                item { Spacer(modifier = Modifier.height(32.dp)) }
            }

        } ?: run {
            // Loading state while fetching news item
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}


@Composable
fun CommentSection(viewModel: NewsItemDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),userProfileId: Int,
                   newsItemId: Int) {
    var isExpanded by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var rating by remember {mutableStateOf<Float>(value = 0.5f) }

    Column(Modifier.padding(16.dp)) {

        Button(onClick = { isExpanded = !isExpanded }) {
            Text(if (isExpanded) "Cancelar" else "Agregar comentario")
        }

        AnimatedVisibility(isExpanded) {
            Card(Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)) {
                Column(Modifier.padding(16.dp)) {


                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Comentario") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )



                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Valor: ${"%.2f".format(rating)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = rating,
                        onValueChange = { rating = it },
                        valueRange = 0f..1f,
                        steps = 99, // Opcional: 0.1 de incremento
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.addComment(
                                userProfileId = userProfileId,
                                newsItemId = newsItemId,
                                comment = comment,
                                onSuccess = {
                                    message = "Comentario enviado ✅"
                                    isExpanded = false
                                    name = ""
                                    comment = ""
                                },
                                onError = { message = "Error al enviar: ${it.message}" },
                                rating = rating.toDouble()
                            )
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Enviar")
                    }

                    message?.let {
                        Text(it, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }

      }
    }
}





