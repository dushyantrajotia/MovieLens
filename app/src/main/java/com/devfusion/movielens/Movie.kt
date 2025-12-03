package com.devfusion.movielens

data class Movie(
    val id: Int,
    val title: String,
    val posterPath: String? = null,
    val overview: String? = null,
    val releaseDate: String? = null,
    val voteAverage: Double? = null,
    val genres: List<String> = emptyList()
)

data class UserMovie(
    val userId: String = "",
    val movieId: Int = 0,
    val title: String = "",
    val posterPath: String? = null,
    val overview: String? = null,
    val releaseDate: String? = null,
    val voteAverage: Double? = null,
    val genres: List<String> = emptyList(),
    val watched: Boolean = false,
    val watchlist: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toMovie(): Movie {
        return Movie(
            id = movieId,
            title = title,
            posterPath = posterPath,
            overview = overview,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            genres = genres
        )
    }
}