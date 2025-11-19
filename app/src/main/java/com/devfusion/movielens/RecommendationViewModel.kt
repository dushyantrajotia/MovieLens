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

class RecommendationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        // load initial data asynchronously
        viewModelScope.launch {
            delay(200) // simulate small IO delay
            val history = loadWatchHistory()
            val recs = recommendFromHistory(history)
            _uiState.value = HomeUiState(
                loading = false,
                userName = fetchUserNameOrDefault(),
                watchHistory = history,
                recommendations = recs
            )
        }
    }

    private suspend fun loadWatchHistory(): List<Movie> {
        // TODO: replace with real repo call (Room / network)
        return listOf(
            Movie("m1", "The Space Between Us", "Sci-Fi", "Netflix"),
            Movie("m2", "Romcom Classic", "Romance", "Amazon Prime"),
            Movie("m3", "Spy Thriller", "Action", "MX Player")
        )
    }

    private suspend fun recommendFromHistory(history: List<Movie>): List<Movie> {
        // placeholder recommendation logic
        val popular = listOf(
            Movie("r1", "Interstellar", "Sci-Fi", "Netflix"),
            Movie("r2", "The Martian", "Sci-Fi", "Amazon Prime"),
            Movie("r3", "Love Actually", "Romance", "Netflix"),
            Movie("r4", "Mission Impossible: Fallout", "Action", "MX Player"),
            Movie("r5", "Edge of Tomorrow", "Action", "Amazon Prime")
        )
        val genres = history.map { it.genre }.toSet()
        return popular.filter { it.genre in genres }
    }

    private fun fetchUserNameOrDefault(): String {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.displayName ?: (user?.email ?: "User")
    }
}
