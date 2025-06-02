package pilotos

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

class PilotoAdapter(
    private val pilotosList: MutableList<Piloto>,
    private val itemClickListener: OnPilotoClickListener,
    private val isAdmin: Boolean // Recibir el estado de administrador
) : RecyclerView.Adapter<PilotoAdapter.PilotoViewHolder>() {

    private val storage = Firebase.storage // Completar la inicialización

    interface OnPilotoClickListener {
        fun onPilotoClick(piloto: Piloto)
        fun onPilotoLongClick(piloto: Piloto, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PilotoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_piloto, parent, false) // Asegúrate que R.layout.item_piloto existe
        return PilotoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PilotoViewHolder, position: Int) {
        val currentItem = pilotosList[position]
        // Pasar isAdmin al método bind del ViewHolder
        holder.bind(currentItem, itemClickListener, position, isAdmin)

        holder.nombrePiloto.text = currentItem.nombre
        // Log.d("PilotoAdapter", "Item: ${currentItem.nombre}, imagenUrl: '${currentItem.imagen}'") // Comentado para reducir logs

        if (currentItem.imagen.startsWith("gs://")) {
            val storageRef = storage.getReferenceFromUrl(currentItem.imagen)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(holder.itemView.context)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image) // Asegúrate que estos drawables existen
                    .error(R.drawable.error_image)             // Asegúrate que estos drawables existen
                    .into(holder.imagenPiloto)
            }.addOnFailureListener { exception ->
                Log.e("PilotoAdapter", "Fallo al obtener URL para ${currentItem.imagen}. Error: ${exception.message}")
                holder.imagenPiloto.setImageResource(R.drawable.error_image)
            }
        } else if (currentItem.imagen.trim().isNotEmpty()) { // Asume URL HTTP/HTTPS
            Glide.with(holder.itemView.context)
                .load(currentItem.imagen)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imagenPiloto)
        } else {
            holder.imagenPiloto.setImageResource(R.drawable.placeholder_image) // Imagen por defecto
        }
    }

    override fun getItemCount() = pilotosList.size

    // Este método ya no es estrictamente necesario si el Fragmento maneja la lista
    // y notifica los cambios específicos (notifyItemRemoved, etc.)
    // Pero si lo usas desde el fragmento, está bien.
    fun removeItem(position: Int) {
        if (position >= 0 && position < pilotosList.size) {
            pilotosList.removeAt(position)
            notifyItemRemoved(position)
            // notifyItemRangeChanged(position, pilotosList.size) // Opcional
        }
    }

    // Útil si quieres reemplazar toda la lista de una vez
    fun updateData(newPilotos: List<Piloto>) {
        pilotosList.clear()
        pilotosList.addAll(newPilotos)
        notifyDataSetChanged()
    }

    class PilotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenPiloto: ImageView = itemView.findViewById(R.id.imageViewPiloto) // Asegúrate que el ID es correcto
        val nombrePiloto: TextView = itemView.findViewById(R.id.textViewNombrePiloto) // Asegúrate que el ID es correcto

        // Añadir isAdmin al método bind
        fun bind(piloto: Piloto, clickListener: OnPilotoClickListener, position: Int, isAdmin: Boolean) {
            itemView.setOnClickListener {
                clickListener.onPilotoClick(piloto)
            }
            // Configurar el Long Click Listener solo si es admin
            if (isAdmin) {
                itemView.setOnLongClickListener {
                    clickListener.onPilotoLongClick(piloto, position)
                    true // Devuelve true para indicar que el evento ha sido consumido
                }
            } else {
                itemView.isLongClickable = false // Deshabilitar long click si no es admin
            }
        }
    }
}