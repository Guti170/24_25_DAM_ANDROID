package escuderias

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
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
    private lateinit var buttonVerComentariosEscuderia: Button

    private val storage = Firebase.storage
    private var escuderiaRecibida: Escuderia? = null
    private var isAdmin: Boolean = false
    private val TAG = "DetalleEscuderiaAct"

    companion object {
        const val EXTRA_ESCUDERIA = "extra_escuderia"
    }

    private val editEscuderiaLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == EditEscuderiaActivity.RESULT_ESCUDERIA_EDITADA) {
                result.data?.getParcelableExtra<Escuderia>(EditEscuderiaActivity.EXTRA_ESCUDERIA_ACTUALIZADA)?.let {
                    escuderiaRecibida = it
                    cargarDatosEnUI(it)
                    setResult(Activity.RESULT_OK, Intent()) // Notificar para recargar
                }
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
        buttonVerComentariosEscuderia = findViewById(R.id.buttonVerComentariosEscuderia)

        isAdmin = intent.getBooleanExtra("IS_ADMIN_USER", false)
        escuderiaRecibida = intent.getParcelableExtra(EXTRA_ESCUDERIA)

        if (escuderiaRecibida == null) {
            Toast.makeText(this, "Error al cargar detalles.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        cargarDatosEnUI(escuderiaRecibida)

        fabEditEscuderia.visibility = if (isAdmin) View.VISIBLE else View.GONE
        if (isAdmin) {
            fabEditEscuderia.setOnClickListener {
                escuderiaRecibida?.let { escuderia ->
                    if (escuderia.id.isNotEmpty()) {
                        val intent = Intent(this, EditEscuderiaActivity::class.java).apply {
                            putExtra(EditEscuderiaActivity.EXTRA_ESCUDERIA_EDIT, escuderia)
                        }
                        editEscuderiaLauncher.launch(intent)
                    } else {
                        Toast.makeText(this, "ID de escudería no válido.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        buttonVerComentariosEscuderia.setOnClickListener {
            escuderiaRecibida?.let { escuderia ->
                if (escuderia.id.isNotEmpty()) {
                    val intent = Intent(this, ComentariosEscuderiaActivity::class.java).apply {
                        putExtra(ComentariosEscuderiaActivity.EXTRA_ESCUDERIA_ID, escuderia.id)
                        putExtra(ComentariosEscuderiaActivity.EXTRA_NOMBRE_ESCUDERIA, escuderia.nombre)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "ID de escudería no disponible.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cargarDatosEnUI(escuderia: Escuderia?) {
        escuderia?.let {
            supportActionBar?.title = it.nombre
            nombreTextView.text = it.nombre
            descripcionTextView.text = it.descripcion
            estadisticasTextView.text = it.estadisticas // Ajusta según tu modelo Escuderia
            loadImage(it.imagen, escuderiaImageView)
        }
    }

    private fun loadImage(imageUrl: String?, imageView: ImageView) {
        if (imageUrl.isNullOrEmpty()) {
            imageView.setImageResource(R.drawable.placeholder_image) // Tu placeholder
            return
        }

        if (imageUrl.startsWith("gs://")) {
            val storageRef = storage.getReferenceFromUrl(imageUrl)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image) // Tu imagen de error
                    .into(imageView)
            }.addOnFailureListener {
                imageView.setImageResource(R.drawable.error_image)
                Log.e(TAG, "Error al cargar imagen desde Storage: $imageUrl", it)
            }
        } else { // Asume URL HTTP/HTTPS
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imageView)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}