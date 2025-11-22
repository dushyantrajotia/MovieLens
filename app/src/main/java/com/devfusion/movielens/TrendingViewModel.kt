package com.devfusion.movielens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class TrendingUiState(
    val loading: Boolean = true,
    val platform: String = "Netflix",
    val items: List<Movie> = emptyList()
)

class TrendingViewModel(
    private val tmdb: TmdbService = TmdbService.create(),

    // SAFE way to read BuildConfig.TMDB_API_KEY
    private val tmdbApiKey: String? = try {
        BuildConfig::class.java.getField("TMDB_API_KEY").get(null) as? String
    } catch (t: Throwable) {
        null
    }
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrendingUiState())
    val uiState: StateFlow<TrendingUiState> = _uiState

    private val providerIds = mapOf(
        "Netflix" to 8,
//        "Amazon Prime" to 119,
        "MX Player" to 387
    )

    init {
        loadForPlatform(_uiState.value.platform)
    }

    fun loadForPlatform(platform: String) {
        val providerId = providerIds[platform] ?: providerIds.values.first()

        _uiState.value = _uiState.value.copy(
            loading = true,
            platform = platform
        )

        val key = tmdbApiKey?.takeIf { it.isNotBlank() }
        if (key == null) {
            _uiState.value = _uiState.value.copy(loading = false, items = emptyList())
            return
        }

        viewModelScope.launch {
            try {
                val resp = tmdb.discoverMovies(
                    apiKey = key,
                    providerId = providerId,
                    page = 1
                )

                if (resp.isSuccessful) {
                    val body = resp.body()

                    val movies = (body?.results ?: emptyList()).take(10).map { item ->
                        Movie(
                            id = item.id.toString(),
                            title = (item.title ?: item.name).orEmpty(),
                            genre = item.genre_ids?.firstOrNull()?.toString(),
                            platform = platform,
                            posterUrl = tmdbPosterUrl(item.poster_path),
                            releaseYear = item.release_date?.take(4)
                        )
                    }

                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        items = movies
                    )
                } else {
                    _uiState.value = _uiState.value.copy(loading = false, items = emptyList())
                }

            } catch (t: Throwable) {
                t.printStackTrace()
                _uiState.value = _uiState.value.copy(loading = false, items = emptyList())
            }
        }
    }
}
