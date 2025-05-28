package com.example.appf1insider // O tu paquete de activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.appf1insider.R
import com.example.appf1insider.model.Piloto // Importa tu modelo Piloto
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DetallePilotoActivity : AppCompatActivity() {

    private lateinit var nombreTextView: TextView
    private lateinit var descripcionTextView: TextView
    private lateinit var estadisticasTextView: TextView // Para mostrar las estadísticas
    private lateinit var pilotoImageView: ImageView

    private val storage = Firebase.storage
    private val TAG = "DetallePilotoActivity"

    companion object {
        const val EXTRA_PILOTO = "extra_piloto" // Clave para el Intent extra
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_piloto) // Crearemos este layout a continuación

        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Botón de atrás en la ActionBar

        nombreTextView = findViewById(R.id.textViewDetalleNombrePiloto)
        descripcionTextView = findViewById(R.id.textViewDetalleDescripcionPiloto)
        estadisticasTextView = findViewById(R.id.textViewDetalleEstadisticasPiloto)
        pilotoImageView = findViewById(R.id.imageViewDetallePiloto)

        val piloto = intent.getParcelableExtra<Piloto>(EXTRA_PILOTO)

        if (piloto != null) {
            supportActionBar?.title = piloto.nombre // Título de la ActionBar
            nombreTextView.text = piloto.nombre
            descripcionTextView.text = piloto.descripcion
            estadisticasTextView.text = formatEstadisticas(piloto.estadisticas) // Formatear o mostrar directamente

            // Cargar imagen del piloto
            loadImage(piloto.imagen)
        } else {
            Log.e(TAG, "No se recibió el objeto Piloto.")
            Toast.makeText(this, "Error al cargar detalles del piloto", Toast.LENGTH_LONG).show()
            finish() // Cerrar la activity si no hay datos
        }
    }

    // Función para formatear las estadísticas si es necesario
    // Si 'estadisticas' es un String simple, puedes mostrarlo directamente.
    // Si es un JSON o una estructura más compleja, necesitarías parsearlo aquí.
    private fun formatEstadisticas(stats: String): String {
        // Ejemplo simple: si es un string con "Victorias: 10, Poles: 5"
        // podrías reemplazar comas por saltos de línea.
        // return stats.replace(", ", "\n")
        return stats // Por ahora, lo mostramos tal cual
    }

    private fun loadImage(imageUrl: String) {
        if (imageUrl.startsWith("gs://")) {
            val storageRef = storage.getReferenceFromUrl(imageUrl)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(pilotoImageView)
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Error al cargar imagen desde Storage: ${exception.message}")
                pilotoImageView.setImageResource(R.drawable.error_image)
            }
        } else if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(pilotoImageView)
        } else {
            Log.d(TAG, "URL de imagen vacía.")
            pilotoImageView.setImageResource(R.drawable.placeholder_image)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Manejar el clic en el botón de atrás de la ActionBar
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}