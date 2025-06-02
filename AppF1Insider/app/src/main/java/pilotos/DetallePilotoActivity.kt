package pilotos

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

class DetallePilotoActivity : AppCompatActivity() {

    private lateinit var nombreTextView: TextView
    private lateinit var descripcionTextView: TextView
    private lateinit var estadisticasTextView: TextView
    private lateinit var pilotoImageView: ImageView
    private lateinit var fabEditPiloto: FloatingActionButton
    private lateinit var buttonVerComentariosPiloto: Button

    private val storage = Firebase.storage
    private var pilotoRecibido: Piloto? = null
    private var isAdmin: Boolean = false
    private val TAG = "DetallePilotoActivity"

    companion object {
        const val EXTRA_PILOTO = "extra_piloto"
    }

    private val editPilotoLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == EditPilotoActivity.RESULT_PILOTO_EDITADO) { // Asegúrate que este RESULT_CODE existe en EditPilotoActivity
                val pilotoActualizado = result.data?.getParcelableExtra<Piloto>(EditPilotoActivity.EXTRA_PILOTO_ACTUALIZADO) // Y este EXTRA
                if (pilotoActualizado != null) {
                    pilotoRecibido = pilotoActualizado
                    cargarDatosEnUI(pilotoRecibido)
                    setResult(Activity.RESULT_OK) // Notificar al fragmento que hubo cambios
                }
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
        buttonVerComentariosPiloto = findViewById(R.id.buttonVerComentariosPiloto)

        isAdmin = intent.getBooleanExtra("IS_ADMIN_USER", false)
        pilotoRecibido = intent.getParcelableExtra(EXTRA_PILOTO) // Completar esta línea

        if (pilotoRecibido != null) {
            cargarDatosEnUI(pilotoRecibido)
        } else {
            Log.e(TAG, "No se recibió el objeto Piloto.")
            Toast.makeText(this, "Error al cargar detalles", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Configurar visibilidad y acción del FAB de edición
        fabEditPiloto.visibility = if (isAdmin) View.VISIBLE else View.GONE
        if (isAdmin) {
            fabEditPiloto.setOnClickListener {
                pilotoRecibido?.let { piloto ->
                    if (piloto.id.isNotEmpty()) {
                        val intent = Intent(this, EditPilotoActivity::class.java).apply {
                            putExtra(EditPilotoActivity.EXTRA_PILOTO_EDIT, piloto) // Asegúrate que este EXTRA existe en EditPilotoActivity
                        }
                        editPilotoLauncher.launch(intent)
                    } else {
                        Toast.makeText(this, "No se puede editar (ID no válido).", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        buttonVerComentariosPiloto.setOnClickListener {
            pilotoRecibido?.let { piloto ->
                if (piloto.id.isNotEmpty()) {
                    val intent = Intent(this, ComentariosPilotoActivity::class.java).apply {
                        putExtra(ComentariosPilotoActivity.EXTRA_PILOTO_ID, piloto.id)
                        putExtra(ComentariosPilotoActivity.EXTRA_NOMBRE_PILOTO, piloto.nombre)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No se pueden cargar comentarios (ID no válido).", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cargarDatosEnUI(piloto: Piloto?) {
        piloto?.let {
            supportActionBar?.title = it.nombre
            nombreTextView.text = it.nombre
            descripcionTextView.text = it.descripcion
            estadisticasTextView.text = formatEstadisticas(it.estadisticas) // Asume que tienes un campo 'estadisticas' en tu modelo Piloto
            loadImage(it.imagen)
        }
    }

    private fun formatEstadisticas(stats: String?): String {
        return stats ?: "No disponibles" // O tu lógica de formateo
    }

    private fun loadImage(imageUrl: String?) {
        if (imageUrl.isNullOrEmpty()) {
            pilotoImageView.setImageResource(R.drawable.placeholder_image) // Imagen por defecto
            return
        }

        if (imageUrl.startsWith("gs://")) {
            val storageRef = storage.getReferenceFromUrl(imageUrl)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this).load(uri)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(pilotoImageView)
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Error cargando imagen de Storage: ${exception.message}")
                pilotoImageView.setImageResource(R.drawable.error_image)
            }
        } else { // Asume URL HTTP/HTTPS
            Glide.with(this).load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(pilotoImageView)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}