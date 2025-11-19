package com.devfusion.movielens

// canonical Movie data class used by viewmodels & UI
data class Movie(
    val id: String,
    val title: String,
    val genre: String,
    val platform: String
)
