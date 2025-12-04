package com.example.test_lab_week_12

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle // <-- BARU
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope // <-- BARU
import androidx.lifecycle.repeatOnLifecycle // <-- BARU
import androidx.recyclerview.widget.RecyclerView
import com.example.test_lab_week_12.model.Movie
import com.google.android.material.snackbar.Snackbar
// Hapus import java.util.Calendar (Karena logic sudah pindah ke ViewModel)
import kotlinx.coroutines.launch // <-- BARU

class MainActivity : AppCompatActivity() {
    private val movieAdapter by lazy {
        MovieAdapter(object : MovieAdapter.MovieClickListener {
            override fun onMovieClick(movie: Movie) {
                openMovieDetails(movie)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.movie_list)
        recyclerView.adapter = movieAdapter

        val movieRepository = (application as MovieApplication).movieRepository
        val movieViewModel = ViewModelProvider(
            this, object: ViewModelProvider.Factory {
                override fun <T: androidx.lifecycle.ViewModel> create (modelClass: Class<T>): T{
                    return MovieViewModel (movieRepository) as T
                }
            }) [MovieViewModel::class.java]

        // Mengganti LiveData.observe() dengan Flow.collect()
        lifecycleScope.launch {
            repeatOnLifecycle (Lifecycle.State.STARTED) {
                // 1. Collect Movie Flow
                launch {
                    movieViewModel.popularMovies.collect { movies ->
                        // Movies yang dikumpulkan sudah di filter dan sort di ViewModel
                        movieAdapter.addMovies (movies)
                    }
                }
                // 2. Collect Error Flow
                launch {
                    movieViewModel.error.collect { error ->
                        if (error.isNotEmpty()) {
                            Snackbar.make(
                                recyclerView, error, Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun openMovieDetails(movie: Movie) {
        val intent = Intent(this, DetailsActivity::class.java).apply {
            putExtra(DetailsActivity.EXTRA_TITLE, movie.title)
            putExtra(DetailsActivity.EXTRA_RELEASE, movie.releaseDate)
            putExtra(DetailsActivity.EXTRA_OVERVIEW, movie.overview)
            putExtra(DetailsActivity.EXTRA_POSTER, movie.posterPath)
        }
        startActivity(intent)
    }
}