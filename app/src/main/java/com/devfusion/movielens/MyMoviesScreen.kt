package com.devfusion.movielens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import coil.compose.AsyncImage
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMoviesScreen(
    modifier: Modifier = Modifier,
    viewModel: MyMoviesViewModel = viewModel()
) {
    // Dialog visibility state
    var showAddDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Collect state from ViewModel
    val watchedMovies by viewModel.watchedMovies.collectAsState()
    val watchlistMovies by viewModel.watchlistMovies.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserMovies()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add movie")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("To Be Watched", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (watchlistMovies.isEmpty()) {
                Text(
                    "No movies added to your to-be-watched list.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(watchlistMovies, key = { it.movieId }) { userMovie ->
                        MovieListItem(
                            userMovie = userMovie,
                            showWatchedButton = true,
                            onMarkAsWatched = {
                                coroutineScope.launch {
                                    viewModel.markAsWatched(userMovie)
                                }
                            },
                            onRemove = {
                                coroutineScope.launch {
                                    viewModel.removeFromWatchlist(userMovie.movieId)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("My Movies (Watched)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (watchedMovies.isEmpty()) {
                Text(
                    "You haven't marked any movies as watched yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(watchedMovies, key = { it.movieId }) { userMovie ->
                        MovieListItem(
                            userMovie = userMovie,
                            showWatchedButton = false,
                            onRemove = {
                                coroutineScope.launch {
                                    viewModel.removeFromWatched(userMovie.movieId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        MovieSearchDialog(
            onDismiss = { showAddDialog = false },
            onMovieSelected = { movie, addToWatched ->
                coroutineScope.launch {
                    if (addToWatched) {
                        viewModel.addToWatched(movie)
                    } else {
                        viewModel.addToWatchlist(movie)
                    }
                }
                showAddDialog = false
            }
        )
    }
}

// New reusable MovieListItem composable
@Composable
fun MovieListItem(
    userMovie: UserMovie,
    showWatchedButton: Boolean = false,
    onMarkAsWatched: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Movie Poster
            Box(
                modifier = Modifier.size(70.dp),
                contentAlignment = Alignment.Center
            ) {
                if (userMovie.posterPath != null && userMovie.posterPath.isNotBlank()) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w200${userMovie.posterPath}",
                        contentDescription = "${userMovie.title} poster",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.small)
                    )
                } else {
                    // Fallback when no poster
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No\nImage",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Movie Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = userMovie.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Release Year
                if (userMovie.releaseDate != null && userMovie.releaseDate.isNotBlank()) {
                    Text(
                        text = "📅 ${userMovie.releaseDate.take(4)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Rating
                if (userMovie.voteAverage != null && userMovie.voteAverage > 0) {
                    Text(
                        text = "⭐ ${String.format("%.1f", userMovie.voteAverage)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status badge
                Text(
                    text = if (userMovie.watched) "✓ Watched" else "📝 To Watch",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (userMovie.watched) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Action buttons
            Row {
                if (showWatchedButton && onMarkAsWatched != null) {
                    IconButton(
                        onClick = onMarkAsWatched,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Done,
                            contentDescription = "Mark as watched",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (onRemove != null) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

// MovieSearchDialog (updated to ensure it passes all movie data)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieSearchDialog(
    onDismiss: () -> Unit,
    onMovieSelected: (Movie, Boolean) -> Unit,
    movieRepository: MovieRepository = MovieRepository()
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var addToWatched by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Debounce search
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            isLoading = true
            delay(500)

            val queryToSearch = searchQuery
            if (queryToSearch == searchQuery) {
                coroutineScope.launch {
                    try {
                        val results = movieRepository.searchMovies(queryToSearch)
                        searchResults = results
                    } catch (e: Exception) {
                        e.printStackTrace()
                        searchResults = emptyList()
                    } finally {
                        isLoading = false
                    }
                }
            }
        } else {
            searchResults = emptyList()
            isLoading = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            // Empty confirm button (required by this signature)
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Search and Add Movie") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search movies (min 2 characters)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Add as watched?")
                    Switch(
                        checked = addToWatched,
                        onCheckedChange = { addToWatched = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Results
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Searching...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                } else if (searchQuery.isNotEmpty() && searchResults.isEmpty() && !isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No movies found for '$searchQuery'",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (searchResults.isNotEmpty()) {
                    Text(
                        "Found ${searchResults.size} movies:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchResults, key = { it.id }) { movie ->
                            MovieSearchResultItem(
                                movie = movie,
                                onClick = {
                                    // IMPORTANT: Pass the complete movie object
                                    onMovieSelected(movie, addToWatched)
                                }
                            )
                        }
                    }
                } else if (searchQuery.length == 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Type at least 2 characters to search",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun MovieSearchResultItem(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Movie Poster
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                if (movie.posterPath != null) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w200${movie.posterPath}",
                        contentDescription = "${movie.title} poster",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No\nImage",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Movie Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (movie.releaseDate != null && movie.releaseDate.isNotBlank()) {
                    Text(
                        text = "📅 ${movie.releaseDate.take(4)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                if (movie.voteAverage != null && movie.voteAverage > 0) {
                    Text(
                        text = "⭐ ${String.format("%.1f", movie.voteAverage)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}