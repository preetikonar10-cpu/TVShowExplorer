package com.example.tvshowexplorer

import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil.load

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val imgPoster = findViewById<ImageView>(R.id.imgDetailPoster)
        val txtTitle = findViewById<TextView>(R.id.txtDetailTitle)
        val txtRating = findViewById<TextView>(R.id.txtDetailRating)
        val txtSummary = findViewById<TextView>(R.id.txtDetailSummary)

        // Receive data passed via Intent
        val name = intent.getStringExtra("EXTRA_NAME") ?: "N/A"
        val rating = intent.getDoubleExtra("EXTRA_RATING", 0.0)
        val summary = intent.getStringExtra("EXTRA_SUMMARY") ?: "No summary available."
        val imageUrl = intent.getStringExtra("EXTRA_IMAGE")

        txtTitle.text = name
        txtRating.text = if (rating > 0.0) "⭐ $rating / 10" else "⭐ Rating N/A"

        // Strip HTML tags from TVmaze summary text
        txtSummary.text = Html.fromHtml(summary, Html.FROM_HTML_MODE_LEGACY).toString()

        imgPoster.load(imageUrl) {
            crossfade(true)
        }
    }
}