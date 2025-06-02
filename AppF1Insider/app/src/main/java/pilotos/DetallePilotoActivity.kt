package pilotos // O tu paquete de activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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

class DetallePilotoActivity : AppCompatActivity() {

    private lateinit var nombreTextView: TextView
    private lateinit var descripcionTextView: TextView
    private lateinit var estadisticasTextView: TextView
    private lateinit var pilotoImageView: ImageView
    private lateinit var fabEditPiloto: FloatingActionButton
    private lateinit var buttonVerComentariosPiloto: Button // Botón para comentarios

    private val storage = Firebase.storage
    private var pilotoRecibido: Piloto? = null
    private val TAG = "DetallePilotoActivity"

    companion object {
        const val EXTRA_PILOTO = "extra_piloto"
    }

    private val editPilotoLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == EditPilotoActivity.RESULT_PILOTO_EDITADO) {
                val pilotoActualizado = result.data?.getParcelableExtra<Piloto>(EditPilotoActivity.EXTRA_PILOTO_ACTUALIZADO)
                if (pilotoActualizado != null) {
                    Log.d(TAG, "Piloto actualizado recibido: ${pilotoActualizado.nombre}")
                    pilotoRecibido = pilotoActualizado
                    cargarDatosEnUI(pilotoRecibido)
                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK, resultIntent)
                } else {
                    Log.d(TAG, "EditPilotoActivity finalizó con RESULT_PILOTO_EDITADO pero sin datos actualizados.")
                }
            } else {
                Log.d(TAG, "EditPilotoActivity finalizó con código: ${result.resultCode}")
            }
        }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_piloto)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nombreTextView = findViewById(R.id.textViewDetalleNombrePiloto)
        descripcionTextView = findViewById(R.id.textViewDetalleDescripcionPiloto)
        estadisticasTextView = findViewById(R.id.textViewDetalleEstadisticasPiloto)
        pilotoImageView = findViewById(R.id.imageViewDetallePiloto)
        fabEditPiloto = findViewById(R.id.fabEditPiloto)
        buttonVerComentariosPiloto = findViewById(R.id.buttonVerComentariosPiloto) // Inicializar el botón

        pilotoRecibido = intent.getParcelableExtra(EXTRA_PILOTO)

        if (pilotoRecibido != null) {
            cargarDatosEnUI(pilotoRecibido)
        } else {
            Log.e(TAG, "No se recibió el objeto Piloto.")
            Toast.makeText(this, "Error al cargar detalles del piloto", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        fabEditPiloto.setOnClickListener {
            if (pilotoRecibido != null && pilotoRecibido!!.id.isNotEmpty()) {
                val intent = Intent(this, EditPilotoActivity::class.java).apply {
                    putExtra(EditPilotoActivity.EXTRA_PILOTO_EDIT, pilotoRecibido)
                }
                editPilotoLauncher.launch(intent)
            } else {
                Toast.makeText(this, "No se puede editar el piloto (datos incompletos).", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Intento de editar piloto con datos nulos o ID vacío.")
            }
        }

        buttonVerComentariosPiloto.setOnClickListener {
            if (pilotoRecibido != null && pilotoRecibido!!.id.isNotEmpty()) {
                val intent = Intent(this, ComentariosPilotoActivity::class.java).apply {
                    putExtra(ComentariosPilotoActivity.EXTRA_PILOTO_ID, pilotoRecibido!!.id)
                    putExtra(ComentariosPilotoActivity.EXTRA_NOMBRE_PILOTO, pilotoRecibido!!.nombre)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "No se pueden cargar los comentarios (ID del piloto no disponible).", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarDatosEnUI(piloto: Piloto?) {
        if (piloto == null) {
            Log.e(TAG, "Intento de cargar UI con piloto nulo.")
            return
        }

        supportActionBar?.title = piloto.nombre
        nombreTextView.text = piloto.nombre
        descripcionTextView.text = piloto.descripcion
        estadisticasTextView.text = formatEstadisticas(piloto.estadisticas)

        loadImage(piloto.imagen)
    }

    private fun formatEstadisticas(stats: String): String {
        // Puedes mantener tu lógica de formateo aquí o simplemente devolver el string
        return stats // O, por ejemplo: stats.replace(";", "\n") si las estadísticas vienen separadas por ;
    }

    private fun loadImage(imageUrl: String) {
        if (imageUrl.startsWith("gs://")) {
            val storageRef = storage.getReferenceFromUrl(imageUrl)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image) // Asegúrate que estos drawables existen
                    .error(R.drawable.error_image)             // Asegúrate que estos drawables existen
                    .into(pilotoImageView)
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Error al cargar imagen desde Storage: ${exception.message}")
                pilotoImageView.setImageResource(R.drawable.error_image)
            }
        } else if (imageUrl.isNotEmpty()) { // Asume que es una URL HTTP/HTTPS
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(pilotoImageView)
        } else {
            Log.d(TAG, "URL de imagen vacía o no es de Storage.")
            pilotoImageView.setImageResource(R.drawable.placeholder_image) // Imagen por defecto
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