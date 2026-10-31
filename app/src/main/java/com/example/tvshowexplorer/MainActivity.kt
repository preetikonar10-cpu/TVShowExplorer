package com.example.tvshowexplorer

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.tvshowexplorer.data.model.TvShow
import com.example.tvshowexplorer.data.repository.TvShowRepository
import com.example.tvshowexplorer.ui.adapter.TvShowAdapter
import com.example.tvshowexplorer.ui.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var txtError: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adapter: TvShowAdapter
    private val repository = TvShowRepository()
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        txtError = findViewById(R.id.txtError)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        adapter = TvShowAdapter(emptyList())
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        // Fetch initial popular shows
        fetchPopularShows()

        // Setup real-time search listener with debouncing
        setupSearch()

        // Setup Pull-to-Refresh
        swipeRefreshLayout.setOnRefreshListener {
            val currentQuery = searchView.query.toString()
            if (currentQuery.isBlank()) {
                fetchPopularShows()
            } else {
                performSearch(currentQuery)
            }
        }
    }

    private fun fetchPopularShows() {
        lifecycleScope.launch {
            if (!swipeRefreshLayout.isRefreshing) showLoading()
            when (val result = repository.fetchPopularShows()) {
                is UiState.Success -> showData(result.data)
                is UiState.Error -> showError(result.message)
                is UiState.Loading -> showLoading()
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    performSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500) // 500ms debounce
                    if (newText.isNullOrBlank()) {
                        fetchPopularShows()
                    } else {
                        performSearch(newText)
                    }
                }
                return true
            }
        })
    }

    private fun performSearch(query: String) {
        lifecycleScope.launch {
            if (!swipeRefreshLayout.isRefreshing) showLoading()
            when (val result = repository.searchShows(query)) {
                is UiState.Success -> {
                    if (result.data.isEmpty()) {
                        showError("No TV shows found for '$query'")
                    } else {
                        showData(result.data)
                    }
                }
                is UiState.Error -> showError(result.message)
                is UiState.Loading -> showLoading()
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        txtError.visibility = View.GONE
        recyclerView.visibility = View.GONE
    }

    private fun showData(shows: List<TvShow>) {
        progressBar.visibility = View.GONE
        txtError.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        adapter.updateData(shows)
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        txtError.visibility = View.VISIBLE
        txtError.text = message
    }
}