package com.example.tvshowexplorer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshowexplorer.R
import com.example.tvshowexplorer.data.api.Episode

class EpisodeAdapter(private var episodes: List<Episode>) :
    RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    class EpisodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtEpisodeNumber: TextView = itemView.findViewById(R.id.txtEpisodeNumber)
        val txtEpisodeName: TextView = itemView.findViewById(R.id.txtEpisodeName)
        val txtAirDate: TextView = itemView.findViewById(R.id.txtAirDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val ep = episodes[position]
        holder.txtEpisodeNumber.text = "S${ep.season} E${ep.number}"
        holder.txtEpisodeName.text = ep.name
        holder.txtAirDate.text = "Aired: ${ep.airdate ?: "N/A"}"
    }

    override fun getItemCount(): Int = episodes.size

    fun updateEpisodes(newEpisodes: List<Episode>) {
        episodes = newEpisodes
        notifyDataSetChanged()
    }
}