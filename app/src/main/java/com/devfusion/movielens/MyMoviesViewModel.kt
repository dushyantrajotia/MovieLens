package com.devfusion.movielens

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class MyMoviesViewModel : ViewModel() {
    // Two lists: toBeWatched and watched. Using MutableStateList so Compose observes changes.
    val toBeWatched = mutableStateListOf<Movie>()
    val watched = mutableStateListOf<Movie>()

    // Add a movie to "to be watched"
    fun addToBeWatched(movie: Movie) {
        toBeWatched += movie
    }

    // Move from toBeWatched -> watched
    fun markAsWatched(movie: Movie) {
        if (toBeWatched.remove(movie)) {
            watched += movie
        } else if (!watched.contains(movie)) {
            // added directly if not present
            watched += movie
        }
    }

    // Add directly to watched (user manual input)
    fun addWatched(movie: Movie) {
        if (!watched.any { it.id == movie.id }) watched += movie
    }

    // Remove helpers if needed
    fun removeFromToBeWatched(movie: Movie) { toBeWatched.remove(movie) }
    fun removeFromWatched(movie: Movie) { watched.remove(movie) }
}
