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
import com.example.appf1insider.model.Circuito
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class CircuitoAdapter(
    private val circuitosList: MutableList<Circuito>,
    private val itemClickListener: OnItemClickListener // Interfaz para el listener
) : RecyclerView.Adapter<CircuitoAdapter.CircuitoViewHolder>() {

    private val storage = Firebase.storage

    // Interfaz para manejar los clics en los ítems
    interface OnItemClickListener {
        fun onCircuitoClick(circuito: Circuito)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircuitoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_circuito, parent, false)
        return CircuitoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CircuitoViewHolder, position: Int) {
        val currentItem = circuitosList[position]
        holder.bind(currentItem, itemClickListener) // Pasa el ítem y el listener al ViewHolder

        // La lógica de carga de imagen permanece aquí o puede moverse a bind si se prefiere
        holder.nombreCircuito.text = currentItem.nombre

        if (currentItem.imagen.startsWith("gs://")) {
            Log.d("CircuitoAdapter", "Intentando obtener URL de descarga para: ${currentItem.imagen}")
            val storageRef = storage.getReferenceFromUrl(currentItem.imagen)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Log.d("CircuitoAdapter", "URL de descarga obtenida para ${currentItem.nombre}: $uri")
                Glide.with(holder.itemView.context)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image) // Asegúrate de tener estos drawables
                    .error(R.drawable.error_image)             // Asegúrate de tener estos drawables
                    .into(holder.imagenCircuito)
            }.addOnFailureListener { exception ->
                Log.e("CircuitoAdapter", "FALLO al obtener URL de descarga para ${currentItem.imagen}. Error: ${exception.message}", exception)
                holder.imagenCircuito.setImageResource(R.drawable.error_image)
            }
        } else if (currentItem.imagen.trim().isNotEmpty()) {
            Log.w("CircuitoAdapter", "imagenUrl no es gs:// pero no está vacía: '${currentItem.imagen}'. Intentando cargar directamente.")
            Glide.with(holder.itemView.context)
                .load(currentItem.imagen)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imagenCircuito)
        } else {
            Log.d("CircuitoAdapter", "imagenUrl está vacía o es inválida para ${currentItem.nombre}.")
            holder.imagenCircuito.setImageResource(R.drawable.placeholder_image)
        }
    }

    override fun getItemCount() = circuitosList.size

    fun updateData(newCircuitos: List<Circuito>) {
        circuitosList.clear()
        circuitosList.addAll(newCircuitos)
        notifyDataSetChanged() // Considera usar DiffUtil para mejor rendimiento en listas grandes
    }

    // ViewHolder ahora tiene un método bind que configura el OnClickListener
    class CircuitoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenCircuito: ImageView = itemView.findViewById(R.id.imageViewCircuito)
        val nombreCircuito: TextView = itemView.findViewById(R.id.textViewNombreCircuito)

        fun bind(circuito: Circuito, clickListener: OnItemClickListener) {
            // El nombre y la imagen ya se establecen en onBindViewHolder,
            // pero si movieras esa lógica aquí, este sería el lugar.

            itemView.setOnClickListener {
                clickListener.onCircuitoClick(circuito)
            }
        }
    }
}