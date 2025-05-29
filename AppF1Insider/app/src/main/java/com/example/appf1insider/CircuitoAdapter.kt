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
import com.example.appf1insider.model.Circuito // Asegúrate que el modelo es correcto
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
        fun onCircuitoLongClick(circuito: Circuito, position: Int) // Nuevo método para long click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircuitoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_circuito, parent, false) // Asegúrate que R.layout.item_circuito existe
        return CircuitoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CircuitoViewHolder, position: Int) {
        val currentItem = circuitosList[position]
        holder.bind(currentItem, itemClickListener, position) // Pasa la posición también

        holder.nombreCircuito.text = currentItem.nombre
        Log.d("CircuitoAdapter", "Item: ${currentItem.nombre}, imagenUrl de Firestore: '${currentItem.imagen}'")

        if (currentItem.imagen.startsWith("gs://")) {
            val storageRef = storage.getReferenceFromUrl(currentItem.imagen)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(holder.itemView.context)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.imagenCircuito)
            }.addOnFailureListener { exception ->
                Log.e("CircuitoAdapter", "FALLO al obtener URL de descarga para ${currentItem.imagen}. Error: ${exception.message}", exception)
                holder.imagenCircuito.setImageResource(R.drawable.error_image)
            }
        } else if (currentItem.imagen.trim().isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(currentItem.imagen)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imagenCircuito)
        } else {
            holder.imagenCircuito.setImageResource(R.drawable.placeholder_image)
        }
    }

    override fun getItemCount() = circuitosList.size

    fun removeItem(position: Int) {
        if (position >= 0 && position < circuitosList.size) {
            circuitosList.removeAt(position)
            notifyItemRemoved(position)
            // Opcional: si quieres que las posiciones se reajusten visualmente de inmediato
            // notifyItemRangeChanged(position, circuitosList.size)
        }
    }


    class CircuitoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenCircuito: ImageView = itemView.findViewById(R.id.imageViewCircuito) // Asegúrate que el ID es correcto
        val nombreCircuito: TextView = itemView.findViewById(R.id.textViewNombreCircuito) // Asegúrate que el ID es correcto

        fun bind(circuito: Circuito, clickListener: OnItemClickListener, position: Int) {
            itemView.setOnClickListener {
                clickListener.onCircuitoClick(circuito)
            }
            // Configurar el Long Click Listener
            itemView.setOnLongClickListener {
                clickListener.onCircuitoLongClick(circuito, position)
                true // Devuelve true para indicar que el evento ha sido consumido
            }
        }
    }
}