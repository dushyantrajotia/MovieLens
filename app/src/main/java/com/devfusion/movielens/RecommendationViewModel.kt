package com.devfusion.movielens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

data class HomeUiState(
    val loading: Boolean = true,
    val userName: String = "User",
    val watchHistory: List<Movie> = emptyList(),
    val recommendations: List<Movie> = emptyList()
)

class RecommendationViewModel(
    // You can inject your repository here later
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    // Sample movie database - replace with your actual data source
    private val allMovies = listOf(
        Movie("1", "Interstellar", "Sci-Fi", "Netflix", "2014"),
        Movie("2", "The Martian", "Sci-Fi", "Amazon Prime", "2015"),
        Movie("3", "Love Actually", "Romance", "Netflix", "2003"),
        Movie("4", "Mission Impossible: Fallout", "Action", "MX Player", "2018"),
        Movie("5", "Edge of Tomorrow", "Action", "Amazon Prime", "2014"),
        Movie("6", "Gravity", "Sci-Fi", "Netflix", "2013"),
        Movie("7", "The Notebook", "Romance", "Amazon Prime", "2004"),
        Movie("8", "John Wick", "Action", "MX Player", "2014"),
        Movie("9", "Arrival", "Sci-Fi", "Netflix", "2016"),
        Movie("10", "La La Land", "Romance", "Amazon Prime", "2016"),
        Movie("11", "Mad Max: Fury Road", "Action", "MX Player", "2015"),
        Movie("12", "Inception", "Sci-Fi", "Netflix", "2010")
    )

    init {
        loadRecommendations()
    }

    fun refreshRecommendations() {
        loadRecommendations()
    }

    private fun loadRecommendations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)

            delay(500) // Simulate network delay

            val history = loadWatchHistory()
            val recommendations = generateRecommendations(history)

            _uiState.value = HomeUiState(
                loading = false,
                userName = fetchUserNameOrDefault(),
                watchHistory = history,
                recommendations = recommendations
            )
        }
    }

    private suspend fun loadWatchHistory(): List<Movie> {
        // TODO: Replace with actual data source (Room database, Firestore, etc.)
        // For now, return some sample watched movies
        return listOf(
            Movie("1", "Interstellar", "Sci-Fi", "Netflix", "2014"),
            Movie("3", "Love Actually", "Romance", "Netflix", "2003"),
            Movie("8", "John Wick", "Action", "MX Player", "2014")
        )
    }

    private fun generateRecommendations(watchHistory: List<Movie>): List<Movie> {
        if (watchHistory.isEmpty()) {
            // If no watch history, return popular movies
            return allMovies.shuffled().take(6)
        }

        // Extract user preferences from watch history
        val favoriteGenres = watchHistory
            .groupBy { it.genre ?: "" }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(2)
            .map { it.first }

        val favoritePlatforms = watchHistory
            .groupBy { it.platform ?: "" }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(2)
            .map { it.first }

        // Generate recommendations based on preferences
        val recommendations = allMovies.filter { movie ->
            // Exclude movies already in watch history
            movie.id !in watchHistory.map { it.id } &&
                    // Prioritize movies that match user's favorite genres AND platforms
                    ((movie.genre in favoriteGenres && movie.platform in favoritePlatforms) ||
                            // Also include movies that match either genre or platform
                            (movie.genre in favoriteGenres || movie.platform in favoritePlatforms))
        }.shuffled().take(6)

        // If we don't have enough recommendations, fill with popular movies
        return if (recommendations.size < 6) {
            recommendations + allMovies
                .filter { it.id !in watchHistory.map { m -> m.id } && it.id !in recommendations.map { r -> r.id } }
                .shuffled()
                .take(6 - recommendations.size)
        } else {
            recommendations
        }
    }

    private fun fetchUserNameOrDefault(): String {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.displayName ?: (user?.email?.substringBefore('@') ?: "Movie Lover")
    }

    // Function to add a movie to watch history (call this when user watches a movie)
    fun addToWatchHistory(movie: Movie) {
        // TODO: Implement actual storage logic
        // For now, just refresh recommendations
        loadRecommendations()
    }
}