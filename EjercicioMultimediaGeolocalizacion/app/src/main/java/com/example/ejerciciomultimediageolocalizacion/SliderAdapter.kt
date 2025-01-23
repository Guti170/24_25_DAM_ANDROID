package com.example.ejerciciomultimediageolocalizacion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SliderAdapter(
    private val places: List<Place>
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    private var listener: ((Place) -> Unit)? = null

    fun setOnItemClickListener(listener: (Place) -> Unit) {
        this.listener = listener
    }

    inner class SliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val textView: TextView = view.findViewById(R.id.textView)

        init {
            view.setOnClickListener {
                listener?.invoke(places[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_slider_adapter, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val place = places[position]
        holder.imageView.setImageResource(place.image)
        holder.textView.text = place.name
    }

    override fun getItemCount(): Int = places.size
}
