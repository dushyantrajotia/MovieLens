package com.devfusion.movielens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: RecommendationViewModel = viewModel()
) {
    // collectAsState + getValue imports are required (added above)
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (uiState.loading) {
                // Loading UI
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading recommendations...", style = MaterialTheme.typography.bodyLarge)
                return@Column
            }

            Text(text = "Hi ${uiState.userName}!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Recommended for you", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.recommendations.isEmpty()) {
                Text("We don't have recommendations yet. Watch some movies to get personalized picks.")
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.recommendations) { movie ->
                        MovieCard(movie = movie, onClick = {
                            // placeholder click — replace with navigation
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Recently watched", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Render watch history (ensure HomeUiState.watchHistory exists)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.watchHistory.forEach { m ->
                    Text("${m.title} • ${m.platform} • ${m.genre}", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        // Chatbot FAB anchored bottom-end
        FloatingActionButton(
            onClick = {
                // TODO: integrate Chatbase here.
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Email, contentDescription = "Chatbot")
        }
    }
}

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(width = 180.dp, height = 240.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Placeholder for poster image — replace with Coil AsyncImage
            Box(
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) { Text("Poster", style = MaterialTheme.typography.bodyMedium) }

            Spacer(modifier = Modifier.height(8.dp))
            Text(movie.title, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${movie.platform} • ${movie.genre}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
