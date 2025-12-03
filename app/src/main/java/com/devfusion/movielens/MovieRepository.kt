package com.devfusion.movielens

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(){

    private val tmdbService = TmdbService.create()

    suspend fun getNewReleases(): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val response = tmdbService.nowPlayingMovies(ApiKeyManager.TMDB_API_KEY)
                if (response.isSuccessful) {
                    response.body()?.results?.map { it.toMovie() }?.take(6) ?: emptyList()
                } else {
                    getFallbackNewReleases()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getFallbackNewReleases()
            }
        }
    }

    suspend fun searchMovies(query: String): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val response = tmdbService.searchMovies(
                    apiKey = ApiKeyManager.TMDB_API_KEY,
                    query = query
                )
                if (response.isSuccessful) {
                    response.body()?.results?.map { it.toMovie() } ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun getUpcomingMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val response = tmdbService.upcomingMovies(ApiKeyManager.TMDB_API_KEY)
                if (response.isSuccessful) {
                    response.body()?.results?.map { it.toMovie() }?.take(6) ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun getPopularMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val response = tmdbService.popularMovies(ApiKeyManager.TMDB_API_KEY)
                if (response.isSuccessful) {
                    response.body()?.results?.map { it.toMovie() }?.take(6) ?: getFallbackPopularMovies()
                } else {
                    getFallbackPopularMovies()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getFallbackPopularMovies()
            }
        }
    }

    suspend fun getSimilarMovies(userPreferences: UserPreferences): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                // Convert genres to TMDB genre IDs
                val genreIds = userPreferences.favoriteGenres.mapNotNull { getGenreId(it) }
                val genreString = if (genreIds.isNotEmpty()) genreIds.take(2).joinToString(",") else null

                val response = tmdbService.discoverMoviesByGenre(
                    apiKey = ApiKeyManager.TMDB_API_KEY,
                    genres = genreString,
                    sort_by = "popularity.desc"
                )

                if (response.isSuccessful) {
                    response.body()?.results?.map { it.toMovie() }?.take(6) ?: getFallbackRecommendations()
                } else {
                    getFallbackRecommendations()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getFallbackRecommendations()
            }
        }
    }

    private fun getGenreId(genre: String): Int? {
        val genreMap = mapOf(
            "Action" to 28, "Adventure" to 12, "Animation" to 16,
            "Comedy" to 35, "Crime" to 80, "Documentary" to 99,
            "Drama" to 18, "Family" to 10751, "Fantasy" to 14,
            "History" to 36, "Horror" to 27, "Music" to 10402,
            "Mystery" to 9648, "Romance" to 10749, "Science Fiction" to 878,
            "Thriller" to 53, "War" to 10752, "Western" to 37
        )
        return genreMap[genre]
    }

    // Fallback methods (keep your existing ones)
    private fun getFallbackNewReleases(): List<Movie> {
        return listOf(
            Movie(1, "Dune: Part Two", "/8b8R8l88Qje9dn9OE8PY05Nxl1X.jpg", releaseDate = "2024", voteAverage = 8.5),
            Movie(2, "Oppenheimer", "/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg", releaseDate = "2023", voteAverage = 8.3)
        )
    }

    private fun getFallbackPopularMovies(): List<Movie> {
        return listOf(
            Movie(1, "The Dark Knight", "/qJ2tW6WMUDux911r6m7haRef0WH.jpg", releaseDate = "2008", voteAverage = 9.0),
            Movie(2, "Pulp Fiction", "/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg", releaseDate = "1994", voteAverage = 8.9)
        )
    }

    private fun getFallbackRecommendations(): List<Movie> {
        return getFallbackPopularMovies()
    }
}