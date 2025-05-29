package com.example.appf1insider // O tu paquete de activities

import android.annotation.SuppressLint
import android.app.Activity // Necesario para Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.appf1insider.model.Circuito
import com.google.android.material.floatingactionbutton.FloatingActionButton // Importar FAB
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.example.appf1insider.R // Asegúrate de importar R

class DetalleCircuitoActivity : AppCompatActivity() {

    private lateinit var nombreTextView: TextView
    private lateinit var descripcionTextView: TextView
    private lateinit var circuitoImageView: ImageView
    private lateinit var circuitoVideoView: VideoView
    private lateinit var fabEditCircuito: FloatingActionButton // FAB para editar

    private val storage = Firebase.storage
    private var circuitoRecibido: Circuito? = null // Para mantener el circuito actual que se muestra

    companion object {
        const val EXTRA_CIRCUITO = "extra_circuito"
        private const val TAG = "DetalleCircuito"
    }

    // ActivityResultLauncher para manejar el resultado de EditCircuitoActivity
    private val editCircuitoLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == EditCircuitoActivity.RESULT_CIRCUITO_EDITADO) {
                val circuitoActualizado = result.data?.getParcelableExtra<Circuito>(EditCircuitoActivity.EXTRA_CIRCUITO_ACTUALIZADO)
                if (circuitoActualizado != null) {
                    Log.d(TAG, "Circuito actualizado recibido: ${circuitoActualizado.nombre}")
                    circuitoRecibido = circuitoActualizado // Actualizar el circuito local
                    cargarDatosEnUI(circuitoRecibido) // Recargar la UI con los nuevos datos
                    // Notificar al fragmento anterior que los datos han cambiado
                    // Esto es importante para que la lista en CircuitosFragment se actualice
                    // si el usuario vuelve atrás.
                    val resultIntent = Intent()
                    // No es necesario pasar el circuito de vuelta aquí si CircuitosFragment
                    // simplemente recarga desde Firestore. Pero si quieres optimizar, puedes pasarlo.
                    setResult(Activity.RESULT_OK, resultIntent) // Indica que algo cambió
                } else {
                    Log.d(TAG, "EditCircuitoActivity finalizó con RESULT_CIRCUITO_EDITADO pero sin datos actualizados.")
                }
            } else {
                Log.d(TAG, "EditCircuitoActivity finalizó con código: ${result.resultCode}")
            }
        }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_circuito)

        nombreTextView = findViewById(R.id.textViewDetalleNombreCircuito)
        descripcionTextView = findViewById(R.id.textViewDetalleDescripcionCircuito)
        circuitoImageView = findViewById(R.id.imageViewDetalleCircuito)
        circuitoVideoView = findViewById(R.id.videoViewDetalleCircuito)
        fabEditCircuito = findViewById(R.id.fabEditCircuito) // Inicializar el FAB

        // Recuperar el objeto Circuito del Intent
        circuitoRecibido = intent.getParcelableExtra(EXTRA_CIRCUITO)

        if (circuitoRecibido != null) {
            cargarDatosEnUI(circuitoRecibido)
        } else {
            Log.e(TAG, "No se recibió el objeto Circuito.")
            Toast.makeText(this, "Error al cargar detalles del circuito.", Toast.LENGTH_SHORT).show()
            finish()
        }

        fabEditCircuito.setOnClickListener {
            if (circuitoRecibido != null && circuitoRecibido!!.id.isNotEmpty()) {
                val intent = Intent(this, EditCircuitoActivity::class.java).apply {
                    putExtra(EditCircuitoActivity.EXTRA_CIRCUITO_EDIT, circuitoRecibido)
                }
                editCircuitoLauncher.launch(intent)
            } else {
                Toast.makeText(this, "No se puede editar el circuito (datos incompletos).", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Intento de editar circuito con datos nulos o ID vacío.")
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun cargarDatosEnUI(circuito: Circuito?) {
        if (circuito == null) {
            Log.e(TAG, "Intento de cargar UI con circuito nulo.")
            return
        }

        nombreTextView.text = circuito.nombre
        descripcionTextView.text = circuito.descripcion
        supportActionBar?.title = circuito.nombre ?: "Detalles del Circuito"

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
                .load(circuito.imagen)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(circuitoImageView)
        } else {
            circuitoImageView.setImageResource(R.drawable.placeholder_image)
        }

        // Configurar VideoView
        if (circuito.video.startsWith("gs://")) {
            val videoStorageRef = storage.getReferenceFromUrl(circuito.video)
            videoStorageRef.downloadUrl.addOnSuccessListener { videoUri ->
                setupVideoView(videoUri)
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Error al obtener URL del video: ${exception.message}")
                circuitoVideoView.visibility = android.view.View.GONE
            }
        } else if (circuito.video.startsWith("http://") || circuito.video.startsWith("https://")) {
            setupVideoView(Uri.parse(circuito.video))
        } else {
            circuitoVideoView.visibility = android.view.View.GONE
        }
    }

    private fun setupVideoView(videoUri: Uri) {
        circuitoVideoView.setVideoURI(videoUri)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(circuitoVideoView)
        circuitoVideoView.setMediaController(mediaController)
        circuitoVideoView.visibility = android.view.View.VISIBLE
        circuitoVideoView.setOnPreparedListener { mp ->
            // Video listo
        }
        circuitoVideoView.setOnErrorListener { mp, what, extra ->
            Log.e(TAG, "Error al reproducir video: what $what, extra $extra")
            circuitoVideoView.visibility = android.view.View.GONE
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}