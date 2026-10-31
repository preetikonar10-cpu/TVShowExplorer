package com.example.tvshowexplorer

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
import com.example.tvshowexplorer.ui.state.UiState
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private val repository = TvShowRepository()
    private lateinit var castAdapter: CastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val imgPoster = findViewById<ImageView>(R.id.imgDetailPoster)
        val txtTitle = findViewById<TextView>(R.id.txtDetailTitle)
        val txtRating = findViewById<TextView>(R.id.txtDetailRating)
        val txtSummary = findViewById<TextView>(R.id.txtDetailSummary)
        val recyclerCast = findViewById<RecyclerView>(R.id.recyclerCast)

        val showId = intent.getIntExtra("EXTRA_ID", -1)
        val name = intent.getStringExtra("EXTRA_NAME") ?: "N/A"
        val rating = intent.getDoubleExtra("EXTRA_RATING", 0.0)
        val summary = intent.getStringExtra("EXTRA_SUMMARY") ?: "No summary available."
        val imageUrl = intent.getStringExtra("EXTRA_IMAGE")

        txtTitle.text = name
        txtRating.text = if (rating > 0.0) "⭐ $rating / 10" else "⭐ Rating N/A"
        txtSummary.text = Html.fromHtml(summary, Html.FROM_HTML_MODE_LEGACY).toString()
        imgPoster.load(imageUrl) { crossfade(true) }

        // Setup Cast RecyclerView (Horizontal Scrolling)
        castAdapter = CastAdapter(emptyList())
        recyclerCast.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerCast.adapter = castAdapter

        // Fetch Cast Data
        if (showId != -1) {
            lifecycleScope.launch {
                when (val result = repository.fetchShowCast(showId)) {
                    is UiState.Success -> castAdapter.updateCast(result.data)
                    else -> { /* Ignore cast loading errors */ }
                }
            }
        }
    }
}