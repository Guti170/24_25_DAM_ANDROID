package escuderias

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appf1insider.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EscuderiaAdapter(
    private val escuderiasList: MutableList<Escuderia>,
    private val itemClickListener: OnEscuderiaClickListener // Interfaz para el listener
) : RecyclerView.Adapter<EscuderiaAdapter.EscuderiaViewHolder>() {

    private val storage = Firebase.storage

    // Interfaz para manejar los clics en los ítems
    interface OnEscuderiaClickListener {
        fun onEscuderiaClick(escuderia: Escuderia)
        fun onEscuderiaLongClick(escuderia: Escuderia, position: Int) // Nuevo método para long click
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EscuderiaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_escuderia, parent, false) // Asegúrate que R.layout.item_escuderia existe
        return EscuderiaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EscuderiaViewHolder, position: Int) {
        val currentItem = escuderiasList[position]
        holder.bind(currentItem, itemClickListener, position) // Pasa la posición también

        holder.nombreEscuderia.text = currentItem.nombre
        Log.d("EscuderiaAdapter", "Item: ${currentItem.nombre}, imagenUrl de Firestore: '${currentItem.imagen}'")

        if (currentItem.imagen.startsWith("gs://")) {
            val storageRef = storage.getReferenceFromUrl(currentItem.imagen)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
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
            Glide.with(holder.itemView.context)
                .load(currentItem.imagen)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imagenEscuderia)
        } else {
            holder.imagenEscuderia.setImageResource(R.drawable.placeholder_image)
        }
    }

    override fun getItemCount() = escuderiasList.size

    // Método para eliminar un ítem de la lista y notificar al adaptador
    fun removeItem(position: Int) {
        if (position >= 0 && position < escuderiasList.size) {
            escuderiasList.removeAt(position)
            notifyItemRemoved(position)
            // Opcional: si quieres que las posiciones se reajusten visualmente de inmediato
            // notifyItemRangeChanged(position, escuderiasList.size)
        }
    }

    fun updateData(newEscuderias: List<Escuderia>) {
        escuderiasList.clear()
        escuderiasList.addAll(newEscuderias)
        notifyDataSetChanged()
    }

    class EscuderiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenEscuderia: ImageView = itemView.findViewById(R.id.imageViewEscuderia) // Asegúrate que el ID es correcto
        val nombreEscuderia: TextView = itemView.findViewById(R.id.textViewNombreEscuderia) // Asegúrate que el ID es correcto

        fun bind(escuderia: Escuderia, clickListener: OnEscuderiaClickListener, position: Int) {
            itemView.setOnClickListener {
                clickListener.onEscuderiaClick(escuderia)
            }
            // Configurar el Long Click Listener
            itemView.setOnLongClickListener {
                clickListener.onEscuderiaLongClick(escuderia, position)
                true // Devuelve true para indicar que el evento ha sido consumido
            }
        }
    }
}