package com.example.test_lab_week_12

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test_lab_week_12.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow // <-- BARU
import kotlinx.coroutines.flow.StateFlow // <-- BARU
import kotlinx.coroutines.flow.catch // <-- BARU
import kotlinx.coroutines.launch
import java.util.Calendar // <-- BARU

class MovieViewModel (private val movieRepository: MovieRepository)
    : ViewModel() {

    // StateFlow untuk data (menggantikan LiveData)
    private val _popularMovies = MutableStateFlow(emptyList<Movie>())
    val popularMovies: StateFlow<List<Movie>> = _popularMovies

    // StateFlow untuk error (menggantikan LiveData)
    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    init {
        fetchPopularMovies()
    }

    private fun fetchPopularMovies() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()

        viewModelScope.launch {
            movieRepository.fetchMovies()
                .catch { exception -> // Tangkap error
                    _error.value = "An exception occurred: ${exception.message}"
                }
                .collect { movies ->
                    // Logic Assignment: Filter dan Sort di ViewModel
                    val filteredAndSortedMovies = movies
                        .filter { movie ->
                            // Filter: hanya film di tahun ini (aman dari null)
                            movie.releaseDate?.startsWith(currentYear) == true
                        }
                        // Sort: Diurutkan menurun berdasarkan popularitas
                        .sortedByDescending { it.popularity }

                    // Emit hasil yang sudah diproses ke StateFlow
                    _popularMovies.value = filteredAndSortedMovies
                }
        }
    }
}