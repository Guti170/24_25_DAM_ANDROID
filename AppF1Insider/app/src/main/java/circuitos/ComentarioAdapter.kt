package circuitos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appf1insider.R
import java.text.SimpleDateFormat
import java.util.Locale

class ComentarioAdapter(private val comentarios: List<Comentario>) :
    RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder>() {

    // Formateador para la fecha y hora del comentario
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comentario, parent, false) // Crearemos este layout
        return ComentarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val comentario = comentarios[position]
        holder.bind(comentario)
    }

    override fun getItemCount(): Int = comentarios.size

    inner class ComentarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewUsuario: TextView = itemView.findViewById(R.id.textViewComentarioUsuario)
        private val textViewTexto: TextView = itemView.findViewById(R.id.textViewComentarioTexto)
        private val textViewTimestamp: TextView = itemView.findViewById(R.id.textViewComentarioTimestamp)

        fun bind(comentario: Comentario) {
            textViewUsuario.text = comentario.usuarioEmail // O podrías mostrar un nombre de usuario si lo tuvieras
            textViewTexto.text = comentario.texto
            textViewTimestamp.text = if (comentario.timestamp != null) {
                dateFormat.format(comentario.timestamp)
            } else {
                "Enviando..." // O alguna indicación de que el timestamp aún no está disponible
            }
        }
    }
}