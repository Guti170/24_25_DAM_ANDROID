package bebidas

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
import modeloBebida.Bebida

class Bebidas(private val bebidas: MutableList<Bebida>,
              private val listener: OnBebidaSeleccionadaListener) : RecyclerView.Adapter<Bebidas.BebidaViewHolder>() {
    private val selectedItems = mutableSetOf<Int>()


    inner class BebidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreBebida)
        val caloriaTextView: TextView = itemView.findViewById(R.id.caloriasBebida)
        val proteinasTextView: TextView = itemView.findViewById(R.id.proteinasBebida)
        val bebidaImageView: ImageView = itemView.findViewById(R.id.ivBebida)

        init {
            itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.item_background)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BebidaViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_bebidas, parent, false)
        return BebidaViewHolder(itemView)
    }

    interface OnBebidaSeleccionadaListener {
        fun onBebidaSeleccionada(bebida: Bebida)
    }

    override fun onBindViewHolder(holder: BebidaViewHolder, position: Int) {

        val bebida = bebidas[position]
        holder.nombreTextView.text = bebida.nombre
        holder.caloriaTextView.text = "Calorias: ${bebida.calorias} kcal"
        holder.proteinasTextView.text = "Proteinas: ${bebida.proteinas} g"

        /*val imageResourceId = holder.itemView.context.resources.getIdentifier(
            bebida.nombreImagen, "drawable", holder.itemView.context.packageName
        )*/

        // Cargar la imagen desde Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference.child("bebida/${bebida.nombreImagen}.jpg")
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(holder.itemView.context)
                .load(uri.toString())
                .into(holder.bebidaImageView)
        }.addOnFailureListener { exception ->
            // Manejar el error de descarga de la imagen, por ejemplo, mostrar una imagen predeterminada
            Log.e("AdaptadorComida", "Error al cargar la imagen: ${exception.message}")
        }

        //holder.bebidaImageView.setImageResource(imageResourceId)

        holder.itemView.setOnClickListener {
            listener.onBebidaSeleccionada(bebidas[position])
            holder.itemView.isSelected = !holder.itemView.isSelected
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
            } else {
                selectedItems.add(position)
            }
            holder.itemView.isSelected = selectedItems.contains(position)
            Toast.makeText(holder.itemView.context, "Selecinado: ${bebida.nombre}", Toast.LENGTH_SHORT)
                .show()
            Log.d("ACSCO", "Selecinado: ${selectedItems.joinToString(", ")}")
            notifyItemChanged(position)
        }

    }

    override fun getItemCount(): Int {
        return bebidas.size
    }

    fun getSelectedItems(): List<Int> {
        return selectedItems.toList()
    }
}