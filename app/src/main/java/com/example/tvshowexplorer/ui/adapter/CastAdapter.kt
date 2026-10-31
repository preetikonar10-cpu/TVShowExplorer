package com.example.tvshowexplorer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.tvshowexplorer.R
import com.example.tvshowexplorer.data.api.CastResponse

class CastAdapter(private var castList: List<CastResponse>) :
    RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    class CastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgActor: ImageView = itemView.findViewById(R.id.imgActor)
        val txtActorName: TextView = itemView.findViewById(R.id.txtActorName)
        val txtCharacterName: TextView = itemView.findViewById(R.id.txtCharacterName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cast, parent, false)
        return CastViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val cast = castList[position]
        holder.txtActorName.text = cast.person.name
        holder.txtCharacterName.text = cast.character.name

        holder.imgActor.load(cast.person.image?.medium ?: cast.person.image?.original) {
            crossfade(true)
        }
    }

    override fun getItemCount(): Int = castList.size

    fun updateCast(newCast: List<CastResponse>) {
        castList = newCast
        notifyDataSetChanged()
    }
}