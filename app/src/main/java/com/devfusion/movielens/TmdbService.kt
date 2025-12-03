package com.devfusion.movielens

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

// DTOs for TMDB API responses
data class TmdbPagedResponse(
    val page: Int,
    val results: List<TmdbMovieItem>
)

data class TmdbMovieItem(
    val id: Long,
    val title: String?,
    val name: String?, // for TV (not used heavily here)
    val poster_path: String?,
    val release_date: String?,
    val genre_ids: List<Int>?,
    val vote_average: Double? // Added this for recommendations
)

// Extended service interface for recommendations
interface TmdbService {
    // For streaming platforms (your existing)
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String,
        @Query("with_watch_providers") providerId: Int,
        @Query("watch_region") region: String = "US",
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse>

    // For recommendations (new)
    @GET("discover/movie")
    suspend fun discoverMoviesByGenre(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genres: String? = null,
        @Query("sort_by") sort_by: String = "popularity.desc",
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): Response<TmdbPagedResponse>

    @GET("movie/popular")
    suspend fun popularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse>

    // New endpoints for recommendations
    @GET("movie/now_playing")
    suspend fun nowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse>

    @GET("movie/upcoming")
    suspend fun upcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse>

    companion object {
        private const val BASE = "https://api.themoviedb.org/3/"

        fun create(): TmdbService {
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TmdbService::class.java)
        }
    }
}

/** Helper: build full poster url */
fun tmdbPosterUrl(path: String?): String? {
    if (path.isNullOrBlank()) return null
    val baseUrl = "https://image.tmdb.org/t/p/"
    val size = "w500"
    val cleanPath = if (path.startsWith("/")) path.substring(1) else path
    return "$baseUrl$size/$cleanPath"
}

/** Convert TMDB movie to our app's Movie model */
fun TmdbMovieItem.toMovie(): Movie {
    return Movie(
        id = this.id.toInt(),
        title = this.title ?: this.name ?: "Unknown Title",
        posterPath = this.poster_path,
        releaseDate = this.release_date,
        voteAverage = this.vote_average,
        genres = emptyList() // We'll handle genres separately
    )
}