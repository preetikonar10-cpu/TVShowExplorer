package com.example.tvshowexplorer

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.tvshowexplorer.data.repository.TvShowRepository
import com.example.tvshowexplorer.ui.adapter.CastAdapter
import com.example.tvshowexplorer.ui.adapter.EpisodeAdapter
import com.example.tvshowexplorer.ui.state.UiState
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private val repository = TvShowRepository()
    private lateinit var castAdapter: CastAdapter
    private lateinit var episodeAdapter: EpisodeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val imgPoster = findViewById<ImageView>(R.id.imgDetailPoster)
        val txtTitle = findViewById<TextView>(R.id.txtDetailTitle)
        val txtRating = findViewById<TextView>(R.id.txtDetailRating)
        val txtGenres = findViewById<TextView>(R.id.txtDetailGenres)
        val txtStatus = findViewById<TextView>(R.id.txtDetailStatus)
        val txtSummary = findViewById<TextView>(R.id.txtDetailSummary)
        val recyclerCast = findViewById<RecyclerView>(R.id.recyclerCast)
        val recyclerEpisodes = findViewById<RecyclerView>(R.id.recyclerEpisodes)

        val showId = intent.getIntExtra("EXTRA_ID", -1)
        val name = intent.getStringExtra("EXTRA_NAME") ?: "N/A"
        val rating = intent.getDoubleExtra("EXTRA_RATING", 0.0)
        val status = intent.getStringExtra("EXTRA_STATUS") ?: "Unknown"
        val genresList = intent.getStringArrayListExtra("EXTRA_GENRES")
        val summary = intent.getStringExtra("EXTRA_SUMMARY") ?: "No summary available."
        val imageUrl = intent.getStringExtra("EXTRA_IMAGE")

        // Populate basic text views and poster image
        txtTitle.text = name
        txtRating.text = if (rating > 0.0) "⭐ $rating / 10" else "⭐ Rating N/A"
        txtSummary.text = Html.fromHtml(summary, Html.FROM_HTML_MODE_LEGACY).toString()
        imgPoster.load(imageUrl) { crossfade(true) }

        // Set Genres
        txtGenres.text = if (!genresList.isNullOrEmpty()) {
            "Genres: ${genresList.joinToString(", ")}"
        } else {
            "Genres: N/A"
        }

        // Set Status Badge with dynamic colors
        txtStatus.text = status.uppercase()
        if (status.equals("Ended", ignoreCase = true)) {
            txtStatus.setBackgroundColor(Color.parseColor("#E53935")) // Red background for Ended shows
        } else {
            txtStatus.setBackgroundColor(Color.parseColor("#4CAF50")) // Green background for Running/Other shows
        }

        // Setup Cast RecyclerView (Horizontal Scrolling)
        castAdapter = CastAdapter(emptyList())
        recyclerCast.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerCast.adapter = castAdapter

        // Setup Episode RecyclerView (Vertical Scrolling)
        episodeAdapter = EpisodeAdapter(emptyList())
        recyclerEpisodes.layoutManager = LinearLayoutManager(this)
        recyclerEpisodes.adapter = episodeAdapter

        // Fetch Cast & Episode Data concurrently if valid showId exists
        if (showId != -1) {
            lifecycleScope.launch {
                // Fetch Cast members
                when (val castResult = repository.fetchShowCast(showId)) {
                    is UiState.Success -> castAdapter.updateCast(castResult.data)
                    else -> { /* Ignore cast loading errors */ }
                }

                // Fetch Episode list
                when (val episodeResult = repository.fetchShowEpisodes(showId)) {
                    is UiState.Success -> episodeAdapter.updateEpisodes(episodeResult.data)
                    else -> { /* Ignore episode loading errors */ }
                }
            }
        }
    }
}