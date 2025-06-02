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
    private val itemClickListener: OnEscuderiaClickListener,
    private val isAdmin: Boolean // Añadir isAdmin al constructor
) : RecyclerView.Adapter<EscuderiaAdapter.EscuderiaViewHolder>() {

    private val storage = Firebase.storage

    interface OnEscuderiaClickListener {
        fun onEscuderiaClick(escuderia: Escuderia)
        fun onEscuderiaLongClick(escuderia: Escuderia, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EscuderiaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_escuderia, parent, false)
        return EscuderiaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EscuderiaViewHolder, position: Int) {
        val currentItem = escuderiasList[position]
        // Pasar isAdmin al método bind del ViewHolder
        holder.bind(currentItem, itemClickListener, position, isAdmin)

        holder.nombreEscuderia.text = currentItem.nombre
        Log.d("EscuderiaAdapter", "Item: ${currentItem.nombre}, imagenUrl de Firestore: '${currentItem.imagen}'")

        // Cargar imagen principal de la escudería
        loadImageIntoView(currentItem.imagen, holder.imagenEscuderia, holder)
    }

    private fun loadImageIntoView(imageUrl: String?, imageView: ImageView, holder: EscuderiaViewHolder) {
        if (imageUrl.isNullOrEmpty()) {
            imageView.setImageResource(R.drawable.placeholder_image) // Imagen por defecto
            return
        }

        if (imageUrl.startsWith("gs://")) {
            val storageRef = storage.getReferenceFromUrl(imageUrl)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(holder.itemView.context)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imageView)
            }.addOnFailureListener { exception ->
                Log.e("EscuderiaAdapter", "FALLO al obtener URL de descarga para $imageUrl. Error: ${exception.message}", exception)
                imageView.setImageResource(R.drawable.error_image)
            }
        } else { // Asume URL HTTP/HTTPS
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imageView)
        }
    }


    override fun getItemCount() = escuderiasList.size

    fun removeItem(position: Int) {
        if (position >= 0 && position < escuderiasList.size) {
            escuderiasList.removeAt(position)
            notifyItemRemoved(position)
            // Considera notifyItemRangeChanged si la eliminación afecta a otros elementos visualmente
            // notifyItemRangeChanged(position, escuderiasList.size)
        }
    }

    fun updateData(newEscuderias: List<Escuderia>) {
        escuderiasList.clear()
        escuderiasList.addAll(newEscuderias)
        notifyDataSetChanged()
    }

    class EscuderiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenEscuderia: ImageView = itemView.findViewById(R.id.imageViewEscuderia)
        val nombreEscuderia: TextView = itemView.findViewById(R.id.textViewNombreEscuderia)

        // Modificar bind para aceptar isAdmin
        fun bind(
            escuderia: Escuderia,
            clickListener: OnEscuderiaClickListener,
            position: Int,
            isAdmin: Boolean // Recibir isAdmin
        ) {
            itemView.setOnClickListener {
                clickListener.onEscuderiaClick(escuderia)
            }
            // Configurar el Long Click Listener solo si es admin
            if (isAdmin) {
                itemView.setOnLongClickListener {
                    clickListener.onEscuderiaLongClick(escuderia, position)
                    true // Devuelve true para indicar que el evento ha sido consumido
                }
            } else {
                itemView.setOnLongClickListener(null) // No permitir long click si no es admin
            }
        }
    }
}