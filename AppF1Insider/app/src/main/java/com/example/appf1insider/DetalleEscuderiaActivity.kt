package com.example.appf1insider // O tu paquete de activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.appf1insider.R
import com.example.appf1insider.model.Escuderia
import com.google.android.material.floatingactionbutton.FloatingActionButton // Importar FAB
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DetalleEscuderiaActivity : AppCompatActivity() {

    private lateinit var nombreTextView: TextView
    private lateinit var descripcionTextView: TextView
    private lateinit var estadisticasTextView: TextView
    private lateinit var escuderiaImageView: ImageView
    private lateinit var fabEditEscuderia: FloatingActionButton // FAB para editar

    private val storage = Firebase.storage
    private var escuderiaRecibida: Escuderia? = null // Para mantener la escudería actual que se muestra
    private val TAG = "DetalleEscuderiaAct"

    companion object {
        const val EXTRA_ESCUDERIA = "extra_escuderia"
    }

    // ActivityResultLauncher para manejar el resultado de EditEscuderiaActivity
    private val editEscuderiaLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == EditEscuderiaActivity.RESULT_ESCUDERIA_EDITADA) {
                val escuderiaActualizada = result.data?.getParcelableExtra<Escuderia>(EditEscuderiaActivity.EXTRA_ESCUDERIA_ACTUALIZADA)
                if (escuderiaActualizada != null) {
                    Log.d(TAG, "Escudería actualizada recibida: ${escuderiaActualizada.nombre}")
                    escuderiaRecibida = escuderiaActualizada // Actualizar la escudería local
                    cargarDatosEnUI(escuderiaRecibida) // Recargar la UI con los nuevos datos
                    // Notificar al fragmento anterior que los datos han cambiado
                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK, resultIntent) // Indica que algo cambió
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
        setContentView(R.layout.activity_detalle_escuderia) // Asegúrate que este es el layout correcto

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nombreTextView = findViewById(R.id.textViewDetalleNombreEscuderia)
        descripcionTextView = findViewById(R.id.textViewDetalleDescripcionEscuderia)
        estadisticasTextView = findViewById(R.id.textViewDetalleEstadisticasEscuderia)
        escuderiaImageView = findViewById(R.id.imageViewDetalleEscuderia)
        fabEditEscuderia = findViewById(R.id.fabEditEscuderia) // Inicializar el FAB

        escuderiaRecibida = intent.getParcelableExtra(EXTRA_ESCUDERIA)

        if (escuderiaRecibida != null) {
            cargarDatosEnUI(escuderiaRecibida)
        } else {
            Log.e(TAG, "No se recibió el objeto Escuderia.")
            Toast.makeText(this, "Error al cargar detalles de la escudería", Toast.LENGTH_LONG).show()
            finish()
        }

        fabEditEscuderia.setOnClickListener {
            if (escuderiaRecibida != null && escuderiaRecibida!!.id.isNotEmpty()) {
                val intent = Intent(this, EditEscuderiaActivity::class.java).apply {
                    putExtra(EditEscuderiaActivity.EXTRA_ESCUDERIA_EDIT, escuderiaRecibida)
                }
                editEscuderiaLauncher.launch(intent)
            } else {
                Toast.makeText(this, "No se puede editar la escudería (datos incompletos).", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Intento de editar escudería con datos nulos o ID vacío.")
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