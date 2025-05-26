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
import com.example.appf1insider.model.Escuderia // Importa el modelo Escuderia
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EscuderiaAdapter(private val escuderiasList: MutableList<Escuderia>) :
    RecyclerView.Adapter<EscuderiaAdapter.EscuderiaViewHolder>() {

    private val storage = Firebase.storage

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EscuderiaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_escuderia, parent, false) // Usa el layout del ítem de escudería
        return EscuderiaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EscuderiaViewHolder, position: Int) {
        val currentItem = escuderiasList[position]
        holder.nombreEscuderia.text = currentItem.nombre

        Log.d("EscuderiaAdapter", "Item: ${currentItem.nombre}, imagenUrl de Firestore: '${currentItem.imagen}'")

        if (currentItem.imagen.startsWith("gs://")) {
            Log.d("EscuderiaAdapter", "Intentando obtener URL de descarga para: ${currentItem.imagen}")
            val storageRef = storage.getReferenceFromUrl(currentItem.imagen)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Log.d("EscuderiaAdapter", "URL de descarga obtenida para ${currentItem.nombre}: $uri")
                Glide.with(holder.itemView.context)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.imagenEscuderia)
            }.addOnFailureListener { exception ->
                Log.e("EscuderiaAdapter", "FALLO al obtener URL de descarga para ${currentItem.imagen}. Error: ${exception.message}", exception)
                holder.imagenEscuderia.setImageResource(R.drawable.error_image)
            }
        } else if (currentItem.imagen.trim().isNotEmpty()) {
            Log.w("EscuderiaAdapter", "imagenUrl no es gs:// pero no está vacía: '${currentItem.imagen}'. Intentando cargar directamente.")
            Glide.with(holder.itemView.context)
                .load(currentItem.imagen)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imagenEscuderia)
        } else {
            Log.d("EscuderiaAdapter", "imagenUrl está vacía o es inválida para ${currentItem.nombre}.")
            holder.imagenEscuderia.setImageResource(R.drawable.placeholder_image)
        }
    }

    override fun getItemCount() = escuderiasList.size

    fun updateData(newEscuderias: List<Escuderia>) {
        escuderiasList.clear()
        escuderiasList.addAll(newEscuderias)
        notifyDataSetChanged()
    }

    class EscuderiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenEscuderia: ImageView = itemView.findViewById(R.id.imageViewEscuderia)
        val nombreEscuderia: TextView = itemView.findViewById(R.id.textViewNombreEscuderia)
    }
}