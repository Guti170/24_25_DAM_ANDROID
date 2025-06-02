package escuderias

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.appf1insider.R

class EditEscuderiaActivity : AppCompatActivity() {

    private lateinit var editTextNombreEscuderia: EditText
    private lateinit var editTextImagenUrlEscuderia: EditText
    private lateinit var editTextEstadisticasEscuderia: EditText
    private lateinit var editTextDescripcionEscuderia: EditText
    private lateinit var buttonGuardarCambiosEscuderia: Button
    private lateinit var progressBarEditEscuderia: ProgressBar

    private lateinit var db: FirebaseFirestore
    private var escuderiaRecibida: Escuderia? = null
    private var escuderiaId: String? = null
    private val TAG = "EditEscuderiaActivity"

    companion object {
        const val EXTRA_ESCUDERIA_EDIT = "extra_escuderia_para_editar"
        const val RESULT_ESCUDERIA_EDITADA = 2003 // Código de resultado personalizado
        const val EXTRA_ESCUDERIA_ACTUALIZADA = "extra_escuderia_actualizada"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_escuderia) // Crearemos este layout a continuación

        supportActionBar?.title = "Editar Escudería"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = Firebase.firestore

        // Asumiré que los IDs son los mismos que en activity_add_escuderia.xml
        // o una copia con los mismos IDs.
        editTextNombreEscuderia = findViewById(R.id.editTextNombreEscuderia)
        editTextImagenUrlEscuderia = findViewById(R.id.editTextImagenUrlEscuderia)
        editTextEstadisticasEscuderia = findViewById(R.id.editTextEstadisticasEscuderia)
        editTextDescripcionEscuderia = findViewById(R.id.editTextDescripcionEscuderia)
        buttonGuardarCambiosEscuderia = findViewById(R.id.buttonGuardarEscuderia) // Reutilizando el botón
        progressBarEditEscuderia = findViewById(R.id.progressBarAddEscuderia) // Reutilizando la ProgressBar

        buttonGuardarCambiosEscuderia.text = "Guardar Cambios"

        escuderiaRecibida = intent.getParcelableExtra(EXTRA_ESCUDERIA_EDIT)

        if (escuderiaRecibida == null || escuderiaRecibida?.id == null || escuderiaRecibida!!.id.isEmpty()) {
            Toast.makeText(this, "Error: No se pudo cargar la escudería para editar.", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Escudería nula o ID de escudería no válido recibido.")
            finish()
            return
        }

        escuderiaId = escuderiaRecibida!!.id
        cargarDatosDeLaEscuderia()

        buttonGuardarCambiosEscuderia.setOnClickListener {
            if (validarCampos()) {
                actualizarEscuderiaEnFirestore()
            }
        }
    }

    private fun cargarDatosDeLaEscuderia() {
        escuderiaRecibida?.let {
            editTextNombreEscuderia.setText(it.nombre)
            editTextImagenUrlEscuderia.setText(it.imagen)
            editTextEstadisticasEscuderia.setText(it.estadisticas)
            editTextDescripcionEscuderia.setText(it.descripcion)
        }
    }

    private fun validarCampos(): Boolean {
        if (editTextNombreEscuderia.text.toString().trim().isEmpty()) {
            editTextNombreEscuderia.error = "El nombre es obligatorio"
            return false
        }
        if (editTextEstadisticasEscuderia.text.toString().trim().isEmpty()) {
            editTextEstadisticasEscuderia.error = "Las estadísticas son obligatorias"
            return false
        }
        // Puedes añadir más validaciones
        return true
    }

    private fun actualizarEscuderiaEnFirestore() {
        progressBarEditEscuderia.visibility = View.VISIBLE
        buttonGuardarCambiosEscuderia.isEnabled = false

        val nombreActualizado = editTextNombreEscuderia.text.toString().trim()
        val imagenUrlActualizada = editTextImagenUrlEscuderia.text.toString().trim()
        val estadisticasActualizadas = editTextEstadisticasEscuderia.text.toString().trim()
        val descripcionActualizada = editTextDescripcionEscuderia.text.toString().trim()

        val escuderiaActualizadaMap = mapOf(
            "nombre" to nombreActualizado,
            "imagen" to imagenUrlActualizada,
            "estadisticas" to estadisticasActualizadas,
            "descripcion" to descripcionActualizada
            // No actualizamos 'id' ya que es la clave del documento
        )

        if (escuderiaId == null) {
            Log.e(TAG, "El ID de la escudería es nulo, no se puede actualizar.")
            Toast.makeText(this, "Error al guardar: ID de la escudería no encontrado.", Toast.LENGTH_LONG).show()
            progressBarEditEscuderia.visibility = View.GONE
            buttonGuardarCambiosEscuderia.isEnabled = true
            return
        }

        db.collection("escuderia").document(escuderiaId!!) // Nombre de tu colección de escuderías
            .update(escuderiaActualizadaMap)
            .addOnSuccessListener {
                Log.d(TAG, "Escudería actualizada correctamente en Firestore con ID: $escuderiaId")
                Toast.makeText(this, "Escudería actualizada", Toast.LENGTH_SHORT).show()
                progressBarEditEscuderia.visibility = View.GONE

                val escuderiaParaDevolver = Escuderia(
                    id = escuderiaId!!,
                    nombre = nombreActualizado,
                    imagen = imagenUrlActualizada,
                    estadisticas = estadisticasActualizadas,
                    descripcion = descripcionActualizada
                )

                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_ESCUDERIA_ACTUALIZADA, escuderiaParaDevolver)
                setResult(RESULT_ESCUDERIA_EDITADA, resultIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al actualizar la escudería en Firestore", e)
                Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
                progressBarEditEscuderia.visibility = View.GONE
                buttonGuardarCambiosEscuderia.isEnabled = true
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