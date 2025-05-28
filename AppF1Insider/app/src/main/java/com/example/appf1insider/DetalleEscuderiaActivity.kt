package com.example.appf1insider // O tu paquete de activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.appf1insider.R
import com.example.appf1insider.model.Escuderia // Importa tu modelo Escuderia
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DetalleEscuderiaActivity : AppCompatActivity() {

    private lateinit var nombreTextView: TextView
    private lateinit var descripcionTextView: TextView
    private lateinit var estadisticasTextView: TextView
    private lateinit var escuderiaImageView: ImageView

    private val storage = Firebase.storage
    private val TAG = "DetalleEscuderiaAct" // Tag para logs

    companion object {
        const val EXTRA_ESCUDERIA = "extra_escuderia" // Clave para el Intent extra
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_escuderia) // Crearemos este layout a continuación

        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Botón de atrás

        nombreTextView = findViewById(R.id.textViewDetalleNombreEscuderia)
        descripcionTextView = findViewById(R.id.textViewDetalleDescripcionEscuderia)
        estadisticasTextView = findViewById(R.id.textViewDetalleEstadisticasEscuderia)
        escuderiaImageView = findViewById(R.id.imageViewDetalleEscuderia)

        val escuderia = intent.getParcelableExtra<Escuderia>(EXTRA_ESCUDERIA)

        if (escuderia != null) {
            supportActionBar?.title = escuderia.nombre
            nombreTextView.text = escuderia.nombre
            descripcionTextView.text = escuderia.descripcion
            // Aquí podrías formatear 'escuderia.estadisticas' si es necesario
            estadisticasTextView.text = escuderia.estadisticas

            loadImage(escuderia.imagen)
        } else {
            Log.e(TAG, "No se recibió el objeto Escuderia.")
            Toast.makeText(this, "Error al cargar detalles de la escudería", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun loadImage(imageUrl: String) {
        if (imageUrl.startsWith("gs://")) {
            val storageRef = storage.getReferenceFromUrl(imageUrl)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(escuderiaImageView)
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Error al cargar imagen desde Storage: ${exception.message}")
                escuderiaImageView.setImageResource(R.drawable.error_image)
            }
        } else if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(escuderiaImageView)
        } else {
            Log.d(TAG, "URL de imagen vacía.")
            escuderiaImageView.setImageResource(R.drawable.placeholder_image)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}