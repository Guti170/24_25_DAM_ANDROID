package com.example.appf1insider.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appf1insider.R
import com.example.appf1insider.model.Piloto
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class PilotoAdapter(
    private val pilotosList: MutableList<Piloto>,
    private val itemClickListener: OnPilotoClickListener // Interfaz para el listener
) : RecyclerView.Adapter<PilotoAdapter.PilotoViewHolder>() {

    private val storage = Firebase.storage

    // Interfaz para manejar los clics en los ítems
    interface OnPilotoClickListener {
        fun onPilotoClick(piloto: Piloto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PilotoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_piloto, parent, false)
        return PilotoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PilotoViewHolder, position: Int) {
        val currentItem = pilotosList[position]
        holder.bind(currentItem, itemClickListener) // Pasa el ítem y el listener al ViewHolder

        // La lógica de carga de imagen y nombre se puede mantener aquí o mover a bind
        // Por consistencia con el ejemplo de Circuito, lo dejamos aquí,
        // pero el click listener se establece en bind.
        holder.nombrePiloto.text = currentItem.nombre

        Log.d("PilotoAdapter", "Item: ${currentItem.nombre}, imagenUrl de Firestore: '${currentItem.imagen}'")

        if (currentItem.imagen.startsWith("gs://")) {
            Log.d("PilotoAdapter", "Intentando obtener URL de descarga para: ${currentItem.imagen}")
            val storageRef = storage.getReferenceFromUrl(currentItem.imagen)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Log.d("PilotoAdapter", "URL de descarga obtenida para ${currentItem.nombre}: $uri")
                Glide.with(holder.itemView.context)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.imagenPiloto)
            }.addOnFailureListener { exception ->
                Log.e("PilotoAdapter", "FALLO al obtener URL de descarga para ${currentItem.imagen}. Error: ${exception.message}", exception)
                holder.imagenPiloto.setImageResource(R.drawable.error_image)
            }
        } else if (currentItem.imagen.trim().isNotEmpty()) {
            Log.w("PilotoAdapter", "imagenUrl no es gs:// pero no está vacía: '${currentItem.imagen}'. Intentando cargar directamente.")
            Glide.with(holder.itemView.context)
                .load(currentItem.imagen)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imagenPiloto)
        } else {
            Log.d("PilotoAdapter", "imagenUrl está vacía o es inválida para ${currentItem.nombre}.")
            holder.imagenPiloto.setImageResource(R.drawable.placeholder_image)
        }
    }

    override fun getItemCount() = pilotosList.size

    fun updateData(newPilotos: List<Piloto>) {
        pilotosList.clear()
        pilotosList.addAll(newPilotos)
        notifyDataSetChanged() // Considera usar DiffUtil para mejor rendimiento
    }

    // ViewHolder ahora tiene un método bind que configura el OnClickListener
    class PilotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenPiloto: ImageView = itemView.findViewById(R.id.imageViewPiloto)
        val nombrePiloto: TextView = itemView.findViewById(R.id.textViewNombrePiloto)

        fun bind(piloto: Piloto, clickListener: OnPilotoClickListener) {
            // El nombre y la imagen ya se establecen en onBindViewHolder,
            // pero si movieras esa lógica aquí, este sería el lugar.

            itemView.setOnClickListener {
                clickListener.onPilotoClick(piloto)
            }
        }
    }
}