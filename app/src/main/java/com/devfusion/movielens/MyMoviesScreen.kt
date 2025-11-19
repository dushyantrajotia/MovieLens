package com.devfusion.movielens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMoviesScreen(
    modifier: Modifier = Modifier,
    viewModel: MyMoviesViewModel = viewModel()
) {
    // NOTE: do NOT use rememberSaveable with SnapshotStateList
    // The viewModel exposes mutableStateListOf, which Compose will observe.

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // For demo: add a dummy "watched" movie. Replace with a dialog/input in real app.
                val id = UUID.randomUUID().toString()
                val sample = Movie(id, "Manual Add ${viewModel.watched.size + 1}", "Unknown", "Manual")
                viewModel.addWatched(sample)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add movie")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)) {

            Text("To Be Watched", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.toBeWatched.isEmpty()) {
                Text("No movies added to your to-be-watched list.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.toBeWatched) { movie ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // mark as watched when clicked
                                    viewModel.markAsWatched(movie)
                                }
                                .padding(8.dp)
                        ) {
                            Text(movie.title, modifier = Modifier.weight(1f))
                            Text("Tap to mark watched", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            Text("My Movies (Watched)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.watched.isEmpty()) {
                Text("You haven't marked any movies as watched yet.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.watched) { movie ->
                        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                            Text(movie.title, modifier = Modifier.weight(1f))
                            Text(movie.platform ?: "", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
