package com.example.test_lab_week_12.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true) // <-- PENTING: Moshi Annotation
data class PopularMoviesResponse(
    val page: Int,
    val results: List<Movie> // <-- Memuat List<Movie>
)