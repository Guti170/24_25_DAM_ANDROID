package circuitos

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.example.appf1insider.R


class DetalleCircuitoActivity : AppCompatActivity() {

    private lateinit var nombreTextView: TextView
    private lateinit var descripcionTextView: TextView
    private lateinit var circuitoImageView: ImageView
    private lateinit var circuitoVideoView: VideoView
    private lateinit var fabEditCircuito: FloatingActionButton
    private lateinit var buttonVerComentarios: Button

    private val storage = Firebase.storage
    private var circuitoRecibido: Circuito? = null
    private var isAdmin: Boolean = false

    companion object {
        const val EXTRA_CIRCUITO = "extra_circuito"
        private const val TAG = "DetalleCircuito"
    }

    private val editCircuitoLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == EditCircuitoActivity.RESULT_CIRCUITO_EDITADO) {
                val circuitoActualizado = result.data?.getParcelableExtra<Circuito>(
                    EditCircuitoActivity.EXTRA_CIRCUITO_ACTUALIZADO
                )
                if (circuitoActualizado != null) {
                    Log.d(TAG, "Circuito actualizado: ${circuitoActualizado.nombre}")
                    circuitoRecibido = circuitoActualizado
                    cargarDatosEnUI(circuitoRecibido)
                    setResult(Activity.RESULT_OK, Intent()) // Notificar cambio
                }
            }
        }

    @SuppressLint("MissingInflatedId") // Considera usar ViewBinding o Kotlin View Extensions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_circuito)

        nombreTextView = findViewById(R.id.textViewDetalleNombreCircuito)
        descripcionTextView = findViewById(R.id.textViewDetalleDescripcionCircuito)
        circuitoImageView = findViewById(R.id.imageViewDetalleCircuito)
        circuitoVideoView = findViewById(R.id.videoViewDetalleCircuito)
        fabEditCircuito = findViewById(R.id.fabEditCircuito)
        buttonVerComentarios = findViewById(R.id.buttonVerComentarios) // Asegúrate que este ID existe en tu layout

        isAdmin = intent.getBooleanExtra("IS_ADMIN_USER", false)
        Log.d(TAG, "Usuario es admin: $isAdmin")

        circuitoRecibido = intent.getParcelableExtra(EXTRA_CIRCUITO)

        if (circuitoRecibido != null) {
            cargarDatosEnUI(circuitoRecibido)
            setupEditFab(circuitoRecibido!!)
        } else {
            Log.e(TAG, "No se recibió el objeto Circuito.")
            Toast.makeText(this, "Error al cargar detalles.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        buttonVerComentarios.setOnClickListener {
            circuitoRecibido?.let {
                if (it.id.isNotEmpty()) {
                    val intent = Intent(this, ComentariosActivity::class.java).apply {
                        putExtra(ComentariosActivity.EXTRA_CIRCUITO_ID, it.id)
                        putExtra(ComentariosActivity.EXTRA_NOMBRE_CIRCUITO, it.nombre)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "ID de circuito no disponible.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun cargarDatosEnUI(circuito: Circuito?) {
        circuito ?: return // Salir si es nulo

        nombreTextView.text = circuito.nombre
        descripcionTextView.text = circuito.descripcion
        supportActionBar?.title = circuito.nombre

        // Cargar imagen
        if (circuito.imagen.startsWith("gs://")) {
            storage.getReferenceFromUrl(circuito.imagen).downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this).load(uri).placeholder(R.drawable.placeholder_image).error(R.drawable.error_image).into(circuitoImageView)
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error img: ${e.message}"); circuitoImageView.setImageResource(R.drawable.error_image)
            }
        } else if (circuito.imagen.isNotEmpty()) {
            Glide.with(this).load(circuito.imagen).placeholder(R.drawable.placeholder_image).error(R.drawable.error_image).into(circuitoImageView)
        } else {
            circuitoImageView.setImageResource(R.drawable.placeholder_image)
        }

        // Configurar VideoView
        if (circuito.video.startsWith("gs://")) {
            storage.getReferenceFromUrl(circuito.video).downloadUrl.addOnSuccessListener { videoUri ->
                setupVideoView(videoUri)
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error video URL: ${e.message}"); circuitoVideoView.visibility = View.GONE
            }
        } else if (circuito.video.startsWith("http")) {
            setupVideoView(Uri.parse(circuito.video))
        } else {
            circuitoVideoView.visibility = View.GONE
        }
    }

    private fun setupVideoView(videoUri: Uri) {
        circuitoVideoView.setVideoURI(videoUri)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(circuitoVideoView)
        circuitoVideoView.setMediaController(mediaController)
        circuitoVideoView.visibility = View.VISIBLE
        circuitoVideoView.setOnErrorListener { _, _, _ ->
            Log.e(TAG, "Error al reproducir video."); circuitoVideoView.visibility = View.GONE; true
        }
    }

    private fun setupEditFab(circuito: Circuito) {
        if (isAdmin) {
            fabEditCircuito.visibility = View.VISIBLE
            fabEditCircuito.setOnClickListener {
                if (circuito.id.isNotEmpty()) {
                    val intent = Intent(this, EditCircuitoActivity::class.java).apply {
                        putExtra(EditCircuitoActivity.EXTRA_CIRCUITO_EDIT, circuito)
                    }
                    editCircuitoLauncher.launch(intent)
                } else {
                    Toast.makeText(this, "No se puede editar (ID no válido).", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            fabEditCircuito.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Manejar el clic en el botón de "atrás" de la ActionBar
        if (item.itemId == android.R.id.home) {
            // onBackPressedDispatcher.onBackPressed() // Opción moderna
            finish() // Opción simple, funciona bien si no hay lógica compleja de back stack
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}