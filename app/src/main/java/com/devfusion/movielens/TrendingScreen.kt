package com.devfusion.movielens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TrendingScreen(modifier: Modifier = Modifier, viewModel: TrendingViewModel = viewModel()) {
    // collectAsState and getValue imports are required
    val uiState by viewModel.uiState.collectAsState()
    val platforms = listOf("Netflix", "Amazon Prime", "MX Player")

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Trending", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            platforms.forEach { p ->
                FilterChip(
                    selected = p == uiState.platform,
                    onClick = { viewModel.loadForPlatform(p) },
                    label = { Text(p) }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (uiState.loading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // uiState.items must be List<Movie>
                items(uiState.items) { movie ->
                    TrendingRow(movie)
                }
            }
        }
    }
}

@Composable
fun TrendingRow(movie: Movie) {
    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder poster box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(movie.title, style = MaterialTheme.typography.bodyLarge)
                Text("${movie.genre} • ${movie.platform}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
