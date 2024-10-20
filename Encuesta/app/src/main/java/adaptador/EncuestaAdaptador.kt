package adaptador

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.encuesta.R
import modelo.Alumno

class EncuestaAdaptador(private val ListaAlumnos: ArrayList<Alumno>) : RecyclerView.Adapter<EncuestaAdaptador.EncuestaViewHolder>() {
    private val selecionAlumnos = mutableSetOf<Int>()
    inner class EncuestaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val alumnoNombre: TextView = itemView.findViewById(R.id.encuestaNombre)
        val alumnoEspecialidad: TextView = itemView.findViewById(R.id.encuestaEspecialidad)
        val alumnoSistemaOperativo: TextView = itemView.findViewById(R.id.encuestaSistema)
        val alumnoHoras: TextView = itemView.findViewById(R.id.encuestaHoras)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EncuestaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_encuesta, parent, false)
        return EncuestaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EncuestaViewHolder, position: Int) {
        val alumno = ListaAlumnos[position]
        holder.alumnoNombre.text = alumno.nombre
        holder.alumnoEspecialidad.text = alumno.especialidad
        holder.alumnoSistemaOperativo.text = alumno.sistemaOperativo
        holder.alumnoHoras.text = alumno.horas.toString()

        holder.itemView.setOnClickListener {
            if (selecionAlumnos.contains(position)) {
                selecionAlumnos.remove(position)
            } else {
                selecionAlumnos.add(position)
            }
            Toast.makeText(holder.itemView.context, "Clicked: ${alumno.nombre}", Toast.LENGTH_SHORT).show()
            Log.d("ACSCO", "Clicked: ${selecionAlumnos.joinToString(", ")}")
            notifyItemChanged(position)
        }

        holder.itemView.setOnLongClickListener {
            val removedPosition = holder.adapterPosition
            Toast.makeText(holder.itemView.context, "Clicked: ${alumno.nombre}", Toast.LENGTH_SHORT).show()

            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Borrado")
                .setMessage("¿Estás seguro de que deseas eliminar el planeta?")
                .setPositiveButton("Borrar") { dialog, _ ->
                    val removedItem = ListaAlumnos.removeAt(removedPosition)
                    notifyItemRemoved(removedPosition)
                    notifyItemRangeChanged(removedPosition, ListaAlumnos.size - removedPosition)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            true
        }
    }

    override fun getItemCount(): Int {
        return ListaAlumnos.size
    }
    fun getSelectedItems(): List<Int> {
        return selecionAlumnos.toList()
    }
}