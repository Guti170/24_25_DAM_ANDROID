package circuitos

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

class CircuitoAdapter(
    private val circuitosList: MutableList<Circuito>,
    private val itemClickListener: OnItemClickListener,
    private val isAdmin: Boolean // Recibir el estado de admin
) : RecyclerView.Adapter<CircuitoAdapter.CircuitoViewHolder>() {

    private val storage = Firebase.storage

    interface OnItemClickListener {
        fun onCircuitoClick(circuito: Circuito)
        fun onCircuitoLongClick(circuito: Circuito, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircuitoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_circuito, parent, false)
        return CircuitoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CircuitoViewHolder, position: Int) {
        val currentItem = circuitosList[position]
        // Pasar isAdmin al método bind del ViewHolder
        holder.bind(currentItem, itemClickListener, position, isAdmin)

        holder.nombreCircuito.text = currentItem.nombre
        // Log.d("CircuitoAdapter", "Item: ${currentItem.nombre}, imagenUrl: '${currentItem.imagen}'")

        if (currentItem.imagen.startsWith("gs://")) {
            val storageRef = storage.getReferenceFromUrl(currentItem.imagen)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(holder.itemView.context)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image) // Define estos drawables
                    .error(R.drawable.error_image)             // Define estos drawables
                    .into(holder.imagenCircuito)
            }.addOnFailureListener { exception ->
                Log.e("CircuitoAdapter", "Fallo al obtener URL para ${currentItem.imagen}. Error: ${exception.message}")
                holder.imagenCircuito.setImageResource(R.drawable.error_image)
            }
        } else if (currentItem.imagen.trim().isNotEmpty()) { // Asumir URL HTTP/HTTPS
            Glide.with(holder.itemView.context)
                .load(currentItem.imagen)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imagenCircuito)
        } else {
            holder.imagenCircuito.setImageResource(R.drawable.placeholder_image) // Imagen por defecto si está vacía
        }
    }

    override fun getItemCount() = circuitosList.size

    class CircuitoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenCircuito: ImageView = itemView.findViewById(R.id.imageViewCircuito) // ID de tu layout de item
        val nombreCircuito: TextView = itemView.findViewById(R.id.textViewNombreCircuito) // ID de tu layout de item

        // Modificar bind para aceptar isAdmin
        fun bind(
            circuito: Circuito,
            clickListener: OnItemClickListener,
            position: Int,
            isAdmin: Boolean // Recibir isAdmin
        ) {
            itemView.setOnClickListener {
                clickListener.onCircuitoClick(circuito)
            }

            // Configuracion del LongClickListener si el usuario es admin
            if (isAdmin) {
                itemView.setOnLongClickListener {
                    clickListener.onCircuitoLongClick(circuito, position)
                    true // Indicar que el evento ha sido consumido
                }
            } else {
                // Si no es admin, asegurarse de que no haya un LongClickListener previo
                itemView.setOnLongClickListener(null)
            }
        }
    }
}