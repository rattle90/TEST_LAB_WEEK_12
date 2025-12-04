package com.example.test_lab_week_12

import com.example.test_lab_week_12.api.MovieService
import com.example.test_lab_week_12.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow // <-- BARU
import kotlinx.coroutines.flow.flow // <-- BARU
import kotlinx.coroutines.flow.flowOn // <-- BARU

class MovieRepository (private val movieService: MovieService) {
    // GANTI "your_api_key_here" dengan API KEY kamu!
    private val apiKey = "a7bd5a4ec078d3d28bf399d4e6f43724"

    // Hapus LiveData (movieLiveData, errorLiveData, dan val movies/error)

    // fetch movies from the API
    fun fetchMovies(): Flow<List<Movie>> {
        return flow {
            // emit list of popular movies dari API
            val popularMovies = movieService.getPopularMovies (apiKey)
            emit(popularMovies.results)
        }.flowOn(Dispatchers.IO) // Menjamin API call berjalan di thread IO
    }
}