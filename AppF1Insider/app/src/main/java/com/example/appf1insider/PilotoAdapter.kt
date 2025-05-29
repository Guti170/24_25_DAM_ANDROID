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
        fun onPilotoLongClick(piloto: Piloto, position: Int) // Nuevo método para long click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PilotoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_piloto, parent, false) // Asegúrate que R.layout.item_piloto existe
        return PilotoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PilotoViewHolder, position: Int) {
        val currentItem = pilotosList[position]
        holder.bind(currentItem, itemClickListener, position) // Pasa la posición también

        holder.nombrePiloto.text = currentItem.nombre
        Log.d("PilotoAdapter", "Item: ${currentItem.nombre}, imagenUrl de Firestore: '${currentItem.imagen}'")

        if (currentItem.imagen.startsWith("gs://")) {
            val storageRef = storage.getReferenceFromUrl(currentItem.imagen)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
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
            Glide.with(holder.itemView.context)
                .load(currentItem.imagen)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imagenPiloto)
        } else {
            holder.imagenPiloto.setImageResource(R.drawable.placeholder_image)
        }
    }

    override fun getItemCount() = pilotosList.size

    // Método para eliminar un ítem de la lista y notificar al adaptador
    fun removeItem(position: Int) {
        if (position >= 0 && position < pilotosList.size) {
            pilotosList.removeAt(position)
            notifyItemRemoved(position)
            // Opcional: si quieres que las posiciones se reajusten visualmente de inmediato
            // notifyItemRangeChanged(position, pilotosList.size)
        }
    }

    fun updateData(newPilotos: List<Piloto>) {
        pilotosList.clear()
        pilotosList.addAll(newPilotos)
        notifyDataSetChanged()
    }

    class PilotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenPiloto: ImageView = itemView.findViewById(R.id.imageViewPiloto) // Asegúrate que el ID es correcto
        val nombrePiloto: TextView = itemView.findViewById(R.id.textViewNombrePiloto) // Asegúrate que el ID es correcto

        fun bind(piloto: Piloto, clickListener: OnPilotoClickListener, position: Int) {
            itemView.setOnClickListener {
                clickListener.onPilotoClick(piloto)
            }
            // Configurar el Long Click Listener
            itemView.setOnLongClickListener {
                clickListener.onPilotoLongClick(piloto, position)
                true // Devuelve true para indicar que el evento ha sido consumido
            }
        }
    }
}