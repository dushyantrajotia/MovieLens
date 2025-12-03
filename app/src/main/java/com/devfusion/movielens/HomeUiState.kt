package com.devfusion.movielens

data class HomeUiState(
    val userName: String = "User",
    val watchHistory: List<Movie> = emptyList(),
    val recommendations: List<Movie> = emptyList(),
    val newReleases: List<Movie> = emptyList(),
    val upcomingMovies: List<Movie> = emptyList()
)
data class UserPreferences(
    val favoriteGenres: List<String>,
    val preferredRatingRange: ClosedFloatingPointRange<Double>,
    val preferredYears: IntRange
)

data class UserMovieInteraction(
    val userId: String,
    val movieId: Int,
    val rating: Double? = null,
    val watched: Boolean = false,
    val watchCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)