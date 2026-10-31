package com.example.tvshowexplorer

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.tvshowexplorer.data.api.Episode
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
        val btnOfficialSite = findViewById<Button>(R.id.btnOfficialSite)
        val txtSummary = findViewById<TextView>(R.id.txtDetailSummary)
        val recyclerCast = findViewById<RecyclerView>(R.id.recyclerCast)
        val recyclerEpisodes = findViewById<RecyclerView>(R.id.recyclerEpisodes)

        val showId = intent.getIntExtra("EXTRA_ID", -1)
        val name = intent.getStringExtra("EXTRA_NAME") ?: "N/A"
        val rating = intent.getDoubleExtra("EXTRA_RATING", 0.0)
        val status = intent.getStringExtra("EXTRA_STATUS") ?: "Unknown"
        val genresList = intent.getStringArrayListExtra("EXTRA_GENRES")
        val webUrl = intent.getStringExtra("EXTRA_URL")
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
            txtStatus.setBackgroundColor(Color.parseColor("#E53935"))
        } else {
            txtStatus.setBackgroundColor(Color.parseColor("#4CAF50"))
        }

        // Website Button Intent
        if (!webUrl.isNullOrBlank()) {
            btnOfficialSite.visibility = View.VISIBLE
            btnOfficialSite.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
                startActivity(browserIntent)
            }
        } else {
            btnOfficialSite.visibility = View.GONE
        }

        // Setup Cast RecyclerView (Horizontal Scrolling)
        castAdapter = CastAdapter(emptyList())
        recyclerCast.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerCast.adapter = castAdapter

        // Setup Episode RecyclerView with Dialog Click Listener
        episodeAdapter = EpisodeAdapter(emptyList()) { episode ->
            showEpisodeDialog(episode)
        }
        recyclerEpisodes.layoutManager = LinearLayoutManager(this)
        recyclerEpisodes.isNestedScrollingEnabled = false
        recyclerEpisodes.adapter = episodeAdapter

        // Fetch Cast & Episode Data concurrently if valid showId exists
        if (showId != -1) {
            lifecycleScope.launch {
                when (val castResult = repository.fetchShowCast(showId)) {
                    is UiState.Success -> castAdapter.updateCast(castResult.data)
                    else -> { /* Ignore cast loading errors */ }
                }

                when (val episodeResult = repository.fetchShowEpisodes(showId)) {
                    is UiState.Success -> episodeAdapter.updateEpisodes(episodeResult.data)
                    else -> { /* Ignore episode loading errors */ }
                }
            }
        }
    }

    private fun showEpisodeDialog(episode: Episode) {
        try {
            val view = layoutInflater.inflate(R.layout.layout_episode_bottom_sheet, null)

            val txtTitle = view.findViewById<TextView>(R.id.txtBottomSheetTitle)
            val txtMeta = view.findViewById<TextView>(R.id.txtBottomSheetMeta)
            val txtSummary = view.findViewById<TextView>(R.id.txtBottomSheetSummary)

            txtTitle?.text = episode.name
            txtMeta?.text = "Season ${episode.season}, Episode ${episode.number} • Aired: ${episode.airdate ?: "N/A"}"

            val summaryText = episode.summary ?: "No episode summary available."
            txtSummary?.text = Html.fromHtml(summaryText, Html.FROM_HTML_MODE_LEGACY).toString()

            AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Close") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}