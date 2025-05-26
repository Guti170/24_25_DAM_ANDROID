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

class CircuitoAdapter(private val circuitosList: MutableList<Circuito>) :
    RecyclerView.Adapter<CircuitoAdapter.CircuitoViewHolder>() {

    private val storage = Firebase.storage // Referencia a Firebase Storage

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircuitoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_circuito, parent, false)
        return CircuitoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CircuitoViewHolder, position: Int) {
        val currentItem = circuitosList[position]
        holder.nombreCircuito.text = currentItem.nombre

        // Log para verificar qué contiene currentItem.imagenUrl ANTES de cualquier lógica
        Log.d("CircuitoAdapter", "Item: ${currentItem.nombre}, imagenUrl de Firestore: '${currentItem.imagen}'")

        if (currentItem.imagen.startsWith("gs://")) {
            Log.d("CircuitoAdapter", "Intentando obtener URL de descarga para: ${currentItem.imagen}")
            val storageRef = storage.getReferenceFromUrl(currentItem.imagen)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Log.d("CircuitoAdapter", "URL de descarga obtenida para ${currentItem.nombre}: $uri")
                Glide.with(holder.itemView.context)
                    .load(uri) // Usar la URI de descarga
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.imagenCircuito)
            }.addOnFailureListener { exception ->
                // ESTE LOG ES CRUCIAL
                Log.e("CircuitoAdapter", "FALLO al obtener URL de descarga para ${currentItem.imagen}. Error: ${exception.message}", exception)
                holder.imagenCircuito.setImageResource(R.drawable.error_image) // Cargar imagen de error
            }
        } else if (currentItem.imagen.trim().isNotEmpty()) {
            // Si no es gs:// pero tampoco está vacía, podría ser una URL HTTPS directa (menos probable ahora)
            // O podría ser una cadena inválida.
            Log.w("CircuitoAdapter", "imagenUrl no es gs:// pero no está vacía: '${currentItem.imagen}'. Intentando cargar directamente (puede fallar).")
            Glide.with(holder.itemView.context)
                .load(currentItem.imagen)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imagenCircuito)
        } else {
            // imagenUrl está vacía o solo contiene espacios en blanco
            Log.d("CircuitoAdapter", "imagenUrl está vacía o es inválida para ${currentItem.nombre}.")
            holder.imagenCircuito.setImageResource(R.drawable.placeholder_image)
        }
    }

    override fun getItemCount() = circuitosList.size

    fun updateData(newCircuitos: List<Circuito>) {
        circuitosList.clear()
        circuitosList.addAll(newCircuitos)
        notifyDataSetChanged()
    }

    class CircuitoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenCircuito: ImageView = itemView.findViewById(R.id.imageViewCircuito)
        val nombreCircuito: TextView = itemView.findViewById(R.id.textViewNombreCircuito)
    }
}