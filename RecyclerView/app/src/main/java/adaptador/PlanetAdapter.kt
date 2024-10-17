package adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview.R
import modelo.Planet

class PlanetAdapter(private val planets: MutableList<Planet>) :
    RecyclerView.Adapter<PlanetAdapter.PlanetViewHolder>() {
    private val selectedItems = mutableSetOf<Int>()
    inner class PlanetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.planetName)
        val sizeTextView: TextView = itemView.findViewById(R.id.planetSize)
        val distanceTextView: TextView = itemView.findViewById(R.id.planetDistance)

    }
    //Este método se llama cuando se necesita crear un nuevo ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanetViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_planet, parent, false)
        return PlanetViewHolder(itemView)
    }
    //Este método se llama para actualizar el contenido de un ViewHolder existente
    override fun onBindViewHolder(holder: PlanetViewHolder, position: Int) {

        //holder.planetaImageView.setImageResource( R.drawable.saturno)
        val planet = planets[position]
        holder.nameTextView.text = planet.name
        holder.sizeTextView.text = "Size: ${planet.sizeKM} km"
        holder.distanceTextView.text = "Distance: ${planet.distanceAU} AU"

        /*val imageResourceId = holder.itemView.context.resources.getIdentifier( // Use holder.itemView.context
            planet.imageName,"drawable",holder.itemView.context.packageName)

        //definimos el evento click para cada elemento de la lista
        holder.itemView.setOnClickListener {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            Toast.makeText(holder.itemView.context, "Clicked: ${planet.name}", Toast.LENGTH_SHORT).show()
            Log.d("ACSCO", "Clicked: ${selectedItems.joinToString(", ")}")
            notifyItemChanged(position) // Avisas que ha habido un cambio en la posición y así pasa por el onBindViewHolder de nuevo y en el último if repinta.
        }

        //definimos el evento long click para cada elemento de la lista
        holder.itemView.setOnLongClickListener {
            val removedPosition = holder.adapterPosition
            Toast.makeText(holder.itemView.context, "Clicked: ${planet.name}", Toast.LENGTH_SHORT).show()

            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Borrado")
                .setMessage("¿Estás seguro de que deseas eliminar el planeta?")
                .setPositiveButton("Borrar") { dialog, _ ->
                    val removedItem = planets.removeAt(removedPosition) // Remove from data list
                    notifyItemRemoved(removedPosition) // Notify adapter
                    // Update selected items positions
                    // Update indices from removed position to the end
                    notifyItemRangeChanged(removedPosition, planets.size - removedPosition)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()

            true // Indicar que el evento ha sido consumido
        }

        //Si está o no seleccionado, cambiamos el color del fondo
        if (selectedItems.contains(position)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.item_seleccionado))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.item_no_seleccionado))
        }*/
    }

    override fun getItemCount(): Int {
        return planets.size
    }
    fun getSelectedItems(): List<Int> {
        return selectedItems.toList()
    }
}