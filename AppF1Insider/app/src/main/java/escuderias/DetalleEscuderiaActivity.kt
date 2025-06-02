package escuderias // O tu paquete de activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
// import android.net.Uri // Necesario si Glide usa URIs directamente
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button // Asegúrate que este import está
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.appf1insider.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DetalleEscuderiaActivity : AppCompatActivity() {

    private lateinit var nombreTextView: TextView
    private lateinit var descripcionTextView: TextView
    private lateinit var estadisticasTextView: TextView
    private lateinit var escuderiaImageView: ImageView
    private lateinit var fabEditEscuderia: FloatingActionButton
    private lateinit var buttonVerComentariosEscuderia: Button // Botón para comentarios

    private val storage = Firebase.storage
    private var escuderiaRecibida: Escuderia? = null
    private val TAG = "DetalleEscuderiaAct"

    companion object {
        const val EXTRA_ESCUDERIA = "extra_escuderia"
    }

    private val editEscuderiaLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == EditEscuderiaActivity.RESULT_ESCUDERIA_EDITADA) {
                val escuderiaActualizada = result.data?.getParcelableExtra<Escuderia>(
                    EditEscuderiaActivity.EXTRA_ESCUDERIA_ACTUALIZADA
                )
                if (escuderiaActualizada != null) {
                    Log.d(TAG, "Escudería actualizada recibida: ${escuderiaActualizada.nombre}")
                    escuderiaRecibida = escuderiaActualizada
                    cargarDatosEnUI(escuderiaRecibida)
                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK, resultIntent)
                } else {
                    Log.d(TAG, "EditEscuderiaActivity finalizó con RESULT_ESCUDERIA_EDITADA pero sin datos actualizados.")
                }
            } else {
                Log.d(TAG, "EditEscuderiaActivity finalizó con código: ${result.resultCode}")
            }
        }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_escuderia)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nombreTextView = findViewById(R.id.textViewDetalleNombreEscuderia)
        descripcionTextView = findViewById(R.id.textViewDetalleDescripcionEscuderia)
        estadisticasTextView = findViewById(R.id.textViewDetalleEstadisticasEscuderia)
        escuderiaImageView = findViewById(R.id.imageViewDetalleEscuderia)
        fabEditEscuderia = findViewById(R.id.fabEditEscuderia)
        buttonVerComentariosEscuderia = findViewById(R.id.buttonVerComentariosEscuderia) // Inicializar el botón

        escuderiaRecibida = intent.getParcelableExtra(EXTRA_ESCUDERIA)

        if (escuderiaRecibida != null) {
            cargarDatosEnUI(escuderiaRecibida)
        } else {
            Log.e(TAG, "No se recibió el objeto Escuderia.")
            Toast.makeText(this, "Error al cargar detalles de la escudería", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        fabEditEscuderia.setOnClickListener {
            if (escuderiaRecibida != null && escuderiaRecibida!!.id.isNotEmpty()) {
                val intent = Intent(this, EditEscuderiaActivity::class.java).apply {
                    putExtra(EditEscuderiaActivity.EXTRA_ESCUDERIA_EDIT, escuderiaRecibida)
                }
                editEscuderiaLauncher.launch(intent) // Lanzar la actividad de edición
            } else {
                Toast.makeText(this, "No se puede editar la escudería (datos incompletos).", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Intento de editar escudería con datos nulos o ID vacío.")
            }
        }

        buttonVerComentariosEscuderia.setOnClickListener {
            if (escuderiaRecibida != null && escuderiaRecibida!!.id.isNotEmpty()) {
                val intent = Intent(this, ComentariosEscuderiaActivity::class.java).apply {
                    putExtra(ComentariosEscuderiaActivity.EXTRA_ESCUDERIA_ID, escuderiaRecibida!!.id)
                    putExtra(ComentariosEscuderiaActivity.EXTRA_NOMBRE_ESCUDERIA, escuderiaRecibida!!.nombre)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "No se pueden cargar los comentarios (ID de la escudería no disponible).", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarDatosEnUI(escuderia: Escuderia?) {
        if (escuderia == null) {
            Log.e(TAG, "Intento de cargar UI con escudería nula.")
            return
        }

        supportActionBar?.title = escuderia.nombre
        nombreTextView.text = escuderia.nombre
        descripcionTextView.text = escuderia.descripcion
        estadisticasTextView.text = escuderia.estadisticas // Asumiendo que es un String simple

        loadImage(escuderia.imagen)
    }

    private fun loadImage(imageUrl: String) {
        if (imageUrl.startsWith("gs://")) {
            val storageRef = storage.getReferenceFromUrl(imageUrl)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image) // Asegúrate que estos drawables existen
                    .error(R.drawable.error_image)             // Asegúrate que estos drawables existen
                    .into(escuderiaImageView)
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Error al cargar imagen desde Storage: ${exception.message}")
                escuderiaImageView.setImageResource(R.drawable.error_image)
            }
        } else if (imageUrl.isNotEmpty()) { // Asume que es una URL HTTP/HTTPS
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(escuderiaImageView)
        } else {
            Log.d(TAG, "URL de imagen vacía o no es de Storage.")
            escuderiaImageView.setImageResource(R.drawable.placeholder_image) // Imagen por defecto
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed() // Maneja el botón "Up" de la ActionBar
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}