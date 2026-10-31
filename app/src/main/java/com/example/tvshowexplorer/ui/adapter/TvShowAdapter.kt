package com.example.tvshowexplorer.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.tvshowexplorer.DetailActivity
import com.example.tvshowexplorer.R
import com.example.tvshowexplorer.data.model.TvShow

class TvShowAdapter(private var shows: List<TvShow>) :
    RecyclerView.Adapter<TvShowAdapter.TvShowViewHolder>() {

    class TvShowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPoster: ImageView = itemView.findViewById(R.id.imgPoster)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val txtRating: TextView = itemView.findViewById(R.id.txtRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tv_show, parent, false)
        return TvShowViewHolder(view)
    }

    override fun onBindViewHolder(holder: TvShowViewHolder, position: Int) {
        val show = shows[position]
        holder.txtTitle.text = show.name
        holder.txtRating.text = "⭐ ${show.rating?.average ?: "N/A"}"

        holder.imgPoster.load(show.image?.medium) {
            crossfade(true)
        }

        // Open DetailActivity on click with extra information
        holder.itemView.setOnClickListener { context ->
            val intent = Intent(context.context, DetailActivity::class.java).apply {
                putExtra("EXTRA_ID", show.id)
                putExtra("EXTRA_NAME", show.name)
                putExtra("EXTRA_RATING", show.rating?.average ?: 0.0)
                putExtra("EXTRA_SUMMARY", show.summary)
                putExtra("EXTRA_IMAGE", show.image?.original ?: show.image?.medium)
                putExtra("EXTRA_STATUS", show.status)
                putStringArrayListExtra("EXTRA_GENRES", ArrayList(show.genres ?: emptyList()))
                putExtra("EXTRA_URL", show.officialSite ?: show.url)
            }
            context.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = shows.size

    fun updateData(newShows: List<TvShow>) {
        shows = newShows
        notifyDataSetChanged()
    }
}