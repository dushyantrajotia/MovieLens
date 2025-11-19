package com.devfusion.movielens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TrendingUiState(
    val loading: Boolean = true,
    val platform: String = "Netflix",
    val items: List<Movie> = emptyList()
)

class TrendingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TrendingUiState())
    val uiState: StateFlow<TrendingUiState> = _uiState

    init {
        loadForPlatform(_uiState.value.platform)
    }

    fun loadForPlatform(platform: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, platform = platform)
            delay(250) // simulate network
            _uiState.value = TrendingUiState(loading = false, platform = platform, items = sampleTrendingForPlatform(platform))
        }
    }

    // sample data function kept inside the ViewModel file for simplicity
    private fun sampleTrendingForPlatform(platform: String): List<Movie> {
        return listOf(
            Movie("t1", "Top Hit A", "Drama", platform),
            Movie("t2", "Top Hit B", "Comedy", platform),
            Movie("t3", "Top Hit C", "Thriller", platform),
        )
    }
}
