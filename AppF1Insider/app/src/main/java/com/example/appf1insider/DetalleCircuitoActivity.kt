package com.example.appf1insider // O tu paquete de activities

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import com.bumptech.glide.Glide
import com.example.appf1insider.model.Circuito
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DetalleCircuitoActivity : AppCompatActivity() {

    private lateinit var nombreTextView: TextView
    private lateinit var descripcionTextView: TextView
    private lateinit var circuitoImageView: ImageView
    private lateinit var circuitoVideoView: VideoView

    private val storage = Firebase.storage

    companion object {
        const val EXTRA_CIRCUITO = "extra_circuito" // Key para pasar el objeto Circuito
        private const val TAG = "DetalleCircuito"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_circuito)

        nombreTextView = findViewById(R.id.textViewDetalleNombreCircuito)
        descripcionTextView = findViewById(R.id.textViewDetalleDescripcionCircuito)
        circuitoImageView = findViewById(R.id.imageViewDetalleCircuito)
        circuitoVideoView = findViewById(R.id.videoViewDetalleCircuito)

        // Recuperar el objeto Circuito del Intent
        val circuito = intent.getParcelableExtra<Circuito>(EXTRA_CIRCUITO)

        if (circuito != null) {
            // Configurar la UI con los datos del circuito
            nombreTextView.text = circuito.nombre
            descripcionTextView.text = circuito.descripcion

            // Cargar imagen
            if (circuito.imagen.startsWith("gs://")) {
                val storageRef = storage.getReferenceFromUrl(circuito.imagen)
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(circuitoImageView)
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Error al cargar imagen: ${exception.message}")
                    circuitoImageView.setImageResource(R.drawable.error_image)
                }
            } else if (circuito.imagen.isNotEmpty()) {
                Glide.with(this)
                    .load(circuito.imagen) // Asumiendo que podría ser una URL HTTPS directa
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(circuitoImageView)
            } else {
                circuitoImageView.setImageResource(R.drawable.placeholder_image)
            }

            // Configurar VideoView si hay URL de video
            if (circuito.video.startsWith("gs://")) {
                val videoStorageRef = storage.getReferenceFromUrl(circuito.video)
                videoStorageRef.downloadUrl.addOnSuccessListener { videoUri ->
                    Log.d(TAG, "Video URL obtenida: $videoUri")
                    setupVideoView(videoUri)
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Error al obtener URL del video: ${exception.message}")
                    // Ocultar VideoView o mostrar un mensaje
                    circuitoVideoView.visibility = android.view.View.GONE
                }
            } else if (circuito.video.startsWith("http://") || circuito.video.startsWith("https://")) {
                // Si es una URL directa (menos probable si usas Storage para todo)
                Log.d(TAG, "Video URL directa: ${circuito.video}")
                setupVideoView(Uri.parse(circuito.video))
            }
            else {
                Log.d(TAG, "No hay URL de video válida.")
                circuitoVideoView.visibility = android.view.View.GONE
            }

        } else {
            // Manejar el caso donde no se pasó el circuito (error)
            Log.e(TAG, "No se recibió el objeto Circuito.")
            // Podrías cerrar la activity o mostrar un mensaje de error
            finish()
        }

        // Configurar el botón de atrás en la ActionBar (opcional)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = circuito?.nombre ?: "Detalles del Circuito"
    }

    private fun setupVideoView(videoUri: Uri) {
        circuitoVideoView.setVideoURI(videoUri)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(circuitoVideoView)
        circuitoVideoView.setMediaController(mediaController)
        circuitoVideoView.visibility = android.view.View.VISIBLE
        // Opcional: Iniciar reproducción automáticamente
        // circuitoVideoView.start()
        circuitoVideoView.setOnPreparedListener { mp ->
            // El video está listo para reproducirse
            // Puedes iniciar la reproducción aquí si no lo hiciste antes
            // mp.start()
            // Opcional: Ajustar el tamaño del VideoView al del video
            // val videoWidth = mp.videoWidth
            // val videoHeight = mp.videoHeight
            // Ajusta los layoutParams del VideoView si es necesario
        }
        circuitoVideoView.setOnErrorListener { mp, what, extra ->
            Log.e(TAG, "Error al reproducir video: what $what, extra $extra")
            // Ocultar VideoView o mostrar un mensaje de error
            circuitoVideoView.visibility = android.view.View.GONE
            true // Indica que el error ha sido manejado
        }
    }


    // Para manejar el clic en el botón de atrás de la ActionBar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}