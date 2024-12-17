package comidas

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectoclientedesayuno.R
import com.google.firebase.storage.FirebaseStorage
import modeloComida.Comida

class Comidas(private val comidas: MutableList<Comida>,
              private val listener: OnComidaSeleccionadaListener) : RecyclerView.Adapter<Comidas.ComidaViewHolder>() {
    private val selectedItems = mutableSetOf<Int>()

    inner class ComidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreComida)
        val caloriaTextView: TextView = itemView.findViewById(R.id.caloriasComida)
        val proteinasTextView: TextView = itemView.findViewById(R.id.proteinasComida)
        val comidaImageView: ImageView = itemView.findViewById(R.id.ivComida)

        init {
            itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.item_background)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComidaViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_comidas, parent, false)
        return ComidaViewHolder(itemView)
    }

    interface OnComidaSeleccionadaListener {
        fun onComidaSeleccionada(comida: Comida)
    }

    override fun onBindViewHolder(holder: ComidaViewHolder, position: Int) {

        val comida = comidas[position]
        holder.nombreTextView.text = comida.nombre
        holder.caloriaTextView.text = "Calorias: ${comida.calorias} kcal"
        holder.proteinasTextView.text = "Proteinas: ${comida.proteinas} g"

        /*val imageResourceId = holder.itemView.context.resources.getIdentifier(
            comida.nombreImagen, "drawable", holder.itemView.context.packageName
        )*/

        // Cargar la imagen desde Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference.child("comida/${comida.nombreImagen}.jpg")
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(holder.itemView.context)
                .load(uri.toString())
                .into(holder.comidaImageView)
        }.addOnFailureListener { exception ->
            // Manejar el error de descarga de la imagen, por ejemplo, mostrar una imagen predeterminada
            Log.e("AdaptadorComida", "Error al cargar la imagen: ${exception.message}")
        }

        //holder.comidaImageView.setImageResource(imageResourceId)

        holder.itemView.setOnClickListener {
            listener.onComidaSeleccionada(comidas[position])
            holder.itemView.isSelected = !holder.itemView.isSelected
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            holder.itemView.isSelected = selectedItems.contains(position)
            Toast.makeText(holder.itemView.context, "Selecinado: ${comida.nombre}", Toast.LENGTH_SHORT)
                .show()
            Log.d("ACSCO", "Selecinado: ${selectedItems.joinToString(", ")}")
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return comidas.size
    }

    fun getSelectedItems(): List<Int> {
        return selectedItems.toList()
    }
}