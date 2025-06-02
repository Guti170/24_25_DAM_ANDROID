package calendario

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

class CalendarioAdapter(private val eventosList: MutableList<EventoCalendario>) :
    RecyclerView.Adapter<CalendarioAdapter.EventoViewHolder>() {

    private val storage = Firebase.storage

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendario, parent, false)
        return EventoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val currentItem = eventosList[position]
        holder.nombreEvento.text = currentItem.nombre
        holder.horarioEvento.text = currentItem.horario // Asigna el horario

        Log.d("CalendarioAdapter", "Item: ${currentItem.nombre}, Imagen: '${currentItem.imagen}', Horario: ${currentItem.horario}")

        if (currentItem.imagen.startsWith("gs://")) {
            val storageRef = storage.getReferenceFromUrl(currentItem.imagen)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(holder.itemView.context)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.imagenEvento)
            }.addOnFailureListener { exception ->
                Log.e("CalendarioAdapter", "Fallo al obtener URL para ${currentItem.imagen}: ${exception.message}")
                holder.imagenEvento.setImageResource(R.drawable.error_image)
            }
        } else if (currentItem.imagen.trim().isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(currentItem.imagen)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.imagenEvento)
        } else {
            holder.imagenEvento.setImageResource(R.drawable.placeholder_image)
        }
    }

    override fun getItemCount() = eventosList.size

    fun updateData(newEventos: List<EventoCalendario>) {
        eventosList.clear()
        eventosList.addAll(newEventos)
        notifyDataSetChanged()
    }

    class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenEvento: ImageView = itemView.findViewById(R.id.imageViewEvento)
        val nombreEvento: TextView = itemView.findViewById(R.id.textViewNombreEvento)
        val horarioEvento: TextView = itemView.findViewById(R.id.textViewHorarioEvento) // TextView para el horario
    }
}